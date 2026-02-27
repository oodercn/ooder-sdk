package net.ooder.sdk.llm.nlp.model;

import net.ooder.sdk.llm.common.enums.InputType;

import java.util.Map;

public class NlpInput {
    private String sessionId;
    private InputType inputType;
    private String content;
    private String language;
    private Map<String, Object> context;

    public NlpInput() {
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public InputType getInputType() {
        return inputType;
    }

    public void setInputType(InputType inputType) {
        this.inputType = inputType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }
}
