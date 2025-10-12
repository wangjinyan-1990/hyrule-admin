package com.king.tools.toolset.OCR.component;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Tesseract OCR识别组件
 * 
 * @author hyrule-admin
 * @date 2025-10-01
 */
@Component
public class TesseractOCR {
    
    private static final Logger logger = LoggerFactory.getLogger(TesseractOCR.class);
    
    private Tesseract tesseract;
    
    @Value("${ocr.tesseract.datapath:tessdata}")
    private String datapath;
    
    @Value("${ocr.tesseract.language:chi_sim+eng}")
    private String language;
    
    @PostConstruct
    public void init() {
        try {
            logger.info("开始初始化Tesseract OCR...");
            
            tesseract = new Tesseract();
            
            // 验证并设置数据文件路径
            validateAndSetDatapath();
            
            // 设置识别语言（中文简体+英文）
            tesseract.setLanguage(language);
            logger.info("设置识别语言: {}", language);
            
            // 设置页面分割模式
            // PSM_AUTO = 3 (默认，自动页面分割)
            tesseract.setPageSegMode(3);
            
            // 设置OCR引擎模式
            // OEM_DEFAULT = 3 (默认模式，更稳定)
            // 改用默认模式以避免内存访问错误
            tesseract.setOcrEngineMode(3);
            
            logger.info("Tesseract OCR初始化成功");
            
        } catch (Exception e) {
            logger.error("Tesseract OCR初始化失败: {}", e.getMessage(), e);
            throw new RuntimeException("Tesseract OCR初始化失败，请检查配置和语言数据文件", e);
        }
    }
    
    /**
     * 验证并设置数据文件路径
     */
    private void validateAndSetDatapath() {
        File datapathFile = new File(datapath);
        
        logger.info("检查Tesseract数据路径: {}", datapathFile.getAbsolutePath());
        
        // 检查数据路径是否存在
        if (!datapathFile.exists()) {
            logger.warn("数据路径不存在: {}", datapathFile.getAbsolutePath());
            logger.warn("请确保已安装Tesseract并配置正确的数据路径");
        } else {
            logger.info("数据路径存在: {}", datapathFile.getAbsolutePath());
            
            // 检查语言数据文件是否存在
            String[] languages = language.split("\\+");
            for (String lang : languages) {
                File langFile = new File(datapathFile, lang + ".traineddata");
                if (langFile.exists()) {
                    logger.info("语言数据文件存在: {}", langFile.getName());
                } else {
                    logger.warn("语言数据文件不存在: {}", langFile.getAbsolutePath());
                    logger.warn("请下载对应的语言数据文件: {}.traineddata", lang);
                }
            }
        }
        
        // 设置数据路径
        tesseract.setDatapath(datapath);
    }
    
    /**
     * 识别图片中的文本
     * @param image 待识别的图片
     * @return 识别出的文本
     * @throws Exception 识别失败时抛出异常
     */
    public String recognize(BufferedImage image) throws Exception {
        if (image == null) {
            throw new Exception("图片对象为null，无法进行OCR识别");
        }
        
        try {
            logger.debug("开始OCR识别，图片尺寸: {}x{}", image.getWidth(), image.getHeight());
            
            String result = tesseract.doOCR(image);
            
            logger.debug("OCR识别完成，结果长度: {} 字符", result != null ? result.length() : 0);
            
            return result;
            
        } catch (TesseractException e) {
            logger.error("Tesseract识别失败: {}", e.getMessage());
            
            // 提供更友好的错误提示
            if (e.getMessage().contains("Error opening data file")) {
                throw new Exception("无法打开Tesseract语言数据文件，请检查：\n" +
                        "1. tessdata文件夹是否存在于: " + datapath + "\n" +
                        "2. 语言数据文件是否存在: " + language + ".traineddata\n" +
                        "3. 参考TESSERACT_SETUP.md进行配置", e);
            } else if (e.getMessage().contains("Invalid memory access")) {
                throw new Exception("Tesseract内存访问错误，可能原因：\n" +
                        "1. 语言数据文件损坏或不完整\n" +
                        "2. Tesseract未正确安装\n" +
                        "3. 数据文件路径配置错误\n" +
                        "当前配置路径: " + datapath, e);
            } else {
                throw new Exception("OCR识别失败: " + e.getMessage(), e);
            }
        } catch (Exception e) {
            logger.error("OCR识别过程出现异常: {}", e.getMessage(), e);
            throw new Exception("OCR识别失败: " + e.getMessage(), e);
        }
    }
}

