package com.king.framework.attachment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.king.framework.attachment.entity.TfAttachment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 附件Mapper接口
 */
@Mapper
public interface AttachmentMapper extends BaseMapper<TfAttachment> {
    
}
