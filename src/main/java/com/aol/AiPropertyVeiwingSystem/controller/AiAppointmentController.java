package com.aol.AiPropertyVeiwingSystem.controller;

import com.aol.AiPropertyVeiwingSystem.dto.response.ApiResponse;
import com.aol.AiPropertyVeiwingSystem.dto.response.AppointmentResponseDTO;
import com.aol.AiPropertyVeiwingSystem.persistence.entity.User;
import com.aol.AiPropertyVeiwingSystem.persistence.repository.UserRepository;
import com.aol.AiPropertyVeiwingSystem.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ai-appointments")
@RequiredArgsConstructor
public class AiAppointmentController {
    private final AppointmentService appointmentService;
    private final UserRepository userRepository;

    @PostMapping("/process")
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> processAppointmentRequest(
            @RequestParam String conversationId,
            @RequestBody String userMessage) {
        return appointmentService.processAiAppointmentRequest(conversationId, userMessage);
    }

    @PostMapping("/validate/{appointmentId}")
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> validateAppointmentDetails(
            @PathVariable Long appointmentId) {
        return appointmentService.validateAiExtractedDetails(appointmentId);
    }

    @PostMapping("/confirm/{appointmentId}")
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> confirmAppointment(
            @PathVariable Long appointmentId,
            @RequestParam String confirmationCode) {
        return appointmentService.confirmAiAppointment(appointmentId, confirmationCode);
    }

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<String>> startConversation() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String conversationId = UUID.randomUUID().toString();
        appointmentService.createNewChatSession(conversationId, user);

        return ResponseEntity.ok(ApiResponse.success(conversationId));
    }
} 