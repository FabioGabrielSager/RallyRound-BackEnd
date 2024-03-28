package org.example.rallyroundbackend.entity.location;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "admins_subdistricts")
@Getter @Setter
public class AdminSubdistrictEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @OneToMany(mappedBy = "adminSubdistrict")
    private List<LocalityEntity> localities;

    @ManyToOne
    @JoinColumn(name = "admin_distric_id")
    private AdminDistrictEntity adminDistrict;
}
