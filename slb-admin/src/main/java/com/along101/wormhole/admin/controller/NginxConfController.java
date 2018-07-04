package com.along101.wormhole.admin.controller;

import com.along101.wormhole.admin.dto.Response;
import com.along101.wormhole.admin.dto.appsite.AppSiteArchiveDto;
import com.along101.wormhole.admin.manager.nginx.NginxConfManager;
import com.along101.wormhole.admin.service.AppSiteArchiveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * Created by yinzuolong on 2017/7/11.
 */
@RestController
@RequestMapping("/web/nginx")
@Slf4j
public class NginxConfController {

    @Autowired
    private AppSiteArchiveService appSiteArchiveService;
    @Autowired
    private NginxConfManager nginxConfManager;

    @RequestMapping(value = "/{appSiteId}", method = RequestMethod.GET)
    public Response<String> getOnlineConfig(@PathVariable("appSiteId") Long appSiteId) {
        Optional<AppSiteArchiveDto> appSiteArchiveDto = appSiteArchiveService.getOnlineArchive(appSiteId);
        Optional<String> config = appSiteArchiveDto
                .map(archive -> nginxConfManager.convert(archive.getDomainAppSiteDtos()).orElse(null))
                .map(nginxConfManager::getNginxConf);
        return Response.success(config.orElse(null));
    }

}
