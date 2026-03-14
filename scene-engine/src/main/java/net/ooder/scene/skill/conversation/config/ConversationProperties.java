package net.ooder.scene.skill.conversation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 对话服务配置属性
 *
 * <p>配置对话存储、自动学习、审计等功能</p>
 *
 * <p>配置示例（application.yml）：</p>
 * <pre>
 * se:
 *   conversation:
 *     enabled: true
 *     storage:
 *       type: file
 *       path: ${user.home}/.ooder/data/conversations
 *     auto-learn: true
 *     max-history: 100
 *     audit:
 *       enabled: true
 *       include-tool-calls: true
 *     knowledge:
 *       auto-update: true
 *       min-content-length: 50
 * </pre>
 *
 * <p>架构层次：配置层</p>
 *
 * @author ooder
 * @since 2.3
 */
@Component
@ConfigurationProperties(prefix = "se.conversation")
public class ConversationProperties {

    /**
     * 是否启用对话服务
     */
    private boolean enabled = true;

    /**
     * 存储配置
     */
    private Storage storage = new Storage();

    /**
     * 是否自动学习
     */
    private boolean autoLearn = false;

    /**
     * 最大历史消息数
     */
    private int maxHistory = 100;

    /**
     * 审计配置
     */
    private Audit audit = new Audit();

    /**
     * 知识库配置
     */
    private Knowledge knowledge = new Knowledge();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public boolean isAutoLearn() {
        return autoLearn;
    }

    public void setAutoLearn(boolean autoLearn) {
        this.autoLearn = autoLearn;
    }

    public int getMaxHistory() {
        return maxHistory;
    }

    public void setMaxHistory(int maxHistory) {
        this.maxHistory = maxHistory;
    }

    public Audit getAudit() {
        return audit;
    }

    public void setAudit(Audit audit) {
        this.audit = audit;
    }

    public Knowledge getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(Knowledge knowledge) {
        this.knowledge = knowledge;
    }

    /**
     * 存储配置
     */
    public static class Storage {
        /**
         * 存储类型：file, memory, database
         */
        private String type = "memory";

        /**
         * 文件存储路径（当 type=file 时有效）
         */
        private String path = System.getProperty("user.home") + "/.ooder/data/conversations";

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

    /**
     * 审计配置
     */
    public static class Audit {
        /**
         * 是否启用审计
         */
        private boolean enabled = true;

        /**
         * 是否包含工具调用记录
         */
        private boolean includeToolCalls = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isIncludeToolCalls() {
            return includeToolCalls;
        }

        public void setIncludeToolCalls(boolean includeToolCalls) {
            this.includeToolCalls = includeToolCalls;
        }
    }

    /**
     * 知识库配置
     */
    public static class Knowledge {
        /**
         * 是否自动更新知识库
         */
        private boolean autoUpdate = false;

        /**
         * 最小内容长度（用于学习过滤）
         */
        private int minContentLength = 50;

        public boolean isAutoUpdate() {
            return autoUpdate;
        }

        public void setAutoUpdate(boolean autoUpdate) {
            this.autoUpdate = autoUpdate;
        }

        public int getMinContentLength() {
            return minContentLength;
        }

        public void setMinContentLength(int minContentLength) {
            this.minContentLength = minContentLength;
        }
    }
}
