package com.king.test.bugManage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.king.test.bugManage.entity.TfBug;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 缺陷Mapper接口
 */
@Mapper
public interface TfBugMapper extends BaseMapper<TfBug> {

    /**
     * 分页查询缺陷列表
     * @param page 分页对象
     * @param systemId 系统ID（可选）
     * @param directoryIds 目录ID列表（可选，包含当前目录及其所有子目录）
     * @param bugId 缺陷ID（可选，模糊查询）
     * @param bugName 缺陷名称（可选，模糊查询）
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
    Page<TfBug> selectPageBugList(Page<TfBug> page,
                                  @Param("systemId") String systemId,
                                  @Param("directoryIds") List<String> directoryIds,
                                  @Param("bugId") String bugId,
                                  @Param("bugName") String bugName,
                                  @Param("bugState") String bugState,
                                  @Param("bugType") String bugType,
                                  @Param("bugSeverityLevel") Integer bugSeverityLevel,
                                  @Param("bugSource") String bugSource,
                                  @Param("submitterId") String submitterId,
                                  @Param("checkerId") String checkerId,
                                  @Param("developerId") String developerId,
                                  @Param("commitTimeStart") String commitTimeStart,
                                  @Param("commitTimeEnd") String commitTimeEnd,
                                  @Param("closeTimeStart") String closeTimeStart,
                                  @Param("closeTimeEnd") String closeTimeEnd);

    /**
     * 根据缺陷ID获取缺陷详情
     * @param bugId 缺陷ID
     * @return 缺陷详情
     */
    TfBug selectBugDetailById(@Param("bugId") String bugId);

    /**
     * 查询缺陷列表用于导出
     * @param systemId 系统ID（可选）
     * @param directoryIds 目录ID列表（可选）
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
    List<TfBug> selectBugsForExport(@Param("systemId") String systemId,
                                    @Param("directoryIds") List<String> directoryIds,
                                    @Param("bugId") String bugId,
                                    @Param("bugName") String bugName,
                                    @Param("bugState") String bugState,
                                    @Param("bugType") String bugType,
                                    @Param("bugSeverityLevel") Integer bugSeverityLevel,
                                    @Param("bugSource") String bugSource,
                                    @Param("submitterId") String submitterId,
                                    @Param("checkerId") String checkerId,
                                    @Param("developerId") String developerId,
                                    @Param("commitTimeStart") String commitTimeStart,
                                    @Param("commitTimeEnd") String commitTimeEnd,
                                    @Param("closeTimeStart") String closeTimeStart,
                                    @Param("closeTimeEnd") String closeTimeEnd);

    /**
     * 分页查询关联的缺陷列表
     * @param page 分页对象
     * @param usecaseId 用例ID（可选）
     * @param directoryId 目录ID（可选，字符串类型，支持UUID格式）
     * @return 分页结果
     */
    Page<TfBug> selectPageRelatedBugs(Page<TfBug> page,
                                       @Param("usecaseId") String usecaseId,
                                       @Param("directoryId") String directoryId);
}
