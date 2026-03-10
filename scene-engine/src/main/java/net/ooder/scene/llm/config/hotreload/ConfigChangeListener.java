package net.ooder.scene.llm.config.hotreload;

import java.nio.file.Path;

/**
 * 配置变更监听器
 *
 * @author ooder
 * @since 2.4
 */
public interface ConfigChangeListener {

    /**
     * 配置变更
     */
    void onConfigChanged(ConfigChangedEvent event);

    /**
     * 配置删除
     */
    default void onConfigDeleted(String skillId, Path configFile) {}

    /**
     * 配置无效
     */
    default void onConfigInvalid(String skillId, Path configFile, String errorMessage) {}
}
