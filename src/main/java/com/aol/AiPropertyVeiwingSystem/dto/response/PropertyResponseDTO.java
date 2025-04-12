package com.aol.AiPropertyVeiwingSystem.dto.response;

import com.aol.AiPropertyVeiwingSystem.enums.PropertyStatus;
import com.aol.AiPropertyVeiwingSystem.enums.PropertyType;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.math.BigDecimal;
import java.util.Set;

@Value
@Builder
@Data
public class PropertyResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String address;
    private BigDecimal price;
    private PropertyStatus status;
    private PropertyType type;
    private Set<String> amenities;
    private Integer bedrooms;
    private Integer bathrooms;
    private Double squareFootage;
    private UserSummaryDTO landlord;
    private UserSummaryDTO tenant;
} 