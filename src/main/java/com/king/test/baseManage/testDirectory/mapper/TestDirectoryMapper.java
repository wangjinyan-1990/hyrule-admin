package com.king.test.baseManage.testDirectory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.king.test.baseManage.testDirectory.entity.TTestDirectory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 测试系统Mapper接口
 */
@Mapper
public interface TestDirectoryMapper extends BaseMapper<TTestDirectory> {
    
    /**
     * 查询用户参与的测试系统,目录树展示系统根目录
     * @param userId 用户ID
     * @return
     */
    List<TTestDirectory> getRootDirectoryByUserId( @Param("userId") String userId);
    
    /**
     * 查询所有根目录（系统）,admin用户目录树展示所有系统根目录
     * @return 根目录列表
     */
    List<TTestDirectory> getRootDirectory();
    
    /**
     * 根据父目录ID和系统ID查询子目录
     * @param directoryParentId 父目录ID（可为空）
     * @param systemId 系统ID
     * @return 子目录列表
     */
    List<TTestDirectory> getChildrenByParentId(@Param("directoryParentId") String directoryParentId, 
                                             @Param("systemId") String systemId);
    
    /**
     * 检查目录名称是否存在
     * @param directoryName 目录名称
     * @param directoryParentId 父目录ID
     * @param systemId 系统ID
     * @param excludeDirectoryId 排除的目录ID（更新时使用）
     * @return 存在的目录列表
     */
    List<TTestDirectory> checkDirectoryNameExists(@Param("directoryName") String directoryName,
                                                @Param("directoryParentId") String directoryParentId,
                                                @Param("systemId") String systemId,
                                                @Param("excludeDirectoryId") String excludeDirectoryId);
}
