package com.along101.wormhole.admin.manager.nginx.build;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yinzuolong on 2017/7/11.
 */
@Component
@ConditionalOnProperty(name = "pslb.nginx.template.resource", havingValue = "class")
public class ClasspathNginxConfBuilder implements NginxConfBuilder {

    private VelocityEngine ve;
    private String path = "template/nginx/";
    private Map<String, String> templateMap = new ConcurrentHashMap<>();

    public ClasspathNginxConfBuilder() {
        ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "class");
        ve.setProperty("class.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.setProperty("class.resource.loader.cache", true);
        ve.setProperty("class.resource.loader.modificationCheckInterval", "-1");
        ve.setProperty("input.encoding", "UTF-8");
        ve.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
        ve.init();
    }

    @Override
    public String merge(String template, Map<String, Object> context) throws Exception {
        StringWriter out = new StringWriter();
        VelocityContext vContext = new VelocityContext();
        for (Map.Entry<String, Object> entry : context.entrySet()) {
            vContext.put(entry.getKey(), entry.getValue());
        }
        ve.evaluate(vContext, out, "dynamic template", getTemplateContent(template));
        return out.toString();
    }

    public String getTemplateContent(String template) throws Exception {
        if (templateMap.containsKey(template)) {
            return templateMap.get(template);
        }
        String value = readTemplateContent(template);
        templateMap.put(template, value);
        return value;
    }

    private String readTemplateContent(String template) throws Exception {
        ClassPathResource resource = new ClassPathResource(path + template + ".vm");
        try (InputStream in = resource.getInputStream()) {
            return StreamUtils.copyToString(in, StandardCharsets.UTF_8);
        }
//        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(path + template + ".vm")) {
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            byte[] b = new byte[1024];
//            int n;
//            while ((n = in.read(b)) > 0) {
//                bos.write(b, 0, n);
//            }
//            return new String(bos.toByteArray(), "UTF-8");
//        }
    }
}
