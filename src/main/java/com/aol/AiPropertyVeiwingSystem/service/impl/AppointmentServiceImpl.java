package com.aol.AiPropertyVeiwingSystem.service.impl;

import com.aol.AiPropertyVeiwingSystem.dto.response.ApiResponse;
import com.aol.AiPropertyVeiwingSystem.dto.response.AppointmentResponseDTO;
import com.aol.AiPropertyVeiwingSystem.dto.response.UserSummaryDTO;
import com.aol.AiPropertyVeiwingSystem.enums.AppointmentStatus;
import com.aol.AiPropertyVeiwingSystem.enums.ChatSessionStatus;
import com.aol.AiPropertyVeiwingSystem.enums.MessageType;
import com.aol.AiPropertyVeiwingSystem.exception.ResourceNotFoundException;
import com.aol.AiPropertyVeiwingSystem.model.*;
import com.aol.AiPropertyVeiwingSystem.persistence.entity.*;
import com.aol.AiPropertyVeiwingSystem.persistence.repository.*;
import com.aol.AiPropertyVeiwingSystem.service.AppointmentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Value("${openai.model}")
    private String openAiModel;

    private final AppointmentRepository appointmentRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> processAiAppointmentRequest(String conversationId, String userMessage) {
        try {
            ChatSession chatSession = chatSessionRepository.findBySessionId(conversationId)
                    .orElseThrow(() -> new RuntimeException("Chat session not found. Please start a conversation first."));

            ChatMessage userChatMessage = ChatMessage.builder()
                    .chatSession(chatSession)
                    .content(userMessage)
                    .type(MessageType.USER)
                    .build();
            chatMessageRepository.save(userChatMessage);
            chatSession.addMessage(userChatMessage);

            OpenAiService service = new OpenAiService(openAiApiKey, Duration.ofSeconds(30));
            List<com.theokanning.openai.completion.chat.ChatMessage> openAiMessages = new ArrayList<>();

            chatSession.getMessages().forEach(msg -> {
                openAiMessages.add(new com.theokanning.openai.completion.chat.ChatMessage(
                        msg.getType() == MessageType.USER ? "user" : "assistant",
                        msg.getContent()
                ));
            });

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(openAiModel)
                    .messages(openAiMessages)
                    .build();

            String aiResponse = service.createChatCompletion(request)
                    .getChoices()
                    .get(0)
                    .getMessage()
                    .getContent();

            ChatMessage aiChatMessage = ChatMessage.builder()
                    .chatSession(chatSession)
                    .content(aiResponse)
                    .type(MessageType.AI)
                    .aiResponse(aiResponse)
                    .build();
            chatMessageRepository.save(aiChatMessage);
            chatSession.addMessage(aiChatMessage);

            chatSession.setStatus(ChatSessionStatus.ACTIVE);
            chatSession.updateLastActive();
            chatSessionRepository.save(chatSession);

            AppointmentResponseDTO appointment = processAiResponse(aiResponse);

            return ResponseEntity.ok(ApiResponse.success(appointment));
        } catch (Exception e) {
            log.error("Error processing AI appointment request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error processing AI request: " + e.getMessage()));
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> validateAiExtractedDetails(Long appointmentId) {
        try {
            Appointment appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

            if (appointment.getAiExtractedDetails() == null || appointment.getAiExtractedDetails().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("No AI extracted details found for this appointment"));
            }

            boolean isValid = validateAppointmentDetails(appointment);
            if (!isValid) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid appointment details extracted by AI"));
            }

            appointment.setAiDataValidated(true);
            Appointment updatedAppointment = appointmentRepository.save(appointment);

            return ResponseEntity.ok(ApiResponse.success(convertToResponseDTO(updatedAppointment)));
        } catch (ResourceNotFoundException e) {
            log.error("Error validating AI details: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error validating AI details", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error validating AI details"));
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> confirmAiAppointment(Long appointmentId, String confirmationCode) {
        try {
            Appointment appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

            if (!isValidConfirmationCode(appointment, confirmationCode)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid confirmation code"));
            }

            appointment.setStatus(AppointmentStatus.CONFIRMED);
            Appointment updatedAppointment = appointmentRepository.save(appointment);

            return ResponseEntity.ok(ApiResponse.success(convertToResponseDTO(updatedAppointment)));
        } catch (ResourceNotFoundException e) {
            log.error("Error confirming AI appointment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error confirming AI appointment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error confirming AI appointment"));
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<String>> createNewChatSession(String conversationId, User user) {
        try {
            ChatSession newSession = ChatSession.builder()
                    .sessionId(conversationId)
                    .user(user)
                    .status(ChatSessionStatus.ACTIVE)
                    .lastActive(LocalDateTime.now())
                    .build();

            chatSessionRepository.save(newSession);
            return ResponseEntity.ok(ApiResponse.success(conversationId));
        } catch (Exception e) {
            log.error("Error creating new chat session: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create new chat session: " + e.getMessage()));
        }
    }

    // Helper Methods
    private boolean validateAppointmentDetails(Appointment appointment) {
        if (appointment.getProperty() == null) {
            log.error("Appointment validation failed: Property is required");
            return false;
        }
        if (appointment.getTenant() == null) {
            log.error("Appointment validation failed: Tenant is required");
            return false;
        }
        if (appointment.getAppointmentTime() == null) {
            log.error("Appointment validation failed: Appointment time is required");
            return false;
        }
        if (appointment.getDuration() == null) {
            log.error("Appointment validation failed: End time is required");
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        if (appointment.getAppointmentTime().isBefore(now)) {
            log.error("Appointment validation failed: Appointment time cannot be in the past");
            return false;
        }
        if (appointment.getStatus() == null) {
            log.error("Appointment validation failed: Status is required");
            return false;
        }

        if (appointment.getConfirmationCode() != null) {
            if (appointment.getConfirmationCode().length() != 6) {
                log.error("Appointment validation failed: Invalid confirmation code length");
                return false;
            }
        }
        return true;
    }

    private boolean isValidConfirmationCode(Appointment appointment, String confirmationCode) {
        try {
            if (confirmationCode == null || confirmationCode.trim().isEmpty()) {
                log.error("Confirmation code validation failed: Code is empty");
                return false;
            }

            if (!confirmationCode.matches("[A-Z0-9]{6}")) {
                log.error("Confirmation code validation failed: Invalid format");
                return false;
            }

            if (!confirmationCode.equals(appointment.getConfirmationCode())) {
                log.error("Confirmation code validation failed: Code mismatch");
                return false;
            }

            if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
                log.error("Confirmation code validation failed: Invalid appointment status");
                return false;
            }

            return true;
        } catch (Exception e) {
            log.error("Error validating confirmation code: {}", e.getMessage());
            return false;
        }
    }

    private AppointmentResponseDTO processAiResponse(String aiResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseNode = objectMapper.readTree(aiResponse);

            Long propertyId = extractAndValidateLong(responseNode, "propertyId", "Property ID");
            Long tenantId = extractAndValidateLong(responseNode, "tenantId", "Tenant ID");
            LocalDateTime appointmentTime = extractAndValidateDateTime(responseNode, "appointmentTime", "Appointment time");
            LocalDateTime endTime = extractAndValidateDateTime(responseNode, "endTime", "End time");

            String notes = responseNode.path("notes").asText("");
            String locationDetails = responseNode.path("locationDetails").asText("");
            String specialInstructions = responseNode.path("specialInstructions").asText("");
            String contactPhone = responseNode.path("contactPhone").asText("");
            String contactEmail = responseNode.path("contactEmail").asText("");

            validateTimeConstraints(appointmentTime, endTime);

            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));
            User tenant = userRepository.findById(tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + tenantId));
            User landlord = property.getLandlord();

            if (hasOverlappingAppointments(propertyId, appointmentTime, endTime)) {
                throw new IllegalArgumentException("There is an overlapping appointment for this property");
            }

            return AppointmentResponseDTO.builder()
                    .propertyId(propertyId)
                    .tenant(convertToUserSummaryDTO(tenant))
                    .landlord(convertToUserSummaryDTO(landlord))
                    .appointmentTime(appointmentTime)
                    .status(AppointmentStatus.SCHEDULED)
                    .notes(notes)
                    .specialInstructions(specialInstructions)
                    .contactPhone(contactPhone)
                    .contactEmail(contactEmail)
                    .aiDataValidated(false)
                    .aiExtractedDetails(aiResponse)
                    .build();

        } catch (JsonProcessingException e) {
            log.error("Error parsing AI response JSON: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid AI response format: " + e.getMessage());
        } catch (ResourceNotFoundException e) {
            log.error("Resource not found while processing AI response: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("Validation error in AI response: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error processing AI response: {}", e.getMessage());
            throw new RuntimeException("Error processing AI response: " + e.getMessage());
        }
    }

    private Long extractAndValidateLong(JsonNode node, String fieldName, String fieldDescription) {
        JsonNode valueNode = node.path(fieldName);
        if (valueNode.isMissingNode() || valueNode.isNull()) {
            throw new IllegalArgumentException(fieldDescription + " is required");
        }
        try {
            return valueNode.asLong();
        } catch (Exception e) {
            throw new IllegalArgumentException(fieldDescription + " must be a valid number");
        }
    }

    private LocalDateTime extractAndValidateDateTime(JsonNode node, String fieldName, String fieldDescription) {
        JsonNode valueNode = node.path(fieldName);
        if (valueNode.isMissingNode() || valueNode.isNull()) {
            throw new IllegalArgumentException(fieldDescription + " is required");
        }
        try {
            return LocalDateTime.parse(valueNode.asText());
        } catch (Exception e) {
            throw new IllegalArgumentException(fieldDescription + " must be a valid date-time in ISO format");
        }
    }

    private void validateTimeConstraints(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime now = LocalDateTime.now();

        if (startTime.isBefore(now)) {
            throw new IllegalArgumentException("Appointment time must be in the future");
        }

        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End time must be after appointment time");
        }

        long durationMinutes = java.time.Duration.between(startTime, endTime).toMinutes();
        if (durationMinutes < 15 || durationMinutes > 240) {
            throw new IllegalArgumentException("Appointment duration must be between 15 minutes and 4 hours");
        }
    }

    private boolean hasOverlappingAppointments(Long propertyId, LocalDateTime startTime, LocalDateTime endTime) {
        return appointmentRepository.existsOverlappingAppointment(
                propertyId, startTime, endTime, AppointmentStatus.CANCELLED, AppointmentStatus.COMPLETED);
    }

    private AppointmentResponseDTO convertToResponseDTO(Appointment appointment) {
        return AppointmentResponseDTO.builder()
                .id(appointment.getId())
                .propertyId(appointment.getProperty().getId())
                .tenant(convertToUserSummaryDTO(appointment.getTenant()))
                .landlord(convertToUserSummaryDTO(appointment.getProperty().getLandlord()))
                .appointmentTime(appointment.getAppointmentTime())
                .status(appointment.getStatus())
                .notes(appointment.getNotes())
                .feedback(appointment.getFeedback())
                .rating(appointment.getRating())
                .tenantReminderSent(Boolean.TRUE.equals(appointment.getTenantReminderSent()))
                .landlordReminderSent(Boolean.TRUE.equals(appointment.getLandlordReminderSent()))
                .aiDataValidated(Boolean.TRUE.equals(appointment.getAiDataValidated()))
                .aiConversationId(appointment.getAiConversationId())
                .aiExtractedDetails(appointment.getAiExtractedDetails())
                .build();
    }

    private UserSummaryDTO convertToUserSummaryDTO(User user) {
        return UserSummaryDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
} 