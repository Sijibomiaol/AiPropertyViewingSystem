package com.aol.AiPropertyVeiwingSystem.dto.response;

import com.aol.AiPropertyVeiwingSystem.enums.AppointmentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppointmentResponseDTO {
    private Long id;
    private Long propertyId;
    private UserSummaryDTO landlord;
    private UserSummaryDTO tenant;
    private LocalDateTime appointmentTime;
    private AppointmentStatus status;
    private String contactPhone;
    private String specialInstructions;
    private String contactEmail;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String aiExtractedDetails;
    private String aiConversationId;
    private Boolean tenantReminderSent;
    private Boolean landlordReminderSent;
    private Boolean aiDataValidated;
    private Integer duration;
    private Boolean isConfirmed;
    private String feedback;
    private Integer rating;
} 