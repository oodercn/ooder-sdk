package net.ooder.scene.todo.persistence;

import net.ooder.scene.todo.TodoDTO;
import net.ooder.scene.todo.TodoStatus;
import net.ooder.scene.todo.TodoType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * JSON 文件存储的 Todo Repository 实现
 *
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class JsonTodoRepository implements TodoRepository {

    private static final String TYPE = "json";

    private final Path storagePath;
    private final Map<String, TodoDTO> todoCache = new ConcurrentHashMap<>();

    public JsonTodoRepository(String basePath) {
        this.storagePath = Paths.get(basePath, "todos");
    }

    @Override
    public void initialize() {
        try {
            Files.createDirectories(storagePath);
            loadAllTodos();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize TodoRepository", e);
        }
    }

    @Override
    public void close() {
        todoCache.clear();
    }

    @Override
    public String getStorageType() {
        return TYPE;
    }

    private void loadAllTodos() throws IOException {
        if (!Files.exists(storagePath)) {
            return;
        }

        Files.walk(storagePath)
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".json"))
                .forEach(this::loadTodoFromFile);
    }

    private void loadTodoFromFile(Path path) {
        try {
            String content = Files.readString(path);
            TodoDTO todo = parseTodoFromJson(content);
            if (todo != null && todo.getId() != null) {
                todoCache.put(todo.getId(), todo);
            }
        } catch (IOException e) {
            // ignore
        }
    }

    private TodoDTO parseTodoFromJson(String json) {
        try {
            Map<String, Object> map = parseJsonToMap(json);
            if (map == null) {
                return null;
            }

            TodoDTO dto = new TodoDTO();
            dto.setId((String) map.get("id"));
            dto.setType(TodoType.fromCode((String) map.get("type")));
            dto.setTitle((String) map.get("title"));
            dto.setDescription((String) map.get("description"));
            dto.setStatus(TodoStatus.fromCode((String) map.get("status")));
            dto.setPriority((String) map.get("priority"));
            dto.setSceneGroupId((String) map.get("sceneGroupId"));
            dto.setSceneGroupName((String) map.get("sceneGroupName"));
            dto.setFromUserId((String) map.get("fromUserId"));
            dto.setFromUserName((String) map.get("fromUserName"));
            dto.setToUserId((String) map.get("toUserId"));
            dto.setToUserName((String) map.get("toUserName"));
            dto.setRole((String) map.get("role"));
            dto.setInstallId((String) map.get("installId"));
            dto.setCapabilityId((String) map.get("capabilityId"));
            dto.setActionType((String) map.get("actionType"));

            if (map.get("deadline") != null) {
                dto.setDeadline(((Number) map.get("deadline")).longValue());
            }
            if (map.get("createTime") != null) {
                dto.setCreateTime(((Number) map.get("createTime")).longValue());
            }
            if (map.get("completedTime") != null) {
                dto.setCompletedTime(((Number) map.get("completedTime")).longValue());
            }

            dto.setCompletedBy((String) map.get("completedBy"));
            dto.setErrorMessage((String) map.get("errorMessage"));

            @SuppressWarnings("unchecked")
            Map<String, Object> extra = (Map<String, Object>) map.get("extra");
            if (extra != null) {
                dto.setExtra(extra);
            }

            return dto;
        } catch (Exception e) {
            return null;
        }
    }

    private Map<String, Object> parseJsonToMap(String json) {
        Map<String, Object> result = new HashMap<>();
        if (json == null || json.isEmpty()) {
            return result;
        }

        json = json.trim();
        if (!json.startsWith("{") || !json.endsWith("}")) {
            return result;
        }

        json = json.substring(1, json.length() - 1);
        String[] pairs = json.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        for (String pair : pairs) {
            int colonIndex = pair.indexOf(':');
            if (colonIndex > 0) {
                String key = pair.substring(0, colonIndex).trim().replace("\"", "");
                String value = pair.substring(colonIndex + 1).trim();

                if (value.startsWith("\"") && value.endsWith("\"")) {
                    result.put(key, value.substring(1, value.length() - 1));
                } else if ("null".equals(value)) {
                    result.put(key, null);
                } else if ("true".equals(value)) {
                    result.put(key, true);
                } else if ("false".equals(value)) {
                    result.put(key, false);
                } else {
                    try {
                        if (value.contains(".")) {
                            result.put(key, Double.parseDouble(value));
                        } else {
                            result.put(key, Long.parseLong(value));
                        }
                    } catch (NumberFormatException e) {
                        result.put(key, value);
                    }
                }
            }
        }

        return result;
    }

    private String todoToJson(TodoDTO todo) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"id\":\"").append(escapeJson(todo.getId())).append("\",");
        sb.append("\"type\":\"").append(todo.getType() != null ? todo.getType().getCode() : "").append("\",");
        sb.append("\"title\":\"").append(escapeJson(todo.getTitle())).append("\",");
        sb.append("\"description\":\"").append(escapeJson(todo.getDescription())).append("\",");
        sb.append("\"status\":\"").append(todo.getStatus() != null ? todo.getStatus().getCode() : "").append("\",");
        sb.append("\"priority\":\"").append(escapeJson(todo.getPriority())).append("\",");
        sb.append("\"sceneGroupId\":\"").append(escapeJson(todo.getSceneGroupId())).append("\",");
        sb.append("\"sceneGroupName\":\"").append(escapeJson(todo.getSceneGroupName())).append("\",");
        sb.append("\"fromUserId\":\"").append(escapeJson(todo.getFromUserId())).append("\",");
        sb.append("\"fromUserName\":\"").append(escapeJson(todo.getFromUserName())).append("\",");
        sb.append("\"toUserId\":\"").append(escapeJson(todo.getToUserId())).append("\",");
        sb.append("\"toUserName\":\"").append(escapeJson(todo.getToUserName())).append("\",");
        sb.append("\"role\":\"").append(escapeJson(todo.getRole())).append("\",");
        sb.append("\"installId\":\"").append(escapeJson(todo.getInstallId())).append("\",");
        sb.append("\"capabilityId\":\"").append(escapeJson(todo.getCapabilityId())).append("\",");
        sb.append("\"actionType\":\"").append(escapeJson(todo.getActionType())).append("\",");
        sb.append("\"deadline\":").append(todo.getDeadline() != null ? todo.getDeadline() : "null").append(",");
        sb.append("\"createTime\":").append(todo.getCreateTime() != null ? todo.getCreateTime() : "null").append(",");
        sb.append("\"completedTime\":").append(todo.getCompletedTime() != null ? todo.getCompletedTime() : "null").append(",");
        sb.append("\"completedBy\":\"").append(escapeJson(todo.getCompletedBy())).append("\",");
        sb.append("\"errorMessage\":\"").append(escapeJson(todo.getErrorMessage())).append("\",");
        sb.append("\"extra\":{}");
        sb.append("}");
        return sb.toString();
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private void saveTodoToFile(TodoDTO todo) {
        try {
            String userId = todo.getToUserId();
            if (userId == null) {
                userId = "unknown";
            }

            Path userDir = storagePath.resolve(userId);
            Files.createDirectories(userDir);

            Path filePath = userDir.resolve(todo.getId() + ".json");
            String json = todoToJson(todo);
            Files.writeString(filePath, json);
        } catch (IOException e) {
            // ignore
        }
    }

    private void deleteTodoFile(TodoDTO todo) {
        try {
            String userId = todo.getToUserId();
            if (userId == null) {
                userId = "unknown";
            }

            Path filePath = storagePath.resolve(userId).resolve(todo.getId() + ".json");
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // ignore
        }
    }

    @Override
    public void save(TodoDTO todo) {
        todoCache.put(todo.getId(), todo);
        saveTodoToFile(todo);
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
        TodoDTO todo = todoCache.remove(todoId);
        if (todo != null) {
            deleteTodoFile(todo);
        }
    }

    @Override
    public void deleteAll(List<String> todoIds) {
        for (String todoId : todoIds) {
            deleteById(todoId);
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
