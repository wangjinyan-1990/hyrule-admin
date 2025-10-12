package com.king.framework.param.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.framework.param.entity.TSysParam;
import com.king.framework.param.mapper.SysParamMapper;
import com.king.framework.param.service.ISysParamService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 系统参数Service实现类
 */
@Service("sysParamServiceImpl")
public class SysParamServiceImpl extends ServiceImpl<SysParamMapper, TSysParam> implements ISysParamService {
    
    @Override
    public String getParamValueById(String paramId) {
        if (!StringUtils.hasText(paramId)) {
            return null;
        }
        
        LambdaQueryWrapper<TSysParam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TSysParam::getParamId, paramId);
        
        TSysParam sysParam = this.baseMapper.selectOne(wrapper);
        
        return sysParam != null ? sysParam.getParamValue() : null;
    }
}
