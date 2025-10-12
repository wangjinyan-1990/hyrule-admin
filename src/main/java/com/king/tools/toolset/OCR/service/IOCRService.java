package com.king.tools.toolset.OCR.service;

import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;

/**
 * OCR识别服务接口
 * 
 * @author hyrule-admin
 * @date 2025-10-01
 */
public interface IOCRService {
    
    /**
     * 识别图片中的文本（使用默认引擎）
     * @param file 上传的图片文件
     * @return 识别出的文本内容
     * @throws Exception 识别失败时抛出异常
     */
    String recognizeText(MultipartFile file) throws Exception;
    
    /**
     * 识别图片中的文本（指定引擎）
     * @param file 上传的图片文件
     * @param engine OCR引擎类型（aliyun/tesseract）
     * @return 识别出的文本内容
     * @throws Exception 识别失败时抛出异常
     */
    String recognizeText(MultipartFile file, String engine) throws Exception;
    
    /**
     * 图片预处理 - 灰度化
     * @param image 原始图片
     * @return 灰度化后的图片
     */
    BufferedImage preprocessImage(BufferedImage image);
    
    /**
     * 图片二值化处理
     * @param image 灰度图
     * @param threshold 阈值（0-255）
     * @return 二值化后的图片
     */
    BufferedImage binarizeImage(BufferedImage image, int threshold);
}

