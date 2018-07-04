package com.along101.wormhole.admin.service;

import com.along.wormhole.admin.dto.AgentAppSiteStatusDto;
import com.along.wormhole.admin.manager.agent.AgentStatusManager;
import com.along101.wormhole.admin.dto.AgentAppSiteStatusDto;
import com.along101.wormhole.admin.manager.agent.AgentStatusManager;
import com.along101.wormhole.admin.dto.AgentAppSiteStatusDto;
import com.along101.wormhole.admin.manager.agent.AgentStatusManager;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yinzuolong on 2017/7/11.
 */
@Service
@Log4j
public class AgentStatusService {

    @Autowired
    private AgentStatusManager agentStatusManager;

    /**
     * 获取指定站点在agent上的发布状态
     *
     * @param siteId 站点ID
     * @return
     */
    public List<AgentAppSiteStatusDto> getAgentStatusBySiteId(Long siteId) {
        return agentStatusManager.getAgentStatusBySiteId(siteId);
    }
}
