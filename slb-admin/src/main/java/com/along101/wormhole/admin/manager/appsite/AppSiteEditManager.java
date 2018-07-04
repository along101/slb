package com.along101.wormhole.admin.manager.appsite;

import com.alibaba.fastjson.JSON;
import com.along.wormhole.admin.dao.AppSiteEditRepository;
import com.along.wormhole.admin.dto.appsite.AppSiteDto;
import com.along.wormhole.admin.dto.appsite.AppSiteServerDto;
import com.along.wormhole.admin.entity.AppSiteEditEntity;
import com.along101.wormhole.admin.dao.AppSiteEditRepository;
import com.along101.wormhole.admin.dto.appsite.AppSiteDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteServerDto;
import com.along101.wormhole.admin.entity.AppSiteEditEntity;
import com.along101.wormhole.admin.dao.AppSiteEditRepository;
import com.along101.wormhole.admin.dto.appsite.AppSiteDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteServerDto;
import com.along101.wormhole.admin.entity.AppSiteEditEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Created by yinzuolong on 2017/7/27.
 */
@Component
public class AppSiteEditManager {
    @Autowired
    private AppSiteEditRepository editRepository;
    @Autowired
    private AppSiteManager appSiteManager;
    @Autowired
    private AppSiteServerManager appSiteServerManager;

    public AppSiteEditEntity convert(AppSiteDto appSiteDto) {
        AppSiteEditEntity entity = new AppSiteEditEntity();
        entity.setAppSiteId(appSiteDto.getId());
        entity.setSlbId(appSiteDto.getSlbId());
        entity.setAppId(appSiteDto.getAppId());
        entity.setDomain(appSiteDto.getDomain());
        entity.setPort(appSiteDto.getPort());
        entity.setOnlineVersion(appSiteDto.getOnlineVersion());
        entity.setOfflineVersion(appSiteDto.getOfflineVersion());
        entity.setAppSiteContent(JSON.toJSONString(appSiteDto));
        return entity;
    }

    private void save(AppSiteDto appSiteDto) {
        AppSiteEditEntity entity = convert(appSiteDto);
        editRepository.save(entity);
    }

    //获取编辑版本
    public Optional<AppSiteDto> getByAppSiteId(Long appSiteId) {
        AppSiteEditEntity entity = editRepository.getByAppSiteId(appSiteId);
        if (entity == null) {
            Optional<AppSiteDto> optional = appSiteManager.getById(appSiteId);
            optional.ifPresent(appSiteDto -> appSiteDto.setAppSiteServers(appSiteServerManager.getByAppSiteId(appSiteId)));
            return optional;
        }
        AppSiteDto appSiteDto = JSON.parseObject(entity.getAppSiteContent(), AppSiteDto.class);
        return Optional.of(appSiteDto);
    }

    //更新，线下版本累加
    @Transactional
    public AppSiteDto update(AppSiteDto appSiteDto) {
        long version = appSiteDto.getOfflineVersion() == null ? 0 : appSiteDto.getOfflineVersion()+1;
        appSiteDto.setOfflineVersion(version);
        save(appSiteDto);
        return appSiteDto;
    }

    //更新servers
    public Optional<AppSiteDto> updateServers(Long appSiteId, List<AppSiteServerDto> servers) {
        Optional<AppSiteDto> optional = getByAppSiteId(appSiteId);
        optional.ifPresent(appSiteDto -> {
            appSiteDto.setAppSiteServers(servers);
            update(appSiteDto);
        });
        return optional;
    }

}
