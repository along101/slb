package com.along101.wormhole.admin.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.along101.wormhole.admin.entity.AgentTaskResultEntity;

public interface AgentTaskResultRepository extends BaseJpaRepository<AgentTaskResultEntity, Long> {

    @Query("select max(a.taskId) from AgentTaskResultEntity a where a.agentIp=?1 ")
    Long getAgentMaxTaskId(String ip);

    @Query("select a from AgentTaskResultEntity a where a.appSiteId=?1 order by a.updateTime desc")
    Page<AgentTaskResultEntity> getTaskResultBySiteId(Long siteId, Pageable pageable);

    @Query("select a from AgentTaskResultEntity a where a.taskId >= ?1 and a.updateTime < ?2 and a.isActive=true ")
    List<AgentTaskResultEntity> getTaskResultBeforeOvertime(Long taskId, Date overtime);
}
