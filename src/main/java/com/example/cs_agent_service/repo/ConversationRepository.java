package com.example.cs_agent_service.repo;

import com.example.cs_agent_service.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
}
