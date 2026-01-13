package com.example.cs_agent_service.controller;

import com.example.cs_agent_service.entity.KnowledgeBase;
import com.example.cs_agent_service.service.KnowledgeBaseService;
import com.example.cs_agent_service.service.ai.DashScopeClient;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private final KnowledgeBaseService knowledgeBaseService;
    private final DashScopeClient dashScopeClient;

    public AgentController(KnowledgeBaseService knowledgeBaseService, DashScopeClient dashScopeClient) {
        this.knowledgeBaseService = knowledgeBaseService;
        this.dashScopeClient = dashScopeClient;
    }

    @GetMapping("/chat")
    @Operation(summary = "智能客服问答测试")
    public Map<String, Object> chat(@RequestParam("question") String question) {
        if (question == null || question.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "question 不能为空");
        }
        String q = question.trim();

        List<KnowledgeBase> hits = knowledgeBaseService.searchTop5(q);
        if (hits == null || hits.isEmpty()) {
            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("answer", "抱歉，小云暂时还没学会这个问题");
            resp.put("hits", 0);
            return resp;
        }

        String systemMsg = """
            你是网易云音乐智能客服小云，请用亲切活泼的语气回答。
            必须优先基于【已知信息】回答；
            如果已知信息不足，就回答：'抱歉，小云暂时还没学会这个问题'。
            不要编造事实。
        """.trim();

        StringBuilder known = new StringBuilder();
        known.append("已知信息：\n");
        for (int i = 0; i < hits.size(); i++) {
            String ans = hits.get(i).getAnswer();
            if (ans == null) ans = "";
            known.append("[").append(i + 1).append("] ").append(ans).append("\n");
        }
        known.append("用户问题：").append(q);

        String answer = dashScopeClient.call(systemMsg, known.toString());

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("answer", answer);
        resp.put("hits", hits.size());
        return resp;
    }
}

