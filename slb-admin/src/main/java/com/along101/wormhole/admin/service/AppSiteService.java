package com.along101.wormhole.admin.service;

import com.along.wormhole.admin.common.constant.OnlineStatusEnum;
import com.along.wormhole.admin.common.utils.ConvertUtils;
import com.along.wormhole.admin.dto.PageDto;
import com.along.wormhole.admin.dto.appsite.AppSiteDto;
import com.along.wormhole.admin.dto.appsite.AppSiteQuery;
import com.along.wormhole.admin.dto.appsite.AppSiteServerDto;
import com.along.wormhole.admin.manager.appsite.AppSiteEditManager;
import com.along.wormhole.admin.manager.appsite.AppSiteManager;
import com.along.wormhole.admin.manager.appsite.AppSiteServerManager;
import com.along.wormhole.admin.manager.operation.OperationManager;
import com.along.wormhole.admin.service.validate.AppSiteValidate;
import com.along101.wormhole.admin.common.constant.OnlineStatusEnum;
import com.along101.wormhole.admin.common.utils.ConvertUtils;
import com.along101.wormhole.admin.dto.PageDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteQuery;
import com.along101.wormhole.admin.dto.appsite.AppSiteServerDto;
import com.along101.wormhole.admin.manager.appsite.AppSiteEditManager;
import com.along101.wormhole.admin.manager.appsite.AppSiteManager;
import com.along101.wormhole.admin.manager.appsite.AppSiteServerManager;
import com.along101.wormhole.admin.manager.operation.OperationManager;
import com.along101.wormhole.admin.service.validate.AppSiteValidate;
import com.google.common.base.Preconditions;
import com.along101.wormhole.admin.common.constant.OnlineStatusEnum;
import com.along101.wormhole.admin.dto.PageDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteQuery;
import com.along101.wormhole.admin.dto.appsite.AppSiteServerDto;
import com.along101.wormhole.admin.manager.appsite.AppSiteEditManager;
import com.along101.wormhole.admin.manager.appsite.AppSiteManager;
import com.along101.wormhole.admin.manager.appsite.AppSiteServerManager;
import com.along101.wormhole.admin.manager.operation.OperationManager;
import com.along101.wormhole.admin.service.validate.AppSiteValidate;
import com.along101.wormhole.admin.common.utils.ConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by yinzuolong on 2017/7/11.
 */
@Service
public class AppSiteService {
    @Autowired
    private AppSiteManager appSiteManager;
    @Autowired
    private AppSiteServerManager appSiteServerManager;
    @Autowired
    private AppSiteValidate appSiteValidate;
    @Autowired
    private OperationManager operationManager;
    @Autowired
    private AppSiteEditManager appSiteEditManager;

    /**
     * 新增站点
     */
    @Transactional
    public AppSiteDto createAppSite(AppSiteDto appSiteDto) {
        Preconditions.checkNotNull(appSiteDto, "appSite object can not be null");
        //检验slb是否存在,检查appId唯一性,检查domain/port/path
        appSiteValidate.validSlbExist(appSiteDto);
//        appSiteValidate.validAppIdUnique(appSiteDto);
        appSiteValidate.validDomainPath(appSiteDto);
        //保存appSite
        appSiteDto.setId(null);
        appSiteDto.setOnlineVersion(0L);
        appSiteDto.setStatus(OnlineStatusEnum.OFFLINE.getValue());
        appSiteManager.saveAppSite(appSiteDto);
        if (!CollectionUtils.isEmpty(appSiteDto.getAppSiteServers())) {
            List<AppSiteServerDto> appSiteServerDtos =
                    appSiteServerManager.addServers(appSiteDto.getId(), appSiteDto.getAppSiteServers());
            appSiteDto.setAppSiteServers(appSiteServerDtos);
        }
        return appSiteDto;
    }

    /**
     * 修改站点
     *
     * @param appSiteDto
     * @return
     */
    @Transactional
    public AppSiteDto updateAppSite(AppSiteDto appSiteDto) {
        Preconditions.checkNotNull(appSiteDto, "appSiteDto object can not be null.");
        Preconditions.checkNotNull(appSiteDto.getId(), "appSiteId can not be null.");
        appSiteManager.increaseVersion(appSiteDto.getId());
        //校验appSite是否存在,检验slb是否存在,检查appId唯一性,检查domain/port/path
        appSiteValidate.validAppSitExist(appSiteDto.getId());
        appSiteValidate.validSlbExist(appSiteDto);
//        appSiteValidate.validAppIdUnique(appSiteDto);
        appSiteValidate.validDomainPath(appSiteDto);

        appSiteManager.saveAppSite(appSiteDto);
        //servers不为null则先删除servers，为null则不修改servers
        if (appSiteDto.getAppSiteServers() != null) {
            List<AppSiteServerDto> newServerDtos = appSiteServerManager.saveServers(appSiteDto.getId(), appSiteDto.getAppSiteServers());
            appSiteDto.setAppSiteServers(newServerDtos);
        }
        //修改后部署
        operationManager.deploySite(appSiteDto);
        return appSiteDto;
    }

    /**
     * 删除站点（下线后才能删除）
     *
     * @param appSiteId
     */
    @Transactional
    public void deleteAppSite(Long appSiteId) {
        Preconditions.checkNotNull(appSiteId, "appSiteId can not be null.");
        appSiteManager.increaseVersion(appSiteId);
        //检查站点是否存在，是否已经下线，下线后才能删除
        appSiteValidate.validAppSitExist(appSiteId);
        appSiteValidate.validAppSiteOffline(appSiteId);

        appSiteManager.removeAppSite(appSiteId);
        appSiteServerManager.removeByAppSiteId(appSiteId);
    }

    /**
     * 根据id查询站点
     *
     * @param appSiteId
     * @return
     */
    public Optional<AppSiteDto> getById(Long appSiteId) {
        Preconditions.checkNotNull(appSiteId, "appSiteId can not be null.");
        Optional<AppSiteDto> appSiteDto = appSiteManager.getById(appSiteId);
        appSiteDto.ifPresent(o -> joinAppSiteServer(Collections.singletonList(o)));
        return appSiteDto;
    }

    /**
     * 分页查询所有站点
     *
     * @param page
     * @param size
     * @return
     */
    public PageDto<AppSiteDto> getAll(int page, int size) {
        PageDto<AppSiteDto> appSiteDtos = appSiteManager.getByPage(page, size);
        joinAppSiteServer(appSiteDtos.getContent());
        return appSiteDtos;
    }


    /**
     * 根据appId查询站点
     *
     * @param appId
     * @return
     */
    public Optional<AppSiteDto> getByAppId(Long slbId, String appId) {
        Preconditions.checkNotNull(slbId, "slbId can not be null.");
        Preconditions.checkNotNull(appId, "appId can not be null.");
        Optional<AppSiteDto> appSiteDto = appSiteManager.getByAppId(slbId, appId);
        appSiteDto.ifPresent(o -> joinAppSiteServer(Collections.singletonList(o)));
        return appSiteDto;
    }

    /**
     * 根据siteId查询指定站点服务器列表
     *
     * @param siteId
     * @return
     */
    public List<AppSiteServerDto> getAppSiteServers(Long siteId) {
        Preconditions.checkNotNull(siteId, "siteId can not be null.");
        return appSiteServerManager.getByAppSiteIds(Collections.singletonList(siteId));
    }

    /**
     * 根据分页查询所有Servers
     *
     * @param page
     * @param size
     * @return
     */
    public PageDto<AppSiteServerDto> getAllAppSiteServers(int page, int size) {
        Preconditions.checkArgument(page >= 0, "page must be equal or greater than 0.");
        Preconditions.checkArgument(size > 0, "size must be equal or greater than 0.");
        return appSiteServerManager.getAll(page, size);
    }

    /**
     * 根据siteId查询指定站点服务器列表
     *
     * @param siteId
     * @return
     */
    public List<AppSiteServerDto> getAllAppSiteServers(Long siteId) {
        Preconditions.checkNotNull(siteId, "siteId can not be null.");
        return appSiteServerManager.getByServerIds(Collections.singletonList(siteId));
    }

    /**
     * 根据ids查询服务器列表
     *
     * @param ids
     * @return
     */
    public List<AppSiteServerDto> getAppSiteServersByIds(List<Long> ids) {
        Preconditions.checkNotNull(ids, "ids can not be null.");
        return appSiteServerManager.getByServerIds(ids);
    }

    /**
     * 根据names查询服务器列表
     *
     * @param names
     * @return
     */
    public List<AppSiteServerDto> getAppSiteServersByNames(List<String> names) {
        Preconditions.checkNotNull(names, "names can not be null.");
        return appSiteServerManager.getByServerNames(names);
    }

    /**
     * 添加指定站点服务器
     *
     * @param appSiteId
     * @param appSiteServerDtos
     * @return
     */
    @Transactional
    public List<AppSiteServerDto> addAppSiteServers(Long appSiteId, List<AppSiteServerDto> appSiteServerDtos) {
        Preconditions.checkNotNull(appSiteId, "appSiteId can not be null");
        Preconditions.checkNotNull(appSiteServerDtos, "appSiteServerDtos can not be null");
        appSiteManager.increaseVersion(appSiteId);
        appSiteValidate.validAppSitExist(appSiteId);
        List<AppSiteServerDto> serverDtos = appSiteServerManager.addServers(appSiteId, appSiteServerDtos);
        //修改后部署
        operationManager.deploySite(appSiteId);
        return serverDtos;
    }

    /**
     * 对指定站点服务器进行更新
     */
    @Transactional
    public List<AppSiteServerDto> updateAppSiteServers(Long appSiteId, List<AppSiteServerDto> appSiteServerDtos) {
        Preconditions.checkNotNull(appSiteId, "appSiteId can not be null");
        Preconditions.checkNotNull(appSiteServerDtos, "appSiteServerDtos can not be null");
        appSiteManager.increaseVersion(appSiteId);
        appSiteValidate.validAppSitExist(appSiteId);
        List<AppSiteServerDto> serverDtos = appSiteServerManager.updateServers(appSiteId, appSiteServerDtos);
        //修改后部署
        operationManager.deploySite(appSiteId);
        return serverDtos;
    }

    /**
     * 删除站点的指定服务器
     */
    @Transactional
    public void deleteSiteServers(Long appSiteId, List<Long> serverIds) {
        Preconditions.checkNotNull(appSiteId, "appSiteId can not be null");
        Preconditions.checkNotNull(serverIds, "appSiteServerDtos can not be null");
        appSiteManager.increaseVersion(appSiteId);
        appSiteValidate.validAppSitExist(appSiteId);
        appSiteServerManager.deleteServers(appSiteId, serverIds);
        //修改后部署
        operationManager.deploySite(appSiteId);
    }

    /**
     * 多条件查询站点
     * @param appSiteQuery
     * @param pageRequest
     * @return
     */
    @Transactional
    public PageDto<AppSiteDto> searchAppSiteByConditions(AppSiteQuery appSiteQuery, PageRequest pageRequest) {
        PageDto<AppSiteDto> appSiteDtos = appSiteManager.getByConditions(appSiteQuery, pageRequest);
        joinAppSiteServer(appSiteDtos.getContent());
        return appSiteDtos;
    }

    private List<AppSiteDto> joinAppSiteServer(List<AppSiteDto> appSiteDtos) {
        List<AppSiteServerDto> appSiteServerDtos = appSiteServerManager.getByAppSiteIds(ConvertUtils.convert(appSiteDtos, AppSiteDto::getId));
        Map<Long, List<AppSiteServerDto>> serverMap = appSiteServerDtos.stream().collect(Collectors.groupingBy(AppSiteServerDto::getAppSiteId));
        appSiteDtos.forEach(appSiteDto -> appSiteDto.setAppSiteServers(serverMap.getOrDefault(appSiteDto.getId(), new ArrayList<>())));
        return appSiteDtos;
    }

//    // 发布
//    @Transactional
//    public AppSiteDto updateFromEdit(Long appSiteId) {
//        Preconditions.checkNotNull(appSiteId, "appSiteId can not be null.");
//        Optional<AppSiteDto> optional = appSiteEditManager.getByAppSiteId(appSiteId);
//        if (!optional.isPresent()) {
//            throw SlbServiceException.newException("appSite id='%s' not exist.", appSiteId);
//        }
//        AppSiteDto appSiteDto = optional.get();
//        return this.updateAppSite(appSiteDto);
//    }
}
