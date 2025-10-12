package com.king.framework.param.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.king.framework.param.entity.TSysParam;

/**
 * 系统参数Service接口
 */
public interface ISysParamService extends IService<TSysParam> {
    
    /**
     * 根据参数ID获取参数值
     * @param paramId 参数ID
     * @return 参数值，如果未找到则返回null
     */
    String getParamValueById(String paramId);
}
