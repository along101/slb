package com.along101.wormhole.admin.config;


import com.along.wormhole.admin.dao.AuditLogRepository;
import com.along101.wormhole.admin.dao.AuditLogRepository;
import com.along101.wormhole.admin.dao.AuditLogRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created by huangyinhuang on 1/20/2017.
 */
@Configuration
@EnableJpaRepositories(basePackageClasses = {AuditLogRepository.class})
@EnableJpaAuditing
public class JpaConfiguration {

    @Bean
    AuditorAware<String> auditorProvider() {
        return new UserAuditorAware();
    }

}
