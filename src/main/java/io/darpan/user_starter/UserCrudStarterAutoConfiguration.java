package io.darpan.user_starter;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@AutoConfiguration
@ComponentScan(basePackages = {
        "io.darpan.user_starter.config",
        "io.darpan.user_starter.controller",
        "io.darpan.user_starter.service",
        "io.darpan.user_starter.helper",
        "io.darpan.user_starter.filter"
})
@EnableJpaRepositories(basePackages = "io.darpan.user_starter.repository")
@EntityScan(basePackages = "io.darpan.user_starter.model")
@EnableScheduling
public class UserCrudStarterAutoConfiguration {
}
