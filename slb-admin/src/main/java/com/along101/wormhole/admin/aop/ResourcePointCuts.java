package com.along101.wormhole.admin.aop;

import org.aspectj.lang.annotation.Pointcut;

/**
 * Created by huangyinhuang on 7/17/2017.
 */
public class ResourcePointCuts {

    @Pointcut("execution(public * com.along101.wormhole.admin.controller..*.*(..))")
    public void webController(){}

    @Pointcut("execution(public * com.along101.wormhole.admin.api..*.*(..))")
    public void apiController(){}
}
