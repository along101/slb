package com.along101.wormhole.admin.manager.appsite;

import com.along.wormhole.admin.common.utils.ConvertUtils;
import com.along.wormhole.admin.dao.AppSiteServerRepository;
import com.along.wormhole.admin.dto.PageDto;
import com.along.wormhole.admin.dto.appsite.AppSiteServerDto;
import com.along.wormhole.admin.entity.AppSiteServerEntity;
import com.along.wormhole.admin.service.converter.AppSiteServerConverter;
import com.along.wormhole.admin.service.validate.AppSiteValidate;
import com.along101.wormhole.admin.common.utils.ConvertUtils;
import com.along101.wormhole.admin.dao.AppSiteServerRepository;
import com.along101.wormhole.admin.dto.PageDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteServerDto;
import com.along101.wormhole.admin.entity.AppSiteServerEntity;
import com.along101.wormhole.admin.service.converter.AppSiteServerConverter;
import com.along101.wormhole.admin.service.validate.AppSiteValidate;
import com.along101.wormhole.admin.common.utils.ConvertUtils;
import com.along101.wormhole.admin.dao.AppSiteServerRepository;
import com.along101.wormhole.admin.dto.PageDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteServerDto;
import com.along101.wormhole.admin.entity.AppSiteServerEntity;
import com.along101.wormhole.admin.service.converter.AppSiteServerConverter;
import com.along101.wormhole.admin.service.validate.AppSiteValidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yinzuolong on 2017/7/26.
 */
@Component
public class AppSiteServerManager {
    @Autowired
    private AppSiteServerRepository appSiteServerRepository;
    @Autowired
    private AppSiteServerConverter appSiteServerConverter;
    @Autowired
    private AppSiteValidate appSiteValidate;

    /**
     * 比对更新
     *
     * @param appSiteId
     * @param appSiteServerDtos
     */
    @Transactional
    public List<AppSiteServerDto> saveServers(Long appSiteId, List<AppSiteServerDto> appSiteServerDtos) {
        //校验ip:port重复
        appSiteValidate.validServerIpPortUnique(appSiteServerDtos);
        //比对删除
        List<Long> existIds = this.getByAppSiteId(appSiteId).stream().map(AppSiteServerDto::getId).collect(Collectors.toList());
        List<Long> saveIds = appSiteServerDtos.stream().map(AppSiteServerDto::getId).collect(Collectors.toList());
        existIds.removeAll(saveIds);
        if (!CollectionUtils.isEmpty(existIds)) {
            appSiteServerRepository.removeByIds(appSiteId, existIds);
        }
        //保存
        List<AppSiteServerEntity> entities = ConvertUtils.convert(appSiteServerDtos,
                appSiteServerDto -> {
                    AppSiteServerEntity entity = appSiteServerConverter.convertS2T(appSiteServerDto, AppSiteServerEntity.class);
                    entity.setAppSiteId(appSiteId);
                    return entity;
                });
        List<AppSiteServerEntity> newEntities = appSiteServerRepository.save(entities);
        appSiteServerRepository.flush();
        return ConvertUtils.convert(newEntities, entity -> appSiteServerConverter.convertT2S(entity, AppSiteServerDto.class));
    }

    @Transactional
    public List<AppSiteServerDto> addServers(Long appSiteId, List<AppSiteServerDto> appSiteServerDtos) {
        List<AppSiteServerEntity> entities = ConvertUtils.convert(appSiteServerDtos,
                appSiteServerDto -> {
                    AppSiteServerEntity entity = appSiteServerConverter.convertS2T(appSiteServerDto, AppSiteServerEntity.class);
                    entity.setAppSiteId(appSiteId);
                    entity.setId(null);
                    return entity;
                });
        //查询还存在的数据，校验ip port重复
        List<AppSiteServerDto> exists = getByAppSiteId(appSiteId);
        exists.addAll(appSiteServerDtos);
        appSiteValidate.validServerIpPortUnique(exists);
        //保存
        List<AppSiteServerEntity> newEntities = appSiteServerRepository.save(entities);
        appSiteServerRepository.flush();
        return ConvertUtils.convert(newEntities, entity -> appSiteServerConverter.convertT2S(entity, AppSiteServerDto.class));
    }


    @Transactional
    public List<AppSiteServerDto> updateServers(Long appSiteId, List<AppSiteServerDto> appSiteServerDtos) {
        List<AppSiteServerEntity> entities = ConvertUtils.convert(appSiteServerDtos,
                appSiteServerDto -> {
                    AppSiteServerEntity entity = appSiteServerConverter.convertS2T(appSiteServerDto, AppSiteServerEntity.class);
                    entity.setAppSiteId(appSiteId);
                    return entity;
                });
        //保存
        List<AppSiteServerEntity> newEntities = appSiteServerRepository.save(entities);
        appSiteServerRepository.flush();
        //校验ip port重复
        List<AppSiteServerDto> exists = getByAppSiteId(appSiteId);
        appSiteValidate.validServerIpPortUnique(exists);
        return ConvertUtils.convert(newEntities, entity -> appSiteServerConverter.convertT2S(entity, AppSiteServerDto.class));
    }

    /**
     * 删除server
     *
     * @param appSiteId
     * @param ids
     */
    @Transactional
    public void deleteServers(Long appSiteId, List<Long> ids) {
        appSiteServerRepository.removeByIds(appSiteId, ids);
    }

    @Transactional
    public void removeByAppSiteId(Long appSiteId) {
        appSiteServerRepository.removeByAppSiteId(appSiteId);
        appSiteServerRepository.flush();
    }

    public PageDto<AppSiteServerDto> getAll(int page, int size) {
        PageRequest pageRequest = new PageRequest(page, size);
        Page<AppSiteServerEntity> entities = appSiteServerRepository.findAll(pageRequest);
        return ConvertUtils.convertPage(entities, entity -> appSiteServerConverter.convertT2S(entity, AppSiteServerDto.class));
    }

    public List<AppSiteServerDto> getByServerIds(List<Long> serverIds) {
        if (CollectionUtils.isEmpty(serverIds))
            return new ArrayList<>();
        List<AppSiteServerEntity> entities = appSiteServerRepository.findByIds(serverIds);
        return ConvertUtils.convert(entities, entity -> appSiteServerConverter.convertT2S(entity, AppSiteServerDto.class));
    }

    public List<AppSiteServerDto> getByServerNames(List<String> serverNames) {
        if (CollectionUtils.isEmpty(serverNames))
            return new ArrayList<>();
        List<AppSiteServerEntity> entities = appSiteServerRepository.findByNames(serverNames);
        return ConvertUtils.convert(entities, entity -> appSiteServerConverter.convertT2S(entity, AppSiteServerDto.class));
    }

    public List<AppSiteServerDto> getByAppSiteId(Long appSiteId) {
        List<AppSiteServerEntity> entities = appSiteServerRepository.findByAppSiteId(appSiteId);
        return ConvertUtils.convert(entities, entity -> appSiteServerConverter.convertT2S(entity, AppSiteServerDto.class));
    }

    public List<AppSiteServerDto> getByAppSiteIds(List<Long> appSiteIds) {
        if (CollectionUtils.isEmpty(appSiteIds))
            return new ArrayList<>();
        List<AppSiteServerEntity> entities = appSiteServerRepository.findByAppSiteIds(appSiteIds);
        return ConvertUtils.convert(entities, entity -> appSiteServerConverter.convertT2S(entity, AppSiteServerDto.class));
    }

}
