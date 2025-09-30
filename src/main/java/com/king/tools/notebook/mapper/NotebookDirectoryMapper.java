package com.king.tools.notebook.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.king.tools.notebook.entity.TfNotebookDirectory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 记事本目录Mapper接口
 */
@Mapper
public interface NotebookDirectoryMapper extends BaseMapper<TfNotebookDirectory> {
}