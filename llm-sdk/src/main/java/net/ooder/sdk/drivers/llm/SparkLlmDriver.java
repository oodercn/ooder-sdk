package net.ooder.sdk.drivers.llm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 讯飞星火 LLM Driver 实现
 * 申请地址: https://xinghuo.xfyun.cn
 * 
 * 使用示例:
 * <pre>
 * SparkLlmDriver driver = new SparkLlmDriver();
 * LlmConfig config = new LlmConfig();
 * config.setApiKey("your-api-key");
 * config.setApiSecret("your-api-secret");
 * config.setAppId("your-app-id");
 * driver.init(config);
 * 
 * ChatResponse response = driver.chat(request).get();
 * </pre>
 */
public class SparkLlmDriver extends AbstractLlmDriver {
    
    private static final Logger log = LoggerFactory.getLogger(SparkLlmDriver.class);
    
    private String appId;
    private String apiKey;
    private String apiSecret;
    private String apiUrl = "wss://spark-api.xf-yun.com/v3.5/chat";
    
    @Override
    protected void doInit() {
        if (config != null) {
            this.appId = config.getAppId();
            this.apiKey = config.getApiKey();
            this.apiSecret = config.getApiSecret();
            
            if (config.getBaseUrl() != null) {
                this.apiUrl = config.getBaseUrl();
            }
        }
        
        if (appId == null || apiKey == null || apiSecret == null) {
            throw new IllegalArgumentException("APPID, APIKey and APISecret are required");
        }
        
        log.info("Spark LLM Driver initialized, appId: {}", appId);
    }
    
    @Override
    protected CompletableFuture<ChatResponse> doChat(ChatRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // TODO: 实现讯飞星火 WebSocket API 调用
                // 参考文档: https://www.xfyun.cn/doc/spark/Web.html
                
                // 这里返回模拟响应，实际实现需要:
                // 1. 构建鉴权 URL
                // 2. 建立 WebSocket 连接
                // 3. 发送请求
                // 4. 解析响应
                
                log.warn("Spark API not fully implemented, returning mock response");
                return createMockChatResponse(request);
                
            } catch (Exception e) {
                log.error("Spark API call failed", e);
                throw new RuntimeException("Spark API call failed", e);
            }
        });
    }
    
    @Override
    protected CompletableFuture<ChatResponse> doChatStream(ChatRequest request, ChatStreamHandler handler) {
        // TODO: 实现流式响应
        return doChat(request);
    }
    
    @Override
    protected CompletableFuture<EmbeddingResponse> doEmbed(EmbeddingRequest request) {
        CompletableFuture<EmbeddingResponse> future = new CompletableFuture<>();
        future.completeExceptionally(new UnsupportedOperationException("Spark embedding not supported yet"));
        return future;
    }
    
    @Override
    protected CompletableFuture<CompletionResponse> doComplete(CompletionRequest request) {
        // 讯飞星火主要支持 chat 模式，这里转换为 chat 调用
        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setMessages(Arrays.asList(ChatMessage.user(request.getPrompt())));
        
        return doChat(chatRequest).thenApply(chatResponse -> {
            CompletionResponse response = new CompletionResponse();
            response.setId(chatResponse.getId());
            response.setModel(chatResponse.getModel());
            
            CompletionChoice choice = new CompletionChoice();
            choice.setText(chatResponse.getMessage().getContent());
            choice.setIndex(0);
            choice.setFinishReason("stop");
            List<CompletionChoice> choices = new java.util.ArrayList<>();
            choices.add(choice);
            response.setChoices(choices);
            
            response.setUsage(chatResponse.getUsage());
            return response;
        });
    }
    
    @Override
    protected CompletableFuture<List<String>> doListModels() {
        return CompletableFuture.completedFuture(Arrays.asList(
            "spark-lite",
            "spark-pro",
            "spark-max",
            "spark-4.0-ultra"
        ));
    }
    
    @Override
    protected CompletableFuture<ModelInfo> doGetModelInfo(String modelId) {
        return CompletableFuture.supplyAsync(() -> {
            ModelInfo info = new ModelInfo();
            info.setId(modelId);
            info.setName("Spark " + modelId);
            info.setDescription("iFlytek Spark LLM");
            
            switch (modelId) {
                case "spark-lite":
                    info.setContextLength(4096);
                    break;
                case "spark-pro":
                case "spark-max":
                    info.setContextLength(8192);
                    break;
                case "spark-4.0-ultra":
                    info.setContextLength(128000);
                    break;
                default:
                    info.setContextLength(4096);
            }
            
            info.setSupportsStreaming(true);
            info.setSupportsEmbeddings(false);
            return info;
        });
    }
    
    @Override
    protected void doClose() {
        log.info("Spark LLM Driver closed");
    }
    
    @Override
    public String getDriverName() {
        return "spark-llm";
    }
    
    @Override
    public String getDriverVersion() {
        return "3.5.0";
    }
    
    /**
     * 设置 API URL
     */
    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
    
    /**
     * 创建模拟响应（用于测试）
     */
    private ChatResponse createMockChatResponse(ChatRequest request) {
        String userMessage = request.getMessages().stream()
            .filter(m -> "user".equals(m.getRole()))
            .reduce((first, second) -> second)
            .map(ChatMessage::getContent)
            .orElse("");
        
        String responseContent = "[Spark Mock] 收到你的消息: " + userMessage.substring(0, Math.min(userMessage.length(), 20)) + "...\n" +
            "这是一个模拟响应。要获取真实响应，请:\n" +
            "1. 在讯飞星火官网申请 API 密钥\n" +
            "2. 配置 appId, apiKey, apiSecret\n" +
            "3. 实现 WebSocket API 调用逻辑";
        
        return createChatResponse(responseContent, "spark-mock");
    }
}
