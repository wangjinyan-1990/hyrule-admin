package com.king.configuration.deploy.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.configuration.deploy.dto.MergeRequestInfo;
import com.king.configuration.deploy.entity.TfDeployRecord;
import com.king.configuration.deploy.mapper.DeployRecordMapper;
import com.king.configuration.deploy.service.AcceptMergeRequestService;
import com.king.configuration.deploy.service.ISITDeployService;
import com.king.common.utils.DateUtil;
import com.king.configuration.sysConfigInfo.entity.TfSystemConfiguration;
import com.king.configuration.sysConfigInfo.mapper.SysConfigInfoMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
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
public class SITDeployServiceImpl extends ServiceImpl<DeployRecordMapper, TfDeployRecord> implements ISITDeployService {

    private static final Logger logger = LoggerFactory.getLogger(SITDeployServiceImpl.class);

    @Resource
    private SysConfigInfoMapper sysConfigInfoMapper;

    @Resource
    private AcceptMergeRequestService acceptMergeRequestService;

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
        
        // 如果mergeRequest为"1"，只进行登记，不走合并流程
        if ("1".equals(mergeRequest)) {
            logger.info("mergeRequest 为 '1'，跳过代码合并，只进行登记");
        } else if (StringUtils.hasText(mergeRequest)) {
            // 如果mergeState为null或不是'merged'，则需要合并
            boolean needMerge = mergeState == null || !"merged".equals(mergeState);
            
            if (needMerge) {
                logger.info("开始调用 GitLab /merge 接口进行合并，当前状态: {}, URL: {}", 
                        mergeState != null ? mergeState : "null", mergeRequest);
                
                try {
                    // 解析 Merge Request URL
                    MergeRequestInfo mrInfo = parseMergeRequestUrl(mergeRequest);
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
        
        // 获取当前日期（YYYYMMDD格式）
        String currentDate = DateUtil.getDateFormatYMD();
        
        // 查询当天同一系统同一测试阶段的最后一条记录（按部署时间倒序）
        TfDeployRecord lastRecord = this.baseMapper.selectLastDeployRecordBySystemAndStageAndDate(
                deployRecord.getSystemId(), "SIT", currentDate);
        
        // 获取本次发版登记的版本登记数量，如果为空则默认为1
        Integer currentRecordNum = deployRecord.getRecordNum();
        if (currentRecordNum == null || currentRecordNum < 1) {
            currentRecordNum = 1;
        }
        
        // 计算最终的版本登记数量
        Integer finalRecordNum;
        if (lastRecord != null && StringUtils.hasText(lastRecord.getVersionCode())) {
            // 从最后一条记录的 versionCode 中提取 recordNum
            // versionCode 格式：sysAbbreviation-SIT-YYYYMMDD-recordNum
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
        // 格式：sysAbbreviation-SIT-YYYYMMDD-recordNum
        String versionCode = String.format("%s-SIT-%s-%d", sysAbbreviation, currentDate, finalRecordNum);
        deployRecord.setVersionCode(versionCode);
        // 更新 recordNum 为最终计算的值
        deployRecord.setRecordNum(currentRecordNum);
        
        // 合并成功后，保存发版登记信息
        this.save(deployRecord);
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
