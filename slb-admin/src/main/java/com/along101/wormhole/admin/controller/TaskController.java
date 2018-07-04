package com.along101.wormhole.admin.controller;

import com.along101.wormhole.admin.dto.Response;
import com.along101.wormhole.admin.dto.task.TaskDto;
import com.along101.wormhole.admin.service.TaskService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by yinzuolong on 2017/7/12.
 */
@RestController
@RequestMapping("/web/tasks")
@Slf4j
public class TaskController {

    @Autowired
    private TaskService taskService;

    //获取指定站点的发布历史
    @RequestMapping(method = RequestMethod.GET)
    public Response<List<TaskDto>> getSiteReleaseHistory(@RequestParam(value = "siteId") Long siteId) {
        List<TaskDto> tasks = taskService.getSiteReleaseHistory(siteId);
        return Response.success(tasks);
    }

    //获取指定站点的发布历史
    @RequestMapping(value = "/{taskId}", method = RequestMethod.GET)
    public Response<TaskDto> getTaskById(@PathVariable("taskId") Long taskId) {
        TaskDto taskDto = taskService.getTaskById(taskId);
        return Response.success(taskDto);
    }
}
