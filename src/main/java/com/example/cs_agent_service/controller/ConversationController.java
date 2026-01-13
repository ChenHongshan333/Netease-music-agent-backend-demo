package com.example.cs_agent_service.controller;

import com.example.cs_agent_service.dto.ConversationResponse;
import com.example.cs_agent_service.dto.CreateConversationRequest;
import com.example.cs_agent_service.service.ConversationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import com.example.cs_agent_service.dto.AddMessageRequest;
import com.example.cs_agent_service.dto.MessageResponse;
import java.util.List;


@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService service;

    public ConversationController(ConversationService service) {
        this.service = service;
    }

    @PostMapping("/{id}/messages")
    public MessageResponse addMessage(@PathVariable Long id, @Valid @RequestBody AddMessageRequest req) {
        return service.addMessage(id, req);
    }

    @GetMapping("/{id}/messages")
    public List<MessageResponse> listMessages(@PathVariable Long id) {
        return service.listMessages(id);
    }

    @PostMapping
    public ConversationResponse create(@Valid @RequestBody CreateConversationRequest req) {
        return service.create(req);
    }

    @GetMapping("/{id}")
    public ConversationResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping("/{id}/close")
    public ConversationResponse close(@PathVariable Long id) {
        return service.close(id);
    }
}
