package net.ooder.config.scene.extension;

/**
 * 配置变更监听器接口
 *
 * @deprecated 自 SDK 7.3 起，请使用 {@code net.ooder.sdk.infra.observer.ConfigChangeListener} 替代。
 *             该接口将在未来版本中移除。
 *             <p>
 *             迁移示例:
 *             <pre>
 *             // 旧代码
 *             import net.ooder.config.scene.extension.ConfigChangeListener;
 *             
 *             // 新代码
 *             import net.ooder.sdk.infra.observer.ConfigChangeListener;
 *             </pre>
 *             </p>
 * @see net.ooder.sdk.infra.observer.ConfigChangeListener
 */
@Deprecated
public interface ConfigChangeListener {
    
    /**
     * 配置变更回调
     * @param event 配置变更事件
     * @deprecated 使用 {@code net.ooder.sdk.infra.observer.ConfigChangeListener#onConfigChanged} 替代
     */
    @Deprecated
    void onConfigChanged(ConfigChangeEvent event);
    
    /**
     * 获取监听器顺序
     * @return 顺序值，值越小优先级越高
     * @deprecated 使用 {@code net.ooder.sdk.infra.observer.ConfigChangeListener#getOrder} 替代
     */
    @Deprecated
    default int getOrder() {
        return 0;
    }
}
