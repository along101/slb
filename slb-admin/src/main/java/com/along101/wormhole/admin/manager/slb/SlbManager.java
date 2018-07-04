package com.along101.wormhole.admin.manager.slb;

import com.along.wormhole.admin.common.utils.ConvertUtils;
import com.along.wormhole.admin.dao.SlbRepository;
import com.along.wormhole.admin.dao.SlbServerRepository;
import com.along.wormhole.admin.dto.slb.SlbDto;
import com.along.wormhole.admin.dto.slb.SlbServerDto;
import com.along.wormhole.admin.entity.SlbEntity;
import com.along.wormhole.admin.entity.SlbServerEntity;
import com.along.wormhole.admin.service.validate.SlbValidate;
import com.along101.wormhole.admin.common.utils.ConvertUtils;
import com.along101.wormhole.admin.dao.SlbRepository;
import com.along101.wormhole.admin.dao.SlbServerRepository;
import com.along101.wormhole.admin.dto.slb.SlbDto;
import com.along101.wormhole.admin.dto.slb.SlbServerDto;
import com.along101.wormhole.admin.entity.SlbEntity;
import com.along101.wormhole.admin.entity.SlbServerEntity;
import com.along101.wormhole.admin.service.validate.SlbValidate;
import com.along101.wormhole.admin.common.utils.ConvertUtils;
import com.along101.wormhole.admin.dao.SlbRepository;
import com.along101.wormhole.admin.dao.SlbServerRepository;
import com.along101.wormhole.admin.dto.slb.SlbDto;
import com.along101.wormhole.admin.dto.slb.SlbServerDto;
import com.along101.wormhole.admin.entity.SlbEntity;
import com.along101.wormhole.admin.entity.SlbServerEntity;
import com.along101.wormhole.admin.service.validate.SlbValidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by yinzuolong on 2017/7/29.
 */
@Component
public class SlbManager {

    @Autowired
    private SlbRepository slbRepository;
    @Autowired
    private SlbServerRepository slbServerRepository;
    @Autowired
    private SlbValidate slbValidate;

    @Transactional
    public SlbDto saveSlb(SlbDto slbDto) {
        SlbEntity slbEntity = ConvertUtils.convert(slbDto, SlbEntity.class);
        final SlbEntity newSlbEntity = slbRepository.save(slbEntity);
        slbRepository.flush();
        return ConvertUtils.convert(newSlbEntity, slbDto);
    }

    @Transactional
    public Boolean deleteSlb(Long slbId) {
        slbRepository.remove(slbId);
        slbServerRepository.removeBySlbId(slbId);
        return Boolean.TRUE;
    }

    //获取所有slb
    public List<SlbDto> getAllSlbs() {
        List<SlbEntity> slbEntities = slbRepository.findAll();
        List<SlbDto> slbDtos = ConvertUtils.convert(slbEntities, SlbDto.class);
        setSlbServerDto(slbDtos);
        return slbDtos;
    }

    public List<SlbDto> queryByName(String name){
        List<SlbEntity> slbEntities = slbRepository.findByName(name);
        List<SlbDto> slbDtos = ConvertUtils.convert(slbEntities, SlbDto.class);
        setSlbServerDto(slbDtos);
        return slbDtos;
    }
    private void setSlbServerDto(List<SlbDto> slbDtos) {
        if (CollectionUtils.isEmpty(slbDtos))
            return;
        List<Long> slbIds = slbDtos.stream().map(SlbDto::getId).collect(Collectors.toList());
        List<SlbServerEntity> slbServerEntities = slbServerRepository.findBySlbIds(slbIds);
        List<SlbServerDto> slbServerDtos = ConvertUtils.convert(slbServerEntities, SlbServerDto.class);
        Map<Long, List<SlbServerDto>> slbServerMap = slbServerDtos.stream().collect(Collectors.groupingBy(SlbServerDto::getSlbId));
        slbDtos.forEach(slbDto -> slbDto.setSlbServers(slbServerMap.getOrDefault(slbDto.getId(), new ArrayList<>())));
    }

    public Optional<SlbDto> getSlbById(Long slbId) {
        SlbEntity slbEntity = slbRepository.findOne(slbId);
        return Optional.ofNullable(slbEntity).map(entity -> {
            SlbDto slb = ConvertUtils.convert(slbEntity, SlbDto.class);
            setSlbServerDto(Collections.singletonList(slb));
            return slb;
        });
    }

    public Optional<SlbServerDto> getSlbServerById(Long slbServerId) {
        SlbServerEntity slbServerEntity = slbServerRepository.findById(slbServerId);
        return Optional.ofNullable(slbServerEntity).map(entity -> ConvertUtils.convert(slbServerEntity, SlbServerDto.class));
    }

    public Optional<SlbServerDto> getSlbServerByIp(String ip) {
        SlbServerEntity slbServerEntity = slbServerRepository.findByIp(ip);
        return Optional.ofNullable(slbServerEntity).map(entity -> ConvertUtils.convert(slbServerEntity, SlbServerDto.class));
    }

    public List<SlbServerDto> getSlbServerDtos(Long slbId) {
        List<SlbServerEntity> slbServerEntities = slbServerRepository.findBySlbId(slbId);
        return ConvertUtils.convert(slbServerEntities, SlbServerDto.class);
    }

    @Transactional
    public List<SlbServerDto> addServers(Long slbId, List<SlbServerDto> slbServers) {
        List<String> ips = slbServers.stream().map(SlbServerDto::getIp).collect(Collectors.toList());
        slbValidate.validateIpUnique(ips);
        slbServers.forEach(slbServerDto -> {
            slbServerDto.setSlbId(slbId);
            slbServerDto.setId(null);
        });
        List<SlbServerEntity> slbServerEntities = ConvertUtils.convert(slbServers, SlbServerEntity.class);
        List<SlbServerEntity> newServerEntities = slbServerRepository.save(slbServerEntities);
        slbServerRepository.flush();
        return ConvertUtils.convert(newServerEntities, SlbServerDto.class);
    }

    @Transactional
    public List<SlbServerDto> saveServers(Long slbId, List<SlbServerDto> slbServers) {
        //先删除，再插入
        slbServerRepository.deleteBySlbId(slbId);
        slbServerRepository.flush();
        //校验ip存在
        List<String> ips = slbServers.stream().map(SlbServerDto::getIp).collect(Collectors.toList());
        slbValidate.validateIpUnique(ips);
        return addServers(slbId, slbServers);
    }

    @Transactional
    public void deleteServers(List<Long> serverIds) {
        slbServerRepository.removeByIds(serverIds);
        slbServerRepository.flush();
    }

    public void updateAgentHeartBeat(String ip) {
        slbServerRepository.agentHeartBeat(ip);
    }
}
