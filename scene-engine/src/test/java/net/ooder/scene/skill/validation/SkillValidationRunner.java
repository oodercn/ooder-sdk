package net.ooder.scene.skill.validation;

import net.ooder.skills.capability.CapabilityAddress;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 技能配置验证运行器
 * 
 * <p>独立运行的技能验证程序，无需依赖 Spring</p>
 *
 * @author Engine Team
 * @since 2.4.0
 */
public class SkillValidationRunner {

    private static final Yaml yaml = new Yaml();

    // Skills 目录路径
    private static final String SKILLS_BASE_PATH = "E:\\github\\ooder-skills\\skills";
    
    // 验证结果
    private static final List<ValidationResult> results = new ArrayList<>();
    
    // 统计信息
    private static int totalSkills = 0;
    private static int validSkills = 0;
    private static int invalidSkills = 0;
    private static int warningSkills = 0;

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("      Skills 配置验证测试 v3.0");
        System.out.println("========================================");
        System.out.println("扫描路径: " + SKILLS_BASE_PATH);
        System.out.println();

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
            System.err.println("验证过程发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 扫描所有技能
     */
    private static void scanAllSkills() {
        System.out.println("【步骤1】扫描技能目录...");
        
        Path skillsPath = Paths.get(SKILLS_BASE_PATH);
        
        try (Stream<Path> paths = Files.walk(skillsPath)) {
            List<Path> skillYamlFiles = paths
                .filter(Files::isRegularFile)
                .filter(p -> p.getFileName().toString().equals("skill.yaml"))
                .collect(Collectors.toList());
            
            totalSkills = skillYamlFiles.size();
            System.out.println("发现 " + totalSkills + " 个技能配置文件");
            
            for (Path yamlPath : skillYamlFiles) {
                String relativePath = skillsPath.relativize(yamlPath.getParent()).toString();
                System.out.println("  - " + relativePath);
            }
            
        } catch (Exception e) {
            System.err.println("扫描技能目录失败: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * 验证 skill-index.yaml
     */
    private static void validateSkillIndex() {
        System.out.println("【步骤2】验证 skill-index.yaml...");
        
        Path indexPath = Paths.get(SKILLS_BASE_PATH, "skill-index.yaml");
        
        if (!Files.exists(indexPath)) {
            results.add(new ValidationResult("skill-index.yaml", false, 
                "文件不存在", ValidationSeverity.ERROR));
            return;
        }
        
        try {
            Map<String, Object> index = yaml.load(new FileInputStream(indexPath.toFile()));
            
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
                System.out.println("  skill-index.yaml 包含 " + skills.size() + " 个技能");
                
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
                
                System.out.println("  - 包含新分类字段: " + withNewFields + " 个");
                System.out.println("  - 未包含新分类字段: " + withoutNewFields + " 个 (需要更新)");
                
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
        System.out.println();
    }

    /**
     * 验证所有技能
     */
    private static void validateSkills() {
        System.out.println("【步骤3】验证技能配置...");
        
        Path skillsPath = Paths.get(SKILLS_BASE_PATH);
        
        try (Stream<Path> paths = Files.walk(skillsPath)) {
            paths.filter(Files::isRegularFile)
                .filter(p -> p.getFileName().toString().equals("skill.yaml"))
                .forEach(SkillValidationRunner::validateSingleSkill);
                
        } catch (Exception e) {
            System.err.println("验证技能失败: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * 验证单个技能
     */
    private static void validateSingleSkill(Path yamlPath) {
        String skillPath = yamlPath.getParent().toString();
        String skillId = skillPath.substring(skillPath.lastIndexOf("\\") + 1);
        
        try {
            Map<String, Object> skill = yaml.load(new FileInputStream(yamlPath.toFile()));
            
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
                
                // 检查新分类字段 (SE标准 v1.1.0)
                if (!metadata.containsKey("skillForm")) {
                    warnings.add("缺少 skillForm (SCENE/PROVIDER/DRIVER/INTERNAL)");
                } else {
                    String skillForm = (String) metadata.get("skillForm");
                    if (!isValidSkillForm(skillForm)) {
                        errors.add("无效的 skillForm: " + skillForm + " (应为 SCENE/PROVIDER/DRIVER/INTERNAL)");
                    }
                }
                
                String skillForm = (String) metadata.get("skillForm");
                if ("SCENE".equals(skillForm) && !metadata.containsKey("sceneType")) {
                    warnings.add("SCENE 技能缺少 sceneType (AUTO/TRIGGER)");
                }
                
                if (!metadata.containsKey("visibility")) {
                    warnings.add("缺少 visibility (public/developer/internal)");
                } else {
                    String visibility = (String) metadata.get("visibility");
                    if (!isValidVisibility(visibility)) {
                        errors.add("无效的 visibility: " + visibility + " (应为 public/developer/internal)");
                    }
                }
                
                // 检查业务分类字段 (v1.1.0)
                if (!metadata.containsKey("businessCategory")) {
                    warnings.add("缺少 businessCategory (OFFICE_COLLABORATION/HUMAN_RESOURCE/AI_ASSISTANT/...)");
                }
                
                // 检查技术分类字段
                if (!metadata.containsKey("category")) {
                    warnings.add("缺少 category (KNOWLEDGE/LLM/TOOL/WORKFLOW/DATA/SERVICE/UI/OTHER)");
                }
                
                // 检查 capabilityCategory (v1.1.0)
                if (!metadata.containsKey("capabilityCategory")) {
                    warnings.add("缺少 capabilityCategory (sys/org/auth/llm/know/...)");
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
    private static void validateCapabilityAddresses(Map<String, Object> addresses, 
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
    private static void validateSingleAddress(Map<String, Object> addr, String type,
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
    private static void validateSkillsMd(Path mdPath, List<String> warnings) {
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
    private static void generateClassificationReport() {
        System.out.println("【步骤4】生成分类列表...");
        
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
                        Map<String, Object> skill = yaml.load(new FileInputStream(yamlPath.toFile()));
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
            System.out.println("分类统计:");
            System.out.println("  STANDALONE (独立技能): " + classification.get("STANDALONE").size() + " 个");
            System.out.println("  SCENE_AUTO (自驱场景): " + classification.get("SCENE_AUTO").size() + " 个");
            System.out.println("  SCENE_TRIGGER (触发场景): " + classification.get("SCENE_TRIGGER").size() + " 个");
            System.out.println("  INTERNAL (内部技能): " + classification.get("INTERNAL").size() + " 个");
            System.out.println("  UNKNOWN (未分类): " + classification.get("UNKNOWN").size() + " 个");
            
            if (!classification.get("UNKNOWN").isEmpty()) {
                System.out.println("  未分类技能列表:");
                for (String skillId : classification.get("UNKNOWN")) {
                    System.out.println("    - " + skillId);
                }
            }
            
        } catch (Exception e) {
            System.err.println("生成分类列表失败: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * 输出验证报告
     */
    private static void printValidationReport() {
        System.out.println("========================================");
        System.out.println("          验证报告");
        System.out.println("========================================");
        System.out.println("总技能数: " + totalSkills);
        System.out.println("有效配置: " + validSkills + " 个");
        System.out.println("有警告: " + warningSkills + " 个");
        System.out.println("有错误: " + invalidSkills + " 个");
        System.out.println();
        
        // 按严重程度分组
        Map<ValidationSeverity, List<ValidationResult>> grouped = results.stream()
            .collect(Collectors.groupingBy(r -> r.severity));
        
        // 输出错误
        List<ValidationResult> errors = grouped.getOrDefault(ValidationSeverity.ERROR, Collections.emptyList());
        if (!errors.isEmpty()) {
            System.out.println("【错误列表】");
            for (ValidationResult result : errors) {
                System.out.println("  ❌ " + result.skillId + ": " + result.message);
            }
            System.out.println();
        }
        
        // 输出警告
        List<ValidationResult> warnings = grouped.getOrDefault(ValidationSeverity.WARNING, Collections.emptyList());
        if (!warnings.isEmpty()) {
            System.out.println("【警告列表】(前10条)");
            warnings.stream().limit(10).forEach(result -> {
                System.out.println("  ⚠️  " + result.skillId + ": " + result.message);
            });
            if (warnings.size() > 10) {
                System.out.println("  ... 还有 " + (warnings.size() - 10) + " 条警告");
            }
            System.out.println();
        }
        
        // 输出建议
        System.out.println("【改进建议】");
        System.out.println("1. 为所有技能添加新分类字段:");
        System.out.println("   - skillForm: STANDALONE 或 SCENE");
        System.out.println("   - sceneType: AUTO 或 TRIGGER (仅SCENE)");
        System.out.println("   - visibility: public 或 internal");
        System.out.println();
        System.out.println("2. 添加 capabilityAddresses 配置:");
        System.out.println("   - required: 必需的能力地址");
        System.out.println("   - optional: 可选的能力地址");
        System.out.println();
        System.out.println("3. 完善 LLM 文档:");
        System.out.println("   - 添加 skills.md 文件");
        System.out.println("   - 包含能力描述和使用说明");
        System.out.println();
        
        System.out.println("========================================");
        System.out.println("验证完成");
        System.out.println("========================================");
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
    
    /**
     * 验证 skillForm 是否有效 (v1.1.0)
     */
    private static boolean isValidSkillForm(String skillForm) {
        if (skillForm == null) return false;
        return skillForm.equals("SCENE") || 
               skillForm.equals("PROVIDER") || 
               skillForm.equals("DRIVER") || 
               skillForm.equals("INTERNAL");
    }
    
    /**
     * 验证 visibility 是否有效 (v1.1.0)
     */
    private static boolean isValidVisibility(String visibility) {
        if (visibility == null) return false;
        return visibility.equals("public") || 
               visibility.equals("developer") || 
               visibility.equals("internal");
    }
}
