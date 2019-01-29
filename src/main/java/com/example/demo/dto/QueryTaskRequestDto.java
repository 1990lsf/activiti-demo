package com.example.demo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

/**
 * The type Query task request dto.
 * @author naughty
 */
@ApiModel("查询流程实例任务")
public class QueryTaskRequestDto {
    //用户ID
    @ApiModelProperty("用户ID")
    @NotBlank
    private String userId;
    //流程实例ID
    @ApiModelProperty("流程实例ID")
    private String processId;

    /**
     * Gets user id.
     *
     * @return the user id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets user id.
     *
     * @param userId the user id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets process id.
     *
     * @return the process id
     */
    public String getProcessId() {
        return processId;
    }

    /**
     * Sets process id.
     *
     * @param processId the process id
     */
    public void setProcessId(String processId) {
        this.processId = processId;
    }
}
