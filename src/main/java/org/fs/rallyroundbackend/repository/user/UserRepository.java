package org.fs.rallyroundbackend.repository.user;

import org.fs.rallyroundbackend.entity.users.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    @Query("SELECT u FROM UserEntity AS u WHERE u.email = :email AND u.enabled = true")
    Optional<UserEntity> findEnabledUserByEmail(String email);
    @Query("SELECT u FROM UserEntity AS u WHERE u.email = :email AND u.enabled = false")
    Optional<UserEntity> findDisabledUserByEmail(String email);
    boolean existsByEmailAndEnabled(String email, boolean enabled);
}
