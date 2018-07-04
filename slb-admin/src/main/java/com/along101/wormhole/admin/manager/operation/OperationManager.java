package com.along101.wormhole.admin.manager.operation;

import com.along.wormhole.admin.common.constant.OperationEnum;
import com.along.wormhole.admin.common.constant.TaskStatusEnum;
import com.along.wormhole.admin.common.exception.SlbServiceException;
import com.along.wormhole.admin.dto.appsite.AppSiteArchiveDto;
import com.along.wormhole.admin.dto.appsite.AppSiteDto;
import com.along.wormhole.admin.dto.appsite.AppSiteServerDto;
import com.along.wormhole.admin.dto.task.TaskDto;
import com.along.wormhole.admin.manager.appsite.AppSiteServerManager;
import com.along.wormhole.admin.manager.nginx.NginxConfManager;
import com.along.wormhole.admin.manager.task.TaskManager;
import com.along.wormhole.admin.service.converter.AppSiteServerConverter;
import com.along101.wormhole.admin.common.constant.OperationEnum;
import com.along101.wormhole.admin.common.constant.TaskStatusEnum;
import com.along101.wormhole.admin.common.exception.SlbServiceException;
import com.along101.wormhole.admin.dto.appsite.AppSiteArchiveDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteServerDto;
import com.along101.wormhole.admin.dto.task.TaskDto;
import com.along101.wormhole.admin.manager.appsite.AppSiteServerManager;
import com.along101.wormhole.admin.manager.nginx.NginxConfManager;
import com.along101.wormhole.admin.manager.task.TaskManager;
import com.along101.wormhole.admin.service.converter.AppSiteServerConverter;
import com.along101.wormhole.admin.common.constant.OperationEnum;
import com.along101.wormhole.admin.common.constant.TaskStatusEnum;
import com.along101.wormhole.admin.common.exception.SlbServiceException;
import com.along101.wormhole.admin.dto.appsite.AppSiteArchiveDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteServerDto;
import com.along101.wormhole.admin.dto.task.TaskDto;
import com.along101.wormhole.admin.manager.appsite.AppSiteArchiveManager;
import com.along101.wormhole.admin.manager.appsite.AppSiteManager;
import com.along101.wormhole.admin.manager.appsite.AppSiteServerManager;
import com.along101.wormhole.admin.manager.nginx.NginxConfManager;
import com.along101.wormhole.admin.manager.task.TaskManager;
import com.along101.wormhole.admin.service.converter.AppSiteServerConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by yinzuolong on 2017/9/6.
 */
@Component
public class OperationManager {

    @Autowired
    private AppSiteManager appSiteManager;
    @Autowired
    private AppSiteServerManager appSiteServerManager;
    @Autowired
    private AppSiteArchiveManager archiveManager;
    @Autowired
    private TaskManager taskManager;
    @Autowired
    private NginxConfManager nginxConfManager;

    //站点部署
    @Transactional
    public TaskDto deploySite(Long appSiteId) {
        Optional<AppSiteDto> optional = getAppSiteDto(appSiteId);
        if (!optional.isPresent()) {
            throw SlbServiceException.newException("appSite id='%s' not found.", appSiteId);
        }
        return deploySite(optional.get());
    }

    //站点部署，与归档配置比较，不相同需要新增Task和归档
    @Transactional
    public TaskDto deploySite(AppSiteDto appSiteDto) {
        //检查nginx配置变化，没有变化直接返回
        if (!checkNginxConfigChanged(appSiteDto)) {
            return TaskDto.builder().status(TaskStatusEnum.SUCCESS.getCode())
                    .msg(String.format("appSite id='%s' nginx conf not changed, no need for deploy", appSiteDto.getId())).build();
        }
        //归档
        archiveManager.doArchive(appSiteDto);
        //插入task
        return taskManager.addTask(appSiteDto, OperationEnum.DEPLOY_APPSITE);
    }

    //server操作
    @Transactional
    public TaskDto operateServers(Long appSiteId, List<Long> serverIds, OperationEnum operation) {
        if (operation.getOperationOffset() == null) {
            throw SlbServiceException.newException("operation server error , can not be '%s'", operation);
        }
        Optional<AppSiteDto> optional = getAppSiteDto(appSiteId);
        if (!optional.isPresent()) {
            throw SlbServiceException.newException("appSite id='%s' not found.", appSiteId);
        }
        AppSiteDto appSiteDto = optional.get();
        //设置槽位状态和最终状态
        String operationOffset = operation.getOperationOffset().getOperation();
        int operationStatus = operation.getOperationType().getCode();

        List<AppSiteServerDto> updateServerDtos = appSiteDto.getAppSiteServers().stream()
                .filter(serverDto -> serverIds.contains(serverDto.getId()))
                .peek(serverDto -> {
                    serverDto.getStatusDetail().put(operationOffset, operationStatus);
                    serverDto.setStatus(AppSiteServerConverter.getServerFinalStatus(serverDto.getStatusDetail()));
                }).collect(Collectors.toList());
        //更新server
        appSiteServerManager.updateServers(appSiteId, updateServerDtos);
        return deploySite(appSiteDto);
    }

    //检查nginx配置变化
    private boolean checkNginxConfigChanged(AppSiteDto appSiteDto) {
        Optional<AppSiteDto> archiveAppSiteDto = getFromArchive(appSiteDto.getId());
        String archiveConf = archiveAppSiteDto.map(dto -> nginxConfManager.getNginxConf(dto).orElse("")).orElse("");
        String appSiteConf = nginxConfManager.getNginxConf(appSiteDto).orElse("");
        return !archiveConf.equals(appSiteConf);
    }

    private Optional<AppSiteDto> getFromArchive(Long appSiteId) {
        Optional<AppSiteArchiveDto> archive = archiveManager.getOnlineArchive(appSiteId);
        return archive.map(AppSiteArchiveDto::getAppSiteDto);
    }

    private Optional<AppSiteDto> getAppSiteDto(Long appSiteId) {
        Optional<AppSiteDto> optional = appSiteManager.getById(appSiteId);
        optional.ifPresent(appSiteDto -> appSiteDto.setAppSiteServers(appSiteServerManager.getByAppSiteId(appSiteId)));
        return optional;

    }
}
