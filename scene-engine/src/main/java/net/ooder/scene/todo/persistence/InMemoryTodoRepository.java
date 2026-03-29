package net.ooder.scene.todo.persistence;

import net.ooder.scene.todo.TodoDTO;
import net.ooder.scene.todo.TodoStatus;
import net.ooder.scene.todo.TodoType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 内存存储的 Todo Repository 实现（用于测试）
 *
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class InMemoryTodoRepository implements TodoRepository {

    private static final String TYPE = "memory";

    private final Map<String, TodoDTO> todoCache = new ConcurrentHashMap<>();

    @Override
    public void initialize() {
        // nothing to do
    }

    @Override
    public void close() {
        todoCache.clear();
    }

    @Override
    public String getStorageType() {
        return TYPE;
    }

    @Override
    public void save(TodoDTO todo) {
        todoCache.put(todo.getId(), todo);
    }

    @Override
    public void saveAll(List<TodoDTO> todos) {
        for (TodoDTO todo : todos) {
            save(todo);
        }
    }

    @Override
    public TodoDTO findById(String todoId) {
        return todoCache.get(todoId);
    }

    @Override
    public List<TodoDTO> findByToUserId(String userId) {
        return todoCache.values().stream()
                .filter(t -> userId.equals(t.getToUserId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<TodoDTO> findByToUserIdAndStatus(String userId, TodoStatus status) {
        return todoCache.values().stream()
                .filter(t -> userId.equals(t.getToUserId()))
                .filter(t -> status == t.getStatus())
                .collect(Collectors.toList());
    }

    @Override
    public List<TodoDTO> findBySceneGroupId(String sceneGroupId) {
        return todoCache.values().stream()
                .filter(t -> sceneGroupId.equals(t.getSceneGroupId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<TodoDTO> findAll() {
        return new ArrayList<>(todoCache.values());
    }

    @Override
    public void deleteById(String todoId) {
        todoCache.remove(todoId);
    }

    @Override
    public void deleteAll(List<String> todoIds) {
        for (String todoId : todoIds) {
            todoCache.remove(todoId);
        }
    }

    @Override
    public long count() {
        return todoCache.size();
    }

    @Override
    public long countByToUserIdAndStatus(String userId, TodoStatus status) {
        return todoCache.values().stream()
                .filter(t -> userId.equals(t.getToUserId()))
                .filter(t -> status == t.getStatus())
                .count();
    }

    @Override
    public Map<String, Integer> countByType(String userId) {
        Map<String, Integer> counts = new HashMap<>();

        todoCache.values().stream()
                .filter(t -> userId.equals(t.getToUserId()))
                .filter(t -> t.getStatus() == TodoStatus.PENDING)
                .forEach(t -> {
                    String type = t.getType() != null ? t.getType().getCode() : "unknown";
                    counts.merge(type, 1, Integer::sum);
                });

        return counts;
    }
}
