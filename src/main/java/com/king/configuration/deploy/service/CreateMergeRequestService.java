package com.king.configuration.deploy.service;

import com.king.configuration.deploy.dto.MergeToTargetBranchDTO;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.MergeRequest;
import org.gitlab4j.api.models.MergeRequestParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * GitLab合并请求服务类
 */
@Service
public class CreateMergeRequestService {

    private static final Logger logger = LoggerFactory.getLogger(CreateMergeRequestService.class);

    /**
     * 创建合并请求
     * @param mergeDTO 合并到目标分支的信息
     * @return MergeRequest 合并请求对象
     * @throws GitLabApiException GitLab API异常
     */
    public MergeRequest createMergeRequest(MergeToTargetBranchDTO mergeDTO) throws GitLabApiException {
        Assert.notNull(mergeDTO, "合并到目标分支的信息不能为空");
        
        // 获取 gitlabUrl
        String gitlabUrl = mergeDTO.getGitlabUrl();
        if (!StringUtils.hasText(gitlabUrl)) {
            throw new RuntimeException("GitlabUrl为空，无法进行合并操作");
        }
        
        // 获取 projectId
        Integer projectId = mergeDTO.getProjectId();
        if (projectId == null) {
            throw new RuntimeException("ProjectId为空，无法进行合并操作");
        }
        
        // 获取 privateToken
        String privateToken = mergeDTO.getPrivateToken();
        if (!StringUtils.hasText(privateToken)) {
            throw new RuntimeException("访问令牌为空，无法进行合并操作");
        }
        
        // 获取 title
        String title = mergeDTO.getTitle();
        if (!StringUtils.hasText(title)) {
            title = "Auto Merge Request";
        }
        
        // 获取 sourceBranch
        String sourceBranch = mergeDTO.getSourceBranch();
        if (!StringUtils.hasText(sourceBranch)) {
            throw new RuntimeException("源分支为空，无法进行合并操作");
        }
        
        // 获取 targetBranch
        String targetBranch = mergeDTO.getTargetBranch();
        if (!StringUtils.hasText(targetBranch)) {
            throw new RuntimeException("目标分支为空，无法进行合并操作");
        }
        
        // 获取 files
        List<String> files = mergeDTO.getFilePaths();
        if (files == null || files.isEmpty()) {
            throw new RuntimeException("需要合并的文件不能为空，无法进行合并操作");
        }
        
        // 创建 GitLabApi 实例
        GitLabApi gitLabApi = new GitLabApi(gitlabUrl, privateToken);
        
        // 创建合并请求参数
        MergeRequestParams mergeRequestParams = new MergeRequestParams()
                .withSourceBranch(sourceBranch)
                .withTargetBranch(targetBranch)
                .withTitle(title + " : " + sourceBranch + " to " + targetBranch);

        // 设置合并请求描述，包含要合并的文件列表
        StringBuilder description = new StringBuilder("Files to merge: ");
        for (String file : files) {
            description.append(file).append(", ");
        }
        if (description.length() > 0) {
            description.delete(description.length() - 2, description.length());
        }
        mergeRequestParams.withDescription(description.toString());

        // 创建合并请求
        logger.info("创建合并请求: 项目ID={}, 源分支={}, 目标分支={}", projectId, sourceBranch, targetBranch);
        MergeRequest mergeRequest = gitLabApi.getMergeRequestApi().createMergeRequest(projectId, mergeRequestParams);
        logger.info("合并请求创建成功: MR IID={}, URL={}", mergeRequest.getIid(), mergeRequest.getWebUrl());

        return mergeRequest;
    }
}

