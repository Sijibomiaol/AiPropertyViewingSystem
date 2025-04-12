package com.aol.AiPropertyVeiwingSystem.dto.request;

import com.aol.AiPropertyVeiwingSystem.dto.response.UserSummaryDTO;
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
public class PropertyRequestDTO {
    String title;
    String description;
    String address;
    BigDecimal price;
    PropertyStatus status;
    PropertyType type;
    Set<String> amenities;
    Integer bedrooms;
    Integer bathrooms;
    Double squareFootage;
    UserSummaryDTO landlord;
    UserSummaryDTO tenant;
} 