package com.example.cs_agent_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "知识库响应")
public record KnowledgeBaseResponse(
        @Schema(description = "主键ID", example = "1")
        Long id,

        @Schema(description = "常见问题", example = "黑胶VIP会员价格是多少？")
        String question,

        @Schema(description = "标准回复", example = "黑胶VIP会员价格请以App页面显示为准，活动价格可能会有变动...")
        String answer,

        @Schema(description = "检索关键词，逗号分隔", example = "黑胶VIP,会员,价格,费用")
        String keywords,

        @Schema(description = "是否启用", example = "true")
        Boolean active,

        @Schema(description = "创建时间", example = "2026-01-13T10:30:00")
        LocalDateTime createTime
) {
}