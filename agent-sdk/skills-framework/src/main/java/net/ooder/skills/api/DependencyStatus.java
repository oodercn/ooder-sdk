package net.ooder.skills.api;

/**
 * 依赖状态
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public enum DependencyStatus {
    /**
     * 已安装
     */
    INSTALLED,

    /**
     * 未安装
     */
    NOT_INSTALLED,

    /**
     * 版本不兼容
     */
    VERSION_MISMATCH,

    /**
     * 安装中
     */
    INSTALLING,

    /**
     * 安装失败
     */
    INSTALL_FAILED,

    /**
     * 已跳过
     */
    SKIPPED
}
