package com.along101.wormhole.admin.entity;

import com.along.wormhole.admin.common.constant.OperationEnum;
import com.along101.wormhole.admin.common.constant.OperationEnum;
import com.along101.wormhole.admin.common.constant.OperationEnum;
import lombok.Data;

import javax.persistence.*;

/**
 * Created by yinzuolong on 2017/7/15.
 */
@Data
@Entity
@Table(name = "task", schema = "slb")
public class TaskEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "slb_id", nullable = true)
    private Long slbId;

    @Column(name = "app_site_id", nullable = true)
    private Long appSiteId;

    @Column(name = "app_site_version", nullable = true)
    private Long appSiteVersion;

    @Column(name = "app_id", nullable = false, length = 45)
    private String appId;

    @Column(name = "domain_port", nullable = false, length = 200)
    private String domainPort;

    @Column(name = "ops_type", nullable = true, length = 45)
    @Enumerated(EnumType.STRING)
    private OperationEnum opsType;

    @Column(name = "status", nullable = true)
    private Integer status;
}
