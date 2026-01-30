package com.king.test.usecaseManage.usecaseRequireLink.service;

import com.king.test.usecaseManage.usecaseRepository.entity.TfUsecase;

import java.util.List;

/**
 * 用例和需求点关联Service接口
 */
public interface IUsecaseRequireLinkService {

    /**
     * 将用例和需求点关联起来
     * @param usecaseId 用例ID
     * @param requirePointId 需求点ID（需求点编号）
     * @return 关联是否成功
     */
    boolean linkUsecaseToRequirePoint(String usecaseId, String requirePointId);

    /**
     * 批量将用例和需求点关联起来
     * @param usecaseId 用例ID
     * @param requirePointIds 需求点ID列表（需求点编号列表）
     * @return 关联是否成功
     */
    boolean linkUsecaseToRequirePoints(String usecaseId, List<String> requirePointIds);

    /**
     * 根据需求点ID获取关联的测试用例列表
     * @param requirePointId 需求点ID
     * @return 关联的测试用例列表
     */
    List<TfUsecase> getUsecasesByRequirePointId(String requirePointId);

    /**
     * 取消关联测试用例（根据需求点ID和用例ID列表）
     * @param requirePointId 需求点ID
     * @param usecaseIds 测试用例ID列表
     * @return 取消关联是否成功
     */
    boolean unlinkTestCasesFromRequirePoint(String requirePointId, List<String> usecaseIds);

    /**
     * 关联测试用例到需求点（根据需求点ID和用例ID列表）
     * @param requirePointId 需求点ID
     * @param usecaseIds 测试用例ID列表
     * @return 关联是否成功
     */
    boolean linkTestCasesToRequirePoint(String requirePointId, List<String> usecaseIds);
}
