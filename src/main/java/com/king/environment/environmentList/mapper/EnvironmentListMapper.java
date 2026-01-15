package com.king.environment.environmentList.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.king.environment.environmentList.entity.TfEnvironment;
import com.king.environment.environmentList.entity.TfEnvironmentList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 环境清单Mapper接口
 * 对应数据库表：tf_environment_list
 */
@Mapper
public interface EnvironmentListMapper extends BaseMapper<TfEnvironmentList> {

    /**
     * 查询环境列表
     * @param testStage 测试阶段（可选，SIT、PAT）
     * @return 环境列表
     */
    List<TfEnvironment> selectEnvironmentList(@Param("testStage") String testStage);

    /**
     * 查询环境清单列表（带关联信息）
     * @param envId 环境Id（可选）
     * @param systemName 系统名称（可选，模糊查询）
     * @param serverName 服务名称（可选，模糊查询）
     * @param ipAddress 主机地址（可选，模糊查询）
     * @return 环境清单列表
     */
    List<TfEnvironmentList> selectEnvironmentListWithJoin(@Param("envId") Integer envId,
                                                           @Param("systemName") String systemName,
                                                           @Param("serverName") String serverName,
                                                           @Param("ipAddress") String ipAddress);

    /**
     * 根据ID查询环境清单详情（带关联信息）
     * @param envListId 环境清单Id
     * @return 环境清单详情
     */
    TfEnvironmentList selectEnvironmentListDetailById(@Param("envListId") Integer envListId);

    /**
     * 根据系统ID、服务名称、主机地址查询环境清单（用于唯一性校验）
     * @param systemId 系统ID
     * @param serverName 服务名称
     * @param ipAddress 主机地址
     * @param excludeEnvListId 排除的环境清单Id（用于更新时排除自身，可为null）
     * @return 环境清单列表
     */
    List<TfEnvironmentList> selectBySystemIdAndServerNameAndIpAddress(@Param("systemId") String systemId,
                                                                       @Param("serverName") String serverName,
                                                                       @Param("ipAddress") String ipAddress,
                                                                       @Param("excludeEnvListId") Integer excludeEnvListId);

    /**
     * 根据系统ID和主机地址查询环境清单（用于导入时的覆盖更新判断）
     * @param systemId 系统ID
     * @param ipAddress 主机地址
     * @return 环境清单列表
     */
    List<TfEnvironmentList> selectBySystemIdAndIpAddress(@Param("systemId") String systemId,
                                                          @Param("ipAddress") String ipAddress);
}

