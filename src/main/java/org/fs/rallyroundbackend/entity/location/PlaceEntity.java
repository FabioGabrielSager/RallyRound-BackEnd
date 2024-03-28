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

import java.util.UUID;

@Entity
@Getter @Setter
@Table(name="places")
public class PlaceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(columnDefinition = "NUMERIC(10, 8)", nullable = false)
    private double latitude;
    @Column(columnDefinition = "NUMERIC(10, 8)", nullable = false)
    private double longitude;

    @ManyToOne
    @JoinColumn(name = "neighborhood_id")
    private NeighborhoodEntity neighborhood;

    @Column(name = "postal_code")
    private int postalCode;

    @ManyToOne
    @JoinColumn(name = "formatted_address_id")
    private FormattedAddressEntity formattedAddress;

    @ManyToOne
    @JoinColumn(name = "address_line_id")
    private AddressLineEntity addressLine;

    @ManyToOne
    @JoinColumn(name = "entity_type_id")
    private EntityTypeEntity entityType;

}
