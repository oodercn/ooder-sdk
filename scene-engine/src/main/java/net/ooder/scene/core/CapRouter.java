package net.ooder.scene.core;

import net.ooder.scene.event.SceneEventPublisher;
import net.ooder.scene.event.capability.CapabilityEvent;
import net.ooder.sdk.api.capability.CapRegistry;
import net.ooder.sdk.api.capability.Capability;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CAP 能力路由器
 * 
 * <p>负责将能力请求路由到对应的处理器，是能力调用的核心组件。</p>
 * 
 * <h3>核心功能：</h3>
 * <ul>
 *   <li>能力注册表管理 - 维护能力ID到处理器的映射</li>
 *   <li>请求路由分发 - 根据能力ID将请求路由到对应处理器</li>
 *   <li>事件发布 - 发布能力调用事件用于审计和监控</li>
 *   <li>默认处理 - 提供默认处理器兜底</li>
 * </ul>
 * 
 * <h3>使用示例：</h3>
 * <pre>
 * // 注册处理器
 * capRouter.registerHandler("40", new MessageHandler());
 * 
 * // 路由请求
 * CapResponse response = capRouter.routeRequest("40", capRequest);
 * </pre>
 * 
 * @author Ooder Team
 * @version 2.3
 * @since 0.7.0
 * @see CapRegistry
 * @see CapRequest
 * @see CapResponse
 */
@Component
public class CapRouter {

    /** 能力注册表 */
    private final CapRegistry registry;

    /** 能力处理器映射表 - 能力ID -> 处理器 */
    private final Map<String, CapHandler> handlers = new ConcurrentHashMap<>();

    /** 事件发布器 */
    private SceneEventPublisher eventPublisher;

    /**
     * 构造器
     * 
     * @param registry 能力注册表
     */
    @Autowired
    public CapRouter(CapRegistry registry) {
        this.registry = registry;
    }

    /**
     * 初始化方法
     */
    @PostConstruct
    public void init() {
        // 初始化默认处理器
    }

    /**
     * 设置事件发布器
     * 
     * @param eventPublisher 事件发布器
     */
    @Autowired(required = false)
    public void setEventPublisher(SceneEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * 注册能力处理器
     * 
     * @param capId 能力ID
     * @param handler 能力处理器
     */
    public void registerHandler(String capId, CapHandler handler) {
        handlers.put(capId, handler);
    }

    /**
     * 注销能力处理器
     * 
     * @param capId 能力ID
     */
    public void unregisterHandler(String capId) {
        handlers.remove(capId);
    }

    /**
     * 路由能力请求
     * 
     * <p>根据能力ID查找对应的处理器并执行，同时发布能力调用事件。</p>
     * 
     * @param capId 能力ID
     * @param request 能力请求
     * @return 能力响应
     */
    public CapResponse routeRequest(String capId, CapRequest request) {
        // 检查能力是否存在
        if (!registry.hasCapability(capId)) {
            publishCapabilityEvent(CapabilityEvent.invocationFailed(this, capId, 
                request.getRequestId(), "Capability not found"));
            return CapResponse.failure(request.getRequestId(), capId, "Capability not found");
        }

        // 查找处理器
        CapHandler handler = handlers.get(capId);
        CapResponse response;
        
        if (handler != null) {
            response = handler.handle(request);
        } else {
            response = handleDefault(request);
        }
        
        // 发布事件
        if (response.isSuccess()) {
            Capability capability = registry.findById(capId);
            String capName = capability != null ? capability.getName() : capId;
            publishCapabilityEvent(CapabilityEvent.invoked(this, capId, capName, request.getRequestId()));
        } else {
            publishCapabilityEvent(CapabilityEvent.invocationFailed(this, capId, 
                request.getRequestId(), response.getErrorMessage()));
        }
        
        return response;
    }

    /**
     * 默认处理器
     * 
     * <p>当找不到对应处理器时执行的默认逻辑。</p>
     * 
     * @param request 能力请求
     * @return 默认响应
     */
    private CapResponse handleDefault(CapRequest request) {
        return CapResponse.success(request.getRequestId(), request.getCapId(), "Default handler executed");
    }
    
    /**
     * 发布能力事件
     * 
     * @param event 能力事件
     */
    private void publishCapabilityEvent(CapabilityEvent event) {
        if (eventPublisher != null) {
            eventPublisher.publish(event);
        }
    }

    /**
     * 获取已注册的能力ID列表
     * 
     * @return 能力ID列表
     */
    public Map<String, CapHandler> getRegisteredHandlers() {
        return new ConcurrentHashMap<>(handlers);
    }

    // ==================== 内部接口 ====================

    /**
     * 能力处理器接口
     * 
     * <p>实现此接口以处理特定的能力请求。</p>
     */
    public interface CapHandler {
        
        /**
         * 处理能力请求
         * 
         * @param request 能力请求
         * @return 能力响应
         */
        CapResponse handle(CapRequest request);
    }
}
