package com.king.configuration.deploy.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.configuration.deploy.dto.MergeRequestInfo;
import com.king.configuration.deploy.entity.TfDeployRecord;
import com.king.configuration.deploy.mapper.DeployRecordMapper;
import com.king.configuration.deploy.service.AcceptMergeRequestService;
import com.king.configuration.deploy.service.ISITDeployService;
import com.king.configuration.deploy.service.SaveDeployRecordService;
import com.king.configuration.deploy.service.ParseMergeRequestService;
import com.king.configuration.sysConfigInfo.entity.TfSystemConfiguration;
import com.king.configuration.sysConfigInfo.mapper.SysConfigInfoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SIT部署Service实现类
 */
@Service("sitDeployServiceImpl")
public class SITDeployServiceImpl extends ServiceImpl<DeployRecordMapper, TfDeployRecord> implements ISITDeployService {

    private static final Logger logger = LoggerFactory.getLogger(SITDeployServiceImpl.class);

    @Resource
    private SysConfigInfoMapper sysConfigInfoMapper;

    @Resource
    private ParseMergeRequestService parseMergeRequestService;

    @Resource
    private AcceptMergeRequestService acceptMergeRequestService;

    @Resource
    private SaveDeployRecordService saveDeployRecordService;

    @Override
    public Map<String, String> parseMergeRequest(String mergeRequest, String systemId) {
        Map<String, String> result = new HashMap<>();

        try {
            // 1. 根据systemId获取系统配置信息
            if (!StringUtils.hasText(systemId)) {
                throw new IllegalArgumentException("系统ID不能为空");
            }

            List<TfSystemConfiguration> sysConfigInfo = sysConfigInfoMapper.selectSysConfigInfoBySystemId(systemId);
            if (sysConfigInfo == null || sysConfigInfo.isEmpty()) {
                throw new IllegalArgumentException("未找到系统ID为 " + systemId + " 的配置信息");
            }

            TfSystemConfiguration config = sysConfigInfo.get(0);
            String privateToken = config.getPrivateToken();

            if (!StringUtils.hasText(privateToken)) {
                throw new IllegalArgumentException("系统配置中访问令牌为空");
            }

            // 2. 解析Merge Request URL
            MergeRequestInfo mrInfo = parseMergeRequestService.parseMergeRequestUrl(mergeRequest);
            if (mrInfo == null) {
                throw new IllegalArgumentException("Merge Request URL格式不正确");
            }

            // 3. 调用GitLab API获取MR信息
            JSONObject mrData = acceptMergeRequestService.getMergeRequestFromGitLab(mrInfo.getGitLabUrl(), mrInfo.getProjectPath(),
                    mrInfo.getMrIid(), privateToken);

            if (mrData == null) {
                throw new RuntimeException("获取Merge Request信息失败");
            }

            // 4. 提取标题
            String title = mrData.getString("title");
            result.put("sendTestInfo", title != null ? title : "");

            // 5. 提取合并状态
            String state = mrData.getString("state");
            result.put("mergeState", state != null ? state : "");

            // 6. 获取代码清单（commits）
            String codeList = parseMergeRequestService.getCodeList(mrInfo.getGitLabUrl(), mrInfo.getProjectPath(),
                    mrInfo.getMrIid(), privateToken);
            result.put("codeList", codeList);

            logger.info("成功解析Merge Request: {}, 系统ID: {}", mergeRequest, systemId);

        } catch (Exception e) {
            logger.error("解析Merge Request失败: {}", e.getMessage(), e);
            throw new RuntimeException("解析Merge Request失败: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public void createSITDeployRecord(TfDeployRecord deployRecord) {
        Assert.notNull(deployRecord, "发版登记信息不能为空");

        // 设置测试阶段为SIT
        deployRecord.setTestStage("SIT");

        // 校验必填字段
        Assert.isTrue(StringUtils.hasText(deployRecord.getSystemId()), "系统ID不能为空");

        // 根据systemId获取系统配置信息
        List<TfSystemConfiguration> sysConfigList = sysConfigInfoMapper.selectSysConfigInfoBySystemId(deployRecord.getSystemId());
        Assert.isTrue(sysConfigList != null && !sysConfigList.isEmpty(), "未找到系统ID为 " + deployRecord.getSystemId() + " 的配置信息");

        TfSystemConfiguration sysConfig = sysConfigList.get(0);
        String sysAbbreviation = sysConfig.getSysAbbreviation();
        Assert.isTrue(StringUtils.hasText(sysAbbreviation), "系统简称不能为空");

        // 判断是否需要合并：如果有mergeRequest且状态不是'merged'，则调用 GitLab /merge 接口进行合并
        String mergeState = deployRecord.getMergeState();
        String mergeRequest = deployRecord.getMergeRequest();

        // 如果mergeRequest为"1"，或者mergeRequest已合并，只进行登记，不走合并流程
        if ("1".equals(mergeRequest)  || "merged".equals(mergeState)) {
            logger.info("mergeRequest 为 '1'或者mergeRequest已合并，跳过代码合并，只进行登记");
        } else if (StringUtils.hasText(mergeRequest)) {
            // 如果mergeState为null或不是'merged'，则需要合并
            boolean needMerge = mergeState == null || !"merged".equals(mergeState);

            if (needMerge) {
                logger.info("开始调用 GitLab /merge 接口进行合并，当前状态: {}, URL: {}",
                        mergeState != null ? mergeState : "null", mergeRequest);

                try {
                    // 解析 Merge Request URL
                    MergeRequestInfo mrInfo = parseMergeRequestService.parseMergeRequestUrl(mergeRequest);
                    if (mrInfo == null) {
                        throw new RuntimeException("Merge Request URL格式不正确: " + mergeRequest);
                    }

                    // 获取 privateToken
                    String privateToken = sysConfig.getPrivateToken();
                    if (!StringUtils.hasText(privateToken)) {
                        throw new RuntimeException("系统配置中访问令牌为空，无法进行合并操作");
                    }

                    // 调用 GitLab API 接受 Merge Request
                    String mergedState = acceptMergeRequestService.acceptMergeRequest(
                            mrInfo.getGitLabUrl(), mrInfo.getProjectPath(), mrInfo.getMrIid(), privateToken);

                    // 验证合并是否成功
                    if (!"merged".equals(mergedState)) {
                        logger.error("Merge Request合并失败，最终状态: {}, URL: {}", mergedState, mergeRequest);
                        throw new RuntimeException("Merge Request合并失败，最终状态: " + mergedState);
                    }

                    // 更新deployRecord中的mergeState
                    deployRecord.setMergeState("merged");

                    logger.info("成功合并 Merge Request: {}, 状态: {}", mergeRequest, mergedState);
                } catch (Exception e) {
                    logger.error("接受 Merge Request 失败: {}", e.getMessage(), e);
                    throw new RuntimeException("接受 Merge Request 失败: " + e.getMessage(), e);
                }
            } else {
                logger.info("Merge Request状态已经是 'merged'，跳过合并操作: {}", mergeRequest);
            }
        }

        // 调用 SaveDeployRecordService 保存发版登记信息
        String versionCode = saveDeployRecordService.saveDeployRecord(
                deployRecord.getSystemId(),
                "SIT",
                deployRecord.getSendTestInfo(),
                deployRecord.getRecordNum(),
                deployRecord.getIsRunSql(),
                deployRecord.getIsUpdateConfig(),
                deployRecord.getCodeList(),
                deployRecord.getComponentInfo()
        );
        logger.info("SIT发版登记创建成功: versionCode={}", versionCode);
    }


    @Override
    public void updateSITDeployRecord(TfDeployRecord deployRecord) {
        Assert.notNull(deployRecord, "发版登记信息不能为空");
        Assert.notNull(deployRecord.getDeployId(), "部署ID不能为空");

        // 检查记录是否存在
        TfDeployRecord existingRecord = this.getById(deployRecord.getDeployId());
        Assert.notNull(existingRecord, "发版登记信息不存在");

        // 确保测试阶段为SIT
        Assert.isTrue("SIT".equals(existingRecord.getTestStage()), "该记录不是SIT发版登记");

        // 设置测试阶段为SIT（确保更新后仍然是SIT）
        deployRecord.setTestStage("SIT");

        // 更新记录信息
        this.updateById(deployRecord);
    }

    @Override
    public TfDeployRecord getSITDeployRecordDetail(Integer deployId) {
        Assert.notNull(deployId, "部署ID不能为空");

        // 使用自定义查询方法，包含系统名称
        TfDeployRecord record = this.baseMapper.selectDeployRecordDetailById(deployId);
        Assert.notNull(record, "发版登记信息不存在");

        // 验证是否为SIT发版登记
        Assert.isTrue("SIT".equals(record.getTestStage()), "该记录不是SIT发版登记");

        return record;
    }

}
