package com.king.environment.environmentList.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 环境实体类
 * 对应表: tf_environment
 */
@Data
@TableName("tf_environment")
public class TfEnvironment {

    /**
     * 环境Id
     */
    @TableId(value = "ENV_ID", type = IdType.AUTO)
    private Integer envId;

    /**
     * 环境名称
     */
    @TableField("ENV_NAME")
    private String envName;

    /**
     * 测试阶段:SIT、PAT
     */
    @TableField("TEST_STAGE")
    private String testStage;

    /**
     * 备注
     */
    @TableField("REMARK")
    private String remark;

    /**
     * 无参构造函数
     */
    public TfEnvironment() {
    }

    /**
     * 全参构造函数
     * @param envId 环境Id
     * @param envName 环境名称
     * @param testStage 测试阶段:SIT、PAT
     * @param remark 备注
     */
    public TfEnvironment(Integer envId, String envName, String testStage, String remark) {
        this.envId = envId;
        this.envName = envName;
        this.testStage = testStage;
        this.remark = remark;
    }

    // Getter和Setter方法
    /**
     * 获取环境Id
     * @return 环境Id
     */
    public Integer getEnvId() {
        return envId;
    }

    /**
     * 设置环境Id
     * @param envId 环境Id
     */
    public void setEnvId(Integer envId) {
        this.envId = envId;
    }

    /**
     * 获取环境名称
     * @return 环境名称
     */
    public String getEnvName() {
        return envName;
    }

    /**
     * 设置环境名称
     * @param envName 环境名称
     */
    public void setEnvName(String envName) {
        this.envName = envName;
    }

    /**
     * 获取测试阶段
     * @return 测试阶段:SIT、PAT
     */
    public String getTestStage() {
        return testStage;
    }

    /**
     * 设置测试阶段
     * @param testStage 测试阶段:SIT、PAT
     */
    public void setTestStage(String testStage) {
        this.testStage = testStage;
    }

    /**
     * 获取备注
     * @return 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }
}
