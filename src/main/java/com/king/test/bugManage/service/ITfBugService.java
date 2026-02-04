package com.king.test.bugManage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.king.test.bugManage.entity.TfBug;
import com.king.test.bugManage.entity.TfBugState;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 缺陷Service接口
 */
public interface ITfBugService extends IService<TfBug> {

    /**
     * 分页查询缺陷列表
     * @param queryType 查询类型：allBugs-全部缺陷，myActiveBugs-和我相关的活动缺陷
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @param systemId 系统ID（可选）
     * @param directoryId 目录ID（可选）
     * @param bugId 缺陷ID（可选）
     * @param bugName 缺陷名称（可选）
     * @param bugState 缺陷状态（可选）
     * @param bugType 缺陷类型（可选）
     * @param bugSeverityLevel 缺陷严重级别（可选）
     * @param bugSource 缺陷来源（可选）
     * @param submitterId 提交人ID（可选）
     * @param checkerId 验证人ID（可选）
     * @param developerId 开发人员ID（可选）
     * @param commitTimeStart 提交时间开始（可选）
     * @param commitTimeEnd 提交时间结束（可选）
     * @param closeTimeStart 关闭时间开始（可选）
     * @param closeTimeEnd 关闭时间结束（可选）
     * @return 分页结果
     */
    Map<String, Object> getBugPage(String queryType, int pageNo, int pageSize,
                                    String systemId, String directoryId,
                                    String bugId, String bugName,
                                    String bugState, String bugType,
                                    Integer bugSeverityLevel, String bugSource,
                                    String submitterId, String checkerId, String developerId,
                                    String commitTimeStart, String commitTimeEnd,
                                    String closeTimeStart, String closeTimeEnd);

    /**
     * 获取缺陷详情
     * @param bugId 缺陷ID
     * @return 缺陷详情
     */
    TfBug getBugDetail(String bugId);

    /**
     * 创建缺陷
     * @param bug 缺陷对象
     * @return 是否创建成功
     */
    boolean createBug(TfBug bug);

    /**
     * 更新缺陷
     * @param bug 缺陷对象
     * @return 是否更新成功
     */
    boolean updateBug(TfBug bug);

    /**
     * 删除缺陷
     * @param bugId 缺陷ID
     * @return 是否删除成功
     */
    boolean deleteBug(String bugId);

    /**
     * 批量删除缺陷
     * @param bugIds 缺陷ID列表
     * @return 是否删除成功
     */
    boolean batchDeleteBugs(List<String> bugIds);

    /**
     * 查询缺陷列表用于导出
     * @param systemId 系统ID（可选）
     * @param directoryId 目录ID（可选）
     * @param bugId 缺陷ID（可选）
     * @param bugName 缺陷名称（可选）
     * @param bugState 缺陷状态（可选）
     * @param bugType 缺陷类型（可选）
     * @param bugSeverityLevel 缺陷严重级别（可选）
     * @param bugSource 缺陷来源（可选）
     * @param submitterId 提交人ID（可选）
     * @param checkerId 验证人ID（可选）
     * @param developerId 开发人员ID（可选）
     * @param commitTimeStart 提交时间开始（可选）
     * @param commitTimeEnd 提交时间结束（可选）
     * @param closeTimeStart 关闭时间开始（可选）
     * @param closeTimeEnd 关闭时间结束（可选）
     * @return 缺陷列表
     */
    List<TfBug> listBugsForExport(String systemId, String directoryId,
                                   String bugId, String bugName,
                                   String bugState, String bugType,
                                   Integer bugSeverityLevel, String bugSource,
                                   String submitterId, String checkerId, String developerId,
                                   String commitTimeStart, String commitTimeEnd,
                                   String closeTimeStart, String closeTimeEnd);

    /**
     * 导出缺陷列表到Excel
     * @param bugs 缺陷列表
     * @param response HTTP响应对象
     */
    void exportBugsToExcel(List<TfBug> bugs, HttpServletResponse response);

    /**
     * 获取缺陷历史记录
     * @param bugId 缺陷ID
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Map<String, Object> getBugHistory(String bugId, int pageNo, int pageSize);

    /**
     * 分页查询关联的缺陷列表
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @param usecaseId 用例ID（可选）
     * @param directoryId 目录ID（可选）
     * @return 分页结果
     */
    Map<String, Object> getRelatedBugs(int pageNo, int pageSize, String usecaseId, String directoryId);

    /**
     * 获取所有缺陷状态
     * @return 缺陷状态列表（包含 bugStateCode, bugStateName）
     */
    List<TfBugState> getAllBugStates();

    /**
     * 获取下一步可变更的状态
     * @param bugId 缺陷ID
     * @return 可变更的状态列表（根据当前状态和用户角色过滤）
     */
    List<TfBugState> getNextAvailableStates(String bugId);

    /**
     * 根据系统ID获取开发组长列表
     * @param systemId 系统ID
     * @return 开发组长列表
     */
    List<com.king.sys.user.entity.TSysUser> getDevLeadersBySystemId(String systemId);

    /**
     * 根据系统ID获取开发人员列表
     * @param systemId 系统ID
     * @return 开发人员列表
     */
    List<com.king.sys.user.entity.TSysUser> getDevelopersBySystemId(String systemId);
}
