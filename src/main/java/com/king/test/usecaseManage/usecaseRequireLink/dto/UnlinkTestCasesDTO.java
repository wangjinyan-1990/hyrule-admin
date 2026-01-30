package com.king.test.usecaseManage.usecaseRequireLink.dto;

import java.util.List;

/**
 * 取消关联测试用例DTO
 */
public class UnlinkTestCasesDTO {
    
    /**
     * 需求点ID
     */
    private String requirePointId;
    
    /**
     * 测试用例ID数组
     */
    private List<String> testCaseIds;

    public String getRequirePointId() {
        return requirePointId;
    }

    public void setRequirePointId(String requirePointId) {
        this.requirePointId = requirePointId;
    }

    public List<String> getTestCaseIds() {
        return testCaseIds;
    }

    public void setTestCaseIds(List<String> testCaseIds) {
        this.testCaseIds = testCaseIds;
    }
}

