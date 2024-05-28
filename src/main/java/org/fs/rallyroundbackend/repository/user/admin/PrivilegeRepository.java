package org.fs.rallyroundbackend.repository.user.admin;

import org.fs.rallyroundbackend.entity.users.PrivilegeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrivilegeRepository extends JpaRepository<PrivilegeEntity, Short> {
    Optional<PrivilegeEntity> findByName(String privilegeName);
}
