package org.fs.rallyroundbackend.repository.user.admin;

import org.fs.rallyroundbackend.entity.users.PrivilegeCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PrivilegeCategoryRepository extends JpaRepository<PrivilegeCategoryEntity, UUID> {
}
