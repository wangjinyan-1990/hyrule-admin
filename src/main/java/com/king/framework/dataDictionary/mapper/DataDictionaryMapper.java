package com.king.framework.dataDictionary.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.king.framework.dataDictionary.entity.DataDictionary;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 数据字典Mapper接口
 */
@Mapper
public interface DataDictionaryMapper extends BaseMapper<DataDictionary> {
    
    /**
     * 根据数据类型查询字典列表
     * @param dataType 数据类型
     * @return 字典列表
     */
    List<DataDictionary> selectByDataType(String dataType);
}
