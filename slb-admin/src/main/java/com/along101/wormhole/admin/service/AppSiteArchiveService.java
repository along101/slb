package com.along101.wormhole.admin.service;

import com.along.wormhole.admin.dto.appsite.AppSiteArchiveDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteArchiveDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteArchiveDto;
import com.along101.wormhole.admin.manager.appsite.AppSiteArchiveManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by huangyinhuang on 7/18/2017.
 */
@Service
@Slf4j
public class AppSiteArchiveService {
    @Autowired
    private AppSiteArchiveManager appSiteArchiveManager;

    // 查询归档信息
    public Optional<AppSiteArchiveDto> getArchiveByVersion(Long appSiteId, Long version) {
        return appSiteArchiveManager.getArchiveByVersion(appSiteId, version);
    }


    // 查询归档信息
    public Optional<AppSiteArchiveDto> getOnlineArchive(Long appSiteId) {
        return appSiteArchiveManager.getOnlineArchive(appSiteId);
    }
}
