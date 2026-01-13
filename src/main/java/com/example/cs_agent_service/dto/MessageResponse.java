package com.example.cs_agent_service.dto;

import com.example.cs_agent_service.entity.MessageSender;
import java.time.Instant;

public record MessageResponse(
        Long id,
        MessageSender sender,
        String content,
        Instant createdAt
) {}
