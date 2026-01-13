package com.example.cs_agent_service.dto;

import com.example.cs_agent_service.entity.ConversationStatus;
import java.time.Instant;

public record ConversationResponse(
        Long id,
        String customerId,
        ConversationStatus status,
        Instant createdAt,
        Instant updatedAt
) {}

