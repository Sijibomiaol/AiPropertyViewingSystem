package com.aol.AiPropertyVeiwingSystem.dto.request;

import com.aol.AiPropertyVeiwingSystem.enums.MessageType;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class ChatMessageDTO {
    String content;
    String sessionId;
    MessageType type;
    LocalDateTime timestamp;
} 