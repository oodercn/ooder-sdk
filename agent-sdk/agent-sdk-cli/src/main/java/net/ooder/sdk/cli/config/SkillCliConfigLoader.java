package net.ooder.sdk.cli.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Skill CLI 配置加载器
 *
 * <p>支持从 skill.yaml 文件加载配置</p>
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class SkillCliConfigLoader {

    private static final Logger logger = LoggerFactory.getLogger(SkillCliConfigLoader.class);

    private static final String DEFAULT_CONFIG_FILE = "skill.yaml";
    private static final String[] CONFIG_LOCATIONS = {
        "./skill.yaml",
        "./skill.yml",
        "./config/skill.yaml",
        "./config/skill.yml",
        "src/main/resources/skill.yaml",
        "src/main/resources/skill.yml"
    };

    private final ObjectMapper yamlMapper;

    public SkillCliConfigLoader() {
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
    }

    /**
     * 加载默认配置
     */
    public SkillCliConfiguration load() {
        for (String location : CONFIG_LOCATIONS) {
            Path path = Paths.get(location);
            if (Files.exists(path)) {
                logger.info("Loading CLI configuration from: {}", path.toAbsolutePath());
                return loadFromFile(path.toFile());
            }
        }

        logger.warn("No skill.yaml configuration found, using default configuration");
        return createDefaultConfig();
    }

    /**
     * 从指定路径加载配置
     */
    public SkillCliConfiguration load(String configPath) {
        File file = new File(configPath);
        if (!file.exists()) {
            logger.warn("Configuration file not found: {}, using default configuration", configPath);
            return createDefaultConfig();
        }
        return loadFromFile(file);
    }

    /**
     * 从文件加载配置
     */
    public SkillCliConfiguration loadFromFile(File file) {
        try (InputStream is = new FileInputStream(file)) {
            return yamlMapper.readValue(is, SkillCliConfiguration.class);
        } catch (Exception e) {
            logger.error("Failed to load configuration from: {}", file.getAbsolutePath(), e);
            return createDefaultConfig();
        }
    }

    /**
     * 从类路径加载配置
     */
    public SkillCliConfiguration loadFromClasspath(String resourcePath) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                logger.warn("Resource not found in classpath: {}", resourcePath);
                return createDefaultConfig();
            }
            return yamlMapper.readValue(is, SkillCliConfiguration.class);
        } catch (Exception e) {
            logger.error("Failed to load configuration from classpath: {}", resourcePath, e);
            return createDefaultConfig();
        }
    }

    /**
     * 加载多个配置文件并合并
     */
    public SkillCliConfiguration loadMultiple(String... configPaths) {
        SkillCliConfiguration mergedConfig = createDefaultConfig();

        for (String path : configPaths) {
            SkillCliConfiguration config = load(path);
            mergeConfig(mergedConfig, config);
        }

        return mergedConfig;
    }

    /**
     * 扫描目录加载所有 skill.yaml 配置
     */
    public List<SkillCliConfiguration> scanDirectory(String directory) {
        List<SkillCliConfiguration> configs = new ArrayList<>();
        File dir = new File(directory);

        if (!dir.isDirectory()) {
            logger.warn("Not a directory: {}", directory);
            return configs;
        }

        File[] files = dir.listFiles((d, name) ->
            name.equals("skill.yaml") || name.equals("skill.yml"));

        if (files != null) {
            for (File file : files) {
                try {
                    SkillCliConfiguration config = loadFromFile(file);
                    configs.add(config);
                    logger.info("Loaded configuration from: {}", file.getAbsolutePath());
                } catch (Exception e) {
                    logger.error("Failed to load configuration from: {}", file.getAbsolutePath(), e);
                }
            }
        }

        return configs;
    }

    /**
     * 创建默认配置
     */
    private SkillCliConfiguration createDefaultConfig() {
        SkillCliConfiguration config = new SkillCliConfiguration();

        SkillCliConfiguration.SkillConfig skillConfig = new SkillCliConfiguration.SkillConfig();
        SkillCliConfiguration.CliConfig cliConfig = new SkillCliConfiguration.CliConfig();

        SkillCliConfiguration.SecurityConfig securityConfig = new SkillCliConfiguration.SecurityConfig();
        securityConfig.setEnabled(true);
        securityConfig.setAuditEnabled(true);
        securityConfig.setInjectionCheckEnabled(true);

        SkillCliConfiguration.OutputConfig outputConfig = new SkillCliConfiguration.OutputConfig();
        outputConfig.setFormat("text");
        outputConfig.setColorEnabled(true);
        outputConfig.setVerbose(false);

        cliConfig.setSecurity(securityConfig);
        cliConfig.setOutput(outputConfig);
        skillConfig.setCli(cliConfig);
        config.setSkill(skillConfig);

        return config;
    }

    /**
     * 合并配置
     */
    private void mergeConfig(SkillCliConfiguration target, SkillCliConfiguration source) {
        if (source.getSkill() != null) {
            if (target.getSkill() == null) {
                target.setSkill(source.getSkill());
            } else {
                if (source.getSkill().getCli() != null) {
                    if (target.getSkill().getCli() == null) {
                        target.getSkill().setCli(source.getSkill().getCli());
                    } else {
                        // 合并扩展列表
                        if (source.getSkill().getCli().getExtensions() != null) {
                            if (target.getSkill().getCli().getExtensions() == null) {
                                target.getSkill().getCli().setExtensions(source.getSkill().getCli().getExtensions());
                            } else {
                                target.getSkill().getCli().getExtensions().addAll(
                                    source.getSkill().getCli().getExtensions());
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 验证配置
     */
    public boolean validate(SkillCliConfiguration config) {
        if (config == null) {
            logger.error("Configuration is null");
            return false;
        }

        if (config.getSkill() == null) {
            logger.error("Skill configuration is null");
            return false;
        }

        if (config.getSkill().getCli() == null) {
            logger.warn("CLI configuration is null, using defaults");
        }

        return true;
    }
}
