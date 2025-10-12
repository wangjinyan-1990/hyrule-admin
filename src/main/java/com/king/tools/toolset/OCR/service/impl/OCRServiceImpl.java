package com.king.tools.toolset.OCR.service.impl;

import com.king.tools.toolset.OCR.component.AliyunOCR;
import com.king.tools.toolset.OCR.component.TesseractOCR;
import com.king.tools.toolset.OCR.service.IOCRService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * OCR识别服务实现类
 * 
 * @author hyrule-admin
 * @date 2025-10-01
 */
@Service("ocrServiceImpl")
public class OCRServiceImpl implements IOCRService {
    
    private static final Logger logger = LoggerFactory.getLogger(OCRServiceImpl.class);
    
    /**
     * 默认二值化阈值
     */
    private static final int DEFAULT_THRESHOLD = 128;
    
    @Autowired
    private TesseractOCR tesseractOCR;
    
    @Autowired
    private AliyunOCR aliyunOCR;
    
    @Value("${ocr.default.engine:aliyun}")
    private String defaultEngine;
    
    /**
     * 识别图片中的文本（使用默认引擎）
     * 
     * @param file 上传的图片文件
     * @return 识别出的文本内容
     * @throws Exception 识别失败时抛出异常
     */
    @Override
    public String recognizeText(MultipartFile file) throws Exception {
        return recognizeText(file, defaultEngine);
    }
    
    /**
     * 识别图片中的文本（指定引擎）
     * 
     * @param file 上传的图片文件
     * @param engine OCR引擎类型（aliyun/tesseract）
     * @return 识别出的文本内容
     * @throws Exception 识别失败时抛出异常
     */
    @Override
    public String recognizeText(MultipartFile file, String engine) throws Exception {
        logger.info("开始OCR文本识别，文件名: {}, 文件大小: {} bytes, 引擎: {}", 
                   file.getOriginalFilename(), file.getSize(), engine);
        
        try {
            // 验证文件
            validateFile(file);
            
            String recognizedText;
            
            // 根据引擎类型选择识别方式
            if ("aliyun".equalsIgnoreCase(engine)) {
                // 使用阿里云OCR
                if (!aliyunOCR.isAvailable()) {
                    logger.warn("阿里云OCR不可用，切换到Tesseract");
                    engine = "tesseract";
                } else {
                    // 将MultipartFile转换为BufferedImage
                    BufferedImage image = convertToBufferedImage(file);
                    
                    // 调用阿里云OCR识别
                    recognizedText = aliyunOCR.recognize(image);
                    
                    logger.info("阿里云OCR识别成功，识别文本长度: {} 字符", 
                               recognizedText != null ? recognizedText.length() : 0);
                    
                    return recognizedText;
                }
            }
            
            // 使用Tesseract OCR
            if ("tesseract".equalsIgnoreCase(engine)) {
                // 将MultipartFile转换为BufferedImage
                BufferedImage image = convertToBufferedImage(file);
                
                // 图片预处理（灰度化）
                BufferedImage processedImage = preprocessImage(image);
                
                // 调用Tesseract进行OCR识别
                recognizedText = tesseractOCR.recognize(processedImage);
                
                logger.info("Tesseract OCR识别成功，识别文本长度: {} 字符", 
                           recognizedText != null ? recognizedText.length() : 0);
                
                return recognizedText;
            }
            
            // 未知引擎类型
            throw new Exception("不支持的OCR引擎类型: " + engine + "，支持的类型: aliyun, tesseract");
            
        } catch (IOException e) {
            logger.error("读取图片文件失败: {}", e.getMessage(), e);
            throw new Exception("无法读取图片文件: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("OCR文本识别失败: {}", e.getMessage(), e);
            throw new Exception("OCR识别失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 图片预处理 - 灰度化
     * 
     * @param image 原始图片
     * @return 灰度化后的图片
     */
    @Override
    public BufferedImage preprocessImage(BufferedImage image) {
        if (image == null) {
            logger.warn("预处理的图片为null");
            return null;
        }
        
        int width = image.getWidth();
        int height = image.getHeight();
        
        // 如果已经是灰度图，直接返回
        if (image.getType() == BufferedImage.TYPE_BYTE_GRAY) {
            logger.debug("图片已经是灰度图，无需转换");
            return image;
        }
        
        logger.debug("开始图片灰度化处理，尺寸: {}x{}", width, height);
        
        // 转换为灰度图
        BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d = grayImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        
        logger.debug("图片灰度化处理完成");
        
        return grayImage;
    }
    
    /**
     * 图片二值化处理
     * 用于进一步提高识别准确度
     * 
     * @param image 灰度图
     * @param threshold 阈值（0-255）
     * @return 二值化后的图片
     */
    @Override
    public BufferedImage binarizeImage(BufferedImage image, int threshold) {
        if (image == null) {
            logger.warn("二值化的图片为null");
            return null;
        }
        
        // 验证阈值范围
        if (threshold < 0 || threshold > 255) {
            logger.warn("二值化阈值超出范围[0-255]，使用默认值: {}", DEFAULT_THRESHOLD);
            threshold = DEFAULT_THRESHOLD;
        }
        
        int width = image.getWidth();
        int height = image.getHeight();
        
        logger.debug("开始图片二值化处理，尺寸: {}x{}, 阈值: {}", width, height, threshold);
        
        BufferedImage binaryImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y);
                int gray = (pixel >> 16) & 0xff;
                
                // 大于阈值的设为白色，否则设为黑色
                if (gray > threshold) {
                    binaryImage.setRGB(x, y, Color.WHITE.getRGB());
                } else {
                    binaryImage.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }
        
        logger.debug("图片二值化处理完成");
        
        return binaryImage;
    }
    
    /**
     * 验证上传的文件
     * 
     * @param file 上传的文件
     * @throws Exception 验证失败时抛出异常
     */
    private void validateFile(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new Exception("上传的文件为空");
        }
        
        String originalFilename = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            throw new Exception("文件名不能为空");
        }
        
        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new Exception("不支持的文件类型，仅支持图片文件");
        }
        
        // 验证文件大小（10MB）
        long maxSize = 10 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new Exception("文件大小超过限制，最大支持10MB");
        }
        
        logger.debug("文件验证通过: {}, 类型: {}, 大小: {} bytes", 
                    originalFilename, contentType, file.getSize());
    }
    
    /**
     * 将MultipartFile转换为BufferedImage
     * 
     * @param file 上传的文件
     * @return BufferedImage对象
     * @throws IOException 转换失败时抛出异常
     */
    private BufferedImage convertToBufferedImage(MultipartFile file) throws IOException {
        BufferedImage image = null;
        java.io.InputStream inputStream = null;
        
        try {
            inputStream = file.getInputStream();
            image = ImageIO.read(inputStream);
            
            if (image == null) {
                throw new IOException("无法将文件转换为图片对象，请确认文件格式正确");
            }
            
            logger.debug("成功转换为BufferedImage，尺寸: {}x{}, 类型: {}", 
                        image.getWidth(), image.getHeight(), image.getType());
            
            return image;
            
        } finally {
            // 确保输入流被关闭
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.warn("关闭输入流失败: {}", e.getMessage());
                }
            }
        }
    }
}

