package com.along101.slbcoordinate.service;

import com.along101.slbcoordinate.common.Util;
import com.along101.slbcoordinate.dao.SlbLockRepository;
import com.along101.slbcoordinate.entity.SlbLockEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.Date;

@Slf4j
@Service
public class SlbLockService {

	@Autowired
	private Util util;
	@Autowired
	private SlbLockRepository slbLockRepository;
	private String ip;
	private String key = "slb-lock-key";	
	@PostConstruct
	private void initial() {
		ip = util.getIp();
		try {
			clearOld();
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			// 保证数据库中有一条记录
			insert();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	private void insert() {
		Date now=util.getDbNow();
		SlbLockEntity entity = new SlbLockEntity();
		entity.setIp(ip);
		entity.setUpdateTime(new Date(now.getSeconds() - 20));
		entity.setInsertTime(now);
		entity.setKey(key);		
		slbLockRepository.saveAndFlush(entity);
	}
	@Transactional
	public boolean isMaster() {
		SlbLockEntity entity = slbLockRepository.findByKey(key);
		if (entity.getUpdateTime().getTime()< util.getDbNow().getTime() - 10*1000) {
			// 根据受影响条数争夺分配锁
			Integer count = slbLockRepository.updateHeatTime(ip);
			return count > 0;
		} else {
			boolean flag = entity.getIp().equals(ip);
			if (flag) {
				updateHeatTime();
			}
			return flag;
		}
	}
	public SlbLockEntity getMaster() {
		return slbLockRepository.findByKey(key);
	}
	@Transactional
	private void clearOld() {
		slbLockRepository.deleteOld(key);
	}
	@Transactional
	public void updateHeatTime() {
		slbLockRepository.updateHeatTimeByIp(ip);		
	}
}
