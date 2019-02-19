package com.example.demo.dto;

public class ExeTaskRequestDto {
    //任务ID
    private String taskId;
    //流程实例ID
    private String processId;
    //用户ID
    private String userId;
    //结果CODE 1.同意，0.否决
    private String resultCode;
    //内容
    private String context;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
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
}
