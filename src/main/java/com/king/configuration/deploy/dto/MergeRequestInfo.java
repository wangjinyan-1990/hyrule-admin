package com.king.configuration.deploy.dto;

/**
 * Merge Request信息DTO
 * 用于存储从URL解析出的GitLab Merge Request信息
 */
public class MergeRequestInfo {
    
    /**
     * GitLab服务器地址
     */
    private final String gitLabUrl;
    
    /**
     * 项目路径（格式：group/project）
     */
    private final String projectPath;
    
    /**
     * Merge Request ID
     */
    private final String mrIid;

    /**
     * 构造函数
     * @param gitLabUrl GitLab服务器地址
     * @param projectPath 项目路径
     * @param mrIid Merge Request ID
     */
    public MergeRequestInfo(String gitLabUrl, String projectPath, String mrIid) {
        this.gitLabUrl = gitLabUrl;
        this.projectPath = projectPath;
        this.mrIid = mrIid;
    }

    /**
     * 获取GitLab服务器地址
     * @return GitLab服务器地址
     */
    public String getGitLabUrl() {
        return gitLabUrl;
    }

    /**
     * 获取项目路径
     * @return 项目路径
     */
    public String getProjectPath() {
        return projectPath;
    }

    /**
     * 获取Merge Request ID
     * @return Merge Request ID
     */
    public String getMrIid() {
        return mrIid;
    }
}
