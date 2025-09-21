package com.king.test.usecaseManage.requireRepository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.king.test.usecaseManage.requireRepository.entity.TfRequirepoint;

import java.util.List;
import java.util.Map;

/**
 * 需求点Service接口
 */
public interface ITfRequirepointService extends IService<TfRequirepoint> {

    /**
     * 分页查询需求点列表
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @param systemId 系统ID（可选）
     * @param directoryId 目录ID（可选）
     * @param requirePointType 需求点类型（可选）
     * @param reviewStatus 评审状态（可选）
     * @param requireStatus 需求状态（可选）
     * @param designerId 设计人ID（可选）
     * @return 分页结果
     */
    Map<String, Object> getRequirepointsWithPagination(int pageNo, int pageSize, 
                                                      String systemId, String directoryId, 
                                                      String requirePointType, String reviewStatus, 
                                                      String requireStatus, String designerId);

    /**
     * 创建需求点
     * @param requirepoint 需求点信息
     * @return 创建结果
     */
    boolean createRequirepoint(TfRequirepoint requirepoint);

    /**
     * 更新需求点
     * @param requirepoint 需求点信息
     * @return 更新结果
     */
    boolean updateRequirepoint(TfRequirepoint requirepoint);

    /**
     * 删除需求点
     * @param requirePointId 需求点ID
     * @return 删除结果
     */
    boolean deleteRequirepoint(String requirePointId);

    /**
     * 批量删除需求点
     * @param requirePointIds 需求点ID列表
     * @return 删除结果
     */
    boolean batchDeleteRequirepoints(List<String> requirePointIds);

    /**
     * 批量评审需求点
     * @param requirePointIds 需求点ID列表
     * @param reviewStatus 评审状态
     * @param reviewComment 评审意见
     * @return 评审结果
     */
    boolean batchReviewRequirepoints(List<String> requirePointIds, String reviewStatus, String reviewComment);

    /**
     * 导出需求点数据
     * @param systemId 系统ID（可选）
     * @param directoryId 目录ID（可选）
     * @param requirePointType 需求点类型（可选）
     * @param reviewStatus 评审状态（可选）
     * @param requireStatus 需求状态（可选）
     * @param designerId 设计人ID（可选）
     * @return 需求点列表
     */
    List<TfRequirepoint> exportRequirepoints(String systemId, String directoryId, 
                                           String requirePointType, String reviewStatus, 
                                           String requireStatus, String designerId);

    /**
     * 获取目录及其所有子目录的ID列表
     * @param directoryId 目录ID
     * @return 目录ID列表（包含自身和所有子目录）
     */
    List<String> getDirectoryAndChildrenIds(String directoryId);
}
