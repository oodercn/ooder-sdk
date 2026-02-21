package net.ooder.scene.skill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mock LLM Provider
 *
 * <p>默认的 Mock 实现，用于测试和开发</p>
 */
public class MockLlmProvider implements LlmProvider {

    @Override
    public String getProviderType() {
        return "mock";
    }

    @Override
    public List<String> getSupportedModels() {
        List<String> models = new ArrayList<String>();
        models.add("mock-gpt-4");
        models.add("mock-gpt-3.5-turbo");
        models.add("mock-embedding");
        return models;
    }

    @Override
    public Map<String, Object> chat(String model, List<Map<String, Object>> messages, Map<String, Object> options) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("content", "This is a mock response from " + model);
        result.put("role", "assistant");
        
        Map<String, Object> tokens = new HashMap<String, Object>();
        tokens.put("prompt", 10);
        tokens.put("completion", 5);
        result.put("tokens", tokens);
        
        return result;
    }

    @Override
    public String complete(String model, String prompt, Map<String, Object> options) {
        return "Mock completion for: " + prompt.substring(0, Math.min(prompt.length(), 50)) + "...";
    }

    @Override
    public List<double[]> embed(String model, List<String> texts) {
        List<double[]> result = new ArrayList<double[]>();
        for (int i = 0; i < texts.size(); i++) {
            result.add(new double[]{0.1 * i, 0.2 * i, 0.3 * i, 0.4 * i, 0.5 * i});
        }
        return result;
    }

    @Override
    public String translate(String model, String text, String targetLanguage, String sourceLanguage) {
        return "[Mock translation to " + targetLanguage + "]: " + text;
    }

    @Override
    public String summarize(String model, String text, int maxLength) {
        return "Mock summary: " + text.substring(0, Math.min(text.length(), maxLength)) + "...";
    }
}
