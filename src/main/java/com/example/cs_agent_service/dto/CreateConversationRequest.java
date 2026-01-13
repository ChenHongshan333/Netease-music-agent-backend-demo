package com.example.cs_agent_service.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateConversationRequest(
        @NotBlank String customerId
) {}
