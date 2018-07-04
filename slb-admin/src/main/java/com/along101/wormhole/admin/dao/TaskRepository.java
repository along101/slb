package com.along101.wormhole.admin.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.along101.wormhole.admin.entity.TaskEntity;

/**
 * Created by yinzuolong on 2017/7/11.
 */
public interface TaskRepository extends BaseJpaRepository<TaskEntity, Long> {

    @Query("select a from TaskEntity a where a.isActive=true and a.appSiteId =?1")
    List<TaskEntity> findTasksBySiteId(Long siteId);

    @Query("select a from TaskEntity a where a.isActive=true and a.id in (select  max(b.id)  from TaskEntity b where  b.isActive=true and b.appSiteId=?1 group by b.appSiteId)")
    TaskEntity findMaxTaskBySiteId(Long siteId);

    @Query("select a from TaskEntity a where a.id in (select  max(b.id)  from TaskEntity b where b.id>?2 and b.isActive=true and b.slbId=?1 group by b.appSiteId )")
    List<TaskEntity> findMaxTaskGroupByAppSite(long sibId, long taskId);

    @Modifying(clearAutomatically = true)
    @Query("update TaskEntity a set a.status=?2 where a.id=?1")
    int updateTaskStatus(Long taskId, int status);

}
