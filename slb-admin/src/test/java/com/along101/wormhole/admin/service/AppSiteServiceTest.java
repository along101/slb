package com.along101.wormhole.admin.service;

import com.alibaba.fastjson.JSON;
import com.along101.wormhole.admin.SlbAdminServer;
import com.along101.wormhole.admin.TestHelper;
import com.along101.wormhole.admin.common.exception.SlbServiceException;
import com.along101.wormhole.admin.dto.PageDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteServerDto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yinzuolong on 2017/7/18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SlbAdminServer.class)
@Transactional
@Rollback
public class AppSiteServiceTest {

    @Autowired
    private AppSiteService appSiteService;

    @Autowired
    private JdbcTemplate jdbcTemplate;


//    @Before
//    public void before() throws Exception {
//        String sql = "INSERT INTO slb.slb " +
//                "(id, name, nginx_conf, nginx_sbin, insert_time, insert_by, update_time, update_by, is_active) " +
//                "VALUES(1, 'test', '/usr/local/nginx/conf', '/usr/local/nginx/sbin', NULL, NULL, NULL, NULL, 1)";
//        jdbcTemplate.execute(sql);
//        sql = "delete from slb.app_site";
//        jdbcTemplate.execute(sql);
//    }

    @Test
    public void testCreateAppSite() throws Exception {
        AppSiteDto appSiteDto = TestHelper.getTestObject("service/AppSiteDto1.json", AppSiteDto.class);
        AppSiteDto newAppSiteDto = appSiteService.createAppSite(appSiteDto);
        Assert.assertNotNull(newAppSiteDto);
        System.out.println(JSON.toJSONString(newAppSiteDto));
    }

    @Test
    public void testUpdateAppSite() throws Exception {
        AppSiteDto appSiteDto = TestHelper.getTestObject("service/AppSiteDto1.json", AppSiteDto.class);
        AppSiteDto newAppSiteDto = appSiteService.createAppSite(appSiteDto);
        newAppSiteDto.setPath("/test");
        newAppSiteDto.getAppSiteServers().remove(0);
        AppSiteDto updateAppSite = appSiteService.updateAppSite(newAppSiteDto);
        Assert.assertEquals("/test", updateAppSite.getPath());
        Assert.assertEquals(1, updateAppSite.getAppSiteServers().size());
    }

    @Test
    public void testDeleteAppSite() throws Exception {
        AppSiteDto appSiteDto = TestHelper.getTestObject("service/AppSiteDto1.json", AppSiteDto.class);
        AppSiteDto newAppSiteDto = appSiteService.createAppSite(appSiteDto);
        appSiteService.deleteAppSite(newAppSiteDto.getId());
        AppSiteDto findAppSiteDto = appSiteService.getById(newAppSiteDto.getId()).orElse(null);
        Assert.assertNull(findAppSiteDto);
    }

    @Test
    public void testGetAll() throws Exception {
        List<AppSiteDto> appSiteDtos = TestHelper.getTestList("service/AppSiteDto2.json", AppSiteDto.class);
        appSiteDtos.forEach(appSiteDto -> appSiteService.createAppSite(appSiteDto));
        PageDto<AppSiteDto> finds = appSiteService.getAll(0, 2);
        Assert.assertEquals(2, finds.getContent().size());
        Assert.assertEquals(appSiteDtos.get(0).getAppId(), finds.getContent().get(0).getAppId());
        Assert.assertEquals(appSiteDtos.get(1).getAppId(), finds.getContent().get(1).getAppId());
    }

    @Test
    public void testGetByAppId() throws Exception {
        List<AppSiteDto> appSiteDtos = TestHelper.getTestList("service/AppSiteDto2.json", AppSiteDto.class);
        appSiteDtos.forEach(appSiteDto -> appSiteService.createAppSite(appSiteDto));
        AppSiteDto appSiteDto = appSiteService.getByAppId(1L, appSiteDtos.get(3).getAppId()).orElse(null);
        Assert.assertEquals(appSiteDtos.get(3).getAppId(), appSiteDto.getAppId());
    }


    @Test
    public void testGetAppSiteServers() throws Exception {
        List<AppSiteDto> appSiteDtos = TestHelper.getTestList("service/AppSiteDto2.json", AppSiteDto.class);
        appSiteDtos.forEach(appSiteDto -> appSiteService.createAppSite(appSiteDto));
        AppSiteDto appSiteDto = appSiteService.getByAppId(1L, appSiteDtos.get(0).getAppId()).orElse(null);
        List<AppSiteServerDto> servers = appSiteService.getAppSiteServers(appSiteDto.getId());
        Assert.assertEquals(appSiteDto.getAppSiteServers().size(), servers.size());
    }

    @Test
    public void testAddServers() throws Exception {
        List<AppSiteDto> appSiteDtos = TestHelper.getTestList("service/AppSiteDto2.json", AppSiteDto.class);
        appSiteDtos.forEach(appSiteDto -> appSiteService.createAppSite(appSiteDto));
        AppSiteDto appSiteDto = appSiteService.getByAppId(1L, appSiteDtos.get(0).getAppId()).orElse(null);
        Exception exception = null;
        try {
            //重复
            List<AppSiteServerDto> appSiteServerDtos1 = TestHelper.getTestList("service/AppSiteServerDto1.json", AppSiteServerDto.class);
            appSiteService.addAppSiteServers(appSiteDto.getId(), appSiteServerDtos1);
        } catch (SlbServiceException e) {
            exception = e;
        }
        Assert.assertNotNull(exception);
        List<AppSiteServerDto> servers = appSiteService.getAppSiteServers(appSiteDto.getId());
        Assert.assertEquals(2, servers.size());
        //不重复
        List<AppSiteServerDto> appSiteServerDtos2 = TestHelper.getTestList("service/AppSiteServerDto2.json", AppSiteServerDto.class);
        appSiteService.addAppSiteServers(appSiteDto.getId(), appSiteServerDtos2);
        servers = appSiteService.getAppSiteServers(appSiteDto.getId());
        Assert.assertEquals(4, servers.size());
    }

    @Test
    public void testUpdateServer() throws Exception {
        List<AppSiteDto> appSiteDtos = TestHelper.getTestList("service/AppSiteDto2.json", AppSiteDto.class);
        appSiteDtos.forEach(appSiteDto -> appSiteService.createAppSite(appSiteDto));
        AppSiteDto appSiteDto = appSiteService.getByAppId(1L, appSiteDtos.get(0).getAppId()).orElse(null);
        List<AppSiteServerDto> servers = appSiteDto.getAppSiteServers();
        Long id = servers.get(0).getId();
        servers.get(0).setHostName("testUpdate");
        servers.get(0).setIp("10.0.0.123");
        appSiteService.updateAppSiteServers(appSiteDto.getId(), servers);
        servers = appSiteService.getAppSiteServers(appSiteDto.getId());
        List<String> ips = servers.stream()
                .map(AppSiteServerDto::getIp).collect(Collectors.toList());
        List<String> hosts = servers.stream()
                .map(AppSiteServerDto::getHostName).collect(Collectors.toList());

        Assert.assertTrue(ips.contains("10.0.0.123"));
        Assert.assertTrue(hosts.contains("testUpdate"));
    }

    @Test
    public void testDeleteServers() throws Exception {
        List<AppSiteDto> appSiteDtos = TestHelper.getTestList("service/AppSiteDto2.json", AppSiteDto.class);
        appSiteDtos.forEach(appSiteDto -> appSiteService.createAppSite(appSiteDto));
        AppSiteDto appSiteDto = appSiteService.getByAppId(1L, appSiteDtos.get(0).getAppId()).orElse(null);
        List<Long> ids = appSiteDto.getAppSiteServers().stream().map(AppSiteServerDto::getId).collect(Collectors.toList());
        appSiteService.deleteSiteServers(appSiteDto.getId(), ids);
        AppSiteDto newAppSiteDto = appSiteService.getByAppId(1L, appSiteDtos.get(0).getAppId()).orElse(null);
        Assert.assertTrue(CollectionUtils.isEmpty(newAppSiteDto.getAppSiteServers()));
    }

}
