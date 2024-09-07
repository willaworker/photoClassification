# 图像分类与EXIF元数据提取API

本项目是一个基于Flask的API应用，主要功能包括使用Hugging Face的Swin Transformer模型对图像进行分类，并提取图像的EXIF元数据（包括地理位置信息）。该应用支持标准图像格式（如JPEG、PNG）以及RAW图像格式（如DNG）。

## 功能

- **图像分类**：使用预训练的Swin Transformer模型对上传的图像进行分类。
- EXIF元数据提取
  - 提取拍摄设备型号。
  - 提取GPS数据（纬度、经度）。
  - 将GPS坐标转换为可读的地址信息。
- **支持多种图像格式**：包括标准格式（JPEG、PNG）和RAW格式（DNG）。

## 目录

- [安装指南](#安装指南)
- [使用说明](#使用说明)
- API 说明
  - [分类与元数据提取](#分类与元数据提取)
  - [获取图像](#获取图像)
- [依赖项](#依赖项)
- [运行项目](#运行项目)
- [示例](#示例)
- [许可证](#许可证)

## 安装指南

### 前提条件

- Python 3.7 或更高版本
- pip 包管理工具

### 安装依赖

```
pip install -r requirements.txt
```

**`requirements.txt` 内容**：

```
Flask
flask-cors
Pillow
torch
transformers
rawpy== 0.21.0
numpy==1.26.4
geopy
exifread
```

## 使用说明

### 启动服务器

在项目根目录下运行以下命令启动Flask服务器：

```
python app.py
```

服务器将运行在 `http://0.0.0.0:5000/`。

## API 说明

### 分类与元数据提取

- **URL**: `/predict`
- **方法**: `POST`
- **描述**: 上传图像，获取分类结果及EXIF元数据。

#### 请求参数

- `image`：要上传的图像文件（支持JPEG、PNG、DNG等格式）。

#### 返回结果

- `predictions`：分类结果列表，每个类别包含 `label` 和 `score`。
- `metadata`：图像的EXIF元数据，包括设备型号、纬度、经度和位置信息。

#### 示例

```
curl -X POST -F image=@path_to_your_image.jpg http://localhost:5000/predict
```

**响应示例**：

```
json复制代码{
  "predictions": [
    {"label": "cat", "score": 0.95},
    {"label": "animal", "score": 0.90}
  ],
  "metadata": {
    "device": "M2012K10C",
    "latitude": 37.7749,
    "longitude": -122.4194,
    "location": "San Francisco, California, USA"
  }
}
```

### 获取图像

- **URL**: `/image`
- **方法**: `POST`
- **描述**: 上传图像并返回处理后的图像。

#### 请求参数

- `image`：要上传的图像文件（支持JPEG、PNG、DNG等格式）。

#### 返回结果

- 处理后的图像文件（JPEG格式）。

#### 示例

```
curl -X POST -F image=@path_to_your_image.jpg http://localhost:5000/image --output processed_image.jpg
```

## 依赖项

- **Flask**：用于构建API服务。
- **flask-cors**：处理跨域资源共享（CORS）。
- **Pillow**：图像处理库。
- **torch**：PyTorch深度学习框架。
- **transformers**：Hugging Face的Transformer库，用于加载预训练模型。
- **rawpy**：处理RAW图像文件。
- **geopy**：地理编码库，用于将GPS坐标转换为地址。
- **exifread**：提取图像EXIF元数据。

## 运行项目

1. **确保所有依赖已安装**：

   ```
   pip install -r requirements.txt
   ```

2. **启动Flask服务器**：

   ```
   python app.py
   ```

3. **访问API**：

   服务器启动后，可以通过 `http://localhost:5000/` 访问API端点。

## 示例

### 使用Python进行API调用

```
python复制代码import requests

url = 'http://localhost:5000/predict'
image_path = 'path_to_your_image.jpg'

with open(image_path, 'rb') as img_file:
    files = {'image': img_file}
    response = requests.post(url, files=files)

if response.status_code == 200:
    data = response.json()
    print("分类结果：", data['predictions'])
    print("元数据：", data['metadata'])
else:
    print("请求失败：", response.json())
```

### 使用Postman进行测试

1. 打开Postman应用。
2. 创建一个新的 `POST` 请求，URL为 `http://localhost:5000/predict`。
3. 在 `Body` 选项中选择 `form-data`，添加一个键为 `image` 的文件字段，并上传图像文件。
4. 发送请求，查看响应结果。

## 许可证

本项目采用 MIT 许可证。

------

## 备注

- **GPS数据缺失**：如果上传的图像不包含GPS数据，`metadata` 中的 `latitude`、`longitude` 和 `location` 字段将不会出现。
- **模型性能**：Swin Transformer模型较大，首次加载可能需要一些时间。建议在生产环境中使用更高效的模型或优化加载流程。
