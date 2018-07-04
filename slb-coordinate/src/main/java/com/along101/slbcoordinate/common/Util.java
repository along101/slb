package com.along101.slbcoordinate.common;

import com.along101.slbcoordinate.dao.SlbLockRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class Util {
    @Autowired
    private Environment env;
    @Autowired
    private SlbLockRepository slbLockRepository;
    private String ip;

    @PostConstruct
    private void initial() {
        ip = env.getProperty("spring.cloud.client.ipAddress");
    }

    public String getIp() {
        return ip;
    }

    // 健康检查周期
    public int checkInterval() {
        return Integer.parseInt(env.getProperty("slb.checkInterval", "10"));
    }

    // 是否开启健康检查
    public boolean checkEnabled() {
        return Boolean.parseBoolean(env.getProperty("slb.checkEnabled", "true"));
    }

    public Integer getReadTimeOut() {
        return Integer.parseInt(env.getProperty("slb.okhttpReadTimeOut", "2"));
    }

    public Integer getMaxConnections() {
        return Integer.parseInt(env.getProperty("slb.maxConnections", "5000"));
    }

    public Integer getConnectTimeOut() {
        return Integer.parseInt(env.getProperty("slb.okhttpConnectTimeOut", "3"));
    }

    // 健康检查周期
    public int checkHeartbeatInterval() {
        return Integer.parseInt(env.getProperty("slb.checkHeartbeatInterval", "2"));
    }

    private String getSlbUrl() {
        return env.getProperty("slb.slbUrl", "");
    }

    public String getSlbInfoUrl() {
        return getSlbUrl() + "/api/appSite/servers/getAll?page=0&size=20000";
    }

    public String getSlbCheckUpInUrl() {
        return getSlbUrl() + "/api/operation/updateServersIpPort?operate=HEALTH_CHECK_UP";
    }

    public String getSlbCheckDownInUrl() {
        return getSlbUrl() + "/api/operation/updateServersIpPort?operate=HEALTH_CHECK_DOWN";
    }

    public String getEurekaInfoUrl() {
        return env.getProperty("slb.eurekaUrl", "");
    }

    public Date getDbNow() {
        return slbLockRepository.getNow();
    }

    public <E> List<List<E>> split(List<E> lst, int count) {
        List<List<E>> rs = Lists.newArrayList();
        if (lst != null) {
            List<E> temp = null;
            for (int i = 0; i < lst.size(); i++) {
                if (i % count == 0) {
                    if (temp != null) {
                        rs.add(temp);
                    }
                    temp = new ArrayList<>(count);
                }
                temp.add(lst.get(i));
            }
            if (temp != null) {
                rs.add(temp);
            }
        }

        return rs;
    }
}
