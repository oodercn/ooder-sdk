package net.ooder.scene.discovery;

import net.ooder.scene.core.SceneEngine;
import net.ooder.scene.discovery.api.DiscoveryRequest;
import net.ooder.scene.discovery.api.DiscoveryResult;
import net.ooder.scene.event.SceneEventPublisher;
import net.ooder.scene.event.capability.CapabilityEvent;
import net.ooder.skills.api.SkillPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * SceneEngine 发现集成服务
 * 
 * <p>提供 SceneEngine 与发现服务的集成能力，支持从 skill-index 目录结构发现能力并注册到 SceneEngine。</p>
 * 
 * <h3>核心功能：</h3>
 * <ul>
 *   <li>从索引目录发现能力 - 解析 skill-index 目录结构</li>
 *   <li>批量能力注册 - 将发现的能力注册到 SceneEngine</li>
 *   <li>发现事件处理 - 处理技能发现生命周期事件</li>
 *   <li>钩子管理 - 管理 SceneEngineDiscoveryHook 实现</li>
 * </ul>
 * 
 * <h3>目录结构支持：</h3>
 * <pre>
 * skill-index/
 * ├── categories.yaml          # 分类定义
 * ├── scene-drivers.yaml       # 场景驱动定义
 * ├── skills/
 * │   ├── skill1.yaml
 * │   └── skill2.yaml
 * └── scenes/
 *     ├── scene1.yaml
 *     └── scene2.yaml
 * </pre>
 * 
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 * @see SceneEngineDiscoveryHook
 * @see CapabilityDTO
 */
@Component
public class SceneEngineIntegration {
    
    private static final Logger logger = LoggerFactory.getLogger(SceneEngineIntegration.class);
    
    private final SceneEngine sceneEngine;
    private final SceneEventPublisher eventPublisher;
    private final DiscoveryEventPublisher discoveryEventPublisher;
    private final List<SceneEngineDiscoveryHook> discoveryHooks = new CopyOnWriteArrayList<>();
    private final ObjectMapper yamlMapper;
    private UnifiedDiscoveryService unifiedDiscoveryService;
    
    public SceneEngineIntegration(SceneEngine sceneEngine, SceneEventPublisher eventPublisher, 
                                  DiscoveryEventPublisher discoveryEventPublisher) {
        this.sceneEngine = sceneEngine;
        this.eventPublisher = eventPublisher;
        this.discoveryEventPublisher = discoveryEventPublisher;
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
    }
    
    public void setUnifiedDiscoveryService(UnifiedDiscoveryService service) {
        this.unifiedDiscoveryService = service;
    }
    
    /**
     * 从索引目录发现能力
     * 
     * <p>解析 skill-index 目录结构，返回发现的能力列表。</p>
     *
     * @param indexDir skill-index 目录
     * @return 发现的能力列表
     * @throws DiscoveryException 如果目录不存在或解析失败
     */
    public List<CapabilityDTO> discoverFromIndex(File indexDir) {
        if (indexDir == null || !indexDir.exists()) {
            throw new DiscoveryException("INVALID_INDEX_DIR", "索引目录不存在: " + indexDir);
        }
        
        if (!indexDir.isDirectory()) {
            throw new DiscoveryException("INVALID_INDEX_DIR", "路径不是目录: " + indexDir);
        }
        
        logger.info("开始从索引目录发现能力: {}", indexDir.getAbsolutePath());
        
        // 创建发现请求
        DiscoveryRequest request = new DiscoveryRequest();
        request.setSource("local");
        
        // 发布发现开始事件
        if (discoveryEventPublisher != null) {
            discoveryEventPublisher.publishDiscoveryStarted(request);
        }
        
        List<CapabilityDTO> capabilities = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        
        try {
            // 解析分类定义
            File categoriesFile = new File(indexDir, "categories.yaml");
            if (categoriesFile.exists()) {
                logger.debug("发现分类定义文件: {}", categoriesFile.getName());
            }
            
            // 解析场景驱动定义
            File sceneDriversFile = new File(indexDir, "scene-drivers.yaml");
            if (sceneDriversFile.exists()) {
                logger.debug("发现场景驱动定义文件: {}", sceneDriversFile.getName());
            }
            
            // 解析 skills 目录
            File skillsDir = new File(indexDir, "skills");
            if (skillsDir.exists() && skillsDir.isDirectory()) {
                List<CapabilityDTO> skills = parseSkillsDirectory(skillsDir);
                capabilities.addAll(skills);
                logger.info("从 skills 目录发现 {} 个技能", skills.size());
                
                // 发布技能发现事件
                if (discoveryEventPublisher != null) {
                    skills.forEach(discoveryEventPublisher::publishSkillDiscovered);
                }
            }
            
            // 解析 scenes 目录
            File scenesDir = new File(indexDir, "scenes");
            if (scenesDir.exists() && scenesDir.isDirectory()) {
                List<CapabilityDTO> scenes = parseScenesDirectory(scenesDir);
                capabilities.addAll(scenes);
                logger.info("从 scenes 目录发现 {} 个场景", scenes.size());
                
                // 发布场景发现事件
                if (discoveryEventPublisher != null) {
                    scenes.forEach(discoveryEventPublisher::publishSceneDiscovered);
                }
            }
            
            logger.info("索引目录发现完成，共发现 {} 个能力", capabilities.size());
            
            // 发布发现完成事件
            if (discoveryEventPublisher != null) {
                DiscoveryResult result = new DiscoveryResult();
                result.setSkills(convertToSkillInfoList(capabilities));
                result.setTotalCount(capabilities.size());
                result.setSource("local");
                result.setTimestamp(System.currentTimeMillis());
                discoveryEventPublisher.publishDiscoveryCompleted(result);
            }
            
        } catch (Exception e) {
            String error = "解析索引目录失败: " + e.getMessage();
            logger.error(error, e);
            
            // 发布发现失败事件
            if (discoveryEventPublisher != null) {
                discoveryEventPublisher.publishDiscoveryFailed(request, error);
            }
            
            throw new DiscoveryException("INDEX_PARSE_ERROR", error, e);
        }
        
        return capabilities;
    }
    
    /**
     * 从远程目录结构发现能力
     * 
     * <p>支持从远程 Git 仓库的目录结构发现能力。</p>
     *
     * @param repoUrl 仓库地址
     * @param branch 分支
     * @param basePath 基础路径
     * @return 发现的能力列表
     */
    public List<CapabilityDTO> discoverFromRemote(String repoUrl, String branch, String basePath) {
        logger.info("从远程仓库发现能力: {}@{}/{}", repoUrl, branch, basePath);
        
        if (unifiedDiscoveryService == null) {
            throw new DiscoveryException("SERVICE_NOT_CONFIGURED", 
                "UnifiedDiscoveryService未配置，请通过setUnifiedDiscoveryService()设置");
        }
        
        try {
            List<SkillPackage> skills = unifiedDiscoveryService.discoverSkills(repoUrl, basePath).get();
            
            List<CapabilityDTO> capabilities = skills.stream()
                .map(this::convertSkillPackageToDTO)
                .collect(Collectors.toList());
            
            logger.info("从远程仓库发现 {} 个能力", capabilities.size());
            return capabilities;
            
        } catch (Exception e) {
            String error = "远程仓库发现失败: " + e.getMessage();
            logger.error(error, e);
            throw new DiscoveryException("REMOTE_DISCOVERY_ERROR", error, e);
        }
    }
    
    /**
     * 批量注册能力到 SceneEngine
     * 
     * <p>将发现的能力批量注册到 SceneEngine 的能力注册表。</p>
     *
     * @param capabilities 能力列表
     * @return 注册结果，包含成功和失败的数量
     */
    public BatchRegisterResult registerCapabilities(List<CapabilityDTO> capabilities) {
        if (capabilities == null || capabilities.isEmpty()) {
            return new BatchRegisterResult(0, 0, 0);
        }
        
        logger.info("开始批量注册 {} 个能力", capabilities.size());
        
        int success = 0;
        int failed = 0;
        int skipped = 0;
        List<String> failedIds = new ArrayList<>();
        
        for (CapabilityDTO capability : capabilities) {
            try {
                // 检查是否已注册
                if (capability.isRegistered() || 
                    (capability.getCapabilityAddress() != null && 
                     sceneEngine.hasCapability(capability.getCapabilityAddress()))) {
                    logger.debug("能力已注册，跳过: {}", capability.getId());
                    skipped++;
                    continue;
                }
                
                // 注册能力
                registerCapability(capability);
                success++;
                capability.setRegistered(true);
                
                // 发布能力注册事件
                publishCapabilityRegisteredEvent(capability);
                
            } catch (Exception e) {
                logger.error("注册能力失败: {} - {}", capability.getId(), e.getMessage());
                failed++;
                failedIds.add(capability.getId());
            }
        }
        
        logger.info("批量注册完成: 成功={}, 失败={}, 跳过={}", success, failed, skipped);
        
        return new BatchRegisterResult(success, failed, skipped, failedIds);
    }
    
    /**
     * 注册单个能力
     *
     * @param capability 能力信息
     */
    public void registerCapability(CapabilityDTO capability) {
        if (capability == null || capability.getId() == null) {
            throw new DiscoveryException("INVALID_CAPABILITY", "能力信息无效");
        }
        
        String capAddress = capability.getCapabilityAddress();
        if (capAddress == null) {
            capAddress = generateCapabilityAddress(capability);
            capability.setCapabilityAddress(capAddress);
        }
        
        // 注册到 SceneEngine
        sceneEngine.registerCapability(capAddress, capability);
        
        logger.debug("能力已注册: {} -> {}", capability.getId(), capAddress);
    }
    
    /**
     * 技能发现事件回调
     * 
     * <p>当发现新技能时调用，触发注册的钩子。</p>
     *
     * @param skill 技能包信息
     */
    public void onSkillDiscovered(Object skill) {
        logger.info("发现新技能: {}", skill);
        
        // 触发所有钩子的技能发现处理
        for (SceneEngineDiscoveryHook hook : discoveryHooks) {
            try {
                // 转换为 CapabilityDTO 并触发回调
                CapabilityDTO capability = convertToCapabilityDTO(skill);
                if (capability.isScene()) {
                    hook.onSceneDiscovered(capability);
                } else {
                    hook.onCapabilityDiscovered(capability);
                }
            } catch (Exception e) {
                logger.error("钩子处理技能发现事件失败: {}", e.getMessage());
            }
        }
    }
    
    /**
     * 添加发现钩子
     *
     * @param hook 发现钩子实现
     */
    public void addDiscoveryHook(SceneEngineDiscoveryHook hook) {
        if (hook != null) {
            discoveryHooks.add(hook);
            // 按优先级排序
            discoveryHooks.sort((h1, h2) -> Integer.compare(h2.getPriority(), h1.getPriority()));
            logger.debug("添加发现钩子: {}, 优先级: {}", hook.getClass().getSimpleName(), hook.getPriority());
        }
    }
    
    /**
     * 移除发现钩子
     *
     * @param hook 发现钩子实现
     */
    public void removeDiscoveryHook(SceneEngineDiscoveryHook hook) {
        if (hook != null) {
            discoveryHooks.remove(hook);
            logger.debug("移除发现钩子: {}", hook.getClass().getSimpleName());
        }
    }
    
    /**
     * 执行发现前钩子
     *
     * @param request 发现请求
     */
    public void executeBeforeDiscoveryHooks(DiscoveryRequest request) {
        for (SceneEngineDiscoveryHook hook : discoveryHooks) {
            try {
                if (hook.supportsSource(request.getSource())) {
                    hook.onBeforeDiscovery(request);
                }
            } catch (Exception e) {
                logger.error("执行发现前钩子失败: {} - {}", hook.getClass().getSimpleName(), e.getMessage());
                throw new DiscoveryException("HOOK_EXECUTION_ERROR", "发现前钩子执行失败", e);
            }
        }
    }
    
    /**
     * 执行发现后钩子
     *
     * @param result 发现结果
     */
    public void executeAfterDiscoveryHooks(DiscoveryResult result) {
        for (SceneEngineDiscoveryHook hook : discoveryHooks) {
            try {
                hook.onAfterDiscovery(result);
            } catch (Exception e) {
                logger.error("执行发现后钩子失败: {} - {}", hook.getClass().getSimpleName(), e.getMessage());
            }
        }
    }
    
    /**
     * 执行发现失败钩子
     *
     * @param request 发现请求
     * @param error 错误信息
     */
    public void executeDiscoveryFailedHooks(DiscoveryRequest request, String error) {
        for (SceneEngineDiscoveryHook hook : discoveryHooks) {
            try {
                if (hook.supportsSource(request.getSource())) {
                    hook.onDiscoveryFailed(request, error);
                }
            } catch (Exception e) {
                logger.error("执行发现失败钩子失败: {} - {}", hook.getClass().getSimpleName(), e.getMessage());
            }
        }
    }
    
    // ========== 私有方法 ==========
    
    private List<CapabilityDTO> parseSkillsDirectory(File skillsDir) throws IOException {
        List<CapabilityDTO> skills = new ArrayList<>();
        
        try (Stream<Path> paths = Files.walk(skillsDir.toPath())) {
            skills = paths
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".yaml") || p.toString().endsWith(".yml"))
                .map(this::parseSkillFile)
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
        }
        
        return skills;
    }
    
    private List<CapabilityDTO> parseScenesDirectory(File scenesDir) throws IOException {
        List<CapabilityDTO> scenes = new ArrayList<>();
        
        try (Stream<Path> paths = Files.walk(scenesDir.toPath())) {
            scenes = paths
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".yaml") || p.toString().endsWith(".yml"))
                .map(this::parseSceneFile)
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
        }
        
        return scenes;
    }
    
    private CapabilityDTO parseSkillFile(Path path) {
        try {
            logger.debug("解析技能文件: {}", path);
            
            CapabilityDTO dto = new CapabilityDTO();
            dto.setType("skill");
            dto.setLocation(path.toString());
            dto.setSource("local");
            dto.setDiscoveredAt(System.currentTimeMillis());
            
            String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            @SuppressWarnings("unchecked")
            Map<String, Object> yamlData = yamlMapper.readValue(content, Map.class);
            
            if (yamlData != null) {
                dto.setId((String) yamlData.getOrDefault("id", extractIdFromPath(path)));
                dto.setName((String) yamlData.get("name"));
                dto.setDescription((String) yamlData.get("description"));
                dto.setVersion((String) yamlData.getOrDefault("version", "1.0.0"));
                dto.setCategory((String) yamlData.get("category"));
                
                @SuppressWarnings("unchecked")
                List<String> tags = (List<String>) yamlData.get("tags");
                dto.setTags(tags);
                
                @SuppressWarnings("unchecked")
                List<String> dependencies = (List<String>) yamlData.get("dependencies");
                dto.setDependencies(dependencies);
                
                @SuppressWarnings("unchecked")
                Map<String, Object> metadata = (Map<String, Object>) yamlData.get("metadata");
                dto.setMetadata(metadata);
            } else {
                dto.setId(extractIdFromPath(path));
            }
            
            return dto;
        } catch (Exception e) {
            logger.error("解析技能文件失败: {} - {}", path, e.getMessage());
            return null;
        }
    }
    
    private CapabilityDTO parseSceneFile(Path path) {
        try {
            logger.debug("解析场景文件: {}", path);
            
            CapabilityDTO dto = new CapabilityDTO();
            dto.setType("scene");
            dto.setLocation(path.toString());
            dto.setSource("local");
            dto.setDiscoveredAt(System.currentTimeMillis());
            
            String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            @SuppressWarnings("unchecked")
            Map<String, Object> yamlData = yamlMapper.readValue(content, Map.class);
            
            if (yamlData != null) {
                dto.setId((String) yamlData.getOrDefault("id", extractIdFromPath(path)));
                dto.setName((String) yamlData.get("name"));
                dto.setDescription((String) yamlData.get("description"));
                dto.setVersion((String) yamlData.getOrDefault("version", "1.0.0"));
                dto.setCategory((String) yamlData.get("category"));
                
                @SuppressWarnings("unchecked")
                List<String> tags = (List<String>) yamlData.get("tags");
                dto.setTags(tags);
                
                @SuppressWarnings("unchecked")
                List<String> dependencies = (List<String>) yamlData.get("dependencies");
                dto.setDependencies(dependencies);
                
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> driverConditionsRaw = 
                    (List<Map<String, Object>>) yamlData.get("driverConditions");
                if (driverConditionsRaw != null) {
                    List<CapabilityDTO.DriverCondition> driverConditions = driverConditionsRaw.stream()
                        .map(dc -> new CapabilityDTO.DriverCondition(
                            (String) dc.get("type"),
                            (String) dc.get("expression"),
                            (String) dc.get("description")
                        ))
                        .collect(Collectors.toList());
                    dto.setDriverConditions(driverConditions);
                }
                
                @SuppressWarnings("unchecked")
                Map<String, Object> metadata = (Map<String, Object>) yamlData.get("metadata");
                dto.setMetadata(metadata);
            } else {
                dto.setId(extractIdFromPath(path));
            }
            
            return dto;
        } catch (Exception e) {
            logger.error("解析场景文件失败: {} - {}", path, e.getMessage());
            return null;
        }
    }
    
    private String generateCapabilityAddress(CapabilityDTO capability) {
        return String.format("%s://%s/%s", 
            capability.getType(), 
            capability.getCategory() != null ? capability.getCategory() : "default",
            capability.getId());
    }
    
    private String extractIdFromPath(Path path) {
        String fileName = path.getFileName().toString();
        return fileName.replaceAll("\\.ya?ml$", "");
    }
    
    private CapabilityDTO convertToCapabilityDTO(Object skill) {
        if (skill instanceof CapabilityDTO) {
            return (CapabilityDTO) skill;
        }
        
        if (skill instanceof SkillPackage) {
            return convertSkillPackageToDTO((SkillPackage) skill);
        }
        
        CapabilityDTO dto = new CapabilityDTO();
        dto.setId(skill.toString());
        dto.setType("skill");
        return dto;
    }
    
    private CapabilityDTO convertSkillPackageToDTO(SkillPackage skill) {
        CapabilityDTO dto = new CapabilityDTO();
        dto.setId(skill.getSkillId());
        dto.setName(skill.getName());
        dto.setDescription(skill.getDescription());
        dto.setVersion(skill.getVersion() != null ? skill.getVersion() : "1.0.0");
        dto.setCategory(skill.getCategory());
        dto.setTags(skill.getTags());
        dto.setType("skill");
        dto.setSource("remote");
        return dto;
    }
    
    private List<net.ooder.scene.discovery.api.DiscoveryService.SkillInfo> convertToSkillInfoList(List<CapabilityDTO> capabilities) {
        return capabilities.stream()
            .map(this::convertToSkillInfo)
            .collect(Collectors.toList());
    }
    
    private net.ooder.scene.discovery.api.DiscoveryService.SkillInfo convertToSkillInfo(CapabilityDTO capability) {
        net.ooder.scene.discovery.api.DiscoveryService.SkillInfo info = 
            new net.ooder.scene.discovery.api.DiscoveryService.SkillInfo();
        info.setSkillId(capability.getId());
        info.setName(capability.getName());
        info.setVersion(capability.getVersion());
        info.setDescription(capability.getDescription());
        info.setCategory(capability.getCategory());
        info.setTags(capability.getTags());
        info.setSource(capability.getSource());
        info.setLocation(capability.getLocation());
        info.setInstalled(capability.isInstalled());
        return info;
    }
    
    private void publishCapabilityRegisteredEvent(CapabilityDTO capability) {
        if (eventPublisher != null) {
            CapabilityEvent event = CapabilityEvent.discovered(
                this,
                capability.getId(),
                capability.getCapabilityAddress(),
                capability.getType()
            );
            eventPublisher.publishCapabilityEvent(event);
        }
    }
    
    /**
     * 批量注册结果
     */
    public static class BatchRegisterResult {
        private final int successCount;
        private final int failedCount;
        private final int skippedCount;
        private final List<String> failedIds;
        
        public BatchRegisterResult(int successCount, int failedCount, int skippedCount) {
            this(successCount, failedCount, skippedCount, new ArrayList<>());
        }
        
        public BatchRegisterResult(int successCount, int failedCount, int skippedCount, List<String> failedIds) {
            this.successCount = successCount;
            this.failedCount = failedCount;
            this.skippedCount = skippedCount;
            this.failedIds = failedIds;
        }
        
        public int getSuccessCount() { return successCount; }
        public int getFailedCount() { return failedCount; }
        public int getSkippedCount() { return skippedCount; }
        public List<String> getFailedIds() { return failedIds; }
        
        public boolean isAllSuccess() {
            return failedCount == 0;
        }
        
        @Override
        public String toString() {
            return String.format("BatchRegisterResult{success=%d, failed=%d, skipped=%d}", 
                successCount, failedCount, skippedCount);
        }
    }
}
