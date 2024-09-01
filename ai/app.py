from flask import Flask, request, jsonify
from flask_cors import CORS
from PIL import Image
import torch
import torch.nn.functional as F
from transformers import AutoFeatureExtractor, SwinForImageClassification
import rawpy
import io

app = Flask(__name__)
CORS(app)

# 加载预训练模型和特征提取器
feature_extractor = AutoFeatureExtractor.from_pretrained("microsoft/swin-large-patch4-window12-384-in22k")
model = SwinForImageClassification.from_pretrained("microsoft/swin-large-patch4-window12-384-in22k")
model = model.eval()

def load_image(file):
    try:
        # 尝试使用PIL直接打开图像（适用于JPEG, PNG等标准格式）
        img = Image.open(file).convert('RGB')
    except (IOError, OSError):
        # 如果无法直接打开，尝试作为RAW文件处理
        file.seek(0)  # 重置文件指针
        try:
            raw = rawpy.imread(file)
            img_array = raw.postprocess()
            img = Image.fromarray(img_array)
        except Exception as e:
            raise ValueError(f"Unsupported image format or failed to process RAW file: {str(e)}")
    return img


@app.route('/predict', methods=['POST'])
def classify_image():
    try:
        if 'image' not in request.files:
            return jsonify({"error": "No file part"}), 400

        file = request.files['image']
        if file.filename == '':
            return jsonify({"error": "No selected file"}), 400

        img = load_image(file)

        # 图片预处理并进行预测
        inputs = feature_extractor(images=img, return_tensors="pt")
        with torch.no_grad():  # 关闭梯度计算
            outputs = model(**inputs)

        logits = outputs.logits
        prediction_scores = F.softmax(logits, dim=1)

        # 获取所有置信度大于 0.1 的预测结果
        filtered_predictions = []
        seen_labels = set()

        for idx, score in enumerate(prediction_scores[0]):
            if score > 0.1:
                label = model.config.id2label[idx]
                labels_split = label.split(',')

                for sub_label in labels_split:
                    clean_label = sub_label.strip()
                    if clean_label not in seen_labels:
                        filtered_predictions.append({"label": clean_label, "score": float(score)})
                        seen_labels.add(clean_label)

        # 按置信度从高到低排序
        filtered_predictions.sort(key=lambda x: x['score'], reverse=True)

        return jsonify(filtered_predictions)

    except Exception as e:
        return jsonify({"error": str(e)}), 500


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
