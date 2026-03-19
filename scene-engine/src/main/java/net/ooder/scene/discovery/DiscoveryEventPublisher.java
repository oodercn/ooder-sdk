package net.ooder.scene.discovery;

import net.ooder.scene.discovery.api.DiscoveryRequest;
import net.ooder.scene.discovery.api.DiscoveryResult;
import net.ooder.scene.event.SceneEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 发现事件发布器
 * 
 * <p>管理发现事件监听器的注册和事件发布，支持同步和异步事件处理。</p>
 * 
 * <h3>功能特性：</h3>
 * <ul>
 *   <li>监听器管理 - 注册、注销发现事件监听器</li>
 *   <li>事件发布 - 向所有监听器发布发现事件</li>
 *   <li>异步支持 - 支持异步监听器执行</li>
 *   <li>优先级排序 - 按优先级顺序调用监听器</li>
 *   <li>错误隔离 - 单个监听器错误不影响其他监听器</li>
 * </ul>
 * 
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 * @see DiscoveryEventListener
 * @see SceneEventPublisher
 */
@Component
public class DiscoveryEventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(DiscoveryEventPublisher.class);
    
    private final List<DiscoveryEventListener> listeners = new CopyOnWriteArrayList<>();
    private final ExecutorService asyncExecutor;
    private final SceneEventPublisher sceneEventPublisher;
    
    public DiscoveryEventPublisher(SceneEventPublisher sceneEventPublisher) {
        this.sceneEventPublisher = sceneEventPublisher;
        this.asyncExecutor = Executors.newCachedThreadPool(r -> {
            Thread thread = new Thread(r, "discovery-event-" + System.currentTimeMillis());
            thread.setDaemon(true);
            return thread;
        });
    }
    
    /**
     * 注册发现事件监听器
     *
     * @param listener 监听器
     */
    public void addListener(DiscoveryEventListener listener) {
        if (listener != null) {
            listeners.add(listener);
            // 按优先级排序
            listeners.sort((l1, l2) -> Integer.compare(l2.getPriority(), l1.getPriority()));
            logger.debug("注册发现事件监听器: {}, 优先级: {}", 
                listener.getClass().getSimpleName(), listener.getPriority());
        }
    }
    
    /**
     * 注销发现事件监听器
     *
     * @param listener 监听器
     */
    public void removeListener(DiscoveryEventListener listener) {
        if (listener != null) {
            listeners.remove(listener);
            logger.debug("注销发现事件监听器: {}", listener.getClass().getSimpleName());
        }
    }
    
    /**
     * 发布发现开始事件
     *
     * @param request 发现请求
     */
    public void publishDiscoveryStarted(DiscoveryRequest request) {
        logger.debug("发布发现开始事件: source={}", request.getSource());
        
        for (DiscoveryEventListener listener : listeners) {
            try {
                if (listener.isAsync()) {
                    asyncExecutor.submit(() -> {
                        try {
                            listener.onDiscoveryStarted(request);
                        } catch (Exception e) {
                            logger.error("异步监听器处理发现开始事件失败: {}", e.getMessage());
                        }
                    });
                } else {
                    listener.onDiscoveryStarted(request);
                }
            } catch (Exception e) {
                logger.error("监听器处理发现开始事件失败: {} - {}", 
                    listener.getClass().getSimpleName(), e.getMessage());
            }
        }
    }
    
    /**
     * 发布发现进度事件
     *
     * @param progress 进度信息
     */
    public void publishDiscoveryProgress(DiscoveryEventListener.DiscoveryProgress progress) {
        logger.debug("发布发现进度事件: {}", progress);
        
        for (DiscoveryEventListener listener : listeners) {
            try {
                if (listener.isAsync()) {
                    asyncExecutor.submit(() -> {
                        try {
                            listener.onDiscoveryProgress(progress);
                        } catch (Exception e) {
                            logger.error("异步监听器处理发现进度事件失败: {}", e.getMessage());
                        }
                    });
                } else {
                    listener.onDiscoveryProgress(progress);
                }
            } catch (Exception e) {
                logger.error("监听器处理发现进度事件失败: {} - {}", 
                    listener.getClass().getSimpleName(), e.getMessage());
            }
        }
    }
    
    /**
     * 发布能力发现事件
     *
     * @param capability 能力信息
     */
    public void publishCapabilityDiscovered(CapabilityDTO capability) {
        logger.debug("发布能力发现事件: {} ({})", capability.getId(), capability.getType());
        
        // 同时发布到 SceneEventPublisher
        if (sceneEventPublisher != null) {
            sceneEventPublisher.publishCapabilityEvent(
                net.ooder.scene.event.capability.CapabilityEvent.discovered(
                    this, capability.getId(), capability.getCapabilityAddress(), capability.getType())
            );
        }
        
        for (DiscoveryEventListener listener : listeners) {
            try {
                if (listener.isAsync()) {
                    asyncExecutor.submit(() -> {
                        try {
                            listener.onCapabilityDiscovered(capability);
                        } catch (Exception e) {
                            logger.error("异步监听器处理能力发现事件失败: {}", e.getMessage());
                        }
                    });
                } else {
                    listener.onCapabilityDiscovered(capability);
                }
            } catch (Exception e) {
                logger.error("监听器处理能力发现事件失败: {} - {}", 
                    listener.getClass().getSimpleName(), e.getMessage());
            }
        }
    }
    
    /**
     * 发布场景发现事件
     *
     * @param scene 场景信息
     */
    public void publishSceneDiscovered(CapabilityDTO scene) {
        logger.debug("发布场景发现事件: {}", scene.getId());
        
        // 同时发布到 SceneEventPublisher
        if (sceneEventPublisher != null) {
            sceneEventPublisher.publishCapabilityEvent(
                net.ooder.scene.event.capability.CapabilityEvent.discovered(
                    this, scene.getId(), scene.getCapabilityAddress(), "scene")
            );
        }
        
        for (DiscoveryEventListener listener : listeners) {
            try {
                if (listener.isAsync()) {
                    asyncExecutor.submit(() -> {
                        try {
                            listener.onSceneDiscovered(scene);
                        } catch (Exception e) {
                            logger.error("异步监听器处理场景发现事件失败: {}", e.getMessage());
                        }
                    });
                } else {
                    listener.onSceneDiscovered(scene);
                }
            } catch (Exception e) {
                logger.error("监听器处理场景发现事件失败: {} - {}", 
                    listener.getClass().getSimpleName(), e.getMessage());
            }
        }
    }
    
    /**
     * 发布技能发现事件
     *
     * @param skill 技能信息
     */
    public void publishSkillDiscovered(CapabilityDTO skill) {
        logger.debug("发布技能发现事件: {}", skill.getId());
        
        // 同时发布到 SceneEventPublisher
        if (sceneEventPublisher != null) {
            sceneEventPublisher.publishCapabilityEvent(
                net.ooder.scene.event.capability.CapabilityEvent.discovered(
                    this, skill.getId(), skill.getCapabilityAddress(), "skill")
            );
        }
        
        for (DiscoveryEventListener listener : listeners) {
            try {
                if (listener.isAsync()) {
                    asyncExecutor.submit(() -> {
                        try {
                            listener.onSkillDiscovered(skill);
                        } catch (Exception e) {
                            logger.error("异步监听器处理技能发现事件失败: {}", e.getMessage());
                        }
                    });
                } else {
                    listener.onSkillDiscovered(skill);
                }
            } catch (Exception e) {
                logger.error("监听器处理技能发现事件失败: {} - {}", 
                    listener.getClass().getSimpleName(), e.getMessage());
            }
        }
    }
    
    /**
     * 发布发现完成事件
     *
     * @param result 发现结果
     */
    public void publishDiscoveryCompleted(DiscoveryResult result) {
        logger.debug("发布发现完成事件: {} skills discovered", 
            result.getSkills() != null ? result.getSkills().size() : 0);
        
        // 同时发布到 SceneEventPublisher
        if (sceneEventPublisher != null) {
            int count = result.getSkills() != null ? result.getSkills().size() : 0;
            sceneEventPublisher.publishCapabilityEvent(
                net.ooder.scene.event.capability.CapabilityEvent.discoveryCompleted(this, count)
            );
        }
        
        for (DiscoveryEventListener listener : listeners) {
            try {
                if (listener.isAsync()) {
                    asyncExecutor.submit(() -> {
                        try {
                            listener.onDiscoveryCompleted(result);
                        } catch (Exception e) {
                            logger.error("异步监听器处理发现完成事件失败: {}", e.getMessage());
                        }
                    });
                } else {
                    listener.onDiscoveryCompleted(result);
                }
            } catch (Exception e) {
                logger.error("监听器处理发现完成事件失败: {} - {}", 
                    listener.getClass().getSimpleName(), e.getMessage());
            }
        }
    }
    
    /**
     * 发布发现失败事件
     *
     * @param request 发现请求
     * @param error 错误信息
     */
    public void publishDiscoveryFailed(DiscoveryRequest request, String error) {
        logger.debug("发布发现失败事件: {}", error);
        
        // 同时发布到 SceneEventPublisher
        if (sceneEventPublisher != null) {
            sceneEventPublisher.publishCapabilityEvent(
                net.ooder.scene.event.capability.CapabilityEvent.discoveryFailed(this, error)
            );
        }
        
        for (DiscoveryEventListener listener : listeners) {
            try {
                if (listener.isAsync()) {
                    asyncExecutor.submit(() -> {
                        try {
                            listener.onDiscoveryFailed(request, error);
                        } catch (Exception e) {
                            logger.error("异步监听器处理发现失败事件失败: {}", e.getMessage());
                        }
                    });
                } else {
                    listener.onDiscoveryFailed(request, error);
                }
            } catch (Exception e) {
                logger.error("监听器处理发现失败事件失败: {} - {}", 
                    listener.getClass().getSimpleName(), e.getMessage());
            }
        }
    }
    
    /**
     * 获取已注册的监听器数量
     *
     * @return 监听器数量
     */
    public int getListenerCount() {
        return listeners.size();
    }
    
    /**
     * 关闭发布器
     * 
     * <p>释放线程池资源。</p>
     */
    public void shutdown() {
        logger.info("关闭 DiscoveryEventPublisher");
        asyncExecutor.shutdown();
    }
}
