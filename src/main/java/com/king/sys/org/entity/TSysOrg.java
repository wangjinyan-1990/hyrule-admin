package com.king.sys.org.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

/**
 * SysOrg entity.
 *
 */
@Data
@TableName( "t_sys_org")
public class TSysOrg {

	@TableId(value = "ORG_ID")
	private String orgId; //机构id
	@TableField("ORG_NAME")
	private String orgName; //部门名称
	@TableField("PARENT_ORG_ID")
	private String parentOrgId; //上级部门编号
	@TableField("ORG_LEVEL")
	private int orgLevel; //机构级次
	@TableField("SORT_NO")
	private Integer sortNo; //排序号
	@TableField("ORG_STATUS")
	private String orgStatus;//机构状态  A:有效 B:无效
	@TableField("REMARK")
	private String remark; //备注

	// 用于树形结构的子机构列表
	@TableField(exist = false)
	private List<TSysOrg> children;
	
	// 上级机构名称（用于前端显示）
	@TableField(exist = false)
	private String parentOrgName;

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getParentOrgId() {
		return parentOrgId;
	}

	public void setParentOrgId(String parentOrgId) {
		this.parentOrgId = parentOrgId;
	}

	public int getOrgLevel() {
		return orgLevel;
	}

	public void setOrgLevel(int orgLevel) {
		this.orgLevel = orgLevel;
	}

	public Integer getSortNo() {
		return sortNo;
	}

	public void setSortNo(Integer sortNo) {
		this.sortNo = sortNo;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getOrgStatus() {
		return orgStatus;
	}

	public void setOrgStatus(String orgStatus) {
		this.orgStatus = orgStatus;
	}

	public List<TSysOrg> getChildren() {
		return children;
	}

	public void setChildren(List<TSysOrg> children) {
		this.children = children;
	}

	public String getParentOrgName() {
		return parentOrgName;
	}

	public void setParentOrgName(String parentOrgName) {
		this.parentOrgName = parentOrgName;
	}
}
