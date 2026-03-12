package net.ooder.scene.skill.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import net.ooder.skills.capability.CapabilityAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 技能配置验证测试
 * 
 * <p>验证 skills 目录下的技能配置是否符合 v3.0 规范</p>
 *
 * @author Engine Team
 * @since 2.4.0
 */
public class SkillValidationTest {

    private static final Logger log = LoggerFactory.getLogger(SkillValidationTest.class);
    private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    private static final ObjectMapper jsonMapper = new ObjectMapper();

    // Skills 目录路径
    private static final String SKILLS_BASE_PATH = "E:\\github\\ooder-skills\\skills";
    
    // 验证结果
    private final List<ValidationResult> results = new ArrayList<>();
    
    // 统计信息
    private int totalSkills = 0;
    private int validSkills = 0;
    private int invalidSkills = 0;
    private int warningSkills = 0;

    public static void main(String[] args) {
        SkillValidationTest test = new SkillValidationTest();
        test.runValidation();
    }

    public void runValidation() {
        log.info("========================================");
        log.info("      Skills 配置验证测试 v3.0");
        log.info("========================================");
        log.info("扫描路径: {}", SKILLS_BASE_PATH);
        log.info("");

        try {
            // 1. 扫描所有技能
            scanAllSkills();
            
            // 2. 读取 skill-index.yaml
            validateSkillIndex();
            
            // 3. 验证每个技能
            validateSkills();
            
            // 4. 生成分类列表
            generateClassificationReport();
            
            // 5. 输出验证报告
            printValidationReport();
            
        } catch (Exception e) {
            log.error("验证过程发生错误", e);
        }
    }

    /**
     * 扫描所有技能
     */
    private void scanAllSkills() {
        log.info("【步骤1】扫描技能目录...");
        
        Path skillsPath = Paths.get(SKILLS_BASE_PATH);
        
        try (Stream<Path> paths = Files.walk(skillsPath)) {
            List<Path> skillYamlFiles = paths
                .filter(Files::isRegularFile)
                .filter(p -> p.getFileName().toString().equals("skill.yaml"))
                .collect(Collectors.toList());
            
            totalSkills = skillYamlFiles.size();
            log.info("发现 {} 个技能配置文件", totalSkills);
            
            for (Path yamlPath : skillYamlFiles) {
                String relativePath = skillsPath.relativize(yamlPath.getParent()).toString();
                log.debug("  - {}", relativePath);
            }
            
        } catch (Exception e) {
            log.error("扫描技能目录失败", e);
        }
        log.info("");
    }

    /**
     * 验证 skill-index.yaml
     */
    private void validateSkillIndex() {
        log.info("【步骤2】验证 skill-index.yaml...");
        
        Path indexPath = Paths.get(SKILLS_BASE_PATH, "skill-index.yaml");
        
        if (!Files.exists(indexPath)) {
            results.add(new ValidationResult("skill-index.yaml", false, 
                "文件不存在", ValidationSeverity.ERROR));
            return;
        }
        
        try {
            Map<String, Object> index = yamlMapper.readValue(indexPath.toFile(), Map.class);
            
            // 检查必需字段
            List<String> missingFields = new ArrayList<>();
            
            if (!index.containsKey("apiVersion")) missingFields.add("apiVersion");
            if (!index.containsKey("kind")) missingFields.add("kind");
            if (!index.containsKey("metadata")) missingFields.add("metadata");
            if (!index.containsKey("spec")) missingFields.add("spec");
            
            if (!missingFields.isEmpty()) {
                results.add(new ValidationResult("skill-index.yaml", false,
                    "缺少必需字段: " + missingFields, ValidationSeverity.ERROR));
            } else {
                results.add(new ValidationResult("skill-index.yaml", true,
                    "基本结构完整", ValidationSeverity.INFO));
            }
            
            // 检查 skills 列表
            Map<String, Object> spec = (Map<String, Object>) index.get("spec");
            if (spec != null && spec.containsKey("skills")) {
                List<Map<String, Object>> skills = (List<Map<String, Object>>) spec.get("skills");
                log.info("  skill-index.yaml 包含 {} 个技能", skills.size());
                
                // 检查是否包含新分类字段
                int withNewFields = 0;
                int withoutNewFields = 0;
                
                for (Map<String, Object> skill : skills) {
                    if (skill.containsKey("skillForm") || skill.containsKey("sceneType") || 
                        skill.containsKey("visibility")) {
                        withNewFields++;
                    } else {
                        withoutNewFields++;
                    }
                }
                
                log.info("  - 包含新分类字段: {} 个", withNewFields);
                log.info("  - 未包含新分类字段: {} 个 (需要更新)", withoutNewFields);
                
                if (withoutNewFields > 0) {
                    results.add(new ValidationResult("skill-index.yaml", false,
                        withoutNewFields + " 个技能缺少新分类字段 (skillForm/sceneType/visibility)",
                        ValidationSeverity.WARNING));
                }
            }
            
        } catch (Exception e) {
            results.add(new ValidationResult("skill-index.yaml", false,
                "解析失败: " + e.getMessage(), ValidationSeverity.ERROR));
        }
        log.info("");
    }

    /**
     * 验证所有技能
     */
    private void validateSkills() {
        log.info("【步骤3】验证技能配置...");
        
        Path skillsPath = Paths.get(SKILLS_BASE_PATH);
        
        try (Stream<Path> paths = Files.walk(skillsPath)) {
            paths.filter(Files::isRegularFile)
                .filter(p -> p.getFileName().toString().equals("skill.yaml"))
                .forEach(this::validateSingleSkill);
                
        } catch (Exception e) {
            log.error("验证技能失败", e);
        }
        log.info("");
    }

    /**
     * 验证单个技能
     */
    private void validateSingleSkill(Path yamlPath) {
        String skillPath = yamlPath.getParent().toString();
        String skillId = skillPath.substring(skillPath.lastIndexOf("\\") + 1);
        
        try {
            Map<String, Object> skill = yamlMapper.readValue(yamlPath.toFile(), Map.class);
            
            List<String> errors = new ArrayList<>();
            List<String> warnings = new ArrayList<>();
            
            // 1. 检查 apiVersion
            if (!skill.containsKey("apiVersion")) {
                errors.add("缺少 apiVersion");
            } else {
                String apiVersion = (String) skill.get("apiVersion");
                if (!apiVersion.equals("skill.ooder.net/v1")) {
                    warnings.add("apiVersion 建议使用 skill.ooder.net/v1");
                }
            }
            
            // 2. 检查 metadata
            Map<String, Object> metadata = (Map<String, Object>) skill.get("metadata");
            if (metadata == null) {
                errors.add("缺少 metadata");
            } else {
                // 检查必需字段
                if (!metadata.containsKey("id")) errors.add("metadata 缺少 id");
                if (!metadata.containsKey("name")) errors.add("metadata 缺少 name");
                if (!metadata.containsKey("version")) errors.add("metadata 缺少 version");
                
                // 检查新分类字段
                if (!metadata.containsKey("skillForm")) {
                    warnings.add("缺少 skillForm (STANDALONE/SCENE)");
                }
                if (!metadata.containsKey("sceneType") && "SCENE".equals(metadata.get("skillForm"))) {
                    warnings.add("SCENE 技能缺少 sceneType (AUTO/TRIGGER)");
                }
                if (!metadata.containsKey("visibility")) {
                    warnings.add("缺少 visibility (public/internal)");
                }
            }
            
            // 3. 检查 spec
            Map<String, Object> spec = (Map<String, Object>) skill.get("spec");
            if (spec == null) {
                errors.add("缺少 spec");
            } else {
                // 检查 capabilityAddresses (新规范)
                if (!spec.containsKey("capabilityAddresses")) {
                    warnings.add("缺少 capabilityAddresses (新规范)");
                } else {
                    Map<String, Object> addresses = (Map<String, Object>) spec.get("capabilityAddresses");
                    if (addresses != null) {
                        validateCapabilityAddresses(addresses, errors, warnings);
                    }
                }
                
                // 检查 dependencies
                if (spec.containsKey("dependencies")) {
                    List<Map<String, Object>> deps = (List<Map<String, Object>>) spec.get("dependencies");
                    if (deps != null) {
                        for (Map<String, Object> dep : deps) {
                            if (!dep.containsKey("id")) {
                                warnings.add("依赖项缺少 id");
                            }
                        }
                    }
                }
            }
            
            // 4. 检查 LLM 文档 (skills.md)
            Path skillsMdPath = yamlPath.getParent().resolve("skills.md");
            if (!Files.exists(skillsMdPath)) {
                warnings.add("缺少 skills.md (LLM 知识文档)");
            } else {
                validateSkillsMd(skillsMdPath, warnings);
            }
            
            // 记录结果
            if (!errors.isEmpty()) {
                invalidSkills++;
                results.add(new ValidationResult(skillId, false,
                    "错误: " + String.join(", ", errors), ValidationSeverity.ERROR));
            } else if (!warnings.isEmpty()) {
                warningSkills++;
                results.add(new ValidationResult(skillId, true,
                    "警告: " + String.join(", ", warnings), ValidationSeverity.WARNING));
            } else {
                validSkills++;
                results.add(new ValidationResult(skillId, true,
                    "配置完整", ValidationSeverity.INFO));
            }
            
        } catch (Exception e) {
            invalidSkills++;
            results.add(new ValidationResult(skillId, false,
                "解析失败: " + e.getMessage(), ValidationSeverity.ERROR));
        }
    }

    /**
     * 验证能力地址
     */
    private void validateCapabilityAddresses(Map<String, Object> addresses, 
                                             List<String> errors, 
                                             List<String> warnings) {
        // 验证 required 地址
        if (addresses.containsKey("required")) {
            List<Map<String, Object>> required = (List<Map<String, Object>>) addresses.get("required");
            if (required != null) {
                for (Map<String, Object> addr : required) {
                    validateSingleAddress(addr, "required", errors, warnings);
                }
            }
        }
        
        // 验证 optional 地址
        if (addresses.containsKey("optional")) {
            List<Map<String, Object>> optional = (List<Map<String, Object>>) addresses.get("optional");
            if (optional != null) {
                for (Map<String, Object> addr : optional) {
                    validateSingleAddress(addr, "optional", errors, warnings);
                }
            }
        }
    }

    /**
     * 验证单个地址
     */
    private void validateSingleAddress(Map<String, Object> addr, String type,
                                       List<String> errors, List<String> warnings) {
        if (!addr.containsKey("address")) {
            errors.add(type + " 地址缺少 address 字段");
            return;
        }
        
        Object addressValue = addr.get("address");
        int address;
        
        if (addressValue instanceof Integer) {
            address = (Integer) addressValue;
        } else if (addressValue instanceof String) {
            String addrStr = (String) addressValue;
            if (addrStr.startsWith("0x")) {
                address = Integer.parseInt(addrStr.substring(2), 16);
            } else {
                address = Integer.parseInt(addrStr);
            }
        } else {
            errors.add(type + " 地址格式错误: " + addressValue);
            return;
        }
        
        // 验证地址范围 (0x00-0x7F)
        if (address < 0x00 || address > 0x7F) {
            errors.add(String.format("地址 0x%02X 超出范围 (0x00-0x7F)", address));
        } else {
            // 验证地址是否有效
            try {
                CapabilityAddress capAddr = CapabilityAddress.fromAddress(address);
                if (capAddr == null) {
                    warnings.add(String.format("地址 0x%02X 未定义", address));
                }
            } catch (Exception e) {
                warnings.add(String.format("地址 0x%02X 验证失败: %s", address, e.getMessage()));
            }
        }
    }

    /**
     * 验证 skills.md
     */
    private void validateSkillsMd(Path mdPath, List<String> warnings) {
        try {
            String content = Files.readString(mdPath);
            
            // 检查基本结构
            if (!content.contains("# ")) {
                warnings.add("skills.md 缺少标题");
            }
            if (!content.contains("##")) {
                warnings.add("skills.md 缺少章节结构");
            }
            
            // 检查是否包含能力描述
            if (!content.contains("能力") && !content.contains("capability")) {
                warnings.add("skills.md 可能缺少能力描述");
            }
            
        } catch (Exception e) {
            warnings.add("skills.md 读取失败: " + e.getMessage());
        }
    }

    /**
     * 生成分类列表报告
     */
    private void generateClassificationReport() {
        log.info("【步骤4】生成分类列表...");
        
        Map<String, List<String>> classification = new HashMap<>();
        classification.put("STANDALONE", new ArrayList<>());
        classification.put("SCENE_AUTO", new ArrayList<>());
        classification.put("SCENE_TRIGGER", new ArrayList<>());
        classification.put("INTERNAL", new ArrayList<>());
        classification.put("UNKNOWN", new ArrayList<>());
        
        Path skillsPath = Paths.get(SKILLS_BASE_PATH);
        
        try (Stream<Path> paths = Files.walk(skillsPath)) {
            paths.filter(Files::isRegularFile)
                .filter(p -> p.getFileName().toString().equals("skill.yaml"))
                .forEach(yamlPath -> {
                    try {
                        Map<String, Object> skill = yamlMapper.readValue(yamlPath.toFile(), Map.class);
                        Map<String, Object> metadata = (Map<String, Object>) skill.get("metadata");
                        
                        String skillId = yamlPath.getParent().getFileName().toString();
                        
                        if (metadata == null) {
                            classification.get("UNKNOWN").add(skillId);
                            return;
                        }
                        
                        String skillForm = (String) metadata.get("skillForm");
                        String sceneType = (String) metadata.get("sceneType");
                        String visibility = (String) metadata.get("visibility");
                        
                        if ("internal".equals(visibility)) {
                            classification.get("INTERNAL").add(skillId);
                        } else if ("STANDALONE".equals(skillForm)) {
                            classification.get("STANDALONE").add(skillId);
                        } else if ("SCENE".equals(skillForm)) {
                            if ("AUTO".equals(sceneType)) {
                                classification.get("SCENE_AUTO").add(skillId);
                            } else if ("TRIGGER".equals(sceneType)) {
                                classification.get("SCENE_TRIGGER").add(skillId);
                            } else {
                                classification.get("UNKNOWN").add(skillId);
                            }
                        } else {
                            classification.get("UNKNOWN").add(skillId);
                        }
                        
                    } catch (Exception e) {
                        classification.get("UNKNOWN").add(yamlPath.getParent().getFileName().toString());
                    }
                });
            
            // 输出分类统计
            log.info("分类统计:");
            log.info("  STANDALONE (独立技能): {} 个", classification.get("STANDALONE").size());
            log.info("  SCENE_AUTO (自驱场景): {} 个", classification.get("SCENE_AUTO").size());
            log.info("  SCENE_TRIGGER (触发场景): {} 个", classification.get("SCENE_TRIGGER").size());
            log.info("  INTERNAL (内部技能): {} 个", classification.get("INTERNAL").size());
            log.info("  UNKNOWN (未分类): {} 个", classification.get("UNKNOWN").size());
            
            if (!classification.get("UNKNOWN").isEmpty()) {
                log.info("  未分类技能列表:");
                for (String skillId : classification.get("UNKNOWN")) {
                    log.info("    - {}", skillId);
                }
            }
            
        } catch (Exception e) {
            log.error("生成分类列表失败", e);
        }
        log.info("");
    }

    /**
     * 输出验证报告
     */
    private void printValidationReport() {
        log.info("========================================");
        log.info("          验证报告");
        log.info("========================================");
        log.info("总技能数: {}", totalSkills);
        log.info("有效配置: {} 个", validSkills);
        log.info("有警告: {} 个", warningSkills);
        log.info("有错误: {} 个", invalidSkills);
        log.info("");
        
        // 按严重程度分组
        Map<ValidationSeverity, List<ValidationResult>> grouped = results.stream()
            .collect(Collectors.groupingBy(r -> r.severity));
        
        // 输出错误
        List<ValidationResult> errors = grouped.getOrDefault(ValidationSeverity.ERROR, Collections.emptyList());
        if (!errors.isEmpty()) {
            log.info("【错误列表】");
            for (ValidationResult result : errors) {
                log.info("  ❌ {}: {}", result.skillId, result.message);
            }
            log.info("");
        }
        
        // 输出警告
        List<ValidationResult> warnings = grouped.getOrDefault(ValidationSeverity.WARNING, Collections.emptyList());
        if (!warnings.isEmpty()) {
            log.info("【警告列表】(前10条)");
            warnings.stream().limit(10).forEach(result -> {
                log.info("  ⚠️  {}: {}", result.skillId, result.message);
            });
            if (warnings.size() > 10) {
                log.info("  ... 还有 {} 条警告", warnings.size() - 10);
            }
            log.info("");
        }
        
        // 输出建议
        log.info("【改进建议】");
        log.info("1. 为所有技能添加新分类字段:");
        log.info("   - skillForm: STANDALONE 或 SCENE");
        log.info("   - sceneType: AUTO 或 TRIGGER (仅SCENE)");
        log.info("   - visibility: public 或 internal");
        log.info("");
        log.info("2. 添加 capabilityAddresses 配置:");
        log.info("   - required: 必需的能力地址");
        log.info("   - optional: 可选的能力地址");
        log.info("");
        log.info("3. 完善 LLM 文档:");
        log.info("   - 添加 skills.md 文件");
        log.info("   - 包含能力描述和使用说明");
        log.info("");
        
        log.info("========================================");
        log.info("验证完成");
        log.info("========================================");
    }

    /**
     * 验证结果
     */
    private static class ValidationResult {
        final String skillId;
        final boolean valid;
        final String message;
        final ValidationSeverity severity;
        
        ValidationResult(String skillId, boolean valid, String message, ValidationSeverity severity) {
            this.skillId = skillId;
            this.valid = valid;
            this.message = message;
            this.severity = severity;
        }
    }
    
    /**
     * 验证严重程度
     */
    private enum ValidationSeverity {
        INFO, WARNING, ERROR
    }
}
