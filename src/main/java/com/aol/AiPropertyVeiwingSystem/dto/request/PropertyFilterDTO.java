package com.aol.AiPropertyVeiwingSystem.dto.request;

import com.aol.AiPropertyVeiwingSystem.enums.PropertyStatus;
import com.aol.AiPropertyVeiwingSystem.enums.PropertyType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyFilterDTO {
    private PropertyStatus status;
    private PropertyType type;
    private Long landlordId;
    private Long tenantId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer minBedrooms;
    private Integer maxBedrooms;
    private Integer minBathrooms;
    private Integer maxBathrooms;
    private Double minSquareFootage;
    private Double maxSquareFootage;
    private String searchTerm;
} 