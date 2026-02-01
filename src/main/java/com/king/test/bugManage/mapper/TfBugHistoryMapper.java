package com.king.test.bugManage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.king.test.bugManage.entity.TfBugHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 缺陷历史记录Mapper接口
 */
@Mapper
public interface TfBugHistoryMapper extends BaseMapper<TfBugHistory> {

    /**
     * 分页查询缺陷历史记录列表
     * @param page 分页对象
     * @param bugId 缺陷ID
     * @return 分页结果
     */
    Page<TfBugHistory> selectPageBugHistoryList(Page<TfBugHistory> page,
                                                @Param("bugId") String bugId);
}
