package com.along101.wormhole.admin.entity;

import lombok.Data;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by yinzuolong on 2017/7/15.
 */
@Data
@Entity
@Table(name = "app_site", schema = "slb")
public class AppSiteEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "slb_id", nullable = true)
    private Long slbId;

    @Column(name = "app_id", nullable = false, length = 45)
    private String appId;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "note", nullable = true, length = 200)
    private String note;

    @Column(name = "status", updatable = false, nullable = true)
    private Integer status;

    @Column(name = "domain", nullable = false, length = 200)
    private String domain;

    @Column(name = "port", nullable = true)
    private Integer port;

    @Column(name = "path", nullable = false, length = 200)
    private String path;

    @Column(name = "`group`", nullable = true, length = 50)
    private String group;

    @Column(name = "loadbalance", nullable = true, length = 50)
    private String loadbalance;

    @Column(name = "health_uri", nullable = true, length = 200)
    private String healthUri;

    @Column(name = "server_directive", nullable = true, length = 2000)
    private String serverDirective;

    @Column(name = "upstream_directive", nullable = true, length = 2000)
    private String upstreamDirective;

    @Column(name = "online_version", updatable = false, nullable = true)
    private Long onlineVersion;

    @Column(name = "offline_version", updatable = false, nullable = true)
    private Long offlineVersion;

    @Column(name = "location_directive", nullable = true, length = 2000)
    private String locationDirective;
}
