package io.darpan.user_starter.service;

import io.darpan.user_starter.model.CustomUserDetails;
import io.darpan.user_starter.model.UserV2;
import io.darpan.user_starter.repository.UserV2Repository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserV2Service implements UserDetailsService {

    private final UserV2Repository userV2Repository;
    private final PasswordEncoder passwordEncoder;

    public UserV2Service(UserV2Repository userV2Repository, @Lazy PasswordEncoder passwordEncoder) {
        this.userV2Repository = userV2Repository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserV2 user = userV2Repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return new CustomUserDetails(user);
    }

    public UserV2 createUser(UserV2 user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userV2Repository.save(user);
    }
}
