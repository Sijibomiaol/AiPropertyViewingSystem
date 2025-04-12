package com.aol.AiPropertyVeiwingSystem.service;

import com.aol.AiPropertyVeiwingSystem.dto.response.ApiResponse;
import com.aol.AiPropertyVeiwingSystem.dto.response.AppointmentResponseDTO;
import com.aol.AiPropertyVeiwingSystem.persistence.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

public interface AppointmentService {
    ResponseEntity<ApiResponse<AppointmentResponseDTO>> processAiAppointmentRequest(String conversationId, String userMessage);
    
    ResponseEntity<ApiResponse<AppointmentResponseDTO>> validateAiExtractedDetails(Long appointmentId);
    
    ResponseEntity<ApiResponse<AppointmentResponseDTO>> confirmAiAppointment(Long appointmentId, String confirmationCode);
    
    @Transactional
    ResponseEntity<ApiResponse<String>> createNewChatSession(String conversationId, com.aol.AiPropertyVeiwingSystem.persistence.entity.User user);
} 