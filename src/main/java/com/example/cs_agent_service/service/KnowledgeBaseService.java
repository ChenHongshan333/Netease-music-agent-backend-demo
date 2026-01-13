package com.example.cs_agent_service.service;

import com.example.cs_agent_service.dto.CreateKnowledgeBaseRequest;
import com.example.cs_agent_service.dto.KnowledgeBaseResponse;
import com.example.cs_agent_service.dto.UpdateKnowledgeBaseRequest;
import com.example.cs_agent_service.entity.KnowledgeBase;
import com.example.cs_agent_service.repo.KnowledgeBaseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Service
public class KnowledgeBaseService {

    private final KnowledgeBaseRepository repository;

    public KnowledgeBaseService(KnowledgeBaseRepository repository) {
        this.repository = repository;
    }

    public List<KnowledgeBase> searchTop5(String question) {
        Pageable top5 = PageRequest.of(0, 5);

        List<KnowledgeBase> hits = repository.searchActiveTop(question, top5);
        if (!hits.isEmpty()) return hits;

        String q2 = normalizeQuestion(question);
        if (!q2.isBlank() && !q2.equals(question)) {
            return repository.searchActiveTop(q2, top5);
        }

        return hits;
    }

    private String normalizeQuestion(String q) {
        if (q == null) return "";
        String s = q.trim();
        s = s.replaceAll("[\\s\\p{Punct}，。！？、；：“”‘’（）()【】\\[\\]{}<>《》]+", "");
        s = s.replaceAll("(请问|麻烦|帮我|我想问|想问|请|怎么|如何|怎样|要|想|能|可以|我想知道)+", "");
        s = s.replaceAll("(呢|呀|吗|啊|嘛)+$", "");
        s = s.replaceAll("(要多少钱|多少钱|多少|价格是多少|价钱是多少|是多少)$", "");
        return s;
    }

    @Transactional
    public KnowledgeBaseResponse create(CreateKnowledgeBaseRequest request) {
        KnowledgeBase kb = new KnowledgeBase();
        kb.setQuestion(request.question());
        kb.setAnswer(request.answer());
        kb.setKeywords(request.keywords());
        kb.setActive(true);
        
        KnowledgeBase saved = repository.save(kb);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public KnowledgeBaseResponse get(Long id) {
        KnowledgeBase kb = repository.findByIdAndActiveTrue(id);
        if (kb == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Knowledge base not found or inactive");
        }
        return toResponse(kb);
    }

    @Transactional
    public KnowledgeBaseResponse update(Long id, UpdateKnowledgeBaseRequest request) {
        KnowledgeBase kb = repository.findByIdAndActiveTrue(id);
        if (kb == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Knowledge base not found or inactive");
        }
        
        if (request.question() != null) {
            kb.setQuestion(request.question());
        }
        if (request.answer() != null) {
            kb.setAnswer(request.answer());
        }
        if (request.keywords() != null) {
            kb.setKeywords(request.keywords());
        }
        
        KnowledgeBase saved = repository.save(kb);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<KnowledgeBaseResponse> list(String query) {
        List<KnowledgeBase> knowledgeList;
        
        if (query == null || query.trim().isEmpty()) {
            knowledgeList = repository.findByActiveTrue();
        } else {
            knowledgeList = repository.searchByKeyword(query.trim());
        }
        
        return knowledgeList.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void deactivate(Long id) {
        KnowledgeBase kb = repository.findByIdAndActiveTrue(id);
        if (kb == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Knowledge base not found or already inactive");
        }
        
        kb.setActive(false);
        repository.save(kb);
    }

    private KnowledgeBaseResponse toResponse(KnowledgeBase kb) {
        return new KnowledgeBaseResponse(
                kb.getId(),
                kb.getQuestion(),
                kb.getAnswer(),
                kb.getKeywords(),
                kb.getActive(),
                kb.getCreateTime()
        );
    }
}