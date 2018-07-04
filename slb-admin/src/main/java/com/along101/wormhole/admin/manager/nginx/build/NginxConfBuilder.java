package com.along101.wormhole.admin.manager.nginx.build;

import java.util.Map;

/**
 * Created by yinzuolong on 2017/7/11.
 */
public interface NginxConfBuilder {
    String merge(String template, Map<String, Object> context) throws Exception;
}
