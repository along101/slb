package com.along101.wormhole.admin.dao;

import com.along101.wormhole.admin.entity.AuditLogEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by yinzuolong on 2017/7/11.
 */
public interface AuditLogRepository extends CrudRepository<AuditLogEntity, Long> {
}
