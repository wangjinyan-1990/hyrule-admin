package com.king.framework.dataDictionary.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 数据字典实体类
 */
@Data
@TableName("data_dictionary")
public class DataDictionary {

    /**
     * 字典ID
     */
    @TableId(value = "DICTIONARY_ID", type = IdType.AUTO)
    private Integer dictionaryId;

    /**
     * 数据类型
     */
    @TableField("DATA_TYPE")
    private String dataType;

    /**
     * 数据名称
     */
    @TableField("DATA_NAME")
    private String dataName;

    /**
     * 数据值
     */
    @TableField("DATA_VALUE")
    private String dataValue;

    /**
     * 排序号
     */
    @TableField("POSITION_NO")
    private Integer positionNo;

    /**
     * 状态:A有效；B作废
     */
    @TableField("STATUS")
    private String status;

    /**
     * 备注
     */
    @TableField("COMMENT")
    private String comment;

    // Getter和Setter方法
    public Integer getDictionaryId() {
        return dictionaryId;
    }

    public void setDictionaryId(Integer dictionaryId) {
        this.dictionaryId = dictionaryId;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public String getDataValue() {
        return dataValue;
    }

    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }

    public Integer getPositionNo() {
        return positionNo;
    }

    public void setPositionNo(Integer positionNo) {
        this.positionNo = positionNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
