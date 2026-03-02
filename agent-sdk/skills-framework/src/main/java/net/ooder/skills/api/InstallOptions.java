package net.ooder.skills.api;

import java.util.Map;

/**
 * 安装选项
 * 用于控制场景模板安装行为
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class InstallOptions {

    /**
     * 是否跳过已安装的 Skills
     */
    private boolean skipInstalled = true;

    /**
     * 是否自动创建场景
     */
    private boolean autoCreateScene = true;

    /**
     * 是否自动激活场景
     */
    private boolean autoActivate = false;

    /**
     * 配置覆盖
     */
    private Map<String, Object> configOverrides;

    /**
     * 安装模式
     */
    private InstallMode installMode = InstallMode.PARALLEL;

    /**
     * 超时时间（毫秒）
     */
    private long timeout = 300000; // 5分钟

    /**
     * 是否强制重新安装
     */
    private boolean forceReinstall = false;

    /**
     * 安装模式枚举
     */
    public enum InstallMode {
        /**
         * 串行安装
         */
        SEQUENTIAL,

        /**
         * 并行安装
         */
        PARALLEL,

        /**
         * 拓扑排序后安装
         */
        TOPOLOGICAL
    }

    // Getters and Setters
    public boolean isSkipInstalled() {
        return skipInstalled;
    }

    public void setSkipInstalled(boolean skipInstalled) {
        this.skipInstalled = skipInstalled;
    }

    public boolean isAutoCreateScene() {
        return autoCreateScene;
    }

    public void setAutoCreateScene(boolean autoCreateScene) {
        this.autoCreateScene = autoCreateScene;
    }

    public boolean isAutoActivate() {
        return autoActivate;
    }

    public void setAutoActivate(boolean autoActivate) {
        this.autoActivate = autoActivate;
    }

    public Map<String, Object> getConfigOverrides() {
        return configOverrides;
    }

    public void setConfigOverrides(Map<String, Object> configOverrides) {
        this.configOverrides = configOverrides;
    }

    public InstallMode getInstallMode() {
        return installMode;
    }

    public void setInstallMode(InstallMode installMode) {
        this.installMode = installMode;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public boolean isForceReinstall() {
        return forceReinstall;
    }

    public void setForceReinstall(boolean forceReinstall) {
        this.forceReinstall = forceReinstall;
    }

    /**
     * 创建默认选项
     */
    public static InstallOptions defaults() {
        return new InstallOptions();
    }

    /**
     * 创建跳过已安装的选项
     */
    public static InstallOptions skipInstalled() {
        InstallOptions options = new InstallOptions();
        options.setSkipInstalled(true);
        return options;
    }

    /**
     * 创建自动激活的选项
     */
    public static InstallOptions autoActivate() {
        InstallOptions options = new InstallOptions();
        options.setAutoActivate(true);
        return options;
    }
}
