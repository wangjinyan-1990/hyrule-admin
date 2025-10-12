package com.king.tools.toolset.OCR.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OCR识别结果实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OCRResult {
    /**
     * 识别出的文本内容
     */
    private String text;
    
    /**
     * 识别置信度（0-100）
     */
    private double confidence;
    
    /**
     * 字符数
     */
    private int wordCount;
    
    /**
     * 行数
     */
    private int lineCount;
}

