package com.king.tools.toolset.OCR.component;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 阿里云OCR识别组件（HTTP调用方式）
 * 
 * @author hyrule-admin
 * @date 2025-10-01
 */
@Component
public class AliyunOCR {
    
    private static final Logger logger = LoggerFactory.getLogger(AliyunOCR.class);
    
    private static final String OCR_ENDPOINT = "https://ocr-api.cn-shanghai.aliyuncs.com";
    private static final String API_VERSION = "2021-07-07";
    
    private OkHttpClient httpClient;
    private Gson gson = new Gson();
    
    @Value("${ocr.aliyun.accessKeyId:}")
    private String accessKeyId;
    
    @Value("${ocr.aliyun.accessKeySecret:}")
    private String accessKeySecret;
    
    @Value("${ocr.aliyun.enabled:false}")
    private boolean enabled;
    
    @PostConstruct
    public void init() {
        if (!enabled) {
            logger.info("阿里云OCR未启用，跳过初始化");
            return;
        }
        
        try {
            logger.info("开始初始化阿里云OCR...");
            
            // 验证配置
            if (accessKeyId == null || accessKeyId.isEmpty() || "your-access-key-id".equals(accessKeyId)) {
                logger.warn("阿里云OCR AccessKeyId未配置或使用默认值");
                enabled = false;
                return;
            }
            
            if (accessKeySecret == null || accessKeySecret.isEmpty() || "your-access-key-secret".equals(accessKeySecret)) {
                logger.warn("阿里云OCR AccessKeySecret未配置或使用默认值");
                enabled = false;
                return;
            }
            
            // 创建HTTP客户端
            httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build();
            
            logger.info("阿里云OCR初始化成功");
            
        } catch (Exception e) {
            logger.error("阿里云OCR初始化失败: {}", e.getMessage(), e);
            enabled = false;
        }
    }
    
    /**
     * 检查阿里云OCR是否可用
     * @return true-可用，false-不可用
     */
    public boolean isAvailable() {
        return enabled && httpClient != null;
    }
    
    /**
     * 识别图片中的文本（通用文字识别）
     * @param image 待识别的图片
     * @return 识别出的文本
     * @throws Exception 识别失败时抛出异常
     */
    public String recognize(BufferedImage image) throws Exception {
        if (!isAvailable()) {
            throw new Exception("阿里云OCR未启用或初始化失败");
        }
        
        if (image == null) {
            throw new Exception("图片对象为null，无法进行OCR识别");
        }
        
        try {
            logger.debug("开始阿里云OCR识别，图片尺寸: {}x{}", image.getWidth(), image.getHeight());
            
            // 将BufferedImage转换为Base64
            String imageBase64 = convertImageToBase64(image);
            
            // 构建请求体
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("body", imageBase64);
            
            // 调用API
            String responseData = callOCRAPI(requestBody.toString());
            
            // 解析结果
            String recognizedText = parseResponse(responseData);
            
            logger.debug("阿里云OCR识别完成，结果长度: {} 字符", 
                        recognizedText != null ? recognizedText.length() : 0);
            
            return recognizedText;
            
        } catch (Exception e) {
            logger.error("阿里云OCR识别失败: {}", e.getMessage(), e);
            throw new Exception("阿里云OCR识别失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 识别图片URL中的文本
     * @param imageUrl 图片URL地址
     * @return 识别出的文本
     * @throws Exception 识别失败时抛出异常
     */
    public String recognizeByUrl(String imageUrl) throws Exception {
        if (!isAvailable()) {
            throw new Exception("阿里云OCR未启用或初始化失败");
        }
        
        if (imageUrl == null || imageUrl.isEmpty()) {
            throw new Exception("图片URL不能为空");
        }
        
        try {
            logger.debug("开始阿里云OCR识别（URL模式），图片地址: {}", imageUrl);
            
            // 构建请求体
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("url", imageUrl);
            
            // 调用API
            String responseData = callOCRAPI(requestBody.toString());
            
            // 解析结果
            String recognizedText = parseResponse(responseData);
            
            logger.debug("阿里云OCR识别完成，结果长度: {} 字符", 
                        recognizedText != null ? recognizedText.length() : 0);
            
            return recognizedText;
            
        } catch (Exception e) {
            logger.error("阿里云OCR识别失败: {}", e.getMessage(), e);
            throw new Exception("阿里云OCR识别失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 调用阿里云OCR API
     * @param requestBody 请求体JSON
     * @return 响应数据
     * @throws Exception 调用失败时抛出异常
     */
    private String callOCRAPI(String requestBody) throws Exception {
        try {
            // 构建请求URL和参数
            String action = "RecognizeGeneral";
            String timestamp = getTimestamp();
            String nonce = UUID.randomUUID().toString();
            
            // 构建签名参数
            Map<String, String> params = new TreeMap<>();
            params.put("AccessKeyId", accessKeyId);
            params.put("Action", action);
            params.put("Format", "JSON");
            params.put("SignatureMethod", "HMAC-SHA1");
            params.put("SignatureNonce", nonce);
            params.put("SignatureVersion", "1.0");
            params.put("Timestamp", timestamp);
            params.put("Version", API_VERSION);
            
            // 计算签名
            String signature = calculateSignature(params, "POST");
            params.put("Signature", signature);
            
            // 构建完整URL
            StringBuilder urlBuilder = new StringBuilder(OCR_ENDPOINT);
            urlBuilder.append("/?");
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (!first) {
                    urlBuilder.append("&");
                }
                urlBuilder.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                          .append("=")
                          .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                first = false;
            }
            
            // 发送POST请求
            RequestBody body = RequestBody.create(
                requestBody, 
                MediaType.parse("application/json; charset=utf-8")
            );
            
            Request request = new Request.Builder()
                .url(urlBuilder.toString())
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();
            
            Response response = httpClient.newCall(request).execute();
            
            if (!response.isSuccessful()) {
                throw new Exception("API调用失败，HTTP状态码: " + response.code());
            }
            
            String responseBody = response.body().string();
            logger.debug("阿里云OCR API响应: {}", responseBody);
            
            return responseBody;
            
        } catch (Exception e) {
            throw new Exception("调用阿里云OCR API失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 计算签名
     * @param params 参数
     * @param method HTTP方法
     * @return 签名字符串
     * @throws Exception 计算失败时抛出异常
     */
    private String calculateSignature(Map<String, String> params, String method) throws Exception {
        try {
            // 构建待签名字符串
            StringBuilder stringToSign = new StringBuilder();
            stringToSign.append(method).append("&")
                       .append(URLEncoder.encode("/", "UTF-8")).append("&");
            
            StringBuilder queryString = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (!first) {
                    queryString.append("&");
                }
                queryString.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                          .append("=")
                          .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                first = false;
            }
            
            stringToSign.append(URLEncoder.encode(queryString.toString(), "UTF-8"));
            
            // HMAC-SHA1签名
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec keySpec = new SecretKeySpec(
                (accessKeySecret + "&").getBytes("UTF-8"), 
                "HmacSHA1"
            );
            mac.init(keySpec);
            byte[] signData = mac.doFinal(stringToSign.toString().getBytes("UTF-8"));
            
            return Base64.getEncoder().encodeToString(signData);
            
        } catch (Exception e) {
            throw new Exception("计算签名失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取时间戳
     * @return ISO8601格式的时间戳
     */
    private String getTimestamp() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(new Date());
    }
    
    /**
     * 将BufferedImage转换为Base64字符串
     * @param image 图片对象
     * @return Base64字符串
     * @throws Exception 转换失败时抛出异常
     */
    private String convertImageToBase64(BufferedImage image) throws Exception {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // 将图片写入字节流
            ImageIO.write(image, "jpg", baos);
            
            // 转换为Base64
            byte[] imageBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
            
        } catch (Exception e) {
            throw new Exception("图片转Base64失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 解析阿里云OCR响应结果
     * @param responseData 响应数据JSON字符串
     * @return 识别出的文本
     */
    private String parseResponse(String responseData) {
        if (responseData == null || responseData.isEmpty()) {
            return "";
        }
        
        try {
            JsonObject jsonObject = gson.fromJson(responseData, JsonObject.class);
            
            // 检查是否有错误
            if (jsonObject.has("Code")) {
                String errorCode = jsonObject.get("Code").getAsString();
                String errorMsg = jsonObject.has("Message") ? 
                    jsonObject.get("Message").getAsString() : "未知错误";
                logger.error("阿里云OCR返回错误: {} - {}", errorCode, errorMsg);
                return "";
            }
            
            // 解析识别结果
            if (jsonObject.has("Data")) {
                JsonObject data = jsonObject.getAsJsonObject("Data");
                
                if (data.has("content")) {
                    return data.get("content").getAsString();
                }
                
                // 尝试解析prism_wordsInfo格式
                if (data.has("prism_wordsInfo")) {
                    JsonArray wordsInfo = data.getAsJsonArray("prism_wordsInfo");
                    StringBuilder text = new StringBuilder();
                    
                    for (int i = 0; i < wordsInfo.size(); i++) {
                        JsonObject word = wordsInfo.get(i).getAsJsonObject();
                        if (word.has("word")) {
                            text.append(word.get("word").getAsString());
                            if (i < wordsInfo.size() - 1) {
                                text.append(" ");
                            }
                        }
                    }
                    
                    return text.toString();
                }
            }
            
            return "";
            
        } catch (Exception e) {
            logger.error("解析阿里云OCR响应失败: {}", e.getMessage(), e);
            return "";
        }
    }
}
