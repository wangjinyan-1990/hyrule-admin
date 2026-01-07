package com.king.configuration.deploy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.king.configuration.deploy.entity.TfDeployRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 发版登记Mapper接口
 * 对应数据库表：tf_deploy_record
 */
@Mapper
public interface DeployRecordMapper extends BaseMapper<TfDeployRecord> {
    
    /**
     * 根据部署ID查询发版登记详情（带关联信息）
     * @param deployId 部署ID
     * @return 发版登记信息
     */
    TfDeployRecord selectDeployRecordDetailById(@Param("deployId") Integer deployId);
    
    /**
     * 分页查询发版登记列表（带系统名称）
     * @param page 分页对象
     * @return 分页结果
     */
    IPage<TfDeployRecord> selectDeployRecordListWithSystemName(Page<TfDeployRecord> page);
}

