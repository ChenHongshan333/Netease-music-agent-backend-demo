# Netease Cloud Music CS Agent: Minimal RAG Customer Support (Spring Boot + DashScope)

A minimal, reproducible **RAG-style customer support Agent** for NetEase Cloud Music.

**What it does**
- **Retrieve** top-5 relevant KnowledgeBase entries (H2 + Spring Data JPA)
- **Grounded answering** with a strict system policy: *answer only based on retrieved “Known Info”, otherwise refuse*
- **LLM backend**: Alibaba Bailian **DashScope (OpenAI-compatible endpoint)** via OkHttp + Spring Boot Jackson `ObjectMapper`
- **API**: `GET /api/agent/chat?question=...` → `{"answer":"...","hits":N}`

**Highlights (the “vibe coding” bits)**
- Minimal closed loop: **Controller → Retrieval → Prompt → LLM → JSON response**
- Strict anti-hallucination: if `hits == 0`, **no LLM call**, direct refusal
- Retrieval robustness: **normalize + retry** (removes filler words/punctuations and “多少钱/是多少”等尾巴)
- No extra JSON libs: **reuses Spring Boot Jackson** (`ObjectMapper`), no Gson/Fastjson
- Config via env var: `DASHSCOPE_API_KEY`

---

## Environment Setup

### Requirements
- JDK 17+
- Maven 3.8+
- DashScope API Key (Alibaba Bailian)

### 1) Set API Key (choose one)

**Windows (PowerShell)**
```powershell
setx DASHSCOPE_API_KEY "YOUR_KEY"
```

**macOS / Linus**
```bash
export DASHSCOPE_API_KEY="YOUR_KEY"
```

### 2) Run
``` bash
mvn spring-boot:run
```
After startup:
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- H2 Console : `http://localhost:8080/h2`


## Quick Start
### 1) 
