package com.king.test.usecaseManage.requireRepository.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 测试需求点修改历史实体类
 * 对应表: tf_requirepoint_history
 */
@Data
@TableName("tf_requirepoint_history")
public class TfRequirepointHistory {

    /**
     * 历史记录ID
     */
    @TableId("REQUIRE_POINT_HISTORY_ID")
    private String requirePointHistoryId;

    /**
     * 需求点Id
     */
    @TableField("REQUIRE_POINT_ID")
    private String requirePointId;

    /**
     * 操作时间
     */
    @TableField("OPERATING_TIME")
    private LocalDateTime operatingTime;

    /**
     * 操作人ID
     */
    @TableField("OPERATOR_ID")
    private String operatorId;

    /**
     * 操作人姓名（关联查询字段，不对应数据库字段）
     */
    @TableField(exist = false)
    private String operatorName;

    /**
     * 修改内容
     */
    @TableField("MODIFIED_CONTENT")
    private String modifiedContent;

    // 构造函数
    public TfRequirepointHistory() {
    }

    public TfRequirepointHistory(String requirePointHistoryId, String requirePointId, LocalDateTime operatingTime, 
                                String operatorId, String modifiedContent) {
        this.requirePointHistoryId = requirePointHistoryId;
        this.requirePointId = requirePointId;
        this.operatingTime = operatingTime;
        this.operatorId = operatorId;
        this.modifiedContent = modifiedContent;
    }

    public TfRequirepointHistory(String requirePointId, LocalDateTime operatingTime, String operatorId, String modifiedContent) {
        this.requirePointId = requirePointId;
        this.operatingTime = operatingTime;
        this.operatorId = operatorId;
        this.modifiedContent = modifiedContent;
    }

    // Getter和Setter方法（Lombok的@Data注解会自动生成，这里提供手动版本作为备选）
    public String getRequirePointHistoryId() {
        return requirePointHistoryId;
    }

    public void setRequirePointHistoryId(String requirePointHistoryId) {
        this.requirePointHistoryId = requirePointHistoryId;
    }

    public String getRequirePointId() {
        return requirePointId;
    }

    public void setRequirePointId(String requirePointId) {
        this.requirePointId = requirePointId;
    }

    public LocalDateTime getOperatingTime() {
        return operatingTime;
    }

    public void setOperatingTime(LocalDateTime operatingTime) {
        this.operatingTime = operatingTime;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getModifiedContent() {
        return modifiedContent;
    }

    public void setModifiedContent(String modifiedContent) {
        this.modifiedContent = modifiedContent;
    }
}
