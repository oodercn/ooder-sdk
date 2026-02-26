package net.ooder.sdk.api.skill;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Skill 类型注册表
 * 用于动态注册和管理 Skill 类型，避免硬编码
 */
public class SkillTypeRegistry {

    private static final Map<String, SkillTypeInfo> TYPE_REGISTRY = new ConcurrentHashMap<>();

    static {
        registerDefaultTypes();
    }

    private static void registerDefaultTypes() {
        registerType("skill-org", "Organization", "组织管理 Skill");
        registerType("skill-vfs", "Virtual File System", "虚拟文件系统 Skill");
        registerType("skill-msg", "Message", "消息服务 Skill");
        registerType("skill-agent", "Agent", "智能代理 Skill");
        registerType("skill-workflow", "Workflow", "工作流 Skill");
        registerType("skill-a2ui", "A2UI", "UI生成 Skill");
        registerType("skill-user-auth", "User Auth", "用户认证 Skill");
    }

    /**
     * 注册 Skill 类型
     */
    public static void registerType(String typeId, String name, String description) {
        TYPE_REGISTRY.put(typeId, new SkillTypeInfo(typeId, name, description));
    }

    /**
     * 获取 Skill 类型信息
     */
    public static SkillTypeInfo getTypeInfo(String typeId) {
        return TYPE_REGISTRY.get(typeId);
    }

    /**
     * 检查类型是否已注册
     */
    public static boolean isRegistered(String typeId) {
        return TYPE_REGISTRY.containsKey(typeId);
    }

    /**
     * 获取所有已注册类型
     */
    public static Collection<SkillTypeInfo> getAllTypes() {
        return Collections.unmodifiableCollection(TYPE_REGISTRY.values());
    }

    /**
     * 根据类型 ID 获取常量值（向后兼容）
     */
    public static String getTypeConstant(String typeId) {
        return typeId;
    }

    /**
     * Skill 类型信息
     */
    public static class SkillTypeInfo {
        private final String typeId;
        private final String name;
        private final String description;
        private final Map<String, Object> metadata = new HashMap<>();

        public SkillTypeInfo(String typeId, String name, String description) {
            this.typeId = typeId;
            this.name = name;
            this.description = description;
        }

        public String getTypeId() {
            return typeId;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public void setMetadata(String key, Object value) {
            metadata.put(key, value);
        }

        public Object getMetadata(String key) {
            return metadata.get(key);
        }
    }
}
