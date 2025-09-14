package com.king.sys.org.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.king.common.Result;
import com.king.sys.org.entity.TSysOrg;
import com.king.sys.org.service.IOrgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/org")
public class OrgController {

    @Autowired
    @Qualifier("orgService")
    private IOrgService orgService;

    /**
     * 获取机构树形列表
     */
    @GetMapping("/tree")
    public Result getOrgTree() {
        try {
            List<TSysOrg> orgTree = orgService.getOrgTree();
            return Result.success(orgTree);
        } catch (Exception e) {
            return Result.error("获取机构树失败: " + e.getMessage());
        }
    }

    /**
     * 获取机构列表（扁平）
     */
    @GetMapping("/list")
    public Result getOrgList(@RequestParam(defaultValue = "1") Integer pageNo,
                            @RequestParam(defaultValue = "10") Integer pageSize,
                            @RequestParam(required = false) String orgName,
                            @RequestParam(required = false) String orgStatus) {
        try {
            Page<TSysOrg> page = new Page<>(pageNo, pageSize);
            IPage<TSysOrg> result = orgService.getOrgList(page, orgName, orgStatus);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取机构列表失败: " + e.getMessage());
        }
    }

    /**
     * 创建机构
     */
    @PostMapping("/create")
    public Result createOrg(@RequestBody TSysOrg org) {
        try {
            if (org == null) {
                return Result.error("机构信息不能为空");
            }
            if (!StringUtils.hasText(org.getOrgName())) {
                return Result.error("机构名称不能为空");
            }
            // 机构ID由系统自动生成，不需要前端传入

            boolean success = orgService.createOrg(org);
            return success ? Result.success("创建成功") : Result.error("创建失败");
        } catch (Exception e) {
            return Result.error("创建机构失败: " + e.getMessage());
        }
    }

    /**
     * 更新机构
     */
    @PutMapping("/update")
    public Result updateOrg(@RequestBody TSysOrg org) {
        try {
            if (org == null || !StringUtils.hasText(org.getOrgId())) {
                return Result.error("机构ID不能为空");
            }
            if (!StringUtils.hasText(org.getOrgName())) {
                return Result.error("机构名称不能为空");
            }

            boolean success = orgService.updateOrg(org);
            return success ? Result.success("更新成功") : Result.error("更新失败");
        } catch (Exception e) {
            return Result.error("更新机构失败: " + e.getMessage());
        }
    }

    /**
     * 删除机构
     */
    @DeleteMapping("/delete")
    public Result deleteOrg(@RequestParam("orgId") String orgId) {
        try {
            if (!StringUtils.hasText(orgId)) {
                return Result.error("机构ID不能为空");
            }

            boolean success = orgService.deleteOrg(orgId);
            return success ? Result.success("删除成功") : Result.error("删除失败");
        } catch (Exception e) {
            return Result.error("删除机构失败: " + e.getMessage());
        }
    }

    /**
     * 获取机构详情
     */
    @GetMapping("/detail")
    public Result getOrgDetail(@RequestParam("orgId") String orgId) {
        try {
            if (!StringUtils.hasText(orgId)) {
                return Result.error("机构ID不能为空");
            }

            TSysOrg org = orgService.getOrgDetail(orgId);
            if (org == null) {
                return Result.error("机构不存在");
            }
            return Result.success(org);
        } catch (Exception e) {
            return Result.error("获取机构详情失败: " + e.getMessage());
        }
    }
}
