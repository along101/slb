package com.along101.wormhole.admin.dao;

import com.along101.wormhole.admin.SlbAdminServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by yinzuolong on 2017/7/27.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SlbAdminServer.class)
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    public void name() throws Exception {
        taskRepository.findMaxTaskGroupByAppSite(1L, 1L);
    }
}
