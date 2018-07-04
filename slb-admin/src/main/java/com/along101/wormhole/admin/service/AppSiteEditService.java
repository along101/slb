package com.along101.wormhole.admin.service;

import com.along.wormhole.admin.common.exception.SlbServiceException;
import com.along.wormhole.admin.dto.appsite.AppSiteDto;
import com.along.wormhole.admin.dto.appsite.AppSiteServerDto;
import com.along.wormhole.admin.manager.appsite.AppSiteEditManager;
import com.along.wormhole.admin.manager.appsite.AppSiteManager;
import com.along.wormhole.admin.manager.appsite.AppSiteServerManager;
import com.along.wormhole.admin.manager.nginx.NginxConfManager;
import com.along101.wormhole.admin.common.exception.SlbServiceException;
import com.along101.wormhole.admin.dto.appsite.AppSiteDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteServerDto;
import com.along101.wormhole.admin.manager.appsite.AppSiteEditManager;
import com.along101.wormhole.admin.manager.appsite.AppSiteManager;
import com.along101.wormhole.admin.manager.appsite.AppSiteServerManager;
import com.along101.wormhole.admin.manager.nginx.NginxConfManager;
import com.google.common.base.Preconditions;
import com.along101.wormhole.admin.common.exception.SlbServiceException;
import com.along101.wormhole.admin.dto.appsite.AppSiteDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteServerDto;
import com.along101.wormhole.admin.manager.appsite.AppSiteEditManager;
import com.along101.wormhole.admin.manager.appsite.AppSiteManager;
import com.along101.wormhole.admin.manager.appsite.AppSiteServerManager;
import com.along101.wormhole.admin.manager.nginx.NginxConfManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Created by yinzuolong on 2017/9/6.
 */
@Service
public class AppSiteEditService {
    @Autowired
    private AppSiteManager appSiteManager;
    @Autowired
    private AppSiteServerManager appSiteServerManager;
    @Autowired
    private AppSiteEditManager appSiteEditManager;
    @Autowired
    private NginxConfManager nginxConfManager;
    @Autowired
    private AppSiteService appSiteService;

    //根据id查询
    public Optional<AppSiteDto> getById(Long appSiteId) {
        Preconditions.checkNotNull(appSiteId, "appSiteId can not be null.");
        return appSiteEditManager.getByAppSiteId(appSiteId);
    }

    //修改
    @Transactional
    public AppSiteDto updateAppSite(AppSiteDto appSiteDto) {
        Preconditions.checkNotNull(appSiteDto, "appSiteDto object can not be null.");
        return appSiteEditManager.update(appSiteDto);
    }

    //修改servers
    @Transactional
    public AppSiteDto updateServers(Long appSiteId, List<AppSiteServerDto> serverDtos) {
        Preconditions.checkNotNull(appSiteId, "appSiteId can not be null.");
        Preconditions.checkNotNull(serverDtos, "appSiteServers list can not be null.");
        Optional<AppSiteDto> optional = appSiteEditManager.updateServers(appSiteId, serverDtos);
        if (!optional.isPresent()) {
            throw SlbServiceException.newException("appSite id='%s' not exist.", appSiteId);
        }
        return optional.get();
    }

    // 还原
    @Transactional
    public AppSiteDto revertOnline(Long appSiteId) {
        Preconditions.checkNotNull(appSiteId, "appSiteId can not be null.");
        Optional<AppSiteDto> optional = appSiteManager.getById(appSiteId);
        if (!optional.isPresent()) {
            throw SlbServiceException.newException("appSite id='%s' not exist.", appSiteId);
        }
        AppSiteDto appSiteDto = optional.get();
        appSiteDto.setAppSiteServers(appSiteServerManager.getByAppSiteId(appSiteId));
        appSiteDto.setOfflineVersion(null);
        return appSiteEditManager.update(appSiteDto);
    }

    public String previewEditConfig(Long appSiteId) {
        Optional<AppSiteDto> optional = getById(appSiteId);
        Optional<String> config = optional.map(appSiteDto -> nginxConfManager.getNginxConf(appSiteDto).orElse(""));
        return config.orElse("");
    }
}
