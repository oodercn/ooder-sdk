package net.ooder.scene.core.skill.llm;

import net.ooder.scene.core.security.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LLM服务技能
 *
 * <p>包装SDK语言模型能力，添加安全检查和审计日志</p>
 */
public class LlmSkillService extends SecureSkillService {

    private LlmSdkWrapper llmSdkWrapper;

    @Override
    protected Object doExecute(SkillRequest request) {
        String operation = request.getOperation();
        switch (operation) {
            case "chat":
                return chat(request);
            case "complete":
                return complete(request);
            case "embed":
                return embed(request);
            case "translate":
                return translate(request);
            case "summarize":
                return summarize(request);
            default:
                throw new UnsupportedOperationException("Unsupported operation: " + operation);
        }
    }

    @Override
    protected String getResourceType() {
        return "llm";
    }

    private Object chat(SkillRequest request) {
        Map<String, Object> params = (Map<String, Object>) request.getParameters();
        String model = (String) params.get("model");
        List<Map<String, Object>> messages = (List<Map<String, Object>>) params.get("messages");
        Map<String, Object> options = params.containsKey("options") ? (Map<String, Object>) params.get("options") : null;
        
        Map<String, Object> response = llmSdkWrapper.chat(model, messages, options);
        Map<String, Object> result = new HashMap<>();
        result.put("model", model);
        result.put("response", response);
        result.put("tokens", response.containsKey("tokens") ? response.get("tokens") : null);
        return result;
    }

    private Object complete(SkillRequest request) {
        Map<String, Object> params = (Map<String, Object>) request.getParameters();
        String model = (String) params.get("model");
        String prompt = (String) params.get("prompt");
        Map<String, Object> options = params.containsKey("options") ? (Map<String, Object>) params.get("options") : null;
        
        String completion = llmSdkWrapper.complete(model, prompt, options);
        Map<String, Object> result = new HashMap<>();
        result.put("model", model);
        result.put("prompt", prompt);
        result.put("completion", completion);
        return result;
    }

    private Object embed(SkillRequest request) {
        Map<String, Object> params = (Map<String, Object>) request.getParameters();
        String model = (String) params.get("model");
        List<String> texts = (List<String>) params.get("texts");
        
        List<double[]> embeddings = llmSdkWrapper.embed(model, texts);
        Map<String, Object> result = new HashMap<>();
        result.put("model", model);
        result.put("embeddings", embeddings);
        result.put("count", embeddings.size());
        return result;
    }

    private Object translate(SkillRequest request) {
        Map<String, Object> params = (Map<String, Object>) request.getParameters();
        String model = (String) params.get("model");
        String text = (String) params.get("text");
        String targetLanguage = (String) params.get("targetLanguage");
        String sourceLanguage = params.containsKey("sourceLanguage") ? (String) params.get("sourceLanguage") : null;
        
        String translation = llmSdkWrapper.translate(model, text, targetLanguage, sourceLanguage);
        Map<String, Object> result = new HashMap<>();
        result.put("model", model);
        result.put("text", text);
        result.put("translation", translation);
        result.put("targetLanguage", targetLanguage);
        return result;
    }

    private Object summarize(SkillRequest request) {
        Map<String, Object> params = (Map<String, Object>) request.getParameters();
        String model = (String) params.get("model");
        String text = (String) params.get("text");
        int maxLength = params.containsKey("maxLength") ? ((Number) params.get("maxLength")).intValue() : 150;
        
        String summary = llmSdkWrapper.summarize(model, text, maxLength);
        Map<String, Object> result = new HashMap<>();
        result.put("model", model);
        result.put("originalLength", text.length());
        result.put("summary", summary);
        result.put("summaryLength", summary.length());
        return result;
    }

    @Override
    protected String getSkillId() {
        return "skill-llm";
    }
}

/**
 * LLM SDK包装器
 *
 * <p>包装底层SDK语言模型能力，提供统一接口</p>
 */
class LlmSdkWrapper {

    public Map<String, Object> chat(String model, List<Map<String, Object>> messages, Map<String, Object> options) {
        Map<String, Object> result = new HashMap<>();
        result.put("content", "Hello! How can I help you today?");
        result.put("role", "assistant");
        
        Map<String, Object> tokens = new HashMap<>();
        tokens.put("prompt", 10);
        tokens.put("completion", 5);
        result.put("tokens", tokens);
        
        return result;
    }

    public String complete(String model, String prompt, Map<String, Object> options) {
        return "This is a sample completion for the prompt: " + prompt;
    }

    public List<double[]> embed(String model, List<String> texts) {
        List<double[]> result = new ArrayList<>();
        result.add(new double[]{0.1, 0.2, 0.3});
        result.add(new double[]{0.4, 0.5, 0.6});
        return result;
    }

    public String translate(String model, String text, String targetLanguage, String sourceLanguage) {
        return "[Translated to " + targetLanguage + "] " + text;
    }

    public String summarize(String model, String text, int maxLength) {
        return "Summary of the text: " + text.substring(0, Math.min(text.length(), maxLength)) + "...";
    }
}
