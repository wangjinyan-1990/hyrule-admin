package com.king.tools.toolset.OCR.controller;

import com.king.tools.toolset.OCR.entity.OCRResponse;
import com.king.tools.toolset.OCR.entity.OCRResult;
import com.king.tools.toolset.OCR.service.IOCRService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * OCR文字识别控制器
 * 
 * @author hyrule-admin
 * @date 2025-10-01
 */
@RestController
@RequestMapping("/tools/ocr")
public class OCRController {
    
    private static final Logger logger = LoggerFactory.getLogger(OCRController.class);
    
    @Autowired
    @Qualifier("ocrServiceImpl")
    private IOCRService ocrService;
    
    /**
     * 识别图片中的文字
     * @param file 上传的图片文件
     * @param userId 用户ID（可选）
     * @param engine OCR引擎（aliyun/tesseract，可选，默认使用配置的引擎）
     * @param request HTTP请求
     * @return OCR识别结果
     */
    @PostMapping("/recognize")
    public ResponseEntity<OCRResponse> recognizeText(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "engine", required = false) String engine,
            HttpServletRequest request) {
        
        try {
            // 验证文件
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new OCRResponse(400, "文件不能为空", null));
            }
            
            // 验证文件类型
            if (!isValidImageFile(file)) {
                return ResponseEntity.badRequest()
                    .body(new OCRResponse(400, "不支持的文件格式，仅支持图片文件", null));
            }
            
            // 验证文件大小（限制为10MB）
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                    .body(new OCRResponse(400, "文件大小不能超过10MB", null));
            }
            
            logger.info("开始OCR识别，文件名: {}, 文件大小: {} bytes, 用户ID: {}, 引擎: {}", 
                       file.getOriginalFilename(), file.getSize(), userId, engine);
            
            // 调用OCR服务
            String recognizedText;
            if (engine != null && !engine.isEmpty()) {
                // 使用指定的引擎
                recognizedText = ocrService.recognizeText(file, engine);
            } else {
                // 使用默认引擎
                recognizedText = ocrService.recognizeText(file);
            }
            
            // 构建返回结果
            OCRResult result = new OCRResult();
            result.setText(recognizedText);
            result.setConfidence(calculateConfidence(recognizedText));
            result.setWordCount(recognizedText.length());
            result.setLineCount(countLines(recognizedText));
            
            logger.info("OCR识别成功，识别文本长度: {}, 行数: {}, 置信度: {}", 
                       result.getWordCount(), result.getLineCount(), result.getConfidence());
            
            return ResponseEntity.ok(new OCRResponse(200, "识别成功", result));
            
        } catch (Exception e) {
            logger.error("OCR识别失败: " + e.getMessage(), e);
            return ResponseEntity.status(500)
                .body(new OCRResponse(500, "识别失败: " + e.getMessage(), null));
        }
    }
    
    /**
     * 验证是否为有效的图片文件
     * @param file 上传的文件
     * @return true-有效，false-无效
     */
    private boolean isValidImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }
        
        // 支持的图片格式
        return contentType.equals("image/jpeg") ||
               contentType.equals("image/jpg") ||
               contentType.equals("image/png") ||
               contentType.equals("image/bmp") ||
               contentType.equals("image/gif") ||
               contentType.equals("image/tiff");
    }
    
    /**
     * 计算识别置信度
     * @param text 识别出的文本
     * @return 置信度（0-100）
     */
    private double calculateConfidence(String text) {
        if (text == null || text.length() == 0) {
            return 0.0;
        }
        
        int totalChars = text.length();
        int validChars = 0;
        
        // 统计有效字符（字母、数字、中文、空白字符、标点符号）
        for (char c : text.toCharArray()) {
            if (Character.isLetterOrDigit(c) || 
                Character.isWhitespace(c) ||
                isChinese(c) ||
                isPunctuation(c)) {
                validChars++;
            }
        }
        
        // 计算置信度
        double confidence = (double) validChars / totalChars * 100;
        
        // 保留两位小数
        return Math.round(confidence * 100.0) / 100.0;
    }
    
    /**
     * 判断是否为中文字符
     * @param c 字符
     * @return true-是中文，false-不是中文
     */
    private boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS ||
               ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS ||
               ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A ||
               ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B ||
               ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION ||
               ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS ||
               ub == Character.UnicodeBlock.GENERAL_PUNCTUATION;
    }
    
    /**
     * 判断是否为标点符号
     * @param c 字符
     * @return true-是标点符号，false-不是标点符号
     */
    private boolean isPunctuation(char c) {
        // 中文标点：，。！？；：、""''（）【】《》…—·
        // 英文标点：.,!?;:'()[]<>-
        String punctuation = "，。！？；：、\u201C\u201D\u2018\u2019（）【】《》…—·.,!?;:'()[]<>-";
        return punctuation.indexOf(c) >= 0;
    }
    
    /**
     * 统计文本行数
     * @param text 文本
     * @return 行数
     */
    private int countLines(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        
        String[] lines = text.split("\n");
        int count = 0;
        for (String line : lines) {
            if (line.trim().length() > 0) {
                count++;
            }
        }
        return count;
    }
}
