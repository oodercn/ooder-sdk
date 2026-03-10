package net.ooder.scene.llm.config.layered;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * LLM 配置属性类
 *
 * @author ooder
 * @since 2.4
 */
public class LlmConfigProperties {

    private String provider;
    private String model;
    private String apiKey;
    private String baseUrl;
    private Double temperature;
    private Integer maxTokens;
    private Integer timeout;
    private Integer retryCount;
    private List<ProviderConfig> providers = new ArrayList<>();
    private Map<String, FunctionConfig> functions = new LinkedHashMap<>();
    private PromptConfig prompts;
    private List<RuleConfig> rules = new ArrayList<>();

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public List<ProviderConfig> getProviders() {
        return providers;
    }

    public void setProviders(List<ProviderConfig> providers) {
        this.providers = providers;
    }

    public Map<String, FunctionConfig> getFunctions() {
        return functions;
    }

    public void setFunctions(Map<String, FunctionConfig> functions) {
        this.functions = functions;
    }

    public PromptConfig getPrompts() {
        return prompts;
    }

    public void setPrompts(PromptConfig prompts) {
        this.prompts = prompts;
    }

    public List<RuleConfig> getRules() {
        return rules;
    }

    public void setRules(List<RuleConfig> rules) {
        this.rules = rules;
    }

    public boolean hasFunctions() {
        return functions != null && !functions.isEmpty();
    }

    public boolean hasPrompts() {
        return prompts != null;
    }

    public boolean hasRules() {
        return rules != null && !rules.isEmpty();
    }

    /**
     * Provider 配置
     */
    public static class ProviderConfig {
        private String id;
        private String type;
        private String apiKey;
        private String baseUrl;
        private Map<String, Object> config;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
        public Map<String, Object> getConfig() { return config; }
        public void setConfig(Map<String, Object> config) { this.config = config; }
    }

    /**
     * Function 配置
     */
    public static class FunctionConfig {
        private String name;
        private String description;
        private String capability;
        private Map<String, ParameterConfig> parameters = new LinkedHashMap<>();
        private List<String> required = new ArrayList<>();

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getCapability() { return capability; }
        public void setCapability(String capability) { this.capability = capability; }
        public Map<String, ParameterConfig> getParameters() { return parameters; }
        public void setParameters(Map<String, ParameterConfig> parameters) { this.parameters = parameters; }
        public List<String> getRequired() { return required; }
        public void setRequired(List<String> required) { this.required = required; }
    }

    /**
     * Parameter 配置
     */
    public static class ParameterConfig {
        private String type;
        private String description;
        private String defaultValue;
        private List<String> enumValues;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getDefaultValue() { return defaultValue; }
        public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }
        public List<String> getEnumValues() { return enumValues; }
        public void setEnumValues(List<String> enumValues) { this.enumValues = enumValues; }
    }

    /**
     * Prompt 配置
     */
    public static class PromptConfig {
        private String system;
        private String user;
        private Map<String, String> templates = new LinkedHashMap<>();

        public String getSystem() { return system; }
        public void setSystem(String system) { this.system = system; }
        public String getUser() { return user; }
        public void setUser(String user) { this.user = user; }
        public Map<String, String> getTemplates() { return templates; }
        public void setTemplates(Map<String, String> templates) { this.templates = templates; }
    }

    /**
     * Rule 配置
     */
    public static class RuleConfig {
        private String id;
        private String type;
        private String condition;
        private String action;
        private int priority;
        private boolean enabled = true;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getCondition() { return condition; }
        public void setCondition(String condition) { this.condition = condition; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }
}
