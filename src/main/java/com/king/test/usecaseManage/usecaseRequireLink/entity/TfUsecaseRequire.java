package com.king.test.usecaseManage.usecaseRequireLink.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 测试用例和需求点关联关系实体类
 * 对应表: tf_usecase_require
 */
@Data
@TableName("tf_usecase_require")
public class TfUsecaseRequire {

    /**
     * 关联关系ID（自增主键）
     */
    @TableId("USECASE_REQUIER_ID")
    private Integer usecaseRequierId;

    /**
     * 用例ID
     */
    @TableField("USECASE_ID")
    private String usecaseId;

    /**
     * 需求点ID
     */
    @TableField("REQUIRE_POINT_ID")
    private String requirePointId;

    // 构造函数
    public TfUsecaseRequire() {
    }

    public TfUsecaseRequire(Integer usecaseRequierId, String usecaseId, String requirePointId) {
        this.usecaseRequierId = usecaseRequierId;
        this.usecaseId = usecaseId;
        this.requirePointId = requirePointId;
    }

    public TfUsecaseRequire(String usecaseId, String requirePointId) {
        this.usecaseId = usecaseId;
        this.requirePointId = requirePointId;
    }

    // Getter和Setter方法（Lombok的@Data注解会自动生成，这里提供手动版本作为备选）
    public Integer getUsecaseRequierId() {
        return usecaseRequierId;
    }

    public void setUsecaseRequierId(Integer usecaseRequierId) {
        this.usecaseRequierId = usecaseRequierId;
    }

    public String getUsecaseId() {
        return usecaseId;
    }

    public void setUsecaseId(String usecaseId) {
        this.usecaseId = usecaseId;
    }

    public String getRequirePointId() {
        return requirePointId;
    }

    public void setRequirePointId(String requirePointId) {
        this.requirePointId = requirePointId;
    }
}
