package com.king.environment.environmentList.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.king.environment.environmentList.entity.TfEnvironment;
import com.king.environment.environmentList.entity.TfEnvironmentList;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 环境清单Service接口
 */
public interface IEnvironmentListService extends IService<TfEnvironmentList> {

    /**
     * 查询环境列表
     * @param testStage 测试阶段（可选，SIT、PAT）
     * @return 环境列表
     */
    List<TfEnvironment> getEnvironmentList(String testStage);

    /**
     * 查询环境清单列表（带关联信息）
     * @param envId 环境Id（可选）
     * @param systemName 系统名称（可选，模糊查询）
     * @param serverName 服务名称（可选，模糊查询）
     * @param ipAddress 主机地址（可选，模糊查询）
     * @return 环境清单列表
     */
    List<TfEnvironmentList> getEnvironmentListList(Integer envId, String systemName, String serverName, String ipAddress);

    /**
     * 根据ID查询环境清单详情（带关联信息）
     * @param envListId 环境清单Id
     * @return 环境清单详情
     */
    TfEnvironmentList getEnvironmentListDetail(Integer envListId);

    /**
     * 创建环境清单
     * @param environmentList 环境清单信息
     * @return 是否创建成功
     */
    boolean createEnvironmentList(TfEnvironmentList environmentList);

    /**
     * 更新环境清单
     * @param environmentList 环境清单信息
     * @return 是否更新成功
     */
    boolean updateEnvironmentList(TfEnvironmentList environmentList);

    /**
     * 删除环境清单
     * @param envListId 环境清单Id
     * @return 是否删除成功
     */
    boolean deleteEnvironmentList(Integer envListId);

    /**
     * 导出环境清单数据到Excel
     * @param environmentLists 环境清单列表
     * @param response HTTP响应对象
     */
    void exportEnvironmentListToExcel(List<TfEnvironmentList> environmentLists, HttpServletResponse response);

    /**
     * 导入环境清单数据
     * @param file Excel文件
     * @return 导入结果
     */
    Map<String, Object> importEnvironmentList(MultipartFile file) throws Exception;

    /**
     * 下载导入模板
     * @param response HTTP响应对象
     */
    void downloadTemplate(HttpServletResponse response);
}

