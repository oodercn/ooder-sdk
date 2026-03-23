package net.ooder.scene.llm.context.impl;

import net.ooder.scene.llm.context.*;
import net.ooder.scene.llm.context.store.JsonContextStore;
import net.ooder.scene.llm.context.store.SessionData;
import net.ooder.scene.llm.context.store.SkillContextData;
import net.ooder.scene.skill.tool.Tool;
import net.ooder.scene.skill.tool.ToolRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

/**
 * 多级上下文管理器实现
 *
 * <p>支持上下文缓存、页面跳转重载、Session 级别会话管理</p>
 * <p>使用 JSON 文件存储 + 异步持久化</p>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class MultiLevelContextManagerImpl implements MultiLevelContextManager {

    private static final Logger log = LoggerFactory.getLogger(MultiLevelContextManagerImpl.class);
    private static final int MAX_HISTORY_LENGTH = 50;
    private static final String SESSION_PREFIX = "session:";
    private static final String SKILL_PREFIX = "skill:";

    private final ToolRegistry toolRegistry;
    private final JsonContextStore store;
    private final ExecutorService asyncExecutor;

    private GlobalContextConfig globalConfig;
    private final Map<String, SessionData> sessionCache = new ConcurrentHashMap<>();
    private final Map<String, SkillContextData> skillContextCache = new ConcurrentHashMap<>();
    private final Map<String, String> pageSkillMapping = new ConcurrentHashMap<>();

    private String currentPageId;
    private String currentSkillId;
    private final ThreadLocal<String> currentSessionId = new ThreadLocal<>();

    public MultiLevelContextManagerImpl(ToolRegistry toolRegistry) {
        this(toolRegistry, new JsonContextStore());
    }

    public MultiLevelContextManagerImpl(ToolRegistry toolRegistry, JsonContextStore store) {
        this.toolRegistry = toolRegistry;
        this.store = store;
        this.asyncExecutor = Executors.newFixedThreadPool(2, r -> {
            Thread t = new Thread(r, "context-async-worker");
            t.setDaemon(true);
            return t;
        });
        loadExistingData();
    }

    private void loadExistingData() {
        Set<String> keys = store.listKeys();
        for (String key : keys) {
            try {
                if (key.startsWith(SESSION_PREFIX)) {
                    store.load(key, SessionData.class).ifPresent(data -> {
                        sessionCache.put(key.substring(SESSION_PREFIX.length()), data);
                    });
                } else if (key.startsWith(SKILL_PREFIX)) {
                    store.load(key, SkillContextData.class).ifPresent(data -> {
                        skillContextCache.put(key.substring(SKILL_PREFIX.length()), data);
                    });
                }
            } catch (Exception e) {
                log.warn("Failed to load data for key: {}", key, e);
            }
        }
        log.info("Loaded {} sessions and {} skill contexts from store", 
                sessionCache.size(), skillContextCache.size());
    }

    @Override
    public void initializeGlobalContext(GlobalContextConfig config) {
        this.globalConfig = config;
        log.info("Global context initialized with {} menu items and {} global tools",
                config.getMenuItems().size(), config.getGlobalTools().size());
    }

    @Override
    public LlmContext getCurrentContext() {
        LlmContext context = LlmContext.create(ContextLevel.SESSION);
        if (globalConfig != null) {
            context.setSystemPrompt(globalConfig.getSystemBasePrompt());
            for (GlobalContextConfig.ToolDefinition tool : globalConfig.getGlobalTools()) {
                Map<String, Object> toolDef = new LinkedHashMap<>();
                toolDef.put("type", "function");
                Map<String, Object> function = new LinkedHashMap<>();
                function.put("name", tool.getName());
                function.put("description", tool.getDescription());
                function.put("parameters", tool.getParameters());
                toolDef.put("function", function);
                context.addTool(toolDef);
            }
        }
        String sessionId = currentSessionId.get();
        if (sessionId != null) {
            SessionData session = sessionCache.get(sessionId);
            if (session != null) {
                context.setMessages(new ArrayList<>(session.getHistory()));
                context.getVariables().putAll(session.getVariables());
            }
        }
        SkillActivationContext skillContext = getSkillContext(currentSkillId);
        if (skillContext != null) {
            String skillPrompt = skillContext.buildSystemPrompt();
            if (skillPrompt != null && !skillPrompt.isEmpty()) {
                String basePrompt = context.getSystemPrompt();
                if (basePrompt != null && !basePrompt.isEmpty()) {
                    context.setSystemPrompt(basePrompt + "\n\n" + skillPrompt);
                } else {
                    context.setSystemPrompt(skillPrompt);
                }
            }
            context.getTools().addAll(skillContext.getTools());
        }
        return context;
    }

    @Override
    public void reloadContextForPage(String pageId) {
        log.info("Reloading context for page: {}", pageId);
        this.currentPageId = pageId;
        String skillId = pageSkillMapping.get(pageId);
        if (skillId != null) {
            reloadContextForSkill(skillId);
        }
    }

    @Override
    public void reloadContextForSkill(String skillId) {
        log.info("Reloading context for skill: {}", skillId);
        this.currentSkillId = skillId;
        SkillActivationContext context = getSkillContext(skillId);
        if (context != null && context.getFunctionContext() != null) {
            List<Tool> tools = convertToTools(context.getFunctionContext());
            if (toolRegistry != null) {
                toolRegistry.injectFromSkill(skillId, tools);
            }
        }
    }

    @Override
    public ContextLevel getContextLevel() {
        return ContextLevel.SESSION;
    }

    @Override
    public void pushContextUpdate(ContextUpdate update) {
        log.debug("Pushing context update: {}", update.getType());
        switch (update.getType()) {
            case PAGE_CHANGE:
                reloadContextForPage(update.getTargetId());
                break;
            case SKILL_CHANGE:
                reloadContextForSkill(update.getTargetId());
                break;
            case STATE_UPDATE:
                updateState(update.getData());
                break;
            case CONTEXT_CLEAR:
                clearCurrentContext();
                break;
        }
    }

    @Override
    public List<Map<String, Object>> getConversationHistory(String sessionId) {
        if (sessionId == null) {
            return Collections.emptyList();
        }
        SessionData session = sessionCache.get(sessionId);
        return session != null ? new ArrayList<>(session.getHistory()) : Collections.emptyList();
    }

    @Override
    public void addMessage(String sessionId, Map<String, Object> message) {
        if (sessionId == null || message == null) {
            return;
        }
        SessionData session = sessionCache.computeIfAbsent(sessionId, 
            k -> SessionData.create(k, "unknown"));
        session.addMessage(message);
        if (session.getHistory().size() > MAX_HISTORY_LENGTH) {
            trimHistory(session, MAX_HISTORY_LENGTH);
        }
        persistSessionAsync(sessionId, session);
    }

    private void trimHistory(SessionData session, int maxLength) {
        List<Map<String, Object>> history = session.getHistory();
        if (history.size() > maxLength) {
            session.setHistory(new ArrayList<>(history.subList(history.size() - maxLength, history.size())));
        }
    }

    private void persistSessionAsync(String sessionId, SessionData session) {
        asyncExecutor.submit(() -> {
            try {
                store.saveSync(SESSION_PREFIX + sessionId, session);
                log.debug("Persisted session: {}", sessionId);
            } catch (Exception e) {
                log.error("Failed to persist session: {}", sessionId, e);
            }
        });
    }

    @Override
    public void clearSession(String sessionId) {
        if (sessionId != null) {
            sessionCache.remove(sessionId);
            store.deleteAsync(SESSION_PREFIX + sessionId);
            log.info("Session cleared: {}", sessionId);
        }
    }

    @Override
    public SkillActivationContext getSkillContext(String skillId) {
        if (skillId == null) {
            return null;
        }
        SkillContextData data = skillContextCache.get(skillId);
        if (data == null) {
            data = store.load(SKILL_PREFIX + skillId, SkillContextData.class).orElse(null);
            if (data != null) {
                skillContextCache.put(skillId, data);
            }
        }
        if (data == null) {
            return null;
        }
        return convertToActivationContext(data);
    }

    private SkillActivationContext convertToActivationContext(SkillContextData data) {
        return null;
    }

    @Override
    public void cacheSkillContext(String skillId, SkillActivationContext context) {
        if (skillId == null || context == null) {
            return;
        }
        SkillContextData data = convertToData(context);
        skillContextCache.put(skillId, data);
        persistSkillContextAsync(skillId, data);
        log.debug("Cached skill context: {}", skillId);
    }

    private SkillContextData convertToData(SkillActivationContext context) {
        SkillContextData data = SkillContextData.create(context.getSkillId());
        data.setSystemPrompt(context.buildSystemPrompt());
        return data;
    }

    private void persistSkillContextAsync(String skillId, SkillContextData data) {
        asyncExecutor.submit(() -> {
            try {
                store.saveSync(SKILL_PREFIX + skillId, data);
                log.debug("Persisted skill context: {}", skillId);
            } catch (Exception e) {
                log.error("Failed to persist skill context: {}", skillId, e);
            }
        });
    }

    @Override
    public void clearSkillContextCache(String skillId) {
        if (skillId != null) {
            skillContextCache.remove(skillId);
            store.deleteAsync(SKILL_PREFIX + skillId);
            log.debug("Cleared skill context cache: {}", skillId);
        }
    }

    @Override
    public String getSkillIdForPage(String pageId) {
        return pageSkillMapping.get(pageId);
    }

    @Override
    public void setPageSkillMapping(String pageId, String skillId) {
        pageSkillMapping.put(pageId, skillId);
        log.info("Set page-skill mapping: {} -> {}", pageId, skillId);
    }

    @Override
    public String getCurrentPageId() {
        return currentPageId;
    }

    @Override
    public void setCurrentPageId(String pageId) {
        this.currentPageId = pageId;
        if (pageId != null) {
            reloadContextForPage(pageId);
        }
    }

    @Override
    public String getCurrentSkillId() {
        return currentSkillId;
    }

    @Override
    public String getOrCreateSession(String userId) {
        String sessionId = "session-" + userId + "-" + System.currentTimeMillis();
        SessionData session = SessionData.create(sessionId, userId);
        sessionCache.put(sessionId, session);
        currentSessionId.set(sessionId);
        persistSessionAsync(sessionId, session);
        log.info("Created new session: {} for user: {}", sessionId, userId);
        return sessionId;
    }

    @Override
    public SkillActivationContext getSessionSkillContext(String sessionId) {
        SessionData session = sessionCache.get(sessionId);
        if (session != null && session.getSkillId() != null) {
            return getSkillContext(session.getSkillId());
        }
        return null;
    }

    private void updateState(Map<String, Object> data) {
        String sessionId = currentSessionId.get();
        if (sessionId != null) {
            SessionData session = sessionCache.get(sessionId);
            if (session != null) {
                session.getVariables().putAll(data);
                persistSessionAsync(sessionId, session);
            }
        }
    }

    private void clearCurrentContext() {
        String sessionId = currentSessionId.get();
        if (sessionId != null) {
            clearSession(sessionId);
        }
        currentSessionId.remove();
    }

    private List<Tool> convertToTools(FunctionContext functionContext) {
        List<Tool> tools = new ArrayList<>();
        return tools;
    }

    public void shutdown() {
        asyncExecutor.shutdown();
        store.shutdown();
        try {
            if (!asyncExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                asyncExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            asyncExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info("MultiLevelContextManager shutdown");
    }
}
