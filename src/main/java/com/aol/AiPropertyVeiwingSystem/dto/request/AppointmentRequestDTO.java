package com.aol.AiPropertyVeiwingSystem.dto.request;

import com.aol.AiPropertyVeiwingSystem.dto.response.PropertyResponseDTO;
import com.aol.AiPropertyVeiwingSystem.dto.response.UserSummaryDTO;
import com.aol.AiPropertyVeiwingSystem.enums.AppointmentStatus;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class AppointmentRequestDTO {
    private Long id;
    private PropertyResponseDTO property;
    private UserSummaryDTO tenant;
    private UserSummaryDTO landlord;
    private LocalDateTime appointmentTime;
    private AppointmentStatus status;
    private LocalDateTime duration;
    private String notes;
} 