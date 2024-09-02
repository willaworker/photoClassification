from flask import Flask, request, jsonify, send_file
from flask_cors import CORS
from PIL import Image
import torch
import torch.nn.functional as F
from transformers import AutoFeatureExtractor, SwinForImageClassification
import rawpy
import io
import exifread
from geopy.geocoders import Nominatim

app = Flask(__name__)
CORS(app)

# 加载预训练模型和特征提取器
feature_extractor = AutoFeatureExtractor.from_pretrained("microsoft/swin-large-patch4-window12-384-in22k")
model = SwinForImageClassification.from_pretrained("microsoft/swin-large-patch4-window12-384-in22k")
model = model.eval()

geolocator = Nominatim(user_agent="photo1364561561")

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

def get_exif_data(file):
    file.seek(0)
    tags = exifread.process_file(file, details=False)
    return tags

def convert_gps_to_address(lat, lon):
    try:
        location = geolocator.reverse((lat, lon), exactly_one=True)
        return location.address if location else None
    except Exception as e:
        return str(e)

def convert_to_degrees(value):
    d = float(value.values[0].num) / float(value.values[0].den)
    m = float(value.values[1].num) / float(value.values[1].den)
    s = float(value.values[2].num) / float(value.values[2].den)
    return d + (m / 60.0) + (s / 3600.0)

@app.route('/predict', methods=['POST'])
def classify_image():
    try:
        if 'image' not in request.files:
            return jsonify({"error": "No file part"}), 400

        file = request.files['image']
        if file.filename == '':
            return jsonify({"error": "No selected file"}), 400

        img = load_image(file)

        # 提取图片的 EXIF 信息
        exif_data = get_exif_data(file)
        metadata = {}
        if 'Image Model' in exif_data:
            metadata['device'] = str(exif_data['Image Model'])
        if 'GPS GPSLatitude' in exif_data and 'GPS GPSLongitude' in exif_data:
            lat = convert_to_degrees(exif_data['GPS GPSLatitude'])
            lon = convert_to_degrees(exif_data['GPS GPSLongitude'])

            metadata['latitude'] = lat
            metadata['longitude'] = lon
            metadata['location'] = convert_gps_to_address(lat, lon)

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

        # 返回分类结果和图片的 EXIF 信息
        response = {
            "predictions": filtered_predictions,
            "metadata": metadata
        }

        return jsonify(response)

    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/image', methods=['POST'])
def get_image():
    try:
        if 'image' not in request.files:
            return jsonify({"error": "No file part"}), 400

        file = request.files['image']
        if file.filename == '':
            return jsonify({"error": "No selected file"}), 400

        img = load_image(file)

        img_bytes = io.BytesIO()
        img.save(img_bytes, format='JPEG')
        img_bytes.seek(0)
        return send_file(img_bytes, mimetype='image/jpeg')

    except Exception as e:
        return jsonify({"error": str(e)}), 500


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
