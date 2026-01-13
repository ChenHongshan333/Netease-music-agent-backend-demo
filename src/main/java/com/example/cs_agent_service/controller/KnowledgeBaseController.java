package com.example.cs_agent_service.controller;

import com.example.cs_agent_service.dto.CreateKnowledgeBaseRequest;
import com.example.cs_agent_service.dto.KnowledgeBaseResponse;
import com.example.cs_agent_service.dto.UpdateKnowledgeBaseRequest;
import com.example.cs_agent_service.service.KnowledgeBaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
@Tag(name = "知识库管理", description = "网易云音乐客服知识库API")
public class KnowledgeBaseController {

    private final KnowledgeBaseService service;

    public KnowledgeBaseController(KnowledgeBaseService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "创建知识库条目", description = "添加新的客服知识库问答")
    public KnowledgeBaseResponse create(@Valid @RequestBody CreateKnowledgeBaseRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取知识库详情", description = "根据ID获取单个知识库条目详情")
    public KnowledgeBaseResponse get(@PathVariable @Parameter(description = "知识库ID") Long id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新知识库条目", description = "修改已有的知识库问答内容")
    public KnowledgeBaseResponse update(
            @PathVariable @Parameter(description = "知识库ID") Long id,
            @Valid @RequestBody UpdateKnowledgeBaseRequest request) {
        return service.update(id, request);
    }

    @GetMapping
    @Operation(summary = "查询知识库列表", description = "获取知识库列表，支持关键词搜索")
    public List<KnowledgeBaseResponse> list(
            @RequestParam(required = false) 
            @Parameter(description = "搜索关键词，模糊匹配问题和关键词字段") 
            String q) {
        return service.list(q);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "软删除知识库条目", description = "将知识库条目标记为不可用（软删除）")
    public void deactivate(@PathVariable @Parameter(description = "知识库ID") Long id) {
        service.deactivate(id);
    }
}