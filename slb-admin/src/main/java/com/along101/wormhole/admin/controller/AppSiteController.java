package com.along101.wormhole.admin.controller;

import com.along101.wormhole.admin.dto.PageDto;
import com.along101.wormhole.admin.dto.Response;
import com.along101.wormhole.admin.dto.appsite.AppSiteDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteQuery;
import com.along101.wormhole.admin.dto.appsite.AppSiteServerDto;
import com.along101.wormhole.admin.service.AppSiteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by yinzuolong on 2017/7/11.
 */
@RestController
@RequestMapping("/web/sites")
@Slf4j
public class AppSiteController {

    @Autowired
    private AppSiteService appSiteService;

    //查询站点
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Response<PageDto<AppSiteDto>> getAll(@RequestParam("page") int page, @RequestParam("size") int size) {
        PageDto<AppSiteDto> sites = appSiteService.getAll(page, size);
        return Response.success(sites);
    }

    //查询站点（根据条件），返回站点状态，状态信息在
    @RequestMapping(value = "/{siteId}", method = RequestMethod.GET)
    public Response<AppSiteDto> getById(@PathVariable("siteId") Long siteId) {
        Optional<AppSiteDto> site = appSiteService.getById(siteId);
        return Response.success(site.orElse(null));
    }

    //查询站点，条件包括 slbName, appId, name, domain
    @RequestMapping(value = "/site", method = RequestMethod.GET)
    public Response<PageDto<AppSiteDto>> getAppSiteByConditions(@RequestParam(required = false) Long slbId,
                                                             @RequestParam(required = false) String appId,
                                                             @RequestParam(required = false) String name,
                                                             @RequestParam(required = false) String domain,
                                                             @RequestParam("page") int page,
                                                             @RequestParam("size") int size) {

        log.info("slbId-" + slbId + ",appid-" + appId + ",name-" + name + ",domain-" + domain);

        AppSiteQuery appSiteQuery = new AppSiteQuery();
        appSiteQuery.setSlbId(slbId);
        appSiteQuery.setAppId(appId);
        appSiteQuery.setDomain(domain);
        appSiteQuery.setName(name);

        PageRequest pageRequest = new PageRequest(page, size);
        PageDto<AppSiteDto> appSiteDtos = appSiteService.searchAppSiteByConditions(appSiteQuery, pageRequest);
        return Response.success(appSiteDtos);
    }

    //新增站点
    @RequestMapping(value = "", method = RequestMethod.POST)
    public Response<AppSiteDto> createSite(@RequestBody AppSiteDto site) {
        AppSiteDto newSite = appSiteService.createAppSite(site);
        return Response.success(newSite);
    }

    //修改站点
    @RequestMapping(value = "", method = RequestMethod.PUT)
    public Response<AppSiteDto> updateSite(@RequestBody AppSiteDto site) {
        AppSiteDto newSite = appSiteService.updateAppSite(site);
        return Response.success(newSite);
    }

    //删除站点（下线后才能删除）
    @RequestMapping(value = "/{siteId}", method = RequestMethod.DELETE)
    public Response<String> deleteSiteById(@PathVariable(value = "siteId") Long siteId) {
        appSiteService.deleteAppSite(siteId);
        return Response.success("site已经成功删除");
    }

    //查询某个site的所有服务器列表
    @RequestMapping(value = "/{siteId}/servers", method = RequestMethod.GET)
    public Response<List<AppSiteServerDto>> getSiteServers(@PathVariable(value = "siteId") Long siteId) {
        List<AppSiteServerDto> siteServers = appSiteService.getAppSiteServers(siteId);
        return Response.success(siteServers);
    }

    //增加server
    @RequestMapping(value = "/{siteId}/servers", method = RequestMethod.POST)
    public Response<AppSiteServerDto> addSiteServer(@PathVariable(value = "siteId") Long siteId,
                                                    @RequestBody AppSiteServerDto siteServer) {
        List<AppSiteServerDto> newServers = appSiteService.addAppSiteServers(siteId, Collections.singletonList(siteServer));
        return Response.success(newServers.get(0));
    }

    //修改server
    @RequestMapping(value = "/{siteId}/servers", method = RequestMethod.PUT)
    public Response<AppSiteServerDto> updateSiteServer(@PathVariable(value = "siteId") Long siteId,
                                                       @RequestBody AppSiteServerDto siteServer) {
        List<AppSiteServerDto> newServers = appSiteService.updateAppSiteServers(siteId, Collections.singletonList(siteServer));
        return Response.success(newServers.get(0));
    }

    //删除server
    @RequestMapping(value = "/{siteId}/servers/{serverId}", method = RequestMethod.DELETE)
    public Response<String> deleteSiteServer(@PathVariable(value = "siteId") Long siteId,
                                             @PathVariable(value = "serverId") Long serverId) {
        appSiteService.deleteSiteServers(siteId, Collections.singletonList(serverId));
        return Response.success("服务器已成功删除");
    }

}
