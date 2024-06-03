package org.fs.rallyroundbackend.entity.users;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    protected String name;
    protected String lastName;
    protected String email;
    protected LocalDate birthdate;
    protected String password;
    protected boolean accountNonExpired = true;
    protected boolean credentialsNonExpired = true;
    protected boolean accountNonLocked = true;
    protected boolean enabled = true;
    @Column(name = "registration_date")
    private LocalDateTime registrationDate = LocalDateTime.now();
    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;

    @ManyToMany(fetch = FetchType.EAGER)
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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_privileges",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "privilege_id", referencedColumnName = "id")
    )
    private List<PrivilegeEntity> privileges;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return getGrantedAuthorities(roles);
    }

    private List<GrantedAuthority> getGrantedAuthorities(Collection<RoleEntity> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        for(RoleEntity role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }

        if(privileges != null) {
            for(PrivilegeEntity privilege : privileges) {
                authorities.add(new SimpleGrantedAuthority(privilege.getName()));
            }
        }

        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
