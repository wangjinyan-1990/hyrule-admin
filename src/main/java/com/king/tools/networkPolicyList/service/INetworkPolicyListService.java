package com.king.tools.networkPolicyList.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 网络策略清单Service接口
 */
public interface INetworkPolicyListService {

    /**
     * 下载导入模板
     * @param response HTTP响应对象
     */
    void downloadTemplate(HttpServletResponse response);

    /**
     * 加工网络策略清单
     * 读取上传的Excel文件，处理后生成新的Excel文件返回
     * @param file 上传的Excel文件
     * @param response HTTP响应对象
     */
    void processNetworkPolicyList(MultipartFile file, HttpServletResponse response);
}
