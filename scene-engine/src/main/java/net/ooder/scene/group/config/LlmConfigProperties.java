package net.ooder.scene.group.config;

import java.util.HashMap;
import java.util.Map;

public class LlmConfigProperties {
    
    private String provider;
    private String model;
    private Double temperature;
    private Integer maxTokens;
    private Double topP;
    private Double frequencyPenalty;
    private Double presencePenalty;
    private Map<String, Object> additionalConfig = new HashMap<>();
    
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    public Integer getMaxTokens() { return maxTokens; }
    public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }
    public Double getTopP() { return topP; }
    public void setTopP(Double topP) { this.topP = topP; }
    public Double getFrequencyPenalty() { return frequencyPenalty; }
    public void setFrequencyPenalty(Double frequencyPenalty) { this.frequencyPenalty = frequencyPenalty; }
    public Double getPresencePenalty() { return presencePenalty; }
    public void setPresencePenalty(Double presencePenalty) { this.presencePenalty = presencePenalty; }
    public Map<String, Object> getAdditionalConfig() { return additionalConfig; }
    public void setAdditionalConfig(Map<String, Object> additionalConfig) { this.additionalConfig = additionalConfig; }
    
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        if (provider != null) map.put("provider", provider);
        if (model != null) map.put("model", model);
        if (temperature != null) map.put("temperature", temperature);
        if (maxTokens != null) map.put("maxTokens", maxTokens);
        if (topP != null) map.put("topP", topP);
        if (frequencyPenalty != null) map.put("frequencyPenalty", frequencyPenalty);
        if (presencePenalty != null) map.put("presencePenalty", presencePenalty);
        if (additionalConfig != null) map.putAll(additionalConfig);
        return map;
    }
    
    public static LlmConfigProperties fromMap(Map<String, Object> map) {
        if (map == null) return null;
        
        LlmConfigProperties props = new LlmConfigProperties();
        props.setProvider((String) map.get("provider"));
        props.setModel((String) map.get("model"));
        
        Object temp = map.get("temperature");
        if (temp instanceof Number) props.setTemperature(((Number) temp).doubleValue());
        
        Object maxTokens = map.get("maxTokens");
        if (maxTokens instanceof Number) props.setMaxTokens(((Number) maxTokens).intValue());
        
        Object topP = map.get("topP");
        if (topP instanceof Number) props.setTopP(((Number) topP).doubleValue());
        
        Object freqPenalty = map.get("frequencyPenalty");
        if (freqPenalty instanceof Number) props.setFrequencyPenalty(((Number) freqPenalty).doubleValue());
        
        Object presPenalty = map.get("presencePenalty");
        if (presPenalty instanceof Number) props.setPresencePenalty(((Number) presPenalty).doubleValue());
        
        Map<String, Object> additional = new HashMap<>(map);
        additional.remove("provider");
        additional.remove("model");
        additional.remove("temperature");
        additional.remove("maxTokens");
        additional.remove("topP");
        additional.remove("frequencyPenalty");
        additional.remove("presencePenalty");
        props.setAdditionalConfig(additional);
        
        return props;
    }
}
