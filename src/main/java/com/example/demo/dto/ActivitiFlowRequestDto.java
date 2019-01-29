package com.example.demo.dto;

import java.util.List;

/**
 * The type Activiti flow request dto.
 * @author naughty
 */
public class ActivitiFlowRequestDto {
    /**
     * 流程名称.
     */
    private String activitiName;
    /**
     * 流程描述.
     */
    private String activitiDesc;

    /**
     * 商户ID
     */
    private String tenantId;
    /**
     * 步骤列表
     */
    private List<ActivitiFlowStepDto> stepDtoList;

    /**
     * Gets tenant id.
     *
     * @return the tenant id
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * Sets tenant id.
     *
     * @param tenantId the tenant id
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * Gets step dto list.
     *
     * @return the step dto list
     */
    public List<ActivitiFlowStepDto> getStepDtoList() {
        return stepDtoList;
    }

    /**
     * Sets step dto list.
     *
     * @param stepDtoList the step dto list
     */
    public void setStepDtoList(List<ActivitiFlowStepDto> stepDtoList) {
        this.stepDtoList = stepDtoList;
    }

    /**
     * Gets activiti name.
     *
     * @return the activiti name
     */
    public String getActivitiName() {
        return activitiName;
    }

    /**
     * Sets activiti name.
     *
     * @param activitiName the activiti name
     */
    public void setActivitiName(String activitiName) {
        this.activitiName = activitiName;
    }

    /**
     * Gets activiti desc.
     *
     * @return the activiti desc
     */
    public String getActivitiDesc() {
        return activitiDesc;
    }

    /**
     * Sets activiti desc.
     *
     * @param activitiDesc the activiti desc
     */
    public void setActivitiDesc(String activitiDesc) {
        this.activitiDesc = activitiDesc;
    }
}
