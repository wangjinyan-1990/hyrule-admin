package com.king.configuration.merge.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.configuration.merge.entity.TfDeployRecord;
import com.king.configuration.merge.mapper.MergeRecordMapper;
import com.king.configuration.merge.service.IMergeRecordService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * 合并登记Service实现类
 */
@Service("mergeRecordServiceImpl")
public class MergeRecordServiceImpl extends ServiceImpl<MergeRecordMapper, TfDeployRecord> implements IMergeRecordService {

    @Override
    public Map<String, Object> getDeployRecordList(Integer pageNo, Integer pageSize, String sendTestInfo, String systemName, String testStage, String startDate, String endDate) {
        // 设置默认分页参数
        if (pageNo == null || pageNo < 1) {
            pageNo = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        // 处理空字符串，视为 null
        if (sendTestInfo != null && sendTestInfo.trim().isEmpty()) {
            sendTestInfo = null;
        }
        if (systemName != null && systemName.trim().isEmpty()) {
            systemName = null;
        }
        if (testStage != null && testStage.trim().isEmpty()) {
            testStage = null;
        }
        if (startDate != null && startDate.trim().isEmpty()) {
            startDate = null;
        }
        if (endDate != null && endDate.trim().isEmpty()) {
            endDate = null;
        }

        // 使用自定义查询方法，包含系统名称
        Page<TfDeployRecord> page = new Page<>(pageNo, pageSize);
        IPage<TfDeployRecord> result = this.baseMapper.selectDeployRecordListWithSystemName(page, sendTestInfo, systemName, testStage, startDate, endDate);

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
        Assert.notNull(deployRecord, "合并登记信息不能为空");
        Assert.notNull(deployRecord.getDeployId(), "部署ID不能为空");

        // 检查记录是否存在
        TfDeployRecord existingRecord = this.getById(deployRecord.getDeployId());
        Assert.notNull(existingRecord, "合并登记信息不存在");

        // 更新记录信息
        this.updateById(deployRecord);
    }

    @Override
    public void deleteDeployRecord(Integer deployId) {
        Assert.notNull(deployId, "部署ID不能为空");

        // 检查记录是否存在
        TfDeployRecord existingRecord = this.getById(deployId);
        Assert.notNull(existingRecord, "合并登记信息不存在");

        // 删除记录信息
        this.removeById(deployId);
    }
}

