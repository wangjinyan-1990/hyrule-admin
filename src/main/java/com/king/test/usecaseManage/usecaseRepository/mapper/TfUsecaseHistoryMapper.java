package com.king.test.usecaseManage.usecaseRepository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.king.test.usecaseManage.usecaseRepository.entity.TfUsecaseHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TfUsecaseHistoryMapper extends BaseMapper<TfUsecaseHistory> {

    Page<TfUsecaseHistory> selectHistoryPage(Page<TfUsecaseHistory> page,
                                             @Param("usecaseId") String usecaseId);

    List<TfUsecaseHistory> selectHistoryList(@Param("usecaseId") String usecaseId);
}
