package net.ooder.scene.core.activation.spi;

import net.ooder.scene.core.activation.model.ActivationContext;
import net.ooder.scene.core.activation.model.NetworkAction;

import java.util.concurrent.CompletableFuture;

/**
 * 网络动作执行器接口
 *
 * <p>扩展点：用于执行自定义的网络动作</p>
 * <p>实现类可以通过 SPI 机制注册，系统会根据 actionType 自动查找对应的执行器</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public interface NetworkActionExecutor {
    
    /**
     * 获取支持的动作类型
     *
     * @return 动作类型标识，如：NOTIFICATION, UPDATE, SYNC, CALLBACK
     */
    String getActionType();
    
    /**
     * 执行动作
     *
     * @param context 执行上下文
     * @param action 动作配置
     * @return 执行结果
     */
    CompletableFuture<ActionResult> execute(ActivationContext context, NetworkAction action);
    
    /**
     * 检查是否可以执行该动作
     *
     * @param action 动作配置
     * @return true 如果可以执行
     */
    default boolean canExecute(NetworkAction action) {
        return true;
    }
    
    /**
     * 动作执行结果
     */
    class ActionResult {
        private boolean success;
        private String message;
        private Object data;
        private String errorCode;
        
        public ActionResult() {
        }
        
        public ActionResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public static ActionResult success(String message) {
            return new ActionResult(true, message);
        }
        
        public static ActionResult success(String message, Object data) {
            ActionResult result = new ActionResult(true, message);
            result.setData(data);
            return result;
        }
        
        public static ActionResult failure(String message) {
            return new ActionResult(false, message);
        }
        
        public static ActionResult failure(String errorCode, String message) {
            ActionResult result = new ActionResult(false, message);
            result.setErrorCode(errorCode);
            return result;
        }
        
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
        
        public Object getData() {
            return data;
        }
        
        public void setData(Object data) {
            this.data = data;
        }
        
        public String getErrorCode() {
            return errorCode;
        }
        
        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }
    }
}
