package com.along101.wormhole.admin.dao;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Created by huangyinhuang on 7/17/2017.
 */
@NoRepositoryBean
public interface BaseJpaRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    Iterable<T> findByIsActiveIsTrue();

    default Iterable<T> getAll() {
        return findByIsActiveIsTrue();
    }

}