package com.example.demo.dto;

public class ActivitiFlowStepDto {

    /**
     * 角色或者人的列表
     */
    private String roleOrUserId;

    /**
     * 节点类型，1-会签,2-普通.
     */
    private String nodeType;

    /**
     * 序号
     */
    private Integer serialNumber;

    /**
     * 节点执行人/角色，1-人，2-角色
     */
    private String nodeActionType;

    public String getRoleOrUserId() {
        return roleOrUserId;
    }

    public void setRoleOrUserId(String roleOrUserId) {
        this.roleOrUserId = roleOrUserId;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public Integer getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getNodeActionType() {
        return nodeActionType;
    }

    public void setNodeActionType(String nodeActionType) {
        this.nodeActionType = nodeActionType;
    }
}
