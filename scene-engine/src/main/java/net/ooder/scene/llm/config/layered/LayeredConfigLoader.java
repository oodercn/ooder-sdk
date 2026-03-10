package net.ooder.scene.llm.config.layered;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 多级配置加载器
 *
 * <p>支持四级配置加载：</p>
 * <ol>
 *   <li>Level 1: 系统默认配置 (classpath:)</li>
 *   <li>Level 2: 环境配置 (config/env/{env}/)</li>
 *   <li>Level 3: 应用配置 (config/app/)</li>
 *   <li>Level 4: 用户配置 (~/.ooder/)</li>
 * </ol>
 *
 * @author ooder
 * @since 2.4
 */
public class LayeredConfigLoader {

    private static final Logger log = LoggerFactory.getLogger(LayeredConfigLoader.class);

    private static final String DEFAULT_CONFIG = "llm-config-default.yaml";
    private static final String USER_CONFIG_DIR = ".ooder";

    private final ObjectMapper yamlMapper;
    private final Path configBaseDir;
    private final String environment;
    private final ConfigMergeStrategy mergeStrategy;

    public LayeredConfigLoader() {
        this(null, System.getProperty("ooder.env", "dev"));
    }

    public LayeredConfigLoader(Path configBaseDir, String environment) {
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
        this.configBaseDir = configBaseDir != null ? configBaseDir : Paths.get("config");
        this.environment = environment != null ? environment : "dev";
        this.mergeStrategy = new DeepMergeStrategy();
    }

    /**
     * 加载配置（合并所有层级）
     */
    public LlmConfigProperties load() {
        return load(null);
    }

    /**
     * 加载指定 Skill 的配置
     */
    public LlmConfigProperties load(String skillId) {
        LlmConfigProperties config = new LlmConfigProperties();

        // Level 1: 系统默认配置
        LlmConfigProperties level1 = loadSystemDefault(skillId);
        if (level1 != null) {
            config = mergeStrategy.merge(config, level1);
            log.debug("Loaded system default config");
        }

        // Level 2: 环境配置
        LlmConfigProperties level2 = loadEnvironmentConfig(skillId);
        if (level2 != null) {
            config = mergeStrategy.merge(config, level2);
            log.debug("Loaded environment config for: {}", environment);
        }

        // Level 3: 应用配置
        LlmConfigProperties level3 = loadAppConfig(skillId);
        if (level3 != null) {
            config = mergeStrategy.merge(config, level3);
            log.debug("Loaded app config");
        }

        // Level 4: 用户配置
        LlmConfigProperties level4 = loadUserConfig(skillId);
        if (level4 != null) {
            config = mergeStrategy.merge(config, level4);
            log.debug("Loaded user config");
        }

        // 应用环境变量覆盖
        applyEnvironmentVariables(config);

        return config;
    }

    /**
     * 加载系统默认配置
     */
    private LlmConfigProperties loadSystemDefault(String skillId) {
        String resourcePath = skillId != null 
            ? "skills/" + skillId + "/" + DEFAULT_CONFIG 
            : DEFAULT_CONFIG;

        InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (is != null) {
            try {
                return yamlMapper.readValue(is, LlmConfigProperties.class);
            } catch (IOException e) {
                log.warn("Failed to load system default config: {}", e.getMessage());
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return null;
    }

    /**
     * 加载环境配置
     */
    private LlmConfigProperties loadEnvironmentConfig(String skillId) {
        Path configPath = skillId != null
            ? configBaseDir.resolve("env").resolve(environment).resolve("skills").resolve(skillId).resolve("skill-config.yaml")
            : configBaseDir.resolve("env").resolve(environment).resolve("llm-config.yaml");

        return loadFromFile(configPath);
    }

    /**
     * 加载应用配置
     */
    private LlmConfigProperties loadAppConfig(String skillId) {
        Path configPath = skillId != null
            ? configBaseDir.resolve("app").resolve("skills").resolve(skillId).resolve("skill-config.yaml")
            : configBaseDir.resolve("app").resolve("llm-config.yaml");

        return loadFromFile(configPath);
    }

    /**
     * 加载用户配置
     */
    private LlmConfigProperties loadUserConfig(String skillId) {
        String userHome = System.getProperty("user.home");
        Path configPath = skillId != null
            ? Paths.get(userHome, USER_CONFIG_DIR, "skills", skillId, "skill-config.yaml")
            : Paths.get(userHome, USER_CONFIG_DIR, "llm-config.yaml");

        return loadFromFile(configPath);
    }

    /**
     * 从文件加载配置
     */
    private LlmConfigProperties loadFromFile(Path path) {
        if (!Files.exists(path)) {
            return null;
        }

        try {
            return yamlMapper.readValue(path.toFile(), LlmConfigProperties.class);
        } catch (IOException e) {
            log.warn("Failed to load config from {}: {}", path, e.getMessage());
            return null;
        }
    }

    /**
     * 应用环境变量覆盖
     */
    private void applyEnvironmentVariables(LlmConfigProperties config) {
        Map<String, String> envVars = System.getenv();

        // OODER_LLM_PROVIDER
        String provider = envVars.get("OODER_LLM_PROVIDER");
        if (provider != null && !provider.isEmpty()) {
            config.setProvider(provider);
        }

        // OODER_LLM_MODEL
        String model = envVars.get("OODER_LLM_MODEL");
        if (model != null && !model.isEmpty()) {
            config.setModel(model);
        }

        // OODER_LLM_API_KEY
        String apiKey = envVars.get("OODER_LLM_API_KEY");
        if (apiKey != null && !apiKey.isEmpty()) {
            config.setApiKey(apiKey);
        }

        // OODER_LLM_BASE_URL
        String baseUrl = envVars.get("OODER_LLM_BASE_URL");
        if (baseUrl != null && !baseUrl.isEmpty()) {
            config.setBaseUrl(baseUrl);
        }

        // OODER_LLM_TEMPERATURE
        String temperature = envVars.get("OODER_LLM_TEMPERATURE");
        if (temperature != null && !temperature.isEmpty()) {
            try {
                config.setTemperature(Double.parseDouble(temperature));
            } catch (NumberFormatException e) {
                log.warn("Invalid OODER_LLM_TEMPERATURE value: {}", temperature);
            }
        }

        // OODER_LLM_MAX_TOKENS
        String maxTokens = envVars.get("OODER_LLM_MAX_TOKENS");
        if (maxTokens != null && !maxTokens.isEmpty()) {
            try {
                config.setMaxTokens(Integer.parseInt(maxTokens));
            } catch (NumberFormatException e) {
                log.warn("Invalid OODER_LLM_MAX_TOKENS value: {}", maxTokens);
            }
        }
    }

    public String getEnvironment() {
        return environment;
    }

    public Path getConfigBaseDir() {
        return configBaseDir;
    }
}
