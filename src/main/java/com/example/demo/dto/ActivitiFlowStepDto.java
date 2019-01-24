package com.example.demo.dto;

/**
 * The type Activiti flow step dto.
 */
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
     * 序号,从1开始递增
     */
    private Integer serialNumber;

    /**
     * 节点执行人/角色，1-人，2-角色
     */
    private String nodeActionType;

    /**
     * 前一节点的类型 1-会签，2-普通
     * @return
     */
    private String parentNodeType;

    /**
     * Gets parent node type.
     *
     * @return the parent node type
     */
    public String getParentNodeType() {
        return parentNodeType;
    }

    /**
     * Sets parent node type.
     *
     * @param parentNodeType the parent node type
     */
    public void setParentNodeType(String parentNodeType) {
        this.parentNodeType = parentNodeType;
    }

    /**
     * Gets role or user id.
     *
     * @return the role or user id
     */
    public String getRoleOrUserId() {
        return roleOrUserId;
    }

    /**
     * Sets role or user id.
     *
     * @param roleOrUserId the role or user id
     */
    public void setRoleOrUserId(String roleOrUserId) {
        this.roleOrUserId = roleOrUserId;
    }

    /**
     * Gets node type.
     *
     * @return the node type
     */
    public String getNodeType() {
        return nodeType;
    }

    /**
     * Sets node type.
     *
     * @param nodeType the node type
     */
    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    /**
     * Gets serial number.
     *
     * @return the serial number
     */
    public Integer getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets serial number.
     *
     * @param serialNumber the serial number
     */
    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * Gets node action type.
     *
     * @return the node action type
     */
    public String getNodeActionType() {
        return nodeActionType;
    }

    /**
     * Sets node action type.
     *
     * @param nodeActionType the node action type
     */
    public void setNodeActionType(String nodeActionType) {
        this.nodeActionType = nodeActionType;
    }
}
