package com.king.tools.toolset.OCR.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OCR识别响应实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OCRResponse {
    /**
     * 响应码
     */
    private int code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * OCR识别结果
     */
    private OCRResult data;
}

