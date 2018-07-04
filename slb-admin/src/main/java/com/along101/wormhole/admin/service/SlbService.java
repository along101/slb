package com.along101.wormhole.admin.service;

import com.along.wormhole.admin.common.exception.SlbServiceException;
import com.along.wormhole.admin.dto.slb.SlbDto;
import com.along.wormhole.admin.dto.slb.SlbServerDto;
import com.along.wormhole.admin.manager.slb.SlbManager;
import com.along.wormhole.admin.service.validate.SlbValidate;
import com.along101.wormhole.admin.common.exception.SlbServiceException;
import com.along101.wormhole.admin.dto.slb.SlbDto;
import com.along101.wormhole.admin.dto.slb.SlbServerDto;
import com.along101.wormhole.admin.manager.slb.SlbManager;
import com.along101.wormhole.admin.service.validate.SlbValidate;
import com.google.common.base.Preconditions;
import com.along101.wormhole.admin.common.exception.SlbServiceException;
import com.along101.wormhole.admin.dto.slb.SlbDto;
import com.along101.wormhole.admin.dto.slb.SlbServerDto;
import com.along101.wormhole.admin.manager.appsite.AppSiteManager;
import com.along101.wormhole.admin.manager.slb.SlbManager;
import com.along101.wormhole.admin.service.validate.SlbValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by yinzuolong on 2017/7/11.
 */
@Service
@Slf4j
public class SlbService {

    @Autowired
    private SlbManager slbManager;
    @Autowired
    private AppSiteManager appSiteManager;
    @Autowired
    private SlbValidate slbValidate;

    //获取所有slb
    public List<SlbDto> getAllSlbs() {
        return slbManager.getAllSlbs();
    }

    //获取所有slb
    public List<SlbDto> queryByName(String name) {
        return slbManager.queryByName(name);
    }
    public Optional<SlbDto> getSlbById(Long slbId) {
        return slbManager.getSlbById(slbId);
    }

    @Transactional
    public SlbDto createSlb(SlbDto slbDto) {
        Preconditions.checkNotNull(slbDto, "slb object can not be null");
        slbValidate.validateSlbNameUnique(slbDto);
        slbDto.setId(null);
        slbManager.saveSlb(slbDto);
        if (!CollectionUtils.isEmpty(slbDto.getSlbServers())) {
            List<SlbServerDto> newServers = slbManager.addServers(slbDto.getId(), slbDto.getSlbServers());
            slbDto.setSlbServers(newServers);
        }
        return slbDto;
    }

    @Transactional
    public SlbDto updateSlb(SlbDto slbDto) {
        Preconditions.checkNotNull(slbDto, "slb object can not be null");
        //查找slb是否存在
        if (!slbManager.getSlbById(slbDto.getId()).isPresent())
            throw SlbServiceException.newException("slb not found, id=%s", slbDto.getId());
        slbValidate.validateSlbNameUnique(slbDto);
        slbManager.saveSlb(slbDto);
        //如果slbDto.getSlbServers为null，则不更新slbServers
        if (slbDto.getSlbServers() != null) {
            List<SlbServerDto> newServers = slbManager.saveServers(slbDto.getId(), slbDto.getSlbServers());
            slbDto.setSlbServers(newServers);
        }
        return slbDto;
    }

    @Transactional
    public void deleteSlb(Long slbId) {
        Preconditions.checkNotNull(slbId, "slb Id can not be null");
        if (!slbManager.getSlbById(slbId).isPresent())
            throw SlbServiceException.newException("slb not found, id=%s", slbId);
        //判断slb下的AppSite
        if (!CollectionUtils.isEmpty(appSiteManager.getBySlbId(slbId))) {
            throw SlbServiceException.newException("exist appSite in slb, id=%s", slbId);
        }
        slbManager.deleteSlb(slbId);
    }

    @Transactional
    public SlbServerDto addServer(Long slbId, SlbServerDto slbServerDto) {
        Preconditions.checkNotNull(slbId, "slb Id can not be null");
        Preconditions.checkNotNull(slbServerDto, "slbServer can not be null");
        addServers(slbId, Collections.singletonList(slbServerDto));
        return slbServerDto;
    }

    @Transactional
    public void deleteServer(Long slbId, Long serverId) {
        Preconditions.checkNotNull(slbId, "slb Id can not be null");
        Preconditions.checkNotNull(serverId, "server Id can not be null");
        deleteServers(slbId, Collections.singletonList(serverId));
    }

    @Transactional
    public void addServers(Long slbId, List<SlbServerDto> slbServers) {
        Preconditions.checkNotNull(slbId, "slb Id can not be null");
        Preconditions.checkNotNull(slbServers, "slbServers can not be null");
        if (!slbManager.getSlbById(slbId).isPresent())
            throw SlbServiceException.newException("slb not found, id=%s", slbId);
        slbManager.addServers(slbId, slbServers);
    }

    @Transactional
    public void deleteServers(Long slbId, List<Long> serverIds) {
        Preconditions.checkNotNull(slbId, "slb Id can not be null");
        Preconditions.checkNotNull(serverIds, "slbServers can not be null");
        if (!slbManager.getSlbById(slbId).isPresent())
            throw SlbServiceException.newException("slb not found, id=%s", slbId);
        slbManager.deleteServers(serverIds);
    }

    /**
     * 发送心跳
     *
     * @param ip
     */
    @Transactional
    public void agentHeartBeat(String ip) {
        Preconditions.checkNotNull(ip, "ip can not be null");
        slbManager.updateAgentHeartBeat(ip);
    }

}
