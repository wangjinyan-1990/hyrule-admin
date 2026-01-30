package com.king.test.usecaseManage.usecaseRequireLink.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.king.test.usecaseManage.requireRepository.entity.TfRequirepoint;
import com.king.test.usecaseManage.usecaseRepository.entity.TfUsecase;
import com.king.test.usecaseManage.usecaseRequireLink.entity.TfUsecaseRequire;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TfUsecaseRequireLinkMapper extends BaseMapper<TfUsecaseRequire> {

    List<TfRequirepoint> selectRequirePointsByUsecaseId(@Param("usecaseId") String usecaseId);

    List<String> selectRequirePointIdsByUsecaseId(@Param("usecaseId") String usecaseId);

    int deleteByUsecaseIdAndRequirePointIds(@Param("usecaseId") String usecaseId,
                                            @Param("requirePointIds") List<String> requirePointIds);

    /**
     * 根据需求点ID查询关联的测试用例列表
     * @param requirePointId 需求点ID
     * @return 测试用例列表
     */
    List<TfUsecase> selectUsecasesByRequirePointId(@Param("requirePointId") String requirePointId);

    /**
     * 根据需求点ID和用例ID列表删除关联关系
     * @param requirePointId 需求点ID
     * @param usecaseIds 用例ID列表
     * @return 删除的记录数
     */
    int deleteByRequirePointIdAndUsecaseIds(@Param("requirePointId") String requirePointId,
                                           @Param("usecaseIds") List<String> usecaseIds);

    /**
     * 根据需求点ID查询已关联的用例ID列表
     * @param requirePointId 需求点ID
     * @return 用例ID列表
     */
    List<String> selectUsecaseIdsByRequirePointId(@Param("requirePointId") String requirePointId);
}
