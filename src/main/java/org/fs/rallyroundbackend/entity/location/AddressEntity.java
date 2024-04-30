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
@Table(name = "addresses")
@Getter
@Setter
public class AddressEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "street_id")
    private StreetEntity street;

    @Column(name = "house_number")
    private String houseNumber;

    @ManyToOne
    @JoinColumn(name = "address_line_id")
    private AddressLineEntity addressLine;

    @ManyToOne
    @JoinColumn(name = "formated_address_id")
    private FormattedAddressEntity formattedAddress;

    @Column(name = "postal_code")
    private String postalCode;
}
