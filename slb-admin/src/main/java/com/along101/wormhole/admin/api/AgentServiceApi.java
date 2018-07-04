package com.along101.wormhole.admin.api;

import com.along.wormhole.admin.dto.task.TaskConfGetResponse;
import com.along101.wormhole.admin.dto.task.TaskConfGetResponse;
import com.along101.wormhole.admin.common.exception.SlbServiceException;
import com.along101.wormhole.admin.dto.BaseResponse;
import com.along101.wormhole.admin.dto.task.TaskConfGetRequest;
import com.along101.wormhole.admin.dto.task.TaskConfGetResponse;
import com.along101.wormhole.admin.dto.task.TaskStatusUpdateRequest;
import com.along101.wormhole.admin.service.AgentTaskService;
import com.along101.wormhole.admin.service.SlbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by yinzuolong on 2017/7/11.
 */
@RestController
@RequestMapping("/api/agent")
@Slf4j
public class AgentServiceApi {
    @Autowired
    private AgentTaskService agentTaskService;
    @Autowired
    private SlbService slbService;

    //agent拉取task
    @PostMapping(value = "/getConf")
    public TaskConfGetResponse getConf(@RequestBody TaskConfGetRequest request) {
        try {
            TaskConfGetResponse response = agentTaskService.getTaskConf(request.getIp(), request.getLastTaskId());
            log.info("success getConf request={}, result={}", request, response);
            return response;
        } catch (SlbServiceException e) {
            log.warn("error getconf request={}", request, e);
            TaskConfGetResponse response = new TaskConfGetResponse();
            response.setSuc(false);
            response.setFailMsg(e.getMessage());
            return response;
        } catch (Throwable e) {
            log.error("error getconf request={}", request, e);
            TaskConfGetResponse response = new TaskConfGetResponse();
            response.setSuc(false);
            response.setFailMsg(e.getMessage());
            return response;
        }
    }

    //上报任务执行结果
    @PostMapping("/updateStatus")
    public BaseResponse updateStatus(@RequestBody TaskStatusUpdateRequest request) {
        try {
            agentTaskService.processTaskResult(request.getIp(), request.getTaskApps());
            log.info("success updateStatus request={}", request);
            BaseResponse response = new BaseResponse();
            response.setSuc(true);
            return response;
        } catch (SlbServiceException e) {
            log.warn("error updateStatus request={}", request, e);
            BaseResponse response = new BaseResponse();
            response.setSuc(false);
            response.setFailMsg(e.getMessage());
            return response;
        } catch (Throwable e) {
            log.error("error updateStatus request={}", request, e);
            BaseResponse response = new BaseResponse();
            response.setSuc(false);
            response.setFailMsg(e.getMessage());
            return response;
        }
    }

    @PostMapping("/heartBeat")
    public BaseResponse heartBeat(@Param("ip") String ip) {
        try {
            slbService.agentHeartBeat(ip);
            log.info("success heartBeat request={}", ip);
            BaseResponse response = new BaseResponse();
            response.setSuc(true);
            return response;
        } catch (SlbServiceException e) {
            log.warn("error getconf ip={}", ip, e);
            BaseResponse response = new BaseResponse();
            response.setSuc(false);
            response.setFailMsg(e.getMessage());
            return response;
        } catch (Throwable e) {
            log.error("error getconf request={}", ip, e);
            BaseResponse response = new BaseResponse();
            response.setSuc(false);
            response.setFailMsg(e.getMessage());
            return response;
        }
    }
}
