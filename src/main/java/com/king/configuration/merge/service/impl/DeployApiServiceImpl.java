package com.king.configuration.merge.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.configuration.merge.dto.DeployRecordApiDTO;
import com.king.configuration.merge.entity.TfDeployRecord;
import com.king.configuration.merge.mapper.MergeRecordMapper;
import com.king.configuration.merge.service.IDeployApiService;
import com.king.configuration.merge.service.SaveMergeRecordService;
import com.king.test.baseManage.testSystem.entity.TTestSystem;
import com.king.test.baseManage.testSystem.service.ITestSystemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 外部api部署登记Service实现类
 */
@Service("deployApiServiceImpl")
public class DeployApiServiceImpl extends ServiceImpl<MergeRecordMapper, TfDeployRecord> implements IDeployApiService {

    private static final Logger logger = LoggerFactory.getLogger(DeployApiServiceImpl.class);

    @Autowired
    @Qualifier("testSystemServiceImpl")
    private ITestSystemService testSystemService;

    @Resource
    private SaveMergeRecordService saveMergeRecordService;

    @Override
    public String createDeployRecordByApi(DeployRecordApiDTO dto) {
        Assert.notNull(dto, "发版登记信息不能为空");

        // 校验必填字段
        Assert.isTrue(StringUtils.hasText(dto.getSysAbbreviation()), "系统简称不能为空");

        // 根据系统简称查询测试系统信息
        LambdaQueryWrapper<TTestSystem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TTestSystem::getSysAbbreviation, dto.getSysAbbreviation());
        TTestSystem testSystem = testSystemService.getOne(wrapper);
        Assert.notNull(testSystem, "未找到系统简称为 " + dto.getSysAbbreviation() + " 的测试系统信息");

        String systemId = testSystem.getSystemId();
        String testStage = dto.getTestStage();
        Assert.isTrue(StringUtils.hasText(dto.getTestStage()), "测试阶段不能为空");

        // 转换 testStage 为大写：sit -> SIT, pat -> PAT
        if (testStage != null) {
            testStage = testStage.toUpperCase();
        }

        // 调用 SaveDeployRecordService 保存发版登记信息
        String versionCode = saveMergeRecordService.saveDeployRecord(
                systemId,
                testStage,
                dto.getSendTestInfo(),
                dto.getRecordNum(),
                dto.getIsRunSql(),
                dto.getIsUpdateConfig(),
                dto.getCodeList() != null ? dto.getCodeList() : "",
                dto.getComponentInfo()
        );

        logger.info("合并登记创建成功（外部API）: versionCode={}, componentInfo={}, testStage={}, sendTestInfo={}",
                versionCode, dto.getComponentInfo(), testStage, dto.getSendTestInfo());

        // 返回生成的版本号
        return versionCode;
    }

}
