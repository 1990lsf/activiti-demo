package com.example.demo.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.demo.dto.ActivitiFlowRequestDto;
import com.example.demo.dto.ActivitiFlowStepDto;
import com.example.demo.service.IActivitiFlowService;
import com.example.demo.utils.SnowflakeIdWorker;
import com.google.common.collect.Lists;
import org.activiti.bpmn.BpmnAutoLayout;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.*;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;


import org.activiti.engine.task.TaskQuery;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Activiti flow service.
 *
 * @author naughty
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ActivitiFlowServiceImpl implements IActivitiFlowService {

    private static final Logger logger = LoggerFactory.getLogger(ActivitiFlowServiceImpl.class);

    /**
     * 会签
     */
    private static final String NODE_TYPE_SING = "1";

    /**
     * 角色
     */
    private static final String NODE_ACTIVITI_TYPE_ROLE = "2";

    private static final SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker(0, 0);

    /**
     * 创建流程.
     *
     * @param activitiFlowRequestDto the activiti flow request dto
     * @return string
     */
    @Override
    public String createActivitiFlow(ActivitiFlowRequestDto activitiFlowRequestDto) {
        List<ActivitiFlowStepDto> activitiFlowStepDtos = activitiFlowRequestDto.getStepDtoList();
        List<ActivitiFlowStepDto> sortStepList =
            Optional.ofNullable(activitiFlowStepDtos).orElse(Lists.newArrayList()).stream().filter(Objects::nonNull)
                .sorted(Comparator.comparing(ActivitiFlowStepDto::getSerialNumber))
                .collect(Collectors.toList());
        return createActivitiProcess(activitiFlowRequestDto, sortStepList);
    }

    /**
     * 启动流程实例.
     *
     * @param processId the process id
     */
    @Override
    public void activationActiviti(String processId, String orderId) {

        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 4. 启动一个流程实例

        ProcessInstance processInstance =
            processEngine.getRuntimeService().startProcessInstanceByKeyAndTenantId(processId, orderId, "1234567");
        logger.info("启动流程");
        // 5. 获取流程任务
        List<Task> tasks =
            processEngine.getTaskService().createTaskQuery().processInstanceId(processInstance.getId()).list();

        try {
            //6. 将流程图保存到本地文件
            InputStream processDiagram =
                processEngine.getRepositoryService().getProcessDiagram(processInstance.getProcessDefinitionId());
            FileUtils.copyInputStreamToFile(processDiagram, new File("/Users/naughty/Pictures/" + processId + ".png"));
            logger.info("启动流程流程图保存到本地");
            // 7. 保存BPMN.xml到本地文件
//                    InputStream processBpmn = processEngine.getRepositoryService().getResourceAsStream(deployment
//                    .getId(), process.getId()+".bpmn");
//                    FileUtils.copyInputStreamToFile(processBpmn,new File("/deployments/"+process.getId()+".bpmn"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Query activiti task.
     *
     * @param processId the process id
     * @param orderId   the order id
     */
    @Override
    public List<Task> queryActivitiTask(String processId, String orderId) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskQuery taskQuery = processEngine.getTaskService().createTaskQuery();
        List<Task> list = taskQuery.processInstanceBusinessKey(orderId).list();
//        logger.info("获取结果:{}", JSON.toJSONString(list));
        return list;

    }


    private String createActivitiProcess(ActivitiFlowRequestDto activitiFlowRequestDto,
                                         List<ActivitiFlowStepDto> stepDtoList) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();


        BpmnModel model = new BpmnModel();
        Process process = new Process();
        model.addProcess(process);
        process.setId("P" + String.valueOf(snowflakeIdWorker.nextId()));
        process.setName(activitiFlowRequestDto.getActivitiName());
        process.setDocumentation(activitiFlowRequestDto.getActivitiDesc());

        //开始流程节点
        process.addFlowElement(createStartEvent());

        //节点布局
        createNode(process, stepDtoList);

        //结束流程节点
        process.addFlowElement(createEndEvent());

        //连线
        createLine(process, stepDtoList);

        //生成图形信息
        new BpmnAutoLayout(model).execute();

        //部署流程
        Deployment deployment =
            processEngine.getRepositoryService().createDeployment().addBpmnModel(process.getId() + ".bpmn", model).name(process.getId() + "_deployment").tenantId("1234567").deploy();
        try {
            //生成xml
            InputStream processBpmn = processEngine.getRepositoryService().getResourceAsStream(deployment.getId(),
                process.getId() + ".bpmn");
            FileUtils.copyInputStreamToFile(processBpmn, new File("/Users/naughty/Pictures/" + process.getId() +
                ".bpmn"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return process.getId();
    }

    /**
     * 任务节点-用户.
     *
     * @param id       the id
     * @param name     the name
     * @param userPkno the user pkno
     * @return the user task
     */
    protected UserTask createUserTask(String id, String name, String userPkno) {
        List<String> candidateUsers = new ArrayList<String>();
        candidateUsers.add(userPkno);
        UserTask userTask = new UserTask();
        userTask.setName(name);
        userTask.setId(id);

        userTask.setCandidateUsers(candidateUsers);
        return userTask;
    }

    /**
     * 排他网关.
     *
     * @param id   the id
     * @param name the name
     * @return the exclusive gateway
     */
    protected ExclusiveGateway createExclusiveGateway(String id, String name) {
        ExclusiveGateway exclusiveGateway = new ExclusiveGateway();
        exclusiveGateway.setId(id);
        exclusiveGateway.setName(name);
        return exclusiveGateway;
    }

    /**
     * 并行网关.
     *
     * @param id   the id
     * @param name the name
     * @return the parallel gateway
     */
    protected ParallelGateway createParallelGateway(String id, String name) {
        ParallelGateway gateway = new ParallelGateway();
        gateway.setId(id);
        gateway.setName(name);
        return gateway;
    }

    /**
     * 开始节点.
     *
     * @return the start event
     */
    protected StartEvent createStartEvent() {
        StartEvent startEvent = new StartEvent();
        startEvent.setId("startEvent");
        return startEvent;
    }

    /**
     * 结束节点.
     *
     * @return the end event
     */
    protected EndEvent createEndEvent() {
        EndEvent endEvent = new EndEvent();
        endEvent.setId("endEvent");
        return endEvent;
    }

    /**
     * 连线
     *
     * @param from                the from
     * @param to                  the to
     * @param name                the name
     * @param conditionExpression the condition expression
     * @return sequence flow
     */
    protected SequenceFlow createSequenceFlow(String from, String to, String name, String conditionExpression) {
        SequenceFlow flow = new SequenceFlow();
        long generate = 0L;
        try {
            generate = snowflakeIdWorker.nextId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        flow.setId("F" + String.valueOf(generate));
        flow.setSourceRef(from);
        flow.setTargetRef(to);
        flow.setName(name);
        if (StringUtils.isNotEmpty(conditionExpression)) {
            flow.setConditionExpression(conditionExpression);
        }
        return flow;
    }

    /**
     * 任务节点-组
     *
     * @param id             the id
     * @param name           the name
     * @param candidateGroup the candidate group
     * @return the user task
     */
    protected UserTask createGroupTask(String id, String name, String candidateGroup) {
        List<String> candidateGroups = new ArrayList<String>();
        candidateGroups.add(candidateGroup);
        UserTask userTask = new UserTask();
        userTask.setName(name);
        userTask.setId(id);
        userTask.setCandidateGroups(candidateGroups);
        return userTask;
    }

    /**
     * 节点布局
     */
    private void createNode(Process process, List<ActivitiFlowStepDto> stepDtos) {

        for (int i = 0; i < stepDtos.size(); i++) {
            ActivitiFlowStepDto step = stepDtos.get(i);
            //判断是否会签
            if (NODE_TYPE_SING.equals(step.getNodeType())) {
                //会签
                //加入并行网关-分支
                process.addFlowElement(createParallelGateway("parallelGateway-fork" + i, "并行网关-分支" + i));
                //如果是角色类型的
                if (NODE_ACTIVITI_TYPE_ROLE.equals(step.getNodeActionType())) {
                    //获取角色下所有用户
                    List<String> userList = new ArrayList<>();
                    for (int u = 0; u < userList.size(); u++) {
                        process.addFlowElement(createUserTask("userTask" + i + u + "r", "并行网关分支用户审核节点" + i + u + "r",
                            userList.get(u)));
                    }
                } else {
                    process.addFlowElement(createUserTask("userTask" + i + "0u", "并行网关分支用户审核节点" + i + "0u",
                        step.getRoleOrUserId()));
                }

                //并行网关-汇聚
                process.addFlowElement(createParallelGateway("parallelGateway-join" + i, "并行网关到-汇聚" + i));

            } else {
                //普通流转
                //审核节点
                if (NODE_ACTIVITI_TYPE_ROLE.equals(step.getNodeActionType())) {
                    process.addFlowElement(createGroupTask("task" + i, "组审核节点" + i, step.getRoleOrUserId()));
                } else {
                    process.addFlowElement(createUserTask("task" + i, "组审核节点" + i, step.getRoleOrUserId()));
                }
                //回退节点
                process.addFlowElement(createUserTask("repulse" + i, "回退节点" + i, "${startUserId}"));
            }
        }
    }

    /**
     * 创建连线
     */
    private void createLine(Process process, List<ActivitiFlowStepDto> stepList) {
        for (int y = 0; y < stepList.size(); y++) {
            ActivitiFlowStepDto step = stepList.get(y);
            //是否会签
            if (NODE_TYPE_SING.equals(step.getNodeType())) {
                //会签
                //判断是否第一个节点
                if (y == 0) {
                    //开始节点和并行网关-分支连线
                    process.addFlowElement(createSequenceFlow("startEvent", "parallelGateway-fork" + y,
                        "开始节点到并行网关-分支" + y, ""));
                } else {
                    //审核节点或者并行网关-汇聚到并行网关-分支
                    //判断上一个节点是否是会签
                    if (NODE_TYPE_SING.equals(stepList.get(y - 1).getNodeType())) {
                        process.addFlowElement(createSequenceFlow("parallelGateway-join" + (y - 1), "parallelGateway" +
                            "-fork" + y, "并行网关-汇聚到并行网关-分支" + y, ""));
                    } else {
                        process.addFlowElement(createSequenceFlow("task" + (y - 1), "parallelGateway-fork" + y,
                            "上一个审核节点到并行网关-分支" + y, ""));
                    }
                }
                //并行网关-分支和会签用户连线，会签用户和并行网关-汇聚连线
                if (NODE_ACTIVITI_TYPE_ROLE.equals(step.getNodeActionType())) {
                    List<String> userList = new ArrayList<>();
                    for (int u = 0; u < userList.size(); u++) {
                        process.addFlowElement(createSequenceFlow("parallelGateway-fork" + y, "userTask" + y + u + "r",
                            "并行网关-分支到会签用户" + y + u + "r", ""));
                        process.addFlowElement(createSequenceFlow("userTask" + y + u + "r", "parallelGateway-join" + y,
                            "会签用户到并行网关-汇聚", ""));
                    }
                } else {
                    process.addFlowElement(createSequenceFlow("parallelGateway-fork" + y, "userTask" + y + "0u",
                        "并行网关-分支到会签用户" + y + "0u", ""));
                    process.addFlowElement(createSequenceFlow("userTask" + y + "0u", "parallelGateway-join" + y,
                        "会签用户到并行网关-汇聚", ""));
                }

                //最后一个节点  并行网关-汇聚到结束节点
                if (y == (stepList.size() - 1)) {
                    process.addFlowElement(createSequenceFlow("parallelGateway-join" + y, "endEvent", "并行网关-汇聚到结束节点",
                        ""));
                }
            } else {
                //普通流转
                //第一个节点
                if (y == 0) {
                    //开始节点和审核节点1
                    process.addFlowElement(createSequenceFlow("startEvent", "task" + y, "开始节点到审核节点" + y, ""));
                } else {
                    //判断上一个节点是否会签
                    if (NODE_TYPE_SING.equals(step.getNodeType())) {
                        //会签
                        //并行网关-汇聚到审核节点
                        process.addFlowElement(createSequenceFlow("parallelGateway-join" + (y - 1), "task" + y,
                            "并行网关-汇聚到审核节点" + y, ""));
                    } else {
                        //普通
                        process.addFlowElement(createSequenceFlow("task" + (y - 1), "task" + y, "审核节点" + (y - 1) +
                            "到审核节点" + y, "${flag=='true'}"));
                    }
                }
                //是否最后一个节点
                if (y == (stepList.size() - 1)) {
                    //审核节点到结束节点
                    process.addFlowElement(createSequenceFlow("task" + y, "endEvent", "审核节点" + y + "到结束节点", "${flag" +
                        "=='true'}"));
                }
                //审核节点到回退节点
                process.addFlowElement(createSequenceFlow("task" + y, "repulse" + y, "审核不通过-打回" + y, "${flag=='false" +
                    "'}"));
                process.addFlowElement(createSequenceFlow("repulse" + y, "task" + y, "回退节点到审核节点" + y, ""));
            }
        }
    }
}
