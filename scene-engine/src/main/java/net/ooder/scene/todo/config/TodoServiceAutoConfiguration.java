package net.ooder.scene.todo.config;

import net.ooder.scene.event.SceneEventPublisher;
import net.ooder.scene.todo.TodoService;
import net.ooder.scene.todo.TodoServiceImpl;
import net.ooder.scene.todo.persistence.InMemoryTodoRepository;
import net.ooder.scene.todo.persistence.JsonTodoRepository;
import net.ooder.scene.todo.persistence.TodoRepository;
import net.ooder.scene.todo.push.InMemoryTodoPushService;
import net.ooder.scene.todo.push.TodoPushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Todo 服务自动配置类
 *
 * <p>自动注入 TodoService Bean，支持通过 application.yml 配置。</p>
 *
 * <h3>配置示例：</h3>
 * <pre>
 * ooder:
 *   scene:
 *     todo:
 *       enabled: true
 *       storage:
 *         type: json
 *         path: ./data/todos
 * </pre>
 *
 * <h3>使用示例：</h3>
 * <pre>
 * &#64;Autowired
 * private TodoService todoService;
 *
 * // 创建邀请待办
 * InvitationTodoRequest request = new InvitationTodoRequest();
 * request.setSceneGroupId("sg-001");
 * request.setToUserId("user-001");
 * TodoDTO todo = todoService.createInvitationTodo(request);
 * </pre>
 *
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "ooder.scene.todo", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(TodoProperties.class)
public class TodoServiceAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(TodoServiceAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(TodoRepository.class)
    public TodoRepository todoRepository(TodoProperties properties) {
        String type = properties.getStorage().getType();
        String path = properties.getStorage().getPath();
        
        TodoRepository repository;
        if ("memory".equals(type)) {
            repository = new InMemoryTodoRepository();
        } else {
            repository = new JsonTodoRepository(path);
        }
        
        repository.initialize();
        logger.info("TodoRepository initialized with type: {}, path: {}", type, path);
        
        return repository;
    }

    @Bean
    @ConditionalOnMissingBean(TodoPushService.class)
    public TodoPushService todoPushService() {
        logger.info("TodoPushService initialized (InMemory)");
        return new InMemoryTodoPushService();
    }

    @Bean
    @ConditionalOnBean({SceneEventPublisher.class, TodoRepository.class})
    @ConditionalOnMissingBean(TodoService.class)
    public TodoService todoService(TodoRepository repository, 
                                    SceneEventPublisher eventPublisher,
                                    @Autowired(required = false) TodoPushService pushService) {
        TodoServiceImpl todoService = new TodoServiceImpl(repository, eventPublisher);
        
        if (pushService != null) {
            todoService.setPushService(pushService);
        }
        
        logger.info("TodoService initialized with repository type: {}", repository.getStorageType());
        
        return todoService;
    }
}
