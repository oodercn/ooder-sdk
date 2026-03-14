package net.ooder.scene.llm.proxy;

import net.ooder.sdk.drivers.llm.LlmDriver;
import net.ooder.sdk.drivers.llm.LlmDriver.LlmConfig;
import net.ooder.sdk.llm.model.*;
import net.ooder.scene.core.SceneEngine;
import net.ooder.scene.skill.llm.LlmProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * SceneEngine LLM Proxy
 *
 * <p>将 SceneEngine 的 LLM 能力代理给 agent-sdk 的 LlmDriver 使用</p>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.0
 */
public class SceneEngineLlmProxy implements LlmDriver {

    private static final Logger log = LoggerFactory.getLogger(SceneEngineLlmProxy.class);

    private final SceneEngine sceneEngine;
    private LlmConfig llmConfig;
    private volatile boolean connected = false;

    public SceneEngineLlmProxy(SceneEngine sceneEngine) {
        this.sceneEngine = sceneEngine;
        this.llmConfig = new LlmConfig();
    }

    public SceneEngineLlmProxy(SceneEngine sceneEngine, LlmConfig llmConfig) {
        this.sceneEngine = sceneEngine;
        this.llmConfig = llmConfig != null ? llmConfig : new LlmConfig();
    }

    @Override
    public void init(LlmConfig config) {
        this.llmConfig = config != null ? config : new LlmConfig();
        log.info("SceneEngineLlmProxy initialized");
    }

    @Override
    public CompletableFuture<DriverChatResponse> chat(DriverChatRequest request) {
        log.debug("Chat request: model={}", request.getModel());

        return CompletableFuture.supplyAsync(() -> {
            LlmProvider provider = sceneEngine.getService(LlmProvider.class);
            if (provider == null) {
                throw new IllegalStateException("LlmProvider not available");
            }

            // 转换请求格式
            List<Map<String, Object>> messages = convertMessages(request.getMessages());
            Map<String, Object> options = convertOptions(request);

            Map<String, Object> response = provider.chat(request.getModel(), messages, options);
            return convertToDriverChatResponse(response);
        });
    }

    @Override
    public CompletableFuture<DriverChatResponse> chatStream(DriverChatRequest request, ChatStreamHandler handler) {
        log.debug("Chat stream request: model={}", request.getModel());

        return CompletableFuture.supplyAsync(() -> {
            LlmProvider provider = sceneEngine.getService(LlmProvider.class);
            if (provider == null) {
                throw new IllegalStateException("LlmProvider not available");
            }

            // 转换请求格式
            List<Map<String, Object>> messages = convertMessages(request.getMessages());
            Map<String, Object> options = convertOptions(request);

            // 使用 StreamHandler 适配
            provider.chatStream(request.getModel(), messages, options, new net.ooder.scene.skill.llm.StreamHandler() {
                @Override
                public void onContent(String content) {
                    handler.onToken(content);
                }

                @Override
                public void onChunk(String chunk) {
                    handler.onToken(chunk);
                }

                @Override
                public void onComplete(Map<String, Object> metadata) {
                    // 流式完成
                }

                @Override
                public void onError(Throwable error) {
                    handler.onError(error);
                }
            });

            return new DriverChatResponse();
        });
    }

    @Override
    public CompletableFuture<EmbeddingResponse> embed(EmbeddingRequest request) {
        log.debug("Embed request: model={}", request.getModel());

        return CompletableFuture.supplyAsync(() -> {
            LlmProvider provider = sceneEngine.getService(LlmProvider.class);
            if (provider == null) {
                throw new IllegalStateException("LlmProvider not available");
            }

            List<double[]> embeddings = provider.embed(request.getModel(), request.getInput());
            return convertToEmbeddingResponse(embeddings);
        });
    }

    @Override
    public CompletableFuture<CompletionResponse> complete(CompletionRequest request) {
        log.debug("Complete request: model={}", request.getModel());

        return CompletableFuture.supplyAsync(() -> {
            LlmProvider provider = sceneEngine.getService(LlmProvider.class);
            if (provider == null) {
                throw new IllegalStateException("LlmProvider not available");
            }

            String result = provider.complete(request.getModel(), request.getPrompt(), convertOptions(request));
            CompletionResponse response = new CompletionResponse();
            // CompletionResponse 使用 choices 字段，需要创建 CompletionChoice
            CompletionChoice choice = new CompletionChoice();
            choice.setText(result);
            response.setChoices(Collections.singletonList(choice));
            return response;
        });
    }

    @Override
    public CompletableFuture<TokenCountResponse> countTokens(String text) {
        return CompletableFuture.completedFuture(new TokenCountResponse());
    }

    @Override
    public CompletableFuture<List<String>> listModels() {
        return CompletableFuture.supplyAsync(() -> {
            LlmProvider provider = sceneEngine.getService(LlmProvider.class);
            if (provider != null) {
                return provider.getSupportedModels();
            }
            return new java.util.ArrayList<>();
        });
    }

    @Override
    public CompletableFuture<ModelInfo> getModelInfo(String modelId) {
        return CompletableFuture.completedFuture(new ModelInfo());
    }

    @Override
    public boolean supportsStreaming() {
        LlmProvider provider = sceneEngine.getService(LlmProvider.class);
        return provider != null && provider.supportsStreaming();
    }

    @Override
    public boolean supportsEmbeddings() {
        return true;
    }

    @Override
    public boolean supportsFunctionCalling() {
        LlmProvider provider = sceneEngine.getService(LlmProvider.class);
        return provider != null && provider.supportsFunctionCalling();
    }

    @Override
    public int getMaxContextLength(String modelId) {
        return 8192;
    }

    @Override
    public void close() {
        connected = false;
        log.info("SceneEngineLlmProxy closed");
    }

    @Override
    public boolean isConnected() {
        return connected && sceneEngine.isRunning();
    }

    @Override
    public String getDriverName() {
        return "scene-engine";
    }

    @Override
    public String getDriverVersion() {
        return "2.3.1";
    }

    // ========== 私有方法 ==========

    private List<Map<String, Object>> convertMessages(List<ChatMessage> messages) {
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        if (messages == null) {
            return result;
        }
        for (ChatMessage msg : messages) {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("role", msg.getRole());
            map.put("content", msg.getContent());
            if (msg.getName() != null) {
                map.put("name", msg.getName());
            }
            result.add(map);
        }
        return result;
    }

    private Map<String, Object> convertOptions(Object request) {
        Map<String, Object> options = new java.util.HashMap<>();
        options.put("max_tokens", llmConfig.getMaxTokens());
        options.put("temperature", llmConfig.getTemperature());
        return options;
    }

    private DriverChatResponse convertToDriverChatResponse(Map<String, Object> response) {
        DriverChatResponse driverResponse = new DriverChatResponse();
        if (response == null) {
            return driverResponse;
        }
        
        // 提取模型信息
        Object model = response.get("model");
        if (model != null) {
            driverResponse.setModel(model.toString());
        }
        
        return driverResponse;
    }

    private EmbeddingResponse convertToEmbeddingResponse(List<double[]> embeddings) {
        EmbeddingResponse response = new EmbeddingResponse();
        if (embeddings == null || embeddings.isEmpty()) {
            return response;
        }
        
        // 将 double[] 转换为 float[]
        List<float[]> floatEmbeddings = new java.util.ArrayList<>();
        for (double[] embedding : embeddings) {
            float[] floatArray = new float[embedding.length];
            for (int i = 0; i < embedding.length; i++) {
                floatArray[i] = (float) embedding[i];
            }
            floatEmbeddings.add(floatArray);
        }
        
        List<EmbeddingData> dataList = new java.util.ArrayList<>();
        for (int i = 0; i < floatEmbeddings.size(); i++) {
            float[] embedding = floatEmbeddings.get(i);
            EmbeddingData data = new EmbeddingData();
            data.setIndex(i);
            data.setEmbedding(embedding);
            dataList.add(data);
        }
        response.setData(dataList);
        
        return response;
    }
}
