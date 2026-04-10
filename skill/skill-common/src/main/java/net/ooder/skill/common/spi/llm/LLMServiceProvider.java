package net.ooder.skill.common.spi.llm;

import java.util.List;
import java.util.Map;

public interface LLMServiceProvider {
    
    String generate(String prompt);
    
    String generate(String prompt, Map<String, Object> options);
    
    String generateWithSystem(String systemPrompt, String userPrompt);
    
    String generateWithSystem(String systemPrompt, String userPrompt, Map<String, Object> options);
    
    List<String> generateBatch(List<String> prompts);
    
    int getMaxTokens();
    
    String getModelName();
    
    boolean isAvailable();
}
