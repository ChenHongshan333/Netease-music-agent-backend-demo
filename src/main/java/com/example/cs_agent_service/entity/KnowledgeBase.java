package com.example.cs_agent_service.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "knowledge_base")
@Schema(description = "网易云音乐客服知识库")
public class KnowledgeBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Column(nullable = false, length = 500)
    @Schema(description = "常见问题", example = "黑胶VIP会员价格是多少？")
    private String question;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Schema(description = "标准回复", example = "黑胶VIP会员价格请以App页面显示为准，活动价格可能会有变动...")
    private String answer;

    @Column(length = 1000)
    @Schema(description = "检索关键词，逗号分隔", example = "黑胶VIP,会员,价格,费用")
    private String keywords;

    @Column(nullable = false)
    @Schema(description = "是否启用", example = "true", defaultValue = "true")
    private Boolean active = true;

    @Column(nullable = false, updatable = false)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
        if (this.active == null) {
            this.active = true;
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}