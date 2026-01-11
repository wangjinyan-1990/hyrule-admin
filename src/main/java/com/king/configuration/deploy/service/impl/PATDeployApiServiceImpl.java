package com.king.configuration.deploy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.common.utils.DateUtil;
import com.king.configuration.deploy.dto.PATDeployRecordDTO;
import com.king.configuration.deploy.entity.TfDeployRecord;
import com.king.configuration.deploy.mapper.DeployRecordMapper;
import com.king.configuration.deploy.service.IPATDeployApiService;
import com.king.configuration.sysConfigInfo.entity.TfSystemConfiguration;
import com.king.configuration.sysConfigInfo.mapper.SysConfigInfoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * PAT部署Service实现类
 */
@Service("patDeployApiServiceImpl")
public class PATDeployApiServiceImpl extends ServiceImpl<DeployRecordMapper, TfDeployRecord> implements IPATDeployApiService {

    private static final Logger logger = LoggerFactory.getLogger(PATDeployApiServiceImpl.class);

    @Resource
    private SysConfigInfoMapper sysConfigInfoMapper;

    @Override
    public void createPATDeployRecordByApi(PATDeployRecordDTO dto) {
        Assert.notNull(dto, "发版登记信息不能为空");

        // 校验必填字段
        Assert.isTrue(StringUtils.hasText(dto.getSysAbbreviation()), "系统简称不能为空");

        // 根据系统简称查询系统配置信息
        List<TfSystemConfiguration> sysConfigList = sysConfigInfoMapper.selectSysConfigInfoBySysAbbreviation(dto.getSysAbbreviation());
        Assert.isTrue(sysConfigList != null && !sysConfigList.isEmpty(),
                "未找到系统简称为 " + dto.getSysAbbreviation() + " 的配置信息");

        TfSystemConfiguration sysConfig = sysConfigList.get(0);
        String systemId = sysConfig.getSystemId();
        String sysAbbreviation = sysConfig.getSysAbbreviation();
        Assert.isTrue(StringUtils.hasText(sysAbbreviation), "系统简称不能为空");

        // 创建发版登记对象
        TfDeployRecord deployRecord = new TfDeployRecord();
        deployRecord.setTestStage("PAT");  // testStage固定为PAT
        deployRecord.setSystemId(systemId);
        deployRecord.setComponentInfo(dto.getComponentInfo());
        deployRecord.setSendTestCode(dto.getSendTestCode());
        // 将Boolean转换为数据库中的1/0：true -> 1, false -> 0
        // MyBatis Plus会自动将Boolean的true/false转换为数据库的1/0
        deployRecord.setIsRunSql(dto.getIsRunSql() != null ? dto.getIsRunSql() : false);
        deployRecord.setIsUpdateConfig(dto.getIsUpdateConfig() != null ? dto.getIsUpdateConfig() : false);
        deployRecord.setRecordNum(dto.getRecordNum());
        // 设置代码清单，如果为null则设置为空字符串，避免数据库插入错误
        deployRecord.setCodeList(dto.getCodeList() != null ? dto.getCodeList() : "");
        deployRecord.setDeployTime(LocalDateTime.now());

        // 获取当前日期（YYYYMMDD格式）
        String currentDate = DateUtil.getDateFormatYMD();

        // 查询当天同一系统同一测试阶段的最后一条记录（按部署时间倒序）
        TfDeployRecord lastRecord = this.baseMapper.selectLastDeployRecordBySystemAndStageAndDate(
                systemId, "PAT", currentDate);

        // 获取本次发版登记的版本登记数量，如果为空则默认为1
        Integer currentRecordNum = deployRecord.getRecordNum();
        if (currentRecordNum == null || currentRecordNum < 1) {
            currentRecordNum = 1;
        }

        // 计算最终的版本登记数量
        Integer finalRecordNum;
        if (lastRecord != null && StringUtils.hasText(lastRecord.getVersionCode())) {
            // 从最后一条记录的 versionCode 中提取 recordNum
            // versionCode 格式：sysAbbreviation-PAT-YYYYMMDD-recordNum
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
        // 格式：sysAbbreviation-PAT-YYYYMMDD-recordNum
        String versionCode = String.format("%s-PAT-%s-%d", sysAbbreviation, currentDate, finalRecordNum);
        deployRecord.setVersionCode(versionCode);
        // 更新 recordNum 为最终计算的值
        deployRecord.setRecordNum(currentRecordNum);

        // 保存发版登记信息（gitlabUrl为"1"，componentInfo、sendTestCode正常登记）
        this.save(deployRecord);
        logger.info("PAT发版登记创建成功（外部API）: versionCode={}, componentInfo={}, sendTestCode={}",
                versionCode, deployRecord.getComponentInfo(), deployRecord.getSendTestCode());
    }

}
