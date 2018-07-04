package com.along101.wormhole.admin.dao;

import java.util.List;

import com.along101.wormhole.admin.entity.AppSiteArchiveEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Created by yinzuolong on 2017/7/11.
 */
public interface AppSiteArchiveRepository extends BaseJpaRepository<AppSiteArchiveEntity, Long> {

    @Query("select a from AppSiteArchiveEntity a where a.appSiteId = ?1 and a.isActive=true and a.flag=1")
    AppSiteArchiveEntity getOnlineAppSite(Long appSiteId);

    @Query("select a from AppSiteArchiveEntity a where a.appSiteId in (:appSiteIds) and a.isActive=true")
    List<AppSiteArchiveEntity> getCurrentAppSites(@Param(value = "appSiteIds") List<Long> appSiteIds);

    @Query("select a from AppSiteArchiveEntity a where a.flag=1 and a.domainPort in (?1) and a.isActive=true")
    List<AppSiteArchiveEntity> getCurrentByDomains(List<String> domains);

    @Query("select a from AppSiteArchiveEntity a where a.isActive=true and a.flag=1 and a.domainPort=?1")
    List<AppSiteArchiveEntity> getByDomainPort(String domainPort);

    @Query("select a from AppSiteArchiveEntity a where a.appSiteId=?1 and a.version=?2 and a.isActive=true")
    AppSiteArchiveEntity getArchiveByVersion(Long appSiteId, Long version);

    @Modifying(clearAutomatically = true)
    @Query("update AppSiteArchiveEntity a set a.flag=0 where a.appSiteId=?1 and a.flag=1")
    int offlineArchive(Long appSiteId);
}
