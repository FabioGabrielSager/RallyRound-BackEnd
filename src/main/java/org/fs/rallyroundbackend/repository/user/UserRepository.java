package org.fs.rallyroundbackend.repository.user;

import jakarta.validation.constraints.Email;
import org.fs.rallyroundbackend.entity.users.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(@Email String email);
    boolean existsByEmailAndEnabled(@Email String email, boolean enabled);
}
