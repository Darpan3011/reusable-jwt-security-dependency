package io.darpan.user_starter.repository;

import io.darpan.user_starter.model.UserV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserV2Repository extends JpaRepository<UserV2, Long> {
    Optional<UserV2> findByUsername(String username);
}
