# Devlog (Vibe Coding Diary)

记录真实迭代过程：每次只推进一小步，跑起来再优化。  
目的：让 repo 看起来不像“作业”，而是一个真实工程在成长。

---

## P0-0: 从 0 到能启动
- 初始化 Spring Boot 项目
- H2 in-memory + JPA 跑通
- Swagger UI 可用（用于快速验证接口）

---

## P0-1: KnowledgeBase + CRUD + Swagger
**Done**
- 建立 KnowledgeBase 实体（question/answer/keywords/active/时间）
- CRUD + list/search 接口可用
- 目标：先有数据、先能查，后面才能 RAG

---

## P0-2: 接入 DashScope（OpenAI Compatible）
### 问题 1：ObjectMapper 注入失败
现象：
- 启动报错：找不到 `ObjectMapper` Bean

原因：
- 项目依赖使用 `spring-boot-starter-webmvc`，某些情况下不会自动装配 `ObjectMapper`

解决：
- 在 `CsAgentServiceApplication` 里兜底提供 `@Bean ObjectMapper`（JsonMapper + findAndAddModules）

收获：
- 坚持“复用 Spring Boot 自带 Jackson”，不引入 Gson/Fastjson

### 问题 2：8080 端口被占用
现象：
- `Port 8080 was already in use`

解决：
- 结束占用进程，或调整 `server.port`

---

## P0-3: 最小 RAG 闭环
目标：
- `/api/agent/chat` 能完成：检索 → prompt 注入 → LLM → 返回 answer

### 问题 1：Swagger 里看到 `\n`
现象：
- answer 字符串里出现 `\n`，看起来像“没渲染”

结论：
- 这是 JSON 字符串的正常转义
- 真正前端渲染时会变成换行（或可以在前端做替换/渲染处理）

### 问题 2：检索“必须输入关键词”才命中
现象：
- `取消自动续费` 能命中
- `怎么取消自动续费` 命中率大幅下降

解决：
- 引入 `normalizeQuestion()`：去口水词/标点/后缀（怎么/如何/多少钱/是多少等）
- 检索策略：原句检索失败 → normalize 后再检索

经验：
- P0 阶段的“词法检索”非常依赖关键词，normalize 是成本最低的救命绳

### 问题 3：JPQL Query validation failed（因为注释/中文）
现象：
- 启动失败：Hibernate 解析 JPQL 报错，提示无法识别路径表达式（中文注释被当成语法）

原因：
- 在 `@Query` 字符串里写了 `--` 注释/中文说明，JPQL 不允许

解决：
- 把注释移出 `@Query` 字符串，保持纯 JPQL

---

## P0-4: 文档与发布
- README：按“可复现优先”的结构写
- 三种复现方式：
  - Swagger UI
  - curl
  - H2 Console + curl
- GitHub 发布：
  - `.gitignore` 避免 target/IDE 文件
  - 远端 repo 先有 README → 本地 push 前先 pull/rebase（保持历史干净）

---

## 下一步（P1 想做的）
- 让自然问法更容易命中（拆词/多轮检索/更强 normalize）
- 引用式回答，进一步压制幻觉
- 加 debug 信息与日志，方便定位 “为什么命中/为什么拒答”
