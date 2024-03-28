package org.example.rallyroundbackend.repository.user;

import jakarta.validation.constraints.Email;
import org.example.rallyroundbackend.entity.users.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(@Email String email);
}
