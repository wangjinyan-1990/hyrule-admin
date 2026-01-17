package com.king.tools.networkPolicyList.controller;

import com.king.tools.networkPolicyList.service.INetworkPolicyListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 网络策略清单Controller
 */
@RestController
@RequestMapping("/tools/networkPolicyList")
public class NetworkPolicyListController {

    @Autowired
    @Qualifier("networkPolicyListServiceImpl")
    private INetworkPolicyListService networkPolicyListService;

    /**
     * 下载导入模板
     * @param response HTTP响应对象
     */
    @GetMapping("/downloadTemplate")
    public void downloadTemplate(HttpServletResponse response) {
        try {
            networkPolicyListService.downloadTemplate(response);
        } catch (Exception e) {
            try {
                response.reset();
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\":\"下载模板失败：" + e.getMessage() + "\"}");
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 加工网络策略清单
     * 接收Excel文件，处理后生成新的Excel文件返回
     * @param file 上传的Excel文件
     * @param response HTTP响应对象
     */
    @PostMapping("/process")
    public void processNetworkPolicyList(
            @RequestParam("file") MultipartFile file,
            HttpServletResponse response) {
        try {
            networkPolicyListService.processNetworkPolicyList(file, response);
        } catch (Exception e) {
            try {
                response.reset();
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\":\"加工网络策略清单失败：" + e.getMessage() + "\"}");
            } catch (Exception ignored) {
            }
        }
    }
}
