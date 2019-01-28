package com.example.demo.controller;

import com.alibaba.fastjson.JSON;
import com.example.demo.dto.ActivitiFlowRequestDto;
import com.example.demo.dto.ExeTaskRequestDto;
import com.example.demo.dto.QueryTaskRequestDto;
import com.example.demo.dto.StartTaskRequestDto;
import com.example.demo.service.IActivitiFlowService;
import com.example.demo.dto.PerTask;
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
    public ResponseEntity activationActiviti(@RequestBody StartTaskRequestDto startTaskRequestDto) {
        logger.info("开始流程任务:{},业务编号:{}", startTaskRequestDto.getProcessId(), startTaskRequestDto.getOrderId());
        iActivitiFlowService.activationActiviti(startTaskRequestDto);
        return new ResponseEntity(HttpStatus.OK);
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
        logger.info("获取流程任务:{},业务编号:{}", queryTaskRequestDto.getProcessId(), queryTaskRequestDto.getOrderId());
        return ResponseEntity.ok(iActivitiFlowService.queryActivitiTask(queryTaskRequestDto));
    }

    /**
     * 执行流程任务.
     */
    @PostMapping(value = "/activiti/order/run/task")
    @ApiOperation(value = "执行任务", notes = "执行任务")
    public ResponseEntity exeActivitiTask(ExeTaskRequestDto exeTaskRequestDto) {
        logger.info("执行任务:{},执行人:{}", exeTaskRequestDto.getTaskId(), exeTaskRequestDto.getUserId());
        iActivitiFlowService.exeActivitiTask(exeTaskRequestDto);
        return new ResponseEntity(HttpStatus.OK);
    }
}
