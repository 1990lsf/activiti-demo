package com.example.demo.dto;

/**
 * The type History task request dto.
 * @author naughty
 */
public class HistoryTaskRequestDto {
    //用户ID
    private String userId;

    //实例ID
    private String processId;

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

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

}
