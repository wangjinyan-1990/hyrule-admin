package com.king.framework.param.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.king.framework.param.entity.TSysParam;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统参数Mapper接口
 */
@Mapper
public interface SysParamMapper extends BaseMapper<TSysParam> {
    
}
