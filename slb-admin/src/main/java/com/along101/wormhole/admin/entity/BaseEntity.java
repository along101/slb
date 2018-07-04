package com.along101.wormhole.admin.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;


@Data
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class BaseEntity {

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "insert_time",insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    protected Date insertTime;

    @CreatedBy
    @Column(name = "insert_by", nullable = true, length = 45)
    protected String insertBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    protected Date updateTime;

    @LastModifiedBy
    @Column(name = "update_by", nullable = true, length = 45)
    protected String updateBy;

    @Column(name = "is_active", nullable = false, columnDefinition = "TINYINT(1)")
    protected Boolean isActive = true;

}
