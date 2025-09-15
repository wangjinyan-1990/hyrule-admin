package com.king.test.baseManage.testSystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.king.test.baseManage.testSystem.entity.TTestSystemUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 测试系统用户Mapper接口
 */
@Mapper
public interface TestSystemUserMapper extends BaseMapper<TTestSystemUser> {
}
