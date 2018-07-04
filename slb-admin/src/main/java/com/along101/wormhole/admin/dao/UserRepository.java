package com.along101.wormhole.admin.dao;

import com.along101.wormhole.admin.entity.UserEntity;

/**
 * Created by huangyinhuang on 7/20/2017.
 */
public interface UserRepository extends BaseJpaRepository<UserEntity, Long> {

    Long countByName(String name);

    UserEntity findOneByName(String name);

}
