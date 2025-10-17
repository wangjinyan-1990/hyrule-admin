package com.king.test.usecaseManage.usecaseRepository.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 测试用例修改历史实体类
 * 对应表: tf_usecase_history
 */
@Data
@TableName("tf_usecase_history")
public class TfUsecaseHistory {

    /**
     * 历史记录ID
     */
    @TableId("USECASE_HISTORY_ID")
    private String usecaseHistoryId;

    /**
     * 用例Id
     */
    @TableField("USECASE_ID")
    private String usecaseId;

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
    public TfUsecaseHistory() {
    }

    public TfUsecaseHistory(String usecaseHistoryId, String usecaseId, LocalDateTime operatingTime, 
                           String operatorId, String modifiedContent) {
        this.usecaseHistoryId = usecaseHistoryId;
        this.usecaseId = usecaseId;
        this.operatingTime = operatingTime;
        this.operatorId = operatorId;
        this.modifiedContent = modifiedContent;
    }

    public TfUsecaseHistory(String usecaseId, LocalDateTime operatingTime, String operatorId, String modifiedContent) {
        this.usecaseId = usecaseId;
        this.operatingTime = operatingTime;
        this.operatorId = operatorId;
        this.modifiedContent = modifiedContent;
    }

    // Getter和Setter方法（Lombok的@Data注解会自动生成，这里提供手动版本作为备选）
    public String getUsecaseHistoryId() {
        return usecaseHistoryId;
    }

    public void setUsecaseHistoryId(String usecaseHistoryId) {
        this.usecaseHistoryId = usecaseHistoryId;
    }

    public String getUsecaseId() {
        return usecaseId;
    }

    public void setUsecaseId(String usecaseId) {
        this.usecaseId = usecaseId;
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
