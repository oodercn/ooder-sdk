package net.ooder.scene.discovery;

import net.ooder.scene.discovery.api.DiscoveryRequest;
import net.ooder.scene.discovery.api.DiscoveryResult;

/**
 * 发现事件监听器接口
 * 
 * <p>提供发现流程中各阶段的事件监听能力，允许外部组件响应发现事件。</p>
 * 
 * <h3>事件类型：</h3>
 * <ul>
 *   <li>发现开始 - 发现流程启动时触发</li>
 *   <li>发现进度 - 发现过程中定期触发</li>
 *   <li>能力发现 - 发现新能力时触发</li>
 *   <li>场景发现 - 发现新场景时触发</li>
 *   <li>发现完成 - 发现流程完成时触发</li>
 *   <li>发现失败 - 发现流程失败时触发</li>
 * </ul>
 * 
 * <h3>使用示例：</h3>
 * <pre>
 * public class MyDiscoveryListener implements DiscoveryEventListener {
 *     
 *     public void onDiscoveryStarted(DiscoveryRequest request) {
 *         // 记录发现开始日志
 *     }
 *     
 *     public void onCapabilityDiscovered(CapabilityDTO capability) {
 *         // 处理新发现的能力
 *     }
 *     
 *     public void onDiscoveryCompleted(DiscoveryResult result) {
 *         // 更新 UI 或缓存
 *     }
 * }
 * </pre>
 * 
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 * @see DiscoveryEventPublisher
 * @see SceneEngineDiscoveryHook
 */
public interface DiscoveryEventListener {
    
    /**
     * 发现开始事件
     * 
     * <p>在发现流程开始时触发。</p>
     *
     * @param request 发现请求
     */
    void onDiscoveryStarted(DiscoveryRequest request);
    
    /**
     * 发现进度事件
     * 
     * <p>在发现过程中定期触发，用于报告进度。</p>
     *
     * @param progress 进度信息
     */
    void onDiscoveryProgress(DiscoveryProgress progress);
    
    /**
     * 能力发现事件
     * 
     * <p>当发现新能力时触发。</p>
     *
     * @param capability 发现的能力信息
     */
    void onCapabilityDiscovered(CapabilityDTO capability);
    
    /**
     * 场景发现事件
     * 
     * <p>当发现新场景时触发。</p>
     *
     * @param scene 发现的场景信息
     */
    void onSceneDiscovered(CapabilityDTO scene);
    
    /**
     * 技能发现事件
     * 
     * <p>当发现新技能时触发。</p>
     *
     * @param skill 发现的技能信息
     */
    void onSkillDiscovered(CapabilityDTO skill);
    
    /**
     * 发现完成事件
     * 
     * <p>在发现流程成功完成时触发。</p>
     *
     * @param result 发现结果
     */
    void onDiscoveryCompleted(DiscoveryResult result);
    
    /**
     * 发现失败事件
     * 
     * <p>在发现流程失败时触发。</p>
     *
     * @param request 原始发现请求
     * @param error 错误信息
     */
    void onDiscoveryFailed(DiscoveryRequest request, String error);
    
    /**
     * 获取监听器优先级
     * 
     * <p>优先级高的监听器先执行，默认优先级为 0。</p>
     *
     * @return 优先级数值，数值越大优先级越高
     */
    default int getPriority() {
        return 0;
    }
    
    /**
     * 是否异步执行
     * 
     * <p>返回 true 表示该监听器将异步执行，不会阻塞发现流程。</p>
     *
     * @return true 如果异步执行
     */
    default boolean isAsync() {
        return false;
    }
    
    /**
     * 发现进度信息
     */
    class DiscoveryProgress {
        private final int totalSteps;
        private final int currentStep;
        private final String currentPhase;
        private final int percentage;
        private final String message;
        private final long elapsedTime;
        private final long estimatedRemainingTime;
        
        public DiscoveryProgress(int totalSteps, int currentStep, String currentPhase, 
                                 int percentage, String message, long elapsedTime, long estimatedRemainingTime) {
            this.totalSteps = totalSteps;
            this.currentStep = currentStep;
            this.currentPhase = currentPhase;
            this.percentage = percentage;
            this.message = message;
            this.elapsedTime = elapsedTime;
            this.estimatedRemainingTime = estimatedRemainingTime;
        }
        
        public int getTotalSteps() { return totalSteps; }
        public int getCurrentStep() { return currentStep; }
        public String getCurrentPhase() { return currentPhase; }
        public int getPercentage() { return percentage; }
        public String getMessage() { return message; }
        public long getElapsedTime() { return elapsedTime; }
        public long getEstimatedRemainingTime() { return estimatedRemainingTime; }
        
        @Override
        public String toString() {
            return String.format("DiscoveryProgress{phase='%s', progress=%d%% (%d/%d), message='%s'}", 
                currentPhase, percentage, currentStep, totalSteps, message);
        }
    }
}
