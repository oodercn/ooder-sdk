package net.ooder.sdk.drivers.llm.impl;

import net.ooder.sdk.driver.annotation.DriverImplementation;
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

@DriverImplementation(value = "LlmDriver", skillId = "skill-llm-local")
public class LocalLlmDriver implements LlmDriver {
    
    private static final Logger log = LoggerFactory.getLogger(LocalLlmDriver.class);
    
    private static final String DEFAULT_OLLAMA_ENDPOINT = "http://localhost:11434";
    private static final String OLLAMA_GENERATE_ENDPOINT = "/api/generate";
    private static final String OLLAMA_EMBED_ENDPOINT = "/api/embeddings";
    private static final String OLLAMA_TAGS_ENDPOINT = "/api/tags";
    
    private LlmConfig config;
    private String endpoint;
    private String defaultModel;
    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final AtomicLong requestIdCounter = new AtomicLong(0);
    
    private final Map<String, ModelInfo> models = new ConcurrentHashMap<>();
    private boolean simulationMode = false;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    @Override
    public void init(LlmConfig config) {
        this.config = config;
        
        if (config != null) {
            this.endpoint = config.getEndpoint() != null ? config.getEndpoint() : DEFAULT_OLLAMA_ENDPOINT;
            this.defaultModel = config.getDefaultModel() != null ? config.getDefaultModel() : "llama2";
            this.simulationMode = config.isSimulationMode();
        } else {
            this.endpoint = DEFAULT_OLLAMA_ENDPOINT;
            this.defaultModel = "llama2";
            this.simulationMode = true;
        }
        
        registerModel("llama2", "Llama 2", 4096, true, true, false);
        registerModel("llama3", "Llama 3", 8192, true, true, false);
        registerModel("mistral", "Mistral", 8192, true, true, false);
        registerModel("codellama", "Code Llama", 16384, true, true, false);
        registerModel("phi3", "Phi-3", 4096, true, true, false);
        registerModel("qwen2", "Qwen 2", 8192, true, true, false);
        registerModel("nomic-embed-text", "Nomic Embed Text", 8192, false, true, false);
        registerModel("mxbai-embed-large", "MXBai Embed Large", 512, false, true, false);
        
        if (!simulationMode) {
            discoverOllamaModels();
        }
        
        connected.set(true);
        
        if (simulationMode) {
            log.warn("Local LLM driver initialized in SIMULATION mode");
        } else {
            log.info("Local LLM driver initialized with endpoint: {}", endpoint);
        }
    }
    
    private void registerModel(String id, String name, int contextLength, 
                               boolean streaming, boolean embeddings, boolean functionCalling) {
        ModelInfo info = new ModelInfo();
        info.setId(id);
        info.setName(name);
        info.setProvider("local");
        info.setContextLength(contextLength);
        info.setSupportsStreaming(streaming);
        info.setSupportsEmbeddings(embeddings);
        info.setSupportsFunctionCalling(functionCalling);
        models.put(id, info);
    }
    
    private void discoverOllamaModels() {
        try {
            String response = sendGetRequest(endpoint + OLLAMA_TAGS_ENDPOINT);
            if (response != null) {
                parseAndRegisterModels(response);
                log.info("Discovered {} models from Ollama", models.size());
            }
        } catch (Exception e) {
            log.warn("Failed to discover Ollama models, using defaults: {}", e.getMessage());
        }
    }
    
    private void parseAndRegisterModels(String response) {
        int modelsStart = response.indexOf("\"models\":");
        if (modelsStart < 0) return;
        
        String modelsArray = response.substring(modelsStart);
        int index = 0;
        
        while (true) {
            int nameStart = modelsArray.indexOf("\"name\":", index);
            if (nameStart < 0) break;
            
            nameStart += 8;
            int nameEnd = modelsArray.indexOf("\"", nameStart);
            if (nameEnd < 0) break;
            
            String modelName = modelsArray.substring(nameStart, nameEnd);
            
            if (!models.containsKey(modelName)) {
                ModelInfo info = new ModelInfo();
                info.setId(modelName);
                info.setName(modelName);
                info.setProvider("ollama");
                info.setContextLength(4096);
                info.setSupportsStreaming(true);
                info.setSupportsEmbeddings(true);
                info.setSupportsFunctionCalling(false);
                models.put(modelName, info);
            }
            
            index = nameEnd + 1;
        }
    }
    
    @Override
    public CompletableFuture<ChatResponse> chat(ChatRequest request) {
        if (simulationMode) {
            return chatSimulated(request);
        }
        return chatWithOllama(request);
    }
    
    private CompletableFuture<ChatResponse> chatWithOllama(ChatRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            String model = request.getModel() != null ? request.getModel() : defaultModel;
            
            try {
                String prompt = buildPromptFromMessages(request.getMessages());
                
                StringBuilder sb = new StringBuilder();
                sb.append("{\"model\":\"").append(model).append("\",");
                sb.append("\"prompt\":\"").append(escapeJson(prompt)).append("\",");
                sb.append("\"stream\":false");
                
                if (request.getTemperature() > 0) {
                    sb.append(",\"options\":{\"temperature\":").append(request.getTemperature()).append("}");
                }
                
                sb.append("}");
                
                String response = sendPostRequest(endpoint + OLLAMA_GENERATE_ENDPOINT, sb.toString());
                return parseOllamaResponse(response, model);
            } catch (Exception e) {
                log.error("Ollama chat request failed", e);
                return createErrorResponse("Request failed: " + e.getMessage());
            }
        }, executor);
    }
    
    private CompletableFuture<ChatResponse> chatSimulated(ChatRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            String model = request.getModel() != null ? request.getModel() : defaultModel;
            
            String responseContent = generateSimulatedResponse(request);
            
            ChatMessage assistantMessage = new ChatMessage("assistant", responseContent);
            
            ChatResponse response = new ChatResponse();
            response.setId("chat-" + System.currentTimeMillis() + "-" + requestIdCounter.incrementAndGet());
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
    
    private String buildPromptFromMessages(List<ChatMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            return "";
        }
        
        StringBuilder prompt = new StringBuilder();
        for (ChatMessage msg : messages) {
            String role = msg.getRole();
            if ("system".equals(role)) {
                prompt.append("System: ").append(msg.getContent()).append("\n\n");
            } else if ("user".equals(role)) {
                prompt.append("User: ").append(msg.getContent()).append("\n");
            } else if ("assistant".equals(role)) {
                prompt.append("Assistant: ").append(msg.getContent()).append("\n");
            }
        }
        prompt.append("Assistant: ");
        
        return prompt.toString();
    }
    
    private ChatResponse parseOllamaResponse(String response, String model) {
        try {
            ChatResponse chatResponse = new ChatResponse();
            chatResponse.setModel(model);
            chatResponse.setCreatedTime(System.currentTimeMillis());
            chatResponse.setId("ollama-" + System.currentTimeMillis());
            
            String content = extractJsonValue(response, "response");
            if (content == null) {
                content = extractJsonValue(response, "message");
            }
            
            ChatMessage assistantMessage = new ChatMessage("assistant", content != null ? content : "");
            chatResponse.setMessage(assistantMessage);
            chatResponse.setFinishReason("stop");
            
            UsageInfo usage = new UsageInfo();
            String promptTokens = extractJsonValue(response, "prompt_eval_count");
            String completionTokens = extractJsonValue(response, "eval_count");
            
            usage.setPromptTokens(promptTokens != null ? Integer.parseInt(promptTokens) : 0);
            usage.setCompletionTokens(completionTokens != null ? Integer.parseInt(completionTokens) : 0);
            usage.setTotalTokens(usage.getPromptTokens() + usage.getCompletionTokens());
            chatResponse.setUsage(usage);
            
            log.debug("Ollama chat completed: {}", chatResponse.getId());
            return chatResponse;
        } catch (Exception e) {
            log.error("Failed to parse Ollama response", e);
            return createErrorResponse("Parse error: " + e.getMessage());
        }
    }
    
    @Override
    public CompletableFuture<ChatResponse> chatStream(ChatRequest request, ChatStreamHandler handler) {
        return CompletableFuture.supplyAsync(() -> {
            String model = request.getModel() != null ? request.getModel() : defaultModel;
            
            String fullResponse = generateSimulatedResponse(request);
            
            String[] words = fullResponse.split(" ");
            StringBuilder currentContent = new StringBuilder();
            
            for (String word : words) {
                currentContent.append(word).append(" ");
                
                if (handler != null) {
                    handler.onToken(word + " ");
                }
                
                try {
                    Thread.sleep(50);
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
            response.setId("chat-stream-" + System.currentTimeMillis() + "-" + requestIdCounter.incrementAndGet());
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
        return embedWithOllama(request);
    }
    
    private CompletableFuture<EmbeddingResponse> embedWithOllama(EmbeddingRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            String model = request.getModel() != null ? request.getModel() : "nomic-embed-text";
            
            try {
                List<EmbeddingData> embeddings = new ArrayList<>();
                
                for (int i = 0; i < request.getInput().size(); i++) {
                    String input = request.getInput().get(i);
                    
                    StringBuilder sb = new StringBuilder();
                    sb.append("{\"model\":\"").append(model).append("\",");
                    sb.append("\"prompt\":\"").append(escapeJson(input)).append("\"}");
                    
                    String response = sendPostRequest(endpoint + OLLAMA_EMBED_ENDPOINT, sb.toString());
                    float[] embedding = parseOllamaEmbedding(response);
                    
                    EmbeddingData data = new EmbeddingData();
                    data.setIndex(i);
                    data.setEmbedding(embedding);
                    embeddings.add(data);
                }
                
                EmbeddingResponse embeddingResponse = new EmbeddingResponse();
                embeddingResponse.setModel(model);
                embeddingResponse.setData(embeddings);
                
                UsageInfo usage = new UsageInfo();
                usage.setPromptTokens(request.getInput().stream().mapToInt(s -> s.split("\\s+").length).sum());
                usage.setTotalTokens(usage.getPromptTokens());
                embeddingResponse.setUsage(usage);
                
                log.debug("Ollama embedding completed for {} inputs", request.getInput().size());
                return embeddingResponse;
            } catch (Exception e) {
                log.error("Ollama embedding request failed", e);
                return createEmbeddingErrorResponse("Request failed: " + e.getMessage());
            }
        }, executor);
    }
    
    private CompletableFuture<EmbeddingResponse> embedSimulated(EmbeddingRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            String model = request.getModel() != null ? request.getModel() : "local-embedding";
            int dimension = 1536;
            
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
    
    private float[] parseOllamaEmbedding(String response) {
        String embeddingStr = extractJsonValue(response, "embedding");
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
            String model = request.getModel() != null ? request.getModel() : defaultModel;
            
            String completion = "Local completion for: " + 
                request.getPrompt().substring(0, Math.min(50, request.getPrompt().length())) + "...";
            
            CompletionChoice choice = new CompletionChoice();
            choice.setText(completion);
            choice.setIndex(0);
            choice.setFinishReason("stop");
            
            CompletionResponse response = new CompletionResponse();
            response.setId("completion-" + System.currentTimeMillis() + "-" + requestIdCounter.incrementAndGet());
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
        return false;
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
        log.info("Local LLM driver closed");
    }
    
    @Override
    public boolean isConnected() {
        return connected.get();
    }
    
    @Override
    public String getDriverName() {
        return "LocalLLM";
    }
    
    @Override
    public String getDriverVersion() {
        return "2.0.0";
    }
    
    public boolean isSimulationMode() {
        return simulationMode;
    }
    
    private String generateSimulatedResponse(ChatRequest request) {
        if (request.getMessages() == null || request.getMessages().isEmpty()) {
            return "I don't have any input to respond to.";
        }
        
        ChatMessage lastMessage = request.getMessages().get(request.getMessages().size() - 1);
        String userContent = lastMessage.getContent() != null ? lastMessage.getContent().toLowerCase() : "";
        
        if (userContent.contains("hello") || userContent.contains("hi")) {
            return "Hello! I'm a local LLM simulation. In production, this would connect to Ollama or llama.cpp.";
        } else if (userContent.contains("code") || userContent.contains("programming")) {
            return "I'd be happy to help with coding! This is a simulated response. " +
                   "Configure Ollama endpoint for real local LLM inference.";
        } else if (userContent.contains("?")) {
            return "That's an interesting question. This is a simulated response from the local LLM driver. " +
                   "For real inference, ensure Ollama is running on " + endpoint;
        } else {
            return "I received your message. This is a simulated response. " +
                   "The actual implementation would use Ollama at " + endpoint;
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
    
    private String sendPostRequest(String urlStr, String body) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        try {
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
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
    
    private String sendGetRequest(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        try {
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(30000);
            
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
            }
            return null;
        } finally {
            conn.disconnect();
        }
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
        } else if (firstChar == '[') {
            int bracketCount = 1;
            int end = start + 1;
            while (end < json.length() && bracketCount > 0) {
                if (json.charAt(end) == '[') bracketCount++;
                else if (json.charAt(end) == ']') bracketCount--;
                end++;
            }
            return json.substring(start, end);
        } else {
            int end = start;
            while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-' || json.charAt(end) == '.')) {
                end++;
            }
            return json.substring(start, end);
        }
        return null;
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
