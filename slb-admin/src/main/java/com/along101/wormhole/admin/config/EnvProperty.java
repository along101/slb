package com.along101.wormhole.admin.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Created by huangyinhuang on 7/21/2017.
 */
@Component
@Slf4j
public class EnvProperty {

    @Value("${pslb.jwtcheck.enable}")
    public Boolean JWT_CHECK_ENABLE;

    @Autowired
    private Environment environment;

    public String getProperty(String property) {
        return environment.getProperty(property);
    }

}
