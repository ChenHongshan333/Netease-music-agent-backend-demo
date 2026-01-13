package com.example.cs_agent_service.service.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Component
public class DashScopeClient {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    // OkHttpClient 必须单例复用
    private static final OkHttpClient HTTP = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .callTimeout(45, TimeUnit.SECONDS)
            .build();

    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final String apiKey;
    private final String model;
    private final double temperature;

    public DashScopeClient(
            ObjectMapper objectMapper,
            @Value("${agent.llm.base-url}") String baseUrl,
            @Value("${agent.llm.api-key}") String apiKey,
            @Value("${agent.llm.model}") String model,
            @Value("${agent.llm.temperature:0.3}") double temperature
    ) {
        this.objectMapper = objectMapper;
        this.baseUrl = normalizeBaseUrl(baseUrl);
        this.apiKey = apiKey;
        this.model = model;
        this.temperature = temperature;
    }

    public String call(String systemMsg, String userMsg) {
        if (apiKey == null || apiKey.isBlank() || apiKey.contains("${")) {
            throw new RuntimeException("DashScope apiKey 未配置：请设置环境变量 DASHSCOPE_API_KEY");
        }
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new RuntimeException("DashScope baseUrl 未配置：agent.llm.base-url");
        }

        String url = baseUrl + "/chat/completions";

        String requestJson;
        try {
            ObjectNode root = objectMapper.createObjectNode();
            root.put("model", model);
            root.put("temperature", temperature);

            ArrayNode messages = root.putArray("messages");
            messages.addObject()
                    .put("role", "system")
                    .put("content", systemMsg);
            messages.addObject()
                    .put("role", "user")
                    .put("content", userMsg);

            requestJson = objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            throw new RuntimeException("DashScope 请求体序列化失败: " + e.getMessage(), e);
        }

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(requestJson.getBytes(StandardCharsets.UTF_8), JSON))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .build();

        try (Response response = HTTP.newCall(request).execute()) {
            String body = response.body() == null ? "" : response.body().string();

            if (response.code() != 200) {
                throw new RuntimeException("DashScope 请求失败: status=" + response.code() + ", body=" + body);
            }

            return extractContent(body);
        } catch (IOException e) {
            throw new RuntimeException("DashScope 请求异常: " + e.getMessage(), e);
        }
    }

    private String extractContent(String rawJson) {
        try {
            JsonNode root = objectMapper.readTree(rawJson);
            JsonNode choices = root.get("choices");
            if (choices == null || !choices.isArray() || choices.isEmpty()) {
                throw new RuntimeException("DashScope 响应缺少 choices 字段，raw=" + snippet(rawJson));
            }

            JsonNode message = choices.get(0).get("message");
            if (message == null || message.isNull()) {
                throw new RuntimeException("DashScope 响应缺少 choices[0].message，raw=" + snippet(rawJson));
            }

            JsonNode content = message.get("content");
            if (content == null || content.isNull() || !content.isTextual()) {
                throw new RuntimeException("DashScope 响应缺少 choices[0].message.content，raw=" + snippet(rawJson));
            }

            return content.asText();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("DashScope 响应解析失败，raw=" + snippet(rawJson) + ", err=" + e.getMessage(), e);
        }
    }

    private String normalizeBaseUrl(String s) {
        if (s == null) return null;
        String t = s.trim();
        while (t.endsWith("/")) {
            t = t.substring(0, t.length() - 1);
        }
        return t;
    }

    private String snippet(String s) {
        if (s == null) return "null";
        int max = 800;
        return s.length() <= max ? s : s.substring(0, max) + "...(truncated)";
    }
}

