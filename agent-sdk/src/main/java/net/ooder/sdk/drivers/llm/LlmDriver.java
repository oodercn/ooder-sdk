package net.ooder.sdk.drivers.llm;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface LlmDriver {
    
    void init(LlmConfig config);
    
    CompletableFuture<ChatResponse> chat(ChatRequest request);
    
    CompletableFuture<ChatResponse> chatStream(ChatRequest request, ChatStreamHandler handler);
    
    CompletableFuture<EmbeddingResponse> embed(EmbeddingRequest request);
    
    CompletableFuture<CompletionResponse> complete(CompletionRequest request);
    
    CompletableFuture<TokenCountResponse> countTokens(String text);
    
    CompletableFuture<List<String>> listModels();
    
    CompletableFuture<ModelInfo> getModelInfo(String modelId);
    
    boolean supportsStreaming();
    
    boolean supportsEmbeddings();
    
    boolean supportsFunctionCalling();
    
    int getMaxContextLength(String modelId);
    
    void close();
    
    boolean isConnected();
    
    String getDriverName();
    
    String getDriverVersion();
    
    class LlmConfig {
        private String endpoint;
        private String apiKey;
        private String model;
        private String defaultModel;
        private int maxTokens = 4096;
        private double temperature = 0.7;
        private double topP = 1.0;
        private int timeout = 60000;
        private int maxRetries = 3;
        private boolean simulationMode = false;
        private Map<String, Object> properties = new java.util.concurrent.ConcurrentHashMap<>();
        
        public String getEndpoint() { return endpoint; }
        public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
        
        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        
        public String getDefaultModel() { return defaultModel; }
        public void setDefaultModel(String defaultModel) { this.defaultModel = defaultModel; }
        
        public int getMaxTokens() { return maxTokens; }
        public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }
        
        public double getTemperature() { return temperature; }
        public void setTemperature(double temperature) { this.temperature = temperature; }
        
        public double getTopP() { return topP; }
        public void setTopP(double topP) { this.topP = topP; }
        
        public int getTimeout() { return timeout; }
        public void setTimeout(int timeout) { this.timeout = timeout; }
        
        public int getMaxRetries() { return maxRetries; }
        public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }
        
        public boolean isSimulationMode() { return simulationMode; }
        public void setSimulationMode(boolean simulationMode) { this.simulationMode = simulationMode; }
        
        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }
    }
    
    class ChatRequest {
        private String model;
        private List<ChatMessage> messages;
        private double temperature = 0.7;
        private double topP = 1.0;
        private int maxTokens = 4096;
        private List<ToolDefinition> tools;
        private Object toolChoice;
        private Map<String, Object> metadata;
        
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        
        public List<ChatMessage> getMessages() { return messages; }
        public void setMessages(List<ChatMessage> messages) { this.messages = messages; }
        
        public double getTemperature() { return temperature; }
        public void setTemperature(double temperature) { this.temperature = temperature; }
        
        public double getTopP() { return topP; }
        public void setTopP(double topP) { this.topP = topP; }
        
        public int getMaxTokens() { return maxTokens; }
        public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }
        
        public List<ToolDefinition> getTools() { return tools; }
        public void setTools(List<ToolDefinition> tools) { this.tools = tools; }
        
        public Object getToolChoice() { return toolChoice; }
        public void setToolChoice(Object toolChoice) { this.toolChoice = toolChoice; }
        
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
        
        public static ChatRequest create(String model, List<ChatMessage> messages) {
            ChatRequest request = new ChatRequest();
            request.setModel(model);
            request.setMessages(messages);
            return request;
        }
    }
    
    class ChatResponse {
        private String id;
        private String model;
        private ChatMessage message;
        private UsageInfo usage;
        private String finishReason;
        private long createdTime;
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        
        public ChatMessage getMessage() { return message; }
        public void setMessage(ChatMessage message) { this.message = message; }
        
        public UsageInfo getUsage() { return usage; }
        public void setUsage(UsageInfo usage) { this.usage = usage; }
        
        public String getFinishReason() { return finishReason; }
        public void setFinishReason(String finishReason) { this.finishReason = finishReason; }
        
        public long getCreatedTime() { return createdTime; }
        public void setCreatedTime(long createdTime) { this.createdTime = createdTime; }
    }
    
    class ChatMessage {
        private String role;
        private String content;
        private String name;
        private List<ToolCall> toolCalls;
        private ToolCall toolCall;
        
        public ChatMessage() {}
        
        public ChatMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public List<ToolCall> getToolCalls() { return toolCalls; }
        public void setToolCalls(List<ToolCall> toolCalls) { this.toolCalls = toolCalls; }
        
        public ToolCall getToolCall() { return toolCall; }
        public void setToolCall(ToolCall toolCall) { this.toolCall = toolCall; }
        
        public static ChatMessage system(String content) {
            return new ChatMessage("system", content);
        }
        
        public static ChatMessage user(String content) {
            return new ChatMessage("user", content);
        }
        
        public static ChatMessage assistant(String content) {
            return new ChatMessage("assistant", content);
        }
    }
    
    class ToolCall {
        private String id;
        private String type;
        private FunctionCall function;
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public FunctionCall getFunction() { return function; }
        public void setFunction(FunctionCall function) { this.function = function; }
    }
    
    class FunctionCall {
        private String name;
        private String arguments;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getArguments() { return arguments; }
        public void setArguments(String arguments) { this.arguments = arguments; }
    }
    
    class ToolDefinition {
        private String type;
        private FunctionDefinition function;
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public FunctionDefinition getFunction() { return function; }
        public void setFunction(FunctionDefinition function) { this.function = function; }
    }
    
    class FunctionDefinition {
        private String name;
        private String description;
        private Map<String, Object> parameters;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Map<String, Object> getParameters() { return parameters; }
        public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
    }
    
    class EmbeddingRequest {
        private String model;
        private List<String> input;
        private String encodingFormat;
        
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        
        public List<String> getInput() { return input; }
        public void setInput(List<String> input) { this.input = input; }
        
        public String getEncodingFormat() { return encodingFormat; }
        public void setEncodingFormat(String encodingFormat) { this.encodingFormat = encodingFormat; }
    }
    
    class EmbeddingResponse {
        private String model;
        private List<EmbeddingData> data;
        private UsageInfo usage;
        
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        
        public List<EmbeddingData> getData() { return data; }
        public void setData(List<EmbeddingData> data) { this.data = data; }
        
        public UsageInfo getUsage() { return usage; }
        public void setUsage(UsageInfo usage) { this.usage = usage; }
    }
    
    class EmbeddingData {
        private int index;
        private float[] embedding;
        
        public int getIndex() { return index; }
        public void setIndex(int index) { this.index = index; }
        
        public float[] getEmbedding() { return embedding; }
        public void setEmbedding(float[] embedding) { this.embedding = embedding; }
    }
    
    class CompletionRequest {
        private String model;
        private String prompt;
        private double temperature = 0.7;
        private int maxTokens = 256;
        
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        
        public String getPrompt() { return prompt; }
        public void setPrompt(String prompt) { this.prompt = prompt; }
        
        public double getTemperature() { return temperature; }
        public void setTemperature(double temperature) { this.temperature = temperature; }
        
        public int getMaxTokens() { return maxTokens; }
        public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }
    }
    
    class CompletionResponse {
        private String id;
        private String model;
        private List<CompletionChoice> choices;
        private UsageInfo usage;
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        
        public List<CompletionChoice> getChoices() { return choices; }
        public void setChoices(List<CompletionChoice> choices) { this.choices = choices; }
        
        public UsageInfo getUsage() { return usage; }
        public void setUsage(UsageInfo usage) { this.usage = usage; }
    }
    
    class CompletionChoice {
        private String text;
        private int index;
        private String finishReason;
        
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        
        public int getIndex() { return index; }
        public void setIndex(int index) { this.index = index; }
        
        public String getFinishReason() { return finishReason; }
        public void setFinishReason(String finishReason) { this.finishReason = finishReason; }
    }
    
    class TokenCountResponse {
        private int tokenCount;
        private List<TokenInfo> tokens;
        
        public int getTokenCount() { return tokenCount; }
        public void setTokenCount(int tokenCount) { this.tokenCount = tokenCount; }
        
        public List<TokenInfo> getTokens() { return tokens; }
        public void setTokens(List<TokenInfo> tokens) { this.tokens = tokens; }
    }
    
    class TokenInfo {
        private String token;
        private int id;
        private double probability;
        
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        
        public double getProbability() { return probability; }
        public void setProbability(double probability) { this.probability = probability; }
    }
    
    class UsageInfo {
        private int promptTokens;
        private int completionTokens;
        private int totalTokens;
        
        public int getPromptTokens() { return promptTokens; }
        public void setPromptTokens(int promptTokens) { this.promptTokens = promptTokens; }
        
        public int getCompletionTokens() { return completionTokens; }
        public void setCompletionTokens(int completionTokens) { this.completionTokens = completionTokens; }
        
        public int getTotalTokens() { return totalTokens; }
        public void setTotalTokens(int totalTokens) { this.totalTokens = totalTokens; }
    }
    
    class ModelInfo {
        private String id;
        private String name;
        private String provider;
        private int contextLength;
        private boolean supportsStreaming;
        private boolean supportsEmbeddings;
        private boolean supportsFunctionCalling;
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getProvider() { return provider; }
        public void setProvider(String provider) { this.provider = provider; }
        
        public int getContextLength() { return contextLength; }
        public void setContextLength(int contextLength) { this.contextLength = contextLength; }
        
        public boolean isSupportsStreaming() { return supportsStreaming; }
        public void setSupportsStreaming(boolean supportsStreaming) { this.supportsStreaming = supportsStreaming; }
        
        public boolean isSupportsEmbeddings() { return supportsEmbeddings; }
        public void setSupportsEmbeddings(boolean supportsEmbeddings) { this.supportsEmbeddings = supportsEmbeddings; }
        
        public boolean isSupportsFunctionCalling() { return supportsFunctionCalling; }
        public void setSupportsFunctionCalling(boolean supportsFunctionCalling) { this.supportsFunctionCalling = supportsFunctionCalling; }
    }
    
    interface ChatStreamHandler {
        void onToken(String token);
        void onMessage(ChatMessage message);
        void onComplete(ChatResponse response);
        void onError(Throwable error);
    }
}
