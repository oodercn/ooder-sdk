package net.ooder.sdk.llm.nlp.model;

import java.util.List;
import java.util.Map;

public class NlpResponse {
    private String sessionId;
    private String text;
    private String outputType;
    private List<String> suggestions;
    private Map<String, Object> visualizations;
    private Double confidence;

    public NlpResponse() {
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getOutputType() {
        return outputType;
    }

    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public Map<String, Object> getVisualizations() {
        return visualizations;
    }

    public void setVisualizations(Map<String, Object> visualizations) {
        this.visualizations = visualizations;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }
}
