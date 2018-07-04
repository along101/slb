package com.along101.wormhole.admin.controller;

import com.along101.wormhole.admin.dto.AgentTaskResultDto;
import com.along101.wormhole.admin.dto.PageDto;
import com.along101.wormhole.admin.dto.Response;
import com.along101.wormhole.admin.dto.AgentAppSiteStatusDto;
import com.along101.wormhole.admin.service.AgentStatusService;
import com.along101.wormhole.admin.service.AgentTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by yinzuolong on 2017/7/12.
 */
@RestController
@RequestMapping("/web/agent")
@Slf4j
public class AgentController {

    @Autowired
    private AgentStatusService agentStatusService;
    @Autowired
    private AgentTaskService agentTaskService;

    //获取appSite在各个agent上的版本
    @RequestMapping(value = "/appSite/{id}", method = RequestMethod.GET)
    public Response<List<AgentAppSiteStatusDto>> getAgentStatusBySiteId(@PathVariable("id") Long appSiteId) {
        List<AgentAppSiteStatusDto> statusDtos = agentStatusService.getAgentStatusBySiteId(appSiteId);
        return Response.success(statusDtos);
    }

    @RequestMapping(value = "/taskResult/appSite/{siteId}", method = RequestMethod.GET)
    public Response<PageDto<AgentTaskResultDto>> getAgentTaskResultBySiteId(@PathVariable("siteId") Long siteId,
                                                                            @RequestParam("page") int page,
                                                                            @RequestParam("size") int size) {
        PageDto<AgentTaskResultDto> results = agentTaskService.getAgentTaskResultBySiteId(siteId, page, size);
        return Response.success(results);
    }
}
