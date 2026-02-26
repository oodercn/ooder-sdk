package net.ooder.sdk.drivers.llm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 百度文心 LLM Driver 实现
 * 申请地址: https://cloud.baidu.com/doc/WENXINWORKSHOP/s/Nlks5zkzu
 *
 * 使用示例:
 * <pre>
 * BaiduWenxinDriver driver = new BaiduWenxinDriver();
 * LlmConfig config = new LlmConfig();
 * config.setApiKey("your-api-key");
 * driver.init(config);
 *
 * ChatResponse response = driver.chat(request).get();
 * </pre>
 */
public class BaiduWenxinDriver extends AbstractLlmDriver {

    private static final Logger log = LoggerFactory.getLogger(BaiduWenxinDriver.class);

    // 百度文心 API 地址
    private static final String BASE_URL = "https://qianfan.baidubce.com/v2";
    private static final String CHAT_ENDPOINT = "/chat";
    private static final String COMPLETIONS_ENDPOINT = "/completions";
    private static final String EMBEDDINGS_ENDPOINT = "/embeddings";

    private String apiKey;
    private String baseUrl = BASE_URL;

    @Override
    protected void doInit() {
        if (config != null) {
            this.apiKey = config.getApiKey();
            if (config.getBaseUrl() != null) {
                this.baseUrl = config.getBaseUrl();
            }
        }

        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("API Key is required for Baidu Wenxin");
        }

        log.info("Baidu Wenxin Driver initialized");
    }

    @Override
    protected CompletableFuture<ChatResponse> doChat(ChatRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String model = request.getModel() != null ? request.getModel() : "ernie-speed";
                String url = baseUrl + CHAT_ENDPOINT + "/" + model;

                // 构建请求体
                JSONObject requestBody = new JSONObject();
                requestBody.put("model", model);

                // 转换消息格式
                JSONArray messages = new JSONArray();
                for (ChatMessage msg : request.getMessages()) {
                    JSONObject message = new JSONObject();
                    message.put("role", msg.getRole());
                    message.put("content", msg.getContent());
                    messages.add(message);
                }
                requestBody.put("messages", messages);

                // 可选参数
                if (request.getMaxTokens() > 0) {
                    requestBody.put("max_tokens", request.getMaxTokens());
                }
                if (config != null) {
                    requestBody.put("temperature", config.getTemperature());
                }

                // 发送请求
                String responseJson = sendPostRequest(url, requestBody.toJSONString());
                JSONObject responseObj = JSON.parseObject(responseJson);

                // 解析响应
                return parseChatResponse(responseObj);

            } catch (Exception e) {
                log.error("Baidu Wenxin API call failed", e);
                throw new RuntimeException("Baidu Wenxin API call failed: " + e.getMessage(), e);
            }
        });
    }

    @Override
    protected CompletableFuture<ChatResponse> doChatStream(ChatRequest request, ChatStreamHandler handler) {
        // 百度文心支持流式输出，这里先返回非流式实现
        return doChat(request);
    }

    @Override
    protected CompletableFuture<EmbeddingResponse> doEmbed(EmbeddingRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = baseUrl + EMBEDDINGS_ENDPOINT;

                JSONObject requestBody = new JSONObject();
                requestBody.put("model", "embedding-v1");
                requestBody.put("input", request.getInput());

                String responseJson = sendPostRequest(url, requestBody.toJSONString());
                JSONObject responseObj = JSON.parseObject(responseJson);

                return parseEmbeddingResponse(responseObj);

            } catch (Exception e) {
                log.error("Baidu Wenxin embedding failed", e);
                throw new RuntimeException("Embedding failed: " + e.getMessage(), e);
            }
        });
    }

    @Override
    protected CompletableFuture<CompletionResponse> doComplete(CompletionRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String model = request.getModel() != null ? request.getModel() : "ernie-speed";
                String url = baseUrl + COMPLETIONS_ENDPOINT + "/" + model;

                JSONObject requestBody = new JSONObject();
                requestBody.put("model", model);
                requestBody.put("prompt", request.getPrompt());

                if (request.getMaxTokens() > 0) {
                    requestBody.put("max_tokens", request.getMaxTokens());
                }

                String responseJson = sendPostRequest(url, requestBody.toJSONString());
                JSONObject responseObj = JSON.parseObject(responseJson);

                return parseCompletionResponse(responseObj);

            } catch (Exception e) {
                log.error("Baidu Wenxin completion failed", e);
                throw new RuntimeException("Completion failed: " + e.getMessage(), e);
            }
        });
    }

    @Override
    protected CompletableFuture<List<String>> doListModels() {
        return CompletableFuture.completedFuture(Arrays.asList(
            "ernie-speed",           // 免费
            "ernie-lite",            // 免费
            "ernie-tiny",            // 免费
            "ernie-4.0-turbo-8k",    // 付费
            "ernie-3.5-128k",        // 付费
            "embedding-v1"           // 嵌入模型
        ));
    }

    @Override
    protected CompletableFuture<ModelInfo> doGetModelInfo(String modelId) {
        return CompletableFuture.supplyAsync(() -> {
            ModelInfo info = new ModelInfo();
            info.setId(modelId);
            info.setName("Baidu " + modelId);
            info.setDescription("Baidu Wenxin LLM");

            switch (modelId) {
                case "ernie-speed":
                case "ernie-lite":
                case "ernie-tiny":
                    info.setContextLength(8192);
                    info.setDescription("免费模型，适合日常使用");
                    break;
                case "ernie-4.0-turbo-8k":
                    info.setContextLength(8192);
                    break;
                case "ernie-3.5-128k":
                    info.setContextLength(128000);
                    break;
                default:
                    info.setContextLength(4096);
            }

            info.setSupportsStreaming(true);
            info.setSupportsEmbeddings(modelId.contains("embedding"));
            return info;
        });
    }

    @Override
    protected void doClose() {
        log.info("Baidu Wenxin Driver closed");
    }

    @Override
    public String getDriverName() {
        return "baidu-wenxin";
    }

    @Override
    public String getDriverVersion() {
        return "2.0.0";
    }

    /**
     * 发送 POST 请求
     */
    private String sendPostRequest(String urlString, String jsonBody) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setDoOutput(true);
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(60000);

        // 发送请求体
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // 读取响应
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }
                return response.toString();
            }
        } else {
            // 读取错误信息
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                StringBuilder error = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    error.append(line);
                }
                throw new RuntimeException("API Error " + responseCode + ": " + error.toString());
            }
        }
    }

    /**
     * 解析聊天响应
     */
    private ChatResponse parseChatResponse(JSONObject responseObj) {
        ChatResponse response = new ChatResponse();
        response.setId(responseObj.getString("id"));
        response.setModel(responseObj.getString("model"));
        response.setCreatedTime(System.currentTimeMillis());

        // 解析消息
        JSONObject result = responseObj.getJSONObject("result");
        if (result != null) {
            ChatMessage message = new ChatMessage();
            message.setRole(result.getString("role"));
            message.setContent(result.getString("content"));
            response.setMessage(message);
        }

        // 解析 usage
        JSONObject usage = responseObj.getJSONObject("usage");
        if (usage != null) {
            UsageInfo usageInfo = new UsageInfo();
            usageInfo.setPromptTokens(usage.getIntValue("prompt_tokens"));
            usageInfo.setCompletionTokens(usage.getIntValue("completion_tokens"));
            usageInfo.setTotalTokens(usage.getIntValue("total_tokens"));
            response.setUsage(usageInfo);
        }

        response.setFinishReason(responseObj.getString("finish_reason"));
        return response;
    }

    /**
     * 解析嵌入响应
     */
    private EmbeddingResponse parseEmbeddingResponse(JSONObject responseObj) {
        EmbeddingResponse response = new EmbeddingResponse();
        response.setModel(responseObj.getString("model"));

        JSONArray data = responseObj.getJSONArray("data");
        List<EmbeddingData> embeddingDataList = new java.util.ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            JSONObject item = data.getJSONObject(i);
            EmbeddingData ed = new EmbeddingData();
            ed.setIndex(item.getIntValue("index"));

            JSONArray embeddingArray = item.getJSONArray("embedding");
            float[] embedding = new float[embeddingArray.size()];
            for (int j = 0; j < embeddingArray.size(); j++) {
                embedding[j] = embeddingArray.getFloatValue(j);
            }
            ed.setEmbedding(embedding);
            embeddingDataList.add(ed);
        }

        response.setData(embeddingDataList);

        // 解析 usage
        JSONObject usage = responseObj.getJSONObject("usage");
        if (usage != null) {
            UsageInfo usageInfo = new UsageInfo();
            usageInfo.setPromptTokens(usage.getIntValue("prompt_tokens"));
            usageInfo.setTotalTokens(usage.getIntValue("total_tokens"));
            response.setUsage(usageInfo);
        }

        return response;
    }

    /**
     * 解析补全响应
     */
    private CompletionResponse parseCompletionResponse(JSONObject responseObj) {
        CompletionResponse response = new CompletionResponse();
        response.setId(responseObj.getString("id"));
        response.setModel(responseObj.getString("model"));

        JSONArray choices = responseObj.getJSONArray("choices");
        List<CompletionChoice> choiceList = new java.util.ArrayList<>();

        for (int i = 0; i < choices.size(); i++) {
            JSONObject choiceObj = choices.getJSONObject(i);
            CompletionChoice choice = new CompletionChoice();
            choice.setIndex(choiceObj.getIntValue("index"));
            choice.setText(choiceObj.getString("text"));
            choice.setFinishReason(choiceObj.getString("finish_reason"));
            choiceList.add(choice);
        }

        response.setChoices(choiceList);

        // 解析 usage
        JSONObject usage = responseObj.getJSONObject("usage");
        if (usage != null) {
            UsageInfo usageInfo = new UsageInfo();
            usageInfo.setPromptTokens(usage.getIntValue("prompt_tokens"));
            usageInfo.setCompletionTokens(usage.getIntValue("completion_tokens"));
            usageInfo.setTotalTokens(usage.getIntValue("total_tokens"));
            response.setUsage(usageInfo);
        }

        return response;
    }
}
