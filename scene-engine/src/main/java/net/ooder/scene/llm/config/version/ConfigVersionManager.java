package net.ooder.scene.llm.config.version;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 配置版本管理器
 *
 * <p>管理配置版本，检测版本变化</p>
 *
 * @author ooder
 * @since 2.4
 */
public class ConfigVersionManager {

    private static final Logger log = LoggerFactory.getLogger(ConfigVersionManager.class);
    private static final String VERSION_FILE = ".config-version";

    /**
     * 检查是否需要更新配置
     *
     * @param configDir 配置目录
     * @param skillId Skill ID
     * @param currentVersion 当前版本
     * @return 是否需要更新
     */
    public boolean needsUpdate(Path configDir, String skillId, String currentVersion) {
        Path versionFile = configDir.resolve(skillId).resolve(VERSION_FILE);

        if (!Files.exists(versionFile)) {
            return true;
        }

        BufferedReader reader = null;
        try {
            reader = Files.newBufferedReader(versionFile, StandardCharsets.UTF_8);
            String storedVersion = reader.readLine();
            if (storedVersion != null) {
                storedVersion = storedVersion.trim();
            }
            return !currentVersion.equals(storedVersion);
        } catch (IOException e) {
            return true;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    /**
     * 记录配置版本
     *
     * @param configDir 配置目录
     * @param skillId Skill ID
     * @param version 版本
     */
    public void recordVersion(Path configDir, String skillId, String version) {
        Path versionFile = configDir.resolve(skillId).resolve(VERSION_FILE);
        try {
            Files.createDirectories(versionFile.getParent());
            BufferedWriter writer = Files.newBufferedWriter(versionFile, StandardCharsets.UTF_8);
            try {
                writer.write(version);
            } finally {
                writer.close();
            }
        } catch (IOException e) {
            log.warn("Failed to record version: {}", e.getMessage());
        }
    }

    /**
     * 获取存储的版本
     *
     * @param configDir 配置目录
     * @param skillId Skill ID
     * @return 存储的版本，如果不存在返回 null
     */
    public String getStoredVersion(Path configDir, String skillId) {
        Path versionFile = configDir.resolve(skillId).resolve(VERSION_FILE);

        if (!Files.exists(versionFile)) {
            return null;
        }

        BufferedReader reader = null;
        try {
            reader = Files.newBufferedReader(versionFile, StandardCharsets.UTF_8);
            return reader.readLine();
        } catch (IOException e) {
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    /**
     * 清除版本记录
     *
     * @param configDir 配置目录
     * @param skillId Skill ID
     */
    public void clearVersion(Path configDir, String skillId) {
        Path versionFile = configDir.resolve(skillId).resolve(VERSION_FILE);
        try {
            Files.deleteIfExists(versionFile);
        } catch (IOException e) {
            // ignore
        }
    }
}
