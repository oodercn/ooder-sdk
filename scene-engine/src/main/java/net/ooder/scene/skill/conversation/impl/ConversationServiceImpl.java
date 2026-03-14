package net.ooder.scene.skill.conversation.impl;

import net.ooder.scene.llm.LlmService;
import net.ooder.scene.llm.SceneChatRequest;
import net.ooder.scene.skill.conversation.*;
import net.ooder.scene.skill.knowledge.KnowledgeBaseService;
import net.ooder.scene.skill.knowledge.KnowledgeSearchRequest;
import net.ooder.scene.skill.knowledge.KnowledgeSearchResult;
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
    
    private final Map<String, Conversation> conversations = new ConcurrentHashMap<>();
    private final Map<String, List<Message>> messageHistory = new ConcurrentHashMap<>();
    private final Map<String, ConversationStats> statsMap = new ConcurrentHashMap<>();
    
    public ConversationServiceImpl(KnowledgeBaseService knowledgeBaseService,
                                    RagApi ragPipeline,
                                    ToolRegistry toolRegistry,
                                    ToolOrchestrator toolOrchestrator,
                                    LlmService llmService) {
        this.knowledgeBaseService = knowledgeBaseService;
        this.ragPipeline = ragPipeline;
        this.toolRegistry = toolRegistry;
        this.toolOrchestrator = toolOrchestrator;
        this.llmService = llmService;
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
