CREATE TABLE `hs_check_result` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `app_site_server_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'group_id',
  `check_url` varchar(60) DEFAULT NULL COMMENT '健康检查的url',
  `last_work_ip` varchar(200) NOT NULL DEFAULT '0' COMMENT '周期内执行过的ip',
  `fail_count` int(11) DEFAULT '0' COMMENT '周期内失败次数',
  `total_suc_count` int(11) DEFAULT '0' COMMENT '总的检查成功次数',
  `total_fail_count` int(11) DEFAULT '0' COMMENT '总的检查失败次数',
  `fail_msg` varchar(200) DEFAULT '0' COMMENT '失败的信息',
  `fail_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '失败的时间',
  `last_status` int(11) DEFAULT NULL COMMENT '记录最后一次的状态\n1 表示成功\n0 表示失败\n默认1',
  `invalid_count` int(11) DEFAULT '0' COMMENT '周期内无效的次数，如果周期内出现一次无效状态，那么周期内忽略此节点健康检查的状态',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '插入时间',
  `insert_by` varchar(45) DEFAULT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(45) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `check_url_UNIQUE` (`check_url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='relation table of group and group server ip';

CREATE TABLE `hs_check_sk_lock` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ip` varchar(45) DEFAULT NULL,
  `key` varchar(45) DEFAULT NULL COMMENT '对于任务分配器来说key为health_check_sc, 对于执行器来说key为health_check_wk+"ip"',
  `batch_num` bigint(20) DEFAULT NULL COMMENT '批次号',
  `status` int(11) DEFAULT NULL COMMENT '0,表示开始。\n10，表示分配\n。20，表示结果分析中。\n30，表示结果分析完成。',
  `heat_time` datetime DEFAULT NULL COMMENT '更新时间',
  `work_count` int(11) DEFAULT NULL COMMENT '周期内执行次数',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '任务分配时间',
  `insert_by` varchar(45) DEFAULT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(45) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_UNIQUE` (`key`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='任务分配器锁，任务分配器的key为 group_gs_check_master,任务执行器的key为group_gs_check_worker+"ip"地址';

CREATE TABLE `hs_check_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `hs_check_result_id` bigint(20) NOT NULL,
  `check_url` varchar(60) DEFAULT NULL COMMENT '健康检查的url',
  `work_ip` varchar(200) NOT NULL DEFAULT '0' COMMENT '执行器ip',
  `result` int(11) DEFAULT '0' COMMENT '1,   表示ok\n0，表示失败\n-1，表示刚分配',
  `fail_msg` varchar(2000) DEFAULT NULL,
  `check_time` datetime DEFAULT NULL,
  `last_status` int(11) DEFAULT '1' COMMENT '为了减少联合查询，记录此条信息的上次状态',
  `app_site_server_id` bigint(20) DEFAULT '0',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '任务分配时间',
  `insert_by` varchar(45) DEFAULT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(45) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='relation table of group and group server ip';

CREATE TABLE `hs_check_work_lock` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ip` varchar(45) DEFAULT NULL,
  `key` varchar(45) DEFAULT NULL COMMENT '对于任务分配器来说key为health_check_sc, 对于执行器来说key为health_check_wk+"ip"',
  `heat_time` datetime DEFAULT NULL COMMENT '更新时间',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '任务分配时间',
  `insert_by` varchar(45) DEFAULT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(45) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_UNIQUE` (`key`,`ip`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='任务分配器锁，任务分配器的key为 group_gs_check_master,任务执行器的key为group_gs_check_worker+"ip"地址';
