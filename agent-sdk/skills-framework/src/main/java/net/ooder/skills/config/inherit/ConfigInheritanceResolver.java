package net.ooder.skills.config.inherit;

import net.ooder.skills.config.ConfigNode;

import java.util.*;

/**
 * 配置继承解析器
 *
 * @author Agent-SDK Team
 * @version 2.4.0
 * @since 2.4.0
 */
public class ConfigInheritanceResolver {

    /**
     * 合并两个配置节点
     *
     * @param parent 父配置
     * @param child  子配置
     * @return 合并后的配置
     */
    public ConfigNode merge(ConfigNode parent, ConfigNode child) {
        if (parent == null) {
            return child != null ? child : new ConfigNode();
        }
        if (child == null) {
            return parent;
        }
        return parent.merge(child);
    }

    /**
     * 合并多个配置节点
     *
     * @param configs 配置列表（从底层到高层）
     * @return 合并后的配置
     */
    public ConfigNode mergeAll(List<ConfigNode> configs) {
        if (configs == null || configs.isEmpty()) {
            return new ConfigNode();
        }

        ConfigNode result = new ConfigNode();
        for (ConfigNode config : configs) {
            if (config != null) {
                result = result.merge(config);
            }
        }
        return result;
    }

    /**
     * 解析继承链
     *
     * @param targetType 目标类型
     * @param targetId   目标ID
     * @param loader     配置加载器
     * @return 继承链
     */
    public InheritanceChain resolveChain(String targetType, String targetId, ConfigLoader loader) {
        InheritanceChain chain = new InheritanceChain();
        chain.setTargetType(targetType);
        chain.setTargetId(targetId);

        // 添加系统级配置
        ConfigNode systemConfig = loader.load("system", "system");
        if (systemConfig != null) {
            chain.addLevel("system", "system-config.json", systemConfig);
        }

        // 根据目标类型添加相应配置
        if ("skill".equals(targetType) || "scene".equals(targetType) || "internal_skill".equals(targetType)) {
            ConfigNode skillConfig = loader.load("skill", targetId);
            if (skillConfig != null) {
                chain.addLevel("skill", "skill-config.json", skillConfig);
            }
        }

        if ("scene".equals(targetType) || "internal_skill".equals(targetType)) {
            ConfigNode sceneConfig = loader.load("scene", targetId);
            if (sceneConfig != null) {
                chain.addLevel("scene", "scene-config.json", sceneConfig);
            }
        }

        if ("internal_skill".equals(targetType)) {
            String[] parts = targetId.split(":");
            if (parts.length == 2) {
                ConfigNode internalConfig = loader.load("internal_skill", parts[0] + ":" + parts[1]);
                if (internalConfig != null) {
                    chain.addLevel("internal_skill", "internal-skill-config.json", internalConfig);
                }
            }
        }

        return chain;
    }

    /**
     * 配置加载器接口
     */
    public interface ConfigLoader {
        ConfigNode load(String targetType, String targetId);
    }

    /**
     * 继承链
     */
    public static class InheritanceChain {
        private String targetType;
        private String targetId;
        private final List<ChainLevel> levels = new ArrayList<>();

        public String getTargetType() {
            return targetType;
        }

        public void setTargetType(String targetType) {
            this.targetType = targetType;
        }

        public String getTargetId() {
            return targetId;
        }

        public void setTargetId(String targetId) {
            this.targetId = targetId;
        }

        public void addLevel(String level, String source, ConfigNode config) {
            levels.add(new ChainLevel(level, source, config));
        }

        public List<ChainLevel> getLevels() {
            return Collections.unmodifiableList(levels);
        }

        /**
         * 获取继承值
         *
         * @param key 配置键
         * @return 继承的配置值
         */
        public ConfigNode getInheritedValue(String key) {
            // 从最底层开始查找
            for (int i = levels.size() - 1; i >= 0; i--) {
                ChainLevel level = levels.get(i);
                if (level.config.containsKey(key)) {
                    return level.config.getNode(key);
                }
            }
            return null;
        }

        /**
         * 合并整个链
         *
         * @return 合并后的配置
         */
        public ConfigNode merge() {
            ConfigNode result = new ConfigNode();
            for (ChainLevel level : levels) {
                result = result.merge(level.config);
            }
            return result;
        }
    }

    /**
     * 链层级
     */
    public static class ChainLevel {
        private final String level;
        private final String source;
        private final ConfigNode config;

        public ChainLevel(String level, String source, ConfigNode config) {
            this.level = level;
            this.source = source;
            this.config = config;
        }

        public String getLevel() {
            return level;
        }

        public String getSource() {
            return source;
        }

        public ConfigNode getConfig() {
            return config;
        }
    }
}
