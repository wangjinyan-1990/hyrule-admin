# 阿里云OCR配置说明

## 📋 功能说明

本项目已集成阿里云OCR服务，支持以下两种识别方式：
- **阿里云OCR**（推荐）：云端识别，准确度高，无需本地配置
- **Tesseract OCR**：本地识别，免费，需要配置语言数据文件

## 🔑 步骤1：获取阿里云AccessKey

### 1.1 登录阿里云控制台

访问: https://ram.console.aliyun.com/manage/ak

### 1.2 创建AccessKey

1. 点击"创建AccessKey"按钮
2. 完成身份验证
3. 保存生成的 `AccessKey ID` 和 `AccessKey Secret`

⚠️ **注意**: AccessKey Secret 只显示一次，请妥善保管！

### 1.3 开通OCR服务

1. 访问: https://www.aliyun.com/product/ai/ocr
2. 点击"立即开通"
3. 选择合适的套餐（有免费额度）

## ⚙️ 步骤2：配置项目

### 2.1 修改application.yml

打开 `src/main/resources/application.yml`，配置阿里云OCR：

```yaml
ocr:
  # 默认OCR引擎：aliyun（阿里云） / tesseract（本地）
  default:
    engine: aliyun
  
  # 阿里云OCR配置（云端识别）
  aliyun:
    # 是否启用阿里云OCR
    enabled: true
    # 阿里云AccessKey ID（必填）
    accessKeyId: LTAI5txxxxxxxxxxxxxx
    # 阿里云AccessKey Secret（必填）
    accessKeySecret: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    # 服务端点（可选，默认上海节点）
    endpoint: ocr.cn-shanghai.aliyuncs.com
```

### 2.2 配置项说明

| 配置项 | 必填 | 说明 | 默认值 |
|--------|------|------|--------|
| `ocr.default.engine` | 否 | 默认OCR引擎 | `aliyun` |
| `ocr.aliyun.enabled` | 否 | 是否启用阿里云OCR | `true` |
| `ocr.aliyun.accessKeyId` | 是 | 阿里云AccessKey ID | 无 |
| `ocr.aliyun.accessKeySecret` | 是 | 阿里云AccessKey Secret | 无 |
| `ocr.aliyun.endpoint` | 否 | 服务端点 | `ocr.cn-shanghai.aliyuncs.com` |

### 2.3 技术说明

本项目使用HTTP方式直接调用阿里云OCR API，无需安装阿里云SDK，更加轻量和稳定。

**优势**:
- ✅ 无依赖冲突
- ✅ 更轻量
- ✅ 更稳定
- ✅ 易于调试

## 🚀 步骤3：启动项目

```bash
mvn spring-boot:run
```

查看启动日志，确认阿里云OCR初始化成功：

```
开始初始化阿里云OCR...
阿里云OCR初始化成功，Endpoint: ocr.cn-shanghai.aliyuncs.com
```

## 📡 API使用说明

### 接口地址

```
POST /tools/ocr/recognize
```

### 请求参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `file` | File | 是 | 图片文件 |
| `userId` | String | 否 | 用户ID |
| `engine` | String | 否 | OCR引擎（aliyun/tesseract） |

### 使用默认引擎（阿里云）

```bash
curl -X POST http://localhost:8888/tools/ocr/recognize \
  -F "file=@test.png" \
  -F "userId=admin"
```

### 指定使用阿里云OCR

```bash
curl -X POST http://localhost:8888/tools/ocr/recognize \
  -F "file=@test.png" \
  -F "engine=aliyun"
```

### 指定使用Tesseract OCR

```bash
curl -X POST http://localhost:8888/tools/ocr/recognize \
  -F "file=@test.png" \
  -F "engine=tesseract"
```

### 响应示例

**成功响应**:
```json
{
  "code": 200,
  "message": "识别成功",
  "data": {
    "text": "识别出的文本内容",
    "confidence": 95.5,
    "wordCount": 150,
    "lineCount": 5
  }
}
```

**失败响应**:
```json
{
  "code": 500,
  "message": "识别失败: 阿里云OCR未启用或初始化失败",
  "data": null
}
```

## 💡 前端调用示例

### JavaScript/Vue

```javascript
const formData = new FormData();
formData.append('file', imageFile);
formData.append('engine', 'aliyun');  // 使用阿里云OCR

fetch('/tools/ocr/recognize', {
  method: 'POST',
  body: formData
})
.then(response => response.json())
.then(data => {
  console.log('识别结果:', data.data.text);
});
```

### React

```javascript
const handleOCR = async (file) => {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('engine', 'aliyun');
  
  const response = await fetch('/tools/ocr/recognize', {
    method: 'POST',
    body: formData
  });
  
  const result = await response.json();
  if (result.code === 200) {
    console.log('识别成功:', result.data.text);
  }
};
```

## 🎯 OCR引擎对比

| 特性 | 阿里云OCR | Tesseract OCR |
|------|-----------|---------------|
| **准确度** | ⭐⭐⭐⭐⭐ 非常高 | ⭐⭐⭐ 中等 |
| **识别速度** | ⭐⭐⭐⭐ 快速 | ⭐⭐⭐ 一般 |
| **部署难度** | ⭐⭐⭐⭐⭐ 简单 | ⭐⭐ 复杂 |
| **费用** | 按量收费（有免费额度） | 完全免费 |
| **网络要求** | 需要联网 | 无需联网 |
| **语言支持** | 多语言 | 需下载语言包 |
| **场景支持** | 通用、身份证、银行卡等 | 仅通用文字 |

### 推荐使用场景

#### 使用阿里云OCR（推荐）
- ✅ 生产环境
- ✅ 对准确度要求高
- ✅ 需要快速部署
- ✅ 有网络连接
- ✅ 识别量较大

#### 使用Tesseract OCR
- ✅ 开发测试环境
- ✅ 无网络环境
- ✅ 零成本要求
- ✅ 识别量小
- ✅ 对准确度要求不高

## 💰 阿里云OCR费用

### 免费额度

新用户可获得：
- 通用文字识别：1000次/月（永久）
- 高精度文字识别：500次/月（3个月）

### 按量付费

| 类型 | 价格 | 说明 |
|------|------|------|
| 通用文字识别 | ¥0.01/次 | 识别图片中的文字 |
| 高精度文字识别 | ¥0.05/次 | 更高的识别准确率 |

详见：https://help.aliyun.com/document_detail/178460.html

## ⚠️ 常见问题

### Q1: 提示"阿里云OCR未启用"？

**检查事项**:
1. `application.yml` 中 `enabled` 是否为 `true`
2. AccessKey ID和Secret是否正确配置
3. 是否使用了默认值（your-access-key-id）
4. 查看启动日志确认初始化状态

### Q2: 提示"AccessKey不存在"？

**可能原因**:
- AccessKey ID配置错误
- AccessKey已被删除
- 账号权限不足

**解决方法**:
1. 登录阿里云控制台验证AccessKey
2. 重新创建AccessKey
3. 确认账号有OCR服务权限

### Q3: 识别结果不准确？

**优化建议**:
1. 使用高分辨率图片（推荐300 DPI以上）
2. 确保图片清晰、文字与背景对比度高
3. 避免图片倾斜或变形
4. 考虑使用高精度识别模式

### Q4: 如何切换OCR引擎？

**方法1**: 修改配置文件（全局切换）
```yaml
ocr:
  default:
    engine: tesseract  # 切换到Tesseract
```

**方法2**: 接口参数指定（单次切换）
```bash
curl -X POST /tools/ocr/recognize \
  -F "file=@test.png" \
  -F "engine=tesseract"
```

### Q5: 阿里云OCR调用失败自动降级？

是的！如果阿里云OCR不可用（未配置、网络异常等），系统会自动切换到Tesseract OCR。

```
阿里云OCR不可用，切换到Tesseract
```

## 🔒 安全建议

### 1. 保护AccessKey

- ❌ 不要将AccessKey提交到代码仓库
- ✅ 使用环境变量或配置中心管理
- ✅ 定期更换AccessKey
- ✅ 使用RAM子账号，限制权限

### 2. 使用环境变量

```bash
# 设置环境变量
export ALIYUN_ACCESS_KEY_ID=LTAI5txxxxxxxxxxxxxx
export ALIYUN_ACCESS_KEY_SECRET=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

```yaml
# application.yml 使用环境变量
ocr:
  aliyun:
    accessKeyId: ${ALIYUN_ACCESS_KEY_ID}
    accessKeySecret: ${ALIYUN_ACCESS_KEY_SECRET}
```

## 📊 监控与日志

### 启动日志

```
开始初始化阿里云OCR...
阿里云OCR初始化成功，Endpoint: ocr.cn-shanghai.aliyuncs.com
```

### 识别日志

```
开始OCR文本识别，文件名: test.png, 文件大小: 12345 bytes, 引擎: aliyun
开始阿里云OCR识别，图片尺寸: 800x600
阿里云OCR识别完成，结果长度: 150 字符
阿里云OCR识别成功，识别文本长度: 150, 行数: 5, 置信度: 95.5
```

### 错误日志

```
阿里云OCR识别失败: The specified access key does not exist
```

## 📚 参考资料

- 阿里云OCR产品首页: https://www.aliyun.com/product/ai/ocr
- API文档: https://help.aliyun.com/document_detail/178460.html
- SDK文档: https://help.aliyun.com/document_detail/311427.html
- 价格说明: https://help.aliyun.com/document_detail/178460.html

---

**配置完成后，重启项目即可使用阿里云OCR功能！** 🎉

