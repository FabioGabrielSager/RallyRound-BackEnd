package org.fs.rallyroundbackend.repository.user.admin;

import org.fs.rallyroundbackend.entity.users.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminRepository extends JpaRepository<AdminEntity, UUID> {
    @Query("SELECT a FROM AdminEntity AS a WHERE a.email = :email AND a.enabled = true")
    Optional<AdminEntity> findEnabledUserByEmail(String email);

    @Query("SELECT a FROM AdminEntity AS a " +
            "JOIN DepartmentEntity AS dep ON dep=a.department " +
            "WHERE " +
            "(:name IS NULL OR :name=a.name) " +
            "AND (:lastName IS NULL OR :lastName=a.lastName) " +
            "AND (:department IS NULL OR :department=dep.name) " +
            "AND (:enabled IS NULL OR :enabled=a.enabled) " +
            "AND (:registeredDateFrom IS NULL OR :registeredDateTo IS NULL " +
            "OR CAST(a.registrationDate AS DATE) BETWEEN :registeredDateFrom AND :registeredDateTo)")
    List<AdminEntity> findAll(
            String name,
            String lastName,
            String department,
            LocalDate registeredDateFrom,
            LocalDate registeredDateTo,
            Boolean enabled
    );
}
