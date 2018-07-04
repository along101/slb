package com.along101.wormhole.admin.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by yinzuolong on 2017/7/13.
 */
@Data
@Entity
@Table(name = "slb_server", schema = "slb")
public class SlbServerEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "slb_id", nullable = false)
    private Long slbId;
    @Column(name = "ip", nullable = false, length = 45)
    private String ip;
    @Column(name = "host_name", nullable = true, length = 45)
    private String hostName;
    @Column(name = "heart_time", nullable = true)
    private Date heartTime;
}
