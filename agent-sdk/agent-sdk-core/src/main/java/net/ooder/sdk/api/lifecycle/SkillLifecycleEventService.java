package net.ooder.sdk.api.lifecycle;

import java.util.List;

/**
 * Skill 生命周期事件服务
 * 
 * 提供事件订阅、发布和管理功能
 */
public interface SkillLifecycleEventService {
    
    /**
     * 订阅指定 Skill 的所有事件
     * 
     * @param skillId Skill ID
     * @param observer 观察者
     */
    void subscribe(String skillId, SkillLifecycleObserver observer);
    
    /**
     * 订阅指定类型的事件
     * 
     * @param eventType 事件类型
     * @param observer 观察者
     */
    void subscribe(SkillLifecycleEventType eventType, SkillLifecycleObserver observer);
    
    /**
     * 全局订阅所有 Skill 的所有事件
     * 
     * @param observer 观察者
     */
    void subscribeAll(SkillLifecycleObserver observer);
    
    /**
     * 取消订阅
     * 
     * @param observer 观察者
     */
    void unsubscribe(SkillLifecycleObserver observer);
    
    /**
     * 取消指定 Skill 的订阅
     * 
     * @param skillId Skill ID
     * @param observer 观察者
     */
    void unsubscribe(String skillId, SkillLifecycleObserver observer);
    
    /**
     * 发布事件
     * 
     * @param event 生命周期事件
     */
    void publish(SkillLifecycleEvent event);
    
    /**
     * 发布事件（便捷方法）
     * 
     * @param skillId Skill ID
     * @param eventType 事件类型
     */
    void publish(String skillId, SkillLifecycleEventType eventType);
    
    /**
     * 获取指定 Skill 的订阅者列表
     * 
     * @param skillId Skill ID
     * @return 订阅者列表
     */
    List<SkillLifecycleObserver> getSubscribers(String skillId);
    
    /**
     * 获取所有订阅者数量
     * 
     * @return 订阅者数量
     */
    int getSubscriberCount();
    
    /**
     * 清除所有订阅
     */
    void clearAllSubscriptions();
}
