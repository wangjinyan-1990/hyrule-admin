package com.king.configuration.deploy.service;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * GitLab URL解析服务
 * 用于解析GitLab仓库地址，提取基础URL、项目路径，并获取项目ID
 * 使用 gitlab4j-api 6.0.0-rc.8 版本
 */
@Service
public class GitLabUrlParseService {

    private static final Logger logger = LoggerFactory.getLogger(GitLabUrlParseService.class);

    /**
     * GitLab URL解析结果
     */
    public static class GitLabUrlParseResult {
        private final String baseUrl;      // GitLab基础URL，如 http://9.1.13.159
        private final String projectPath;  // 项目路径，如 escm/dcip/versionsync
        private Integer projectId;        // 项目ID（需要通过API获取）

        public GitLabUrlParseResult(String baseUrl, String projectPath) {
            this.baseUrl = baseUrl;
            this.projectPath = projectPath;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public String getProjectPath() {
            return projectPath;
        }

        public Integer getProjectId() {
            return projectId;
        }

        public void setProjectId(Integer projectId) {
            this.projectId = projectId;
        }
    }

    /**
     * 解析GitLab URL，提取基础URL和项目路径
     * 支持格式：
     * http://9.1.13.159/escm/dcip/versionsync.git
     * http://9.1.13.159/ncp/ncp.git
     * http://9.6.232.212/dtp/MSVFW_OUTBD_MODUL.git
     * 
     * @param gitlabUrl GitLab仓库地址
     * @return 解析结果，包含基础URL和项目路径
     * @throws RuntimeException 如果URL格式不正确
     */
    public GitLabUrlParseResult parseGitLabUrl(String gitlabUrl) {
        if (!StringUtils.hasText(gitlabUrl)) {
            throw new RuntimeException("GitLab URL不能为空");
        }

        // 移除末尾的 .git（如果存在）
        String url = gitlabUrl.trim();
        if (url.endsWith(".git")) {
            url = url.substring(0, url.length() - 4);
        }

        // 正则匹配：http://host/path/to/project
        // 正则说明：
        // - (https?://[^/]+) : 匹配协议和主机名（如 http://9.1.13.159）
        // - /(.+) : 匹配项目路径（如 escm/dcip/versionsync）
        Pattern pattern = Pattern.compile("(https?://[^/]+)/(.+)");
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            String baseUrl = matcher.group(1);
            String projectPath = matcher.group(2);

            logger.debug("解析GitLab URL成功 - 基础URL: {}, 项目路径: {}", baseUrl, projectPath);
            return new GitLabUrlParseResult(baseUrl, projectPath);
        }

        logger.error("无法解析GitLab URL: {}", gitlabUrl);
        throw new RuntimeException("GitLab URL格式不正确，应为 http://host/path/to/project.git 格式");
    }

    /**
     * 解析GitLab URL并获取项目ID
     * 
     * @param gitlabUrl GitLab仓库地址
     * @param privateToken GitLab访问令牌
     * @return 项目ID
     * @throws GitLabApiException GitLab API异常
     */
    public Integer parseGitLabUrlAndGetProjectId(String gitlabUrl, String privateToken) throws GitLabApiException {
        if (!StringUtils.hasText(privateToken)) {
            throw new RuntimeException("访问令牌不能为空");
        }

        // 解析URL
        GitLabUrlParseResult parseResult = parseGitLabUrl(gitlabUrl);

        // 如果已经获取过项目ID，直接返回
        if (parseResult.getProjectId() != null) {
            return parseResult.getProjectId();
        }

        // 通过GitLab API获取项目ID
        try {
            GitLabApi gitLabApi = new GitLabApi(parseResult.getBaseUrl(), privateToken);
            
            // 检查 gitlab4j-api 版本是否兼容
            checkGitLabApiCompatibility(gitLabApi, parseResult.getBaseUrl());
            
            // 获取项目信息
            Project project = gitLabApi.getProjectApi().getProject(parseResult.getProjectPath());
            
            if (project == null) {
                throw new RuntimeException("未找到项目: " + parseResult.getProjectPath());
            }

            // project.getId() 返回 Long 类型，需要转换为 Integer
            Long projectIdLong = project.getId();
            if (projectIdLong == null) {
                throw new RuntimeException("项目ID为空: " + parseResult.getProjectPath());
            }
            Integer projectId = projectIdLong.intValue();
            parseResult.setProjectId(projectId);
            
            logger.info("成功获取项目ID: {} -> {}", parseResult.getProjectPath(), projectId);
            return projectId;
        } catch (GitLabApiException e) {
            logger.error("获取项目ID失败: {}, 错误: {}", parseResult.getProjectPath(), e.getMessage(), e);
            throw new GitLabApiException("获取项目ID失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查 gitlab4j-api 版本是否与 GitLab 服务器兼容
     * @param gitLabApi GitLabApi 实例
     * @param hostUrl GitLab 服务器地址
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
