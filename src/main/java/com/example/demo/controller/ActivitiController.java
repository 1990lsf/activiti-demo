package com.example.demo.controller;

import com.alibaba.fastjson.JSON;
import com.example.demo.dto.ActivitiFlowRequestDto;
import com.example.demo.service.IActivitiFlowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.activiti.engine.task.Task;
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
     * @param processId the process id
     * @param orderId   the order id
     * @return the response entity
     */
    @PostMapping(value = "/activiti/{processId}/order/{orderId}")
    @ApiOperation(value = "开始流程", notes = "开始流程")
    public ResponseEntity activationActiviti(
        @PathVariable("processId")String processId, @PathVariable("orderId") String orderId) {
        logger.info("开始流程任务:{},业务编号:{}", processId, orderId);
        iActivitiFlowService.activationActiviti(processId, orderId);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * 获取流程任务.
     */
    @GetMapping(value = "/activiti/{processId}/order/{orderId}")
    @ApiOperation(value = "获取流程任务", notes = "开始流程任务")
    public ResponseEntity<List<Task>> queryActivitiTask(
        @PathVariable("processId")String processId, @PathVariable("orderId") String orderId) {
        logger.info("获取流程任务:{},业务编号:{}", processId, orderId);

        return ResponseEntity.ok(iActivitiFlowService.queryActivitiTask(processId, orderId));
    }
}
