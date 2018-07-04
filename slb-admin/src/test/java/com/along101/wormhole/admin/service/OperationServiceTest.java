package com.along101.wormhole.admin.service;

import com.along101.wormhole.admin.SlbAdminServer;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by yinzuolong on 2017/7/21.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SlbAdminServer.class)
@Transactional
@Rollback
public class OperationServiceTest {

//    @Autowired
//    private OperationService operationService;
//    @Autowired
//    private AppSiteService appSiteService;
//    @Autowired
//    private AppSiteArchiveService appSiteArchiveService;
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    @Before
//    public void before() throws Exception {
//        String sql = "INSERT INTO slb.slb " +
//                "(id, name, nginx_conf, nginx_sbin, insert_time, insert_by, update_time, update_by, is_active) " +
//                "VALUES(1, 'test', '/usr/local/nginx/conf', '/usr/local/nginx/sbin', NULL, NULL, NULL, NULL, 1)";
//        jdbcTemplate.execute(sql);
//        sql = "delete from slb.app_site";
//        jdbcTemplate.execute(sql);
//    }
//
//    @Test
//    public void testDeployAppSite() throws Exception {
//        AppSiteDto appSiteDto = TestHelper.getTestObject("service/AppSiteDto3.json", AppSiteDto.class);
//        appSiteDto = appSiteService.createAppSite(appSiteDto);
//        TaskDto taskDto = operationService.deployAppSite(appSiteDto.getId());
//        //检查task
//        Assert.assertNotNull(taskDto);
//        Assert.assertEquals(OperationEnum.ONLINE_APPSITE, taskDto.getOpsType());
//        //检查状态
//        appSiteDto = appSiteService.getByAppId(1L, appSiteDto.getAppId()).orElse(null);
//        Assert.assertEquals(OnlineStatusEnum.ONLINE.getValue(), appSiteDto.getStatus().intValue());
//        //检查归档
//        Optional<AppSiteArchiveDto> archiveDto = appSiteArchiveService.getArchiveByVersion(appSiteDto.getId(), taskDto.getAppSiteVersion());
//        Assert.assertTrue(archiveDto.isPresent());
//    }
//
//    @Test
//    public void testUndeployAppSite() throws Exception {
//        AppSiteDto appSiteDto = TestHelper.getTestObject("service/AppSiteDto3.json", AppSiteDto.class);
//        appSiteDto = appSiteService.createAppSite(appSiteDto);
//        operationService.deployAppSite(appSiteDto.getId());
//        TaskDto taskDto = operationService.undeployAppSite(appSiteDto.getId());
//        //检查task
//        Assert.assertNotNull(taskDto);
//        Assert.assertEquals(OperationEnum.OFFLINE_APPSITE, taskDto.getOpsType());
//        //检查状态
//        appSiteDto = appSiteService.getByAppId(1L, appSiteDto.getAppId()).orElse(null);
//        Assert.assertEquals(OnlineStatusEnum.OFFLINE.getValue(), appSiteDto.getStatus().intValue());
//        //检查归档
//        Optional<AppSiteArchiveDto> archiveDto = appSiteArchiveService.getArchiveByVersion(appSiteDto.getId(), taskDto.getAppSiteVersion());
//        Assert.assertNotNull(archiveDto.isPresent());
//    }
//
//    @Test
//    public void testServerUp() throws Exception {
//        AppSiteDto appSiteDto = TestHelper.getTestObject("service/AppSiteDto3.json", AppSiteDto.class);
//        appSiteDto = appSiteService.createAppSite(appSiteDto);
//        operationService.deployAppSite(appSiteDto.getId());
//
//        List<Long> serverIds = appSiteDto.getAppSiteServers().stream()
//                .filter(appSiteServerDto -> appSiteServerDto.getStatus() == OnlineStatusEnum.OFFLINE.getValue())
//                .map(AppSiteServerDto::getId).collect(Collectors.toList());
//        TaskDto taskDto = operationService.operateServers(appSiteDto.getId(), serverIds, OperationEnum.PULL_IN);
//        //检查task
//        Assert.assertNotNull(taskDto);
//        Assert.assertEquals(OperationEnum.PULL_IN, taskDto.getOpsType());
//        //检查server状态
//        List<AppSiteServerDto> servers = appSiteService.getAppSiteServers(appSiteDto.getId());
//        servers.forEach(appSiteServerDto -> {
//            if (serverIds.contains(appSiteServerDto.getId())) {
//                Integer status = appSiteServerDto.getStatusDetail().get(OperationEnum.PULL_IN.getOperationOffset().getOperation());
//                Assert.assertEquals(OnlineStatusEnum.ONLINE.getValue(), status.intValue());
//            }
//        });
//        //检查归档
//        Optional<AppSiteArchiveDto> archiveDto = appSiteArchiveService.getArchiveByVersion(appSiteDto.getId(), taskDto.getAppSiteVersion());
//        Assert.assertNotNull(archiveDto.isPresent());
//
//    }
//
//    @Test
//    public void testServerDown() throws Exception {
//        AppSiteDto appSiteDto = TestHelper.getTestObject("service/AppSiteDto3.json", AppSiteDto.class);
//        appSiteDto = appSiteService.createAppSite(appSiteDto);
//        operationService.deployAppSite(appSiteDto.getId());
//        List<Long> serverIds = appSiteDto.getAppSiteServers().stream()
//                .filter(appSiteServerDto -> appSiteServerDto.getStatus() == OnlineStatusEnum.ONLINE.getValue())
//                .map(AppSiteServerDto::getId).collect(Collectors.toList());
//        TaskDto taskDto = operationService.operateServers(appSiteDto.getId(), serverIds, OperationEnum.PULL_OUT);
//        //检查task
//        Assert.assertNotNull(taskDto);
//        Assert.assertEquals(OperationEnum.PULL_OUT, taskDto.getOpsType());
//        //检查server状态
//        List<AppSiteServerDto> servers = appSiteService.getAppSiteServers(appSiteDto.getId());
//        servers.forEach(appSiteServerDto -> {
//            if (serverIds.contains(appSiteServerDto.getId())) {
//                Integer status = appSiteServerDto.getStatusDetail().get(OperationEnum.PULL_OUT.getOperationOffset().getOperation());
//                Assert.assertEquals(OnlineStatusEnum.OFFLINE.getValue(), status.intValue());
//                Assert.assertEquals(OnlineStatusEnum.OFFLINE.getValue(), appSiteServerDto.getStatus().intValue());
//            }
//        });
//        //检查归档
//        Optional<AppSiteArchiveDto> archiveDto = appSiteArchiveService.getArchiveByVersion(appSiteDto.getId(), taskDto.getAppSiteVersion());
//        Assert.assertNotNull(archiveDto.isPresent());
//    }
}
