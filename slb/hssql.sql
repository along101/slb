CREATE TABLE `hs_check_result` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `app_site_server_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'group_id',
  `check_url` varchar(60) DEFAULT NULL COMMENT '��������url',
  `last_work_ip` varchar(200) NOT NULL DEFAULT '0' COMMENT '������ִ�й���ip',
  `fail_count` int(11) DEFAULT '0' COMMENT '������ʧ�ܴ���',
  `total_suc_count` int(11) DEFAULT '0' COMMENT '�ܵļ��ɹ�����',
  `total_fail_count` int(11) DEFAULT '0' COMMENT '�ܵļ��ʧ�ܴ���',
  `fail_msg` varchar(200) DEFAULT '0' COMMENT 'ʧ�ܵ���Ϣ',
  `fail_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'ʧ�ܵ�ʱ��',
  `last_status` int(11) DEFAULT NULL COMMENT '��¼���һ�ε�״̬\n1 ��ʾ�ɹ�\n0 ��ʾʧ��\nĬ��1',
  `invalid_count` int(11) DEFAULT '0' COMMENT '��������Ч�Ĵ�������������ڳ���һ����Ч״̬����ô�����ں��Դ˽ڵ㽡������״̬',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '����ʱ��',
  `insert_by` varchar(45) DEFAULT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '����ʱ��',
  `update_by` varchar(45) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '�߼�ɾ��',
  PRIMARY KEY (`id`),
  UNIQUE KEY `check_url_UNIQUE` (`check_url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='relation table of group and group server ip';

CREATE TABLE `hs_check_sk_lock` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ip` varchar(45) DEFAULT NULL,
  `key` varchar(45) DEFAULT NULL COMMENT '���������������˵keyΪhealth_check_sc, ����ִ������˵keyΪhealth_check_wk+"ip"',
  `batch_num` bigint(20) DEFAULT NULL COMMENT '���κ�',
  `status` int(11) DEFAULT NULL COMMENT '0,��ʾ��ʼ��\n10����ʾ����\n��20����ʾ��������С�\n30����ʾ���������ɡ�',
  `heat_time` datetime DEFAULT NULL COMMENT '����ʱ��',
  `work_count` int(11) DEFAULT NULL COMMENT '������ִ�д���',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '�������ʱ��',
  `insert_by` varchar(45) DEFAULT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '����ʱ��',
  `update_by` varchar(45) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '�߼�ɾ��',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_UNIQUE` (`key`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='������������������������keyΪ group_gs_check_master,����ִ������keyΪgroup_gs_check_worker+"ip"��ַ';

CREATE TABLE `hs_check_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `hs_check_result_id` bigint(20) NOT NULL,
  `check_url` varchar(60) DEFAULT NULL COMMENT '��������url',
  `work_ip` varchar(200) NOT NULL DEFAULT '0' COMMENT 'ִ����ip',
  `result` int(11) DEFAULT '0' COMMENT '1,   ��ʾok\n0����ʾʧ��\n-1����ʾ�շ���',
  `fail_msg` varchar(2000) DEFAULT NULL,
  `check_time` datetime DEFAULT NULL,
  `last_status` int(11) DEFAULT '1' COMMENT 'Ϊ�˼������ϲ�ѯ����¼������Ϣ���ϴ�״̬',
  `app_site_server_id` bigint(20) DEFAULT '0',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '�������ʱ��',
  `insert_by` varchar(45) DEFAULT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '����ʱ��',
  `update_by` varchar(45) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '�߼�ɾ��',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='relation table of group and group server ip';

CREATE TABLE `hs_check_work_lock` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ip` varchar(45) DEFAULT NULL,
  `key` varchar(45) DEFAULT NULL COMMENT '���������������˵keyΪhealth_check_sc, ����ִ������˵keyΪhealth_check_wk+"ip"',
  `heat_time` datetime DEFAULT NULL COMMENT '����ʱ��',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '�������ʱ��',
  `insert_by` varchar(45) DEFAULT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '����ʱ��',
  `update_by` varchar(45) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '�߼�ɾ��',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_UNIQUE` (`key`,`ip`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='������������������������keyΪ group_gs_check_master,����ִ������keyΪgroup_gs_check_worker+"ip"��ַ';
