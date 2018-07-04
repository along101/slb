package com.along101.wormhole.admin.service;

import com.along.wormhole.admin.common.exception.SlbServiceException;
import com.along.wormhole.admin.common.utils.DomainPortUtils;
import com.along.wormhole.admin.dto.PageDto;
import com.along.wormhole.admin.dto.appsite.AppSiteArchiveDto;
import com.along.wormhole.admin.dto.appsite.AppSiteDto;
import com.along.wormhole.admin.dto.slb.SlbDto;
import com.along.wormhole.admin.dto.slb.SlbServerDto;
import com.along.wormhole.admin.dto.task.TaskAppDto;
import com.along.wormhole.admin.dto.task.TaskConfGetResponse;
import com.along.wormhole.admin.dto.task.TaskDomainDto;
import com.along.wormhole.admin.dto.task.TaskDto;
import com.along.wormhole.admin.manager.agent.AgentStatusManager;
import com.along.wormhole.admin.manager.agent.AgentTaskResultManager;
import com.along.wormhole.admin.manager.appsite.AppSiteArchiveManager;
import com.along.wormhole.admin.manager.nginx.NginxConfManager;
import com.along.wormhole.admin.manager.nginx.model.NginxConfig;
import com.along.wormhole.admin.manager.slb.SlbManager;
import com.along.wormhole.admin.manager.task.TaskManager;
import com.along101.wormhole.admin.common.exception.SlbServiceException;
import com.along101.wormhole.admin.common.utils.DomainPortUtils;
import com.along101.wormhole.admin.dto.PageDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteArchiveDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteDto;
import com.along101.wormhole.admin.dto.slb.SlbDto;
import com.along101.wormhole.admin.dto.slb.SlbServerDto;
import com.along101.wormhole.admin.dto.task.TaskAppDto;
import com.along101.wormhole.admin.dto.task.TaskConfGetResponse;
import com.along101.wormhole.admin.dto.task.TaskDomainDto;
import com.along101.wormhole.admin.dto.task.TaskDto;
import com.along101.wormhole.admin.manager.agent.AgentStatusManager;
import com.along101.wormhole.admin.manager.agent.AgentTaskResultManager;
import com.along101.wormhole.admin.manager.appsite.AppSiteArchiveManager;
import com.along101.wormhole.admin.manager.nginx.NginxConfManager;
import com.along101.wormhole.admin.manager.nginx.model.NginxConfig;
import com.along101.wormhole.admin.manager.slb.SlbManager;
import com.along101.wormhole.admin.manager.task.TaskManager;
import com.google.common.base.Preconditions;
import com.along101.wormhole.admin.common.exception.SlbServiceException;
import com.along101.wormhole.admin.common.utils.DomainPortUtils;
import com.along101.wormhole.admin.dto.AgentTaskResultDto;
import com.along101.wormhole.admin.dto.PageDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteArchiveDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteDto;
import com.along101.wormhole.admin.dto.slb.SlbDto;
import com.along101.wormhole.admin.dto.slb.SlbServerDto;
import com.along101.wormhole.admin.dto.task.TaskAppDto;
import com.along101.wormhole.admin.dto.task.TaskConfGetResponse;
import com.along101.wormhole.admin.dto.task.TaskDomainDto;
import com.along101.wormhole.admin.dto.task.TaskDto;
import com.along101.wormhole.admin.manager.agent.AgentStatusManager;
import com.along101.wormhole.admin.manager.agent.AgentTaskResultManager;
import com.along101.wormhole.admin.manager.appsite.AppSiteArchiveManager;
import com.along101.wormhole.admin.manager.nginx.NginxConfManager;
import com.along101.wormhole.admin.manager.nginx.model.NginxConfig;
import com.along101.wormhole.admin.manager.slb.SlbManager;
import com.along101.wormhole.admin.manager.task.TaskManager;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by yinzuolong on 2017/7/11.
 */
@Service
@Log4j
public class AgentTaskService {
    @Autowired
    private SlbManager slbManager;
    @Autowired
    private AppSiteArchiveManager archiveManager;
    @Autowired
    private TaskManager taskManager;
    @Autowired
    private AgentTaskResultManager agentTaskResultManager;
    @Autowired
    private AgentStatusManager agentStatusManager;
    @Autowired
    private NginxConfManager nginxConfManager;

    //获取agent要执行的任务
    public TaskConfGetResponse getTaskConf(String agentIp, Long lastTaskId) {
        Preconditions.checkNotNull(agentIp, "agentIp can not be null.");
        Optional<SlbServerDto> optional = slbManager.getSlbServerByIp(agentIp);
        if (!optional.isPresent()) {
            throw SlbServiceException.newException("can not find agent in slb ,ip='%s'", agentIp);
        }
        Optional<SlbDto> slbDto = slbManager.getSlbById(optional.get().getSlbId());
        if (!slbDto.isPresent()) {
            throw SlbServiceException.newException("can not find agent in slb ,ip='%s'", agentIp);
        }
        TaskConfGetResponse response = new TaskConfGetResponse();
        response.setNginxConf(slbDto.get().getNginxConf());
        response.setNginxSbin(slbDto.get().getNginxSbin());
        if (lastTaskId == null || lastTaskId == 0) {
            lastTaskId = agentTaskResultManager.getProcessedMaxTaskId(agentIp);
        }

        //找到所有需要执行的task
        List<TaskDto> taskDtos = taskManager.getMaxTaskGroupByAppSite(slbDto.get().getId(), lastTaskId);
        if (CollectionUtils.isEmpty(taskDtos)) {
            response.setSuc(true);
            response.setFailMsg("already newest");
            return response;
        }
        response.setLastTaskId(Collections.max(taskDtos.stream().map(TaskDto::getId).collect(Collectors.toList())));
        //根据domain归并task
        Map<String, List<TaskDto>> domainTaskMap = taskDtos.stream().collect(Collectors.groupingBy(TaskDto::getDomainPort));
        //找到所有需要修改的domain
        List<String> domains = new ArrayList<>(domainTaskMap.keySet());
        //根据域找到归档
        List<AppSiteArchiveDto> appSiteArchiveDtos = archiveManager.getCurrentByDomains(domains);
        //根据域名归并AppSite
        Map<String, List<AppSiteDto>> domainAppSiteMap = appSiteArchiveDtos.stream().map(AppSiteArchiveDto::getAppSiteDto)
                .collect(Collectors.groupingBy(appSiteDto -> DomainPortUtils.getDomainPort(appSiteDto.getDomain(), appSiteDto.getPort())));

        domainAppSiteMap.forEach((domainPort, appSiteDtos) -> {
            TaskDomainDto taskDomainDto = new TaskDomainDto();
            taskDomainDto.setDomain(appSiteDtos.get(0).getDomain());
            taskDomainDto.setPort(appSiteDtos.get(0).getPort());
            //TODO 后续增加dyups操作
            taskDomainDto.setOptType(1);
            List<TaskAppDto> taskAppDtos = domainTaskMap.get(domainPort).stream().map(taskDto -> {
                TaskAppDto taskAppDto = new TaskAppDto();
                taskAppDto.setAppSiteId(taskDto.getAppSiteId());
                taskAppDto.setAppId(taskDto.getAppId());
                taskAppDto.setTaskId(taskDto.getId());
                taskAppDto.setVersion(taskDto.getAppSiteVersion());
                return taskAppDto;
            }).collect(Collectors.toList());
            Optional<NginxConfig> nginxConfig = nginxConfManager.convert(appSiteDtos);
            if (nginxConfig.isPresent()) {
                String conf = nginxConfManager.getNginxConf(nginxConfig.get());
                taskDomainDto.setConf(conf);
            }
            taskDomainDto.setTaskApps(taskAppDtos);
            response.getTaskDomains().add(taskDomainDto);
        });
        response.setSuc(true);
        return response;
    }

    //处理agent执行任务的结果
    @Transactional
    public void processTaskResult(String agentIp, List<TaskAppDto> taskAppDtos) {
        if (CollectionUtils.isEmpty(taskAppDtos)) {
            return;
        }
        Optional<SlbServerDto> serverDto = slbManager.getSlbServerByIp(agentIp);
        if (!serverDto.isPresent()) {
            throw SlbServiceException.newException("can not find agent in slb ,ip='%s'", agentIp);
        }
        //增加task执行结果
        agentTaskResultManager.addTaskResult(serverDto.get(), taskAppDtos);
        //更新task状态
        taskAppDtos.forEach(taskAppDto -> {
            TaskDto taskDto = taskManager.getTask(taskAppDto.getTaskId());
            taskManager.processStatus(taskDto);
        });
        //更新agent状态
        agentStatusManager.updateAgentStatus(agentIp, taskAppDtos);
    }

    public PageDto<AgentTaskResultDto> getAgentTaskResultBySiteId(Long siteId, int page, int size) {
        return agentTaskResultManager.getTaskResultBySiteId(siteId, page, size);
    }
}
