package com.king.test.usecaseManage.usecaseRequireLink.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.king.test.usecaseManage.requireRepository.entity.TfRequirepoint;
import com.king.test.usecaseManage.usecaseRequireLink.entity.TfUsecaseRequire;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TfUsecaseRequireMapper extends BaseMapper<TfUsecaseRequire> {

    List<TfRequirepoint> selectRequirePointsByUsecaseId(@Param("usecaseId") String usecaseId);

    List<String> selectRequirePointIdsByUsecaseId(@Param("usecaseId") String usecaseId);

    int deleteByUsecaseIdAndRequirePointIds(@Param("usecaseId") String usecaseId,
                                            @Param("requirePointIds") List<String> requirePointIds);
}
