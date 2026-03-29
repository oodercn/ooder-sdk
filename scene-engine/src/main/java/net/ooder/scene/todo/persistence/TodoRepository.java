package net.ooder.scene.todo.persistence;

import net.ooder.scene.core.PageResult;
import net.ooder.scene.todo.TodoDTO;
import net.ooder.scene.todo.TodoQuery;
import net.ooder.scene.todo.TodoStatus;
import net.ooder.scene.todo.TodoType;

import java.util.List;
import java.util.Map;

/**
 * Todo 持久化接口
 *
 * <p>提供待办数据的持久化能力，支持多种存储后端：</p>
 * <ul>
 *   <li>json - JSON文件存储（默认）</li>
 *   <li>memory - 内存存储（开发测试）</li>
 *   <li>jpa - JPA数据库存储（生产环境）</li>
 * </ul>
 *
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface TodoRepository {

    void initialize();

    void close();

    String getStorageType();

    void save(TodoDTO todo);

    void saveAll(List<TodoDTO> todos);

    TodoDTO findById(String todoId);

    List<TodoDTO> findByToUserId(String userId);

    List<TodoDTO> findByToUserIdAndStatus(String userId, TodoStatus status);

    List<TodoDTO> findBySceneGroupId(String sceneGroupId);

    List<TodoDTO> findAll();

    void deleteById(String todoId);

    void deleteAll(List<String> todoIds);

    long count();

    long countByToUserIdAndStatus(String userId, TodoStatus status);

    Map<String, Integer> countByType(String userId);

    default PageResult<TodoDTO> query(String userId, TodoQuery query) {
        List<TodoDTO> all = findByToUserId(userId);
        List<TodoDTO> filtered = all.stream()
                .filter(todo -> matchesQuery(todo, query))
                .sorted((a, b) -> Long.compare(
                        b.getCreateTime() != null ? b.getCreateTime() : 0,
                        a.getCreateTime() != null ? a.getCreateTime() : 0))
                .collect(java.util.stream.Collectors.toList());

        int pageNum = query != null ? query.getPageNum() : 1;
        int pageSize = query != null ? query.getPageSize() : 20;
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, filtered.size());

        List<TodoDTO> pageItems = start < filtered.size()
                ? filtered.subList(start, end)
                : java.util.Collections.emptyList();

        return new PageResult<>(pageItems, filtered.size(), pageNum, pageSize);
    }

    default boolean matchesQuery(TodoDTO todo, TodoQuery query) {
        if (query == null) {
            return true;
        }

        if (query.getTypes() != null && !query.getTypes().isEmpty()) {
            if (!query.getTypes().contains(todo.getType())) {
                return false;
            }
        }

        if (query.getStatuses() != null && !query.getStatuses().isEmpty()) {
            if (!query.getStatuses().contains(todo.getStatus())) {
                return false;
            }
        }

        if (query.getSceneGroupId() != null && !query.getSceneGroupId().equals(todo.getSceneGroupId())) {
            return false;
        }

        if (query.getFromUserId() != null && !query.getFromUserId().equals(todo.getFromUserId())) {
            return false;
        }

        if (query.getPriority() != null && !query.getPriority().equals(todo.getPriority())) {
            return false;
        }

        if (Boolean.TRUE.equals(query.getExpired()) && !todo.isExpired()) {
            return false;
        }

        if (Boolean.FALSE.equals(query.getExpired()) && todo.isExpired()) {
            return false;
        }

        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            String keyword = query.getKeyword().toLowerCase();
            boolean matches = (todo.getTitle() != null && todo.getTitle().toLowerCase().contains(keyword)) ||
                    (todo.getDescription() != null && todo.getDescription().toLowerCase().contains(keyword));
            if (!matches) {
                return false;
            }
        }

        return true;
    }
}
