package com.king.test.bugManage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.king.test.bugManage.entity.TfBugState;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 缺陷状态Mapper接口
 */
@Mapper
public interface TfBugStateMapper extends BaseMapper<TfBugState> {

    /**
     * 查询所有缺陷状态
     * @return 缺陷状态列表
     */
    List<TfBugState> selectAllBugStates();
}

