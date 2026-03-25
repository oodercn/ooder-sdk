package net.ooder.scene.driver.tiny;

import net.ooder.scene.spi.LlmProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * LLM 提供者 - Tiny 实现
 *
 * <p>基于 Ollama 本地调用的 LLM 实现</p>
 *
 * <p>配置项：</p>
 * <pre>
 * scene.engine.tiny.llm.endpoint: http://localhost:11434
 * scene.engine.tiny.llm.model: llama2
 * </pre>
 *
 * @author Ooder Team
 * @since 3.0.0
 */
@Component
@ConditionalOnMissingBean(LlmProvider.class)
public class TinyLlmProvider implements LlmProvider {

    private static final Logger log = LoggerFactory.getLogger(TinyLlmProvider.class);

    private final HttpClient httpClient;

    @Value("${scene.engine.tiny.llm.endpoint:http://localhost:11434}")
    private String endpoint;

    @Value("${scene.engine.tiny.llm.model:llama2}")
    private String model;

    public TinyLlmProvider() {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();
    }

    @Override
    public String getProviderType() {
        return "tiny";
    }

    @Override
    public String chat(String prompt, Map<String, Object> params) {
        try {
            String requestBody = String.format(
                "{\"model\":\"%s\",\"prompt\":\"%s\",\"stream\":false}",
                model,
                escapeJson(prompt)
            );

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint + "/api/generate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .timeout(Duration.ofMinutes(5))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return extractResponse(response.body());
            } else {
                log.error("LLM call failed: {}", response.body());
                return "Error: LLM call failed with status " + response.statusCode();
            }
        } catch (Exception e) {
            log.error("Failed to call LLM", e);
            return "Error: " + e.getMessage();
        }
    }

    @Override
    public CompletableFuture<String> chatAsync(String prompt, Map<String, Object> params) {
        return CompletableFuture.supplyAsync(() -> chat(prompt, params));
    }

    @Override
    public void chatStream(String prompt, Map<String, Object> params, StreamCallback callback) {
        try {
            String requestBody = String.format(
                "{\"model\":\"%s\",\"prompt\":\"%s\",\"stream\":true}",
                model,
                escapeJson(prompt)
            );

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint + "/api/generate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .timeout(Duration.ofMinutes(5))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            StringBuilder fullResponse = new StringBuilder();
            String[] lines = response.body().split("\n");

            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                String token = extractToken(line);
                if (token != null) {
                    fullResponse.append(token);
                    callback.onNext(token);
                }
            }

            callback.onComplete(fullResponse.toString());

        } catch (Exception e) {
            callback.onError(e);
        }
    }

    @Override
    public String chatWithContext(String prompt, List<Map<String, String>> context, Map<String, Object> params) {
        StringBuilder fullPrompt = new StringBuilder();

        for (Map<String, String> msg : context) {
            String role = msg.getOrDefault("role", "user");
            String content = msg.getOrDefault("content", "");
            fullPrompt.append(role).append(": ").append(content).append("\n");
        }

        fullPrompt.append("user: ").append(prompt);

        return chat(fullPrompt.toString(), params);
    }

    @Override
    public String getModelName() {
        return model;
    }

    @Override
    public boolean isAvailable() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint + "/api/tags"))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            log.debug("LLM not available: {}", e.getMessage());
            return false;
        }
    }

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }

    private String extractResponse(String body) {
        try {
            int start = body.indexOf("\"response\":\"");
            if (start == -1) return body;

            start += 12;
            int end = body.indexOf("\"", start);
            if (end == -1) return body;

            return body.substring(start, end)
                .replace("\\n", "\n")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
        } catch (Exception e) {
            return body;
        }
    }

    private String extractToken(String line) {
        try {
            int start = line.indexOf("\"response\":\"");
            if (start == -1) return null;

            start += 12;
            int end = line.indexOf("\"", start);
            if (end == -1) return null;

            return line.substring(start, end)
                .replace("\\n", "\n")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
        } catch (Exception e) {
            return null;
        }
    }
}
