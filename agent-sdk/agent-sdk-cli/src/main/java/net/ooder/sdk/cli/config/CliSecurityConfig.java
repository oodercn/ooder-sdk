package net.ooder.sdk.cli.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * CLI安全配置
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
@Component
@ConfigurationProperties(prefix = "ooder.cli.security")
public class CliSecurityConfig {

    /**
     * 命令白名单
     */
    private Set<String> whitelist = new HashSet<>();

    /**
     * 敏感字段列表
     */
    private List<String> sensitiveKeys = Arrays.asList(
            "password", "secret", "token", "key", "api-key", "credential", "auth"
    );

    /**
     * 是否启用安全检查
     */
    private boolean enabled = true;

    /**
     * 是否启用白名单检查
     */
    private boolean whitelistEnabled = true;

    /**
     * 是否启用注入检测
     */
    private boolean injectionCheckEnabled = true;

    /**
     * 是否启用危险字符检测
     */
    private boolean dangerousCharsCheckEnabled = true;

    /**
     * 是否启用审计日志
     */
    private boolean auditEnabled = true;

    /**
     * 危险字符正则表达式
     */
    private String dangerousCharsPattern = "[;|&$`\\{}\\[\\]\\(\\)\\*\\?<>]";

    /**
     * SQL注入检测正则表达式
     */
    private String sqlInjectionPattern = "(\\b(SELECT|INSERT|UPDATE|DELETE|DROP|CREATE|ALTER|EXEC|UNION)\\b)|(--|#|/\\*)";

    /**
     * 脚本注入检测正则表达式
     */
    private String scriptInjectionPattern = "<script|javascript:|on\\w+\\s*=";

    // Getters and Setters

    public Set<String> getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(Set<String> whitelist) {
        this.whitelist = whitelist;
    }

    public List<String> getSensitiveKeys() {
        return sensitiveKeys;
    }

    public void setSensitiveKeys(List<String> sensitiveKeys) {
        this.sensitiveKeys = sensitiveKeys;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isWhitelistEnabled() {
        return whitelistEnabled;
    }

    public void setWhitelistEnabled(boolean whitelistEnabled) {
        this.whitelistEnabled = whitelistEnabled;
    }

    public boolean isInjectionCheckEnabled() {
        return injectionCheckEnabled;
    }

    public void setInjectionCheckEnabled(boolean injectionCheckEnabled) {
        this.injectionCheckEnabled = injectionCheckEnabled;
    }

    public boolean isDangerousCharsCheckEnabled() {
        return dangerousCharsCheckEnabled;
    }

    public void setDangerousCharsCheckEnabled(boolean dangerousCharsCheckEnabled) {
        this.dangerousCharsCheckEnabled = dangerousCharsCheckEnabled;
    }

    public boolean isAuditEnabled() {
        return auditEnabled;
    }

    public void setAuditEnabled(boolean auditEnabled) {
        this.auditEnabled = auditEnabled;
    }

    public String getDangerousCharsPattern() {
        return dangerousCharsPattern;
    }

    public void setDangerousCharsPattern(String dangerousCharsPattern) {
        this.dangerousCharsPattern = dangerousCharsPattern;
    }

    public String getSqlInjectionPattern() {
        return sqlInjectionPattern;
    }

    public void setSqlInjectionPattern(String sqlInjectionPattern) {
        this.sqlInjectionPattern = sqlInjectionPattern;
    }

    public String getScriptInjectionPattern() {
        return scriptInjectionPattern;
    }

    public void setScriptInjectionPattern(String scriptInjectionPattern) {
        this.scriptInjectionPattern = scriptInjectionPattern;
    }
}
