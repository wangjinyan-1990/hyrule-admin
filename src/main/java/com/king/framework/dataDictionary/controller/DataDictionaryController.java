package com.king.framework.dataDictionary.controller;

import com.king.common.Result;
import com.king.framework.dataDictionary.entity.DataDictionary;
import com.king.framework.dataDictionary.service.IDataDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据字典Controller
 */
@RestController
@RequestMapping("/framework/dictionary")
public class DataDictionaryController {
    
    @Autowired
    @Qualifier("dataDictionaryServiceImpl")
    private IDataDictionaryService dataDictionaryService;
    
    /**
     * 根据数据类型查询字典列表
     * @param dataType 数据类型
     * @return 字典列表
     */
    @GetMapping("/type/{dataType}")
    public Result<List<DataDictionary>> getByDataType(@PathVariable("dataType") String dataType) {
        try {
            List<DataDictionary> list = dataDictionaryService.getByDataType(dataType);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("查询数据字典失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据数据类型查询字典列表（GET参数方式）
     * @param dataType 数据类型
     * @return 字典列表
     */
    @GetMapping("/list")
    public Result<List<DataDictionary>> getByDataTypeParam(@RequestParam("dataType") String dataType) {
        try {
            List<DataDictionary> list = dataDictionaryService.getByDataType(dataType);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("查询数据字典失败: " + e.getMessage());
        }
    }
}
