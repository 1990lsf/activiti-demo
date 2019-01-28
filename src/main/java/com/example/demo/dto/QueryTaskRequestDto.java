package com.example.demo.dto;

public class QueryTaskRequestDto {
    //用户ID
    private String userId;
    //业务ID
    private String orderId;
    //流程实例ID
    private String processId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }
}
