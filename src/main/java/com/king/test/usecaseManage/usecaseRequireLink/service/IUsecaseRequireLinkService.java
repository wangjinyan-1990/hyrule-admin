package com.king.test.usecaseManage.usecaseRequireLink.service;

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
}
