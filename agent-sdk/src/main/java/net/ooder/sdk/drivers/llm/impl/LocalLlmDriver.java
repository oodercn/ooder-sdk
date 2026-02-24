package net.ooder.sdk.drivers.llm.impl;

import net.ooder.sdk.driver.annotation.DriverImplementation;
import net.ooder.sdk.drivers.llm.LlmDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@DriverImplementation(value = "LlmDriver", skillId = "skill-llm-local")
public class LocalLlmDriver implements LlmDriver {
    
    private static final Logger log = LoggerFactory.getLogger(LocalLlmDriver.class);
    
    private LlmConfig config;
    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final AtomicLong requestIdCounter = new AtomicLong(0);
    
    private final Map<String, ModelInfo> models = new ConcurrentHashMap<>();
    
    @Override
    public void init(LlmConfig config) {
        this.config = config;
        
        ModelInfo defaultModel = new ModelInfo();
        defaultModel.setId("local-default");
        defaultModel.setName("Local Default Model");
        defaultModel.setProvider("local");
        defaultModel.setContextLength(4096);
        defaultModel.setSupportsStreaming(true);
        defaultModel.setSupportsEmbeddings(true);
        defaultModel.setSupportsFunctionCalling(false);
        
        models.put(defaultModel.getId(), defaultModel);
        
        connected.set(true);
        log.info("Local LLM driver initialized");
    }
    
    @Override
    public CompletableFuture<ChatResponse> chat(ChatRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            String model = request.getModel() != null ? request.getModel() : "local-default";
            
            String responseContent = generateResponse(request);
            
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
            
            log.debug("Chat completed: {}", response.getId());
            return response;
        });
    }
    
    @Override
    public CompletableFuture<ChatResponse> chatStream(ChatRequest request, ChatStreamHandler handler) {
        return CompletableFuture.supplyAsync(() -> {
            String model = request.getModel() != null ? request.getModel() : "local-default";
            
            String fullResponse = generateResponse(request);
            
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
            
            log.debug("Chat stream completed: {}", response.getId());
            return response;
        });
    }
    
    @Override
    public CompletableFuture<EmbeddingResponse> embed(EmbeddingRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            String model = request.getModel() != null ? request.getModel() : "local-default";
            int dimension = 1536;
            
            List<EmbeddingData> embeddings = new ArrayList<>();
            for (int i = 0; i < request.getInput().size(); i++) {
                float[] embedding = generateEmbedding(request.getInput().get(i), dimension);
                
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
            
            log.debug("Embedding completed for {} inputs", request.getInput().size());
            return response;
        });
    }
    
    @Override
    public CompletableFuture<CompletionResponse> complete(CompletionRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            String model = request.getModel() != null ? request.getModel() : "local-default";
            
            String completion = generateCompletion(request.getPrompt());
            
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
            
            log.debug("Completion completed: {}", response.getId());
            return response;
        });
    }
    
    @Override
    public CompletableFuture<TokenCountResponse> countTokens(String text) {
        return CompletableFuture.supplyAsync(() -> {
            int count = estimateTokens(text);
            
            TokenCountResponse response = new TokenCountResponse();
            response.setTokenCount(count);
            
            return response;
        });
    }
    
    @Override
    public CompletableFuture<List<String>> listModels() {
        return CompletableFuture.supplyAsync(() -> new ArrayList<>(models.keySet()));
    }
    
    @Override
    public CompletableFuture<ModelInfo> getModelInfo(String modelId) {
        return CompletableFuture.supplyAsync(() -> models.get(modelId));
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
        return "1.0.0";
    }
    
    private String generateResponse(ChatRequest request) {
        if (request.getMessages() == null || request.getMessages().isEmpty()) {
            return "I don't have any input to respond to.";
        }
        
        ChatMessage lastMessage = request.getMessages().get(request.getMessages().size() - 1);
        String userContent = lastMessage.getContent();
        
        if (userContent.contains("hello") || userContent.contains("hi")) {
            return "Hello! I'm a local LLM simulation. How can I help you today?";
        } else if (userContent.contains("?")) {
            return "That's an interesting question. As a local LLM simulation, I provide placeholder responses. " +
                   "In a real implementation, this would be processed by an actual language model.";
        } else {
            return "I received your message: \"" + userContent + "\". " +
                   "This is a simulated response from the local LLM driver.";
        }
    }
    
    private String generateCompletion(String prompt) {
        return "Completion for: " + prompt.substring(0, Math.min(50, prompt.length())) + "...";
    }
    
    private float[] generateEmbedding(String text, int dimension) {
        float[] embedding = new float[dimension];
        Random random = new Random(text.hashCode());
        
        for (int i = 0; i < dimension; i++) {
            embedding[i] = (float) (random.nextGaussian() * 0.1);
        }
        
        float norm = 0;
        for (float v : embedding) {
            norm += v * v;
        }
        norm = (float) Math.sqrt(norm);
        
        for (int i = 0; i < dimension; i++) {
            embedding[i] /= norm;
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
        return text.split("\\s+").length;
    }
}
