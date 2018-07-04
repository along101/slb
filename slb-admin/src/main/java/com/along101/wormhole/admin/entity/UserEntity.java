package com.along101.wormhole.admin.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by huangyinhuang on 7/20/2017.
 */
@Data
@Entity
@Table(name = "user", schema = "slb")
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = true)
    private String name;

    @Column(name = "email", nullable = true)
    private String email;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_visit_at", nullable = false, length = 45)
    private Date lastVisitAt;

}
