package net.ooder.scene.skill.tool.ext;

import net.ooder.scene.skill.tool.Tool;
import net.ooder.scene.skill.tool.ToolContext;
import net.ooder.scene.skill.tool.ToolResult;

import java.util.Map;

/**
 * 可扩展工具接口
 *
 * <p>支持开发者热插拔扩展的工具接口，提供：</p>
 * <ul>
 *   <li>同步/异步执行策略配置</li>
 *   <li>用户确认机制</li>
 *   <li>执行回调通知</li>
 *   <li>权限控制</li>
 * </ul>
 *
 * <p>架构层次：应用层 - 工具扩展</p>
 *
 * @author ooder
 * @since 2.3
 */
public interface ToolExtension extends Tool {

    /**
     * 获取工具配置
     *
     * @return 工具配置
     */
    ToolExtensionConfig getConfig();

    /**
     * 检查是否需要用户确认
     *
     * @param arguments 执行参数
     * @return 是否需要确认
     */
    default boolean requireUserConfirmation(Map<String, Object> arguments) {
        return getConfig().isRequireConfirmation();
    }

    /**
     * 获取用户确认提示信息
     *
     * @param arguments 执行参数
     * @return 确认提示信息
     */
    default String getConfirmationMessage(Map<String, Object> arguments) {
        return "确认执行操作: " + getName() + "?";
    }

    /**
     * 检查执行权限
     *
     * @param context 执行上下文
     * @param arguments 执行参数
     * @return 是否有权限执行
     */
    default boolean checkPermission(ToolContext context, Map<String, Object> arguments) {
        return true;
    }

    /**
     * 异步执行工具
     *
     * <p>当工具配置为异步执行时调用此方法</p>
     *
     * @param arguments 执行参数
     * @param context 执行上下文
     * @param callback 执行回调
     * @return 异步任务ID
     */
    String executeAsync(Map<String, Object> arguments, ToolContext context, ToolExecutionCallback callback);

    /**
     * 取消异步任务
     *
     * @param taskId 任务ID
     * @return 是否取消成功
     */
    default boolean cancelAsync(String taskId) {
        return false;
    }

    /**
     * 获取异步任务状态
     *
     * @param taskId 任务ID
     * @return 任务状态
     */
    default AsyncTaskStatus getAsyncStatus(String taskId) {
        return AsyncTaskStatus.UNKNOWN;
    }

    /**
     * 异步任务状态
     */
    enum AsyncTaskStatus {
        PENDING,      // 等待中
        RUNNING,      // 执行中
        COMPLETED,    // 已完成
        FAILED,       // 失败
        CANCELLED,    // 已取消
        UNKNOWN       // 未知
    }

    /**
     * 执行回调接口
     */
    interface ToolExecutionCallback {
        /**
         * 执行进度更新
         *
         * @param progress 进度 (0-100)
         * @param message 进度消息
         */
        void onProgress(int progress, String message);

        /**
         * 执行完成
         *
         * @param result 执行结果
         */
        void onComplete(ToolResult result);

        /**
         * 执行失败
         *
         * @param errorCode 错误码
         * @param errorMessage 错误消息
         */
        void onError(String errorCode, String errorMessage);

        /**
         * 执行取消
         */
        default void onCancelled() {}
    }
}
