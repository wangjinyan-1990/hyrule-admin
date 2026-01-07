package com.king.configuration.sysConfigInfo.controller;

import com.king.common.Result;
import com.king.configuration.sysConfigInfo.entity.TfSystemConfiguration;
import com.king.configuration.sysConfigInfo.service.ISysConfigInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 系统配置信息Controller
 */
@RestController
@RequestMapping("/configuration/sysConfigInfo")
public class SysConfigInfoController {
    
    @Autowired
    @Qualifier("sysConfigInfoServiceImpl")
    private ISysConfigInfoService sysConfigInfoService;
    
    /**
     * 获取系统配置信息列表
     * @param pageNo 页码（可选，默认1）
     * @param pageSize 每页大小（可选，默认10）
     * @param systemName 系统名称（可选，模糊查询）
     * @param configurationPeopleNames 配置人员名称（可选，模糊查询）
     * @return 系统配置信息列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getSysConfigInfoList(
            @RequestParam(value = "pageNo", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "systemName", required = false) String systemName,
            @RequestParam(value = "configurationPeopleNames", required = false) String configurationPeopleNames) {
        try {
            Map<String, Object> data = sysConfigInfoService.getSysConfigInfoList(pageNo, pageSize, systemName, configurationPeopleNames);
            return Result.success(data);
        } catch (IllegalArgumentException e) {
            return Result.error(0, e.getMessage());
        } catch (Exception e) {
            return Result.error("获取系统配置信息列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取系统配置信息详情
     * @param configurationId 配置ID
     * @return 系统配置信息详情
     */
    @GetMapping("/{configurationId}")
    public Result<TfSystemConfiguration> getSysConfigInfoDetail(@PathVariable("configurationId") Integer configurationId) {
        try {
            TfSystemConfiguration config = sysConfigInfoService.getSysConfigInfoDetail(configurationId);
            return Result.success(config);
        } catch (IllegalArgumentException e) {
            return Result.error(0, e.getMessage());
        } catch (Exception e) {
            return Result.error("获取系统配置信息详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建系统配置信息
     * @param sysConfigInfo 系统配置信息
     * @return 创建结果
     */
    @PostMapping
    public Result<?> createSysConfigInfo(@RequestBody TfSystemConfiguration sysConfigInfo) {
        try {
            sysConfigInfoService.createSysConfigInfo(sysConfigInfo);
            return Result.success("系统配置信息创建成功");
        } catch (IllegalArgumentException e) {
            return Result.error(0, e.getMessage());
        } catch (Exception e) {
            return Result.error("创建系统配置信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新系统配置信息
     * @param sysConfigInfo 系统配置信息
     * @return 更新结果
     */
    @PutMapping
    public Result<?> updateSysConfigInfo(@RequestBody TfSystemConfiguration sysConfigInfo) {
        try {
            sysConfigInfoService.updateSysConfigInfo(sysConfigInfo);
            return Result.success("系统配置信息更新成功");
        } catch (IllegalArgumentException e) {
            return Result.error(0, e.getMessage());
        } catch (Exception e) {
            return Result.error("更新系统配置信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除系统配置信息
     * @param configurationId 配置ID
     * @return 删除结果
     */
    @DeleteMapping("/{configurationId}")
    public Result<?> deleteSysConfigInfo(@PathVariable("configurationId") Integer configurationId) {
        try {
            sysConfigInfoService.deleteSysConfigInfo(configurationId);
            return Result.success("系统配置信息删除成功");
        } catch (IllegalArgumentException e) {
            return Result.error(0, e.getMessage());
        } catch (Exception e) {
            return Result.error("删除系统配置信息失败: " + e.getMessage());
        }
    }
}

