package com.along101.wormhole.admin.service.validate;

import com.along.wormhole.admin.service.converter.AppSiteServerConverter;
import com.along101.wormhole.admin.service.converter.AppSiteServerConverter;
import com.along101.wormhole.admin.common.constant.OnlineStatusEnum;
import com.along101.wormhole.admin.common.exception.SlbServiceException;
import com.along101.wormhole.admin.common.utils.DomainPortUtils;
import com.along101.wormhole.admin.dao.AppSiteRepository;
import com.along101.wormhole.admin.dao.AppSiteServerRepository;
import com.along101.wormhole.admin.dao.SlbRepository;
import com.along101.wormhole.admin.dto.appsite.AppSiteDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteServerDto;
import com.along101.wormhole.admin.entity.AppSiteEntity;
import com.along101.wormhole.admin.entity.AppSiteServerEntity;
import com.along101.wormhole.admin.entity.SlbEntity;
import com.along101.wormhole.admin.service.converter.AppSiteServerConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by yinzuolong on 2017/7/18.
 * TODO 修改校验框架
 */
@Component
public class AppSiteValidate {

    @Autowired
    private SlbRepository slbRepository;
    @Autowired
    private AppSiteRepository appSiteRepository;
    @Autowired
    private AppSiteServerRepository appSiteServerRepository;

    //修改时校验appSite存在
    public void validAppSitExist(Long appSiteId) {
        if (appSiteRepository.findOne(appSiteId) == null) {
            throw SlbServiceException.newException("appSite id='%s' not exist.", appSiteId);
        }
    }

    //appSite对应slb不能为空
    public void validSlbExist(AppSiteDto appSiteDto) {
        SlbEntity slbEntity = slbRepository.findOne(appSiteDto.getSlbId());
        if (slbEntity == null) {
            throw SlbServiceException.newException("slb id='%s' not exist.", appSiteDto.getSlbId());
        }
    }

    //slb下的appId唯一
    public void validAppIdUnique(AppSiteDto appSiteDto) {
        AppSiteEntity appSiteEntity = appSiteRepository.findByAppId(appSiteDto.getSlbId(), appSiteDto.getAppId());
        if (null != appSiteEntity
                && appSiteEntity.getSlbId().equals(appSiteDto.getSlbId())
                && !appSiteEntity.getId().equals(appSiteDto.getId())) {
            throw SlbServiceException.newException("appSite appId=%s already exist.", appSiteDto.getAppId());
        }
    }

    //域名/端口/path校验
    public void validDomainPath(AppSiteDto appSiteDto) {
        if (StringUtils.isEmpty(appSiteDto.getDomain())) {
            throw SlbServiceException.newException("appSite domain can not be blank");
        }
        if (appSiteDto.getPort() <= 0) {
            throw SlbServiceException.newException("appSite port shoud greater then 0");
        }
        if (StringUtils.isEmpty(appSiteDto.getPath())) {
            throw SlbServiceException.newException("appSite path can not be blank");
        }
        StringBuilder key = new StringBuilder(appSiteDto.getDomain())
                .append(":")
                .append(appSiteDto.getPort())
                .append(appSiteDto.getPath());
        List<AppSiteEntity> appSiteEntities = appSiteRepository.findByDomainAndPort(appSiteDto.getDomain(), appSiteDto.getPort());
        appSiteEntities.forEach(appSiteEntity -> {
            StringBuilder keyFind = new StringBuilder(appSiteEntity.getDomain())
                    .append(":")
                    .append(appSiteEntity.getPort())
                    .append(appSiteEntity.getPath());
            if (key.toString().equalsIgnoreCase(keyFind.toString())
                    && !appSiteEntity.getId().equals(appSiteDto.getId())) {
                throw SlbServiceException.newException("appSite uri already exist: '%s'.", key);
            }
        });
    }

    //检查站点是否下线
    public void validAppSiteOffline(Long appSiteId) {
        AppSiteEntity entity = appSiteRepository.findOne(appSiteId);
        if (entity.getStatus() != OnlineStatusEnum.OFFLINE.getValue()) {
            throw SlbServiceException.newException("appSite id='%s' is not offline.", appSiteId);
        }
    }

    // 检查站点是否上线
    public void validAppSiteOnline(Long appSiteId) {
        AppSiteEntity entity = appSiteRepository.getOne(appSiteId);
        if (entity.getStatus() != OnlineStatusEnum.ONLINE.getValue()) {
            throw SlbServiceException.newException("appSite id='%s' is not online.", appSiteId);
        }
    }

    // 校验server，ip:port不能重复
    public void validServerIpPortUnique(List<AppSiteServerDto> appSiteServerDtos) {
        Map<String, Long> ipPortMap = appSiteServerDtos.stream()
                .filter(p -> p.getIp() != null && p.getPort() != null)
                .collect(Collectors.groupingBy(o -> DomainPortUtils.getDomainPort(o.getIp(), o.getPort()), Collectors.counting()));
        ipPortMap.forEach((key, value) -> {
            if (value > 1) {
                throw SlbServiceException.newException("ip port '%s' duplicated ", key);
            }
        });
    }

    public void validServerNameUnique(List<AppSiteServerDto> appSiteServerDtos) {
        Map<String, Long> nameMap = appSiteServerDtos.stream().filter(serverDto -> serverDto.getName() != null)
                .collect(Collectors.groupingBy(AppSiteServerDto::getName, Collectors.counting()));
        nameMap.forEach((key, value) -> {
            if (value > 1) {
                throw SlbServiceException.newException("server name '%s' duplicated ", key);
            }
        });
    }

    //检验servers是否下线
    public void validServersOffline(Long appSiteId, List<Long> ids) {
        AppSiteEntity entity = appSiteRepository.getOne(appSiteId);
        //检查appSite下线，直接返回
        if (entity.getStatus() == OnlineStatusEnum.OFFLINE.getValue()) return;
        //检查server下线
        List<AppSiteServerEntity> appSiteServerEntities = appSiteServerRepository.findByAppSiteId(appSiteId);
        List<AppSiteServerEntity> filterList = appSiteServerEntities.stream()
                .filter(serverEntity -> ids.contains(serverEntity.getId())).collect(Collectors.toList());
        validServersOffline(filterList);
    }

    public void validServersOffline(List<AppSiteServerEntity> appSiteServerEntities) {
        appSiteServerEntities.forEach(entity -> {
            if (AppSiteServerConverter.getServerFinalStatus(entity.getStatus()) == OnlineStatusEnum.ONLINE.getValue()) {
                throw SlbServiceException.newException("appSite server is not offine uri='%s:%s'", entity.getIp(), entity.getPort());
            }
        });
    }

    //字段校验
    //修改server校验
}
