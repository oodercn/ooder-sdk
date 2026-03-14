package net.ooder.scene.skill.tool.ext;

import java.util.Arrays;
import java.util.List;

/**
 * 工具扩展配置
 *
 * <p>配置工具的执行策略、权限控制、用户确认等行为</p>
 *
 * <p>架构层次：应用层 - 工具扩展</p>
 *
 * @author ooder
 * @since 2.3
 */
public class ToolExtensionConfig {

    /**
     * 是否异步执行
     */
    private boolean async = false;

    /**
     * 是否需要用户确认
     */
    private boolean requireConfirmation = false;

    /**
     * 确认提示模板
     */
    private String confirmationTemplate;

    /**
     * 超时时间（毫秒）
     */
    private long timeout = 30000;

    /**
     * 重试次数
     */
    private int retryCount = 0;

    /**
     * 允许执行的角色列表
     */
    private List<String> allowedRoles = Arrays.asList("USER", "ADMIN");

    /**
     * 允许执行的域名单（null表示不限制）
     */
    private List<String> allowedDomains;

    /**
     * 执行前钩子（Bean名称）
     */
    private String beforeHook;

    /**
     * 执行后钩子（Bean名称）
     */
    private String afterHook;

    /**
     * 是否记录审计日志
     */
    private boolean auditLog = true;

    /**
     * 是否支持取消
     */
    private boolean cancellable = false;

    /**
     * 进度报告间隔（毫秒）
     */
    private long progressInterval = 1000;

    public static ToolExtensionConfig defaults() {
        return new ToolExtensionConfig();
    }

    public static ToolExtensionConfig async() {
        ToolExtensionConfig config = new ToolExtensionConfig();
        config.setAsync(true);
        return config;
    }

    public static ToolExtensionConfig withConfirmation() {
        ToolExtensionConfig config = new ToolExtensionConfig();
        config.setRequireConfirmation(true);
        return config;
    }

    public ToolExtensionConfig async(boolean async) {
        this.async = async;
        return this;
    }

    public ToolExtensionConfig requireConfirmation(boolean require) {
        this.requireConfirmation = require;
        return this;
    }

    public ToolExtensionConfig timeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    public ToolExtensionConfig allowedRoles(String... roles) {
        this.allowedRoles = Arrays.asList(roles);
        return this;
    }

    public ToolExtensionConfig allowedDomains(String... domains) {
        this.allowedDomains = Arrays.asList(domains);
        return this;
    }

    public ToolExtensionConfig cancellable(boolean cancellable) {
        this.cancellable = cancellable;
        return this;
    }

    // Getters and Setters

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public boolean isRequireConfirmation() {
        return requireConfirmation;
    }

    public void setRequireConfirmation(boolean requireConfirmation) {
        this.requireConfirmation = requireConfirmation;
    }

    public String getConfirmationTemplate() {
        return confirmationTemplate;
    }

    public void setConfirmationTemplate(String confirmationTemplate) {
        this.confirmationTemplate = confirmationTemplate;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public List<String> getAllowedRoles() {
        return allowedRoles;
    }

    public void setAllowedRoles(List<String> allowedRoles) {
        this.allowedRoles = allowedRoles;
    }

    public List<String> getAllowedDomains() {
        return allowedDomains;
    }

    public void setAllowedDomains(List<String> allowedDomains) {
        this.allowedDomains = allowedDomains;
    }

    public String getBeforeHook() {
        return beforeHook;
    }

    public void setBeforeHook(String beforeHook) {
        this.beforeHook = beforeHook;
    }

    public String getAfterHook() {
        return afterHook;
    }

    public void setAfterHook(String afterHook) {
        this.afterHook = afterHook;
    }

    public boolean isAuditLog() {
        return auditLog;
    }

    public void setAuditLog(boolean auditLog) {
        this.auditLog = auditLog;
    }

    public boolean isCancellable() {
        return cancellable;
    }

    public void setCancellable(boolean cancellable) {
        this.cancellable = cancellable;
    }

    public long getProgressInterval() {
        return progressInterval;
    }

    public void setProgressInterval(long progressInterval) {
        this.progressInterval = progressInterval;
    }
}
