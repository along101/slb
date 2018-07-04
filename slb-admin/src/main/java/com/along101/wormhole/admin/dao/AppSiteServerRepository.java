package com.along101.wormhole.admin.dao;

import com.along101.wormhole.admin.entity.AppSiteServerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by yinzuolong on 2017/7/11.
 */
public interface AppSiteServerRepository extends BaseJpaRepository<AppSiteServerEntity, Long> {

    @Query("SELECT a from  AppSiteServerEntity a WHERE a.isActive = true AND a.appSiteId=?1")
    List<AppSiteServerEntity> findByAppSiteId(Long appSiteId);

    @Query("SELECT a from  AppSiteServerEntity a WHERE a.isActive = true order by a.id")
    Page<AppSiteServerEntity> findAll(Pageable pageable);

    @Query("SELECT a from  AppSiteServerEntity a WHERE a.isActive = true and a.id in (?1)")
    List<AppSiteServerEntity> findByIds(List<Long> ids);

    @Query("SELECT a from  AppSiteServerEntity a WHERE a.isActive = true and a.name in (?1)")
    List<AppSiteServerEntity> findByNames(List<String> names);

    @Query("SELECT a from  AppSiteServerEntity a WHERE a.isActive = true AND a.appSiteId in (:appSiteIds)")
    List<AppSiteServerEntity> findByAppSiteIds(@Param(value = "appSiteIds") List<Long> appSiteIds);

    @Modifying(clearAutomatically = true)
    @Query("delete from AppSiteServerEntity a WHERE a.appSiteId=?1")
    int removeByAppSiteId(Long appSiteId);

    @Modifying(clearAutomatically = true)
    @Query("delete from AppSiteServerEntity a WHERE a.appSiteId=:appSiteId AND a.id in (:ids)")
    int removeByIds(@Param(value = "appSiteId") Long appSiteId, @Param(value = "ids") List<Long> ids);
}
