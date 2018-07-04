package com.along101.wormhole.admin.common.utils;

/**
 * Created by yinzuolong on 2017/7/29.
 */
public class DomainPortUtils {

    public static String getDomainPort(String domain, Integer port) {
        return domain + "_" + port;
    }
}
