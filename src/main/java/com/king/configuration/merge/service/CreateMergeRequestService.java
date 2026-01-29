package com.king.configuration.merge.service;

import com.king.configuration.merge.dto.MergeToTargetBranchDTO;
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
 * 使用 gitlab4j-api 6.0.0-rc.8 版本
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

        // 获取 gitlabUrl（可能包含项目路径，需要提取纯主机地址）
        String gitlabUrl = mergeDTO.getGitlabUrl();
        if (!StringUtils.hasText(gitlabUrl)) {
            throw new RuntimeException("GitlabUrl为空，无法进行合并操作");
        }

        // 从 gitlabUrl 中提取纯主机地址（gitlab4j-api 需要基础URL，会自动拼接 /api/v4/...）
        // 例如：http://9.1.13.159/escm/dcip/versionsync.git -> http://9.1.13.159
        String baseUrl = extractBaseUrl(gitlabUrl);
        logger.info("从 gitlabUrl 提取基础URL: {} -> {}", gitlabUrl, baseUrl);

        // 获取 projectId 并转换为 Long 类型（gitlab4j-api 5.8.0 内部使用 Long 类型）
        Integer projectIdInteger = mergeDTO.getProjectId();
        if (projectIdInteger == null) {
            throw new RuntimeException("ProjectId为空，无法进行合并操作");
        }
        Long projectId = projectIdInteger.longValue();

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

        // 创建 GitLabApi 实例（使用基础URL，不包含项目路径）
        GitLabApi gitLabApi = new GitLabApi(baseUrl, privateToken);

        // 检查 gitlab4j-api 版本是否兼容
        checkGitLabApiCompatibility(gitLabApi, baseUrl);

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

        // 注意：gitlab4j-api 5.8.0 只支持数字型项目ID（Long/Integer），不支持字符串路径
        // 如果传入字符串路径，会抛出 ClassCastException: String cannot be cast to Long
        // 因此我们只能使用数字型项目ID创建合并请求

        // 如果项目ID失败（404），尝试通过项目路径重新获取项目ID
        try {
            MergeRequest mergeRequest = gitLabApi.getMergeRequestApi().createMergeRequest(projectId, mergeRequestParams);
            logger.info("合并请求创建成功（使用项目ID）: MR IID={}, URL={}", mergeRequest.getIid(), mergeRequest.getWebUrl());
            return mergeRequest;
        } catch (GitLabApiException e) {
            int httpStatus = e.getHttpStatus();
            String errorMessage = e.getMessage();

            logger.error("GitLab API调用失败: HTTP状态码={}, 项目ID={}, GitLab 基础URL={}, 错误信息={}",
                    httpStatus, projectId, baseUrl, errorMessage);

            // 根据 HTTP 状态码提供更具体的错误信息
            if (httpStatus == 404) {
                throw new GitLabApiException("创建合并请求失败: 项目不存在或无权访问（404）。" +
                        "已尝试使用项目ID=" + projectId +
                        "可能的原因：1) 项目ID不正确；" +
                        "2) 源分支或目标分支不存在；" +
                        "3) GitLab API端点路径可能不正确；" +
                        "4) 访问令牌权限不足（虽然已确认有 api 和 write_repository 权限，但可能还需要其他权限）。" +
                        "注意：gitlab4j-api 5.3.0 只支持数字型项目ID，不支持字符串路径。原始错误: " + errorMessage);
            } else if (httpStatus == 401 || httpStatus == 403) {
                throw new GitLabApiException("创建合并请求失败: 认证失败或权限不足（" + httpStatus + "）。" +
                        "请检查访问令牌是否有效且有足够权限（需要 api 和 write_repository 权限）。原始错误: " + errorMessage);
            } else if (httpStatus == 400) {
                throw new GitLabApiException("创建合并请求失败: 请求参数错误（400）。" +
                        "请检查：1) 源分支和目标分支是否存在；2) 分支名称是否正确；3) 是否已存在相同的合并请求。原始错误: " + errorMessage);
            } else {
                throw new GitLabApiException("创建合并请求失败: GitLab API错误（HTTP " + httpStatus + "）。错误信息: " + errorMessage);
            }
        }
    }

    /**
     * 从 gitlabUrl 中提取纯主机地址（基础URL）
     * gitlab4j-api 需要基础URL，会自动拼接 /api/v4/... 路径
     * 例如：http://9.1.13.159/escm/dcip/versionsync.git -> http://9.1.13.159
     *
     * @param gitlabUrl 完整的 GitLab URL（可能包含项目路径）
     * @return 基础URL（纯主机地址）
     */
    private String extractBaseUrl(String gitlabUrl) {
        if (gitlabUrl == null || !gitlabUrl.contains("://")) {
            return gitlabUrl; // 如果格式不正确，直接返回原值
        }

        // 提取协议和主机部分
        // 例如：http://9.1.13.159/escm/dcip/versionsync.git
        // 提取：http://9.1.13.159
        int protocolIndex = gitlabUrl.indexOf("://");
        if (protocolIndex > 0) {
            int pathStartIndex = gitlabUrl.indexOf("/", protocolIndex + 3);
            if (pathStartIndex > 0) {
                return gitlabUrl.substring(0, pathStartIndex);
            }
        }

        // 如果没有找到路径分隔符，说明已经是基础URL
        return gitlabUrl;
    }

    /**
     * 检查 gitlab4j-api 版本是否与 GitLab 服务器兼容
     * @param gitLabApi GitLabApi 实例
     * @param hostUrl GitLab 服务器地址（基础URL）
     */
    private void checkGitLabApiCompatibility(GitLabApi gitLabApi, String hostUrl) {
        try {
            // 尝试使用无参数的 getProjects() 方法，更兼容不同版本
            // 如果失败，尝试使用其他方法
            try {
                gitLabApi.getProjectApi().getProjects();
                logger.info("gitlab4j-api 版本兼容性检查通过，API 调用成功，可能兼容。GitLab URL: {}", hostUrl);
            } catch (NoSuchMethodError | GitLabApiException e) {
                // 如果无参数方法不存在，尝试使用带参数的方法
                try {
                    gitLabApi.getProjectApi().getProjects(1, 1);
                    logger.info("gitlab4j-api 版本兼容性检查通过（使用分页参数），API 调用成功，可能兼容。GitLab URL: {}", hostUrl);
                } catch (GitLabApiException e2) {
                    // 如果都失败，记录详细错误信息
                    int httpStatus = e2.getHttpStatus();
                    logger.warn("gitlab4j-api 版本兼容性检查失败，API 调用失败，可能存在兼容性问题: HTTP状态码={}, 错误信息={}。GitLab URL: {}",
                            httpStatus, e2.getMessage(), hostUrl);
                }
            }
        } catch (Exception e) {
            logger.warn("gitlab4j-api 版本兼容性检查时发生未知错误: {}。GitLab URL: {}",
                    e.getMessage(), hostUrl);
            // 不抛出异常，只记录警告，让后续操作继续尝试
        }
    }
}
