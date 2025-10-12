package com.king.framework.param.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 系统参数实体类
 * 对应数据库表：t_sys_param
 */
@Data
@TableName("t_sys_param")
public class TSysParam {

    /**
     * 系统参数ID
     */
    @TableId(value = "PARAM_ID")
    private String paramId;

    /**
     * 系统参数名称
     */
    @TableField("PARAM_NAME")
    private String paramName;

    /**
     * 系统参数值
     */
    @TableField("PARAM_VALUE")
    private String paramValue;

    /**
     * 排序号
     */
    @TableField("SORT_NO")
    private Integer sortNo;

    /**
     * 备注
     */
    @TableField("REMARK")
    private String remark;

    // Getter和Setter方法
    public String getParamId() {
        return paramId;
    }

    public void setParamId(String paramId) {
        this.paramId = paramId;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
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
}
