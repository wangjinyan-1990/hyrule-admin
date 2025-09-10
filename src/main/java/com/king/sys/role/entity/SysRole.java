package com.king.sys.role.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * SysRole entity.
 */
@Data
@TableName("t_sys_role")
public class SysRole implements java.io.Serializable {

	@TableId(value  = "ROLE_ID")
	private String roleId;		//角色ID
	@TableField("ROLE_NAME")
	private String roleName;	//角色名称
	@TableField("SORT_NO")
	private Integer sortNo;		//排序号
	@TableField("REMARK")
	private String remark;		//备注

	public String getRoleId() {
		return this.roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return this.roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public Integer getSortNo() {
		return this.sortNo;
	}

	public void setSortNo(Integer sortNo) {
		this.sortNo = sortNo;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
