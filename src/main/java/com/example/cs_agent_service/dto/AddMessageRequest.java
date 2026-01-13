package com.example.cs_agent_service.dto;

import com.example.cs_agent_service.entity.MessageSender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddMessageRequest(
        @NotNull MessageSender sender,
        @NotBlank String content
) {}
