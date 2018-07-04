package com.along101.wormhole.admin.controller;

import com.along101.wormhole.admin.dto.Response;
import com.along101.wormhole.admin.dto.appsite.AppSiteArchiveDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteDto;
import com.along101.wormhole.admin.service.AppSiteArchiveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * Created by along on 2017/7/31.
 */
@RestController
@RequestMapping("/web/archive")
@Slf4j
public class AppSiteArchiveController {
    @Autowired
    private AppSiteArchiveService appSiteArchiveService;

    @RequestMapping(value = "/{appSiteId}", method = RequestMethod.GET)
    public Response<AppSiteArchiveDto> getOnlineArchive(@PathVariable("appSiteId") Long appSiteId) {
        Optional<AppSiteArchiveDto> appSiteArchiveDto = appSiteArchiveService.getOnlineArchive(appSiteId);
        return Response.success(appSiteArchiveDto.orElse(null));
    }


    @RequestMapping(value = "/appSite/{appSiteId}", method = RequestMethod.GET)
    public Response<AppSiteDto> getOnlineAppSite(@PathVariable("appSiteId") Long appSiteId) {
        Optional<AppSiteArchiveDto> appSiteArchiveDto = appSiteArchiveService.getOnlineArchive(appSiteId);
        return Response.success(appSiteArchiveDto.map(AppSiteArchiveDto::getAppSiteDto).orElse(null));
    }
}
