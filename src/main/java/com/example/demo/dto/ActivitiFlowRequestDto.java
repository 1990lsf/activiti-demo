package com.example.demo.dto;

import java.util.List;

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
     * 步骤列表
     */
    private List<ActivitiFlowStepDto> stepDtoList;

    public List<ActivitiFlowStepDto> getStepDtoList() {
        return stepDtoList;
    }

    public void setStepDtoList(List<ActivitiFlowStepDto> stepDtoList) {
        this.stepDtoList = stepDtoList;
    }

    public String getActivitiName() {
        return activitiName;
    }

    public void setActivitiName(String activitiName) {
        this.activitiName = activitiName;
    }

    public String getActivitiDesc() {
        return activitiDesc;
    }

    public void setActivitiDesc(String activitiDesc) {
        this.activitiDesc = activitiDesc;
    }
}
