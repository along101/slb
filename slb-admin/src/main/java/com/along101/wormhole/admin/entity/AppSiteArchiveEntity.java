package com.along101.wormhole.admin.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by yinzuolong on 2017/7/15.
 */
@Data
@Entity
@Table(name = "app_site_archive", schema = "slb")
public class AppSiteArchiveEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "app_site_id", nullable = true)
    private Long appSiteId;

    @Column(name = "app_id", nullable = false, length = 45)
    private String appId;

    @Column(name = "version", nullable = true)
    private Long version;

    @Column(name = "appsite_content", nullable = true, length = -1)
    private String appSiteContent;

    @Column(name = "domain_content", nullable = true, length = -1)
    private String domainContent;

    @Column(name = "flag", nullable = true)
    private Integer flag;

    @Column(name = "slb_id", nullable = true)
    private Long slbId;

    @Column(name = "domain", nullable = false, length = 100)
    private String domain;

    @Column(name = "port", nullable = true)
    private Integer port;

    @Column(name = "domain_port", nullable = false, length = 200)
    private String domainPort;
}
