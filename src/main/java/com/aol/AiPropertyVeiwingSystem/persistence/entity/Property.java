package com.aol.AiPropertyVeiwingSystem.persistence.entity;

import com.aol.AiPropertyVeiwingSystem.enums.PropertyStatus;
import com.aol.AiPropertyVeiwingSystem.enums.PropertyType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "properties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Property extends BaseEntity {
    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landlord_id", nullable = false)
    private User landlord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private User tenant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyType type;

    @ElementCollection
    @CollectionTable(name = "property_amenities")
    @Column(name = "amenity")
    private Set<String> amenities = new HashSet<>();

    @Column(nullable = false)
    private Integer bedrooms;

    @Column(nullable = false)
    private Integer bathrooms;

    private Double squareFootage;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Appointment> appointments = new HashSet<>();

    public void setLandlord(User landlord) {
        this.landlord = landlord;
        if (landlord != null) {
            landlord.getOwnedProperties().add(this);
        }
    }

    public void setTenant(User tenant) {
        this.tenant = tenant;
        if (tenant != null) {
            tenant.getRentedProperties().add(this);
        }
    }
} 