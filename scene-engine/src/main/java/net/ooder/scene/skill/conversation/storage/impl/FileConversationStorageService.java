package net.ooder.scene.skill.conversation.storage.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.ooder.scene.skill.conversation.Conversation;
import net.ooder.scene.skill.conversation.FunctionCallLog;
import net.ooder.scene.skill.conversation.Message;
import net.ooder.scene.skill.conversation.storage.ConversationStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文件存储实现
 *
 * <p>使用 JSON 文件存储对话数据</p>
 *
 * <p>架构层次：存储层</p>
 *
 * @author ooder
 * @since 2.3
 */
public class FileConversationStorageService implements ConversationStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileConversationStorageService.class);

    private final ObjectMapper objectMapper;
    private final String storagePath;
    private final Path conversationsDir;
    private final Path messagesDir;
    private final Path toolCallsDir;
    private final Path contextDir;

    public FileConversationStorageService(String storagePath) {
        this.storagePath = storagePath;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        this.conversationsDir = Paths.get(storagePath, "conversations");
        this.messagesDir = Paths.get(storagePath, "messages");
        this.toolCallsDir = Paths.get(storagePath, "toolcalls");
        this.contextDir = Paths.get(storagePath, "context");
    }

    @Override
    public void initialize() {
        try {
            Files.createDirectories(conversationsDir);
            Files.createDirectories(messagesDir);
            Files.createDirectories(toolCallsDir);
            Files.createDirectories(contextDir);
            log.info("File conversation storage initialized at: {}", storagePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize storage directories", e);
        }
    }

    @Override
    public void shutdown() {
        log.info("File conversation storage shutdown");
    }

    @Override
    public void saveConversation(Conversation conversation) {
        try {
            Path filePath = conversationsDir.resolve(conversation.getConversationId() + ".json");
            objectMapper.writeValue(filePath.toFile(), conversation);
            log.debug("Saved conversation: {}", conversation.getConversationId());
        } catch (IOException e) {
            log.error("Failed to save conversation: {}", conversation.getConversationId(), e);
            throw new RuntimeException("Failed to save conversation", e);
        }
    }

    @Override
    public Conversation getConversation(String conversationId) {
        try {
            Path filePath = conversationsDir.resolve(conversationId + ".json");
            if (!Files.exists(filePath)) {
                return null;
            }
            return objectMapper.readValue(filePath.toFile(), Conversation.class);
        } catch (IOException e) {
            log.error("Failed to load conversation: {}", conversationId, e);
            return null;
        }
    }

    @Override
    public void deleteConversation(String conversationId) {
        try {
            Path convPath = conversationsDir.resolve(conversationId + ".json");
            Path msgPath = messagesDir.resolve(conversationId + ".json");
            Path toolPath = toolCallsDir.resolve(conversationId + ".json");

            Files.deleteIfExists(convPath);
            Files.deleteIfExists(msgPath);
            Files.deleteIfExists(toolPath);

            log.info("Deleted conversation: {}", conversationId);
        } catch (IOException e) {
            log.error("Failed to delete conversation: {}", conversationId, e);
        }
    }

    @Override
    public List<Conversation> listConversations(String userId) {
        try {
            if (!Files.exists(conversationsDir)) {
                return Collections.emptyList();
            }

            return Files.list(conversationsDir)
                    .filter(path -> path.toString().endsWith(".json"))
                    .map(path -> {
                        try {
                            return objectMapper.readValue(path.toFile(), Conversation.class);
                        } catch (IOException e) {
                            log.error("Failed to load conversation from: {}", path, e);
                            return null;
                        }
                    })
                    .filter(conv -> conv != null && userId.equals(conv.getUserId()))
                    .sorted((a, b) -> Long.compare(b.getUpdatedAt(), a.getUpdatedAt()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Failed to list conversations for user: {}", userId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public void saveMessage(String conversationId, Message message) {
        try {
            List<Message> messages = getMessages(conversationId);
            messages.add(message);

            Path filePath = messagesDir.resolve(conversationId + ".json");
            objectMapper.writeValue(filePath.toFile(), messages);
            log.debug("Saved message to conversation: {}", conversationId);
        } catch (IOException e) {
            log.error("Failed to save message: {}", conversationId, e);
            throw new RuntimeException("Failed to save message", e);
        }
    }

    @Override
    public List<Message> getMessages(String conversationId) {
        try {
            Path filePath = messagesDir.resolve(conversationId + ".json");
            if (!Files.exists(filePath)) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(filePath.toFile(), new TypeReference<List<Message>>() {});
        } catch (IOException e) {
            log.error("Failed to load messages: {}", conversationId, e);
            return new ArrayList<>();
        }
    }

    @Override
    public void clearMessages(String conversationId) {
        try {
            Path filePath = messagesDir.resolve(conversationId + ".json");
            Files.deleteIfExists(filePath);
            log.info("Cleared messages for conversation: {}", conversationId);
        } catch (IOException e) {
            log.error("Failed to clear messages: {}", conversationId, e);
        }
    }

    @Override
    public void saveToolCallLog(String conversationId, FunctionCallLog functionCallLog) {
        try {
            List<FunctionCallLog> logs = getToolCallLogs(conversationId);
            logs.add(functionCallLog);

            Path filePath = toolCallsDir.resolve(conversationId + ".json");
            objectMapper.writeValue(filePath.toFile(), logs);
            log.debug("Saved tool call log to conversation: {}", conversationId);
        } catch (IOException e) {
            log.error("Failed to save tool call log: {}", conversationId, e);
            throw new RuntimeException("Failed to save tool call log", e);
        }
    }

    @Override
    public List<FunctionCallLog> getToolCallLogs(String conversationId) {
        try {
            Path filePath = toolCallsDir.resolve(conversationId + ".json");
            if (!Files.exists(filePath)) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(filePath.toFile(), new TypeReference<List<FunctionCallLog>>() {});
        } catch (IOException e) {
            log.error("Failed to load tool call logs: {}", conversationId, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<FunctionCallLog> getToolCallLogs(String conversationId, int limit) {
        List<FunctionCallLog> logs = getToolCallLogs(conversationId);
        if (logs.size() <= limit) {
            return logs;
        }
        return logs.subList(logs.size() - limit, logs.size());
    }

    @Override
    public void saveContext(String key, Map<String, Object> data) {
        try {
            Path filePath = contextDir.resolve(key + ".json");
            objectMapper.writeValue(filePath.toFile(), data);
            log.debug("Saved context: {}", key);
        } catch (IOException e) {
            log.error("Failed to save context: {}", key, e);
            throw new RuntimeException("Failed to save context", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> loadContext(String key) {
        try {
            Path filePath = contextDir.resolve(key + ".json");
            if (!Files.exists(filePath)) {
                return null;
            }
            return objectMapper.readValue(filePath.toFile(), Map.class);
        } catch (IOException e) {
            log.error("Failed to load context: {}", key, e);
            return null;
        }
    }

    @Override
    public void deleteContext(String key) {
        try {
            Path filePath = contextDir.resolve(key + ".json");
            Files.deleteIfExists(filePath);
            log.debug("Deleted context: {}", key);
        } catch (IOException e) {
            log.error("Failed to delete context: {}", key, e);
        }
    }
}
