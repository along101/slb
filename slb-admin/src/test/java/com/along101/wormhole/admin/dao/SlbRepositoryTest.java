package com.along101.wormhole.admin.dao;

import com.along101.wormhole.admin.SlbAdminServer;
import com.along101.wormhole.admin.entity.SlbEntity;
import com.along101.wormhole.admin.entity.SlbServerEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by yinzuolong on 2017/7/11.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SlbAdminServer.class)
public class SlbRepositoryTest {

    @Autowired
    private SlbRepository slbRepository;

    @Autowired
    private SlbServerRepository slbServerRepository;

    @Test
    public void testFindById() throws Exception {
        SlbEntity slbEntity = slbRepository.findOne(1L);
        Assert.assertNotNull(slbEntity);
    }

    @Test
    public void name() throws Exception {
        SlbServerEntity serverEntity = slbServerRepository.findByIp("12.10.0.1");
        System.out.println(serverEntity);
    }
}
