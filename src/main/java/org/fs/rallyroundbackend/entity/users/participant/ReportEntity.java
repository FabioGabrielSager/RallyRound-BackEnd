package org.fs.rallyroundbackend.entity.users.participant;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fs.rallyroundbackend.entity.users.UserEntity;

import java.util.UUID;

@Entity
@Table(name = "reports")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String description;

    @Enumerated(EnumType.STRING)
    private ReportMotive motive;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;
}
