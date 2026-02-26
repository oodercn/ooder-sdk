package net.ooder.skills.container.api;

import java.util.List;
import java.util.Map;

/**
 * 能力注册中心
 *
 * Skills 通过此接口注册自己的能力
 * SceneEngine 通过此接口发现能力
 */
public interface CapabilityRegistry {

    /**
     * 注册能力
     */
    void registerCapability(String skillId, Capability capability);

    /**
     * 注销能力
     */
    void unregisterCapability(String skillId, String capabilityId);

    /**
     * 发现能力
     */
    List<Capability> discoverCapabilities(String category);

    /**
     * 获取能力提供者
     */
    String getProviderSkill(String capabilityId);

    /**
     * 调用能力
     */
    Object invokeCapability(String capabilityId, Map<String, Object> params);

    /**
     * 获取所有已注册能力
     */
    List<Capability> getAllCapabilities();

    /**
     * 能力定义
     */
    class Capability {
        private String id;
        private String name;
        private String category;
        private String description;
        private String version;
        private Map<String, Object> metadata;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public Map<String, Object> getMetadata() {
            return metadata;
        }

        public void setMetadata(Map<String, Object> metadata) {
            this.metadata = metadata;
        }
    }
}
