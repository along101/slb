package com.along101.wormhole.admin.api;

import com.along101.wormhole.admin.common.constant.OperationEnum;
import com.along101.wormhole.admin.common.exception.SlbServiceException;
import com.along101.wormhole.admin.dto.Response;
import com.along101.wormhole.admin.dto.operation.UpdateServersIpPortDto;
import com.along101.wormhole.admin.dto.task.TaskDto;
import com.along101.wormhole.admin.service.BatchOperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by yinzuolong on 2017/7/11.
 */
@RestController
@RequestMapping("/api/operation")
@Slf4j
public class OperationApi {
    @Autowired
    private BatchOperationService batchOperationService;

    @RequestMapping(value = "/operateByServerIds", method = RequestMethod.POST)
    public Response<List<TaskDto>> operateServers(@RequestParam("operate") OperationEnum operationEnum,
                                                  @RequestBody List<Long> serverIds) {
        try {
            List<TaskDto> taskDtos = batchOperationService.operateServers(serverIds, operationEnum);
            log.info("success {} servers, serverIds='{}', result='{}'", operationEnum.name(), serverIds, taskDtos);
            return Response.success(taskDtos);
        } catch (SlbServiceException e) {
            log.warn("error {} servers, serverIds='{}'", operationEnum.name(), serverIds, e);
            return Response.error(e);
        } catch (Throwable e) {
            log.warn("error {} servers, serverIds='{}'", operationEnum.name(), serverIds, e);
            return Response.error(e);
        }
    }

    @RequestMapping(value = "/operateByServerNames", method = RequestMethod.POST)
    public Response<List<TaskDto>> operateByServerNames(@RequestParam("operate") OperationEnum operationEnum,
                                                        @RequestBody List<String> serverNames) {
        try {
            List<TaskDto> taskDtos = batchOperationService.operateByServerNames(serverNames, operationEnum);
            log.info("success {} servers, serverNames='{}', result='{}'", operationEnum.name(), serverNames, taskDtos);
            return Response.success(taskDtos);
        } catch (SlbServiceException e) {
            log.warn("error {} servers, serverNames='{}'", operationEnum.name(), serverNames, e);
            return Response.error(e);
        } catch (Throwable e) {
            log.warn("error {} servers, serverNames='{}'", operationEnum.name(), serverNames, e);
            return Response.error(e);
        }
    }

    @RequestMapping(value = "/updateServersIpPort", method = RequestMethod.POST)
    public Response<List<TaskDto>> updateServersIpPort(@RequestParam("operate") OperationEnum operationEnum,
                                                       @RequestBody List<UpdateServersIpPortDto> updateServersIpPortDtos) {
        try {
            List<TaskDto> taskDtos = batchOperationService.updateServersIpPort(updateServersIpPortDtos, operationEnum);
            log.info("success {} servers, updateServersIpPortDtos='{}', result='{}'", operationEnum.name(),
                    updateServersIpPortDtos, taskDtos);
            return Response.success(taskDtos);
        } catch (SlbServiceException e) {
            log.warn("error {} servers, updateServersIpPortDtos='{}'", operationEnum.name(), updateServersIpPortDtos, e);
            return Response.error(e);
        } catch (Throwable e) {
            log.warn("error {} servers, updateServersIpPortDtos='{}'", operationEnum.name(), updateServersIpPortDtos, e);
            return Response.error(e);
        }
    }


}
