package net.ooder.scene.driver.small;

import net.ooder.scene.spi.LlmProvider;
import net.ooder.scene.spi.VectorStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * LLM 提供者 - Small 实现
 *
 * <p>基于远程 API 调用的 LLM 实现</p>
 *
 * <p>支持：OpenAI, Azure OpenAI, 自定义 API</p>
 *
 * @author Ooder Team
 * @since 3.0.0
 */
@Component
@ConditionalOnMissingBean(LlmProvider.class)
public class SmallLlmProvider implements LlmProvider {

    private static final Logger log = LoggerFactory.getLogger(SmallLlmProvider.class);

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${scene.engine.small.llm.endpoint:https://api.openai.com/v1}")
    private String endpoint;

    @Value("${scene.engine.small.llm.api-key:}")
    private String apiKey;

    @Value("${scene.engine.small.llm.model:gpt-3.5-turbo}")
    private String model;

    @Override
    public String getProviderType() {
        return "small";
    }

    @Override
    public String chat(String prompt, Map<String, Object> params) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt)));
        requestBody.putAll(params);

        try {
            Map<String, Object> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + apiKey);
            headers.put("Content-Type", "application/json");

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(
                endpoint + "/chat/completions",
                requestBody,
                Map.class
            );

            if (response != null && response.containsKey("choices")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (!choices.isEmpty()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }

            return "Error: No response from LLM";
        } catch (Exception e) {
            log.error("LLM call failed", e);
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
            String response = chat(prompt, params);
            callback.onComplete(response);
        } catch (Exception e) {
            callback.onError(e);
        }
    }

    @Override
    public String chatWithContext(String prompt, List<Map<String, String>> context, Map<String, Object> params) {
        List<Map<String, String>> messages = new ArrayList<>(context);
        messages.add(Map.of("role", "user", "content", prompt));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", messages);
        requestBody.putAll(params);

        return chat(prompt, params);
    }

    @Override
    public String getModelName() {
        return model;
    }

    @Override
    public boolean isAvailable() {
        try {
            String response = restTemplate.getForObject(endpoint + "/models", String.class);
            return response != null;
        } catch (Exception e) {
            log.warn("LLM not available: {}", e.getMessage());
            return false;
        }
    }
}
