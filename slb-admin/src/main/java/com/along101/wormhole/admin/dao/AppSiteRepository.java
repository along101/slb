package com.along101.wormhole.admin.dao;

import com.along101.wormhole.admin.entity.AppSiteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by yinzuolong on 2017/7/11.
 */
public interface AppSiteRepository extends BaseJpaRepository<AppSiteEntity, Long>{
    @Override
    @Query("SELECT a FROM AppSiteEntity a where a.isActive=true AND a.id=?1")
    AppSiteEntity findOne(Long id);

    @Override
    @Query("SELECT a FROM AppSiteEntity a where a.isActive=true order by a.appId")
    List<AppSiteEntity> findAll();

    @Override
    @Query("SELECT a FROM AppSiteEntity a where a.isActive=true order by a.appId")
    Page<AppSiteEntity> findAll(Pageable pageable);

    @Query("SELECT a FROM AppSiteEntity a where a.isActive=true AND a.id in (?1)")
    List<AppSiteEntity> findByAppIds(List<Long> ids);

    @Query("SELECT a FROM AppSiteEntity a where a.isActive=true AND a.slbId=?1")
    List<AppSiteEntity> findBySlbId(Long slbId);

    @Query("SELECT a FROM AppSiteEntity a where a.isActive=true AND a.slbId=?1 AND a.appId=?2")
    AppSiteEntity findByAppId(Long slbId, String appId);

    Page<AppSiteEntity> findAll(Specification specification, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE AppSiteEntity a SET a.onlineVersion=a.onlineVersion WHERE a.id=?1")
    void lock(Long id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE AppSiteEntity a SET a.onlineVersion=a.onlineVersion+1 , a.offlineVersion=a.onlineVersion WHERE a.id=?1")
    int increaseVersion(Long id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE AppSiteEntity a SET a.status=?2 WHERE a.id=?1")
    int updateStatus(Long id, Integer status);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE AppSiteEntity a SET a.isActive=false WHERE a.id=?1")
    int removeAppSite(Long id);

    @Query("SELECT a FROM AppSiteEntity a where a.isActive=true AND a.domain=?1 AND a.port=?2")
    List<AppSiteEntity> findByDomainAndPort(String domain, Integer port);

}
