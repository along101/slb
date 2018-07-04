package com.along101.wormhole.admin.api;

import com.along101.wormhole.admin.common.exception.SlbServiceException;
import com.along101.wormhole.admin.dto.PageDto;
import com.along101.wormhole.admin.dto.Response;
import com.along101.wormhole.admin.dto.appsite.AppSiteDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteServerDto;
import com.along101.wormhole.admin.service.AppSiteService;
import com.along101.wormhole.admin.service.OperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by yinzuolong on 2017/7/11.
 */
@RestController
@RequestMapping("/api/appSite")
@Slf4j
public class AppSiteApi {

    @Autowired
    private AppSiteService appSiteService;
    @Autowired
    private OperationService operationService;

    //查询站点
    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public Response<PageDto<AppSiteDto>> getAll(@RequestParam("page") int page, @RequestParam("size") int size) {
        try {
            PageDto<AppSiteDto> sites = appSiteService.getAll(page, size);
            log.info("success getAll appSite page={}, size={}, result='{}'", page, size, sites);
            return Response.success(sites);
        } catch (SlbServiceException e) {
            log.warn("error getAll appSite page={}, size={}", page, size, e);
            return Response.error(e);
        } catch (Throwable e) {
            log.error("error getAll appSite page={}, size={}", page, size, e);
            return Response.error(e);
        }
    }

    //创建站点
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Response<AppSiteDto> createSite(@RequestBody AppSiteDto site) {
        try {
            AppSiteDto create = appSiteService.createAppSite(site);
            log.info("success create appSite='{}'", site);
            return Response.success(create);
        } catch (SlbServiceException e) {
            log.warn("error create appSite='{}'", site, e);
            return Response.error(e);
        } catch (Throwable e) {
            log.error("error create appSite='{}'", site, e);
            return Response.error(e);
        }
    }

    //修改站点
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Response<AppSiteDto> updateSite(@RequestBody AppSiteDto site) {
        try {
            AppSiteDto newSite = appSiteService.updateAppSite(site);
            log.error("success update appSite='{}'", site);
            return Response.success(newSite);
        } catch (SlbServiceException e) {
            log.warn("error update appSite='{}'", site, e);
            return Response.error(e);
        } catch (Throwable e) {
            log.error("error update appSite='{}'", site, e);
            return Response.error(e);
        }
    }

    //删除站点（下线后才能删除）
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Response<String> deleteSiteById(@RequestParam("appSiteId") Long appSiteId) {
        try {
            appSiteService.deleteAppSite(appSiteId);
            log.info("success delete appSite, appSiteId='{}'", appSiteId);
            return Response.success("success");
        } catch (SlbServiceException e) {
            log.warn("error delete appSite, appSiteId='{}'", appSiteId, e);
            return Response.error(e);
        } catch (Throwable e) {
            log.error("error delete appSite, appSiteId='{}'", appSiteId, e);
            return Response.error(e);
        }
    }

    //查询站点
    @RequestMapping(value = "/getById", method = RequestMethod.GET)
    public Response<AppSiteDto> getById(@RequestParam("appSiteId") Long appSiteId) {
        try {
            Optional<AppSiteDto> optional = appSiteService.getById(appSiteId);
            log.info("success get appSite, appSiteId='{}', result='{}'", appSiteId, optional.orElse(null));
            return Response.success(optional.orElse(null));
        } catch (SlbServiceException e) {
            log.warn("error get appSite, appSiteId='{}', result='{}'", appSiteId, e);
            return Response.error(e);
        } catch (Throwable e) {
            log.error("error add appSite, appSiteId='{}', result='{}'", appSiteId, e);
            return Response.error(e);
        }
    }

    //查询所有服务器列表
    @RequestMapping(value = "/servers/getAll", method = RequestMethod.GET)
    public Response<PageDto<AppSiteServerDto>> getAllServers(@RequestParam("page") int page, @RequestParam("size") int size) {
        try {
            PageDto<AppSiteServerDto> appSiteServerDtos = appSiteService.getAllAppSiteServers(page, size);
            log.info("success get appSite servers page={}, size={}", page, size);
            return Response.success(appSiteServerDtos);
        } catch (SlbServiceException e) {
            log.warn("error get appSite servers page={}, size={}", page, size, e);
            return Response.error(e);
        } catch (Throwable e) {
            log.error("error get appSite servers page={}, size={}", page, size, e);
            return Response.error(e);
        }
    }

    //查询某个site的所有服务器列表
    @RequestMapping(value = "/servers/getByAppSiteId", method = RequestMethod.GET)
    public Response<List<AppSiteServerDto>> getServersByAppSiteId(@RequestParam("appSiteId") Long appSiteId) {
        try {
            Optional<AppSiteDto> optional = appSiteService.getById(appSiteId);
            log.info("success get appSite servers, appSiteId='{}'", appSiteId);
            return Response.success(optional.map(AppSiteDto::getAppSiteServers).orElse(new ArrayList<>()));
        } catch (SlbServiceException e) {
            log.warn("error get appSite servers, appSiteId='{}'", appSiteId, e);
            return Response.error(e);
        } catch (Throwable e) {
            log.error("error add appSite servers, appSiteId='{}'", appSiteId, e);
            return Response.error(e);
        }
    }

    //增加server
    @RequestMapping(value = "/servers/add", method = RequestMethod.POST)
    public Response<List<AppSiteServerDto>> addServers(@RequestParam("appSiteId") Long appSiteId,
                                                       @RequestBody List<AppSiteServerDto> serverDtos) {
        try {
            List<AppSiteServerDto> addServers = appSiteService.addAppSiteServers(appSiteId, serverDtos);
            log.info("success add appSite servers, appSiteId='{}', serverDtos={}", appSiteId, serverDtos);
            return Response.success(addServers);
        } catch (SlbServiceException e) {
            log.warn("error add appSite servers, appSiteId='{}', serverDtos={}", appSiteId, serverDtos, e);
            return Response.error(e);
        } catch (Throwable e) {
            log.error("error add appSite servers, appSiteId='{}', serverDtos={}", appSiteId, serverDtos, e);
            return Response.error(e);
        }
    }

    //修改server
    @RequestMapping(value = "/servers/update", method = RequestMethod.POST)
    public Response<List<AppSiteServerDto>> updateServers(@RequestParam("appSiteId") Long appSiteId,
                                                          @RequestBody List<AppSiteServerDto> serverDtos) {
        try {
            List<AppSiteServerDto> updateServers = appSiteService.updateAppSiteServers(appSiteId, serverDtos);
            log.info("success update appSite servers,slbId='{}',appSiteId='{}', appSiteId={}", appSiteId, serverDtos);
            return Response.success(updateServers);
        } catch (SlbServiceException e) {
            log.error("error update appSite servers, appSiteId='{}', serverDtos={}", appSiteId, serverDtos, e);
            return Response.error(e);
        } catch (Throwable e) {
            log.error("error update appSite servers, appSiteId='{}', serverDtos={}", appSiteId, serverDtos, e);
            return Response.error(e);
        }
    }

    //删除server
    @RequestMapping(value = "/servers/delete", method = RequestMethod.POST)
    public Response<String> deleteServers(@RequestParam("appSiteId") Long appSiteId,
                                          @RequestBody List<Long> serverIds) {
        try {
            appSiteService.deleteSiteServers(appSiteId, serverIds);
            log.info("success add delete servers, appSiteId='{}', serverIds={}", appSiteId, serverIds);
            return Response.success("success");
        } catch (SlbServiceException e) {
            log.warn("error delete appSite servers, appSiteId='{}', serverIds={}", appSiteId, serverIds, e);
            return Response.error(e);
        } catch (Throwable e) {
            log.error("error delete appSite servers, appSiteId='{}', serverIds={}", appSiteId, serverIds, e);
            return Response.error(e);
        }
    }

}
