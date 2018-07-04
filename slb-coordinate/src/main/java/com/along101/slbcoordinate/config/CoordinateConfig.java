package com.along101.slbcoordinate.config;

import com.along101.slbcoordinate.SlbCoordinateApplication;
import com.along101.slbcoordinate.dao.SlbLockRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created by qiankai02 on 2017/8/29.
 */
@Slf4j
@Configuration
@ComponentScan(basePackageClasses = {SlbCoordinateApplication.class})
@EnableJpaRepositories(basePackageClasses = {SlbLockRepository.class})
public class CoordinateConfig {

}
