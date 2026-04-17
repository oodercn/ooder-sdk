package net.ooder.sdk.cli.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Ooder CLI 配置属性
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
@ConfigurationProperties(prefix = "ooder.cli")
public class OoderCliProperties {

    /**
     * 是否启用 CLI
     */
    private boolean enabled = true;

    /**
     * 交互式模式
     */
    private boolean interactive = false;

    /**
     * 输出格式
     */
    private String outputFormat = "text";

    /**
     * 是否启用颜色输出
     */
    private boolean colorEnabled = true;

    /**
     * 详细模式
     */
    private boolean verbose = false;

    /**
     * 安全配置
     */
    private Security security = new Security();

    /**
     * 扩展配置
     */
    private List<Extension> extensions = new ArrayList<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isInteractive() {
        return interactive;
    }

    public void setInteractive(boolean interactive) {
        this.interactive = interactive;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
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

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public List<Extension> getExtensions() {
        return extensions;
    }

    public void setExtensions(List<Extension> extensions) {
        this.extensions = extensions;
    }

    /**
     * 安全配置类
     */
    public static class Security {
        private boolean enabled = true;
        private boolean auditEnabled = true;
        private boolean injectionCheckEnabled = true;
        private List<String> allowedCommands = new ArrayList<>();
        private List<String> blockedCommands = new ArrayList<>();

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
     * 扩展配置类
     */
    public static class Extension {
        private String skillId;
        private String command;
        private String handler;
        private String description;
        private boolean enabled = true;

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
    }
}
