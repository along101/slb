package com.along101.wormhole.admin.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by yinzuolong on 2017/7/15.
 */
@Data
@Entity
@Table(name = "agent_task_result", schema = "slb")
public class AgentTaskResultEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "slb_id", nullable = false)
    private Long slbId;
    @Column(name = "agent_ip", nullable = false, length = 45)
    private String agentIp;
    @Column(name = "app_site_id", nullable = false)
    private Long appSiteId;
    @Column(name = "app_site_version", nullable = true)
    private Long appSiteVersion;
    @Column(name = "task_id", nullable = true)
    private Long taskId;
    @Column(name = "msg", nullable = true, length = 2000)
    private String msg;
    @Column(name = "status", nullable = true)
    private Integer status;
}
