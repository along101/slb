package com.along101.wormhole.admin.service.validate;

import com.along101.wormhole.admin.common.exception.SlbServiceException;
import com.along101.wormhole.admin.dao.SlbRepository;
import com.along101.wormhole.admin.dao.SlbServerRepository;
import com.along101.wormhole.admin.dto.slb.SlbDto;
import com.along101.wormhole.admin.entity.SlbEntity;
import com.along101.wormhole.admin.entity.SlbServerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by yinzuolong on 2017/7/31.
 */
@Component
public class SlbValidate {
    @Autowired
    private SlbRepository slbRepository;
    @Autowired
    private SlbServerRepository slbServerRepository;

    public void validateSlbNameUnique(SlbDto slbDto) {
        List<SlbEntity> slbEntities = slbRepository.findAll();
        Optional<SlbEntity> find = slbEntities.stream()
                .filter(slbEntity -> !slbEntity.getId().equals(slbDto.getId()) && slbEntity.getName().equals(slbDto.getName())).findAny();
        find.ifPresent(slbEntity -> {
            throw SlbServiceException.newException("slb name '%s' already exist.", slbDto.getName());
        });
    }

    public void validateIpUnique(List<String> ips) {
        Map<String, Long> group = ips.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        List<String> duplicates = group.entrySet().stream().filter(entry -> entry.getValue() > 1).map(Map.Entry::getKey).collect(Collectors.toList());
        if (duplicates.size() > 0) {
            throw SlbServiceException.newException(String.format("ip duplicates: %s.", duplicates));
        }
        List<String> exists = slbServerRepository.findAll().stream().map(SlbServerEntity::getIp).collect(Collectors.toList());
        exists.retainAll(ips);
        if (exists.size() > 0) {
            throw SlbServiceException.newException(String.format("ip duplicates: %s.", exists));
        }
    }
}
