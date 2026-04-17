package net.ooder.sdk.cli.config;

import java.util.List;
import java.util.Map;

/**
 * Skill CLI 配置类
 *
 * <p>统一使用 skill.yaml 配置格式</p>
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class SkillCliConfiguration {

    private SkillConfig skill;

    public SkillConfig getSkill() {
        return skill;
    }

    public void setSkill(SkillConfig skill) {
        this.skill = skill;
    }

    /**
     * Skill 配置
     */
    public static class SkillConfig {
        private CliConfig cli;

        public CliConfig getCli() {
            return cli;
        }

        public void setCli(CliConfig cli) {
            this.cli = cli;
        }
    }

    /**
     * CLI 配置
     */
    public static class CliConfig {
        private List<ExtensionConfig> extensions;
        private SecurityConfig security;
        private OutputConfig output;

        public List<ExtensionConfig> getExtensions() {
            return extensions;
        }

        public void setExtensions(List<ExtensionConfig> extensions) {
            this.extensions = extensions;
        }

        public SecurityConfig getSecurity() {
            return security;
        }

        public void setSecurity(SecurityConfig security) {
            this.security = security;
        }

        public OutputConfig getOutput() {
            return output;
        }

        public void setOutput(OutputConfig output) {
            this.output = output;
        }
    }

    /**
     * 扩展配置
     */
    public static class ExtensionConfig {
        private String skillId;
        private String command;
        private String handler;
        private String description;
        private boolean enabled = true;
        private Map<String, Object> properties;

        public String getSkillId() {
            return skillId;
        }

        public void setSkillId(String skillId) {
            this.skillId = skillId;
        }

        public String getCommand() {
            return command;
        }

        public void setCommand(String command) {
            this.command = command;
        }

        public String getHandler() {
            return handler;
        }

        public void setHandler(String handler) {
            this.handler = handler;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public Map<String, Object> getProperties() {
            return properties;
        }

        public void setProperties(Map<String, Object> properties) {
            this.properties = properties;
        }
    }

    /**
     * 安全配置
     */
    public static class SecurityConfig {
        private boolean enabled = true;
        private boolean auditEnabled = true;
        private boolean injectionCheckEnabled = true;
        private List<String> allowedCommands;
        private List<String> blockedCommands;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isAuditEnabled() {
            return auditEnabled;
        }

        public void setAuditEnabled(boolean auditEnabled) {
            this.auditEnabled = auditEnabled;
        }

        public boolean isInjectionCheckEnabled() {
            return injectionCheckEnabled;
        }

        public void setInjectionCheckEnabled(boolean injectionCheckEnabled) {
            this.injectionCheckEnabled = injectionCheckEnabled;
        }

        public List<String> getAllowedCommands() {
            return allowedCommands;
        }

        public void setAllowedCommands(List<String> allowedCommands) {
            this.allowedCommands = allowedCommands;
        }

        public List<String> getBlockedCommands() {
            return blockedCommands;
        }

        public void setBlockedCommands(List<String> blockedCommands) {
            this.blockedCommands = blockedCommands;
        }
    }

    /**
     * 输出配置
     */
    public static class OutputConfig {
        private String format = "text";
        private boolean colorEnabled = true;
        private boolean verbose = false;

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public boolean isColorEnabled() {
            return colorEnabled;
        }

        public void setColorEnabled(boolean colorEnabled) {
            this.colorEnabled = colorEnabled;
        }

        public boolean isVerbose() {
            return verbose;
        }

        public void setVerbose(boolean verbose) {
            this.verbose = verbose;
        }
    }
}
