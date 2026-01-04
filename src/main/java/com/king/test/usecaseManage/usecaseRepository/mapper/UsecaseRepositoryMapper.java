package com.king.test.usecaseManage.usecaseRepository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.king.test.usecaseManage.usecaseRepository.entity.TfUsecase;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UsecaseRepositoryMapper extends BaseMapper<TfUsecase> {

    Page<TfUsecase> selectPageUsecases(Page<TfUsecase> page,
                                       @Param("systemId") String systemId,
                                       @Param("directoryIds") List<String> directoryIds,
                                       @Param("usecaseName") String usecaseName,
                                       @Param("usecaseType") String usecaseType,
                                       @Param("usecaseNature") String usecaseNature,
                                       @Param("prority") String prority,
                                       @Param("isSmokeTest") String isSmokeTest,
                                       @Param("creatorId") String creatorId);

    TfUsecase selectUsecaseDetailById(@Param("usecaseId") String usecaseId);

    List<TfUsecase> selectUsecasesForExport(@Param("systemId") String systemId,
                                            @Param("directoryIds") List<String> directoryIds,
                                            @Param("usecaseName") String usecaseName,
                                            @Param("usecaseType") String usecaseType,
                                            @Param("usecaseNature") String usecaseNature,
                                            @Param("prority") String prority,
                                            @Param("isSmokeTest") String isSmokeTest,
                                            @Param("creatorId") String creatorId);

    List<Map<String, Object>> selectUsecaseStatistics(@Param("systemId") String systemId,
                                                      @Param("directoryIds") List<String> directoryIds);

    List<Map<String, Object>> selectUsecaseTypeStatistics(@Param("systemId") String systemId,
                                                          @Param("directoryIds") List<String> directoryIds);

    List<Map<String, Object>> selectUsecaseStatusStatistics(@Param("systemId") String systemId,
                                                            @Param("directoryIds") List<String> directoryIds);
}
