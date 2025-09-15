package com.king.framework.dataDictionary.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.king.framework.dataDictionary.entity.DataDictionary;

import java.util.List;

/**
 * 数据字典Service接口
 */
public interface IDataDictionaryService extends IService<DataDictionary> {
    
    /**
     * 根据数据类型查询字典列表
     * @param dataType 数据类型
     * @return 字典列表
     */
    List<DataDictionary> getByDataType(String dataType);
}
