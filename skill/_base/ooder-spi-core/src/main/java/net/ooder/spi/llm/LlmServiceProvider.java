package net.ooder.spi.llm;

import java.util.List;
import java.util.Map;

public interface LlmServiceProvider {
    
    String generate(String prompt);
    
    String generate(String prompt, Map<String, Object> options);
    
    String generateWithSystem(String systemPrompt, String userPrompt);
    
    String generateWithSystem(String systemPrompt, String userPrompt, Map<String, Object> options);
    
    List<String> generateBatch(List<String> prompts);
    
    int getMaxTokens();
    
    String getModelName();
    
    boolean isAvailable();
}
