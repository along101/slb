package com.along101.wormhole.admin.service;

import com.along101.wormhole.admin.SlbAdminServer;
import com.along101.wormhole.admin.TestHelper;
import com.along101.wormhole.admin.dao.SlbRepository;
import com.along101.wormhole.admin.dao.SlbServerRepository;
import com.along101.wormhole.admin.dto.slb.SlbDto;
import com.along101.wormhole.admin.dto.slb.SlbServerDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by yinzuolong on 2017/7/13.
 */
@RunWith(SpringRunner.class)
@Transactional
@Rollback
@SpringBootTest(classes = SlbAdminServer.class)
public class SlbServiceTest {

    @Autowired
    private SlbRepository slbRepository;
    @Autowired
    private SlbServerRepository slbServerRepository;
    @Autowired
    private SlbService slbService;

    @Before
    public void init() {
        slbRepository.removeAll();
        slbServerRepository.removeAll();
    }

    @Test
    public void testCreateAndGetAllSlbs() throws Exception {
        List<SlbDto> slbDtos = TestHelper.getTestList("service/SlbDto2.json", SlbDto.class);
        slbDtos.forEach(slbDto -> slbService.createSlb(slbDto));
        List<SlbDto> findSlbDtos = slbService.getAllSlbs();
        System.out.println(findSlbDtos);
        Assert.assertEquals(slbDtos.size(), findSlbDtos.size());
    }

    @Test
    public void testCreateAndQueryByName() throws Exception {
        List<SlbDto> slbDtos = TestHelper.getTestList("service/SlbDto2.json", SlbDto.class);
        slbDtos.forEach(slbDto -> slbService.createSlb(slbDto));
        List<SlbDto> findSlbDtos = slbService.queryByName("Slb1");
        System.out.println(findSlbDtos);
        Assert.assertEquals(1, findSlbDtos.size());
    }

    @Test
    public void testGetSlbById() throws Exception {
        List<SlbDto> slbDtos = TestHelper.getTestList("service/SlbDto2.json", SlbDto.class);
        slbDtos.forEach(slbDto -> slbService.createSlb(slbDto));
        List<SlbDto> findSlbDtos = slbService.getAllSlbs();
        Optional<SlbDto> find = slbService.getSlbById(findSlbDtos.get(0).getId());
        Assert.assertTrue(find.isPresent());
        Assert.assertEquals(2, find.orElse(null).getSlbServers().size());
        find = slbService.getSlbById(0L);
        Assert.assertTrue(!find.isPresent());
    }

    @Test
    public void testUpdateSlb() throws Exception {
        List<SlbDto> slbDtos = TestHelper.getTestList("service/SlbDto2.json", SlbDto.class);
        slbDtos.forEach(slbDto -> slbService.createSlb(slbDto));
        //update : update name and server host
        SlbDto findSlbDto = slbService.getAllSlbs().get(0);
        findSlbDto.setName("update_" + findSlbDto.getName());
        findSlbDto.getSlbServers().forEach(slbServerDto ->
                slbServerDto.setHostName("update_" + slbServerDto.getHostName()));
        slbService.updateSlb(findSlbDto);
        SlbDto findUpdateSlbDo = slbService.getSlbById(findSlbDto.getId()).orElse(null);
        Assert.assertTrue(findUpdateSlbDo.getName().startsWith("update_"));
        findUpdateSlbDo.getSlbServers().forEach(slbServerDo ->
                Assert.assertTrue(slbServerDo.getHostName().startsWith("update_")));
        //update : remove server
        findUpdateSlbDo.getSlbServers().remove(0);
        slbService.updateSlb(findUpdateSlbDo);
        findUpdateSlbDo = slbService.getSlbById(findSlbDto.getId()).orElse(null);
        Assert.assertEquals(1, findUpdateSlbDo.getSlbServers().size());
        //update : not remove server
        findUpdateSlbDo.setSlbServers(null);
        findUpdateSlbDo = slbService.getSlbById(findUpdateSlbDo.getId()).orElse(null);
        Assert.assertEquals(1, findUpdateSlbDo.getSlbServers().size());
    }

    @Test
    public void testDeleteSlb() throws Exception {
        List<SlbDto> slbDtos = TestHelper.getTestList("service/SlbDto2.json", SlbDto.class);
        slbDtos.forEach(slbDto -> slbService.createSlb(slbDto));
        slbService.deleteSlb(slbDtos.get(0).getId());
        Optional<SlbDto> findSlbDto = slbService.getSlbById(slbDtos.get(0).getId());
        Assert.assertTrue(!findSlbDto.isPresent());
    }

    @Test
    public void testAddServers() throws Exception {
        List<SlbDto> slbDtos = TestHelper.getTestList("service/SlbDto2.json", SlbDto.class);
        slbDtos.forEach(slbDto -> slbService.createSlb(slbDto));
        SlbDto slbDto = slbService.getAllSlbs().get(0);
        //ip重复，抛出异常
        try {
            slbService.addServers(slbDto.getId(), slbDto.getSlbServers());
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
        SlbServerDto server = slbDto.getSlbServers().get(0);
        server.setIp("192.168.0.11");
        server.setHostName("test192");
        slbService.addServers(slbDto.getId(), Collections.singletonList(server));
        Optional<SlbDto> find = slbService.getSlbById(slbDto.getId());
        Assert.assertEquals(3, find.orElse(null).getSlbServers().size());
    }

    @Test
    public void testDeleteServers() throws Exception {
        List<SlbDto> slbDtos = TestHelper.getTestList("service/SlbDto2.json", SlbDto.class);
        slbDtos.forEach(slbDto -> slbService.createSlb(slbDto));
        SlbDto slbDto = slbService.getAllSlbs().get(0);
        List<Long> serverIds = slbDto.getSlbServers().stream().map(SlbServerDto::getId).collect(Collectors.toList());
        slbService.deleteServers(slbDto.getId(), serverIds);
        Optional<SlbDto> find = slbService.getSlbById(slbDto.getId());
        Assert.assertEquals(0, find.orElse(null).getSlbServers().size());

    }

}
