package net.ooder.scene.core.decision;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 决策结果
 * 
 * <p>封装决策引擎的决策结果</p>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class DecisionResult {

    private boolean success;
    private String capability;
    private String capId;
    private Map<String, Object> params;
    private boolean fromLlm;
    private float confidence;
    private String errorMessage;
    private DecisionSource source;
    private Map<String, Object> metadata;

    public enum DecisionSource {
        LLM,
        RULE_ENGINE,
        CACHE,
        DEFAULT
    }

    public DecisionResult() {
        this.params = new HashMap<>();
        this.metadata = new HashMap<>();
        this.confidence = 0.0f;
        this.fromLlm = false;
        this.success = true;
    }

    public static DecisionResult success(String capability, Map<String, Object> params) {
        DecisionResult result = new DecisionResult();
        result.setSuccess(true);
        result.setCapability(capability);
        result.setParams(params);
        return result;
    }

    public static DecisionResult success(String capability, String capId, Map<String, Object> params) {
        DecisionResult result = success(capability, params);
        result.setCapId(capId);
        return result;
    }

    public static DecisionResult failure(String errorMessage) {
        DecisionResult result = new DecisionResult();
        result.setSuccess(false);
        result.setErrorMessage(errorMessage);
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public DecisionResult setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public String getCapability() {
        return capability;
    }

    public DecisionResult setCapability(String capability) {
        this.capability = capability;
        return this;
    }

    public String getCapId() {
        return capId;
    }

    public DecisionResult setCapId(String capId) {
        this.capId = capId;
        return this;
    }

    public Map<String, Object> getParams() {
        return params != null ? params : Collections.emptyMap();
    }

    public DecisionResult setParams(Map<String, Object> params) {
        this.params = params != null ? params : new HashMap<>();
        return this;
    }

    public DecisionResult addParam(String key, Object value) {
        if (this.params == null) {
            this.params = new HashMap<>();
        }
        this.params.put(key, value);
        return this;
    }

    public Object getParam(String key) {
        return params != null ? params.get(key) : null;
    }

    public boolean isFromLlm() {
        return fromLlm;
    }

    public DecisionResult setFromLlm(boolean fromLlm) {
        this.fromLlm = fromLlm;
        this.source = fromLlm ? DecisionSource.LLM : DecisionSource.RULE_ENGINE;
        return this;
    }

    public float getConfidence() {
        return confidence;
    }

    public DecisionResult setConfidence(float confidence) {
        this.confidence = confidence;
        return this;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public DecisionResult setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public DecisionSource getSource() {
        return source;
    }

    public DecisionResult setSource(DecisionSource source) {
        this.source = source;
        return this;
    }

    public Map<String, Object> getMetadata() {
        return metadata != null ? metadata : Collections.emptyMap();
    }

    public DecisionResult setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata != null ? metadata : new HashMap<>();
        return this;
    }

    public DecisionResult addMetadata(String key, Object value) {
        if (this.metadata == null) {
            this.metadata = new HashMap<>();
        }
        this.metadata.put(key, value);
        return this;
    }

    public boolean hasCapability() {
        return capability != null && !capability.trim().isEmpty();
    }

    public boolean hasCapId() {
        return capId != null && !capId.trim().isEmpty();
    }

    public boolean hasParams() {
        return params != null && !params.isEmpty();
    }

    public boolean isHighConfidence() {
        return confidence >= 0.8f;
    }

    public boolean isLowConfidence() {
        return confidence > 0 && confidence < 0.5f;
    }

    @Override
    public String toString() {
        return "DecisionResult{" +
            "success=" + success +
            ", capability='" + capability + '\'' +
            ", capId='" + capId + '\'' +
            ", fromLlm=" + fromLlm +
            ", confidence=" + confidence +
            ", source=" + source +
            '}';
    }
}
