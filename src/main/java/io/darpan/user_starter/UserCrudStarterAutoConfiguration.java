package io.darpan.user_starter;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@AutoConfiguration
@ComponentScan(basePackages = "io.darpan.user_starter")
@EnableJpaRepositories(basePackages = "io.darpan.user_starter.repository")
@EntityScan(basePackages = "io.darpan.user_starter.model")
public class UserCrudStarterAutoConfiguration {
}
