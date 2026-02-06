package com.king.configuration.merge.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.common.utils.DateUtil;
import com.king.configuration.merge.entity.TfDeployRecord;
import com.king.configuration.merge.mapper.MergeRecordMapper;
import com.king.test.baseManage.testSystem.entity.TTestSystem;
import com.king.test.baseManage.testSystem.service.ITestSystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 保存发版记录服务
 */
@Service
public class SaveMergeRecordService extends ServiceImpl<MergeRecordMapper, TfDeployRecord> {

    private static final Logger logger = LoggerFactory.getLogger(SaveMergeRecordService.class);

    @Autowired
    @Qualifier("testSystemServiceImpl")
    private ITestSystemService testSystemService;

    /**
     * 保存合并登记信息
     * 合并成功后调用此方法保存合并登记信息
     *
     * @param systemId 系统ID
     * @param testStage 测试阶段（SIT、PAT）
     * @param sendTestInfo 送测单信息
     * @param recordNum 版本登记数量
     * @param isRunSql 是否执行sql
     * @param isUpdateConfig 是否更新配置
     * @param codeList 代码清单
     * @param componentInfo 组件信息（可选）
     * @return 生成的版本号（versionCode）
     */
    public String saveDeployRecord(String systemId, String testStage, String sendTestInfo,
                                   Integer recordNum, Boolean isRunSql, Boolean isUpdateConfig,
                                   String codeList, String componentInfo) {
        // 参数校验
        Assert.isTrue(StringUtils.hasText(systemId), "系统ID不能为空");
        Assert.isTrue(StringUtils.hasText(testStage), "测试阶段不能为空");

        // 确保 testStage 为大写
        testStage = testStage.toUpperCase();

        // 根据systemId获取测试系统信息
        TTestSystem testSystem = testSystemService.getById(systemId);
        Assert.notNull(testSystem, "未找到系统ID为 " + systemId + " 的测试系统信息");

        String sysAbbreviation = testSystem.getSysAbbreviation();
        Assert.isTrue(StringUtils.hasText(sysAbbreviation), "系统简称不能为空");

        // 获取当前日期（YYYYMMDD格式）
        String currentDate = DateUtil.getDateFormatYMD();

        // 查询当天同一系统同一测试阶段的最后一条记录（按部署时间倒序）
        TfDeployRecord lastRecord = this.baseMapper.selectLastDeployRecordBySystemAndStageAndDate(
                systemId, testStage, currentDate);

        // 获取本次发版登记的版本登记数量，如果为空则默认为1
        Integer currentRecordNum = recordNum;
        if (currentRecordNum == null || currentRecordNum < 1) {
            currentRecordNum = 1;
        }

        // 计算最终的版本登记数量
        Integer finalRecordNum;
        if (lastRecord != null && StringUtils.hasText(lastRecord.getVersionCode())) {
            // 从最后一条记录的 versionCode 中提取 recordNum
            // versionCode 格式：sysAbbreviation-TEST_STAGE-YYYYMMDD-recordNum
            String lastVersionCode = lastRecord.getVersionCode();
            try {
                // 按最后一个 "-" 分割，取最后一部分作为 recordNum
                int lastDashIndex = lastVersionCode.lastIndexOf("-");
                if (lastDashIndex >= 0 && lastDashIndex < lastVersionCode.length() - 1) {
                    String lastRecordNumStr = lastVersionCode.substring(lastDashIndex + 1);
                    Integer lastRecordNum = Integer.parseInt(lastRecordNumStr);
                    // 最终的 recordNum = 最后一条的 recordNum + 本次的 recordNum
                    finalRecordNum = lastRecordNum + currentRecordNum;
                } else {
                    // 如果解析失败，使用本次的 recordNum
                    finalRecordNum = currentRecordNum;
                }
            } catch (NumberFormatException e) {
                logger.warn("解析最后一条记录的版本号失败: {}, 使用本次的版本登记数量", lastVersionCode);
                finalRecordNum = currentRecordNum;
            }
        } else {
            // 如果没有找到最后一条记录，使用本次的 recordNum
            finalRecordNum = currentRecordNum;
        }

        // 拼接版本号：系统简写-测试阶段-年月日-版本登记数量
        // 格式：sysAbbreviation-TEST_STAGE-YYYYMMDD-recordNum
        String versionCode = String.format("%s-%s-%s-%d", sysAbbreviation, testStage, currentDate, finalRecordNum);

        // 创建发版登记对象
        TfDeployRecord deployRecord = new TfDeployRecord();
        deployRecord.setSystemId(systemId);
        deployRecord.setTestStage(testStage);
        deployRecord.setSendTestInfo(sendTestInfo);
        deployRecord.setRecordNum(currentRecordNum);
        deployRecord.setIsRunSql(isRunSql != null ? isRunSql : false);
        deployRecord.setIsUpdateConfig(isUpdateConfig != null ? isUpdateConfig : false);
        deployRecord.setCodeList(codeList);
        deployRecord.setComponentInfo(componentInfo);
        deployRecord.setVersionCode(versionCode);
        deployRecord.setDeployTime(LocalDateTime.now());

        // 保存发版登记信息
        this.save(deployRecord);
        logger.info("合并登记保存成功: systemId={}, testStage={}, versionCode={}",
                systemId, testStage, versionCode);
        return versionCode;
    }
}
