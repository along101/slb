package com.along101.slbcoordinate.entity;

import lombok.Data;

import javax.persistence.*;

@Cacheable(false)
@Entity
@Table(name = "slb_lock")
@Data
public class SlbLockEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "ip")
	private String ip;

	@Column(name = "`key`")
	private String key;
}
