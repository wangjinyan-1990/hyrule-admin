package com.king.configuration.deploy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.king.configuration.deploy.entity.TfDeployRecord;

import java.util.Map;

/**
 * 发版登记Service接口
 */
public interface IDeployRecordService extends IService<TfDeployRecord> {
    
    /**
     * 分页查询发版登记列表
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @param sendTestInfo 送测单信息（可选，模糊查询）
     * @param systemName 系统名称（可选，模糊查询）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 分页结果
     */
    Map<String, Object> getDeployRecordList(Integer pageNo, Integer pageSize, String sendTestInfo, String systemName, String startDate, String endDate);
    
    /**
     * 根据部署ID获取发版登记详情
     * @param deployId 部署ID
     * @return 发版登记信息
     */
    TfDeployRecord getDeployRecordDetail(Integer deployId);
    
    /**
     * 更新发版登记
     * @param deployRecord 发版登记信息
     */
    void updateDeployRecord(TfDeployRecord deployRecord);
    
    /**
     * 删除发版登记
     * @param deployId 部署ID
     */
    void deleteDeployRecord(Integer deployId);
}

