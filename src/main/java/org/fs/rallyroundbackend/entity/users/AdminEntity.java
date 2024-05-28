package org.fs.rallyroundbackend.entity.users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "admins")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AdminEntity extends UserEntity {
    @ManyToOne
    @JoinColumn(name = "department_id")
    @Cascade({CascadeType.PERSIST, CascadeType.MERGE})
    private DepartmentEntity department;

    @OneToMany
    @JoinColumn(name = "admin_id")
    private List<AdminActivityLogEntity> activityLogs;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
}
