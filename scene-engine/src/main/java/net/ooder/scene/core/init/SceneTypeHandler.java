package net.ooder.scene.core.init;

import net.ooder.scene.group.SceneGroup;
import net.ooder.scene.group.SceneGroupManager;
import net.ooder.scene.skill.model.SceneType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 场景类型处理器
 * 
 * <p>根据场景类型（AUTO/TRIGGER/HYBRID）应用不同的行为逻辑。</p>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class SceneTypeHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(SceneTypeHandler.class);
    
    private final SceneGroupManager seSceneGroupManager;
    
    public SceneTypeHandler(SceneGroupManager seSceneGroupManager) {
        this.seSceneGroupManager = seSceneGroupManager;
    }
    
    /**
     * 根据场景类型应用行为
     */
    public void applyBehavior(SceneGroup group, SceneType sceneType) {
        if (group == null || sceneType == null) {
            logger.warn("SceneGroup or SceneType is null");
            return;
        }
        
        switch (sceneType) {
            case AUTO:
                applyAutoBehavior(group);
                break;
            case TRIGGER:
                applyTriggerBehavior(group);
                break;
            case HYBRID:
                applyHybridBehavior(group);
                break;
            default:
                logger.warn("Unknown scene type: {}, using default behavior", sceneType);
                applyDefaultBehavior(group);
        }
    }
    
    /**
     * AUTO 场景行为：立即激活，启动自驱动逻辑
     */
    private void applyAutoBehavior(SceneGroup group) {
        logger.info("Applying AUTO behavior to SceneGroup: {}", group.getSceneGroupId());
        
        // 激活场景组
        boolean activated = group.activate();
        if (activated) {
            logger.info("Activated SceneGroup: {}", group.getSceneGroupId());
        }
        
        // 启动定时任务（由外部调度器处理）
        group.setConfig("auto.started", true);
        group.setConfig("heartbeat.enabled", true);
        
        // 注册事件监听
        group.setConfig("eventListener.registered", true);
    }
    
    /**
     * TRIGGER 场景行为：保持 CREATED 状态，等待外部触发
     */
    private void applyTriggerBehavior(SceneGroup group) {
        logger.info("Applying TRIGGER behavior to SceneGroup: {}", group.getSceneGroupId());
        
        // 不自动激活，保持 CREATED 状态
        group.setConfig("trigger.enabled", true);
        group.setConfig("heartbeat.enabled", false);
        
        // 注册触发入口
        group.setConfig("triggerEndpoint.registered", true);
        
        logger.info("SceneGroup {} is waiting for external trigger", group.getSceneGroupId());
    }
    
    /**
     * HYBRID 场景行为：根据配置决定初始行为，同时支持触发
     */
    private void applyHybridBehavior(SceneGroup group) {
        logger.info("Applying HYBRID behavior to SceneGroup: {}", group.getSceneGroupId());
        
        // 检查配置决定初始行为
        Boolean startAsAuto = (Boolean) group.getConfig("hybrid.startAsAuto");
        
        if (startAsAuto == null || startAsAuto) {
            // 默认作为 AUTO 场景启动
            applyAutoBehavior(group);
        } else {
            // 作为 TRIGGER 场景等待
            applyTriggerBehavior(group);
        }
        
        // 无论初始行为如何，都注册触发入口
        group.setConfig("triggerEndpoint.registered", true);
        group.setConfig("hybrid.mode", true);
    }
    
    /**
     * 默认行为
     */
    private void applyDefaultBehavior(SceneGroup group) {
        logger.info("Applying default behavior to SceneGroup: {}", group.getSceneGroupId());
        
        // 尝试激活
        group.activate();
    }
    
    /**
     * 触发激活（用于 TRIGGER/HYBRID 场景）
     */
    public boolean triggerActivate(String sceneGroupId) {
        SceneGroup group = seSceneGroupManager.getSceneGroup(sceneGroupId);
        if (group == null) {
            logger.warn("SceneGroup not found: {}", sceneGroupId);
            return false;
        }
        
        boolean activated = group.activate();
        if (activated) {
            group.setConfig("triggered", true);
            group.setConfig("heartbeat.enabled", true);
            logger.info("SceneGroup {} activated by trigger", sceneGroupId);
        }
        
        return activated;
    }
}
