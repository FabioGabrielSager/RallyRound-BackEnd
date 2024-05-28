package org.fs.rallyroundbackend.entity.users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Table(name = "privileges_categories")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class PrivilegeCategoryEntity {
    @Id
    private short id;

    @Column(nullable = false)
    private String name;

    @OneToMany
    @JoinColumn(name = "category_id")
    private List<PrivilegeEntity> privileges;
}
