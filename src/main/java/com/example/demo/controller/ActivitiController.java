package com.example.demo.controller;

import com.alibaba.fastjson.JSON;
import com.example.demo.dto.*;
import com.example.demo.service.IActivitiFlowService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The type Activiti controller.
 */
@RestController
@Api
public class ActivitiController {
    private static final Logger logger = LoggerFactory.getLogger(ActivitiController.class);
    @Autowired
    private IActivitiFlowService iActivitiFlowService;

    /***
     * 创建流程.
     * @param activitiFlowRequestDto the activiti flow request dto
     * @return the response entity
     */
    @PostMapping(value = "/activiti")
    @ApiOperation(value = "创建流程", notes = "创建流程")
    public ResponseEntity<String> createActiviti(@RequestBody ActivitiFlowRequestDto activitiFlowRequestDto) {
        logger.info("创建流程:{}", JSON.toJSONString(activitiFlowRequestDto));
        return ResponseEntity.ok(iActivitiFlowService.createActivitiFlow(activitiFlowRequestDto));
    }

    /**
     * 开始流程.
     *
     * @param startTaskRequestDto the start task request dto
     * @return the response entity
     */
    @PostMapping(value = "/activiti/order/start/")
    @ApiOperation(value = "开始流程", notes = "开始流程")
    public ResponseEntity<String> activationActiviti(@RequestBody StartTaskRequestDto startTaskRequestDto) {
        logger.info("开始流程任务:{},业务编号:{}", startTaskRequestDto.getDeploymentId(), startTaskRequestDto.getBusinessId());
        return ResponseEntity.ok(iActivitiFlowService.activationActiviti(startTaskRequestDto));
    }

    /**
     * 获取流程任务.
     *
     * @param queryTaskRequestDto the query task request dto
     * @return the response entity
     */
    @GetMapping(value = "/activiti/order/task/")
    @ApiOperation(value = "获取流程任务", notes = "获取流程任务")
    public ResponseEntity<List<PerTask>> queryActivitiTask(QueryTaskRequestDto queryTaskRequestDto) {
        logger.info("用户:{},获取流程任务:{}", queryTaskRequestDto.getUserId(), queryTaskRequestDto.getProcessId());
        return ResponseEntity.ok(iActivitiFlowService.queryActivitiTask(queryTaskRequestDto));
    }

    /**
     * 执行流程任务.
     *
     * @param exeTaskRequestDto the exe task request dto
     * @return the response entity
     */
    @PostMapping(value = "/activiti/order/run/task")
    @ApiOperation(value = "执行任务", notes = "执行任务")
    public ResponseEntity exeActivitiTask(ExeTaskRequestDto exeTaskRequestDto) {
        logger.info("执行任务:{},执行人:{}", exeTaskRequestDto.getTaskId(), exeTaskRequestDto.getUserId());
        iActivitiFlowService.exeActivitiTask(exeTaskRequestDto);
        return new ResponseEntity(HttpStatus.OK);
    }


    /**
     * 获取历史任务.
     *
     * @param historyTaskRequestDto the history task request dto
     * @return the response entity
     */
    @GetMapping(value = "/activiti/order/history/task")
    @ApiOperation(value = "获取历史任务", notes = "获取历史任务")
    public ResponseEntity<List<HistoryTaskResponseDto>> historyActivitiTask(HistoryTaskRequestDto historyTaskRequestDto) {
        logger.info("获取历史任务:请求参数:{}", JSON.toJSONString(historyTaskRequestDto));
        return ResponseEntity.ok(iActivitiFlowService.historyActivitiTask(historyTaskRequestDto));
    }

}
