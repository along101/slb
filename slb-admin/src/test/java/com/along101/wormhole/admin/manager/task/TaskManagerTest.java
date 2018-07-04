package com.along101.wormhole.admin.manager.task;

import com.alibaba.fastjson.JSON;
import com.along101.wormhole.admin.SlbAdminServer;
import com.along101.wormhole.admin.TestHelper;
import com.along101.wormhole.admin.common.constant.OperationEnum;
import com.along101.wormhole.admin.common.constant.TaskStatusEnum;
import com.along101.wormhole.admin.dto.appsite.AppSiteDto;
import com.along101.wormhole.admin.dto.task.TaskDto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by yinzuolong on 2017/7/28.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SlbAdminServer.class)
@Transactional
@Rollback
public class TaskManagerTest {
    @Autowired
    private TaskManager taskManager;

    @Test
    public void testAddTask() throws Exception {
        AppSiteDto appSiteDto = TestHelper.getTestObject("service/AppSiteDto3.json", AppSiteDto.class);
        appSiteDto.setId(100L);
        TaskDto taskDto = taskManager.addTask(appSiteDto, OperationEnum.DEPLOY_APPSITE);
        Assert.assertEquals(TaskStatusEnum.RUNNING.getCode(), taskDto.getStatus().intValue());
        TaskDto findTaskDto = taskManager.getTask(taskDto.getId());
        Assert.assertNotNull(findTaskDto);
        System.out.println(JSON.toJSONString(findTaskDto));
    }


    //TODO 准备数据
    private TaskDto prepareData() {
        //准备slb数据

        //准备appSite数据

        //准备Task，准备多条

        return null;
    }

    //TODO 所有agent执行成功
    @Test
    public void testGetAndProcessStatus1() throws Exception {
        //准备agent执行结果数据

    }

    //TODO 有agent执行失败
    @Test
    public void testGetAndProcessStatus2() throws Exception {
        //准备agent执行结果数据

    }

    //TODO 有agent没有返回，且超时
    @Test
    public void testGetAndProcessStatus3() throws Exception {
        //准备agent执行结果数据

    }

    //TODO 有agent没有返回，没有超时
    @Test
    public void testGetAndProcessStatus4() throws Exception {
        //准备agent执行结果数据

    }
}
