package org.example.rallyroundbackend.entity.users;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.rallyroundbackend.entity.location.PlaceEntity;


import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter @Setter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    protected String name;
    protected String lastName;
    protected String email;
    protected LocalDate birthdate;
    protected String password;
    protected boolean accountNonExpired;
    protected boolean credentialsNonExpired;
    protected boolean accountNonLocked;
    protected boolean enabled;

    @ManyToMany
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"
            )
    )
    protected Set<RoleEntity> roles;
}
