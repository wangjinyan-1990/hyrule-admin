package com.king.test.baseManage.testSystem.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户系统更新DTO
 * 用于接收前端传递的用户系统关系更新数据
 */
@Data
public class UserSystemUpdateDTO {
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 用户姓名
     */
    private String userName;
    
    /**
     * 登录名
     */
    private String loginName;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 电话
     */
    private String phone;
    
    /**
     * 机构名称
     */
    private String orgName;
    
    /**
     * 系统ID列表
     */
    private List<String> systemIds;
    
    /**
     * 系统名称列表
     */
    private String systemNames;
    
    public UserSystemUpdateDTO() {
    }
    
    @JsonCreator
    public UserSystemUpdateDTO(@JsonProperty("userId") String userId,
                              @JsonProperty("userName") String userName,
                              @JsonProperty("loginName") String loginName,
                              @JsonProperty("email") String email,
                              @JsonProperty("phone") String phone,
                              @JsonProperty("orgName") String orgName,
                              @JsonProperty("systemIds") Object systemIds,
                              @JsonProperty("systemNames") String systemNames) {
        this.userId = userId;
        this.userName = userName;
        this.loginName = loginName;
        this.email = email;
        this.phone = phone;
        this.orgName = orgName;
        this.systemNames = systemNames;
        this.systemIds = parseSystemIds(systemIds);
    }
    
    /**
     * 解析系统ID列表
     * 支持字符串、字符串数组或逗号分隔的字符串
     */
    private List<String> parseSystemIds(Object systemIds) {
        if (systemIds == null) {
            return new ArrayList<>();
        }
        
        List<String> result = new ArrayList<>();
        
        if (systemIds instanceof String) {
            String systemIdsStr = (String) systemIds;
            if (!systemIdsStr.trim().isEmpty()) {
                // 支持逗号分隔的字符串
                String[] ids = systemIdsStr.split(",");
                for (String id : ids) {
                    if (!id.trim().isEmpty()) {
                        result.add(id.trim());
                    }
                }
            }
        } else if (systemIds instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) systemIds;
            for (Object item : list) {
                if (item != null) {
                    result.add(item.toString());
                }
            }
        }
        
        return result;
    }
}
