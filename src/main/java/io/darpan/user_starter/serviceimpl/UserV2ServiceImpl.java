package io.darpan.user_starter.serviceimpl;

import io.darpan.user_starter.model.CustomUserDetails;
import io.darpan.user_starter.model.LoginRequest;
import io.darpan.user_starter.model.UserV2;
import io.darpan.user_starter.repository.UserV2Repository;
import io.darpan.user_starter.service.UserV2Service;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserV2ServiceImpl implements UserDetailsService, UserV2Service {

    private final UserV2Repository userV2Repository;
    private final PasswordEncoder passwordEncoder;

    public UserV2ServiceImpl(UserV2Repository userV2Repository, @Lazy PasswordEncoder passwordEncoder) {
        this.userV2Repository = userV2Repository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<UserV2> findByUsername(String username) {
        return userV2Repository.findByUsername(username);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserV2 user = userV2Repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return new CustomUserDetails(user);
    }

    public void createUser(LoginRequest user) {
        UserV2 userV2 = new UserV2();
        userV2.setUsername(user.username());
        userV2.setPassword(passwordEncoder.encode(user.password()));
        userV2Repository.save(userV2);
    }
}
