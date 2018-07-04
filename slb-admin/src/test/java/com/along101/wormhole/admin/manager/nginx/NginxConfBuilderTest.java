package com.along101.wormhole.admin.manager.nginx;

import com.along101.wormhole.admin.TestHelper;
import com.along101.wormhole.admin.manager.nginx.build.ClasspathNginxConfBuilder;
import com.along101.wormhole.admin.manager.nginx.model.NginxConfig;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yinzuolong on 2017/7/25.
 */
public class NginxConfBuilderTest {
    @Test
    public void testBuildConfig() throws Exception {
        ClasspathNginxConfBuilder builder = new ClasspathNginxConfBuilder();
        NginxConfig config = TestHelper.getTestObject("nginx/NginxConfig1.json", NginxConfig.class);
        Map<String, Object> context = new HashMap<>();
        context.put(NginxConfManager.TEMPLATE_CONFIG, config);
        String rs = builder.merge(NginxConfManager.TEMPLATE_CONFIG, context);
        System.out.println(rs);
    }

    @Test
    public void testBuildServer() throws Exception {
        ClasspathNginxConfBuilder builder = new ClasspathNginxConfBuilder();
        NginxConfig config = TestHelper.getTestObject("nginx/NginxConfig1.json", NginxConfig.class);
        Map<String, Object> context = new HashMap<>();
        context.put(NginxConfManager.TEMPLATE_CONFIG, config);
        String rs = builder.merge(NginxConfManager.TEMPLATE_SERVER, context);
        System.out.println(rs);
    }

    @Test
    public void testUpstream() throws Exception {
        ClasspathNginxConfBuilder builder = new ClasspathNginxConfBuilder();
        NginxConfig config = TestHelper.getTestObject("nginx/NginxConfig1.json", NginxConfig.class);
        Map<String, Object> context = new HashMap<>();
        context.put(NginxConfManager.TEMPLATE_CONFIG, config);
        String rs = builder.merge(NginxConfManager.TEMPLATE_UPSTREAM, context);
        System.out.println(rs);
    }

    @Test
    public void testUpstreamServer() throws Exception {
        ClasspathNginxConfBuilder builder = new ClasspathNginxConfBuilder();
        NginxConfig config = TestHelper.getTestObject("nginx/NginxConfig1.json", NginxConfig.class);
        Map<String, Object> context = new HashMap<>();
        context.put(NginxConfManager.TEMPLATE_UPSTREAMSERVER, config.getUpstreams().get(0).getServers().get(0));
        String rs = builder.merge(NginxConfManager.TEMPLATE_UPSTREAMSERVER, context);
        System.out.println(rs);
    }
}
