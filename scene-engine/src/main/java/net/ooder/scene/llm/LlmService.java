package net.ooder.scene.llm;

import net.ooder.scene.skill.llm.StreamHandler;
import net.ooder.sdk.llm.tool.ChatResponse;

import java.util.List;
import java.util.Map;

public interface LlmService {

    ChatResponse chat(SceneChatRequest request);

    void chatStream(SceneChatRequest request, StreamHandler handler);

    String complete(String prompt, int maxTokens);

    List<ProviderInfo> getProviders();

    List<ModelInfo> getModels(String providerId);

    void setActiveProvider(String providerId);

    void setActiveModel(String providerId, String modelId);

    String getActiveProvider();

    String getActiveModel();

    void registerFunction(String functionId, FunctionConfig functionConfig);

    void unregisterFunction(String functionId);

    Map<String, FunctionConfig> getRegisteredFunctions();

    class ProviderInfo {
        private String id;
        private String name;
        private String type;
        private List<ModelInfo> models;

        public ProviderInfo() {}

        public ProviderInfo(String id, String name, String type) {
            this.id = id;
            this.name = name;
            this.type = type;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public List<ModelInfo> getModels() { return models; }
        public void setModels(List<ModelInfo> models) { this.models = models; }
    }

    class ModelInfo {
        private String id;
        private String name;
        private int contextLength;
        private boolean supportsFunctionCall;
        private boolean supportsVision;

        public ModelInfo() {}

        public ModelInfo(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getContextLength() { return contextLength; }
        public void setContextLength(int contextLength) { this.contextLength = contextLength; }
        public boolean isSupportsFunctionCall() { return supportsFunctionCall; }
        public void setSupportsFunctionCall(boolean supportsFunctionCall) { this.supportsFunctionCall = supportsFunctionCall; }
        public boolean isSupportsVision() { return supportsVision; }
        public void setSupportsVision(boolean supportsVision) { this.supportsVision = supportsVision; }
    }

    class FunctionConfig {
        private String name;
        private String description;
        private Map<String, Object> parameters;
        private List<String> required;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Map<String, Object> getParameters() { return parameters; }
        public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
        public List<String> getRequired() { return required; }
        public void setRequired(List<String> required) { this.required = required; }
    }
}
