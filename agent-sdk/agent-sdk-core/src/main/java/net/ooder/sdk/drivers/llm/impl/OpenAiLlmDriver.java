package net.ooder.sdk.drivers.llm.impl;

import net.ooder.sdk.api.driver.annotation.DriverImplementation;
import net.ooder.sdk.drivers.llm.LlmDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@DriverImplementation(value = "LlmDriver", skillId = "skill-llm-openai")
public class OpenAiLlmDriver implements LlmDriver {
    
    private static final Logger log = LoggerFactory.getLogger(OpenAiLlmDriver.class);
    
    private static final String DEFAULT_ENDPOINT = "https://api.openai.com/v1";
    private static final String CHAT_ENDPOINT = "/chat/completions";
    private static final String EMBEDDING_ENDPOINT = "/embeddings";
    private static final String COMPLETION_ENDPOINT = "/completions";
    
    private LlmConfig config;
    private String apiKey;
    private String endpoint;
    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final AtomicLong requestIdCounter = new AtomicLong(0);
    
    private final Map<String, ModelInfo> models = new ConcurrentHashMap<>();
    private boolean simulationMode = false;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    @Override
    public void init(LlmConfig config) {
        this.config = config;
        
        if (config != null) {
            this.apiKey = config.getApiKey();
            this.endpoint = config.getEndpoint() != null ? config.getEndpoint() : DEFAULT_ENDPOINT;
            this.simulationMode = config.isSimulationMode();
        } else {
            this.endpoint = DEFAULT_ENDPOINT;
            this.simulationMode = true;
        }
        
        registerModel("gpt-4", "GPT-4", 8192, true, false, true);
        registerModel("gpt-4-turbo", "GPT-4 Turbo", 128000, true, false, true);
        registerModel("gpt-4o", "GPT-4o", 128000, true, false, true);
        registerModel("gpt-4o-mini", "GPT-4o Mini", 128000, true, false, true);
        registerModel("gpt-3.5-turbo", "GPT-3.5 Turbo", 16384, true, false, true);
        registerModel("text-embedding-ada-002", "Ada Embeddings", 8191, false, true, false);
        registerModel("text-embedding-3-small", "Embeddings v3 Small", 8191, false, true, false);
        registerModel("text-embedding-3-large", "Embeddings v3 Large", 8191, false, true, false);
        
        connected.set(true);
        
        if (simulationMode || apiKey == null || apiKey.isEmpty()) {
            this.simulationMode = true;
            log.warn("OpenAI LLM driver initialized in SIMULATION mode (no API key configured)");
        } else {
            log.info("OpenAI LLM driver initialized with endpoint: {}", endpoint);
        }
    }
    
    private void registerModel(String id, String name, int contextLength, 
                               boolean streaming, boolean embeddings, boolean functionCalling) {
        ModelInfo info = new ModelInfo();
        info.setId(id);
        info.setName(name);
        info.setProvider("openai");
        info.setContextLength(contextLength);
        info.setSupportsStreaming(streaming);
        info.setSupportsEmbeddings(embeddings);
        info.setSupportsFunctionCalling(functionCalling);
        models.put(id, info);
    }
    
    @Override
    public CompletableFuture<ChatResponse> chat(ChatRequest request) {
        if (simulationMode) {
            return chatSimulated(request);
        }
        return chatWithApi(request);
    }
    
    private CompletableFuture<ChatResponse> chatWithApi(ChatRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            String model = request.getModel() != null ? request.getModel() : 
                (config != null && config.getDefaultModel() != null ? config.getDefaultModel() : "gpt-3.5-turbo");
            
            try {
                String requestBody = buildChatRequestBody(request, model, false);
                String response = sendPostRequest(endpoint + CHAT_ENDPOINT, requestBody);
                return parseChatResponse(response, model);
            } catch (Exception e) {
                log.error("OpenAI chat request failed", e);
                return createErrorResponse("Request failed: " + e.getMessage());
            }
        }, executor);
    }
    
    private CompletableFuture<ChatResponse> chatSimulated(ChatRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            String model = request.getModel() != null ? request.getModel() : 
                (config != null && config.getDefaultModel() != null ? config.getDefaultModel() : "gpt-3.5-turbo");
            
            String responseContent = simulateOpenAiResponse(request);
            
            ChatMessage assistantMessage = new ChatMessage("assistant", responseContent);
            
            ChatResponse response = new ChatResponse();
            response.setId("chatcmpl-" + UUID.randomUUID().toString().substring(0, 24));
            response.setModel(model);
            response.setMessage(assistantMessage);
            response.setFinishReason("stop");
            response.setCreatedTime(System.currentTimeMillis());
            
            UsageInfo usage = new UsageInfo();
            usage.setPromptTokens(estimateTokens(request.getMessages()));
            usage.setCompletionTokens(estimateTokens(responseContent));
            usage.setTotalTokens(usage.getPromptTokens() + usage.getCompletionTokens());
            response.setUsage(usage);
            
            return response;
        }, executor);
    }
    
    private String buildChatRequestBody(ChatRequest request, String model, boolean stream) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"model\":\"").append(model).append("\",");
        sb.append("\"stream\":").append(stream).append(",");
        sb.append("\"messages\":[");
        
        List<ChatMessage> messages = request.getMessages();
        if (messages != null) {
            for (int i = 0; i < messages.size(); i++) {
                ChatMessage msg = messages.get(i);
                if (i > 0) sb.append(",");
                sb.append("{\"role\":\"").append(msg.getRole()).append("\",");
                sb.append("\"content\":\"").append(escapeJson(msg.getContent())).append("\"}");
            }
        }
        
        sb.append("]");
        
        if (request.getTemperature() > 0) {
            sb.append(",\"temperature\":").append(request.getTemperature());
        }
        if (request.getMaxTokens() > 0) {
            sb.append(",\"max_tokens\":").append(request.getMaxTokens());
        }
        
        sb.append("}");
        return sb.toString();
    }
    
    private String sendPostRequest(String urlStr, String body) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        try {
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setDoOutput(true);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(120000);
            
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = body.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            int responseCode = conn.getResponseCode();
            
            if (responseCode == 200) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    return response.toString();
                }
            } else {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder error = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        error.append(line);
                    }
                    throw new IOException("API error " + responseCode + ": " + error.toString());
                }
            }
        } finally {
            conn.disconnect();
        }
    }
    
    private ChatResponse parseChatResponse(String responseBody, String model) {
        try {
            ChatResponse response = new ChatResponse();
            response.setModel(model);
            response.setCreatedTime(System.currentTimeMillis());
            
            String id = extractJsonValue(responseBody, "id");
            response.setId(id != null ? id : "chatcmpl-" + System.currentTimeMillis());
            
            String content = extractNestedJsonValue(responseBody, "choices", "0", "message", "content");
            ChatMessage assistantMessage = new ChatMessage("assistant", content != null ? content : "");
            response.setMessage(assistantMessage);
            
            String finishReason = extractNestedJsonValue(responseBody, "choices", "0", "finish_reason");
            response.setFinishReason(finishReason != null ? finishReason : "stop");
            
            UsageInfo usage = new UsageInfo();
            String promptTokens = extractNestedJsonValue(responseBody, "usage", "prompt_tokens");
            String completionTokens = extractNestedJsonValue(responseBody, "usage", "completion_tokens");
            String totalTokens = extractNestedJsonValue(responseBody, "usage", "total_tokens");
            
            usage.setPromptTokens(promptTokens != null ? Integer.parseInt(promptTokens) : 0);
            usage.setCompletionTokens(completionTokens != null ? Integer.parseInt(completionTokens) : 0);
            usage.setTotalTokens(totalTokens != null ? Integer.parseInt(totalTokens) : 0);
            response.setUsage(usage);
            
            log.debug("OpenAI chat completed: {}", response.getId());
            return response;
        } catch (Exception e) {
            log.error("Failed to parse OpenAI response", e);
            return createErrorResponse("Parse error: " + e.getMessage());
        }
    }
    
    @Override
    public CompletableFuture<ChatResponse> chatStream(ChatRequest request, ChatStreamHandler handler) {
        return CompletableFuture.supplyAsync(() -> {
            String model = request.getModel() != null ? request.getModel() : 
                (config != null && config.getDefaultModel() != null ? config.getDefaultModel() : "gpt-3.5-turbo");
            
            String fullResponse = simulateOpenAiResponse(request);
            
            String[] words = fullResponse.split(" ");
            StringBuilder currentContent = new StringBuilder();
            
            for (String word : words) {
                currentContent.append(word).append(" ");
                
                if (handler != null) {
                    handler.onToken(word + " ");
                }
                
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            ChatMessage assistantMessage = new ChatMessage("assistant", currentContent.toString().trim());
            
            if (handler != null) {
                handler.onMessage(assistantMessage);
            }
            
            ChatResponse response = new ChatResponse();
            response.setId("chatcmpl-" + UUID.randomUUID().toString().substring(0, 24));
            response.setModel(model);
            response.setMessage(assistantMessage);
            response.setFinishReason("stop");
            response.setCreatedTime(System.currentTimeMillis());
            
            UsageInfo usage = new UsageInfo();
            usage.setPromptTokens(estimateTokens(request.getMessages()));
            usage.setCompletionTokens(estimateTokens(currentContent.toString()));
            usage.setTotalTokens(usage.getPromptTokens() + usage.getCompletionTokens());
            response.setUsage(usage);
            
            if (handler != null) {
                handler.onComplete(response);
            }
            
            return response;
        }, executor);
    }
    
    @Override
    public CompletableFuture<EmbeddingResponse> embed(EmbeddingRequest request) {
        if (simulationMode) {
            return embedSimulated(request);
        }
        return embedWithApi(request);
    }
    
    private CompletableFuture<EmbeddingResponse> embedWithApi(EmbeddingRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            String model = request.getModel() != null ? request.getModel() : "text-embedding-ada-002";
            
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("{\"model\":\"").append(model).append("\",");
                sb.append("\"input\":[");
                
                List<String> inputs = request.getInput();
                for (int i = 0; i < inputs.size(); i++) {
                    if (i > 0) sb.append(",");
                    sb.append("\"").append(escapeJson(inputs.get(i))).append("\"");
                }
                
                sb.append("]}");
                
                String response = sendPostRequest(endpoint + EMBEDDING_ENDPOINT, sb.toString());
                return parseEmbeddingResponse(response, model);
            } catch (Exception e) {
                log.error("OpenAI embedding request failed", e);
                return createEmbeddingErrorResponse("Request failed: " + e.getMessage());
            }
        }, executor);
    }
    
    private CompletableFuture<EmbeddingResponse> embedSimulated(EmbeddingRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            String model = request.getModel() != null ? request.getModel() : "text-embedding-ada-002";
            int dimension = model.contains("large") ? 3072 : 1536;
            
            List<EmbeddingData> embeddings = new ArrayList<>();
            for (int i = 0; i < request.getInput().size(); i++) {
                float[] embedding = generateDeterministicEmbedding(request.getInput().get(i), dimension);
                
                EmbeddingData data = new EmbeddingData();
                data.setIndex(i);
                data.setEmbedding(embedding);
                embeddings.add(data);
            }
            
            EmbeddingResponse response = new EmbeddingResponse();
            response.setModel(model);
            response.setData(embeddings);
            
            UsageInfo usage = new UsageInfo();
            usage.setPromptTokens(request.getInput().stream().mapToInt(s -> s.split("\\s+").length).sum());
            usage.setTotalTokens(usage.getPromptTokens());
            response.setUsage(usage);
            
            return response;
        }, executor);
    }
    
    private EmbeddingResponse parseEmbeddingResponse(String responseBody, String model) {
        try {
            EmbeddingResponse response = new EmbeddingResponse();
            response.setModel(model);
            
            List<EmbeddingData> embeddings = new ArrayList<>();
            int index = 0;
            
            while (true) {
                String embeddingStr = extractNestedJsonValue(responseBody, "data", String.valueOf(index), "embedding");
                if (embeddingStr == null) break;
                
                float[] embedding = parseEmbeddingArray(embeddingStr);
                EmbeddingData data = new EmbeddingData();
                data.setIndex(index);
                data.setEmbedding(embedding);
                embeddings.add(data);
                index++;
            }
            
            response.setData(embeddings);
            
            UsageInfo usage = new UsageInfo();
            String totalTokens = extractNestedJsonValue(responseBody, "usage", "total_tokens");
            usage.setTotalTokens(totalTokens != null ? Integer.parseInt(totalTokens) : 0);
            response.setUsage(usage);
            
            log.debug("OpenAI embedding completed for {} inputs", embeddings.size());
            return response;
        } catch (Exception e) {
            log.error("Failed to parse embedding response", e);
            return createEmbeddingErrorResponse("Parse error: " + e.getMessage());
        }
    }
    
    private float[] parseEmbeddingArray(String embeddingStr) {
        if (embeddingStr == null || !embeddingStr.startsWith("[")) {
            return new float[0];
        }
        
        String[] parts = embeddingStr.substring(1, embeddingStr.length() - 1).split(",");
        float[] embedding = new float[parts.length];
        
        for (int i = 0; i < parts.length; i++) {
            try {
                embedding[i] = Float.parseFloat(parts[i].trim());
            } catch (NumberFormatException e) {
                embedding[i] = 0;
            }
        }
        
        return embedding;
    }
    
    @Override
    public CompletableFuture<CompletionResponse> complete(CompletionRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            String model = request.getModel() != null ? request.getModel() : "gpt-3.5-turbo-instruct";
            
            String completion = "OpenAI completion for: " + 
                request.getPrompt().substring(0, Math.min(50, request.getPrompt().length())) + "...";
            
            CompletionChoice choice = new CompletionChoice();
            choice.setText(completion);
            choice.setIndex(0);
            choice.setFinishReason("stop");
            
            CompletionResponse response = new CompletionResponse();
            response.setId("cmpl-" + UUID.randomUUID().toString().substring(0, 24));
            response.setModel(model);
            response.setChoices(Collections.singletonList(choice));
            
            UsageInfo usage = new UsageInfo();
            usage.setPromptTokens(estimateTokens(request.getPrompt()));
            usage.setCompletionTokens(estimateTokens(completion));
            usage.setTotalTokens(usage.getPromptTokens() + usage.getCompletionTokens());
            response.setUsage(usage);
            
            return response;
        }, executor);
    }
    
    @Override
    public CompletableFuture<TokenCountResponse> countTokens(String text) {
        return CompletableFuture.supplyAsync(() -> {
            int count = estimateTokens(text);
            TokenCountResponse response = new TokenCountResponse();
            response.setTokenCount(count);
            return response;
        }, executor);
    }
    
    @Override
    public CompletableFuture<List<String>> listModels() {
        return CompletableFuture.supplyAsync(() -> new ArrayList<>(models.keySet()), executor);
    }
    
    @Override
    public CompletableFuture<ModelInfo> getModelInfo(String modelId) {
        return CompletableFuture.supplyAsync(() -> models.get(modelId), executor);
    }
    
    @Override
    public boolean supportsStreaming() {
        return true;
    }
    
    @Override
    public boolean supportsEmbeddings() {
        return true;
    }
    
    @Override
    public boolean supportsFunctionCalling() {
        return true;
    }
    
    @Override
    public int getMaxContextLength(String modelId) {
        ModelInfo info = models.get(modelId);
        return info != null ? info.getContextLength() : 4096;
    }
    
    @Override
    public void close() {
        connected.set(false);
        executor.shutdown();
        log.info("OpenAI LLM driver closed");
    }
    
    @Override
    public boolean isConnected() {
        return connected.get();
    }
    
    @Override
    public String getDriverName() {
        return "OpenAI";
    }
    
    @Override
    public String getDriverVersion() {
        return "2.0.0";
    }
    
    public boolean isSimulationMode() {
        return simulationMode;
    }
    
    private String simulateOpenAiResponse(ChatRequest request) {
        if (request.getMessages() == null || request.getMessages().isEmpty()) {
            return "I don't have any input to respond to.";
        }
        
        ChatMessage lastMessage = request.getMessages().get(request.getMessages().size() - 1);
        String userContent = lastMessage.getContent() != null ? lastMessage.getContent().toLowerCase() : "";
        
        if (userContent.contains("hello") || userContent.contains("hi")) {
            return "Hello! I'm simulating an OpenAI GPT response. How can I assist you today?";
        } else if (userContent.contains("code") || userContent.contains("programming")) {
            return "I'd be happy to help with coding! As a simulated OpenAI response, " +
                   "I can demonstrate the expected response format. In production, " +
                   "this would connect to the actual OpenAI API.";
        } else if (userContent.contains("?")) {
            return "That's an interesting question. This is a simulated OpenAI response. " +
                   "In a real implementation, this would be processed by GPT-4 or GPT-3.5.";
        } else {
            return "I received your message. This is a simulated response from the OpenAI driver. " +
                   "The actual implementation would use the OpenAI API with your configured API key.";
        }
    }
    
    private float[] generateDeterministicEmbedding(String text, int dimension) {
        float[] embedding = new float[dimension];
        Random random = new Random(text != null ? text.hashCode() : System.currentTimeMillis());
        
        for (int i = 0; i < dimension; i++) {
            embedding[i] = (float) (random.nextGaussian() * 0.1);
        }
        
        float norm = 0;
        for (float v : embedding) {
            norm += v * v;
        }
        norm = (float) Math.sqrt(norm);
        
        if (norm > 0) {
            for (int i = 0; i < dimension; i++) {
                embedding[i] /= norm;
            }
        }
        
        return embedding;
    }
    
    private int estimateTokens(List<ChatMessage> messages) {
        if (messages == null) return 0;
        int total = 0;
        for (ChatMessage msg : messages) {
            if (msg.getContent() != null) {
                total += estimateTokens(msg.getContent());
            }
        }
        return total;
    }
    
    private int estimateTokens(String text) {
        if (text == null) return 0;
        return (int) (text.split("\\s+").length * 1.3);
    }
    
    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
    
    private String extractJsonValue(String json, String key) {
        if (json == null) return null;
        String pattern = "\"" + key + "\"\\s*:\\s*";
        int start = json.indexOf(pattern);
        if (start < 0) return null;
        
        start += pattern.length();
        char firstChar = json.charAt(start);
        
        if (firstChar == '"') {
            int end = json.indexOf('"', start + 1);
            if (end > start) {
                return json.substring(start + 1, end);
            }
        } else {
            int end = start;
            while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-' || json.charAt(end) == '.')) {
                end++;
            }
            return json.substring(start, end);
        }
        return null;
    }
    
    private String extractNestedJsonValue(String json, String... keys) {
        String current = json;
        for (int i = 0; i < keys.length - 1; i++) {
            String key = keys[i];
            if (key.matches("\\d+")) {
                int index = Integer.parseInt(key);
                current = extractArrayElement(current, index);
            } else {
                current = extractJsonObject(current, key);
            }
            if (current == null) return null;
        }
        return extractJsonValue(current, keys[keys.length - 1]);
    }
    
    private String extractJsonObject(String json, String key) {
        if (json == null) return null;
        String pattern = "\"" + key + "\"\\s*:\\s*\\{";
        int start = json.indexOf(pattern);
        if (start < 0) return null;
        
        start += pattern.length() - 1;
        int braceCount = 1;
        int end = start + 1;
        
        while (end < json.length() && braceCount > 0) {
            char c = json.charAt(end);
            if (c == '{') braceCount++;
            else if (c == '}') braceCount--;
            end++;
        }
        
        return json.substring(start, end);
    }
    
    private String extractArrayElement(String json, int index) {
        if (json == null || !json.startsWith("[")) return null;
        
        int start = 1;
        int currentIndex = 0;
        
        while (start < json.length() && currentIndex < index) {
            if (json.charAt(start) == '{') {
                int braceCount = 1;
                start++;
                while (start < json.length() && braceCount > 0) {
                    if (json.charAt(start) == '{') braceCount++;
                    else if (json.charAt(start) == '}') braceCount--;
                    start++;
                }
            } else if (json.charAt(start) == '"') {
                start++;
                while (start < json.length() && json.charAt(start) != '"') {
                    if (json.charAt(start) == '\\') start++;
                    start++;
                }
                start++;
            }
            
            while (start < json.length() && (json.charAt(start) == ',' || json.charAt(start) == ' ')) {
                start++;
            }
            currentIndex++;
        }
        
        if (currentIndex != index) return null;
        
        int end = start;
        if (end < json.length() && json.charAt(end) == '{') {
            int braceCount = 1;
            end++;
            while (end < json.length() && braceCount > 0) {
                if (json.charAt(end) == '{') braceCount++;
                else if (json.charAt(end) == '}') braceCount--;
                end++;
            }
        } else if (end < json.length() && json.charAt(end) == '"') {
            end++;
            while (end < json.length() && json.charAt(end) != '"') {
                if (json.charAt(end) == '\\') end++;
                end++;
            }
            end++;
        }
        
        return json.substring(start, end);
    }
    
    private ChatResponse createErrorResponse(String message) {
        ChatResponse response = new ChatResponse();
        response.setId("error-" + System.currentTimeMillis());
        response.setMessage(new ChatMessage("assistant", "Error: " + message));
        response.setFinishReason("error");
        response.setCreatedTime(System.currentTimeMillis());
        return response;
    }
    
    private EmbeddingResponse createEmbeddingErrorResponse(String message) {
        EmbeddingResponse response = new EmbeddingResponse();
        response.setData(Collections.emptyList());
        return response;
    }
}
