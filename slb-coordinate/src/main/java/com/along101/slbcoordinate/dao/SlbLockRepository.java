package com.along101.slbcoordinate.dao;

import com.along101.slbcoordinate.entity.SlbLockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public interface SlbLockRepository extends JpaRepository<SlbLockEntity, Long>, JpaSpecificationExecutor<SlbLockEntity> {
    // 用来获取分配器
    @Modifying(clearAutomatically = true)
    @Query(value = "update slb_lock set update_time=now() , ip=?1 where update_time<date_sub(now(),interval 10 second)", nativeQuery = true)
    Integer updateHeatTime(String ip);

    @Modifying(clearAutomatically = true)
    @Query(value = "update slb_lock set update_time=now()  where ip=?1", nativeQuery = true)
    Integer updateHeatTimeByIp(String ip);

    @Modifying(clearAutomatically = true)
    @Query(value = "delete from slb_lock where update_time<date_sub(now(),interval 100 second) and `key`=?1", nativeQuery = true)
    void deleteOld(String key);

    SlbLockEntity findByKey(String key);

    @Query(value = "select now()", nativeQuery = true)
    Date getNow();
}
