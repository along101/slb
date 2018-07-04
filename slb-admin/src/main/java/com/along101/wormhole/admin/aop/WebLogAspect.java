package com.along101.wormhole.admin.aop;

import com.alibaba.fastjson.JSON;
import com.along101.wormhole.admin.entity.AuditLogEntity;
import com.along101.wormhole.admin.manager.AuditManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

/**
 * Created by huangyinhuang on 2017/7/18.
 */
@Aspect
@Component
@Slf4j
@Order(10)
public class WebLogAspect {

    @Autowired
    private AuditManager auditService;

    @Around("com.along101.wormhole.admin.aop.ResourcePointCuts.webController()")
    public Object logAccessAudit(ProceedingJoinPoint apiMethod) throws Throwable {
        log.info("logAccessAudit");
        Object retVal = apiMethod.proceed();
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = (HttpServletRequest) attributes.getRequest();
            String userName = (String) request.getAttribute("audit.username");
            String http_uri = request.getRequestURI();
            String http_method = request.getMethod();
            String client_ip = request.getRemoteAddr();
            String class_method = apiMethod.getSignature().getDeclaringTypeName() + "." + apiMethod.getSignature().getName();
            String class_method_args = Arrays.toString(apiMethod.getArgs());

            String rsLog = "USER NAME: " + userName +
                    ",HTTP_METHOD : " + http_method +
                    ",HTTP_URI : " + http_uri +
                    ",IP : " + client_ip +
                    ",CLASS_METHOD : " + class_method +
                    ",CLASS_METHOD_ARGS : " + class_method_args;

            log.info(rsLog);
            AuditLogEntity action = new AuditLogEntity();
            action.setUserName(userName);
            action.setClientIp(request.getRemoteAddr());
            action.setHttpMethod(http_method);
            action.setHttpUri(http_uri);
            action.setClientIp(client_ip);
            action.setClassMethod(class_method);
            action.setClassMethodArgs(class_method_args);

            String result = JSON.toJSONString(retVal);
            result = StringUtils.substring(result, 0, 1024);
            action.setClassMethodReturn(result);

            auditService.recordOperation(action);
        } catch (Exception e) {
            log.error("An exception happened when trying to log access info.");
        }
        return retVal;
    }

}
