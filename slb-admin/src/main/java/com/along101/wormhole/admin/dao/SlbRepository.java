package com.along101.wormhole.admin.dao;

import com.along101.wormhole.admin.entity.SlbEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by yinzuolong on 2017/7/11.
 */
public interface SlbRepository extends BaseJpaRepository<SlbEntity, Long> {

    @Query("select a from SlbEntity a where a.isActive=true")
    List<SlbEntity> findAll();

    @Query("SELECT a FROM SlbEntity a WHERE a.name like %?1% AND a.isActive=true")
    List<SlbEntity> findByName(String name);

    @Query("SELECT a FROM SlbEntity a WHERE a.id=?1 AND a.isActive=true")
    SlbEntity findOne(Long id);

    @Modifying(clearAutomatically = true)
    @Query("delete from  SlbEntity a  where a.id=?1")
    int remove(Long id);


    @Modifying(clearAutomatically = true)
    @Query("delete from SlbEntity a")
    int removeAll();
}
