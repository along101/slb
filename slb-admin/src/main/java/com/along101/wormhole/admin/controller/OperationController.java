package com.along101.wormhole.admin.controller;

import com.along101.wormhole.admin.common.constant.OperationEnum;
import com.along101.wormhole.admin.dto.Response;
import com.along101.wormhole.admin.dto.task.TaskDto;
import com.along101.wormhole.admin.service.OperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by yinzuolong on 2017/7/11.
 */
@RestController
@RequestMapping("/web/operation")
@Slf4j
public class OperationController {

    @Autowired
    private OperationService operationService;

    //发布站点
    @RequestMapping(value = "/deploy/{siteId}", method = RequestMethod.PUT)
    public Response<TaskDto> online(@PathVariable("siteId") Long siteId) {
        TaskDto taskDto = operationService.online(siteId);
        return Response.success(taskDto);
    }

    //下线站点
    @RequestMapping(value = "/undeploy/{siteId}", method = RequestMethod.PUT)
    public Response<TaskDto> offline(@PathVariable("siteId") Long siteId) {
        TaskDto taskDto = operationService.offline(siteId);
        return Response.success(taskDto);
    }

    //拉入
    @RequestMapping(value = "/pullIn/{siteId}", method = RequestMethod.PUT)
    public Response<TaskDto> pullIn(@PathVariable("siteId") Long siteId, @RequestBody List<Long> serverIds) {
        TaskDto taskDto = operationService.operateServers(siteId, serverIds, OperationEnum.PULL_IN);
        return Response.success(taskDto);
    }

    //拉出
    @RequestMapping(value = "/pullOut/{siteId}", method = RequestMethod.PUT)
    public Response<TaskDto> pullOut(@PathVariable("siteId") Long siteId, @RequestBody List<Long> serverIds) {
        TaskDto taskDto = operationService.operateServers(siteId, serverIds, OperationEnum.PULL_OUT);
        return Response.success(taskDto);
    }

    //健康检查上线
    @RequestMapping(value = "/healthCheckUp/{siteId}", method = RequestMethod.PUT)
    public Response<TaskDto> healthCheckUp(@PathVariable("siteId") Long siteId, @RequestBody List<Long> serverIds) {
        TaskDto taskDto = operationService.operateServers(siteId, serverIds, OperationEnum.HEALTH_CHECK_UP);
        return Response.success(taskDto);
    }

    //健康检查下线
    @RequestMapping(value = "/healthCheckDown/{siteId}", method = RequestMethod.PUT)
    public Response<TaskDto> healthCheckDown(@PathVariable("siteId") Long siteId, @RequestBody List<Long> serverIds) {
        TaskDto taskDto = operationService.operateServers(siteId, serverIds, OperationEnum.HEALTH_CHECK_DOWN);
        return Response.success(taskDto);
    }

    //手工上线
    @RequestMapping(value = "/manualUp/{siteId}", method = RequestMethod.PUT)
    public Response<TaskDto> manualUp(@PathVariable("siteId") Long siteId, @RequestBody List<Long> serverIds) {
        TaskDto taskDto = operationService.operateServers(siteId, serverIds, OperationEnum.MANUAL_UP);
        return Response.success(taskDto);
    }

    //手工下线
    @RequestMapping(value = "/manualDown/{siteId}", method = RequestMethod.PUT)
    public Response<TaskDto> manualDown(@PathVariable("siteId") Long siteId, @RequestBody List<Long> serverIds) {
        TaskDto taskDto = operationService.operateServers(siteId, serverIds, OperationEnum.MANUAL_DOWN);
        return Response.success(taskDto);
    }

}
