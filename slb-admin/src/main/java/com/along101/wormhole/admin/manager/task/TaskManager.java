package com.along101.wormhole.admin.manager.task;

import com.along.wormhole.admin.common.constant.OperationEnum;
import com.along.wormhole.admin.common.constant.TaskStatusEnum;
import com.along.wormhole.admin.common.utils.ConvertUtils;
import com.along.wormhole.admin.common.utils.DomainPortUtils;
import com.along.wormhole.admin.dao.AgentTaskResultRepository;
import com.along.wormhole.admin.dao.SlbServerRepository;
import com.along.wormhole.admin.dao.TaskRepository;
import com.along.wormhole.admin.dto.appsite.AppSiteDto;
import com.along.wormhole.admin.dto.task.TaskDto;
import com.along.wormhole.admin.entity.AgentTaskResultEntity;
import com.along.wormhole.admin.entity.SlbServerEntity;
import com.along.wormhole.admin.entity.TaskEntity;
import com.along101.wormhole.admin.common.constant.OperationEnum;
import com.along101.wormhole.admin.common.constant.TaskStatusEnum;
import com.along101.wormhole.admin.common.utils.ConvertUtils;
import com.along101.wormhole.admin.common.utils.DomainPortUtils;
import com.along101.wormhole.admin.dao.AgentTaskResultRepository;
import com.along101.wormhole.admin.dao.SlbServerRepository;
import com.along101.wormhole.admin.dao.TaskRepository;
import com.along101.wormhole.admin.dto.appsite.AppSiteDto;
import com.along101.wormhole.admin.dto.task.TaskDto;
import com.along101.wormhole.admin.entity.AgentTaskResultEntity;
import com.along101.wormhole.admin.entity.SlbServerEntity;
import com.along101.wormhole.admin.entity.TaskEntity;
import com.along101.wormhole.admin.common.constant.OperationEnum;
import com.along101.wormhole.admin.common.constant.TaskStatusEnum;
import com.along101.wormhole.admin.dao.AgentTaskResultRepository;
import com.along101.wormhole.admin.dao.SlbServerRepository;
import com.along101.wormhole.admin.dao.TaskRepository;
import com.along101.wormhole.admin.dto.appsite.AppSiteDto;
import com.along101.wormhole.admin.dto.task.TaskDto;
import com.along101.wormhole.admin.entity.AgentTaskResultEntity;
import com.along101.wormhole.admin.entity.SlbServerEntity;
import com.along101.wormhole.admin.entity.TaskEntity;
import com.along101.wormhole.admin.common.utils.ConvertUtils;
import com.along101.wormhole.admin.common.utils.DomainPortUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by yinzuolong on 2017/7/27.
 */
@Component
public class TaskManager {

    @Setter
    @Getter
    @Value("${slb.task.overtime}")
    private int overtime = 15;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private SlbServerRepository slbServerRepository;
    @Autowired
    private AgentTaskResultRepository agentTaskResultRepository;

    public TaskDto convert(AppSiteDto appSiteDto, OperationEnum operation) {
        TaskDto taskDto = new TaskDto();
        taskDto.setSlbId(appSiteDto.getSlbId());
        taskDto.setAppSiteId(appSiteDto.getId());
        taskDto.setAppSiteVersion(appSiteDto.getOnlineVersion());
        taskDto.setDomainPort(DomainPortUtils.getDomainPort(appSiteDto.getDomain(), appSiteDto.getPort()));
        taskDto.setAppId(appSiteDto.getAppId());
        taskDto.setOpsType(operation);
        return taskDto;
    }

    //增加task
    @Transactional
    public TaskDto addTask(AppSiteDto appSiteDto, OperationEnum operation) {
        TaskDto taskDto = convert(appSiteDto, operation);
        TaskEntity taskEntity = ConvertUtils.convert(taskDto, TaskEntity.class);
        TaskEntity newTaskEntity = taskRepository.save(taskEntity);
        ConvertUtils.convert(newTaskEntity, taskDto);
        taskDto.setStatus(TaskStatusEnum.RUNNING.getCode());
        return taskDto;
    }

    //查询任务，不处理任务状态
    public TaskDto getTask(Long taskId) {
        TaskEntity taskEntity = taskRepository.findOne(taskId);
        if (taskEntity == null) return null;
        return ConvertUtils.convert(taskEntity, TaskDto.class);
    }

    //查询任务，不处理任务状态
    public List<TaskDto> getTasksByAppSiteId(Long siteId) {
        List<TaskEntity> taskEntities = taskRepository.findTasksBySiteId(siteId);
        return ConvertUtils.convert(taskEntities, TaskDto.class);
    }

    //处理任务状态，指定时间内，如果全部成功则成功，如果有失败则失败，否则为执行中
    @Transactional
    public TaskStatusEnum processStatus(TaskDto taskDto) {
        if (taskDto == null) {
            return TaskStatusEnum.INVALID;
        }
        //如果不是执行中状态，直接返回
        if (taskDto.getStatus() != null && taskDto.getStatus() != TaskStatusEnum.RUNNING.getCode()) {
            Optional<TaskStatusEnum> status = TaskStatusEnum.findByCode(taskDto.getStatus());
            return status.orElse(TaskStatusEnum.INVALID);
        }
        //计算任务超时内的状态
        Date overtime = new Date(taskDto.getUpdateTime().getTime() + this.overtime * 1000);
        List<AgentTaskResultEntity> taskResults = agentTaskResultRepository.getTaskResultBeforeOvertime(taskDto.getId(), overtime);
        //分组
        Map<String, List<AgentTaskResultEntity>> agentStatusMap = taskResults.stream()
                .collect(Collectors.groupingBy(AgentTaskResultEntity::getAgentIp));
        //所有的agent
        List<SlbServerEntity> servers = slbServerRepository.findBySlbId(taskDto.getSlbId());
        List<String> allAgents = servers.stream().map(SlbServerEntity::getIp).collect(Collectors.toList());
        final int[] failedCount = new int[1];
        final int[] successCount = new int[1];
        allAgents.forEach(ip -> {
            List<AgentTaskResultEntity> agentTaskResults = agentStatusMap.get(ip);
            if (CollectionUtils.isEmpty(agentTaskResults)) return;
            //求最大
            AgentTaskResultEntity taskResult = Collections.max(agentTaskResults, (o1, o2) -> (int) (o1.getTaskId() - o2.getTaskId()));
            if (taskResult.getStatus() == TaskStatusEnum.FAILED.getCode()) {
                failedCount[0] = failedCount[0] + 1;
            } else if (taskResult.getStatus() == TaskStatusEnum.SUCCESS.getCode()) {
                successCount[0] = successCount[0] + 1;
            }
        });
        TaskStatusEnum result;
        //顺序：1有失败的算失败，2全部成功算成功，3没有执行完且超时算超时，4没有执行完没有超时算执行中
        if (failedCount[0] > 0) result = TaskStatusEnum.FAILED;
        else if (successCount[0] == allAgents.size()) result = TaskStatusEnum.SUCCESS;
        else if (overtime.getTime() < System.currentTimeMillis()) result = TaskStatusEnum.OVERTIME;
        else result = TaskStatusEnum.RUNNING;
        //不是执行中，更新状态
        if (result.getCode() != TaskStatusEnum.RUNNING.getCode()) {
            taskRepository.updateTaskStatus(taskDto.getId(), result.getCode());
        }
        return result;
    }

    public List<TaskDto> getMaxTaskGroupByAppSite(Long slbId, Long taskId) {
        List<TaskEntity> entities = taskRepository.findMaxTaskGroupByAppSite(slbId, taskId);
        return ConvertUtils.convert(entities, TaskDto.class);
    }

    public Optional<TaskDto> getMaxTaskBySiteId(Long appSiteId) {
        TaskEntity entity = taskRepository.findMaxTaskBySiteId(appSiteId);
        return Optional.ofNullable(entity).map(o -> ConvertUtils.convert(o, TaskDto.class));
    }
}
