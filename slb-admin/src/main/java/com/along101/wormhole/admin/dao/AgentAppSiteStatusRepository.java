package com.along101.wormhole.admin.dao;

import java.util.List;

import com.along101.wormhole.admin.entity.AgentAppSiteStatusEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AgentAppSiteStatusRepository extends BaseJpaRepository<AgentAppSiteStatusEntity, Long> {

    @Query("select a from AgentAppSiteStatusEntity a where a.agentIp=:ip and a.appSiteId in (:appSiteIds) and a.isActive=true")
    List<AgentAppSiteStatusEntity> getAgentStatus(@Param(value = "ip") String ip, @Param(value = "appSiteIds") List<Long> appSiteIds);

    @Query("select a from AgentAppSiteStatusEntity a where a.agentIp=:ip and a.appSiteId in (:appSiteIds) and a.isActive=true")
    List<AgentAppSiteStatusEntity> getAgentStatusBySiteIds(@Param(value = "ip") String ip, @Param(value = "appSiteIds") List<Long> appSiteIds);

    @Query("select a from AgentAppSiteStatusEntity a where a.appSiteId=?1 and a.isActive=true")
    List<AgentAppSiteStatusEntity> getAgentStatusBySiteId(Long appSiteId);
}
