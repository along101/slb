package com.along101.wormhole.admin.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by yinzuolong on 2017/7/13.
 */
@Data
@Entity
@Table(name = "slb", schema = "slb")
public class SlbEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "name", nullable = false, length = 45)
    private String name;
    @Column(name = "nginx_conf", nullable = true, length = 200)
    private String nginxConf;
    @Column(name = "nginx_sbin", nullable = true, length = 200)
    private String nginxSbin;
}
