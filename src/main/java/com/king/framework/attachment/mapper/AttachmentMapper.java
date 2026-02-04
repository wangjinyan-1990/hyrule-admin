package com.king.framework.attachment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.king.framework.attachment.entity.TfAttachment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 附件Mapper接口
 */
@Mapper
public interface AttachmentMapper extends BaseMapper<TfAttachment> {
    
    /**
     * 根据模块和关联ID查询附件列表（关联用户表查询上传用户姓名）
     * @param module 模块名称（可选）
     * @param relateId 关联ID（可选）
     * @return 附件列表
     */
    List<TfAttachment> selectAttachmentsWithUserName(@Param("module") String module, 
                                                      @Param("relateId") String relateId);
}
