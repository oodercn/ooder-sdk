package net.ooder.scene.todo;

import net.ooder.scene.core.PageResult;
import net.ooder.scene.event.SceneEventPublisher;
import net.ooder.scene.todo.persistence.TodoRepository;
import net.ooder.scene.todo.push.TodoPushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

/**
 * 待办服务实现
 * 
 * <p>基于 TodoRepository 存储抽象的待办服务实现。</p>
 * 
 * <h3>支持的存储类型：</h3>
 * <ul>
 *   <li>json - JSON 文件存储</li>
 *   <li>memory - 内存存储（测试用）</li>
 *   <li>jpa - 数据库存储（可扩展）</li>
 * </ul>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class TodoServiceImpl implements TodoService {
    
    private static final Logger logger = LoggerFactory.getLogger(TodoServiceImpl.class);
    
    private final TodoRepository repository;
    private final SceneEventPublisher eventPublisher;
    private final Map<String, Set<TodoChangeListener>> listeners = new ConcurrentHashMap<>();
    private TodoPushService pushService;
    
    private int maxSize = 10000;
    private int expireDays = 30;
    
    public TodoServiceImpl(TodoRepository repository, SceneEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        logger.info("TodoService initialized with repository type: {}", repository.getStorageType());
    }
    
    public void setPushService(TodoPushService pushService) {
        this.pushService = pushService;
    }
    
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
    
    public void setExpireDays(int expireDays) {
        this.expireDays = expireDays;
    }
    
    private String generateId() {
        return "todo-" + System.currentTimeMillis() + "-" + 
               Integer.toHexString((int)(Math.random() * 0xFFFF));
    }
    
    @Override
    public TodoDTO createInvitationTodo(InvitationTodoRequest request) {
        return createTodo(request.toTodoDTO());
    }
    
    @Override
    public TodoDTO createDelegationTodo(DelegationTodoRequest request) {
        return createTodo(request.toTodoDTO());
    }
    
    @Override
    public TodoDTO createApprovalTodo(ApprovalTodoRequest request) {
        return createTodo(request.toTodoDTO());
    }
    
    @Override
    public TodoDTO createReminderTodo(ReminderTodoRequest request) {
        return createTodo(request.toTodoDTO());
    }
    
    @Override
    public TodoDTO createActivationTodo(ActivationTodoRequest request) {
        return createTodo(request.toTodoDTO());
    }
    
    @Override
    public TodoDTO createSceneNotificationTodo(SceneNotificationRequest request) {
        return createTodo(request.toTodoDTO());
    }
    
    @Override
    public TodoDTO createTodo(TodoDTO todo) {
        if (todo.getId() == null) {
            todo.setId(generateId());
        }
        if (todo.getCreateTime() == null) {
            todo.setCreateTime(System.currentTimeMillis());
        }
        if (todo.getStatus() == null) {
            todo.setStatus(TodoStatus.PENDING);
        }
        
        repository.save(todo);
        notifyTodoCreated(todo);
        
        logger.info("Created todo: {} for user: {}", todo.getId(), todo.getToUserId());
        return todo;
    }
    
    @Override
    public PageResult<TodoDTO> listUserTodos(String userId, TodoQuery query) {
        return repository.query(userId, query);
    }
    
    @Override
    public TodoDTO getTodo(String todoId) {
        return repository.findById(todoId);
    }
    
    @Override
    public Map<String, Integer> countByType(String userId) {
        return repository.countByType(userId);
    }
    
    @Override
    public PageResult<TodoDTO> listSceneGroupTodos(String sceneGroupId, TodoQuery query) {
        List<TodoDTO> sceneGroupTodos = repository.findBySceneGroupId(sceneGroupId).stream()
            .filter(t -> matchesQuery(t, query))
            .sorted(this::compareByQuery)
            .collect(Collectors.toList());
        
        return paginate(sceneGroupTodos, query);
    }
    
    private boolean matchesQuery(TodoDTO todo, TodoQuery query) {
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
    
    private int compareByQuery(TodoDTO a, TodoDTO b) {
        return Long.compare(b.getCreateTime() != null ? b.getCreateTime() : 0, 
                           a.getCreateTime() != null ? a.getCreateTime() : 0);
    }
    
    private PageResult<TodoDTO> paginate(List<TodoDTO> todos, TodoQuery query) {
        int pageNum = query != null ? query.getPageNum() : 1;
        int pageSize = query != null ? query.getPageSize() : 20;
        
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, todos.size());
        
        List<TodoDTO> pageItems = start < todos.size() ? 
            todos.subList(start, end) : Collections.emptyList();
        
        return new PageResult<>(pageItems, todos.size(), pageNum, pageSize);
    }
    
    @Override
    public int getPendingCount(String userId) {
        return (int) repository.countByToUserIdAndStatus(userId, TodoStatus.PENDING);
    }
    
    @Override
    public boolean acceptTodo(String userId, String todoId) {
        TodoDTO todo = repository.findById(todoId);
        if (todo == null) {
            return false;
        }
        
        if (!userId.equals(todo.getToUserId())) {
            return false;
        }
        
        if (!todo.getStatus().canAccept()) {
            return false;
        }
        
        TodoStatus oldStatus = todo.getStatus();
        todo.setStatus(TodoStatus.ACCEPTED);
        todo.setCompletedTime(System.currentTimeMillis());
        todo.setCompletedBy(userId);
        
        repository.save(todo);
        notifyTodoStatusChanged(todo, oldStatus, TodoStatus.ACCEPTED);
        
        logger.info("Todo accepted: {} by user: {}", todoId, userId);
        return true;
    }
    
    @Override
    public boolean rejectTodo(String userId, String todoId) {
        TodoDTO todo = repository.findById(todoId);
        if (todo == null) {
            return false;
        }
        
        if (!userId.equals(todo.getToUserId())) {
            return false;
        }
        
        if (!todo.getStatus().canReject()) {
            return false;
        }
        
        TodoStatus oldStatus = todo.getStatus();
        todo.setStatus(TodoStatus.REJECTED);
        todo.setCompletedTime(System.currentTimeMillis());
        todo.setCompletedBy(userId);
        
        repository.save(todo);
        notifyTodoStatusChanged(todo, oldStatus, TodoStatus.REJECTED);
        
        logger.info("Todo rejected: {} by user: {}", todoId, userId);
        return true;
    }
    
    @Override
    public boolean completeTodo(String userId, String todoId) {
        TodoDTO todo = repository.findById(todoId);
        if (todo == null) {
            return false;
        }
        
        if (!userId.equals(todo.getToUserId())) {
            return false;
        }
        
        if (!todo.getStatus().canComplete()) {
            return false;
        }
        
        TodoStatus oldStatus = todo.getStatus();
        todo.setStatus(TodoStatus.COMPLETED);
        todo.setCompletedTime(System.currentTimeMillis());
        todo.setCompletedBy(userId);
        
        repository.save(todo);
        notifyTodoStatusChanged(todo, oldStatus, TodoStatus.COMPLETED);
        
        logger.info("Todo completed: {} by user: {}", todoId, userId);
        return true;
    }
    
    @Override
    public boolean approveTodo(String userId, String todoId, boolean approved, String comment) {
        TodoDTO todo = repository.findById(todoId);
        if (todo == null) {
            return false;
        }
        
        if (!userId.equals(todo.getToUserId())) {
            return false;
        }
        
        if (!todo.getStatus().canApprove()) {
            return false;
        }
        
        TodoStatus oldStatus = todo.getStatus();
        TodoStatus newStatus = approved ? TodoStatus.APPROVED : TodoStatus.REJECTED;
        
        todo.setStatus(newStatus);
        todo.setCompletedTime(System.currentTimeMillis());
        todo.setCompletedBy(userId);
        if (comment != null) {
            todo.addExtra("approvalComment", comment);
        }
        
        repository.save(todo);
        notifyTodoStatusChanged(todo, oldStatus, newStatus);
        
        logger.info("Todo {} approved: {} by user: {}", approved ? "" : "not", todoId, userId);
        return true;
    }
    
    @Override
    public boolean cancelTodo(String todoId, String reason) {
        TodoDTO todo = repository.findById(todoId);
        if (todo == null) {
            return false;
        }
        
        if (todo.getStatus().isTerminal()) {
            return false;
        }
        
        TodoStatus oldStatus = todo.getStatus();
        todo.setStatus(TodoStatus.CANCELLED);
        todo.setCompletedTime(System.currentTimeMillis());
        if (reason != null) {
            todo.addExtra("cancelReason", reason);
        }
        
        repository.save(todo);
        notifyTodoStatusChanged(todo, oldStatus, TodoStatus.CANCELLED);
        
        logger.info("Todo cancelled: {} reason: {}", todoId, reason);
        return true;
    }
    
    @Override
    public boolean deleteTodo(String todoId) {
        TodoDTO todo = repository.findById(todoId);
        if (todo == null) {
            return false;
        }
        
        repository.deleteById(todoId);
        notifyTodoDeleted(todoId);
        
        logger.info("Todo deleted: {}", todoId);
        return true;
    }
    
    @Override
    public void subscribe(String userId, TodoChangeListener listener) {
        listeners.computeIfAbsent(userId, k -> new CopyOnWriteArraySet<>()).add(listener);
        logger.debug("Listener subscribed for user: {}", userId);
    }
    
    @Override
    public void unsubscribe(String userId, TodoChangeListener listener) {
        Set<TodoChangeListener> userListeners = listeners.get(userId);
        if (userListeners != null) {
            userListeners.remove(listener);
        }
        logger.debug("Listener unsubscribed for user: {}", userId);
    }
    
    @Override
    public int processExpiredTodos() {
        int count = 0;
        long now = System.currentTimeMillis();
        
        for (TodoDTO todo : repository.findAll()) {
            if (todo.getStatus() == TodoStatus.PENDING && todo.isExpired()) {
                TodoStatus oldStatus = todo.getStatus();
                todo.setStatus(TodoStatus.EXPIRED);
                todo.setCompletedTime(now);
                
                repository.save(todo);
                notifyTodoExpired(todo);
                notifyTodoStatusChanged(todo, oldStatus, TodoStatus.EXPIRED);
                count++;
            }
        }
        
        if (count > 0) {
            logger.info("Processed {} expired todos", count);
        }
        
        return count;
    }
    
    private void notifyTodoCreated(TodoDTO todo) {
        Set<TodoChangeListener> userListeners = listeners.get(todo.getToUserId());
        if (userListeners != null) {
            for (TodoChangeListener listener : userListeners) {
                try {
                    listener.onTodoCreated(todo);
                } catch (Exception e) {
                    logger.warn("Error notifying listener", e);
                }
            }
        }
        
        if (pushService != null) {
            pushService.pushTodoCreated(todo);
        }
    }
    
    private void notifyTodoStatusChanged(TodoDTO todo, TodoStatus oldStatus, TodoStatus newStatus) {
        Set<TodoChangeListener> userListeners = listeners.get(todo.getToUserId());
        if (userListeners != null) {
            for (TodoChangeListener listener : userListeners) {
                try {
                    listener.onTodoStatusChanged(todo, oldStatus, newStatus);
                } catch (Exception e) {
                    logger.warn("Error notifying listener", e);
                }
            }
        }
        
        if (pushService != null) {
            pushService.pushTodoStatusChanged(todo, oldStatus.getCode(), newStatus.getCode());
        }
    }
    
    private void notifyTodoDeleted(String todoId) {
        for (Set<TodoChangeListener> userListeners : listeners.values()) {
            for (TodoChangeListener listener : userListeners) {
                try {
                    listener.onTodoDeleted(todoId);
                } catch (Exception e) {
                    logger.warn("Error notifying listener", e);
                }
            }
        }
    }
    
    private void notifyTodoExpired(TodoDTO todo) {
        Set<TodoChangeListener> userListeners = listeners.get(todo.getToUserId());
        if (userListeners != null) {
            for (TodoChangeListener listener : userListeners) {
                try {
                    listener.onTodoExpired(todo);
                } catch (Exception e) {
                    logger.warn("Error notifying listener", e);
                }
            }
        }
        
        if (pushService != null) {
            pushService.pushTodoExpired(todo);
        }
    }
}
