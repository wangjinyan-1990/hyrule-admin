package com.king.configuration.deploy.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.configuration.deploy.entity.TfDeployRecord;
import com.king.configuration.deploy.mapper.DeployRecordMapper;
import com.king.configuration.deploy.service.IDeployRecordService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * 发版登记Service实现类
 */
@Service("deployRecordServiceImpl")
public class DeployRecordServiceImpl extends ServiceImpl<DeployRecordMapper, TfDeployRecord> implements IDeployRecordService {

    @Override
    public Map<String, Object> getDeployRecordList(Integer pageNo, Integer pageSize) {
        // 设置默认分页参数
        if (pageNo == null || pageNo < 1) {
            pageNo = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        
        // 使用自定义查询方法，包含系统名称
        Page<TfDeployRecord> page = new Page<>(pageNo, pageSize);
        IPage<TfDeployRecord> result = this.baseMapper.selectDeployRecordListWithSystemName(page);
        
        // 构建返回结果
        Map<String, Object> data = new HashMap<>();
        data.put("total", result.getTotal());
        data.put("rows", result.getRecords());
        
        return data;
    }

    @Override
    public TfDeployRecord getDeployRecordDetail(Integer deployId) {
        Assert.notNull(deployId, "部署ID不能为空");
        
        // 使用自定义查询方法，包含系统名称
        TfDeployRecord record = this.baseMapper.selectDeployRecordDetailById(deployId);
        Assert.notNull(record, "发版登记信息不存在");
        
        return record;
    }

    @Override
    public void updateDeployRecord(TfDeployRecord deployRecord) {
        Assert.notNull(deployRecord, "发版登记信息不能为空");
        Assert.notNull(deployRecord.getDeployId(), "部署ID不能为空");
        
        // 检查记录是否存在
        TfDeployRecord existingRecord = this.getById(deployRecord.getDeployId());
        Assert.notNull(existingRecord, "发版登记信息不存在");
        
        // 更新记录信息
        this.updateById(deployRecord);
    }

    @Override
    public void deleteDeployRecord(Integer deployId) {
        Assert.notNull(deployId, "部署ID不能为空");
        
        // 检查记录是否存在
        TfDeployRecord existingRecord = this.getById(deployId);
        Assert.notNull(existingRecord, "发版登记信息不存在");
        
        // 删除记录信息
        this.removeById(deployId);
    }
}

