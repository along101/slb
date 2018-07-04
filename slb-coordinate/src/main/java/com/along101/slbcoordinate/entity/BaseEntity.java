package com.along101.slbcoordinate.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
@MappedSuperclass
public class BaseEntity {
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "insert_time", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
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

	@Column(name = "is_active")
	private int isActive;
}
