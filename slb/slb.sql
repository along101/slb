CREATE TABLE `agent_app_site_status` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `slb_id` bigint(20) NOT NULL,
  `agent_ip` varchar(45) NOT NULL,
  `app_site_id` bigint(20) NOT NULL,
  `domain` varchar(100) NOT NULL,
  `port` bigint(20) DEFAULT NULL,
  `domain_port` varchar(100) DEFAULT NULL,
  `agent_app_site_version` bigint(20) DEFAULT NULL,
  `agent_task_id` bigint(20) DEFAULT NULL,
  `msg` varchar(2000) DEFAULT NULL,
  `status` int(11) DEFAULT NULL COMMENT '1，表示成功\n0，表示失败',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '插入时间',
  `insert_by` varchar(45) DEFAULT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(45) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COMMENT='agent_app_site_status是当前状态表';

CREATE TABLE `agent_task_result` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `slb_id` bigint(20) NOT NULL,
  `agent_ip` varchar(45) NOT NULL,
  `app_site_id` bigint(20) NOT NULL,
  `app_site_version` bigint(20) DEFAULT NULL,
  `task_id` bigint(20) DEFAULT NULL,
  `msg` varchar(2000) DEFAULT NULL,
  `status` int(11) DEFAULT NULL COMMENT '1，表示成功\n0，表示失败',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '插入时间',
  `insert_by` varchar(45) DEFAULT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(45) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=270 DEFAULT CHARSET=utf8 COMMENT='agent task 历史表，每次任务返回都会往这张表里面插入一条记录，判断一个task是否执行成功的标志是某个appsite下所有的taskid大于等于当前的taskid 同时不能有失败才算成功否则是失败';

CREATE TABLE `app_site` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `slb_id` bigint(20) DEFAULT NULL,
  `app_id` varchar(45) NOT NULL,
  `name` varchar(200) NOT NULL,
  `note` varchar(200) DEFAULT NULL,
  `status` int(11) DEFAULT NULL COMMENT '1，上线\n0，下线',
  `online_version` bigint(20) DEFAULT NULL,
  `offline_version` bigint(20) DEFAULT NULL,
  `domain` varchar(200) NOT NULL,
  `port` int(11) DEFAULT NULL,
  `path` varchar(200) NOT NULL,
  `group` varchar(50) DEFAULT NULL,
  `loadbalance` varchar(50) DEFAULT NULL,
  `health_uri` varchar(200) DEFAULT NULL,
  `server_directive` varchar(2000) DEFAULT NULL,
  `location_directive` varchar(2000) DEFAULT NULL,
  `upstream_directive` varchar(2000) DEFAULT NULL,
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '插入时间',
  `insert_by` varchar(45) DEFAULT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(45) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=219 DEFAULT CHARSET=utf8 COMMENT='站点表---类似于归档表，只是归档表保存的是历史的json版本，但是这个表区别在于它是表示当前线上的真实记录，而app_site_archive 是归档的历史记录，但是记录的是引起nginx配置改变的时候才会插入一条记录，否则不插入记录，app_site_edit表示的是当前线上的编辑版本。';

CREATE TABLE `app_site_archive` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `task_id` bigint(20) DEFAULT NULL,
  `slb_id` bigint(20) DEFAULT NULL,
  `app_site_id` bigint(20) DEFAULT NULL,
  `app_id` varchar(45) NOT NULL,
  `domain` varchar(100) NOT NULL,
  `port` int(11) DEFAULT NULL,
  `domain_port` varchar(200) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `appsite_content` text,
  `domain_content` text,
  `flag` int(11) DEFAULT NULL COMMENT '1 表示线上，0表示线下',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '插入时间',
  `insert_by` varchar(45) DEFAULT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(45) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=384 DEFAULT CHARSET=utf8 COMMENT='站点存档';

CREATE TABLE `app_site_edit` (
  `app_site_id` bigint(20) NOT NULL,
  `slb_id` bigint(20) DEFAULT NULL,
  `app_id` varchar(45) NOT NULL,
  `domain` varchar(100) NOT NULL,
  `port` int(11) DEFAULT NULL,
  `online_version` bigint(20) DEFAULT NULL,
  `offline_version` bigint(20) DEFAULT NULL,
  `appsite_content` text,
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '插入时间',
  `insert_by` varchar(45) DEFAULT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(45) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '逻辑删除',
  PRIMARY KEY (`app_site_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='站点存档';

CREATE TABLE `app_site_server` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `app_site_id` bigint(20) NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `host_name` varchar(200) DEFAULT NULL,
  `ip` varchar(200) DEFAULT NULL,
  `port` int(11) DEFAULT NULL,
  `weight` int(11) DEFAULT NULL,
  `max_fails` int(11) DEFAULT NULL,
  `fail_timeout` int(11) DEFAULT NULL,
  `tag` varchar(200) DEFAULT NULL,
  `status` varchar(45) NOT NULL DEFAULT '1111111111',
  `online_app_site_version` bigint(20) DEFAULT NULL,
  `offline_app_site_version` bigint(20) DEFAULT NULL,
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '插入时间',
  `insert_by` varchar(45) DEFAULT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(45) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=5823 DEFAULT CHARSET=utf8 COMMENT='站点机器表';

CREATE TABLE `audit_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(32) DEFAULT NULL COMMENT '发送请求者',
  `client_ip` varchar(32) DEFAULT NULL,
  `http_method` varchar(64) DEFAULT NULL COMMENT '请求方法：GET/POST/PUT/DELETE',
  `http_uri` varchar(256) DEFAULT NULL COMMENT '请求URI',
  `class_method` varchar(128) DEFAULT NULL COMMENT '调用方法',
  `class_method_args` varchar(1024) DEFAULT NULL COMMENT '调用方法参数',
  `class_method_return` varchar(1024) DEFAULT NULL COMMENT '调用方法返回值',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '插入时间',
  `insert_by` varchar(45) DEFAULT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(45) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5789 DEFAULT CHARSET=utf8 COMMENT='操作日志表';

CREATE TABLE `operation_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` varchar(20) DEFAULT NULL COMMENT '字符串',
  `target_id` bigint(20) DEFAULT NULL,
  `operation` varchar(45) DEFAULT NULL,
  `data` varchar(2000) DEFAULT NULL,
  `user_name` varchar(45) DEFAULT NULL,
  `client_ip` varchar(45) DEFAULT NULL,
  `success` int(11) DEFAULT NULL,
  `error_msg` varchar(2000) DEFAULT NULL,
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '插入时间',
  `insert_by` varchar(45) DEFAULT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(45) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='操作日志表';

CREATE TABLE `slb` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `nginx_conf` varchar(200) DEFAULT NULL,
  `nginx_sbin` varchar(200) DEFAULT NULL,
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '插入时间',
  `insert_by` varchar(200) DEFAULT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(200) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8 COMMENT='slb集群表';

CREATE TABLE `slb_server` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `slb_id` bigint(20) NOT NULL,
  `ip` varchar(45) NOT NULL,
  `host_name` varchar(45) DEFAULT NULL,
  `heart_time` datetime DEFAULT NULL,
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '插入时间',
  `insert_by` varchar(45) DEFAULT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(45) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8 COMMENT='slb机器表';

CREATE TABLE `task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `slb_id` bigint(20) DEFAULT NULL,
  `app_site_id` bigint(20) DEFAULT NULL,
  `app_site_version` bigint(20) DEFAULT NULL,
  `app_id` varchar(45) DEFAULT NULL,
  `ops_type` varchar(45) DEFAULT NULL COMMENT '1，slb修改，2，app_site_id修改，3，app_site_id状态修改',
  `domain_port` varchar(100) DEFAULT NULL,
  `status` int(11) DEFAULT NULL COMMENT '获取task的状态 -1 表示不存在，1，表示完成，2，表示执行中 ,3失败, 4超时 ',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '插入时间',
  `insert_by` varchar(45) DEFAULT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(45) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=380 DEFAULT CHARSET=utf8 COMMENT='消息任务表';

CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `name` varchar(45) NOT NULL COMMENT '用户名',
  `email` varchar(45) DEFAULT NULL COMMENT '用户邮箱地址',
  `last_visit_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '上一次访问时间',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `insert_by` varchar(45) DEFAULT NULL COMMENT '创建者',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(45) DEFAULT NULL COMMENT '最近修改者',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_inserttime` (`insert_time`),
  KEY `idx_updatetime` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';
