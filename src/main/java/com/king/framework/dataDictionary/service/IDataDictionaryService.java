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
    
    /**
     * 根据数据类型和数据名称返回数据值
     * @param dataType 数据类型
     * @param dataName 数据名称
     * @return 数据值，如果未找到则返回null
     */
    String getDataValueByTypeAndName(String dataType, String dataName);
    
    /**
     * 根据数据类型和数据值返回数据名称
     * @param dataType 数据类型
     * @param dataValue 数据值
     * @return 数据名称，如果未找到则返回null
     */
    String getDataNameByTypeAndValue(String dataType, String dataValue);
}
