package org.fs.rallyroundbackend.entity.location;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.util.UUID;

@Entity
@Getter @Setter
@NoArgsConstructor
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
    private String postalCode;

    @ManyToOne
    @JoinColumn(name = "formatted_address_id")
    @Cascade({CascadeType.PERSIST, CascadeType.MERGE})
    private FormattedAddressEntity formattedAddress;

    @ManyToOne
    @JoinColumn(name = "address_line_id")
    @Cascade({CascadeType.PERSIST, CascadeType.MERGE})
    private AddressLineEntity addressLine;

    @Enumerated(EnumType.STRING)
    private EntityType entityType;

    private String name;
}
