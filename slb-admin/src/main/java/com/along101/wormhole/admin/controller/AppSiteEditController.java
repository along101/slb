package com.along101.wormhole.admin.controller;

import com.along101.wormhole.admin.dto.Response;
import com.along101.wormhole.admin.dto.appsite.AppSiteDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteServerDto;
import com.along101.wormhole.admin.service.AppSiteEditService;
import com.along101.wormhole.admin.service.AppSiteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Created by yinzuolong on 2017/9/6.
 */
@RestController
@RequestMapping("/web/siteedit")
@Slf4j
public class AppSiteEditController {

    @Autowired
    private AppSiteEditService appSiteEditService;
    @Autowired
    private AppSiteService appSiteService;

    //查询站点
    @RequestMapping(value = "/{siteId}", method = RequestMethod.GET)
    public Response<AppSiteDto> getById(@PathVariable("siteId") Long siteId) {
        Optional<AppSiteDto> optional = appSiteEditService.getById(siteId);
        return Response.success(optional.orElse(null));
    }

    //修改站点
    @RequestMapping(value = "", method = RequestMethod.PUT)
    public Response<AppSiteDto> updateSite(@RequestBody AppSiteDto site) {
        AppSiteDto newSite = appSiteEditService.updateAppSite(site);
        return Response.success(newSite);
    }

    //修改servers
    @RequestMapping(value = "/{siteId}/servers", method = RequestMethod.PUT)
    public Response<AppSiteDto> updateSiteServers(@PathVariable(value = "siteId") Long siteId,
                                                  @RequestBody List<AppSiteServerDto> siteServers) {
        AppSiteDto appSiteDto = appSiteEditService.updateServers(siteId, siteServers);
        return Response.success(appSiteDto);
    }

    //修改servers
    @RequestMapping(value = "/{siteId}/revert", method = RequestMethod.POST)
    public Response<AppSiteDto> revertOnline(@PathVariable(value = "siteId") Long siteId) {
        AppSiteDto appSiteDto = appSiteEditService.revertOnline(siteId);
        return Response.success(appSiteDto);
    }

    //修改servers
    @Transactional
    @RequestMapping(value = "/{siteId}/deploy", method = RequestMethod.POST)
    public Response<AppSiteDto> deploy(@PathVariable(value = "siteId") Long siteId) {
        Optional<AppSiteDto> optional = appSiteEditService.getById(siteId);
        optional.ifPresent(appSiteDto -> appSiteService.updateAppSite(appSiteDto));
        AppSiteDto appSiteDto = appSiteEditService.revertOnline(siteId);
        return Response.success(appSiteDto);
    }

    @RequestMapping(value = "/{siteId}/preview", method = RequestMethod.GET)
    public Response<String> previewEditConfig(@PathVariable("siteId") Long appSiteId) {
        String config = appSiteEditService.previewEditConfig(appSiteId);
        return Response.success(config);
    }
}
