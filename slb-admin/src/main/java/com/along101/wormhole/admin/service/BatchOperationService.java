package com.along101.wormhole.admin.service;

import com.along.wormhole.admin.common.constant.OperationEnum;
import com.along.wormhole.admin.common.utils.ConvertUtils;
import com.along.wormhole.admin.dto.appsite.AppSiteServerDto;
import com.along.wormhole.admin.dto.operation.UpdateServersIpPortDto;
import com.along.wormhole.admin.dto.task.TaskDto;
import com.along.wormhole.admin.manager.appsite.AppSiteServerManager;
import com.along101.wormhole.admin.common.constant.OperationEnum;
import com.along101.wormhole.admin.common.utils.ConvertUtils;
import com.along101.wormhole.admin.dto.appsite.AppSiteServerDto;
import com.along101.wormhole.admin.dto.operation.UpdateServersIpPortDto;
import com.along101.wormhole.admin.dto.task.TaskDto;
import com.along101.wormhole.admin.manager.appsite.AppSiteServerManager;
import com.along101.wormhole.admin.common.constant.OperationEnum;
import com.along101.wormhole.admin.common.utils.ConvertUtils;
import com.along101.wormhole.admin.dto.appsite.AppSiteServerDto;
import com.along101.wormhole.admin.dto.operation.UpdateServersIpPortDto;
import com.along101.wormhole.admin.dto.task.TaskDto;
import com.along101.wormhole.admin.manager.appsite.AppSiteServerManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by yinzuolong on 2017/7/11.
 */
@Service
@Slf4j
public class BatchOperationService {

    @Autowired
    private OperationService operationService;
    @Autowired
    private AppSiteServerManager appSiteServerManager;

    public List<TaskDto> operateServers(List<Long> serverIds, OperationEnum operationEnum) {
        List<AppSiteServerDto> serverDtos = appSiteServerManager.getByServerIds(serverIds);
        return operateServerDtos(serverDtos, operationEnum);
    }

    public List<TaskDto> operateByServerNames(List<String> serverNames, OperationEnum operationEnum) {
        List<AppSiteServerDto> serverDtos = appSiteServerManager.getByServerNames(serverNames);
        return operateServerDtos(serverDtos, operationEnum);
    }

    private List<TaskDto> operateServerDtos(List<AppSiteServerDto> serverDtos, OperationEnum operationEnum) {
        //按照appSite分组
        Map<Long, List<AppSiteServerDto>> appSiteGroup = serverDtos.stream()
                .collect(Collectors.groupingBy(AppSiteServerDto::getAppSiteId));
        List<TaskDto> taskDtos = new ArrayList<>();
        appSiteGroup.forEach((appSiteId, appSiteServerDtos) -> {
            try {
                List<Long> ids = appSiteServerDtos.stream().map(AppSiteServerDto::getId).collect(Collectors.toList());
                TaskDto taskDto = operationService.operateServers(appSiteId, ids, operationEnum);
                taskDtos.add(taskDto);
            } catch (Exception e) {
                //TODO 记日志
            }
        });
        return taskDtos;
    }

    //更新server的IP/port
    public List<TaskDto> updateServersIpPort(List<UpdateServersIpPortDto> updateServersIpPortDtos, OperationEnum operationEnum) {
        Map<String, UpdateServersIpPortDto> serverNameMap = updateServersIpPortDtos.stream()
                .collect(Collectors.toMap(UpdateServersIpPortDto::getName, Function.identity()));
        //找出有变更的server
        List<AppSiteServerDto> updatedServerDtos = appSiteServerManager
                .getByServerNames(new ArrayList<>(serverNameMap.keySet()))
                .stream().filter(serverDto -> {
                    AppSiteServerDto beforeUpdate = ConvertUtils.deepClon(serverDto, AppSiteServerDto.class);
                    UpdateServersIpPortDto updateServersIpPortDto = serverNameMap.get(serverDto.getName());
                    ConvertUtils.convert(updateServersIpPortDto, serverDto);
                    serverDto.getStatusDetail().put(operationEnum.getOperationOffset().getOperation(), operationEnum.getOperationType().getCode());
                    return !beforeUpdate.equals(serverDto);
                }).collect(Collectors.toList());
        //按照站点分组操作server
        Map<Long, List<AppSiteServerDto>> appSiteGroup = updatedServerDtos.stream()
                .collect(Collectors.groupingBy(AppSiteServerDto::getAppSiteId));
        List<TaskDto> taskDtos = new ArrayList<>();
        appSiteGroup.forEach((appSiteId, serverDtos) -> {
            try {
                TaskDto taskDto = operationService.updateAppSiteServers(appSiteId, serverDtos);
                taskDtos.add(taskDto);
            } catch (Exception e) {
                //TODO 记日志
            }
        });
        return taskDtos;
    }
}
