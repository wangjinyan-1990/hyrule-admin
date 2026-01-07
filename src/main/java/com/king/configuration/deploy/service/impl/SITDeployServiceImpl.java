package com.king.configuration.deploy.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.king.configuration.deploy.dto.MergeRequestInfo;
import com.king.configuration.deploy.service.ISITDeployService;
import com.king.configuration.sysConfigInfo.entity.TfSystemConfiguration;
import com.king.configuration.sysConfigInfo.mapper.SysConfigInfoMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SIT部署Service实现类
 */
@Service("sitDeployServiceImpl")
public class SITDeployServiceImpl implements ISITDeployService {

    private static final Logger logger = LoggerFactory.getLogger(SITDeployServiceImpl.class);

    @Resource
    private SysConfigInfoMapper sysConfigInfoMapper;

    private final OkHttpClient httpClient;

    public SITDeployServiceImpl() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

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
            MergeRequestInfo mrInfo = parseMergeRequestUrl(mergeRequest);
            if (mrInfo == null) {
                throw new IllegalArgumentException("Merge Request URL格式不正确");
            }

            // 3. 调用GitLab API获取MR信息
            JSONObject mrData = getMergeRequestFromGitLab(mrInfo.getGitLabUrl(), mrInfo.getProjectPath(),
                    mrInfo.getMrIid(), privateToken);

            if (mrData == null) {
                throw new RuntimeException("获取Merge Request信息失败");
            }

            // 4. 提取标题
            String title = mrData.getString("title");
            result.put("sendTestCode", title != null ? title : "");

            // 5. 提取合并状态
            String state = mrData.getString("state");
            result.put("mergeState", state != null ? state : "");

            // 6. 获取代码清单（commits）
            String codeList = getCodeList(mrInfo.getGitLabUrl(), mrInfo.getProjectPath(),
                    mrInfo.getMrIid(), privateToken);
            result.put("codeList", codeList);

            logger.info("成功解析Merge Request: {}, 系统ID: {}", mergeRequest, systemId);

        } catch (Exception e) {
            logger.error("解析Merge Request失败: {}", e.getMessage(), e);
            throw new RuntimeException("解析Merge Request失败: " + e.getMessage(), e);
        }

        return result;
    }

    /**
     * 解析Merge Request URL
     * 支持格式：
     * http://9.6.232.212/dtp/MSVFW_OUTBD_MODUL/merge_requests/558
     * http://9.1.13.159/ncp/ncp/-/merge_requests/3
     * http://9.1.13.159/CUPS/cups/-/merge_requests/1
     */
    private MergeRequestInfo parseMergeRequestUrl(String url) {
        if (!StringUtils.hasText(url)) {
            return null;
        }

        // 匹配URL格式: 
        // http://host/group/project/merge_requests/id 
        // http://host/group/project/-/merge_requests/id
        // 支持 http:// 和 https:// 开头
        // 正则说明：
        // - (https?://[^/]+) : 匹配协议和主机名
        // - /(.+?)/ : 匹配项目路径（非贪婪匹配，直到遇到下一个斜杠）
        // - (?:-/)? : 可选匹配 -/ 分隔符
        // - merge_requests/(\d+) : 匹配 merge_requests/ 和数字ID
        Pattern pattern = Pattern.compile("(https?://[^/]+)/(.+?)/(?:-/)?merge_requests/(\\d+)");
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            String gitLabUrl = matcher.group(1);
            String projectPath = matcher.group(2);
            String mrIid = matcher.group(3);

            logger.debug("解析Merge Request URL成功 - GitLab URL: {}, 项目路径: {}, MR ID: {}", 
                    gitLabUrl, projectPath, mrIid);
            return new MergeRequestInfo(gitLabUrl, projectPath, mrIid);
        }

        logger.warn("无法解析Merge Request URL: {}", url);
        return null;
    }

    /**
     * 从GitLab获取Merge Request信息
     */
    private JSONObject getMergeRequestFromGitLab(String gitLabUrl, String projectPath, String mrIid, String token) {
        try {
            // GitLab API要求项目路径使用斜杠分隔，需要对每个部分进行URL编码
            // 例如: group/project -> group%2Fproject
            String[] pathParts = projectPath.split("/");
            StringBuilder encodedPath = new StringBuilder();
            for (int i = 0; i < pathParts.length; i++) {
                if (i > 0) {
                    encodedPath.append("%2F");
                }
                encodedPath.append(URLEncoder.encode(pathParts[i], StandardCharsets.UTF_8.toString())
                        .replace("+", "%20"));
            }

            // 构建API URL
            String apiUrl = gitLabUrl + "/api/v4/projects/" + encodedPath.toString() + "/merge_requests/" + mrIid;

            Request request = new Request.Builder()
                    .url(apiUrl)
                    .header("PRIVATE-TOKEN", token)
                    .get()
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    logger.error("GitLab API调用失败，HTTP状态码: {}, URL: {}", response.code(), apiUrl);
                    throw new RuntimeException("GitLab API调用失败，HTTP状态码: " + response.code());
                }

                String responseBody = response.body().string();
                return JSON.parseObject(responseBody);
            }
        } catch (Exception e) {
            logger.error("调用GitLab API获取Merge Request失败: {}", e.getMessage(), e);
            throw new RuntimeException("调用GitLab API失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取代码清单（变更文件列表）
     */
    private String getCodeList(String gitLabUrl, String projectPath, String mrIid, String token) {
        try {
            // GitLab API要求项目路径使用斜杠分隔，需要对每个部分进行URL编码
            String[] pathParts = projectPath.split("/");
            StringBuilder encodedPath = new StringBuilder();
            for (int i = 0; i < pathParts.length; i++) {
                if (i > 0) {
                    encodedPath.append("%2F");
                }
                encodedPath.append(URLEncoder.encode(pathParts[i], StandardCharsets.UTF_8.toString())
                        .replace("+", "%20"));
            }

            // 构建API URL获取changes（变更文件列表）
            String apiUrl = gitLabUrl + "/api/v4/projects/" + encodedPath.toString() + "/merge_requests/" + mrIid + "/changes";

            Request request = new Request.Builder()
                    .url(apiUrl)
                    .header("PRIVATE-TOKEN", token)
                    .get()
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "";
                    logger.error("GitLab API调用失败，HTTP状态码: {}, URL: {}, 响应: {}", 
                            response.code(), apiUrl, errorBody);
                    return "";
                }

                String responseBody = response.body().string();
                logger.info("GitLab API响应长度: {}, 前100个字符: {}", 
                        responseBody != null ? responseBody.length() : 0,
                        responseBody != null && responseBody.length() > 100 ? 
                                responseBody.substring(0, 100) : responseBody);

                // 检查响应是否为空
                if (responseBody == null || responseBody.trim().isEmpty()) {
                    logger.warn("GitLab API返回空响应");
                    return "";
                }

                // 解析为JSON对象（changes API返回的是对象，包含changes数组）
                JSONObject responseObj;
                try {
                    responseObj = JSON.parseObject(responseBody);
                } catch (Exception e) {
                    logger.error("解析GitLab API响应为JSON对象失败: {}, 响应内容: {}", 
                            e.getMessage(), responseBody);
                    return "";
                }
                
                // 获取changes数组
                JSONArray changes = responseObj.getJSONArray("changes");
                if (changes == null || changes.isEmpty()) {
                    logger.warn("GitLab API返回的changes数组为空");
                    return "";
                }

                logger.debug("获取到 {} 个变更文件", changes.size());

                // 构建代码清单字符串（文件路径列表）
                StringBuilder codeList = new StringBuilder();
                for (int i = 0; i < changes.size(); i++) {
                    try {
                        JSONObject change = changes.getJSONObject(i);
                        if (change == null) {
                            logger.warn("第 {} 个change对象为null", i);
                            continue;
                        }

                        // 优先取new_path，如果为空则取old_path
                        String filePath = change.getString("new_path");
                        if (filePath == null || filePath.trim().isEmpty()) {
                            filePath = change.getString("old_path");
                        }

                        // 如果文件路径仍然为空，跳过
                        if (filePath == null || filePath.trim().isEmpty()) {
                            logger.warn("第 {} 个change对象的new_path和old_path都为空", i);
                            continue;
                        }

                        // 每个文件路径换行
                        if (codeList.length() > 0) {
                            codeList.append("\n");
                        }
                        codeList.append(filePath);
                    } catch (Exception e) {
                        logger.error("处理第 {} 个change时出错: {}", i, e.getMessage());
                        continue;
                    }
                }

                String result = codeList.toString();
                logger.debug("生成的代码清单: {}", result);
                return result;
            }
        } catch (Exception e) {
            logger.error("获取代码清单失败: {}", e.getMessage(), e);
            return "";
        }
    }

}
