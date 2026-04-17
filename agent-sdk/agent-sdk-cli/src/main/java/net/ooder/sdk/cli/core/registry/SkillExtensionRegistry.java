package net.ooder.sdk.cli.core.registry;

import net.ooder.sdk.cli.api.SkillCliExtension;
import net.ooder.sdk.cli.config.SkillCliConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Skill 扩展注册表
 *
 * <p>管理 SkillCliExtension 的注册和发现</p>
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class SkillExtensionRegistry {

    private static final Logger logger = LoggerFactory.getLogger(SkillExtensionRegistry.class);

    private final Map<String, SkillCliExtension> extensions = new ConcurrentHashMap<>();
    private final Map<String, String> commandToSkillMap = new ConcurrentHashMap<>();

    /**
     * 注册扩展
     */
    public void register(SkillCliExtension extension) {
        if (extension == null) {
            logger.warn("Cannot register null extension");
            return;
        }

        String skillId = extension.getSkillId();
        String command = extension.getCommand();

        if (skillId == null || skillId.isEmpty()) {
            logger.warn("Cannot register extension with empty skillId");
            return;
        }

        if (command == null || command.isEmpty()) {
            logger.warn("Cannot register extension with empty command");
            return;
        }

        extensions.put(skillId, extension);
        commandToSkillMap.put(command, skillId);

        try {
            extension.initialize();
            logger.info("Registered skill extension: {} (command: {})", skillId, command);
        } catch (Exception e) {
            logger.error("Failed to initialize extension: {}", skillId, e);
        }
    }

    /**
     * 注销扩展
     */
    public void unregister(String skillId) {
        SkillCliExtension extension = extensions.remove(skillId);
        if (extension != null) {
            commandToSkillMap.remove(extension.getCommand());
            try {
                extension.destroy();
                logger.info("Unregistered skill extension: {}", skillId);
            } catch (Exception e) {
                logger.error("Failed to destroy extension: {}", skillId, e);
            }
        }
    }

    /**
     * 获取扩展
     */
    public SkillCliExtension getExtension(String skillId) {
        return extensions.get(skillId);
    }

    /**
     * 通过 Skill ID 获取扩展
     */
    public SkillCliExtension getExtensionBySkillId(String skillId) {
        return extensions.get(skillId);
    }

    /**
     * 通过命令获取扩展
     */
    public SkillCliExtension getExtensionByCommand(String command) {
        String skillId = commandToSkillMap.get(command);
        if (skillId != null) {
            return extensions.get(skillId);
        }
        return null;
    }

    /**
     * 获取所有扩展
     */
    public List<SkillCliExtension> getAllExtensions() {
        return new ArrayList<>(extensions.values());
    }

    /**
     * 获取所有 Skill ID
     */
    public Set<String> getAllSkillIds() {
        return new HashSet<>(extensions.keySet());
    }

    /**
     * 获取所有命令
     */
    public Set<String> getAllCommands() {
        return new HashSet<>(commandToSkillMap.keySet());
    }

    /**
     * 判断是否包含扩展
     */
    public boolean contains(String skillId) {
        return extensions.containsKey(skillId);
    }

    /**
     * 判断是否包含命令
     */
    public boolean containsCommand(String command) {
        return commandToSkillMap.containsKey(command);
    }

    /**
     * 清空所有扩展
     */
    public void clear() {
        for (SkillCliExtension extension : extensions.values()) {
            try {
                extension.destroy();
            } catch (Exception e) {
                logger.error("Failed to destroy extension during clear", e);
            }
        }
        extensions.clear();
        commandToSkillMap.clear();
        logger.info("Cleared all skill extensions");
    }

    /**
     * 从配置加载扩展
     */
    public void loadFromConfig(SkillCliConfiguration config) {
        if (config == null || config.getSkill() == null || config.getSkill().getCli() == null) {
            logger.warn("Invalid configuration, skipping load");
            return;
        }

        List<SkillCliConfiguration.ExtensionConfig> extensionConfigs = config.getSkill().getCli().getExtensions();
        if (extensionConfigs == null) {
            logger.debug("No extensions configured");
            return;
        }

        for (SkillCliConfiguration.ExtensionConfig extConfig : extensionConfigs) {
            if (!extConfig.isEnabled()) {
                logger.debug("Skipping disabled extension: {}", extConfig.getSkillId());
                continue;
            }

            try {
                loadExtensionFromConfig(extConfig);
            } catch (Exception e) {
                logger.error("Failed to load extension from config: {}", extConfig.getSkillId(), e);
            }
        }
    }

    /**
     * 从配置加载单个扩展
     */
    private void loadExtensionFromConfig(SkillCliConfiguration.ExtensionConfig config) throws Exception {
        String handlerClass = config.getHandler();
        if (handlerClass == null || handlerClass.isEmpty()) {
            logger.warn("No handler class specified for extension: {}", config.getSkillId());
            return;
        }

        Class<?> clazz = Class.forName(handlerClass);
        if (!SkillCliExtension.class.isAssignableFrom(clazz)) {
            logger.error("Handler class does not implement SkillCliExtension: {}", handlerClass);
            return;
        }

        SkillCliExtension extension = (SkillCliExtension) clazz.getDeclaredConstructor().newInstance();
        register(extension);
    }

    /**
     * 获取扩展数量
     */
    public int size() {
        return extensions.size();
    }
}
