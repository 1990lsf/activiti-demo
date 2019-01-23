package com.example.demo.service;

import com.example.demo.dto.ActivitiFlowRequestDto;
import org.activiti.engine.task.Task;

import java.util.List;

/**
 * The interface Activiti flow service.
 */
public interface IActivitiFlowService {

     /**
      * Create activiti flow string.
      *
      * @param activitiRequestDto the activiti request dto
      * @return the string
      */
     String createActivitiFlow(ActivitiFlowRequestDto activitiRequestDto);

     /**
      * 启动流程实例.
      *
      * @param processId the process id
      * @param orderId   the order id
      */
     void activationActiviti(String processId,String orderId);

     /**
      * Query activiti task.
      *
      * @param processId the process id
      * @param orderId   the order id
      */
     List<Task> queryActivitiTask(String processId, String orderId);
}
