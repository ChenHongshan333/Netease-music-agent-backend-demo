package com.example.cs_agent_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "更新知识库请求")
public record UpdateKnowledgeBaseRequest(
        @Size(max = 500, message = "问题长度不能超过500字符")
        @Schema(description = "常见问题", example = "黑胶VIP会员价格是多少？")
        String question,

        @Schema(description = "标准回复", example = "黑胶VIP会员价格请以App页面显示为准，活动价格可能会有变动...")
        String answer,

        @Size(max = 1000, message = "关键词长度不能超过1000字符")
        @Schema(description = "检索关键词，逗号分隔", example = "黑胶VIP,会员,价格,费用")
        String keywords
) {
}