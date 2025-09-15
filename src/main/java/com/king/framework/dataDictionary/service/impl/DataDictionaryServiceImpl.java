package com.king.framework.dataDictionary.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.framework.dataDictionary.entity.DataDictionary;
import com.king.framework.dataDictionary.mapper.DataDictionaryMapper;
import com.king.framework.dataDictionary.service.IDataDictionaryService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 数据字典Service实现类
 */
@Service("dataDictionaryServiceImpl")
public class DataDictionaryServiceImpl extends ServiceImpl<DataDictionaryMapper, DataDictionary> implements IDataDictionaryService {
    
    @Override
    public List<DataDictionary> getByDataType(String dataType) {
        if (!StringUtils.hasText(dataType)) {
            return Collections.emptyList(); // 返回空列表
        }
        return baseMapper.selectByDataType(dataType);
    }
}
