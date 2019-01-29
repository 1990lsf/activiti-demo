package com.example.demo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * The type Start task request dto.
 *
 * @author naughty
 */
@ApiModel("开始流程请求对象")
public class StartTaskRequestDto {

    @ApiModelProperty("部署ID")
    private String deploymentId;
    @ApiModelProperty("业务ID")
    private String businessId;
    @ApiModelProperty("商户ID")
    private String tenantId;

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
     * Gets business id.
     *
     * @return the business id
     */
    public String getBusinessId() {
        return businessId;
    }

    /**
     * Sets business id.
     *
     * @param businessId the business id
     */
    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    /**
     * Gets deployment id.
     *
     * @return the deployment id
     */
    public String getDeploymentId() {
        return deploymentId;
    }

    /**
     * Sets deployment id.
     *
     * @param deploymentId the deployment id
     */
    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }
}
