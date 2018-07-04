package com.along101.wormhole.admin.service;

import com.along.wormhole.admin.common.constant.OnlineStatusEnum;
import com.along.wormhole.admin.common.constant.OperationEnum;
import com.along.wormhole.admin.common.exception.SlbServiceException;
import com.along.wormhole.admin.dto.appsite.AppSiteDto;
import com.along.wormhole.admin.dto.appsite.AppSiteServerDto;
import com.along.wormhole.admin.dto.task.TaskDto;
import com.along.wormhole.admin.manager.appsite.AppSiteManager;
import com.along.wormhole.admin.manager.appsite.AppSiteServerManager;
import com.along.wormhole.admin.manager.operation.OperationManager;
import com.along101.wormhole.admin.common.constant.OnlineStatusEnum;
import com.along101.wormhole.admin.common.constant.OperationEnum;
import com.along101.wormhole.admin.common.exception.SlbServiceException;
import com.along101.wormhole.admin.dto.appsite.AppSiteDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteServerDto;
import com.along101.wormhole.admin.dto.task.TaskDto;
import com.along101.wormhole.admin.manager.appsite.AppSiteManager;
import com.along101.wormhole.admin.manager.appsite.AppSiteServerManager;
import com.along101.wormhole.admin.manager.operation.OperationManager;
import com.google.common.base.Preconditions;
import com.along101.wormhole.admin.common.constant.OnlineStatusEnum;
import com.along101.wormhole.admin.common.constant.OperationEnum;
import com.along101.wormhole.admin.common.exception.SlbServiceException;
import com.along101.wormhole.admin.dto.appsite.AppSiteDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteServerDto;
import com.along101.wormhole.admin.dto.task.TaskDto;
import com.along101.wormhole.admin.manager.appsite.AppSiteManager;
import com.along101.wormhole.admin.manager.appsite.AppSiteServerManager;
import com.along101.wormhole.admin.manager.operation.OperationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

/**
 * Created by yinzuolong on 2017/7/11.
 */
@Service
@Slf4j
public class OperationService {

    @Autowired
    private AppSiteManager appSiteManager;
    @Autowired
    private AppSiteServerManager appSiteServerManager;
    @Autowired
    private OperationManager operationManager;
    //上线操作
    @Transactional
    public TaskDto online(Long appSiteId) {
        return operateAppSite(appSiteId, OnlineStatusEnum.ONLINE);
    }

    //下线操作
    @Transactional
    public TaskDto offline(Long appSiteId) {
        return operateAppSite(appSiteId, OnlineStatusEnum.OFFLINE);
    }

    private TaskDto operateAppSite(Long appSiteId, OnlineStatusEnum statusEnum) {
        Preconditions.checkNotNull(appSiteId, "appSiteId can not be null");
        appSiteManager.increaseVersion(appSiteId);
        Optional<AppSiteDto> optional = appSiteManager.getById(appSiteId);
        if (!optional.isPresent()) {
            throw SlbServiceException.newException("appSite id='%s' not exist.", appSiteId);
        }
        AppSiteDto appSiteDto = optional.get();
        appSiteDto.setAppSiteServers(appSiteServerManager.getByAppSiteId(appSiteId));
        appSiteDto.setStatus(statusEnum.getValue());
        appSiteManager.updateStatus(appSiteId, statusEnum);

        return operationManager.deploySite(appSiteDto);
    }

    //server操作
    @Transactional
    public TaskDto operateServers(Long appSiteId, List<Long> serverIds, OperationEnum operationEnum) {
        Preconditions.checkNotNull(appSiteId, "appSiteId can not be null");
        Preconditions.checkArgument(!CollectionUtils.isEmpty(serverIds), "serverIds is empty");
        appSiteManager.increaseVersion(appSiteId);
        return operationManager.operateServers(appSiteId, serverIds, operationEnum);
    }

    @Transactional
    public TaskDto updateAppSiteServers(Long appSiteId, List<AppSiteServerDto> appSiteServerDtos) {
        Preconditions.checkNotNull(appSiteId, "appSiteId can not be null");
        Preconditions.checkNotNull(appSiteServerDtos, "appSiteServerDtos can not be null");
        //增加版本
        appSiteManager.increaseVersion(appSiteId);
        Optional<AppSiteDto> optional = appSiteManager.getById(appSiteId);
        if (!optional.isPresent()) {
            throw SlbServiceException.newException("appSite id='%s' not exist.", appSiteId);
        }
        appSiteServerManager.updateServers(appSiteId, appSiteServerDtos);
        //修改后部署
        return operationManager.deploySite(appSiteId);
    }

}
