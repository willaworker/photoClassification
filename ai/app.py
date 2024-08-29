from flask import Flask, request, jsonify
from flask_cors import CORS
from PIL import Image
import io
import torch
import torch.nn.functional as F
from torchvision import models, transforms


app = Flask(__name__)
CORS(app)

# 加载预训练模型
model = models.resnet152(pretrained=True)
model = model.eval()

# 加载中文标签
def load_labels_from_txt(file_path):
    zh_labels = {}
    with open(file_path, 'r', encoding='utf-8') as f:
        for line in f:
            parts = line.strip().split()
            idx = int(parts[0])  # 类别索引
            zh_label = parts[-1]  # 中文标签
            zh_labels[idx] = zh_label
    return zh_labels

zh_labels = load_labels_from_txt('label.txt')

# 定义图片预处理
transform = transforms.Compose([
    transforms.Resize(256),
    transforms.CenterCrop(224),
    transforms.ToTensor(),
    transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225])
])


@app.route('/predict', methods=['POST'])
def classify_image():
    if 'image' not in request.files:
        return jsonify({"error": "No file part"}), 400

    file = request.files['image']
    if file.filename == '':
        return jsonify({"error": "No selected file"}), 400

    try:
        img = Image.open(file.stream).convert('RGB')
    except Exception as e:
        return jsonify({"error": str(e)}), 500

    # 图片预处理
    transforms_img = transform(img)
    inputimg = transforms_img.unsqueeze(0)  # 增加批次维度

    # 模型预测
    output = model(inputimg)
    output = F.softmax(output, dim=1)

    # 提取前3名预测结果
    prediction_score, pred_label_idx = torch.topk(output, 3)
    prediction_score = prediction_score.detach().numpy()[0]
    pred_label_idx = pred_label_idx.detach().numpy()[0]
    predicted_label_zh = [zh_labels[i] for i in pred_label_idx]

    # # 过滤出概率大于0.2的结果
    # filtered_predictions = [
    #     {"label": predicted_label_zh[i], "score": float(prediction_score[i])}
    #     for i in range(len(prediction_score)) if prediction_score[i] > 0.2
    # ]

    # 过滤出概率大于0.2的结果并去重
    filtered_predictions = []
    seen_labels = set()

    for i in range(len(prediction_score)):
        label = predicted_label_zh[i]
        score = float(prediction_score[i])
        if score > 0.3 and label not in seen_labels:
            filtered_predictions.append({"label": label, "score": score})
            seen_labels.add(label)

    return jsonify(filtered_predictions)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
