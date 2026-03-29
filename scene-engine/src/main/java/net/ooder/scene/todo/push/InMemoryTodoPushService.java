package net.ooder.scene.todo.push;

import net.ooder.scene.todo.TodoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 内存实现的 Todo 推送服务
 *
 * <p>基于内存的简单推送实现，适用于单机部署。</p>
 * <p>生产环境可替换为 WebSocket 实现。</p>
 *
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class InMemoryTodoPushService implements TodoPushService {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryTodoPushService.class);

    private final Map<String, Set<Consumer<TodoPushMessage>>> listeners = new ConcurrentHashMap<>();

    @Override
    public void pushToUser(String userId, TodoDTO todo, String action) {
        if (userId == null) {
            return;
        }

        TodoPushMessage message = new TodoPushMessage(action, todo);
        Set<Consumer<TodoPushMessage>> userListeners = listeners.get(userId);

        if (userListeners != null) {
            for (Consumer<TodoPushMessage> listener : userListeners) {
                try {
                    listener.accept(message);
                } catch (Exception e) {
                    logger.warn("Error pushing todo to user: {}", userId, e);
                }
            }
        }

        logger.debug("Pushed todo {} to user: {}", action, userId);
    }

    @Override
    public void pushToSceneGroup(String sceneGroupId, TodoDTO todo, String action) {
        if (sceneGroupId == null) {
            return;
        }

        TodoPushMessage message = new TodoPushMessage(action, todo);
        message.setSceneGroupId(sceneGroupId);

        // 推送给场景组相关的所有监听者
        for (Map.Entry<String, Set<Consumer<TodoPushMessage>>> entry : listeners.entrySet()) {
            for (Consumer<TodoPushMessage> listener : entry.getValue()) {
                try {
                    listener.accept(message);
                } catch (Exception e) {
                    logger.warn("Error pushing todo to scene group: {}", sceneGroupId, e);
                }
            }
        }

        logger.debug("Pushed todo {} to scene group: {}", action, sceneGroupId);
    }

    /**
     * 订阅用户推送
     *
     * @param userId 用户ID
     * @param listener 消息监听器
     */
    public void subscribe(String userId, Consumer<TodoPushMessage> listener) {
        listeners.computeIfAbsent(userId, k -> new CopyOnWriteArraySet<>()).add(listener);
        logger.debug("Subscribed push listener for user: {}", userId);
    }

    /**
     * 取消订阅
     *
     * @param userId 用户ID
     * @param listener 消息监听器
     */
    public void unsubscribe(String userId, Consumer<TodoPushMessage> listener) {
        Set<Consumer<TodoPushMessage>> userListeners = listeners.get(userId);
        if (userListeners != null) {
            userListeners.remove(listener);
        }
        logger.debug("Unsubscribed push listener for user: {}", userId);
    }
}
