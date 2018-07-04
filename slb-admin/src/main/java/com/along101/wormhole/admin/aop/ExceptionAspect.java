package com.along101.wormhole.admin.aop;

import com.along101.wormhole.admin.common.exception.SlbServiceException;
import com.along101.wormhole.admin.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Created by huangyinhuang on 7/17/2017.
 */
@Aspect
@Component
@Slf4j
@Order(11)
public class ExceptionAspect {

    @Around("com.along101.wormhole.admin.aop.ResourcePointCuts.webController()")
    public Object handleException(ProceedingJoinPoint apiMethod) {
        log.info("try to handle exception thrown from controller method");
        Object retVal;
        try {
            retVal = apiMethod.proceed();
        } catch (SlbServiceException e) {
            log.warn(e.getMessage());
            retVal = Response.error(e);
        } catch (Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
            retVal = Response.error(String.format("unknow error, message key is: %s.", MDC.get("uuid")));
        }
        return retVal;
    }

}
