package com.along101.wormhole.admin.aop;

import com.along101.wormhole.admin.config.UserAuditorAware;
import com.along101.wormhole.admin.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by huangyinhuang on 7/20/2017.
 */
@Aspect
@Component
@Slf4j
@Order(12)
public class AuthorizationAspect {

    @Autowired
    private AuthService authService;

    @Before("com.along101.wormhole.admin.aop.ResourcePointCuts.webController()")
    public void checkPermission(JoinPoint joinPoint) throws Throwable {
        // read user name from request attribute
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String userName = (String) request.getAttribute("slb.audit.username");

        if (userName != null && !userName.equals(UserAuditorAware.DEFAULT_SYSTEM_NAME)) {
            if (!authService.hasUser(userName)) {
                authService.addUser(userName);
            } else {
                authService.updateLastVisitTime(userName);
            }
        }
        // TODO: check user permission
    }

}
