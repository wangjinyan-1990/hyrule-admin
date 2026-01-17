package com.king.configuration.deploy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.common.Result;
import com.king.configuration.deploy.dto.MergeRequestInfo;
import com.king.configuration.deploy.dto.MergeToTargetBranchDTO;
import com.king.configuration.deploy.entity.TfDeployRecord;
import com.king.configuration.deploy.mapper.DeployRecordMapper;
import com.king.configuration.deploy.service.AcceptMergeRequestService;
import com.king.configuration.deploy.service.CreateMergeRequestService;
import com.king.configuration.deploy.service.FilesPusherToBranchService;
import com.king.configuration.deploy.service.GitLabUrlParseService;
import com.king.configuration.deploy.service.IPATDeployService;
import com.king.configuration.deploy.service.ParseMergeRequestService;
import com.king.configuration.deploy.service.SaveDeployRecordService;
import com.king.configuration.sysConfigInfo.entity.TfSystemConfiguration;
import com.king.configuration.sysConfigInfo.mapper.SysConfigInfoMapper;
import org.gitlab4j.api.models.MergeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
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
    GitLabUrlParseService gitLabUrlParseService;

    @Resource
    private ParseMergeRequestService parseMergeRequestService;

    @Resource
    private CreateMergeRequestService createMergeRequestService;

    @Resource
    private AcceptMergeRequestService acceptMergeRequestService;

    @Resource
    FilesPusherToBranchService filesPusherToBranchService;

    @Resource
    private SaveDeployRecordService saveDeployRecordService;

    @Override
    public Result<String> createPATDeployRecord(TfDeployRecord deployRecord) {
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
        // 注意：即使 gitlabUrl 为 "1"，componentInfo、sendTestInfo 等其他字段仍会正常登记保存
        String gitlabUrl = deployRecord.getGitlabUrl();
        if (!StringUtils.hasText(gitlabUrl) || "1".equals(gitlabUrl)) {
            logger.info("gitlabUrl 为 '1' 或为空，跳过代码合并，但其他字段（componentInfo、sendTestInfo等）仍会正常登记");
        } else {
            logger.info("开始处理代码合并，gitlabUrl: {}", gitlabUrl);
            // gitlabUrl 不为 "1"，需要获取分支信息进行合并操作
            String sourceBranch = deployRecord.getSourceBranch();
            String targetBranch = deployRecord.getTargetBranch();
            String sendTestInfo = deployRecord.getSendTestInfo();
            String codeList = deployRecord.getCodeList();

            logger.info("合并参数 - sourceBranch: {}, targetBranch: {}, codeList长度: {}",
                    sourceBranch, targetBranch, codeList != null ? codeList.length() : 0);

            // 校验 GitLab URL 格式：http://开头，.git结尾
            Pattern gitlabUrlPattern = Pattern.compile("^http://.*\\.git$");
            if (!gitlabUrlPattern.matcher(gitlabUrl).matches()) {
                logger.error("GitLab URL格式不正确: {}, 应为 http://开头，.git结尾", gitlabUrl);
                throw new RuntimeException("GitLab URL格式不正确，应为 http://开头，.git结尾");
            }

            // 校验分支信息
            if (!StringUtils.hasText(sourceBranch)) {
                logger.error("源分支为空，无法进行合并操作");
                throw new RuntimeException("源分支不能为空");
            }
            if (!StringUtils.hasText(targetBranch)) {
                logger.error("目标分支为空，无法进行合并操作");
                throw new RuntimeException("目标分支不能为空");
            }

            // 获取 privateToken
            String privateToken = sysConfig.getPrivateToken();
            if (!StringUtils.hasText(privateToken)) {
                logger.error("系统配置中访问令牌为空，无法进行合并操作");
                throw new RuntimeException("系统配置中访问令牌为空，无法进行合并操作");
            }

            try {
                // 处理codeList，转换为文件路径列表
                List<String> files = parseCodeList(codeList);

                // 方式1：调用JGit 把目标分支拉下来 → 把源分支里指定文件覆盖进来 → 提交并直接推回目标分支
                Result<String> pushResult = pushFilesToBranch(
                        gitlabUrl, privateToken, sourceBranch, targetBranch, sendTestInfo, files);
                
                // 检查推送结果
                if (pushResult.getCode() != 20000) {
                    throw new RuntimeException("JGit推送文件到分支失败: " + pushResult.getMessage());
                }
                
                logger.info("JGit推送文件到分支成功: {}", pushResult.getData());

                // 方式2：调用 gitlab4j-api 创建合并请求的方法，创建合并请求mergeRequest
                // createAndAcceptMergeRequest(gitlabUrl, privateToken, sourceBranch, targetBranch, sendTestInfo, files);
            } catch (Exception e) {
                logger.error("代码合并失败: {}", e.getMessage(), e);
                throw new RuntimeException("代码合并失败: " + e.getMessage(), e);
            }

        }

        // 调用 SaveDeployRecordService 保存发版登记信息
        String versionCode = saveDeployRecordService.saveDeployRecord(
                deployRecord.getSystemId(),
                "PAT",
                deployRecord.getSendTestInfo(),
                deployRecord.getRecordNum(),
                deployRecord.getIsRunSql(),
                deployRecord.getIsUpdateConfig(),
                deployRecord.getCodeList(),
                deployRecord.getComponentInfo()
        );
        logger.info("PAT发版登记创建成功: versionCode={}", versionCode);
        
        // 返回成功结果
        return Result.success(versionCode, "PAT发版登记创建成功");
    }

    /**
     * 解析代码清单，将字符串转换为文件路径列表
     * @param codeList 代码清单字符串（换行符分隔）
     * @return 文件路径列表
     */
    private List<String> parseCodeList(String codeList) {
        List<String> files = new ArrayList<>();
        if (StringUtils.hasText(codeList)) {
            logger.info("开始解析codeList，原始内容: {}", codeList);
            // 将codeList，由String类型遍历放进files
            // codeList 通常是用换行符分隔的文件路径列表
            String[] codeLines = codeList.split("[\r\n]+");
            logger.info("codeList分割后行数: {}", codeLines.length);
            for (String line : codeLines) {
                String trimmedLine = line.trim();
                // 过滤掉空行
                if (StringUtils.hasText(trimmedLine)) {
                    files.add(trimmedLine);
                }
            }
            logger.info("解析codeList后，有效文件数: {}", files.size());
        } else {
            logger.warn("codeList为空，将使用空文件列表");
        }
        return files;
    }

    /**
     * 方式1：使用JGit推送文件到分支
     * 把目标分支拉下来 → 把源分支里指定文件覆盖进来 → 提交并直接推回目标分支
     *
     * @param gitlabUrl GitLab仓库地址
     * @param privateToken 访问令牌
     * @param sourceBranch 源分支
     * @param targetBranch 目标分支
     * @param sendTestInfo 送测单信息
     * @param files 要推送的文件列表
     * @return Result<String> 成功时返回"新增X个,删除Y个,修改Z个"，失败时返回失败原因
     */
    public Result<String> pushFilesToBranch(String gitlabUrl, String privateToken, String sourceBranch,
                                  String targetBranch, String sendTestInfo, List<String> files) {
        logger.info("开始使用JGit推送文件到分支: gitlabUrl={}, sourceBranch={}, targetBranch={}, files数量={}",
                gitlabUrl, sourceBranch, targetBranch, files != null ? files.size() : 0);

        Result<String> result = FilesPusherToBranchService.pushFiles(
                gitlabUrl, privateToken, sourceBranch, targetBranch, sendTestInfo, files);
        
        if (result.getCode() == 20000) {
            logger.info("JGit推送文件到分支成功: {}", result.getData());
        } else {
            logger.error("JGit推送文件到分支失败: {}", result.getMessage());
        }
        
        return result;
    }

    /**
     * 方式2：创建并接受合并请求
     * 调用 gitlab4j-api 创建合并请求，然后自动接受合并请求
     *
     * @param gitlabUrl GitLab仓库地址
     * @param privateToken 访问令牌
     * @param sourceBranch 源分支
     * @param targetBranch 目标分支
     * @param files 要合并的文件列表
     * @return 合并请求对象
     */
    public MergeRequest createAndAcceptMergeRequest(String gitlabUrl, String privateToken,
                                                    String sourceBranch, String targetBranch,
                                                    String sendTestInfo, List<String> files) {
        logger.info("开始创建并接受合并请求: gitlabUrl={}, sourceBranch={}, targetBranch={}, files数量={}",
                gitlabUrl, sourceBranch, targetBranch, files != null ? files.size() : 0);

        try {
            // 解析gitlabUrl，返回projectId
            logger.info("开始解析GitLab URL获取projectId: {}", gitlabUrl);
            Integer projectId = gitLabUrlParseService.parseGitLabUrlAndGetProjectId(gitlabUrl, privateToken);
            logger.info("成功获取projectId: {}", projectId);

            // 创建需要合并的DTO
            MergeToTargetBranchDTO mergeDTO = new MergeToTargetBranchDTO();
            mergeDTO.setGitlabUrl(gitlabUrl);
            mergeDTO.setProjectId(projectId);
            mergeDTO.setPrivateToken(privateToken);
            mergeDTO.setSourceBranch(sourceBranch);
            mergeDTO.setTargetBranch(targetBranch);
            mergeDTO.setTitle(sendTestInfo);
            mergeDTO.setFilePaths(files);

            // 调用 gitlab4j-api 创建合并请求的方法，创建合并请求mergeRequest
            MergeRequest mergeRequest = createMergeRequestService.createMergeRequest(mergeDTO);
            if (mergeRequest == null) {
                logger.warn("createMergeRequest返回null");
                throw new RuntimeException("创建合并请求失败: 返回null");
            }

            logger.info("成功创建合并请求: MR ID={}, URL={}",
                    mergeRequest.getIid(), mergeRequest.getWebUrl());
            logger.info("等待合并");

            // 解析 Merge Request URL
            MergeRequestInfo mrInfo = parseMergeRequestService.parseMergeRequestUrl(mergeRequest.getWebUrl());
            if (mrInfo == null) {
                throw new RuntimeException("Merge Request URL格式不正确: " + mergeRequest.getWebUrl());
            }

            // 调用 GitLab API 接受 Merge Request
            String mergedState = acceptMergeRequestService.acceptMergeRequest(
                    mrInfo.getGitLabUrl(), mrInfo.getProjectPath(), mrInfo.getMrIid(), privateToken);

            // 验证合并是否成功
            if (!"merged".equals(mergedState)) {
                logger.error("Merge Request合并失败，最终状态: {}, URL: {}", mergedState, mergeRequest.getWebUrl());
                throw new RuntimeException("Merge Request合并失败，最终状态: " + mergedState);
            }

            logger.info("成功合并 Merge Request: {}, 状态: {}", mergeRequest.getWebUrl(), mergedState);
            return mergeRequest;
        } catch (Exception e) {
            logger.error("创建并接受合并请求失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建并接受合并请求失败: " + e.getMessage(), e);
        }
    }

}
