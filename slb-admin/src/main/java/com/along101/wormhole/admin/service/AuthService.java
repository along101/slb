package com.along101.wormhole.admin.service;

import com.along.wormhole.admin.dao.UserRepository;
import com.along.wormhole.admin.entity.UserEntity;
import com.along101.wormhole.admin.dao.UserRepository;
import com.along101.wormhole.admin.entity.UserEntity;
import com.along101.wormhole.admin.dao.UserRepository;
import com.along101.wormhole.admin.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by huangyinhuang on 7/20/2017.
 * <p>
 * 权限服务
 * 用户、角色、权限的配置管理
 */
@Service
@Slf4j
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public Boolean hasUser(String userName) {
        return userRepository.countByName(userName) > 0;
    }

    public UserEntity addUser(String userName) {
        UserEntity user = null;

        if (!hasUser(userName)) {
            user = new UserEntity();
            user.setName(userName);
            user.setEmail(userName + "@along101corp.com");
            userRepository.save(user);
        }

        return user;
    }

    public void updateLastVisitTime(String userName) {
        UserEntity user = userRepository.findOneByName(userName);
        if (user != null) {
            // last visit time is updated automatically by database
            user.setLastVisitAt(new Date());
            userRepository.save(user);
        }
    }

}
