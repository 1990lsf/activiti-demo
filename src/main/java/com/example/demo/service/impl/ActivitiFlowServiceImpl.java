package com.example.demo.service.impl;


import com.alibaba.fastjson.JSON;
import com.example.demo.dto.*;
import com.example.demo.service.IActivitiFlowService;
import com.example.demo.utils.SnowflakeIdWorker;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.activiti.bpmn.BpmnAutoLayout;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.*;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.*;

import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;



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


import static java.util.stream.Collectors.groupingBy;

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
     * @param startTaskRequestDto the startTaskRequestDto
     */
    @Override
    public String activationActiviti(StartTaskRequestDto startTaskRequestDto) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        ProcessInstance processInstance = processEngine.getRuntimeService()
            .startProcessInstanceByKeyAndTenantId(startTaskRequestDto.getDeploymentId(),
                startTaskRequestDto.getBussinessId(), startTaskRequestDto.getTenantId());
        logger.info("业务:{},使用部署:{},启动流程", startTaskRequestDto.getBussinessId(), startTaskRequestDto.getDeploymentId());
        return processInstance.getProcessInstanceId();
    }

    /**
     * Query activiti task.
     *
     * @param queryTaskRequestDto the queryTaskRequestDto
     */
    @Override
    public List<PerTask> queryActivitiTask(QueryTaskRequestDto queryTaskRequestDto) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        List<Task> list = processEngine.getTaskService()
            .createTaskQuery()
            .taskCandidateUser(queryTaskRequestDto.getUserId())
            .orderByTaskCreateTime()
            .desc()
            .list();
        return Optional.ofNullable(list)
            .orElse(Lists.newArrayList())
            .stream()
            .filter(Objects::nonNull)
            .filter(task -> queryTaskRequestDto.getProcessId() == null ? true :
                task.getProcessInstanceId().equals(queryTaskRequestDto.getProcessId()))
            .map(task -> {
                PerTask perTask = new PerTask();
                perTask.setTaskId(task.getId());
                perTask.setName(task.getName());
                perTask.setTime(task.getCreateTime());
                perTask.setProcessId(task.getProcessInstanceId());
                ProcessInstance processInstance = processEngine.getRuntimeService()
                    .createProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .singleResult();
                perTask.setOrderId(processInstance.getBusinessKey());
                perTask.setTenantId(processInstance.getTenantId());
                return perTask;
            }).collect(Collectors.toList());
    }

    /**
     * 执行任务.
     *
     * @param exeTaskRequestDto
     */
    @Override
    public void exeActivitiTask(ExeTaskRequestDto exeTaskRequestDto) {
        //根据人,进行任务执行。
        logger.info("执行任:{}", JSON.toJSONString(exeTaskRequestDto));
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("result", exeTaskRequestDto.getResultCode());
        resultMap.put("context", exeTaskRequestDto.getContext());
        taskService.setVariablesLocal(exeTaskRequestDto.getTaskId(),resultMap);
        taskService.complete(exeTaskRequestDto.getTaskId());

    }

    /**
     * 查询任务的历史.
     *
     * @param historyTaskRequestDto
     */
    @Override
    public List<HistoryTaskResponseDto> historyActivitiTask(HistoryTaskRequestDto historyTaskRequestDto) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        HistoryService historyService = processEngine.getHistoryService();
        HistoricTaskInstanceQuery historicTaskInstanceQuery = historyService.createHistoricTaskInstanceQuery();
        if (StringUtils.isNotEmpty(historyTaskRequestDto.getUserId())) {
            //传入执行人了.
            historicTaskInstanceQuery = historicTaskInstanceQuery.taskAssignee(historyTaskRequestDto.getUserId());
        }
        if (StringUtils.isNotEmpty(historyTaskRequestDto.getProcessId())) {
            historicTaskInstanceQuery =
                historicTaskInstanceQuery.processInstanceId(historyTaskRequestDto.getProcessId());
        }
        List<HistoricTaskInstance> list =
            historicTaskInstanceQuery.orderByHistoricTaskInstanceEndTime().desc().list();

        return Optional.ofNullable(list).orElse(Lists.newArrayList()).stream().filter(Objects::nonNull)
            .map(hti -> {
                HistoryTaskResponseDto historyTaskResponseDto = new HistoryTaskResponseDto();
                historyTaskResponseDto.setActivitiName(hti.getName());

                historyTaskResponseDto.setEndTime(hti.getCreateTime());
                historyTaskResponseDto.setStartTime(hti.getStartTime());
                historyTaskResponseDto.setTaskId(hti.getId());
                List<HistoricDetail> list1 =
                    historyService.createHistoricDetailQuery().taskId(hti.getId()).list();
                historyTaskResponseDto.setContext(JSON.toJSONString(list1));
                return historyTaskResponseDto;
            })
            .collect(Collectors.toList());

    }

    private String createActivitiProcess(ActivitiFlowRequestDto activitiFlowRequestDto,
                                         List<ActivitiFlowStepDto> stepDtoList) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //处理节点的父类节点
        stepDtoList = makeNodeParentType(stepDtoList);

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
            FileUtils.copyInputStreamToFile(processBpmn, new File("/Users/naughty/Pictures/" + process.getId() +
                ".png"));
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
        UserTask userTask = new UserTask();
        userTask.setName(name);
        userTask.setId(id);
        candidateUsers.add(userPkno);
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
        //按照actiontype进行分组排序  map<节点类型,Map<步骤,列表>>
        Map<String, Map<Integer, List<ActivitiFlowStepDto>>> collect =
            Optional.ofNullable(stepDtos).orElse(Lists.newArrayList())
                .stream()
                .filter(Objects::nonNull)
                .collect(groupingBy(ActivitiFlowStepDto::getNodeType,
                    groupingBy(ActivitiFlowStepDto::getSerialNumber)));


        Optional.ofNullable(collect).orElse(Maps.newConcurrentMap()).entrySet().forEach(entry -> {
            Map<Integer, List<ActivitiFlowStepDto>> stepMap = entry.getValue();
            if (NODE_TYPE_SING.equals(entry.getKey())) {
                //会签类型
                buildSignNode(process, stepMap);
            } else {
                //普通类型
                buildNormalNode(process, stepMap);
            }
        });
    }

    /**
     * 创建连线
     */
    private void createLine(Process process, List<ActivitiFlowStepDto> stepList) {
        Map<String, Map<Integer, List<ActivitiFlowStepDto>>> stepGroupMap =
            Optional.ofNullable(stepList).orElse(Lists.newArrayList())
                .stream()
                .filter(Objects::nonNull)
                .collect(groupingBy(ActivitiFlowStepDto::getNodeType,
                    groupingBy(ActivitiFlowStepDto::getSerialNumber)));
        //获取总的步数
        Integer max =
            Optional.ofNullable(stepList).orElse(Lists.newArrayList()).stream().filter(Objects::nonNull).mapToInt(ActivitiFlowStepDto::getSerialNumber).max().getAsInt();

        Optional.ofNullable(stepGroupMap).orElse(Maps.newConcurrentMap()).entrySet().forEach(entry -> {
            if (NODE_TYPE_SING.equals(entry.getKey())) {
                //会签类型
                buildSignSequenceFlow(process, entry.getValue(), max);
            } else {
                //普通类型
                buildNormalSequenceFlow(process, entry.getValue(), max);
            }
        });
    }

    /**
     * 处理会签节点.
     *
     * @param process
     * @param activitiFlowStepDtoMap
     */
    private void buildSignNode(Process process, Map<Integer, List<ActivitiFlowStepDto>> activitiFlowStepDtoMap) {
        //循环步骤
        Optional.ofNullable(activitiFlowStepDtoMap).orElse(Maps.newConcurrentMap())
            .entrySet().forEach(entry -> {
            Integer stepNumber = entry.getKey();
            List<ActivitiFlowStepDto> activitiFlowStepDtoList = entry.getValue();
            Integer beforeNumber = stepNumber - 1;
            logger.info("增加并行网关分支:{}", "parallelGateway-fork-" + stepNumber + "-" + beforeNumber);
            //增加并行网关-分支
            process.addFlowElement(createParallelGateway("parallelGateway-fork-" + stepNumber + "-" + beforeNumber,
                "并行网关分支" + stepNumber + "-" + beforeNumber));
            //增加分支任务节点
            int size = activitiFlowStepDtoList.size();
            for (int i = 0, j = size; i < j; i++) {
                ActivitiFlowStepDto stepDto = activitiFlowStepDtoList.get(i);
                if (NODE_ACTIVITI_TYPE_ROLE.equals(stepDto.getNodeActionType())) {
                    //配置的是角色
                    List<String> userList = Lists.newArrayList();
                    for (int u = 0; u < userList.size(); u++) {
                        logger.info("根据角色增加用户审核节点:{}", "userTask-" + stepNumber + "-" + i + "-" + u);
                        process.addFlowElement(createUserTask("userTask-" + stepNumber + "-" + i + "-" + u,
                            "并行网关分支用户审核节点" + stepNumber + "-" + i + "u", userList.get(u)));
                    }
                } else {
                    //配置的是具体的用户
                    logger.info("增加用户审核节点:{}", "userTask-" + stepNumber + "-" + i);
                    process.addFlowElement(createUserTask("userTask-" + stepNumber + "-" + i,
                        "并行网关分支用户审核节点" + stepNumber + "-" + i, stepDto.getRoleOrUserId()));
                }
            }
            //增加并行网关-汇聚
            Integer afterNumber = stepNumber + 1;
            logger.info("增加并行网关汇聚:{}", "parallelGateway-join-" + +stepNumber + "-" + afterNumber);
            process.addFlowElement(createParallelGateway("parallelGateway-join-" + +stepNumber + "-" + afterNumber,
                "并行网关汇聚" + stepNumber + "-" + afterNumber));
        });
    }

    /**
     * 处理普通节点.
     *
     * @param process the process
     * @param stepMap the step map
     */
    private void buildNormalNode(Process process, Map<Integer, List<ActivitiFlowStepDto>> stepMap) {
        Optional.ofNullable(stepMap).orElse(Maps.newConcurrentMap())
            .entrySet().forEach(entry -> {
            Integer stepNumber = entry.getKey();
            List<ActivitiFlowStepDto> value = entry.getValue();
            ActivitiFlowStepDto activitiFlowStepDto = value.get(0);
            if (NODE_ACTIVITI_TYPE_ROLE.equals(activitiFlowStepDto.getNodeActionType())) {
                List<String> userList = Lists.newArrayList();
                for (int u = 0; u < userList.size(); u++) {
                    logger.info("根据角色增加用户审批节点:{}", "userTask-" + stepNumber + "-0-" + u);
                    process.addFlowElement(createUserTask("userTask-" + stepNumber + "-0-" + u,
                        "用户审核节点" + stepNumber + "-0-" + u, userList.get(u)));
                }
            } else {
                logger.info("增加用户审批节点:{}", "userTask-" + stepNumber + "-0");
                process.addFlowElement(createUserTask("userTask-" + stepNumber + "-0", "用户审核节点" + stepNumber + "-0",
                    activitiFlowStepDto.getRoleOrUserId()));
            }
        });
    }

    /**
     * 构建连线
     */
    private void buildSignSequenceFlow(Process process, Map<Integer, List<ActivitiFlowStepDto>> stepMap,
                                       Integer totalStepNumber) {
        Optional.ofNullable(stepMap).orElse(Maps.newConcurrentMap()).entrySet().forEach(entry -> {
            Integer stepNumber = entry.getKey();
            List<ActivitiFlowStepDto> stepDtoList = entry.getValue();
            if (stepNumber == 1) {
                //是第一步
                logger.info("连线form:{},to:{}", "startEvent",
                    "parallelGateway-fork-" + stepNumber + "-" + (stepNumber - 1));
                process.addFlowElement(createSequenceFlow("startEvent",
                    "parallelGateway-fork-" + stepNumber + "-" + (stepNumber - 1), "开始节点-并行网关-分支" + stepNumber, ""));
            } else {
                //不是第一步
                ActivitiFlowStepDto activitiFlowStepDto = stepDtoList.get(0);
                if (NODE_TYPE_SING.equals(activitiFlowStepDto.getParentNodeType())) {
                    //上一步是会签节点
                    logger.info("连线form:{},to:{}", "parallelGateway-join-" + (stepNumber - 1) + "-" + stepNumber,
                        "parallelGateway-fork-" + stepNumber + "-" + (stepNumber - 1));
                    process.addFlowElement(createSequenceFlow("parallelGateway-join-" + (stepNumber - 1) + "-" + stepNumber,
                        "parallelGateway-fork-" + stepNumber + "-" + (stepNumber - 1), "并行网关-汇聚到并行网关-分支" + stepNumber,
                        ""));
                } else {
                    //不是会签节点
                    logger.info("连线form:{},to:{}", "userTask-" + (stepNumber - 1) + "-0",
                        "parallelGateway-fork-" + stepNumber + "-" + (stepNumber - 1));
                    process.addFlowElement(createSequenceFlow("userTask-" + (stepNumber - 1) + "-0",
                        "parallelGateway-fork-" + stepNumber + "-" + (stepNumber - 1), "上一审核节点到并行网关-分支" + stepNumber,
                        ""));
                }
            }
            //并行网关-分支和会签用户连线，会签用户和并行网关-汇聚连线
            for (int i = 0; i < stepDtoList.size(); i++) {
                ActivitiFlowStepDto activitiFlowStepDto = stepDtoList.get(i);
                if (NODE_ACTIVITI_TYPE_ROLE.equals(activitiFlowStepDto.getNodeActionType())) {
                    //角色的执行人
                    List<String> userList = Lists.newArrayList();
                    for (int u = 0; u < userList.size(); u++) {
                        logger.info("连线form:{},to:{}", "parallelGateway-fork-" + stepNumber + "-" + (stepNumber - 1),
                            "userTask" + stepNumber + "-" + i + "-" + u);
                        process.addFlowElement(createSequenceFlow("parallelGateway-fork-" + stepNumber + "-" + (stepNumber - 1),
                            "userTask-" + stepNumber + "-" + i + "-" + u,
                            "并行网关-分支到会签用户" + stepNumber + "-" + i + "-" + u, ""));
                        logger.info("连线form:{},to:{}", "userTask-" + stepNumber + "-" + i + "-" + u, "parallelGateway" +
                            "-" +
                            "-join-" + stepNumber + "-" + (stepNumber + 1));
                        process.addFlowElement(createSequenceFlow("userTask-" + stepNumber + "-" + i + "-" + u,
                            "parallelGateway-join-" + stepNumber + "-" + (stepNumber + 1),
                            "会签用户到并行网关-汇聚" + stepNumber + "-" + i + "-" + u, ""));
                    }
                } else {
                    //普通的执行人
                    logger.info("连线form:{},to:{}", "parallelGateway-fork-" + stepNumber + "-" + (stepNumber - 1),
                        "userTask-" + stepNumber + "-" + i);
                    process.addFlowElement(createSequenceFlow("parallelGateway-fork-" + stepNumber + "-" + (stepNumber - 1),
                        "userTask-" + stepNumber + "-" + i, "并行网关-分支到会签用户" + stepNumber + "-" + i, ""));
                    logger.info("连线form:{},to:{}", "userTask-" + stepNumber + "-" + i,
                        "parallelGateway-join-" + stepNumber + "-" + (stepNumber + 1));
                    process.addFlowElement(createSequenceFlow("userTask-" + stepNumber + "-" + i, "parallelGateway" +
                        "-join-"
                        + stepNumber + "-" + (stepNumber + 1), "会签用户到并行网关-汇聚" + stepNumber + "-" + i, ""));
                }
            }
            //判断是不是最后一步
            if (stepNumber.equals(totalStepNumber)) {
                logger.info("连线form:{},to:{}", "parallelGateway-join-" + stepNumber + "-" + (stepNumber + 1),
                    "endEvent");
                process.addFlowElement(createSequenceFlow("parallelGateway-join-" + stepNumber + "-" + (stepNumber + 1),
                    "endEvent", "并行网关-汇聚到结束节点", ""));
            }
        });
    }

    /**
     * 构建普通节点的连线.
     *
     * @param process         the process
     * @param stepMap         the step map
     * @param totalStepNumber the total step number
     */
    private void buildNormalSequenceFlow(Process process, Map<Integer, List<ActivitiFlowStepDto>> stepMap,
                                         Integer totalStepNumber) {
        Optional.ofNullable(stepMap).orElse(Maps.newConcurrentMap()).entrySet().forEach(entry -> {
            Integer stepNumber = entry.getKey();
            List<ActivitiFlowStepDto> stepDtoList = entry.getValue();

            if (stepNumber == 1) {
                //是第一步
                logger.info("连线form:{},to:{}", "startEvent", "userTask-" + stepNumber + "-0");
                process.addFlowElement(createSequenceFlow("startEvent",
                    "userTask-" + stepNumber + "-0", "开始节点-审核节点" + stepNumber + "-0", ""));
            } else {
                //不是第一步
                ActivitiFlowStepDto activitiFlowStepDto = stepDtoList.get(0);
                if (NODE_TYPE_SING.equals(activitiFlowStepDto.getParentNodeType())) {
                    //上一步是会签节点
                    logger.info("连线form:{},to:{}", "parallelGateway-join-" + (stepNumber - 1) + "-" + stepNumber,
                        "userTask-" + stepNumber + "-0");
                    process.addFlowElement(createSequenceFlow("parallelGateway-join-" + (stepNumber - 1) + "-" + stepNumber,
                        "userTask-" + stepNumber + "-0", "并行网关-汇聚到并行网关-分支" + stepNumber + "-0", ""));
                } else {
                    //不是会签节点
                    logger.info("连线form:{},to:{}", "userTask-" + (stepNumber - 1) + "-0", "userTask-" + stepNumber +
                        "-0");
                    process.addFlowElement(createSequenceFlow("userTask-" + (stepNumber - 1) + "-0",
                        "userTask-" + stepNumber + "-0", "上一审核节点到并行网关-分支" + stepNumber + "-0", ""));
                }
            }
            //判断是不是最后一步
            if (stepNumber.equals(totalStepNumber)) {
                logger.info("连线form:{},to:{}", "userTask-" + stepNumber + "-0", "endEvent");
                process.addFlowElement(createSequenceFlow("userTask-" + stepNumber + "-0", "endEvent", "并行网关-汇聚到结束节点",
                    ""));
            }
        });
    }

    /**
     * 处理节点的父类节点.
     *
     * @param stepDtoList the step dto list
     * @return the list
     */
    private List<ActivitiFlowStepDto> makeNodeParentType(List<ActivitiFlowStepDto> stepDtoList) {
        //按照步骤顺序进行排序
        List<ActivitiFlowStepDto> orderStepDtoList =
            Optional.ofNullable(stepDtoList).orElse(Lists.newArrayList()).stream().filter(Objects::nonNull)
                .sorted(Comparator.comparing(ActivitiFlowStepDto::getSerialNumber)).collect(Collectors.toList());
        for (int i = 1, j = orderStepDtoList.size(); i < j; i++) {
            ActivitiFlowStepDto activitiFlowStepDto = orderStepDtoList.get(i);
            ActivitiFlowStepDto parentActivitiFlowStepDto = orderStepDtoList.get(i - 1);
            activitiFlowStepDto.setParentNodeType(parentActivitiFlowStepDto.getNodeType());
        }
        return orderStepDtoList;
    }


}
