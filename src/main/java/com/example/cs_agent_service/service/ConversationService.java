package com.example.cs_agent_service.service;

import com.example.cs_agent_service.dto.ConversationResponse;
import com.example.cs_agent_service.dto.CreateConversationRequest;
import com.example.cs_agent_service.entity.Conversation;
import com.example.cs_agent_service.entity.ConversationStatus;
import com.example.cs_agent_service.repo.ConversationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.cs_agent_service.dto.AddMessageRequest;
import com.example.cs_agent_service.dto.MessageResponse;
import com.example.cs_agent_service.entity.Message;
import com.example.cs_agent_service.repo.MessageRepository;
import java.util.List;


@Service
public class ConversationService {

    private final ConversationRepository repo;
    private final MessageRepository messageRepo;

    public ConversationService(ConversationRepository repo, MessageRepository messageRepo) {
        this.repo = repo;
        this.messageRepo = messageRepo;
    }

    @Transactional
    public MessageResponse addMessage(Long conversationId, AddMessageRequest req) {
        Conversation c = repo.findById(conversationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found"));

        if (c.getStatus() == ConversationStatus.CLOSED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Conversation is CLOSED; cannot add messages");
        }

        Message m = new Message();
        m.setConversation(c);
        m.setSender(req.sender());
        m.setContent(req.content());

        Message saved = messageRepo.save(m);
        return toMessageResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> listMessages(Long conversationId) {
        if (!repo.existsById(conversationId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found");
        }
        return messageRepo.findByConversationIdOrderByCreatedAtAsc(conversationId)
                .stream()
                .map(this::toMessageResponse)
                .toList();
    }

    private MessageResponse toMessageResponse(Message m) {
        return new MessageResponse(
                m.getId(),
                m.getSender(),
                m.getContent(),
                m.getCreatedAt()
        );
    }

    @Transactional
    public ConversationResponse create(CreateConversationRequest req) {
        Conversation c = new Conversation();
        c.setCustomerId(req.customerId());
        c.setStatus(ConversationStatus.OPEN);
        Conversation saved = repo.save(c);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ConversationResponse get(Long id) {
        Conversation c = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found"));
        return toResponse(c);
    }

    @Transactional
    public ConversationResponse close(Long id) {
        Conversation c = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found"));
        c.setStatus(ConversationStatus.CLOSED);
        Conversation saved = repo.save(c);
        return toResponse(saved);
    }

    // return DTO-form of data
    private ConversationResponse toResponse(Conversation c) {
        return new ConversationResponse(
                c.getId(),
                c.getCustomerId(),
                c.getStatus(),
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }
}
