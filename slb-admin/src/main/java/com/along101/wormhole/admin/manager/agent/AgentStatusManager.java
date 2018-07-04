package com.along101.wormhole.admin.manager.agent;

import com.along.wormhole.admin.common.utils.ConvertUtils;
import com.along.wormhole.admin.common.utils.DomainPortUtils;
import com.along.wormhole.admin.dao.AgentAppSiteStatusRepository;
import com.along.wormhole.admin.dto.AgentAppSiteStatusDto;
import com.along.wormhole.admin.dto.appsite.AppSiteDto;
import com.along.wormhole.admin.dto.task.TaskAppDto;
import com.along.wormhole.admin.dto.task.TaskDto;
import com.along.wormhole.admin.entity.AgentAppSiteStatusEntity;
import com.along101.wormhole.admin.common.utils.ConvertUtils;
import com.along101.wormhole.admin.common.utils.DomainPortUtils;
import com.along101.wormhole.admin.dao.AgentAppSiteStatusRepository;
import com.along101.wormhole.admin.dto.AgentAppSiteStatusDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteDto;
import com.along101.wormhole.admin.dto.task.TaskAppDto;
import com.along101.wormhole.admin.dto.task.TaskDto;
import com.along101.wormhole.admin.entity.AgentAppSiteStatusEntity;
import com.along101.wormhole.admin.dao.AgentAppSiteStatusRepository;
import com.along101.wormhole.admin.dto.AgentAppSiteStatusDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteDto;
import com.along101.wormhole.admin.dto.task.TaskAppDto;
import com.along101.wormhole.admin.dto.task.TaskDto;
import com.along101.wormhole.admin.entity.AgentAppSiteStatusEntity;
import com.along101.wormhole.admin.manager.appsite.AppSiteManager;
import com.along101.wormhole.admin.manager.task.TaskManager;
import com.along101.wormhole.admin.common.utils.ConvertUtils;
import com.along101.wormhole.admin.common.utils.DomainPortUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by yinzuolong on 2017/8/1.
 */
@Component
public class AgentStatusManager {

    @Autowired
    private AgentAppSiteStatusRepository agentAppSiteStatusRepository;
    @Autowired
    private AppSiteManager appSiteManager;
    @Autowired
    private TaskManager taskManager;

    public List<AgentAppSiteStatusDto> getAgentStatusBySiteId(Long siteId) {
        List<AgentAppSiteStatusEntity> agentStatuses = agentAppSiteStatusRepository.getAgentStatusBySiteId(siteId);
        List<AgentAppSiteStatusDto> statusDtos = ConvertUtils.convert(agentStatuses, AgentAppSiteStatusDto.class);
        Optional<AppSiteDto> appSiteDto = appSiteManager.getById(siteId);
        Optional<TaskDto> taskDto = taskManager.getMaxTaskBySiteId(siteId);
        statusDtos.forEach(statusDto -> {
            appSiteDto.ifPresent(dto -> statusDto.setCurrentAppSiteVersion(dto.getOnlineVersion()));
            taskDto.ifPresent(dto -> statusDto.setCurrentTaskId(dto.getId()));
        });
        return statusDtos;
    }

    @Transactional
    public void updateAgentStatus(String ip, List<TaskAppDto> taskApps) {
        List<Long> appSiteIds = taskApps.stream().map(TaskAppDto::getAppSiteId).collect(Collectors.toList());
        List<AppSiteDto> appSiteDtos = appSiteManager.getByIds(appSiteIds);
        Map<Long, AppSiteDto> appSiteMap = appSiteDtos.stream().collect(Collectors.toMap(AppSiteDto::getId, Function.identity()));
        List<AgentAppSiteStatusEntity> agentStatusEntities = agentAppSiteStatusRepository.getAgentStatus(ip, appSiteIds);
        Map<Long, AgentAppSiteStatusEntity> agentStatusMap = agentStatusEntities.stream()
                .collect(Collectors.toMap(AgentAppSiteStatusEntity::getAppSiteId, Function.identity()));
        List<AgentAppSiteStatusEntity> updateEntities = taskApps.stream().map(taskAppDto -> {
            AgentAppSiteStatusEntity entity = agentStatusMap.get(taskAppDto.getAppSiteId());
            AppSiteDto appSiteDto = appSiteMap.get(taskAppDto.getAppSiteId());
            if (entity == null) {
                entity = new AgentAppSiteStatusEntity();
                entity.setAgentAppSiteVersion(0L);
                entity.setAgentTaskId(0L);
            }
            entity.setSlbId(appSiteDto.getSlbId());
            entity.setAgentIp(ip);
            entity.setAppSiteId(appSiteDto.getId());
            entity.setDomain(appSiteDto.getDomain());
            entity.setPort(appSiteDto.getPort());
            entity.setDomainPort(DomainPortUtils.getDomainPort(appSiteDto.getDomain(), appSiteDto.getPort()));
            entity.setMsg(taskAppDto.getMsg());
            entity.setStatus(taskAppDto.getStatus());
            //task成功，更新在线版本
            if (taskAppDto.getStatus() == 1) {
                entity.setAgentAppSiteVersion(taskAppDto.getVersion());
                entity.setAgentTaskId(taskAppDto.getTaskId());
            }
            return entity;
        }).collect(Collectors.toList());
        agentAppSiteStatusRepository.save(updateEntities);
    }

}
