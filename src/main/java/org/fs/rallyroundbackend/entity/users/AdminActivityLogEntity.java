package org.fs.rallyroundbackend.entity.users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "admins_activities_logs")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class AdminActivityLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "action_type", nullable = false)
    private String actionType;

    @Column(name = "action_description", nullable = false)
    private String actionDescription;

    @Column(name = "afected_resource_id", nullable = false)
    private String afectedResourceId;

    @Column(name = "afected_resource_id_type", nullable = false)
    private String afectedResourceIdType;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "action_count", nullable = false)
    private int actionCount;

    @ManyToOne
    private AdminEntity admin;
}
