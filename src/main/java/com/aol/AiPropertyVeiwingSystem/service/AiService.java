package com.aol.AiPropertyVeiwingSystem.service;

import com.aol.AiPropertyVeiwingSystem.dto.response.ApiResponse;
import com.aol.AiPropertyVeiwingSystem.dto.response.AppointmentResponseDTO;

public interface AiService {
    ApiResponse<AppointmentResponseDTO> processAppointmentRequest(String conversationId, String userMessage);
    ApiResponse<AppointmentResponseDTO> validateExtractedDetails(Long appointmentId);
    ApiResponse<AppointmentResponseDTO> confirmAppointment(Long appointmentId, String confirmationCode);
} 