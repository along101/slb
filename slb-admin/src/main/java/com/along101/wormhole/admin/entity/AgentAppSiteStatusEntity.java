package com.along101.wormhole.admin.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by yinzuolong on 2017/7/15.
 */
@Data
@Entity
@Table(name = "agent_app_site_status", schema = "slb")
public class AgentAppSiteStatusEntity extends BaseEntity{
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
    @Column(name = "domain", nullable = false, length = 100)
    private String domain;
    @Column(name = "port", nullable = true)
    private Integer port;
    @Column(name = "domain_port", nullable = false, length = 200)
    private String domainPort;
    @Column(name = "agent_app_site_version", nullable = true)
    private Long agentAppSiteVersion;
    @Column(name = "agent_task_id", nullable = true)
    private Long agentTaskId;
    @Column(name = "msg", nullable = true, length = 2000)
    private String msg;
    @Column(name = "status", nullable = true)
    private Integer status;

}
