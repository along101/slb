package com.along101.wormhole.admin.manager.appsite;

import com.alibaba.fastjson.JSON;
import com.along.wormhole.admin.common.utils.ConvertUtils;
import com.along.wormhole.admin.common.utils.DomainPortUtils;
import com.along.wormhole.admin.dao.AppSiteArchiveRepository;
import com.along.wormhole.admin.dto.appsite.AppSiteArchiveDto;
import com.along.wormhole.admin.dto.appsite.AppSiteDto;
import com.along.wormhole.admin.entity.AppSiteArchiveEntity;
import com.along.wormhole.admin.service.converter.AppSiteArchiveConverter;
import com.along101.wormhole.admin.common.utils.ConvertUtils;
import com.along101.wormhole.admin.common.utils.DomainPortUtils;
import com.along101.wormhole.admin.dao.AppSiteArchiveRepository;
import com.along101.wormhole.admin.dto.appsite.AppSiteArchiveDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteDto;
import com.along101.wormhole.admin.entity.AppSiteArchiveEntity;
import com.along101.wormhole.admin.service.converter.AppSiteArchiveConverter;
import com.along101.wormhole.admin.dao.AppSiteArchiveRepository;
import com.along101.wormhole.admin.dto.appsite.AppSiteArchiveDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteDto;
import com.along101.wormhole.admin.entity.AppSiteArchiveEntity;
import com.along101.wormhole.admin.service.converter.AppSiteArchiveConverter;
import com.along101.wormhole.admin.common.utils.ConvertUtils;
import com.along101.wormhole.admin.common.utils.DomainPortUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by yinzuolong on 2017/7/27.
 */
@Component
public class AppSiteArchiveManager {
    @Autowired
    private AppSiteArchiveRepository archiveRepository;
    @Autowired
    private AppSiteArchiveConverter appSiteArchiveConverter;

    public AppSiteArchiveDto convertFromAppSiteDto(AppSiteDto appSiteDto) {
        AppSiteArchiveDto appSiteArchiveDto = new AppSiteArchiveDto();
        appSiteArchiveDto.setSlbId(appSiteDto.getSlbId());
        appSiteArchiveDto.setAppSiteId(appSiteDto.getId());
        appSiteArchiveDto.setAppId(appSiteDto.getAppId());
        appSiteArchiveDto.setDomain(appSiteDto.getDomain());
        appSiteArchiveDto.setPort(appSiteDto.getPort());
        appSiteArchiveDto.setDomainPort(DomainPortUtils.getDomainPort(appSiteDto.getDomain(), appSiteDto.getPort()));
        appSiteArchiveDto.setAppSiteDto(appSiteDto);
        appSiteArchiveDto.setVersion(appSiteDto.getOnlineVersion());
        return appSiteArchiveDto;
    }

    //合并相同domain的AppSite
    public List<AppSiteDto> combineDomain(AppSiteDto appSiteDto) {
        //找相同域名和端口的站点
        List<AppSiteArchiveEntity> archiveEntities = archiveRepository.getByDomainPort(DomainPortUtils.getDomainPort(appSiteDto.getDomain(), appSiteDto.getPort()));
        List<AppSiteDto> appSiteDtos = archiveEntities.stream()
                .filter(appSiteArchiveEntity -> appSiteDto.getId().longValue() != appSiteArchiveEntity.getAppSiteId())
                .map(appSiteArchiveEntity -> JSON.parseObject(appSiteArchiveEntity.getAppSiteContent(), AppSiteDto.class))
                .collect(Collectors.toList());
        appSiteDtos.add(appSiteDto);
        return appSiteDtos;
    }

    //执行归档
    @Transactional
    public AppSiteArchiveDto doArchive(AppSiteDto appSiteDto) {
        AppSiteArchiveDto appSiteArchiveDto = convertFromAppSiteDto(appSiteDto);
        appSiteArchiveDto.setDomainAppSiteDtos(combineDomain(appSiteDto));
        AppSiteArchiveEntity entity = appSiteArchiveConverter.convertS2T(appSiteArchiveDto, AppSiteArchiveEntity.class);
        entity.setFlag(1);
        archiveRepository.offlineArchive(appSiteDto.getId());
        entity = archiveRepository.save(entity);
        return ConvertUtils.convert(entity, AppSiteArchiveDto.class);
    }

    public List<AppSiteArchiveDto> getCurrentByDomains(List<String> domains) {
        List<AppSiteArchiveEntity> entities = archiveRepository.getCurrentByDomains(domains);
        return ConvertUtils.convert(entities,
                appSiteArchiveEntity -> appSiteArchiveConverter.convertT2S(appSiteArchiveEntity, AppSiteArchiveDto.class));
    }

    public Optional<AppSiteArchiveDto> getOnlineArchive(Long appSiteId) {
        AppSiteArchiveEntity entity = archiveRepository.getOnlineAppSite(appSiteId);
        return Optional.ofNullable(entity).map(o -> appSiteArchiveConverter.convertT2S(o, AppSiteArchiveDto.class));
    }

    // 查询归档信息
    public Optional<AppSiteArchiveDto> getArchiveByVersion(Long appSiteId, Long version) {
        AppSiteArchiveEntity entity = archiveRepository.getArchiveByVersion(appSiteId, version);
        return Optional.ofNullable(entity).map(o -> appSiteArchiveConverter.convertT2S(o, AppSiteArchiveDto.class));
    }
}
