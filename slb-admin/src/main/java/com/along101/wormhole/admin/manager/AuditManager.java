package com.along101.wormhole.admin.manager;

import com.along.wormhole.admin.dao.AuditLogRepository;
import com.along.wormhole.admin.entity.AuditLogEntity;
import com.along101.wormhole.admin.dao.AuditLogRepository;
import com.along101.wormhole.admin.entity.AuditLogEntity;
import com.google.common.base.Preconditions;
import com.along101.wormhole.admin.dao.AuditLogRepository;
import com.along101.wormhole.admin.entity.AuditLogEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by huangyinhuang on 7/18/2017.
 *
 * 审计服务
 * 记录各种操作日志，用于后续审计
 *
 */
@Service
@Slf4j
public class AuditManager {

    @Autowired
    AuditLogRepository auditLogRepo;

    @Transactional
    public void recordOperation(AuditLogEntity actionItem){
        Preconditions.checkNotNull(actionItem, "action can not be null");
        auditLogRepo.save(actionItem);
    }

}
