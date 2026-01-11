package com.king.environment.environmentList.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.environment.environmentList.mapper.EnvironmentListMapper;
import com.king.environment.environmentList.entity.TfEnvironment;
import com.king.environment.environmentList.entity.TfEnvironmentList;
import com.king.environment.environmentList.service.IEnvironmentListService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;


/**
 * 环境清单Service实现类
 */
@Service("environmentListServiceImpl")
public class EnvironmentListServiceImpl extends ServiceImpl<EnvironmentListMapper, TfEnvironmentList> implements IEnvironmentListService {

    @Resource
    private EnvironmentListMapper environmentListMapper;

    /**
     * 查询环境列表
     * @param testStage 测试阶段（可选，SIT、PAT）
     * @return 环境列表
     */
    @Override
    public List<TfEnvironment> getEnvironmentList(String testStage) {
        return environmentListMapper.selectEnvironmentList(testStage);
    }

    /**
     * 查询环境清单列表（带关联信息）
     * @param envId 环境Id（可选）
     * @param systemId 系统ID（可选）
     * @param serverName 服务名称（可选，模糊查询）
     * @return 环境清单列表
     */
    @Override
    public List<TfEnvironmentList> getEnvironmentListList(Integer envId, String systemId, String serverName) {
        return environmentListMapper.selectEnvironmentListWithJoin(envId, systemId, serverName);
    }

    /**
     * 根据ID查询环境清单详情（带关联信息）
     * @param envListId 环境清单Id
     * @return 环境清单详情
     */
    @Override
    public TfEnvironmentList getEnvironmentListDetail(Integer envListId) {
        Assert.notNull(envListId, "环境清单Id不能为空");
        return environmentListMapper.selectEnvironmentListDetailById(envListId);
    }

    /**
     * 创建环境清单
     * @param environmentList 环境清单信息
     * @return 是否创建成功
     */
    @Override
    public boolean createEnvironmentList(TfEnvironmentList environmentList) {
        Assert.notNull(environmentList, "环境清单信息不能为空");
        Assert.notNull(environmentList.getEnvId(), "环境Id不能为空");
        Assert.notNull(environmentList.getSystemId(), "系统ID不能为空");
        Assert.hasText(environmentList.getServerName(), "服务名称不能为空");
        Assert.hasText(environmentList.getIpAddress(), "主机地址不能为空");
        
        // 唯一性校验：系统ID、服务名称、主机地址的组合不能重复
        List<TfEnvironmentList> existingList = environmentListMapper.selectBySystemIdAndServerNameAndIpAddress(
                environmentList.getSystemId(),
                environmentList.getServerName(),
                environmentList.getIpAddress(),
                null  // 创建时不需要排除任何记录
        );
        Assert.isTrue(existingList == null || existingList.isEmpty(),
                "该环境清单已存在：系统ID=" + environmentList.getSystemId() + 
                ", 服务名称=" + environmentList.getServerName() + 
                ", 主机地址=" + environmentList.getIpAddress());
        
        return this.save(environmentList);
    }

    /**
     * 更新环境清单
     * @param environmentList 环境清单信息
     * @return 是否更新成功
     */
    @Override
    public boolean updateEnvironmentList(TfEnvironmentList environmentList) {
        Assert.notNull(environmentList, "环境清单信息不能为空");
        Assert.notNull(environmentList.getEnvListId(), "环境清单Id不能为空");
        Assert.notNull(environmentList.getEnvId(), "环境Id不能为空");
        Assert.notNull(environmentList.getSystemId(), "系统ID不能为空");
        Assert.hasText(environmentList.getServerName(), "服务名称不能为空");
        Assert.hasText(environmentList.getIpAddress(), "主机地址不能为空");
        
        // 唯一性校验：系统ID、服务名称、主机地址的组合不能重复（排除当前记录）
        List<TfEnvironmentList> existingList = environmentListMapper.selectBySystemIdAndServerNameAndIpAddress(
                environmentList.getSystemId(),
                environmentList.getServerName(),
                environmentList.getIpAddress(),
                environmentList.getEnvListId()  // 更新时需要排除当前记录
        );
        Assert.isTrue(existingList == null || existingList.isEmpty(),
                "该环境清单已存在：系统ID=" + environmentList.getSystemId() + 
                ", 服务名称=" + environmentList.getServerName() + 
                ", 主机地址=" + environmentList.getIpAddress());
        
        return this.updateById(environmentList);
    }

    /**
     * 删除环境清单
     * @param envListId 环境清单Id
     * @return 是否删除成功
     */
    @Override
    public boolean deleteEnvironmentList(Integer envListId) {
        Assert.notNull(envListId, "环境清单Id不能为空");
        return this.removeById(envListId);
    }
}

