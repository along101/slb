package com.along101.wormhole.admin;

import com.alibaba.fastjson.JSON;
import com.along101.wormhole.admin.dto.appsite.AppSiteDto;
import org.junit.Test;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by yinzuolong on 2017/7/18.
 */
public class TestHelper {

    public static <T> T getTestObject(String rs, Class<T> clazz) throws Exception {
        return JSON.parseObject(readSourceFile(rs), clazz);
    }

    public static <T> List<T> getTestList(String rs, Class<T> clazz) throws Exception {
        return JSON.parseArray(readSourceFile(rs), clazz);
    }

    public static String readSourceFile(String rs) throws Exception {
        URI uri = TestHelper.class.getClassLoader().getResource(rs).toURI();
        byte[] b = Files.readAllBytes(Paths.get(uri));
        return new String(b, "UTF-8");
    }

    @Test
    public void testGetTestObject() throws Exception {
        AppSiteDto appSiteDto = TestHelper.getTestObject("service/AppSiteDto1.json", AppSiteDto.class);
        System.out.println(JSON.toJSONString(appSiteDto));
    }

    @Test
    public void testGetTestList() throws Exception {
        List<AppSiteDto> ls = TestHelper.getTestList("service/AppSiteDto2.json", AppSiteDto.class);
        System.out.println(JSON.toJSONString(ls));
    }
}
