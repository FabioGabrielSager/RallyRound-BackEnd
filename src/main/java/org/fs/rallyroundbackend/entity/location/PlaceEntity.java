package org.fs.rallyroundbackend.entity.location;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.util.UUID;

@Entity
@Getter @Setter
@Table(name="places")
public class PlaceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "neighborhood_id")
    @Cascade({CascadeType.PERSIST, CascadeType.MERGE})
    private NeighborhoodEntity neighborhood;

    @Column(name = "postal_code")
    private int postalCode;

    @ManyToOne
    @JoinColumn(name = "formatted_address_id")
    @Cascade({CascadeType.PERSIST, CascadeType.MERGE})
    private FormattedAddressEntity formattedAddress;

    @ManyToOne
    @JoinColumn(name = "address_line_id")
    @Cascade({CascadeType.PERSIST, CascadeType.MERGE})
    private AddressLineEntity addressLine;

    @ManyToOne
    @JoinColumn(name = "entity_type_id")
    @Cascade({CascadeType.PERSIST, CascadeType.MERGE})
    private EntityTypeEntity entityType;

    private String name;
}
