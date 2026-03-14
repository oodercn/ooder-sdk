package net.ooder.scene.skill.conversation.impl;

import net.ooder.scene.audit.AuditService;
import net.ooder.scene.llm.LlmService;
import net.ooder.scene.llm.SceneChatRequest;
import net.ooder.scene.skill.conversation.*;
import net.ooder.scene.skill.conversation.storage.ConversationStorageService;
import net.ooder.scene.skill.knowledge.*;
import net.ooder.scene.skill.rag.RagApi;
import net.ooder.scene.skill.rag.RagContext;
import net.ooder.scene.skill.rag.RagResult;
import net.ooder.scene.skill.tool.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对话服务实现
 *
 * <p>提供多轮对话能力实现。</p>
 *
 * <p>架构层次：应用层 - 智能增强</p>
 *
 * @author ooder
 * @since 2.3
 */
public class ConversationServiceImpl implements ConversationService {

    private static final Logger log = LoggerFactory.getLogger(ConversationServiceImpl.class);

    private static final int MAX_HISTORY_LENGTH = 100;
    private static final int MAX_CONTEXT_TOKENS = 4000;

    private final KnowledgeBaseService knowledgeBaseService;
    private final RagApi ragPipeline;
    private final ToolRegistry toolRegistry;
    private final ToolOrchestrator toolOrchestrator;
    private final LlmService llmService;
    private final AuditService auditService;
    private final ConversationStorageService storageService;
    private final InteractionFeedbackService feedbackService;

    private final Map<String, Conversation> conversations = new ConcurrentHashMap<>();
    private final Map<String, List<Message>> messageHistory = new ConcurrentHashMap<>();
    private final Map<String, ConversationStats> statsMap = new ConcurrentHashMap<>();
    private final Map<String, List<FunctionCallLog>> toolCallLogs = new ConcurrentHashMap<>();

    private boolean autoLearn = false;

    public ConversationServiceImpl(KnowledgeBaseService knowledgeBaseService,
                                    RagApi ragPipeline,
                                    ToolRegistry toolRegistry,
                                    ToolOrchestrator toolOrchestrator,
                                    LlmService llmService) {
        this(knowledgeBaseService, ragPipeline, toolRegistry, toolOrchestrator, llmService, null, null, null);
    }

    public ConversationServiceImpl(KnowledgeBaseService knowledgeBaseService,
                                    RagApi ragPipeline,
                                    ToolRegistry toolRegistry,
                                    ToolOrchestrator toolOrchestrator,
                                    LlmService llmService,
                                    AuditService auditService,
                                    ConversationStorageService storageService,
                                    InteractionFeedbackService feedbackService) {
        this.knowledgeBaseService = knowledgeBaseService;
        this.ragPipeline = ragPipeline;
        this.toolRegistry = toolRegistry;
        this.toolOrchestrator = toolOrchestrator;
        this.llmService = llmService;
        this.auditService = auditService;
        this.storageService = storageService;
        this.feedbackService = feedbackService;

        if (storageService != null) {
            storageService.initialize();
        }
    }
    
    @Override
    public Conversation createConversation(String userId, ConversationCreateRequest request) {
        log.info("Creating conversation for user: {}", userId);
        
        String conversationId = "conv_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        
        Conversation conversation = new Conversation();
        conversation.setConversationId(conversationId);
        conversation.setUserId(userId);
        conversation.setTitle(request.getTitle() != null ? request.getTitle() : "新对话");
        conversation.setKbId(request.getKbId());
        conversation.setEnabledTools(request.getEnabledTools());
        conversation.setSettings(request.getSettings());
        conversation.setCreatedAt(System.currentTimeMillis());
        conversation.setUpdatedAt(System.currentTimeMillis());
        conversation.setMessageCount(0);
        conversation.setStatus(Conversation.STATUS_ACTIVE);
        
        conversations.put(conversationId, conversation);
        messageHistory.put(conversationId, new ArrayList<>());
        
        if (request.getSystemPrompt() != null) {
            Message systemMessage = Message.system(request.getSystemPrompt());
            systemMessage.setMessageId("msg_sys_" + System.currentTimeMillis());
            systemMessage.setConversationId(conversationId);
            messageHistory.get(conversationId).add(systemMessage);
        }
        
        ConversationStats stats = new ConversationStats(conversationId);
        stats.setCreatedAt(System.currentTimeMillis());
        statsMap.put(conversationId, stats);
        
        log.info("Conversation created: {}", conversationId);
        return conversation;
    }
    
    @Override
    public Conversation getConversation(String conversationId) {
        return conversations.get(conversationId);
    }
    
    @Override
    public void deleteConversation(String conversationId) {
        log.info("Deleting conversation: {}", conversationId);
        
        conversations.remove(conversationId);
        messageHistory.remove(conversationId);
        statsMap.remove(conversationId);
    }
    
    @Override
    public List<Conversation> listConversations(String userId, int limit) {
        List<Conversation> result = new ArrayList<>();
        for (Conversation conv : conversations.values()) {
            if (conv.getUserId().equals(userId)) {
                result.add(conv);
            }
        }
        result.sort((a, b) -> Long.compare(b.getUpdatedAt(), a.getUpdatedAt()));
        
        if (result.size() > limit) {
            result = result.subList(0, limit);
        }
        
        return result;
    }
    
    @Override
    public MessageResponse sendMessage(String conversationId, MessageRequest request) {
        log.info("Sending message to conversation: {}", conversationId);
        
        Conversation conversation = conversations.get(conversationId);
        if (conversation == null) {
            throw new IllegalArgumentException("Conversation not found: " + conversationId);
        }
        
        List<Message> history = messageHistory.get(conversationId);
        ConversationStats stats = statsMap.get(conversationId);
        
        Message userMessage = Message.user(request.getContent());
        userMessage.setMessageId("msg_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        userMessage.setConversationId(conversationId);
        history.add(userMessage);
        
        stats.incrementUserMessages();
        stats.setLastMessageAt(System.currentTimeMillis());
        
        MessageResponse response = new MessageResponse();
        response.setConversationId(conversationId);
        response.setMessageId("msg_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        
        List<MessageResponse.SourceReference> sources = new ArrayList<>();
        List<MessageResponse.ToolExecution> toolExecutions = new ArrayList<>();
        
        try {
            if (request.isEnableRag() || (request.getKbIds() != null && !request.getKbIds().isEmpty())) {
                List<String> kbIds = request.getKbIds() != null ? request.getKbIds() : 
                        (conversation.getKbId() != null ? Arrays.asList(conversation.getKbId()) : new ArrayList<>());
                
                for (String kbId : kbIds) {
                    KnowledgeSearchRequest searchRequest = new KnowledgeSearchRequest();
                    searchRequest.setQuery(request.getContent());
                    searchRequest.setTopK(5);
                    
                    List<KnowledgeSearchResult> results = knowledgeBaseService.search(kbId, searchRequest);
                    
                    for (KnowledgeSearchResult kr : results) {
                        MessageResponse.SourceReference ref = new MessageResponse.SourceReference();
                        ref.setDocId(kr.getDocId());
                        ref.setTitle(kr.getTitle());
                        ref.setContent(kr.getContent());
                        ref.setScore(kr.getScore());
                        sources.add(ref);
                    }
                }
            }
            
            if (request.isEnableTools()) {
                List<ToolCall> toolCalls = detectToolCalls(request.getContent(), conversation);
                
                if (!toolCalls.isEmpty()) {
                    ToolExecutionContext toolContext = ToolExecutionContext.of(
                            conversation.getUserId(), conversation.getKbId());
                    toolContext.setConversationId(conversationId);
                    
                    List<ToolCallResult> toolResults = toolOrchestrator.executeToolCalls(toolCalls, toolContext);
                    
                    for (int i = 0; i < toolCalls.size(); i++) {
                        ToolCall tc = toolCalls.get(i);
                        ToolCallResult tcr = toolResults.get(i);
                        
                        MessageResponse.ToolExecution exec = new MessageResponse.ToolExecution();
                        exec.setToolName(tc.getName());
                        exec.setArguments(tc.getArguments());
                        exec.setResult(tcr.getToolResult().getData());
                        exec.setSuccess(tcr.isSuccess());
                        toolExecutions.add(exec);
                        
                        stats.incrementToolCalls();
                    }
                }
            }
            
            String responseContent = generateResponse(request.getContent(), history, sources, toolExecutions);
            response.setContent(responseContent);
            
            Message assistantMessage = Message.assistant(responseContent);
            assistantMessage.setMessageId(response.getMessageId());
            assistantMessage.setConversationId(conversationId);
            history.add(assistantMessage);
            
            stats.incrementAssistantMessages();
            
        } catch (Exception e) {
            log.error("Failed to process message", e);
            response.setContent("处理消息时发生错误: " + e.getMessage());
        }
        
        response.setSources(sources);
        response.setToolExecutions(toolExecutions);
        
        conversation.incrementMessageCount();
        conversation.setUpdatedAt(System.currentTimeMillis());
        
        trimHistory(history);
        
        // 记录交互反馈
        if (feedbackService != null) {
            Map<String, Object> context = new HashMap<>();
            context.put("userId", conversation.getUserId());
            context.put("conversationId", conversationId);
            context.put("enableRag", request.isEnableRag());
            context.put("enableTools", request.isEnableTools());
            context.put("sourceCount", sources.size());
            context.put("toolExecutionCount", toolExecutions.size());
            
            feedbackService.recordInteraction(conversationId, request.getContent(), response.getContent(), context);
            
            // 如果开启自动学习，触发知识库更新
            if (autoLearn && stats.getTotalMessages() % 10 == 0) {
                feedbackService.triggerKnowledgeBaseUpdate(conversation.getKbId());
            }
        }
        
        return response;
    }
    
    @Override
    public void sendMessageStream(String conversationId, MessageRequest request, StreamMessageHandler handler) {
        MessageResponse response = sendMessage(conversationId, request);
        handler.onContent(response.getContent());
        handler.onComplete(response);
    }
    
    @Override
    public List<Message> getHistory(String conversationId, int limit) {
        List<Message> history = messageHistory.get(conversationId);
        if (history == null) {
            return java.util.Collections.emptyList();
        }
        
        if (history.size() <= limit) {
            return new ArrayList<>(history);
        }
        
        return new ArrayList<>(history.subList(history.size() - limit, history.size()));
    }
    
    @Override
    public void clearHistory(String conversationId) {
        List<Message> history = messageHistory.get(conversationId);
        if (history != null) {
            history.clear();
        }
        
        Conversation conversation = conversations.get(conversationId);
        if (conversation != null) {
            conversation.setMessageCount(0);
        }
        
        log.info("History cleared for conversation: {}", conversationId);
    }
    
    @Override
    public ConversationStats getStats(String conversationId) {
        return statsMap.get(conversationId);
    }

    @Override
    public void recordToolCall(String conversationId, ToolCallResult result) {
        log.info("Recording tool call for conversation: {} - tool: {}", conversationId, result.getToolName());

        Conversation conversation = conversations.get(conversationId);
        if (conversation == null) {
            log.warn("Conversation not found: {}", conversationId);
            return;
        }

        // 创建工具调用日志
        String logId = "log_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        FunctionCallLog functionCallLog = new FunctionCallLog();
        functionCallLog.setLogId(logId);
        functionCallLog.setSessionId(conversationId);
        functionCallLog.setToolCallId(result.getToolCallId());
        functionCallLog.setToolName(result.getToolName());
        functionCallLog.setResult(result);
        functionCallLog.setExecutionTime(result.getExecutionTime());

        // 保存到内存
        toolCallLogs.computeIfAbsent(conversationId, k -> new ArrayList<>()).add(functionCallLog);

        // 保存到存储
        if (storageService != null) {
            storageService.saveToolCallLog(conversationId, functionCallLog);
        }

        // 记录审计日志
        if (auditService != null) {
            auditService.log(
                    conversation.getUserId(),
                    "TOOL_CALL",
                    result.getToolName(),
                    conversationId,
                    result.isSuccess() ? "SUCCESS" : "FAILURE",
                    "Tool call executed: " + result.getToolName() + ", success: " + result.isSuccess()
            );
        }

        // 如果开启自动学习，更新知识库
        if (autoLearn && result.isSuccess() && result.getToolResult() != null) {
            learnFromToolResult(conversation, result);
        }

        log.debug("Tool call recorded: {}", logId);
    }

    private void learnFromToolResult(Conversation conversation, ToolCallResult result) {
        // 从工具调用结果中学习
        // 实际应用中应该根据工具类型和结果内容决定如何更新知识库
        log.debug("Learning from tool result: {}", result.getToolName());

        // 示例：如果工具调用成功且返回了数据，可以考虑更新知识库
        if (result.getToolResult() != null && result.getToolResult().getData() != null) {
            // 这里可以实现具体的知识更新逻辑
            // 例如：将搜索结果添加到知识库
        }
    }

    @Override
    public List<FunctionCallLog> getToolCallHistory(String conversationId) {
        return getToolCallHistory(conversationId, Integer.MAX_VALUE);
    }

    @Override
    public List<FunctionCallLog> getToolCallHistory(String conversationId, int limit) {
        // 优先从存储服务获取
        if (storageService != null) {
            return storageService.getToolCallLogs(conversationId, limit);
        }

        // 从内存获取
        List<FunctionCallLog> logs = toolCallLogs.getOrDefault(conversationId, new ArrayList<>());
        if (logs.size() <= limit) {
            return new ArrayList<>(logs);
        }
        return new ArrayList<>(logs.subList(logs.size() - limit, logs.size()));
    }

    @Override
    public LearnResult learnFromConversation(String conversationId) {
        log.info("Learning from conversation: {}", conversationId);

        Conversation conversation = conversations.get(conversationId);
        if (conversation == null) {
            return new LearnResult(0, new ArrayList<>(), "Conversation not found");
        }

        List<Message> history = messageHistory.get(conversationId);
        if (history == null || history.isEmpty()) {
            return new LearnResult(0, new ArrayList<>(), "No messages to learn from");
        }

        List<String> updatedIds = new ArrayList<>();
        int updatedCount = 0;

        // 从对话中提取知识
        for (Message message : history) {
            if (Message.ROLE_USER.equals(message.getRole())) {
                // 这里可以实现从用户消息中提取知识的逻辑
                // 例如：提取实体、事实、偏好等
                String content = message.getContent();
                if (content.length() >= 50) { // 最小内容长度限制
                    // 简化示例：将用户消息作为知识存储
                    // 实际应用中应该使用 NLP 技术提取结构化知识
                    updatedCount++;
                }
            }
        }

        String message = String.format("Learned from conversation, updated %d entries", updatedCount);
        log.info(message);

        return new LearnResult(updatedCount, updatedIds, message);
    }

    @Override
    public void setAutoLearn(boolean autoLearn) {
        this.autoLearn = autoLearn;
        log.info("Auto learn set to: {}", autoLearn);
    }

    @Override
    public boolean isAutoLearn() {
        return autoLearn;
    }

    @Override
    public Message chat(String conversationId, String content) {
        MessageRequest request = new MessageRequest(content);
        MessageResponse response = sendMessage(conversationId, request);

        Message assistantMessage = Message.assistant(response.getContent());
        assistantMessage.setMessageId(response.getMessageId());
        assistantMessage.setConversationId(conversationId);

        return assistantMessage;
    }

    @Override
    public Message chatWithTools(String conversationId, String content, List<String> toolNames) {
        MessageRequest request = new MessageRequest(content);
        request.setEnableTools(true);
        request.setSpecificTools(toolNames);

        MessageResponse response = sendMessage(conversationId, request);

        Message assistantMessage = Message.assistant(response.getContent());
        assistantMessage.setMessageId(response.getMessageId());
        assistantMessage.setConversationId(conversationId);

        return assistantMessage;
    }

    @Override
    public void chatStream(String conversationId, String content, StreamMessageHandler handler) {
        MessageRequest request = new MessageRequest(content);
        sendMessageStream(conversationId, request, handler);
    }

    @Override
    public ConversationAnalysis analyze(String conversationId) {
        log.info("Analyzing conversation: {}", conversationId);

        Conversation conversation = conversations.get(conversationId);
        if (conversation == null) {
            return null;
        }

        List<Message> history = messageHistory.get(conversationId);
        ConversationAnalysis analysis = new ConversationAnalysis(conversationId);

        if (history == null || history.isEmpty()) {
            analysis.setIntent("unknown");
            analysis.setSentiment("neutral");
            return analysis;
        }

        // 简化分析逻辑
        // 实际应用中应该使用 LLM 或 NLP 技术进行分析
        int userMsgCount = 0;
        int assistantMsgCount = 0;
        StringBuilder allContent = new StringBuilder();

        for (Message msg : history) {
            if (Message.ROLE_USER.equals(msg.getRole())) {
                userMsgCount++;
                allContent.append(msg.getContent()).append(" ");
            } else if (Message.ROLE_ASSISTANT.equals(msg.getRole())) {
                assistantMsgCount++;
            }
        }

        // 简单意图识别
        String content = allContent.toString().toLowerCase();
        if (content.contains("搜索") || content.contains("查找") || content.contains("查询")) {
            analysis.setIntent("search");
        } else if (content.contains("帮助") || content.contains("怎么") || content.contains("如何")) {
            analysis.setIntent("help");
        } else {
            analysis.setIntent("chat");
        }

        // 简单情感分析
        if (content.contains("好") || content.contains("谢谢") || content.contains("赞")) {
            analysis.setSentiment("positive");
        } else if (content.contains("差") || content.contains("坏") || content.contains("错误")) {
            analysis.setSentiment("negative");
        } else {
            analysis.setSentiment("neutral");
        }

        analysis.setTopic("general");
        analysis.setConfidence(0.7);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("userMessageCount", userMsgCount);
        metadata.put("assistantMessageCount", assistantMsgCount);
        metadata.put("totalMessages", history.size());
        analysis.setMetadata(metadata);

        return analysis;
    }

    private List<ToolCall> detectToolCalls(String content, Conversation conversation) {
        List<ToolCall> toolCalls = new ArrayList<>();
        
        if (content.contains("搜索") || content.contains("查找") || content.contains("检索")) {
            if (toolRegistry.hasTool("search_knowledge")) {
                Map<String, Object> args = new HashMap<>();
                args.put("kbId", conversation.getKbId());
                args.put("query", extractQuery(content));
                args.put("topK", 5);
                
                toolCalls.add(new ToolCall("tc_" + System.currentTimeMillis(), "search_knowledge", args));
            }
        }
        
        return toolCalls;
    }
    
    private String extractQuery(String content) {
        String[] keywords = {"搜索", "查找", "检索", "查询", "找"};
        for (String keyword : keywords) {
            int idx = content.indexOf(keyword);
            if (idx >= 0) {
                return content.substring(idx + keyword.length()).trim();
            }
        }
        return content;
    }
    
    private String generateResponse(String query, List<Message> history,
                                    List<MessageResponse.SourceReference> sources,
                                    List<MessageResponse.ToolExecution> toolExecutions) {
        // 构建系统提示词
        StringBuilder systemPrompt = new StringBuilder();
        systemPrompt.append("你是一个智能助手，能够回答用户的问题。");
        
        // 添加知识库检索结果到上下文
        if (!sources.isEmpty()) {
            systemPrompt.append("\n\n参考信息：\n");
            for (int i = 0; i < Math.min(3, sources.size()); i++) {
                MessageResponse.SourceReference ref = sources.get(i);
                systemPrompt.append("[").append(i + 1).append("] ")
                           .append(ref.getTitle()).append(": ")
                           .append(ref.getContent().substring(0, Math.min(200, ref.getContent().length())))
                           .append("\n");
            }
        }
        
        // 添加工具执行结果到上下文
        if (!toolExecutions.isEmpty()) {
            systemPrompt.append("\n工具执行结果：\n");
            for (MessageResponse.ToolExecution exec : toolExecutions) {
                systemPrompt.append("- ").append(exec.getToolName()).append(": ");
                if (exec.isSuccess()) {
                    systemPrompt.append(exec.getResult() != null ? exec.getResult() : "执行成功");
                } else {
                    systemPrompt.append("执行失败");
                }
                systemPrompt.append("\n");
            }
        }
        
        // 使用 LLM 生成响应
        try {
            SceneChatRequest request = new SceneChatRequest()
                .system(systemPrompt.toString())
                .user(query);
            
            // 添加历史消息
            for (Message msg : history) {
                if (msg.getRole().equals(Message.ROLE_USER)) {
                    request.user(msg.getContent());
                } else if (msg.getRole().equals(Message.ROLE_ASSISTANT)) {
                    request.assistant(msg.getContent());
                }
            }
            
            // 调用 LLM 服务
            net.ooder.sdk.llm.tool.ChatResponse response = llmService.chat(request);
            
            if (response != null && response.getContent() != null) {
                return response.getContent();
            }
        } catch (Exception e) {
            log.error("Failed to generate response using LLM: {}", e.getMessage(), e);
        }
        
        // 如果 LLM 调用失败，返回兜底响应
        return "抱歉，我暂时无法回答您的问题。请稍后再试。";
    }
    
    private void trimHistory(List<Message> history) {
        while (history.size() > MAX_HISTORY_LENGTH) {
            if (history.get(0).getRole().equals(Message.ROLE_SYSTEM)) {
                if (history.size() > 1) {
                    history.remove(1);
                } else {
                    break;
                }
            } else {
                history.remove(0);
            }
        }
    }
}
