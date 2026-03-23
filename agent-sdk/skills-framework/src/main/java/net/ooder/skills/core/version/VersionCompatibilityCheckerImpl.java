package net.ooder.skills.core.version;

import net.ooder.skills.api.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 版本兼容性检查器实现
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class VersionCompatibilityCheckerImpl implements VersionCompatibilityChecker {
    
    private final SkillRegistry skillRegistry;
    
    // 版本范围正则表达式
    private static final Pattern VERSION_RANGE_PATTERN = Pattern.compile(
        "(>=?|<=?|[~^])?\\s*(\\d+(?:\\.\\d+)*)"
    );
    
    public VersionCompatibilityCheckerImpl(SkillRegistry skillRegistry) {
        this.skillRegistry = skillRegistry;
    }
    
    @Override
    public CompatibilityResult check(String skillId, String requiredVersion) {
        if (skillId == null || skillId.isEmpty()) {
            return CompatibilityResult.incompatible(null, requiredVersion, null, "Skill ID不能为空");
        }
        
        // 获取已安装的Skill版本
        SkillManifest installedSkill = skillRegistry.getSkill(skillId);
        if (installedSkill == null) {
            return CompatibilityResult.unknown(skillId, requiredVersion);
        }
        
        String actualVersion = installedSkill.getVersion();
        
        // 解析版本范围
        VersionRange versionRange = parseVersionRange(requiredVersion);
        
        // 检查版本是否满足范围
        boolean satisfies = versionRange.satisfies(actualVersion);
        
        if (satisfies) {
            return CompatibilityResult.compatible(skillId, requiredVersion, actualVersion);
        } else {
            CompatibilityResult result = CompatibilityResult.incompatible(
                skillId, requiredVersion, actualVersion,
                String.format("版本不兼容: 需要 %s, 实际 %s", requiredVersion, actualVersion)
            );
            
            // 尝试建议兼容版本
            String suggested = suggestVersion(skillId, requiredVersion);
            if (suggested != null) {
                result.setSuggestedVersion(suggested);
            }
            
            return result;
        }
    }
    
    @Override
    public CompletableFuture<CompatibilityResult> checkAsync(String skillId, String requiredVersion) {
        return CompletableFuture.supplyAsync(() -> check(skillId, requiredVersion));
    }
    
    @Override
    public BatchCompatibilityResult checkAll(List<SkillManifest.Dependency> dependencies) {
        BatchCompatibilityResult batchResult = new BatchCompatibilityResult();
        
        if (dependencies == null || dependencies.isEmpty()) {
            batchResult.setAllCompatible(true);
            return batchResult;
        }
        
        for (SkillManifest.Dependency dependency : dependencies) {
            CompatibilityResult result = check(dependency.getSkillId(), dependency.getVersionRange());
            batchResult.addResult(result);
        }
        
        // 检测版本冲突
        ConflictDetectionResult conflictResult = detectConflicts(dependencies);
        if (conflictResult.isHasConflict()) {
            batchResult.setConflicts(
                conflictResult.getConflicts().stream()
                    .map(c -> c.getSkillId() + ": " + c.getMessage())
                    .collect(Collectors.toList())
            );
        }
        
        return batchResult;
    }
    
    @Override
    public CompletableFuture<BatchCompatibilityResult> checkAllAsync(List<SkillManifest.Dependency> dependencies) {
        return CompletableFuture.supplyAsync(() -> checkAll(dependencies));
    }
    
    @Override
    public VersionRange parseVersionRange(String versionRange) {
        if (versionRange == null || versionRange.isEmpty()) {
            return new VersionRange("*");
        }
        
        VersionRange range = new VersionRange(versionRange);
        
        // 精确版本（如 "1.0.0"）
        if (versionRange.matches("\\d+(?:\\.\\d+)*")) {
            range.addConstraint(new VersionRange.VersionConstraint(
                VersionRange.VersionConstraint.Operator.EQ, versionRange
            ));
            return range;
        }
        
        // 解析复合版本范围（如 ">=1.0.0 <2.0.0"）
        Matcher matcher = VERSION_RANGE_PATTERN.matcher(versionRange);
        while (matcher.find()) {
            String operatorStr = matcher.group(1);
            String version = matcher.group(2);
            
            VersionRange.VersionConstraint.Operator operator = parseOperator(operatorStr);
            range.addConstraint(new VersionRange.VersionConstraint(operator, version));
        }
        
        return range;
    }
    
    private VersionRange.VersionConstraint.Operator parseOperator(String operatorStr) {
        if (operatorStr == null) {
            return VersionRange.VersionConstraint.Operator.EQ;
        }
        
        switch (operatorStr) {
            case ">":
                return VersionRange.VersionConstraint.Operator.GT;
            case ">=":
                return VersionRange.VersionConstraint.Operator.GTE;
            case "<":
                return VersionRange.VersionConstraint.Operator.LT;
            case "<=":
                return VersionRange.VersionConstraint.Operator.LTE;
            case "~":
                return VersionRange.VersionConstraint.Operator.TILDE;
            case "^":
                return VersionRange.VersionConstraint.Operator.CARET;
            default:
                return VersionRange.VersionConstraint.Operator.EQ;
        }
    }
    
    @Override
    public int compareVersions(String version1, String version2) {
        if (version1 == null && version2 == null) return 0;
        if (version1 == null) return -1;
        if (version2 == null) return 1;
        
        String[] parts1 = version1.split("\\.");
        String[] parts2 = version2.split("\\.");
        
        int maxLength = Math.max(parts1.length, parts2.length);
        
        for (int i = 0; i < maxLength; i++) {
            int num1 = i < parts1.length ? parseVersionPart(parts1[i]) : 0;
            int num2 = i < parts2.length ? parseVersionPart(parts2[i]) : 0;
            
            if (num1 != num2) {
                return Integer.compare(num1, num2);
            }
        }
        
        return 0;
    }
    
    private int parseVersionPart(String part) {
        // 处理预发布版本标记
        String numericPart = part.replaceAll("-.*$", "");
        try {
            return Integer.parseInt(numericPart);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    @Override
    public String suggestVersion(String skillId, String requiredVersion) {
        // 获取所有可用版本
        List<String> availableVersions = skillRegistry.getAvailableVersions(skillId);
        if (availableVersions == null || availableVersions.isEmpty()) {
            return null;
        }
        
        VersionRange range = parseVersionRange(requiredVersion);
        
        // 找到满足范围的最高版本
        return availableVersions.stream()
            .filter(range::satisfies)
            .max(this::compareVersions)
            .orElse(null);
    }
    
    @Override
    public ConflictDetectionResult detectConflicts(List<SkillManifest.Dependency> dependencies) {
        if (dependencies == null || dependencies.isEmpty()) {
            return ConflictDetectionResult.noConflict();
        }
        
        ConflictDetectionResult result = new ConflictDetectionResult();
        
        // 按Skill ID分组
        Map<String, List<SkillManifest.Dependency>> grouped = dependencies.stream()
            .collect(Collectors.groupingBy(SkillManifest.Dependency::getSkillId));
        
        // 检查每个Skill的版本冲突
        for (Map.Entry<String, List<SkillManifest.Dependency>> entry : grouped.entrySet()) {
            String skillId = entry.getKey();
            List<SkillManifest.Dependency> deps = entry.getValue();
            
            if (deps.size() > 1) {
                // 同一Skill有多个版本要求
                List<String> versionRanges = deps.stream()
                    .map(SkillManifest.Dependency::getVersionRange)
                    .distinct()
                    .collect(Collectors.toList());
                
                if (versionRanges.size() > 1) {
                    // 检查这些版本范围是否有交集
                    boolean hasIntersection = checkVersionIntersection(versionRanges);
                    
                    if (!hasIntersection) {
                        ConflictDetectionResult.VersionConflict conflict = 
                            new ConflictDetectionResult.VersionConflict(
                                skillId,
                                "VERSION_RANGE_CONFLICT",
                                String.format("Skill %s 有冲突的版本要求: %s", skillId, versionRanges)
                            );
                        conflict.setRequiredVersions(versionRanges);
                        conflict.setResolution("请统一版本要求或使用兼容的版本范围");
                        result.addConflict(conflict);
                    }
                }
            }
        }
        
        // 添加建议
        if (result.isHasConflict()) {
            result.addSuggestion("检查依赖树，确保同一Skill的版本要求一致");
            result.addSuggestion("考虑使用更宽松的版本范围（如 ^1.0.0 代替 >=1.0.0 <1.1.0）");
        }
        
        return result;
    }
    
    /**
     * 检查多个版本范围是否有交集
     */
    private boolean checkVersionIntersection(List<String> versionRanges) {
        if (versionRanges.size() < 2) {
            return true;
        }
        
        List<VersionRange> ranges = versionRanges.stream()
            .map(this::parseVersionRange)
            .collect(Collectors.toList());
        
        String lowerBound = null;
        String upperBound = null;
        boolean lowerInclusive = true;
        boolean upperInclusive = true;
        
        for (VersionRange range : ranges) {
            List<VersionRange.VersionConstraint> constraints = range.getConstraints();
            if (constraints == null || constraints.isEmpty()) {
                continue;
            }
            
            for (VersionRange.VersionConstraint constraint : constraints) {
                String version = constraint.getVersion();
                VersionRange.VersionConstraint.Operator op = constraint.getOperator();
                
                switch (op) {
                    case EQ:
                        if (lowerBound == null) {
                            lowerBound = version;
                            upperBound = version;
                            lowerInclusive = true;
                            upperInclusive = true;
                        } else {
                            if (compareVersions(version, lowerBound) < 0 || 
                                compareVersions(version, upperBound) > 0) {
                                return false;
                            }
                        }
                        break;
                    case GTE:
                    case GT:
                        boolean inclusive = (op == VersionRange.VersionConstraint.Operator.GTE);
                        if (lowerBound == null || compareVersions(version, lowerBound) > 0 ||
                            (compareVersions(version, lowerBound) == 0 && !lowerInclusive)) {
                            lowerBound = version;
                            lowerInclusive = inclusive;
                        } else if (compareVersions(version, lowerBound) == 0 && inclusive) {
                            lowerInclusive = true;
                        }
                        break;
                    case LTE:
                    case LT:
                        inclusive = (op == VersionRange.VersionConstraint.Operator.LTE);
                        if (upperBound == null || compareVersions(version, upperBound) < 0 ||
                            (compareVersions(version, upperBound) == 0 && !upperInclusive)) {
                            upperBound = version;
                            upperInclusive = inclusive;
                        } else if (compareVersions(version, upperBound) == 0 && inclusive) {
                            upperInclusive = true;
                        }
                        break;
                    case TILDE:
                        if (lowerBound == null || compareVersions(version, lowerBound) > 0) {
                            lowerBound = version;
                            lowerInclusive = true;
                        }
                        String tildeUpper = incrementMinorVersion(version);
                        if (upperBound == null || compareVersions(tildeUpper, upperBound) < 0) {
                            upperBound = tildeUpper;
                            upperInclusive = false;
                        }
                        break;
                    case CARET:
                        if (lowerBound == null || compareVersions(version, lowerBound) > 0) {
                            lowerBound = version;
                            lowerInclusive = true;
                        }
                        String caretUpper = incrementMajorVersion(version);
                        if (upperBound == null || compareVersions(caretUpper, upperBound) < 0) {
                            upperBound = caretUpper;
                            upperInclusive = false;
                        }
                        break;
                }
            }
        }
        
        if (lowerBound == null || upperBound == null) {
            return true;
        }
        
        int cmp = compareVersions(lowerBound, upperBound);
        if (cmp > 0) {
            return false;
        }
        if (cmp == 0 && (!lowerInclusive || !upperInclusive)) {
            return false;
        }
        
        return true;
    }
    
    private String incrementMinorVersion(String version) {
        String[] parts = version.split("\\.");
        if (parts.length >= 2) {
            int minor = Integer.parseInt(parts[1]) + 1;
            return parts[0] + "." + minor + ".0";
        }
        return version;
    }
    
    private String incrementMajorVersion(String version) {
        String[] parts = version.split("\\.");
        if (parts.length >= 1) {
            int major = Integer.parseInt(parts[0]) + 1;
            return major + ".0.0";
        }
        return version;
    }
}
