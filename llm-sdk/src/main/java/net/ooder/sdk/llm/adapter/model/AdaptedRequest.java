package net.ooder.sdk.llm.adapter.model;

import java.util.Map;

public class AdaptedRequest {
    private String requestId;
    private String providerId;
    private String modelId;
    private String adaptedPrompt;
    private Map<String, Object> adaptedParameters;
    private String adaptedFormat;
    private Map<String, String> headers;

    public AdaptedRequest() {
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getAdaptedPrompt() {
        return adaptedPrompt;
    }

    public void setAdaptedPrompt(String adaptedPrompt) {
        this.adaptedPrompt = adaptedPrompt;
    }

    public Map<String, Object> getAdaptedParameters() {
        return adaptedParameters;
    }

    public void setAdaptedParameters(Map<String, Object> adaptedParameters) {
        this.adaptedParameters = adaptedParameters;
    }

    public String getAdaptedFormat() {
        return adaptedFormat;
    }

    public void setAdaptedFormat(String adaptedFormat) {
        this.adaptedFormat = adaptedFormat;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
