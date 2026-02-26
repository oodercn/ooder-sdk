package net.ooder.sdk.discovery;

import net.ooder.sdk.plugin.SkillDependency;
import net.ooder.sdk.plugin.SkillMetadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * skill.yaml解析器
 *
 * <p>解析Skill包中的skill.yaml配置文件，提取Skill元数据。</p>
 *
 * <p>支持从以下位置读取skill.yaml:</p>
 * <ul>
 *   <li>JAR包根目录: skill.yaml</li>
 *   <li>JAR包META-INF目录: META-INF/skill.yaml</li>
 *   <li>文件系统: 直接读取yaml文件</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class SkillYamlParser {

    private static final Logger log = LoggerFactory.getLogger(SkillYamlParser.class);

    /**
     * skill.yaml在JAR包中的可能路径
     */
    private static final String[] SKILL_YAML_PATHS = {
            "skill.yaml",
            "META-INF/skill.yaml",
            "BOOT-INF/classes/skill.yaml"
    };

    private final Yaml yaml;

    public SkillYamlParser() {
        this.yaml = new Yaml();
    }

    // ==================== 核心解析方法 ====================

    /**
     * 从JAR文件解析skill.yaml
     *
     * @param jarFile JAR文件
     * @return Skill元数据
     * @throws SkillParseException 解析失败时抛出
     */
    public SkillMetadata parseFromJar(File jarFile) throws SkillParseException {
        if (jarFile == null || !jarFile.exists()) {
            throw new SkillParseException("JAR file does not exist: " + (jarFile != null ? jarFile.getPath() : "null"));
        }

        if (!jarFile.getName().endsWith(".jar")) {
            throw new SkillParseException("Not a JAR file: " + jarFile.getName());
        }

        log.debug("Parsing skill.yaml from JAR: {}", jarFile.getAbsolutePath());

        try (JarFile jar = new JarFile(jarFile)) {
            // 尝试从多个路径读取skill.yaml
            for (String path : SKILL_YAML_PATHS) {
                JarEntry entry = jar.getJarEntry(path);
                if (entry != null) {
                    log.debug("Found skill.yaml at: {}", path);
                    try (InputStream is = jar.getInputStream(entry)) {
                        return parseFromInputStream(is);
                    }
                }
            }

            throw new SkillParseException("skill.yaml not found in JAR: " + jarFile.getName());

        } catch (IOException e) {
            throw new SkillParseException("Failed to read JAR file: " + jarFile.getName(), e);
        }
    }

    /**
     * 从YAML文件解析
     *
     * @param yamlFile YAML文件
     * @return Skill元数据
     * @throws SkillParseException 解析失败时抛出
     */
    public SkillMetadata parseFromFile(File yamlFile) throws SkillParseException {
        if (yamlFile == null || !yamlFile.exists()) {
            throw new SkillParseException("YAML file does not exist: " + (yamlFile != null ? yamlFile.getPath() : "null"));
        }

        log.debug("Parsing skill.yaml from file: {}", yamlFile.getAbsolutePath());

        try (FileInputStream fis = new FileInputStream(yamlFile)) {
            return parseFromInputStream(fis);
        } catch (IOException e) {
            throw new SkillParseException("Failed to read YAML file: " + yamlFile.getName(), e);
        }
    }

    /**
     * 从输入流解析
     *
     * @param inputStream YAML输入流
     * @return Skill元数据
     * @throws SkillParseException 解析失败时抛出
     */
    public SkillMetadata parseFromInputStream(InputStream inputStream) throws SkillParseException {
        if (inputStream == null) {
            throw new SkillParseException("Input stream is null");
        }

        try {
            Map<String, Object> yamlData = yaml.load(inputStream);
            if (yamlData == null) {
                throw new SkillParseException("YAML content is empty");
            }

            return convertToMetadata(yamlData);

        } catch (Exception e) {
            if (e instanceof SkillParseException) {
                throw (SkillParseException) e;
            }
            throw new SkillParseException("Failed to parse YAML content", e);
        }
    }

    /**
     * 从字符串解析
     *
     * @param yamlContent YAML内容字符串
     * @return Skill元数据
     * @throws SkillParseException 解析失败时抛出
     */
    public SkillMetadata parseFromString(String yamlContent) throws SkillParseException {
        if (yamlContent == null || yamlContent.trim().isEmpty()) {
            throw new SkillParseException("YAML content is empty");
        }

        try {
            Map<String, Object> yamlData = yaml.load(yamlContent);
            if (yamlData == null) {
                throw new SkillParseException("YAML content is empty");
            }

            return convertToMetadata(yamlData);

        } catch (Exception e) {
            if (e instanceof SkillParseException) {
                throw (SkillParseException) e;
            }
            throw new SkillParseException("Failed to parse YAML content", e);
        }
    }

    // ==================== 转换方法 ====================

    /**
     * 将YAML数据转换为SkillMetadata
     *
     * @param yamlData YAML数据Map
     * @return SkillMetadata对象
     * @throws SkillParseException 转换失败时抛出
     */
    @SuppressWarnings("unchecked")
    private SkillMetadata convertToMetadata(Map<String, Object> yamlData) throws SkillParseException {
        SkillMetadata metadata = new SkillMetadata();

        // 解析基础信息（必填）
        metadata.setId(getRequiredString(yamlData, "id"));
        metadata.setName(getRequiredString(yamlData, "name"));
        metadata.setVersion(getRequiredString(yamlData, "version"));
        metadata.setDescription(getString(yamlData, "description", ""));
        metadata.setAuthor(getString(yamlData, "author", ""));
        metadata.setType(getString(yamlData, "type", "service"));

        // 解析分类信息（可选）
        metadata.setCategory(getString(yamlData, "category", ""));
        metadata.setTags(getStringList(yamlData, "tags"));

        // 解析依赖（可选）
        metadata.setDependencies(parseDependencies(yamlData.get("dependencies")));

        // 解析能力声明（可选）
        metadata.setCapabilities(getMapList(yamlData, "capabilities"));

        // 解析路由配置（可选）
        metadata.setRoutes(getMapList(yamlData, "routes"));

        // 解析服务配置（可选）
        metadata.setServices(getMapList(yamlData, "services"));

        // 解析UI配置（可选）
        metadata.setUi(getMap(yamlData, "ui"));

        // 解析权限配置（可选）
        metadata.setPermissions(getMapList(yamlData, "permissions"));

        // 解析配置项定义（可选）
        metadata.setConfiguration(getMapList(yamlData, "configuration"));

        // 解析健康检查配置（可选）
        metadata.setHealthCheck(getMap(yamlData, "healthCheck"));

        // 解析监控指标配置（可选）
        metadata.setMetrics(getMap(yamlData, "metrics"));

        // 验证必填字段
        validateMetadata(metadata);

        return metadata;
    }

    /**
     * 解析依赖列表
     *
     * @param dependenciesData 依赖数据
     * @return 依赖列表
     */
    @SuppressWarnings("unchecked")
    private List<SkillDependency> parseDependencies(Object dependenciesData) {
        List<SkillDependency> dependencies = new ArrayList<>();

        if (dependenciesData == null) {
            return dependencies;
        }

        if (!(dependenciesData instanceof List)) {
            log.warn("Dependencies should be a list");
            return dependencies;
        }

        List<Map<String, Object>> depList = (List<Map<String, Object>>) dependenciesData;
        for (Map<String, Object> depData : depList) {
            SkillDependency dependency = new SkillDependency();

            dependency.setId(getString(depData, "id", null));
            dependency.setVersion(getString(depData, "version", "*"));
            dependency.setOptional(getBoolean(depData, "optional", false));

            if (dependency.getId() != null && !dependency.getId().isEmpty()) {
                dependencies.add(dependency);
            } else {
                log.warn("Skipping dependency with missing id");
            }
        }

        return dependencies;
    }

    // ==================== 辅助方法 ====================

    /**
     * 获取必填字符串字段
     *
     * @param data 数据Map
     * @param key 字段名
     * @return 字段值
     * @throws SkillParseException 字段缺失或为空时抛出
     */
    private String getRequiredString(Map<String, Object> data, String key) throws SkillParseException {
        Object value = data.get(key);
        if (value == null) {
            throw new SkillParseException("Required field '" + key + "' is missing");
        }
        String strValue = value.toString().trim();
        if (strValue.isEmpty()) {
            throw new SkillParseException("Required field '" + key + "' is empty");
        }
        return strValue;
    }

    /**
     * 获取字符串字段
     *
     * @param data 数据Map
     * @param key 字段名
     * @param defaultValue 默认值
     * @return 字段值或默认值
     */
    private String getString(Map<String, Object> data, String key, String defaultValue) {
        Object value = data.get(key);
        if (value == null) {
            return defaultValue;
        }
        String strValue = value.toString().trim();
        return strValue.isEmpty() ? defaultValue : strValue;
    }

    /**
     * 获取布尔字段
     *
     * @param data 数据Map
     * @param key 字段名
     * @param defaultValue 默认值
     * @return 字段值或默认值
     */
    private boolean getBoolean(Map<String, Object> data, String key, boolean defaultValue) {
        Object value = data.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return Boolean.parseBoolean(value.toString());
    }

    /**
     * 获取字符串列表
     *
     * @param data 数据Map
     * @param key 字段名
     * @return 字符串列表
     */
    @SuppressWarnings("unchecked")
    private List<String> getStringList(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null) {
            return new ArrayList<>();
        }
        if (value instanceof List) {
            List<String> result = new ArrayList<>();
            for (Object item : (List<?>) value) {
                if (item != null) {
                    result.add(item.toString());
                }
            }
            return result;
        }
        return new ArrayList<>();
    }

    /**
     * 获取Map
     *
     * @param data 数据Map
     * @param key 字段名
     * @return Map对象
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getMap(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        return new HashMap<>();
    }

    /**
     * 获取Map列表
     *
     * @param data 数据Map
     * @param key 字段名
     * @return Map列表
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getMapList(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value instanceof List) {
            List<Map<String, Object>> result = new ArrayList<>();
            for (Object item : (List<?>) value) {
                if (item instanceof Map) {
                    result.add((Map<String, Object>) item);
                }
            }
            return result;
        }
        return new ArrayList<>();
    }

    /**
     * 验证元数据
     *
     * @param metadata 元数据对象
     * @throws SkillParseException 验证失败时抛出
     */
    private void validateMetadata(SkillMetadata metadata) throws SkillParseException {
        // 验证ID格式
        if (!metadata.getId().matches("^[a-z0-9-]+$")) {
            throw new SkillParseException(
                    "Invalid skill id format: " + metadata.getId() + ". Must match ^[a-z0-9-]+$");
        }

        // 验证版本号格式 (SemVer)
        if (!metadata.getVersion().matches("^\\d+\\.\\d+\\.\\d+.*")) {
            log.warn("Version '{}' does not follow SemVer format", metadata.getVersion());
        }

        // 验证类型
        String type = metadata.getType();
        if (!Arrays.asList("service", "ui", "driver", "adapter").contains(type)) {
            log.warn("Unknown skill type: {}. Using 'service' as default", type);
            metadata.setType("service");
        }
    }

    // ==================== 批量解析方法 ====================

    /**
     * 批量解析多个JAR文件
     *
     * @param jarFiles JAR文件列表
     * @return Skill元数据列表
     */
    public List<SkillMetadata> parseMultipleJars(List<File> jarFiles) {
        List<SkillMetadata> metadataList = new ArrayList<>();

        for (File jarFile : jarFiles) {
            try {
                SkillMetadata metadata = parseFromJar(jarFile);
                metadataList.add(metadata);
            } catch (SkillParseException e) {
                log.error("Failed to parse JAR file: {}", jarFile.getName(), e);
            }
        }

        return metadataList;
    }

    /**
     * 从目录扫描并解析所有Skill JAR
     *
     * @param directory 扫描目录
     * @return Skill元数据列表
     */
    public List<SkillMetadata> scanAndParseDirectory(File directory) {
        if (directory == null || !directory.isDirectory()) {
            log.warn("Invalid directory: {}", directory);
            return new ArrayList<>();
        }

        List<File> jarFiles = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".jar")) {
                    jarFiles.add(file);
                }
            }
        }

        log.info("Found {} JAR files in directory: {}", jarFiles.size(), directory.getAbsolutePath());

        return parseMultipleJars(jarFiles);
    }
}
