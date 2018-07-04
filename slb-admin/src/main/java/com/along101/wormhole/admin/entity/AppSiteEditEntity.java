package com.along101.wormhole.admin.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by yinzuolong on 2017/9/6.
 */
@Data
@Entity
@Table(name = "app_site_edit", schema = "slb")
public class AppSiteEditEntity extends BaseEntity {

    @Id
    @Column(name = "app_site_id", nullable = false)
    private Long appSiteId;

    @Column(name = "slb_id", nullable = true)
    private Long slbId;

    @Column(name = "app_id", nullable = false, length = 45)
    private String appId;

    @Column(name = "domain", nullable = false, length = 100)
    private String domain;

    @Column(name = "port", nullable = true)
    private Integer port;

    @Column(name = "online_version", nullable = true)
    private Long onlineVersion;

    @Column(name = "offline_version", nullable = true)
    private Long offlineVersion;

    @Column(name = "appsite_content", nullable = true, length = -1)
    private String appSiteContent;

}
