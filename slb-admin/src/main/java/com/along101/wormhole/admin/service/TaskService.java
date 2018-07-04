package com.along101.wormhole.admin.service;

import com.along.wormhole.admin.common.constant.TaskStatusEnum;
import com.along.wormhole.admin.dto.task.TaskDto;
import com.along.wormhole.admin.manager.appsite.AppSiteManager;
import com.along.wormhole.admin.manager.task.TaskManager;
import com.along101.wormhole.admin.common.constant.TaskStatusEnum;
import com.along101.wormhole.admin.dto.task.TaskDto;
import com.along101.wormhole.admin.manager.appsite.AppSiteManager;
import com.along101.wormhole.admin.manager.task.TaskManager;
import com.google.common.base.Preconditions;
import com.along101.wormhole.admin.common.constant.OperationEnum;
import com.along101.wormhole.admin.common.constant.TaskStatusEnum;
import com.along101.wormhole.admin.dto.appsite.AppSiteDto;
import com.along101.wormhole.admin.dto.task.TaskDto;
import com.along101.wormhole.admin.manager.appsite.AppSiteManager;
import com.along101.wormhole.admin.manager.task.TaskManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Created by yinzuolong on 2017/7/20.
 */
@Service
public class TaskService {

    @Autowired
    private TaskManager taskManager;
    @Autowired
    private AppSiteManager appSiteManager;

    //查询task
    public TaskDto getTaskById(Long taskId) {
        Preconditions.checkNotNull(taskId, "taskId can not be null ");
        TaskDto taskDto = taskManager.getTask(taskId);
        TaskStatusEnum status = taskManager.processStatus(taskDto);
        taskDto.setStatus(status.getCode());
        return taskDto;
    }

    public List<TaskDto> getTasksBySiteId(Long siteId) {
        return taskManager.getTasksByAppSiteId(siteId);
    }

    public List<TaskDto> getSiteReleaseHistory(Long siteId) {
        // 获取历史发布任务
        List<TaskDto> taskDtos = taskManager.getTasksByAppSiteId(siteId);
        return taskDtos;
    }

}
