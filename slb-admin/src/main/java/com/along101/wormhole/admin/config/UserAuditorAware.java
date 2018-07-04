package com.along101.wormhole.admin.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by huangyinhuang on 5/18/2017.
 */
@Slf4j
public class UserAuditorAware implements AuditorAware<String> {

    public static final String DEFAULT_SYSTEM_NAME = "system";

    @Override
    public String getCurrentAuditor() {
        String userName = DEFAULT_SYSTEM_NAME;

        try {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            userName = (String) requestAttributes.getAttribute("slb.audit.username", 0);
        } catch (Exception e) {
            log.info("Not able to read the user name by servlet requests. Probably it's a system call.");
        }

        return userName;
    }

}
