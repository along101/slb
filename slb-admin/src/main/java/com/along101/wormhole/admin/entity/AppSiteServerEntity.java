package com.along101.wormhole.admin.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by yinzuolong on 2017/7/15.
 */
@Data
@Entity
@Table(name = "app_site_server", schema = "slb")
public class AppSiteServerEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "app_site_id", nullable = false)
    private Long appSiteId;

    @Column(name = "host_name", nullable = true, length = 200)
    private String hostName;

    @Column(name = "ip", nullable = false, length = 200)
    private String ip;

    @Column(name = "port", nullable = false)
    private Integer port;

    @Column(name = "weight", nullable = true)
    private Integer weight;

    @Column(name = "max_fails", nullable = true)
    private Integer maxFails;

    @Column(name = "fail_timeout", nullable = true)
    private Integer failTimeout;

    @Column(name = "tag", nullable = true, length = 200)
    private String tag;

    @Column(name = "status", nullable = true, length = 45)
    private String status;

}
