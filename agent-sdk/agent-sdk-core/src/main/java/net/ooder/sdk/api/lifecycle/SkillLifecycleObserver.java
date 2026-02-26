package net.ooder.sdk.api.lifecycle;

/**
 * Skill 生命周期观察者接口
 * 
 * 允许外部系统订阅特定 Skill 或特定类型的事件
 */
public interface SkillLifecycleObserver {
    
    /**
     * 当 Skill 生命周期事件发生时触发
     * 
     * @param event 生命周期事件
     */
    void onEvent(SkillLifecycleEvent event);
    
    /**
     * 获取观察者ID
     * 
     * @return 观察者唯一标识
     */
    default String getObserverId() {
        return this.getClass().getName() + "@" + System.identityHashCode(this);
    }
    
    /**
     * 是否关注特定事件类型
     * 
     * @param eventType 事件类型
     * @return true 表示关注该类型事件
     */
    default boolean isInterestedIn(SkillLifecycleEventType eventType) {
        return true; // 默认关注所有事件
    }
    
    /**
     * 是否关注特定 Skill
     * 
     * @param skillId Skill ID
     * @return true 表示关注该 Skill
     */
    default boolean isInterestedInSkill(String skillId) {
        return true; // 默认关注所有 Skill
    }
}
