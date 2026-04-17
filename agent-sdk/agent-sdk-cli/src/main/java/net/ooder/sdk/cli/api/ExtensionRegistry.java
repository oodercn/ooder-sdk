package net.ooder.sdk.cli.api;

import java.util.List;
import java.util.Optional;

/**
 * CLI扩展注册表接口
 *
 * <p>管理CLI扩展命令的注册和发现</p>
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public interface ExtensionRegistry {

    /**
     * 注册扩展
     *
     * @param extension 扩展
     */
    void register(CliExtension extension);

    /**
     * 注销扩展
     *
     * @param extensionId 扩展ID
     */
    void unregister(String extensionId);

    /**
     * 获取扩展
     *
     * @param extensionId 扩展ID
     * @return 扩展
     */
    Optional<CliExtension> getExtension(String extensionId);

    /**
     * 获取所有扩展
     *
     * @return 扩展列表
     */
    List<CliExtension> getAllExtensions();

    /**
     * 获取已启用的扩展
     *
     * @return 扩展列表
     */
    List<CliExtension> getEnabledExtensions();

    /**
     * 启用扩展
     *
     * @param extensionId 扩展ID
     */
    void enableExtension(String extensionId);

    /**
     * 禁用扩展
     *
     * @param extensionId 扩展ID
     */
    void disableExtension(String extensionId);

    /**
     * 加载扩展
     *
     * @param path 扩展路径
     */
    void loadExtension(String path);

    /**
     * 扫描并加载扩展
     *
     * @param directory 目录
     */
    void scanExtensions(String directory);

    /**
     * CLI扩展接口
     */
    interface CliExtension {
        /**
         * 获取扩展ID
         *
         * @return 扩展ID
         */
        String getId();

        /**
         * 获取扩展名称
         *
         * @return 扩展名称
         */
        String getName();

        /**
         * 获取扩展版本
         *
         * @return 版本
         */
        String getVersion();

        /**
         * 获取扩展提供的命令
         *
         * @return 命令列表
         */
        List<CliCommand> getCommands();

        /**
         * 初始化扩展
         */
        void initialize();

        /**
         * 销毁扩展
         */
        void destroy();

        /**
         * 是否已启用
         *
         * @return 是否启用
         */
        boolean isEnabled();
    }
}
