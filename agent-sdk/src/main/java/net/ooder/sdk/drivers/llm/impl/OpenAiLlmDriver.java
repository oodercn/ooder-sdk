package net.ooder.sdk.drivers.llm.impl;

import net.ooder.sdk.driver.annotation.DriverImplementation;
import net.ooder.sdk.drivers.llm.LlmDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@DriverImplementation(value = "LlmDriver", skillId = "skill-llm-openai")
public class OpenAiLlmDriver implements LlmDriver {
    
    private static final Logger log = LoggerFactory.getLogger(OpenAiLlmDriver.class);
    
    private static final String DEFAULT_ENDPOINT = "https://api.openai.com/v1";
    
    private LlmConfig config;
    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final AtomicLong requestIdCounter = new AtomicLong(0);
    
    private final Map<String, ModelInfo> models = new ConcurrentHashMap<>();
    
    @Override
    public void init(LlmConfig config) {
        this.config = config;
        
        registerModel("gpt-4", "GPT-4", 8192, true, false, true);
        registerModel("gpt-4-turbo", "GPT-4 Turbo", 128000, true, false, true);
        registerModel("gpt-3.5-turbo", "GPT-3.5 Turbo", 16384, true, false, true);
        registerModel("text-embedding-ada-002", "Ada Embeddings", 8191, false, true, false);
        registerModel("text-embedding-3-small", "Embeddings v3 Small", 8191, false, true, false);
        registerModel("text-embedding-3-large", "Embeddings v3 Large", 8191, false, true, false);
        
        connected.set(true);
        log.info("OpenAI LLM driver initialized (simulated mode)");
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
            
            log.debug("OpenAI chat completed: {}", response.getId());
            return response;
        });
    }
    
    @Override
    public CompletableFuture<ChatResponse> chatStream(ChatRequest request, ChatStreamHandler handler) {
        return CompletableFuture.supplyAsync(() -> {
            String model = request.getModel() != null ? request.getModel() : "gpt-3.5-turbo";
            
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
            
            log.debug("OpenAI chat stream completed: {}", response.getId());
            return response;
        });
    }
    
    @Override
    public CompletableFuture<EmbeddingResponse> embed(EmbeddingRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            String model = request.getModel() != null ? request.getModel() : "text-embedding-ada-002";
            int dimension = model.contains("large") ? 3072 : 1536;
            
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
            
            log.debug("OpenAI embedding completed for {} inputs", request.getInput().size());
            return response;
        });
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
            
            log.debug("OpenAI completion completed: {}", response.getId());
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
        return "1.0.0";
    }
    
    private String simulateOpenAiResponse(ChatRequest request) {
        if (request.getMessages() == null || request.getMessages().isEmpty()) {
            return "I don't have any input to respond to.";
        }
        
        ChatMessage lastMessage = request.getMessages().get(request.getMessages().size() - 1);
        String userContent = lastMessage.getContent();
        
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
        return (int) (text.split("\\s+").length * 1.3);
    }
}
