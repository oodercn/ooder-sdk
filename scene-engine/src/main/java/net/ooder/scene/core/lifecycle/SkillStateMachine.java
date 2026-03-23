package net.ooder.scene.core.lifecycle;

import net.ooder.scene.core.lifecycle.SceneSkillLifecycle.SkillLifecycleState;
import net.ooder.scene.core.lifecycle.SceneSkillLifecycle.StateChangeEvent;
import net.ooder.scene.core.lifecycle.SceneSkillLifecycle.StateChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 技能状态机管理器
 * 
 * <p>管理技能生命周期的状态转换，提供状态验证、转换和事件通知功能</p>
 *
 * <h3>状态机流程：</h3>
 * <pre>
 * DISCOVERED → PREVIEWING → CONFIGURING → DEP_CHECKING → DEP_CONFIRMING
 *                                                         ↓
 * ACTIVATED ← ACTIVATING ← INSTALLED ←────────────────────┘
 *     ↓
 * DEACTIVATED → UNINSTALLING → UNINSTALLED
 * 
 * 任意状态可转入 ERROR 或 UPDATING
 * </pre>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class SkillStateMachine {

    private static final Logger log = LoggerFactory.getLogger(SkillStateMachine.class);

    private final Map<String, SkillStateInfo> stateMap = new ConcurrentHashMap<>();
    private final Map<String, List<StateChangeListener>> listeners = new ConcurrentHashMap<>();
    private final Map<String, StateTransitionHistory> transitionHistory = new ConcurrentHashMap<>();

    public SkillStateMachine() {
    }

    /**
     * 获取技能状态
     * 
     * @param sceneId 场景ID
     * @param skillId 技能ID
     * @return 技能状态信息，如果不存在返回 null
     */
    public SkillStateInfo getState(String sceneId, String skillId) {
        String key = buildKey(sceneId, skillId);
        return stateMap.get(key);
    }

    /**
     * 初始化技能状态
     * 
     * @param sceneId 场景ID
     * @param skillId 技能ID
     * @param skillName 技能名称
     * @return 初始化后的状态信息
     */
    public SkillStateInfo initializeState(String sceneId, String skillId, String skillName) {
        String key = buildKey(sceneId, skillId);
        
        SkillStateInfo stateInfo = new SkillStateInfo();
        stateInfo.setSceneId(sceneId);
        stateInfo.setSkillId(skillId);
        stateInfo.setSkillName(skillName);
        stateInfo.setState(SkillLifecycleState.DISCOVERED);
        stateInfo.setStateChangedAt(System.currentTimeMillis());
        
        stateMap.put(key, stateInfo);
        recordTransition(key, null, SkillLifecycleState.DISCOVERED, "初始化");
        
        log.info("[initializeState] Skill initialized: {} in scene: {}", skillId, sceneId);
        return stateInfo;
    }

    /**
     * 转换状态
     * 
     * @param sceneId 场景ID
     * @param skillId 技能ID
     * @param targetState 目标状态
     * @return 转换后的状态信息
     * @throws IllegalStateException 如果状态转换不合法
     */
    public SkillStateInfo transition(String sceneId, String skillId, SkillLifecycleState targetState) {
        return transition(sceneId, skillId, targetState, null);
    }

    /**
     * 转换状态
     * 
     * @param sceneId 场景ID
     * @param skillId 技能ID
     * @param targetState 目标状态
     * @param message 转换消息
     * @return 转换后的状态信息
     * @throws IllegalStateException 如果状态转换不合法
     */
    public SkillStateInfo transition(String sceneId, String skillId, 
                                      SkillLifecycleState targetState, String message) {
        String key = buildKey(sceneId, skillId);
        SkillStateInfo stateInfo = stateMap.get(key);
        
        if (stateInfo == null) {
            throw new IllegalStateException("Skill state not found: " + skillId + " in scene: " + sceneId);
        }
        
        SkillLifecycleState currentState = stateInfo.getState();
        
        if (!currentState.canTransitionTo(targetState)) {
            throw new IllegalStateException(
                String.format("Invalid state transition: %s -> %s for skill: %s", 
                    currentState, targetState, skillId));
        }
        
        SkillLifecycleState previousState = currentState;
        stateInfo.setPreviousState(previousState.name());
        stateInfo.setState(targetState);
        stateInfo.setStateChangedAt(System.currentTimeMillis());
        
        if (targetState == SkillLifecycleState.ERROR && message != null) {
            stateInfo.setErrorMessage(message);
        }
        
        if (targetState == SkillLifecycleState.INSTALLED) {
            stateInfo.setInstallTime(System.currentTimeMillis());
        } else if (targetState == SkillLifecycleState.ACTIVATED) {
            stateInfo.setActivateTime(System.currentTimeMillis());
        }
        
        recordTransition(key, previousState, targetState, message);
        
        notifyStateChange(sceneId, skillId, previousState, targetState, message);
        
        log.info("[transition] State changed: {} -> {} for skill: {} in scene: {}", 
            previousState, targetState, skillId, sceneId);
        
        return stateInfo;
    }

    /**
     * 尝试转换状态（不抛出异常）
     * 
     * @param sceneId 场景ID
     * @param skillId 技能ID
     * @param targetState 目标状态
     * @return 是否成功转换
     */
    public boolean tryTransition(String sceneId, String skillId, SkillLifecycleState targetState) {
        try {
            transition(sceneId, skillId, targetState);
            return true;
        } catch (IllegalStateException e) {
            log.warn("[tryTransition] Failed to transition: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 检查是否可以转换到目标状态
     * 
     * @param sceneId 场景ID
     * @param skillId 技能ID
     * @param targetState 目标状态
     * @return 是否可以转换
     */
    public boolean canTransition(String sceneId, String skillId, SkillLifecycleState targetState) {
        SkillStateInfo stateInfo = getState(sceneId, skillId);
        if (stateInfo == null) {
            return false;
        }
        return stateInfo.getState().canTransitionTo(targetState);
    }

    /**
     * 订阅状态变更事件
     * 
     * @param sceneId 场景ID
     * @param listener 监听器
     * @return 订阅ID
     */
    public String subscribe(String sceneId, StateChangeListener listener) {
        String subscriptionId = UUID.randomUUID().toString();
        listeners.computeIfAbsent(sceneId, k -> new CopyOnWriteArrayList<>()).add(listener);
        return subscriptionId;
    }

    /**
     * 取消订阅
     * 
     * @param sceneId 场景ID
     * @param listener 监听器
     */
    public void unsubscribe(String sceneId, StateChangeListener listener) {
        List<StateChangeListener> sceneListeners = listeners.get(sceneId);
        if (sceneListeners != null) {
            sceneListeners.remove(listener);
        }
    }

    /**
     * 获取状态转换历史
     * 
     * @param sceneId 场景ID
     * @param skillId 技能ID
     * @return 转换历史
     */
    public StateTransitionHistory getTransitionHistory(String sceneId, String skillId) {
        String key = buildKey(sceneId, skillId);
        return transitionHistory.get(key);
    }

    /**
     * 移除技能状态
     * 
     * @param sceneId 场景ID
     * @param skillId 技能ID
     */
    public void removeState(String sceneId, String skillId) {
        String key = buildKey(sceneId, skillId);
        stateMap.remove(key);
        transitionHistory.remove(key);
        log.info("[removeState] Skill state removed: {} in scene: {}", skillId, sceneId);
    }

    /**
     * 获取场景中所有技能状态
     * 
     * @param sceneId 场景ID
     * @return 技能状态列表
     */
    public List<SkillStateInfo> getSceneSkillStates(String sceneId) {
        List<SkillStateInfo> result = new ArrayList<>();
        for (Map.Entry<String, SkillStateInfo> entry : stateMap.entrySet()) {
            if (entry.getKey().startsWith(sceneId + ":")) {
                result.add(entry.getValue());
            }
        }
        return result;
    }

    /**
     * 获取指定状态的所有技能
     * 
     * @param state 目标状态
     * @return 技能状态列表
     */
    public List<SkillStateInfo> getSkillsByState(SkillLifecycleState state) {
        List<SkillStateInfo> result = new ArrayList<>();
        for (SkillStateInfo info : stateMap.values()) {
            if (info.getState() == state) {
                result.add(info);
            }
        }
        return result;
    }

    private String buildKey(String sceneId, String skillId) {
        return sceneId + ":" + skillId;
    }

    private void recordTransition(String key, SkillLifecycleState fromState, 
                                   SkillLifecycleState toState, String message) {
        StateTransitionHistory history = transitionHistory.computeIfAbsent(
            key, k -> new StateTransitionHistory());
        history.addTransition(fromState, toState, message);
    }

    private void notifyStateChange(String sceneId, String skillId, 
                                    SkillLifecycleState oldState, 
                                    SkillLifecycleState newState, 
                                    String message) {
        List<StateChangeListener> sceneListeners = listeners.get(sceneId);
        if (sceneListeners == null || sceneListeners.isEmpty()) {
            return;
        }
        
        StateChangeEvent event = new StateChangeEvent();
        event.setSceneId(sceneId);
        event.setSkillId(skillId);
        event.setOldState(oldState);
        event.setNewState(newState);
        event.setTimestamp(System.currentTimeMillis());
        event.setMessage(message);
        
        for (StateChangeListener listener : sceneListeners) {
            try {
                listener.onStateChange(event);
            } catch (Exception e) {
                log.error("[notifyStateChange] Listener error", e);
            }
        }
    }

    /**
     * 状态转换历史
     */
    public static class StateTransitionHistory {
        private final List<TransitionRecord> records = new ArrayList<>();
        private static final int MAX_RECORDS = 100;

        public void addTransition(SkillLifecycleState fromState, 
                                   SkillLifecycleState toState, 
                                   String message) {
            if (records.size() >= MAX_RECORDS) {
                records.remove(0);
            }
            records.add(new TransitionRecord(fromState, toState, message, System.currentTimeMillis()));
        }

        public List<TransitionRecord> getRecords() {
            return Collections.unmodifiableList(records);
        }

        public TransitionRecord getLastTransition() {
            if (records.isEmpty()) {
                return null;
            }
            return records.get(records.size() - 1);
        }
    }

    /**
     * 转换记录
     */
    public static class TransitionRecord {
        private final SkillLifecycleState fromState;
        private final SkillLifecycleState toState;
        private final String message;
        private final long timestamp;

        public TransitionRecord(SkillLifecycleState fromState, 
                                 SkillLifecycleState toState, 
                                 String message, 
                                 long timestamp) {
            this.fromState = fromState;
            this.toState = toState;
            this.message = message;
            this.timestamp = timestamp;
        }

        public SkillLifecycleState getFromState() { return fromState; }
        public SkillLifecycleState getToState() { return toState; }
        public String getMessage() { return message; }
        public long getTimestamp() { return timestamp; }

        @Override
        public String toString() {
            return String.format("%s -> %s (%s) at %d", fromState, toState, message, timestamp);
        }
    }
}
