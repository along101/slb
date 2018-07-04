package com.along101.wormhole.admin.dao;

import com.along101.wormhole.admin.entity.SlbServerEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by yinzuolong on 2017/7/11.
 */
public interface SlbServerRepository extends BaseJpaRepository<SlbServerEntity, Long> {

    @Query("SELECT a FROM SlbServerEntity a WHERE a.id=?1 and a.isActive=true")
    SlbServerEntity findById(Long id);

    @Query("SELECT a FROM SlbServerEntity a WHERE a.slbId=?1 and a.isActive=true")
    List<SlbServerEntity> findBySlbId(Long slbId);

    @Query("SELECT a FROM SlbServerEntity a WHERE a.isActive=true")
    List<SlbServerEntity> findAll();

    @Query("SELECT a FROM SlbServerEntity a WHERE a.slbId in (?1) and a.isActive=true")
    List<SlbServerEntity> findBySlbIds(List<Long> slbIds);

    @Modifying(clearAutomatically = true)
    @Query("update SlbServerEntity a set a.isActive=false WHERE a.slbId=?1")
    int deleteBySlbId(Long slbId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE SlbServerEntity a SET a.isActive=false WHERE a.slbId=?1")
    void removeBySlbId(Long slbId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE SlbServerEntity a SET a.isActive=false WHERE a.id in (?1)")
    void removeByIds(List<Long> ids);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE SlbServerEntity a SET a.isActive=false ")
    void removeAll();

    @Query("SELECT a FROM SlbServerEntity a WHERE a.ip=?1 and a.isActive=true")
    SlbServerEntity findByIp(String ip);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE SlbServerEntity a SET a.heartTime=now() WHERE a.ip=?1 and a.isActive=true")
    void agentHeartBeat(String ip);

}
