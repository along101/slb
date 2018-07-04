package com.along101.wormhole.admin.common.utils;

import com.along101.wormhole.admin.entity.SlbEntity;
import com.along101.wormhole.admin.dto.slb.SlbDto;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by yinzuolong on 2017/7/14.
 */
public class BeanUtilsTest {


    @Test
    public void test1() throws Exception {
        SlbEntity slbEntity = new SlbEntity();
        slbEntity.setInsertTime(new Timestamp(System.currentTimeMillis()));

        SlbDto slbDo = new SlbDto();
        BeanUtils.copyProperties(slbEntity, slbDo);

        Assert.assertNotNull(slbDo.getInsertTime());
    }

    @Test
    public void name() throws Exception {
        A a = new A();
        a.setTest1("test");
        a.setT(new Timestamp(System.currentTimeMillis()));

        B b = new B();
        BeanUtils.copyProperties(a, b);
        Assert.assertNotNull(b.getT());
    }


    @Data
    static class A {
        String test1;
        Date t;
    }

    @Data
    static class B {
        String test1;
        Date t;
    }
}
