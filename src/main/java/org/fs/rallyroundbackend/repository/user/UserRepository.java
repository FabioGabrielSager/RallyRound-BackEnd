package org.fs.rallyroundbackend.repository.user;

import org.fs.rallyroundbackend.entity.users.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmailAndEnabled(String email, boolean enabled);
}
