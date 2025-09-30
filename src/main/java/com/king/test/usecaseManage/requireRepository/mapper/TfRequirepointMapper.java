package com.king.test.usecaseManage.requireRepository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.king.test.usecaseManage.requireRepository.entity.TfRequirepoint;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 需求点Mapper接口
 */
@Mapper
public interface TfRequirepointMapper extends BaseMapper<TfRequirepoint> {

    /**
     * 分页查询需求点列表（关联用户表获取设计人姓名）
     * @param page 分页对象
     * @param systemId 系统ID
     * @param directoryIds 目录ID列表
     * @param requirePointType 需求点类型
     * @param reviewStatus 评审状态
     * @param requireStatus 需求状态
     * @param designer 设计人
     * @return 分页结果
     */
    Page<TfRequirepoint> selectPageRequirepoints(Page<TfRequirepoint> page,
                                               @Param("systemId") String systemId,
                                               @Param("directoryIds") List<String> directoryIds,
                                               @Param("requirePointType") String requirePointType,
                                               @Param("reviewStatus") String reviewStatus,
                                               @Param("requireStatus") String requireStatus,
                                               @Param("designer") String designer);

    /**
     * 查询需求点列表（关联用户表获取设计人姓名）
     * @param systemId 系统ID
     * @param directoryIds 目录ID列表
     * @param requirePointType 需求点类型
     * @param reviewStatus 评审状态
     * @param requireStatus 需求状态
     * @param designer 设计人
     * @return 需求点列表
     */
    List<TfRequirepoint> selectRequirepoints(@Param("systemId") String systemId,
                                               @Param("directoryIds") List<String> directoryIds,
                                               @Param("requirePointType") String requirePointType,
                                               @Param("reviewStatus") String reviewStatus,
                                               @Param("requireStatus") String requireStatus,
                                               @Param("designer") String designer);

    TfRequirepoint selectRequirepointDetailById(@Param("requirePointId") String requirePointId);
}
