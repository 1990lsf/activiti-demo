package com.example.demo.service;

import com.example.demo.dto.ActivitiFlowRequestDto;
import com.example.demo.dto.ExeTaskRequestDto;
import com.example.demo.dto.QueryTaskRequestDto;
import com.example.demo.dto.StartTaskRequestDto;
import com.example.demo.dto.PerTask;

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
      * @param startTaskRequestDto the start task request dto
      */
     void activationActiviti(StartTaskRequestDto startTaskRequestDto);

     /**
      * Query activiti task.
      *
      * @param queryTaskRequestDto the query task request dto
      * @return the list
      */
     List<PerTask> queryActivitiTask(QueryTaskRequestDto queryTaskRequestDto);

     /**
      * 执行任务.
      * @param exeTaskRequestDto
      */
    void exeActivitiTask(ExeTaskRequestDto exeTaskRequestDto);
}
