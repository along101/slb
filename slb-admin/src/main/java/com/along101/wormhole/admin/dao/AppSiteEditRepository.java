package com.along101.wormhole.admin.dao;

import com.along101.wormhole.admin.entity.AppSiteEditEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by yinzuolong on 2017/7/11.
 */
public interface AppSiteEditRepository extends BaseJpaRepository<AppSiteEditEntity, Long> {

    @Query("select a from AppSiteEditEntity a where a.appSiteId = ?1 and a.isActive=true")
    AppSiteEditEntity getByAppSiteId(Long appSiteId);

    @Modifying(clearAutomatically = true)
    @Query("delete from  AppSiteEditEntity a  where a.appSiteId=?1")
    int remove(Long appSiteId);

}
