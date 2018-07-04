package com.along101.wormhole.admin.manager.nginx;

import com.alibaba.fastjson.JSON;
import com.along.wormhole.admin.common.constant.OnlineStatusEnum;
import com.along.wormhole.admin.common.exception.SlbServiceException;
import com.along.wormhole.admin.common.utils.ConvertUtils;
import com.along.wormhole.admin.dto.appsite.AppSiteDto;
import com.along.wormhole.admin.dto.appsite.AppSiteServerDto;
import com.along.wormhole.admin.manager.nginx.build.NginxConfBuilder;
import com.along.wormhole.admin.manager.nginx.model.*;
import com.along101.wormhole.admin.common.constant.OnlineStatusEnum;
import com.along101.wormhole.admin.common.exception.SlbServiceException;
import com.along101.wormhole.admin.common.utils.ConvertUtils;
import com.along101.wormhole.admin.dto.appsite.AppSiteDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteServerDto;
import com.along101.wormhole.admin.manager.nginx.build.NginxConfBuilder;
import com.along101.wormhole.admin.manager.nginx.model.*;
import com.google.common.base.Preconditions;
import com.along101.wormhole.admin.common.constant.OnlineStatusEnum;
import com.along101.wormhole.admin.common.exception.SlbServiceException;
import com.along101.wormhole.admin.common.utils.ConvertUtils;
import com.along101.wormhole.admin.dto.appsite.AppSiteDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteServerDto;
import com.along101.wormhole.admin.manager.nginx.build.NginxConfBuilder;
import com.along101.wormhole.admin.manager.nginx.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by yinzuolong on 2017/7/11.
 */
@Component
public class NginxConfManager {
    public static final String UPSTREAM_PREFIX = "up_";
    public static final String TEMPLATE_CONFIG = "config";
    public static final String TEMPLATE_SERVER = "server";
    public static final String TEMPLATE_UPSTREAM = "upstream";
    public static final String TEMPLATE_UPSTREAMSERVER = "upstreamServer";
    @Autowired
    private NginxConfBuilder nginxConfBuilder;

    private String merge(String template, NginxConfig nginxConfig) {
        Map<String, Object> context = new HashMap<>();
        context.put(TEMPLATE_CONFIG, nginxConfig);
        return merge(template, context);
    }

    private String merge(String template, Map<String, Object> context) {
        try {
            return nginxConfBuilder.merge(template, context);
        } catch (Exception e) {
            throw SlbServiceException.newException("merge nginx config error, template='%s' context: %s",
                    TEMPLATE_CONFIG, JSON.toJSON(context), e);
        }
    }

    public String getNginxConf(NginxConfig nginxConfig) {
        Preconditions.checkNotNull(nginxConfig);
        return merge(TEMPLATE_CONFIG, nginxConfig);
    }

    public String getServerConf(NginxConfig nginxConfig) {
        Preconditions.checkNotNull(nginxConfig);
        return merge(TEMPLATE_SERVER, nginxConfig);
    }

    public String getUpstreamConf(NginxConfig nginxConfig) {
        Preconditions.checkNotNull(nginxConfig);
        return merge(TEMPLATE_UPSTREAM, nginxConfig);
    }

    public Optional<String> getUpstreamServersConf(NginxConfig nginxConfig, String upstreamName) {
        Optional<NginxUpstream> us = nginxConfig.getUpstreams().stream()
                .filter(nginxUpstream -> nginxUpstream.getName().equals(upstreamName))
                .findFirst();
        return us.map(nginxUpstream -> {
            StringBuilder sb = new StringBuilder();
            nginxUpstream.getServers().forEach(nginxUpstreamServer -> {
                Map<String, Object> context = new HashMap<>();
                context.put(TEMPLATE_UPSTREAMSERVER, nginxUpstreamServer);
                sb.append(merge(TEMPLATE_UPSTREAMSERVER, context));
            });
            return sb.toString();
        });
    }

    /**
     * 检查两个站点信息生成nginx配置是否变更
     *
     * @param appSiteDto1
     * @param appSiteDto2
     * @return
     */
    public boolean checkChange(AppSiteDto appSiteDto1, AppSiteDto appSiteDto2) {
        Optional<NginxConfig> optional1 = convert(Collections.singletonList(appSiteDto1));
        Optional<NginxConfig> optional2 = convert(Collections.singletonList(appSiteDto2));
        String config1 = optional1.map(this::getNginxConf).orElse("");
        String config2 = optional2.map(this::getNginxConf).orElse("");
        return config1.equals(config2);
    }

    public Optional<String> getNginxConf(AppSiteDto appSiteDto) {
        return getNginxConf(Collections.singletonList(appSiteDto));
    }

    public Optional<String> getNginxConf(List<AppSiteDto> appSiteDtos) {
        return convert(appSiteDtos).map(this::getNginxConf);
    }

    /**
     * 将相同域名端口的一组appSiteDto转换成NginxConfig，域名端口取list中第一个对象的
     *
     * @param appSiteDtos
     * @return
     */
    public Optional<NginxConfig> convert(List<AppSiteDto> appSiteDtos) {
        Preconditions.checkNotNull(appSiteDtos);
        appSiteDtos.sort(Comparator.comparing(AppSiteDto::getAppId));
        List<NginxUpstream> upstreams = new ArrayList<>();
        List<NginxLocation> nginxLocations = new ArrayList<>();
        appSiteDtos.forEach(appSiteDto -> {
            //下线返回
            if (appSiteDto.getStatus() == OnlineStatusEnum.OFFLINE.getValue()) {
                return;
            }

            AppSiteDto tmpAppSiteDto = ConvertUtils.convert(appSiteDto, AppSiteDto.class);

            List<AppSiteServerDto> appSiteServers = appSiteDto.getAppSiteServers();
            List<AppSiteServerDto> tmpAppSiteServers = ConvertUtils.convert(appSiteServers, AppSiteServerDto.class);
            //只保留 ip 和 port 都不为空的 server
            tmpAppSiteServers = tmpAppSiteServers.stream().filter(appSiteServer -> appSiteServer.getIp() != null && appSiteServer.getPort() != null).collect(Collectors.toList());

            tmpAppSiteDto.setAppSiteServers(tmpAppSiteServers);

            //servers为空返回
            if (CollectionUtils.isEmpty(tmpAppSiteDto.getAppSiteServers())) {
                return;
            } else {
                Optional<AppSiteServerDto> optional = tmpAppSiteDto.getAppSiteServers().stream()
                        .filter(serverDto -> serverDto.getStatus() == OnlineStatusEnum.ONLINE.getValue()).findAny();
                //所有server都下线返回
                if (!optional.isPresent()) {
                    return;
                }
            }
            //locations
            nginxLocations.add(convertLocation(tmpAppSiteDto));
            upstreams.add(convertUpstream(tmpAppSiteDto));

        });
        //no location or upstreams return
        if (CollectionUtils.isEmpty(nginxLocations) || CollectionUtils.isEmpty(upstreams)) {
            return Optional.empty();
        }
        //server
        NginxServer nginxServer = new NginxServer();
        nginxServer.setLocations(nginxLocations);
        nginxServer.setListen(appSiteDtos.get(0).getPort());
        nginxServer.setServerName(appSiteDtos.get(0).getDomain());
        //config
        NginxConfig nginxConfig = new NginxConfig();
        nginxConfig.setServer(nginxServer);
        nginxConfig.setUpstreams(upstreams);
        return Optional.of(nginxConfig);
    }

    private NginxLocation convertLocation(AppSiteDto appSiteDto) {
        NginxLocation nginxLocation = new NginxLocation();
        nginxLocation.setPattern(appSiteDto.getPath());
        nginxLocation.setUpstream(UPSTREAM_PREFIX + appSiteDto.getId());
        return nginxLocation;
    }

    private NginxUpstream convertUpstream(AppSiteDto appSiteDto) {
        NginxUpstream nginxUpstream = new NginxUpstream();
        nginxUpstream.setName(UPSTREAM_PREFIX + appSiteDto.getId());
        List<NginxUpstreamServer> upstreamServers = new ArrayList<>();
        appSiteDto.getAppSiteServers().sort((o1, o2) -> {
            String str1 = o1.getName() + o1.getIp() + o1.getPort();
            String str2 = o2.getName() + o2.getIp() + o2.getPort();
            return str1.compareTo(str2);
        });
        appSiteDto.getAppSiteServers().forEach(appSiteServerDto -> {
            if (appSiteServerDto.getStatus() == OnlineStatusEnum.OFFLINE.getValue()) {
                return;
            }
            upstreamServers.add(convertUpstreamServer(appSiteServerDto));
        });
        nginxUpstream.setServers(upstreamServers);
        return nginxUpstream;
    }

    private NginxUpstreamServer convertUpstreamServer(AppSiteServerDto appSiteServerDto) {
        NginxUpstreamServer upstreamServer = new NginxUpstreamServer();
        upstreamServer.setIp(appSiteServerDto.getIp());
        Optional.ofNullable(appSiteServerDto.getPort()).ifPresent(upstreamServer::setPort);
        Optional.ofNullable(appSiteServerDto.getWeight()).ifPresent(upstreamServer::setWeight);
        Optional.ofNullable(appSiteServerDto.getMaxFails()).ifPresent(upstreamServer::setMaxFails);
        Optional.ofNullable(appSiteServerDto.getFailTimeout()).ifPresent(upstreamServer::setFailTimeout);
        return upstreamServer;
    }
}
