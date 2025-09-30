package com.king.tools.notebook.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.king.tools.notebook.entity.TfNotebook;
import org.apache.ibatis.annotations.Mapper;

/**
 * 记事本Mapper接口
 * 对应数据库表：tf_notebook
 */
@Mapper
public interface NotebookMapper extends BaseMapper<TfNotebook> {
}
