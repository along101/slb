package com.along101.wormhole.admin.manager.agent;

import com.along.wormhole.admin.common.utils.ConvertUtils;
import com.along.wormhole.admin.dao.AgentTaskResultRepository;
import com.along.wormhole.admin.dto.AgentTaskResultDto;
import com.along.wormhole.admin.dto.PageDto;
import com.along.wormhole.admin.dto.slb.SlbServerDto;
import com.along.wormhole.admin.dto.task.TaskAppDto;
import com.along.wormhole.admin.entity.AgentTaskResultEntity;
import com.along101.wormhole.admin.common.utils.ConvertUtils;
import com.along101.wormhole.admin.dao.AgentTaskResultRepository;
import com.along101.wormhole.admin.dto.AgentTaskResultDto;
import com.along101.wormhole.admin.dto.PageDto;
import com.along101.wormhole.admin.dto.slb.SlbServerDto;
import com.along101.wormhole.admin.dto.task.TaskAppDto;
import com.along101.wormhole.admin.entity.AgentTaskResultEntity;
import com.along101.wormhole.admin.common.utils.ConvertUtils;
import com.along101.wormhole.admin.dao.AgentTaskResultRepository;
import com.along101.wormhole.admin.dto.AgentTaskResultDto;
import com.along101.wormhole.admin.dto.PageDto;
import com.along101.wormhole.admin.dto.slb.SlbServerDto;
import com.along101.wormhole.admin.dto.task.TaskAppDto;
import com.along101.wormhole.admin.entity.AgentTaskResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by yinzuolong on 2017/8/1.
 */
@Component
public class AgentTaskResultManager {

    @Autowired
    private AgentTaskResultRepository agentTaskResultRepository;

    public void addTaskResult(SlbServerDto slbServerDto, List<TaskAppDto> taskApps) {
        List<AgentTaskResultEntity> agentTaskResultEntities = taskApps.stream().map(taskAppDto -> {
            AgentTaskResultEntity agentTaskResult = new AgentTaskResultEntity();
            agentTaskResult.setAgentIp(slbServerDto.getIp());
            agentTaskResult.setSlbId(slbServerDto.getSlbId());
            agentTaskResult.setAppSiteId(taskAppDto.getAppSiteId());
            agentTaskResult.setAppSiteVersion(taskAppDto.getVersion());
            agentTaskResult.setMsg(taskAppDto.getMsg());
            agentTaskResult.setStatus(taskAppDto.getStatus());
            agentTaskResult.setTaskId(taskAppDto.getTaskId());
            return agentTaskResult;
        }).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(agentTaskResultEntities))
            agentTaskResultRepository.save(agentTaskResultEntities);
    }

    public PageDto<AgentTaskResultDto> getTaskResultBySiteId(Long siteId, int page, int size) {
        PageRequest pageRequest = new PageRequest(page, size);
        Page<AgentTaskResultEntity> entities = agentTaskResultRepository.getTaskResultBySiteId(siteId, pageRequest);
        return ConvertUtils.convertPage(entities, AgentTaskResultDto.class);
    }

    /**
     * 获取agent已经处理最大的TaskId
     *
     * @param agentIp
     * @return
     */
    public Long getProcessedMaxTaskId(String agentIp) {
        Long taskId = agentTaskResultRepository.getAgentMaxTaskId(agentIp);
        return Optional.ofNullable(taskId).orElse(0L);
    }
}
