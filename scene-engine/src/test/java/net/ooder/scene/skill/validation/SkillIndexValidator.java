package net.ooder.scene.skill.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Skill Index 验证器
 * 
 * 验证技能索引配置是否符合 SE v1.1.0 标准
 * 
 * @author Engine Team
 * @version 1.0.0
 * @since 2026-03-11
 */
public class SkillIndexValidator {

    private final ObjectMapper yamlMapper;
    private final List<ValidationError> errors;
    private final List<ValidationWarning> warnings;
    
    // 有效值定义
    private static final Set<String> VALID_SKILL_FORMS = Set.of("SCENE", "PROVIDER", "DRIVER", "INTERNAL");
    private static final Set<String> VALID_SCENE_TYPES = Set.of("AUTO", "TRIGGER");
    private static final Set<String> VALID_VISIBILITIES = Set.of("public", "developer", "internal");
    private static final Set<String> VALID_CATEGORIES = Set.of(
        "KNOWLEDGE", "LLM", "TOOL", "WORKFLOW", "DATA", "SERVICE", "UI", "OTHER"
    );
    private static final Set<String> VALID_CAPABILITY_CATEGORIES = Set.of(
        "sys", "org", "auth", "vfs", "db", "llm", "know", "payment", 
        "media", "comm", "mon", "iot", "search", "sched", "sec", "net", "util"
    );
    private static final Set<String> VALID_BUSINESS_CATEGORIES = Set.of(
        "OFFICE_COLLABORATION", "HUMAN_RESOURCE", "AI_ASSISTANT", "DATA_PROCESSING",
        "PROJECT_MANAGEMENT", "MARKETING_OPERATIONS", "SYSTEM_TOOLS", 
        "SYSTEM_MONITOR", "SECURITY_AUDIT", "INFRASTRUCTURE"
    );
    private static final Set<String> VALID_ROLE_NAMES = Set.of("MANAGER", "LEADER", "MEMBER", "USER");
    private static final Set<String> VALID_PERMISSIONS = Set.of("READ", "WRITE", "CONFIG", "DELETE");
    
    public SkillIndexValidator() {
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
    }
    
    /**
     * 验证单个技能条目文件
     */
    public boolean validateSkillEntry(Path entryPath) throws IOException {
        errors.clear();
        warnings.clear();
        
        System.out.println("\n========================================");
        System.out.println("Validating: " + entryPath.getFileName());
        System.out.println("========================================");
        
        if (!Files.exists(entryPath)) {
            addError("FILE_NOT_FOUND", "File not found: " + entryPath, ValidationSeverity.ERROR);
            return false;
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> entry = yamlMapper.readValue(entryPath.toFile(), Map.class);
        
        // 验证基本结构
        validateStructure(entry);
        
        // 提取 metadata 和 spec
        @SuppressWarnings("unchecked")
        Map<String, Object> metadata = (Map<String, Object>) entry.getOrDefault("metadata", entry);
        @SuppressWarnings("unchecked")
        Map<String, Object> spec = (Map<String, Object>) entry.getOrDefault("spec", entry);
        
        // 验证基础字段
        validateBasicFields(metadata);
        
        // 验证 SE三维分类
        validateClassification(spec);
        
        // 验证能力地址
        validateCapabilityAddresses(spec);
        
        // 验证角色 (SCENE技能)
        validateRoles(spec);
        
        // 打印结果
        printResults();
        
        return errors.isEmpty();
    }
    
    /**
     * 验证所有技能条目
     */
    public boolean validateAllSkills(Path skillsDir) throws IOException {
        errors.clear();
        warnings.clear();
        
        System.out.println("\n########################################");
        System.out.println("#  Skill Index Validation Report       #");
        System.out.println("########################################");
        
        List<Path> skillEntries = findAllSkillEntries(skillsDir);
        System.out.println("\nFound " + skillEntries.size() + " skill entries to validate\n");
        
        int passed = 0;
        int failed = 0;
        
        for (Path entry : skillEntries) {
            if (validateSkillEntry(entry)) {
                passed++;
            } else {
                failed++;
            }
        }
        
        // 打印汇总
        System.out.println("\n########################################");
        System.out.println("#  Validation Summary                  #");
        System.out.println("########################################");
        System.out.println("Total:  " + skillEntries.size());
        System.out.println("Passed: " + passed + " ✓");
        System.out.println("Failed: " + failed + " ✗");
        System.out.println("Warnings: " + warnings.size() + " ⚠");
        
        if (failed > 0) {
            System.out.println("\n❌ Validation FAILED");
            return false;
        } else {
            System.out.println("\n✅ Validation PASSED");
            return true;
        }
    }
    
    /**
     * 查找所有技能条目文件
     */
    private List<Path> findAllSkillEntries(Path skillsDir) throws IOException {
        List<Path> entries = new ArrayList<>();
        
        try (Stream<Path> paths = Files.walk(skillsDir)) {
            paths.filter(Files::isRegularFile)
                .filter(p -> p.getFileName().toString().equals("skill-index-entry.yaml"))
                .forEach(entries::add);
        }
        
        return entries;
    }
    
    /**
     * 验证基本结构
     */
    private void validateStructure(Map<String, Object> entry) {
        // 检查 apiVersion
        if (!entry.containsKey("apiVersion")) {
            warnings.add(new ValidationWarning("MISSING_API_VERSION", 
                "Missing apiVersion, assuming skill.ooder.net/v1"));
        }
        
        // 检查 kind
        if (!entry.containsKey("kind")) {
            warnings.add(new ValidationWarning("MISSING_KIND", 
                "Missing kind, assuming SkillIndexEntry"));
        }
        
        // 检查 metadata
        if (!entry.containsKey("metadata") && !entry.containsKey("id")) {
            addError("MISSING_METADATA", "Missing metadata section", ValidationSeverity.ERROR);
        }
        
        // 检查 spec
        if (!entry.containsKey("spec") && !entry.containsKey("skillForm")) {
            addError("MISSING_SPEC", "Missing spec section", ValidationSeverity.ERROR);
        }
    }
    
    /**
     * 验证基础字段
     */
    private void validateBasicFields(Map<String, Object> metadata) {
        // id
        String id = (String) metadata.get("id");
        if (id == null || id.isEmpty()) {
            addError("MISSING_ID", "Missing required field: id", ValidationSeverity.ERROR);
        } else if (!id.matches("^skill-[a-z0-9-]+$")) {
            addError("INVALID_ID_FORMAT", 
                "Invalid id format '" + id + "', expected: skill-{name}", ValidationSeverity.ERROR);
        }
        
        // name
        String name = (String) metadata.get("name");
        if (name == null || name.isEmpty()) {
            addError("MISSING_NAME", "Missing required field: name", ValidationSeverity.ERROR);
        }
        
        // version
        String version = (String) metadata.get("version");
        if (version == null || version.isEmpty()) {
            addError("MISSING_VERSION", "Missing required field: version", ValidationSeverity.ERROR);
        } else if (!version.matches("^\\d+\\.\\d+\\.\\d+$")) {
            addError("INVALID_VERSION", 
                "Invalid version format '" + version + "', expected: x.y.z", ValidationSeverity.ERROR);
        }
        
        // description
        String description = (String) metadata.get("description");
        if (description == null || description.isEmpty()) {
            warnings.add(new ValidationWarning("MISSING_DESCRIPTION", "Missing description"));
        }
    }
    
    /**
     * 验证分类字段
     */
    private void validateClassification(Map<String, Object> spec) {
        // skillForm
        String skillForm = (String) spec.get("skillForm");
        if (skillForm == null || skillForm.isEmpty()) {
            addError("MISSING_SKILL_FORM", "Missing required field: skillForm", ValidationSeverity.ERROR);
        } else if (!VALID_SKILL_FORMS.contains(skillForm)) {
            addError("INVALID_SKILL_FORM", 
                "Invalid skillForm '" + skillForm + "', expected: " + VALID_SKILL_FORMS, 
                ValidationSeverity.ERROR);
        }
        
        // sceneType (仅SCENE时必需)
        if ("SCENE".equals(skillForm)) {
            String sceneType = (String) spec.get("sceneType");
            if (sceneType == null || sceneType.isEmpty()) {
                addError("MISSING_SCENE_TYPE", 
                    "SCENE skill must specify sceneType (AUTO or TRIGGER)", ValidationSeverity.ERROR);
            } else if (!VALID_SCENE_TYPES.contains(sceneType)) {
                addError("INVALID_SCENE_TYPE", 
                    "Invalid sceneType '" + sceneType + "', expected: " + VALID_SCENE_TYPES, 
                    ValidationSeverity.ERROR);
            }
        }
        
        // visibility
        String visibility = (String) spec.get("visibility");
        if (visibility == null || visibility.isEmpty()) {
            addError("MISSING_VISIBILITY", "Missing required field: visibility", ValidationSeverity.ERROR);
        } else if (!VALID_VISIBILITIES.contains(visibility)) {
            addError("INVALID_VISIBILITY", 
                "Invalid visibility '" + visibility + "', expected: " + VALID_VISIBILITIES, 
                ValidationSeverity.ERROR);
        }
        
        // category
        String category = (String) spec.get("category");
        if (category == null || category.isEmpty()) {
            addError("MISSING_CATEGORY", "Missing required field: category", ValidationSeverity.ERROR);
        } else if (!VALID_CATEGORIES.contains(category)) {
            addError("INVALID_CATEGORY", 
                "Invalid category '" + category + "', expected: " + VALID_CATEGORIES, 
                ValidationSeverity.ERROR);
        }
        
        // capabilityCategory
        String capabilityCategory = (String) spec.get("capabilityCategory");
        if (capabilityCategory == null || capabilityCategory.isEmpty()) {
            addError("MISSING_CAPABILITY_CATEGORY", 
                "Missing required field: capabilityCategory", ValidationSeverity.ERROR);
        } else if (!VALID_CAPABILITY_CATEGORIES.contains(capabilityCategory)) {
            addError("INVALID_CAPABILITY_CATEGORY", 
                "Invalid capabilityCategory '" + capabilityCategory + "', expected: " + 
                VALID_CAPABILITY_CATEGORIES, ValidationSeverity.ERROR);
        }
        
        // businessCategory
        String businessCategory = (String) spec.get("businessCategory");
        if (businessCategory == null || businessCategory.isEmpty()) {
            addError("MISSING_BUSINESS_CATEGORY", 
                "Missing required field: businessCategory", ValidationSeverity.ERROR);
        } else if (!VALID_BUSINESS_CATEGORIES.contains(businessCategory)) {
            addError("INVALID_BUSINESS_CATEGORY", 
                "Invalid businessCategory '" + businessCategory + "', expected: " + 
                VALID_BUSINESS_CATEGORIES, ValidationSeverity.ERROR);
        }
    }
    
    /**
     * 验证能力地址
     */
    @SuppressWarnings("unchecked")
    private void validateCapabilityAddresses(Map<String, Object> spec) {
        Map<String, Object> capabilityAddresses = (Map<String, Object>) spec.get("capabilityAddresses");
        
        if (capabilityAddresses == null) {
            warnings.add(new ValidationWarning("MISSING_CAPABILITY_ADDRESSES", 
                "Missing capabilityAddresses configuration"));
            return;
        }
        
        List<Map<String, Object>> required = (List<Map<String, Object>>) capabilityAddresses.get("required");
        if (required == null || required.isEmpty()) {
            warnings.add(new ValidationWarning("NO_REQUIRED_ADDRESSES", 
                "No required capability addresses defined"));
        } else {
            for (int i = 0; i < required.size(); i++) {
                Map<String, Object> addr = required.get(i);
                validateAddressEntry(addr, "required[" + i + "]");
            }
        }
        
        List<Map<String, Object>> optional = (List<Map<String, Object>>) capabilityAddresses.get("optional");
        if (optional != null) {
            for (int i = 0; i < optional.size(); i++) {
                Map<String, Object> addr = optional.get(i);
                validateAddressEntry(addr, "optional[" + i + "]");
            }
        }
    }
    
    /**
     * 验证单个地址条目
     */
    private void validateAddressEntry(Map<String, Object> addr, String path) {
        String address = (String) addr.get("address");
        if (address == null || address.isEmpty()) {
            addError("MISSING_ADDRESS", "Missing address at " + path, ValidationSeverity.ERROR);
        } else if (!address.matches("^0x[0-9A-Fa-f]{2}$")) {
            addError("INVALID_ADDRESS_FORMAT", 
                "Invalid address format '" + address + "' at " + path + ", expected: 0xXX", 
                ValidationSeverity.ERROR);
        }
        
        String name = (String) addr.get("name");
        if (name == null || name.isEmpty()) {
            warnings.add(new ValidationWarning("MISSING_ADDRESS_NAME", 
                "Missing address name at " + path));
        }
    }
    
    /**
     * 验证角色配置
     */
    @SuppressWarnings("unchecked")
    private void validateRoles(Map<String, Object> spec) {
        String skillForm = (String) spec.get("skillForm");
        List<Map<String, Object>> roles = (List<Map<String, Object>>) spec.get("roles");
        
        if ("SCENE".equals(skillForm)) {
            if (roles == null || roles.isEmpty()) {
                warnings.add(new ValidationWarning("MISSING_ROLES", 
                    "SCENE skill should define roles"));
                return;
            }
            
            for (int i = 0; i < roles.size(); i++) {
                Map<String, Object> role = roles.get(i);
                validateRoleEntry(role, i);
            }
        }
    }
    
    /**
     * 验证单个角色条目
     */
    @SuppressWarnings("unchecked")
    private void validateRoleEntry(Map<String, Object> role, int index) {
        String name = (String) role.get("name");
        if (name == null || name.isEmpty()) {
            addError("MISSING_ROLE_NAME", "Missing role name at roles[" + index + "]", 
                ValidationSeverity.ERROR);
        } else if (!VALID_ROLE_NAMES.contains(name)) {
            addError("INVALID_ROLE_NAME", 
                "Invalid role name '" + name + "' at roles[" + index + "], expected: " + 
                VALID_ROLE_NAMES, ValidationSeverity.ERROR);
        }
        
        List<String> permissions = (List<String>) role.get("permissions");
        if (permissions == null || permissions.isEmpty()) {
            warnings.add(new ValidationWarning("MISSING_ROLE_PERMISSIONS", 
                "Missing permissions for role '" + name + "'"));
        } else {
            for (String perm : permissions) {
                if (!VALID_PERMISSIONS.contains(perm)) {
                    addError("INVALID_PERMISSION", 
                        "Invalid permission '" + perm + "' for role '" + name + "', expected: " + 
                        VALID_PERMISSIONS, ValidationSeverity.ERROR);
                }
            }
        }
    }
    
    /**
     * 添加错误
     */
    private void addError(String code, String message, ValidationSeverity severity) {
        errors.add(new ValidationError(code, message, severity));
    }
    
    /**
     * 打印验证结果
     */
    private void printResults() {
        if (!errors.isEmpty()) {
            System.out.println("\n❌ ERRORS:");
            for (ValidationError error : errors) {
                System.out.println("   [" + error.severity + "] " + error.code + ": " + error.message);
            }
        }
        
        if (!warnings.isEmpty()) {
            System.out.println("\n⚠️  WARNINGS:");
            for (ValidationWarning warning : warnings) {
                System.out.println("   " + warning.code + ": " + warning.message);
            }
        }
        
        if (errors.isEmpty() && warnings.isEmpty()) {
            System.out.println("✓ Validation passed");
        }
    }
    
    /**
     * 主入口
     */
    public static void main(String[] args) {
        try {
            SkillIndexValidator validator = new SkillIndexValidator();
            
            Path skillsDir = Paths.get("E:\\github\\ooder-skills\\skills");
            
            // 支持命令行参数
            if (args.length > 0) {
                skillsDir = Paths.get(args[0]);
            }
            
            boolean success = validator.validateAllSkills(skillsDir);
            System.exit(success ? 0 : 1);
            
        } catch (Exception e) {
            System.err.println("\n❌ Validation failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    // 内部类
    private static class ValidationError {
        final String code;
        final String message;
        final ValidationSeverity severity;
        
        ValidationError(String code, String message, ValidationSeverity severity) {
            this.code = code;
            this.message = message;
            this.severity = severity;
        }
    }
    
    private static class ValidationWarning {
        final String code;
        final String message;
        
        ValidationWarning(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }
    
    private enum ValidationSeverity {
        ERROR, WARNING
    }
}
