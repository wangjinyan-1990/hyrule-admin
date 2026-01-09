package com.king.configuration.deploy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.common.utils.DateUtil;
import com.king.configuration.deploy.dto.MergeToTargetBranchDTO;
import com.king.configuration.deploy.dto.PATDeployRecordDTO;
import com.king.configuration.deploy.entity.TfDeployRecord;
import com.king.configuration.deploy.mapper.DeployRecordMapper;
import com.king.configuration.deploy.service.CreateMergeRequestService;
import com.king.configuration.deploy.service.GitLabUrlParseService;
import com.king.configuration.deploy.service.IPATDeployService;
import com.king.configuration.sysConfigInfo.entity.TfSystemConfiguration;
import com.king.configuration.sysConfigInfo.mapper.SysConfigInfoMapper;
import org.gitlab4j.api.models.MergeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * PAT部署Service实现类
 */
@Service("patDeployServiceImpl")
public class PATDeployServiceImpl extends ServiceImpl<DeployRecordMapper, TfDeployRecord> implements IPATDeployService {

    private static final Logger logger = LoggerFactory.getLogger(PATDeployServiceImpl.class);

    @Resource
    private SysConfigInfoMapper sysConfigInfoMapper;

    @Resource
    private CreateMergeRequestService gitLabMergeService;

    @Resource
    GitLabUrlParseService gitLabUrlParseService;

    @Override
    public void createPATDeployRecord(TfDeployRecord deployRecord) {
        Assert.notNull(deployRecord, "发版登记信息不能为空");
        
        // 设置测试阶段为PAT
        deployRecord.setTestStage("PAT");
        
        // 校验必填字段
        Assert.isTrue(StringUtils.hasText(deployRecord.getSystemId()), "系统ID不能为空");
        
        // 根据systemId获取系统配置信息
        List<TfSystemConfiguration> sysConfigList = sysConfigInfoMapper.selectSysConfigInfoBySystemId(deployRecord.getSystemId());
        Assert.isTrue(sysConfigList != null && !sysConfigList.isEmpty(), "未找到系统ID为 " + deployRecord.getSystemId() + " 的配置信息");
        
        TfSystemConfiguration sysConfig = sysConfigList.get(0);
        String sysAbbreviation = sysConfig.getSysAbbreviation();
        Assert.isTrue(StringUtils.hasText(sysAbbreviation), "系统简称不能为空");
        
        // 先判断 gitlabUrl 是否为 "1"，如果是则跳过合并操作
        // 注意：即使 gitlabUrl 为 "1"，componentInfo、sendTestCode 等其他字段仍会正常登记保存
        String gitlabUrl = deployRecord.getGitlabUrl();
        if (!StringUtils.hasText(gitlabUrl) || "1".equals(gitlabUrl)) {
            logger.info("gitlabUrl 为 '1' 或为空，跳过代码合并，但其他字段（componentInfo、sendTestCode等）仍会正常登记");
        } else {
            // gitlabUrl 不为 "1"，需要获取分支信息进行合并操作
            String sourceBranch = deployRecord.getSourceBranch();
            String targetBranch = deployRecord.getTargetBranch();
            String codeList = deployRecord.getCodeList();
            
            // 校验 GitLab URL 格式：http://开头，.git结尾
            Pattern gitlabUrlPattern = Pattern.compile("^http://.*\\.git$");
            if (!gitlabUrlPattern.matcher(gitlabUrl).matches()) {
                throw new RuntimeException("GitLab URL格式不正确，应为 http://开头，.git结尾");
            }
            
            // 校验分支信息
            if (!StringUtils.hasText(sourceBranch)) {
                throw new RuntimeException("源分支不能为空");
            }
            if (!StringUtils.hasText(targetBranch)) {
                throw new RuntimeException("目标分支不能为空");
            }
            
            // 获取 privateToken
            String privateToken = sysConfig.getPrivateToken();
            if (!StringUtils.hasText(privateToken)) {
                throw new RuntimeException("系统配置中访问令牌为空，无法进行合并操作");
            }
            
            try {
                if (StringUtils.hasText(codeList)) {
                    List<String> files = new ArrayList<>();
                    // 将codeList，由String类型遍历放进files
                    // codeList 通常是用换行符分隔的文件路径列表
                    String[] codeLines = codeList.split("[\r\n]+");
                    for (String line : codeLines) {
                        String trimmedLine = line.trim();
                        // 过滤掉空行
                        if (StringUtils.hasText(trimmedLine)) {
                            files.add(trimmedLine);
                        }
                    }
                    
                    if (files.isEmpty()) {
                        throw new RuntimeException("代码清单不能为空");
                    }

                    //解析gitlabUrl，返回projectId
                    Integer projectId = gitLabUrlParseService.parseGitLabUrlAndGetProjectId(gitlabUrl, privateToken);
                    // 创建需要合并的DTO
                    MergeToTargetBranchDTO mergeDTO = new MergeToTargetBranchDTO();
                    mergeDTO.setGitlabUrl(gitlabUrl);
                    mergeDTO.setProjectId(projectId);
                    mergeDTO.setPrivateToken(privateToken);
                    mergeDTO.setSourceBranch(sourceBranch);
                    mergeDTO.setTargetBranch(targetBranch);
                    mergeDTO.setFilePaths(files);
                    //调用创建合并请求的方法，创建合并请求mergeRequest
                    MergeRequest mergeRequest = gitLabMergeService.createMergeRequest(mergeDTO);
                }
                logger.info("成功完成代码合并");
            } catch (Exception e) {
                logger.error("代码合并失败: {}", e.getMessage(), e);
                throw new RuntimeException("代码合并失败: " + e.getMessage(), e);
            }
        }
        
        // 获取当前日期（YYYYMMDD格式）
        String currentDate = DateUtil.getDateFormatYMD();
        
        // 查询当天同一系统同一测试阶段的最后一条记录（按部署时间倒序）
        TfDeployRecord lastRecord = this.baseMapper.selectLastDeployRecordBySystemAndStageAndDate(
                deployRecord.getSystemId(), "PAT", currentDate);
        
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
        
        // 处理部署时间：如果前端传来的是 ISO 格式字符串，需要转换
        if (deployRecord.getDeployTime() == null) {
            deployRecord.setDeployTime(LocalDateTime.now());
        }
        
        // 处理布尔值：前端传来的是 1/0，需要转换为 Boolean
        // 注意：MyBatis Plus 会自动处理，但为了保险起见，这里也处理一下
        if (deployRecord.getIsRunSql() == null) {
            deployRecord.setIsRunSql(false);
        }
        if (deployRecord.getIsUpdateConfig() == null) {
            deployRecord.setIsUpdateConfig(false);
        }
        
        // 合并成功后，保存发版登记信息
        this.save(deployRecord);
        logger.info("PAT发版登记创建成功: versionCode={}", versionCode);
    }

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
        deployRecord.setIsRunSql(dto.getIsRunSql() != null ? dto.getIsRunSql() : false);
        deployRecord.setIsUpdateConfig(dto.getIsUpdateConfig() != null ? dto.getIsUpdateConfig() : false);
        deployRecord.setRecordNum(dto.getRecordNum());
        deployRecord.setGitlabUrl("1");  // 外部API接口，gitlabUrl固定为"1"，不进行合并
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
