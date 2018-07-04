package com.along101.wormhole.admin.manager.nginx;

import com.along101.wormhole.admin.SlbAdminServer;
import com.along101.wormhole.admin.TestHelper;
import com.along101.wormhole.admin.dto.appsite.AppSiteDto;
import com.along101.wormhole.admin.manager.nginx.model.NginxConfig;
import com.along101.wormhole.admin.service.converter.AppSiteServerConverter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

/**
 * Created by yinzuolong on 2017/7/26.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SlbAdminServer.class)
public class NginxConfManagerTest {
    @Autowired
    private NginxConfManager nginxConfManager;

    @Test
    public void testGetNginxConf() throws Exception {
        Optional<NginxConfig> nginxConfig = nginxConfManager.convert(getAppSiteDtos());
        String nginxConf = nginxConfManager.getNginxConf(nginxConfig.orElse(null));
        System.out.println(nginxConf);
    }

    @Test
    public void testGetServerConf() throws Exception {
        Optional<NginxConfig> nginxConfig = nginxConfManager.convert(getAppSiteDtos());
        String nginxConf = nginxConfManager.getServerConf(nginxConfig.orElse(null));
        System.out.println(nginxConf);
    }

    @Test
    public void testGetUpstreamConf() throws Exception {
        Optional<NginxConfig> nginxConfig = nginxConfManager.convert(getAppSiteDtos());
        String nginxConf = nginxConfManager.getUpstreamConf(nginxConfig.orElse(null));
        System.out.println(nginxConf);
    }

    @Test
    public void testGetUpstreamServersConf() throws Exception {
        List<AppSiteDto> appSiteDtos = getAppSiteDtos();
        Optional<NginxConfig> nginxConfig = nginxConfManager.convert(appSiteDtos);
        Optional<String> nginxConf = nginxConfManager.getUpstreamServersConf(nginxConfig.orElse(null),
                NginxConfManager.UPSTREAM_PREFIX + appSiteDtos.get(0).getAppId());
        Assert.assertNotNull(nginxConf.orElse(null));
        System.out.println(nginxConf.orElse(null));
    }

    @Test
    public void testConvert() throws Exception {
        List<AppSiteDto> appSiteDtos = getAppSiteDtos();
        Optional<NginxConfig> nginxConfig = nginxConfManager.convert(appSiteDtos);
        Assert.assertTrue(nginxConfig.isPresent());
        Assert.assertEquals(2, nginxConfig.orElse(null).getUpstreams().size());
        Assert.assertEquals(2, nginxConfig.orElse(null).getServer().getLocations().size());
        Assert.assertEquals(1, nginxConfig.orElse(null).getUpstreams().get(0).getServers().size());
    }

    private List<AppSiteDto> getAppSiteDtos() throws Exception {
        List<AppSiteDto> appSiteDtos = TestHelper.getTestList("service/AppSiteDto4.json", AppSiteDto.class);
        appSiteDtos.forEach(
                appSiteDto -> appSiteDto.getAppSiteServers().forEach(
                        appSiteServerDto -> appSiteServerDto.setStatus(AppSiteServerConverter.getServerFinalStatus(appSiteServerDto.getStatusDetail()))));
        return appSiteDtos;
    }
}
