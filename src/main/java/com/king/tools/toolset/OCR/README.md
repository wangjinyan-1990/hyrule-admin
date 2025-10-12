# OCR文字识别模块

## 功能说明

本模块提供基于Tesseract OCR的文字识别功能，支持从图片中识别中文和英文文字。

## 技术栈

- **Tesseract OCR**: 开源OCR识别引擎
- **Tess4J**: Tesseract的Java包装库
- **Spring Boot**: Web框架

## 安装Tesseract OCR

### Windows系统

1. **下载Tesseract安装包**
   - 访问: https://github.com/UB-Mannheim/tesseract/wiki
   - 下载最新版本的安装包（推荐 tesseract-ocr-w64-setup-5.3.x.exe）

2. **安装Tesseract**
   - 双击安装包进行安装
   - 默认安装路径: `C:\Program Files\Tesseract-OCR`
   - **重要**: 安装时勾选"Additional language data"选项，选择中文语言包（chi_sim）

3. **配置环境变量**（可选）
   - 添加系统环境变量: `TESSDATA_PREFIX = C:\Program Files\Tesseract-OCR\tessdata`
   - 将 `C:\Program Files\Tesseract-OCR` 添加到 PATH 环境变量

4. **复制语言数据文件到项目**
   - 在项目根目录创建 `tessdata` 文件夹
   - 从 `C:\Program Files\Tesseract-OCR\tessdata` 复制以下文件到项目的 `tessdata` 文件夹:
     - `chi_sim.traineddata` (简体中文)
     - `eng.traineddata` (英文)

### Linux系统

1. **Ubuntu/Debian**
   ```bash
   sudo apt-get update
   sudo apt-get install tesseract-ocr
   sudo apt-get install tesseract-ocr-chi-sim  # 简体中文
   sudo apt-get install tesseract-ocr-eng      # 英文
   ```

2. **CentOS/RHEL**
   ```bash
   sudo yum install epel-release
   sudo yum install tesseract
   sudo yum install tesseract-langpack-chi_sim  # 简体中文
   sudo yum install tesseract-langpack-eng      # 英文
   ```

3. **语言数据文件位置**
   - 通常在: `/usr/share/tesseract-ocr/4.00/tessdata` 或 `/usr/share/tessdata`
   - 需要在 `application.yml` 中配置正确的路径

### macOS系统

```bash
brew install tesseract
brew install tesseract-lang  # 安装所有语言包
```

## 配置说明

### application.yml配置

```yaml
ocr:
  tesseract:
    # Tesseract数据文件路径
    datapath: tessdata  # Windows项目根目录
    # datapath: /usr/share/tesseract-ocr/4.00/tessdata  # Linux
    
    # 识别语言
    language: chi_sim+eng  # 简体中文+英文
```

### 支持的语言

- `chi_sim`: 简体中文
- `chi_tra`: 繁体中文
- `eng`: 英文
- `jpn`: 日语
- `kor`: 韩语
- 多语言组合用 `+` 连接，如: `chi_sim+eng`

## API接口

### 识别图片中的文字

**接口地址**: `POST /tools/ocr/recognize`

**请求参数**:
- `file`: 图片文件（必填），支持格式：jpg、png、bmp、gif、tiff
- `userId`: 用户ID（可选）

**请求示例** (使用FormData):
```javascript
const formData = new FormData();
formData.append('file', imageFile);
formData.append('userId', 'admin');

fetch('/tools/ocr/recognize', {
  method: 'POST',
  body: formData
})
.then(response => response.json())
.then(data => {
  console.log('识别结果:', data);
});
```

**响应示例**:
```json
{
  "code": 200,
  "message": "识别成功",
  "data": {
    "text": "识别出的文本内容\n可能包含多行",
    "confidence": 95.5,
    "wordCount": 150,
    "lineCount": 5
  }
}
```

**响应字段说明**:
- `code`: 响应状态码（200-成功，400-请求错误，500-服务器错误）
- `message`: 响应消息
- `data.text`: 识别出的文本内容
- `data.confidence`: 识别置信度（0-100）
- `data.wordCount`: 字符总数
- `data.lineCount`: 行数

**错误响应示例**:
```json
{
  "code": 400,
  "message": "文件不能为空",
  "data": null
}
```

## 使用限制

- 文件大小限制: 10MB
- 支持的图片格式: JPEG、PNG、BMP、GIF、TIFF
- 建议图片清晰度: 300 DPI以上
- 建议图片格式: PNG（无损压缩）

## 优化建议

### 提高识别准确率

1. **图片质量**
   - 使用高分辨率图片（300 DPI以上）
   - 确保文字清晰、无模糊
   - 避免图片倾斜或变形

2. **图片预处理**
   - 灰度化: 将彩色图片转为灰度图
   - 二值化: 增强文字与背景的对比度
   - 去噪: 去除图片中的噪点
   - 倾斜矫正: 矫正图片倾斜角度

3. **文字特征**
   - 字体清晰、规范
   - 文字大小适中（建议12pt以上）
   - 文字与背景对比度高

### 性能优化

1. **图片大小控制**
   - 压缩大图片（保持清晰度）
   - 裁剪无用区域
   - 建议图片尺寸不超过4000x4000

2. **异步处理**
   - 对大量图片识别任务使用异步处理
   - 使用消息队列进行任务调度

3. **缓存策略**
   - 对相同图片的识别结果进行缓存
   - 避免重复识别

## 常见问题

### 1. 找不到tessdata路径

**错误信息**: `Error opening data file tessdata/chi_sim.traineddata`

**解决方法**:
- 确认 `tessdata` 文件夹存在且包含语言数据文件
- 检查 `application.yml` 中的路径配置是否正确
- Windows: 使用相对路径 `tessdata` 或绝对路径 `C:/tessdata`
- Linux: 使用绝对路径 `/usr/share/tesseract-ocr/4.00/tessdata`

### 2. 中文识别不准确

**可能原因**:
- 未安装中文语言包
- 图片质量差、文字模糊
- 字体过于花哨或特殊

**解决方法**:
- 确认安装了 `chi_sim.traineddata`
- 提高图片清晰度
- 使用标准字体的图片

### 3. 识别速度慢

**优化方法**:
- 压缩图片大小
- 裁剪不需要识别的区域
- 使用更高性能的服务器
- 考虑使用GPU加速（需要编译支持GPU的Tesseract）

### 4. 内存占用高

**解决方法**:
- 限制并发识别任务数量
- 调整JVM堆内存大小
- 及时释放图片资源

## 测试用例

### 使用Postman测试

1. 选择 `POST` 方法
2. URL: `http://localhost:8888/tools/ocr/recognize`
3. Body选择 `form-data`
4. 添加参数:
   - Key: `file`, Type: `File`, Value: 选择一张图片
   - Key: `userId`, Type: `Text`, Value: `admin`
5. 点击 `Send` 发送请求

### 使用curl测试

```bash
curl -X POST http://localhost:8888/tools/ocr/recognize \
  -F "file=@/path/to/image.png" \
  -F "userId=admin"
```

## 目录结构

```
OCR/
├── component/
│   └── TesseractOCR.java          # Tesseract OCR组件
├── controller/
│   └── OCRController.java         # OCR控制器
├── entity/
│   ├── OCRResponse.java           # OCR响应实体
│   └── OCRResult.java             # OCR结果实体
├── service/
│   └── OCRService.java            # OCR服务类
└── README.md                      # 本文档
```

## 参考资料

- Tesseract OCR官方文档: https://tesseract-ocr.github.io/
- Tess4J GitHub: https://github.com/nguyenq/tess4j
- Tesseract语言数据下载: https://github.com/tesseract-ocr/tessdata

