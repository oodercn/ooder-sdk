package net.ooder.scene.core.spi;

import net.ooder.scene.core.activation.model.ActivationProcess;
import net.ooder.scene.core.template.ActivationStepConfig;

import java.util.Map;

/**
 * 激活步骤执行器接口
 * 
 * <p>扩展点：用于执行自定义的激活步骤</p>
 * <p>实现类可以通过 SPI 机制注册，系统会根据 stepType 自动查找对应的执行器</p>
 *
 * <h3>使用示例</h3>
 * <pre>
 * public class ConfirmJoinExecutor implements ActivationStepExecutor {
 *     @Override
 *     public String getStepType() {
 *         return "CONFIRM_JOIN";
 *     }
 *     
 *     @Override
 *     public boolean canExecute(ActivationStepConfig stepConfig) {
 *         return true;
 *     }
 *     
 *     @Override
 *     public StepResult execute(ActivationStepConfig stepConfig, 
 *                              ActivationProcess process,
 *                              Map&lt;String, Object&gt; context) {
 *         // 执行确认加入逻辑
 *         return StepResult.success("已确认加入");
 *     }
 * }
 * </pre>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface ActivationStepExecutor {
    
    /**
     * 获取步骤类型
     * 
     * @return 步骤类型标识，如：CONFIRM_JOIN, CONFIG_CAPABILITY, CONFIRM_CONDITION
     */
    String getStepType();
    
    /**
     * 检查是否可以执行该步骤
     * 
     * @param stepConfig 步骤配置
     * @return true 如果可以执行
     */
    boolean canExecute(ActivationStepConfig stepConfig);
    
    /**
     * 执行步骤
     * 
     * @param stepConfig 步骤配置
     * @param process 激活流程
     * @param context 执行上下文
     * @return 执行结果
     */
    StepResult execute(ActivationStepConfig stepConfig, 
                       ActivationProcess process, 
                       Map<String, Object> context);
    
    /**
     * 步骤执行结果
     */
    class StepResult {
        private boolean success;         // 是否成功
        private String message;          // 结果消息
        private Map<String, Object> data; // 附加数据
        
        public StepResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public StepResult(boolean success, String message, Map<String, Object> data) {
            this(success, message);
            this.data = data;
        }
        
        public static StepResult success(String message) {
            return new StepResult(true, message);
        }
        
        public static StepResult success(String message, Map<String, Object> data) {
            return new StepResult(true, message, data);
        }
        
        public static StepResult failure(String message) {
            return new StepResult(false, message);
        }
        
        public static StepResult failure(String message, Map<String, Object> data) {
            return new StepResult(false, message, data);
        }
        
        // Getters and Setters
        
        public boolean isSuccess() {
            return success;
        }
        
        public void setSuccess(boolean success) {
            this.success = success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public Map<String, Object> getData() {
            return data;
        }
        
        public void setData(Map<String, Object> data) {
            this.data = data;
        }
    }
}
