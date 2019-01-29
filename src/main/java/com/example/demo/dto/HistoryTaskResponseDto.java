package com.example.demo.dto;

import java.util.Date;

public class HistoryTaskResponseDto {
    //用户ID
    private String userId;
    //结果编码
    private String resultCode;
    //内容
    private String context;
    //开始时间
    private Date startTime;
    //结束时间
    private Date endTime;
    //业务ID
    private String orderId;
    //活动名称
    private String activitiName;
    //任务ID
    private String taskId;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getActivitiName() {
        return activitiName;
    }

    public void setActivitiName(String activitiName) {
        this.activitiName = activitiName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
