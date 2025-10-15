package io.darpan.user_starter.service;

import io.darpan.user_starter.model.LoginRequest;
import io.darpan.user_starter.model.UserV2;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserV2Service {

    void createUser(LoginRequest user);

    Optional<UserV2> findByUsername(String username);

    UserDetails loadUserByUsername(String username);
}
