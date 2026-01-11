package com.king.configuration.deploy.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * 接受Merge Request合并的服务类
 */
@Service
public class AcceptMergeRequestService {

    private static final Logger logger = LoggerFactory.getLogger(AcceptMergeRequestService.class);

    private final OkHttpClient httpClient;

    public AcceptMergeRequestService() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 接受 Merge Request（调用 GitLab API 的 /merge 接口）
     * @param gitLabUrl GitLab服务器地址
     * @param projectPath 项目路径（格式：group/project）
     * @param mrIid Merge Request ID
     * @param token 访问令牌
     * @return 合并后的MR状态
     */
    public String acceptMergeRequest(String gitLabUrl, String projectPath, String mrIid, String token) {
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
            
            // 构建API URL: PUT /projects/:id/merge_requests/:merge_request_iid/merge
            String apiUrl = gitLabUrl + "/api/v4/projects/" + encodedPath.toString() + "/merge_requests/" + mrIid + "/merge";
            logger.info("接受Merge Request合并的GitLab API URL: {}", apiUrl);
            
            // 构建请求体（可以传递merge_commit_message等参数，这里使用空对象）
            String requestBody = "{}";
            
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .header("PRIVATE-TOKEN", token)
                    .header("Content-Type", "application/json")
                    .put(okhttp3.RequestBody.create(requestBody, okhttp3.MediaType.parse("application/json")))
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                String responseBody = response.body() != null ? response.body().string() : "";
                
                if (!response.isSuccessful()) {
                    logger.error("GitLab API接受Merge Request失败，HTTP状态码: {}, URL: {}, 响应: {}", 
                            response.code(), apiUrl, responseBody);
                    throw new RuntimeException("GitLab API接受Merge Request失败，HTTP状态码: " + response.code() + ", 响应: " + responseBody);
                }
                
                logger.info("GitLab API接受Merge Request请求成功: {}, 响应: {}", apiUrl, responseBody);
                
                // 解析响应，获取合并后的状态
                try {
                    JSONObject mrResponse = JSON.parseObject(responseBody);
                    String state = mrResponse.getString("state");
                    logger.info("Merge Request合并后状态: {}", state);
                    
                    // 如果状态不是merged，等待一下再检查（GitLab可能需要时间处理）
                    if (!"merged".equals(state)) {
                        logger.warn("合并后状态不是merged，当前状态: {}，等待2秒后重新检查", state);
                        try {
                            Thread.sleep(2000); // 等待2秒
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            logger.warn("等待合并状态时被中断");
                        }
                        
                        // 重新查询MR状态
                        JSONObject mrData = getMergeRequestFromGitLab(gitLabUrl, projectPath, mrIid, token);
                        if (mrData != null) {
                            state = mrData.getString("state");
                            logger.info("重新查询后的Merge Request状态: {}", state);
                        }
                    }
                    
                    return state != null ? state : "unknown";
                } catch (Exception e) {
                    logger.warn("解析合并响应失败: {}, 响应内容: {}", e.getMessage(), responseBody);
                    // 即使解析失败，也尝试重新查询状态
                    try {
                        Thread.sleep(2000); // 等待2秒
                        JSONObject mrData = getMergeRequestFromGitLab(gitLabUrl, projectPath, mrIid, token);
                        if (mrData != null) {
                            String state = mrData.getString("state");
                            logger.info("重新查询后的Merge Request状态: {}", state);
                            return state != null ? state : "unknown";
                        }
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        logger.warn("等待合并状态时被中断");
                    } catch (Exception ex) {
                        logger.error("重新查询MR状态失败: {}", ex.getMessage());
                    }
                    return "unknown";
                }
            }
        } catch (RuntimeException e) {
            // 如果是 RuntimeException，直接抛出
            throw e;
        } catch (Exception e) {
            logger.error("调用GitLab API接受Merge Request失败: {}", e.getMessage(), e);
            throw new RuntimeException("调用GitLab API接受Merge Request失败: " + e.getMessage(), e);
        }
    }

    /**
     * 从GitLab获取Merge Request信息
     * @param gitLabUrl GitLab服务器地址
     * @param projectPath 项目路径（格式：group/project）
     * @param mrIid Merge Request ID
     * @param token 访问令牌
     * @return Merge Request信息
     */
    public JSONObject getMergeRequestFromGitLab(String gitLabUrl, String projectPath, String mrIid, String token) {
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
            logger.info("GitLab API URL: {}", apiUrl);

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
}

