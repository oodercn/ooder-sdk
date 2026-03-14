package net.ooder.scene.llm.context;

import net.ooder.scene.skill.conversation.storage.ConversationStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 持久化记忆上下文
 * <p>扩展 MemoryContext，添加对话历史持久化功能</p>
 *
 * <p>功能：</p>
 * <ul>
 *   <li>自动保存对话历史到存储服务</li>
 *   <li>支持从存储恢复对话历史</li>
 *   <li>支持长期记忆（跨会话）</li>
 *   <li>支持记忆摘要生成</li>
 * </ul>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class PersistentMemoryContext extends MemoryContext {

    private static final Logger log = LoggerFactory.getLogger(PersistentMemoryContext.class);

    private transient ConversationStorageService storageService;
    private boolean autoSave = true;
    private String storageKey;

    public PersistentMemoryContext() {
        super();
    }

    public PersistentMemoryContext(String sessionId) {
        super(sessionId);
        this.storageKey = "memory_" + sessionId;
    }

    public PersistentMemoryContext(String sessionId, ConversationStorageService storageService) {
        super(sessionId);
        this.storageService = storageService;
        this.storageKey = "memory_" + sessionId;
        loadFromStorage();
    }

    /**
     * 添加消息并自动保存
     */
    @Override
    public void addMessage(String role, String content) {
        super.addMessage(role, content);
        
        if (autoSave && storageService != null) {
            saveToStorage();
        }
    }

    /**
     * 添加带工具调用的消息
     */
    @Override
    public void addAssistantMessageWithTools(String content, List<Map<String, Object>> toolCalls) {
        super.addAssistantMessageWithTools(content, toolCalls);
        
        if (autoSave && storageService != null) {
            saveToStorage();
        }
    }

    /**
     * 添加工具结果消息
     */
    @Override
    public void addToolResultMessage(String toolCallId, String content) {
        super.addToolResultMessage(toolCallId, content);
        
        if (autoSave && storageService != null) {
            saveToStorage();
        }
    }

    /**
     * 保存到存储
     */
    public void saveToStorage() {
        if (storageService == null) {
            log.warn("Storage service not available, cannot save memory context");
            return;
        }

        try {
            Map<String, Object> data = new HashMap<>();
            data.put("sessionId", getSessionId());
            data.put("history", getHistory());
            data.put("summary", getSummary());
            data.put("maxHistoryLength", getMaxHistoryLength());
            data.put("timestamp", System.currentTimeMillis());

            storageService.saveContext(storageKey, data);
            log.debug("Memory context saved to storage: {}", storageKey);
        } catch (Exception e) {
            log.error("Failed to save memory context: {}", e.getMessage(), e);
        }
    }

    /**
     * 从存储加载
     */
    @SuppressWarnings("unchecked")
    public void loadFromStorage() {
        if (storageService == null) {
            log.debug("Storage service not available, starting with empty memory");
            return;
        }

        try {
            Map<String, Object> data = storageService.loadContext(storageKey);
            if (data != null) {
                List<Map<String, Object>> history = (List<Map<String, Object>>) data.get("history");
                if (history != null) {
                    setHistory(history);
                }

                Map<String, Object> summary = (Map<String, Object>) data.get("summary");
                if (summary != null) {
                    setSummary(summary);
                }

                Integer maxLength = (Integer) data.get("maxHistoryLength");
                if (maxLength != null) {
                    setMaxHistoryLength(maxLength);
                }

                log.info("Memory context loaded from storage: {} ({} messages)", 
                        storageKey, getMessageCount());
            }
        } catch (Exception e) {
            log.error("Failed to load memory context: {}", e.getMessage(), e);
        }
    }

    /**
     * 清除存储
     */
    public void clearStorage() {
        if (storageService == null) {
            return;
        }

        try {
            storageService.deleteContext(storageKey);
            log.info("Memory context cleared from storage: {}", storageKey);
        } catch (Exception e) {
            log.error("Failed to clear memory context: {}", e.getMessage(), e);
        }
    }

    /**
     * 设置存储服务
     */
    public void setStorageService(ConversationStorageService storageService) {
        this.storageService = storageService;
    }

    /**
     * 获取存储服务
     */
    public ConversationStorageService getStorageService() {
        return storageService;
    }

    /**
     * 是否自动保存
     */
    public boolean isAutoSave() {
        return autoSave;
    }

    /**
     * 设置自动保存
     */
    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
    }

    /**
     * 获取存储键
     */
    public String getStorageKey() {
        return storageKey;
    }

    /**
     * 设置存储键
     */
    public void setStorageKey(String storageKey) {
        this.storageKey = storageKey;
    }
}
