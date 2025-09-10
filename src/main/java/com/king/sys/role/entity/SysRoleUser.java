package com.king.sys.role.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * SysRoleUser entity.
 * 
 * @author MyEclipse Persistence Tools
 */
@Data
@TableName("t_sys_role_user")
public class SysRoleUser implements java.io.Serializable {

	// Fields
	@TableId(value = "ID",type = IdType.AUTO)
	private String id;
	@TableField("ROLE_ID")
	private String roleId;// 角色ID
	@TableField("USER_ID")
	private String userId;// 用户ID

	// Constructors

	/** default constructor */
	public SysRoleUser() {
	}

	/** full constructor */
	public SysRoleUser(String id, String roleId, String userId) {
		this.id = id;
		this.roleId = roleId;
		this.userId = userId;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRoleId() {
		return this.roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
