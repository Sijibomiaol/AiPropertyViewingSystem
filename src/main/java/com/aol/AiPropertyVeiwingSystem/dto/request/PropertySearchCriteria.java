package com.aol.AiPropertyVeiwingSystem.dto.request;

import com.aol.AiPropertyVeiwingSystem.enums.PropertyStatus;
import com.aol.AiPropertyVeiwingSystem.enums.PropertyType;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class PropertySearchCriteria {
    String searchTerm;
    PropertyStatus status;
    PropertyType type;
    BigDecimal minPrice;
    BigDecimal maxPrice;
    Integer minBedrooms;
    Integer maxBedrooms;
} 