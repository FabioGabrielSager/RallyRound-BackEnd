package org.fs.rallyroundbackend.repository.user.admin;

import org.fs.rallyroundbackend.entity.users.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DepartmentRepository extends JpaRepository<DepartmentEntity, UUID> {
    Optional<DepartmentEntity> findByName(String name);
}
