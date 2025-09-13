package com.king.framework.counter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_sys_counter")
public class SysCounter {
    @TableId(value = "COUNTER_ID",type = IdType.AUTO)
    private Integer counterId;
    @TableField("COUNTER_NAME")
    private String counterName;
    @TableField("CURRENT_NUMBER")
    private Integer currentNumber;//计数器当前数字
    @TableField("PREFIX")
    private String prefix;//前缀
    @TableField("COUNTER_LENGTH")
    private String counterLength;//计数长度

    public Integer getCounterId() {
        return counterId;
    }

    public void setCounterId(Integer counterId) {
        this.counterId = counterId;
    }

    public String getCounterName() {
        return counterName;
    }

    public void setCounterName(String counterName) {
        this.counterName = counterName;
    }

    public Integer getCurrentNumber() {
        return currentNumber;
    }

    public void setCurrentNumber(Integer currentNumber) {
        this.currentNumber = currentNumber;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getCounterLength() {
        return counterLength;
    }

    public void setCounterLength(String counterLength) {
        this.counterLength = counterLength;
    }
}
