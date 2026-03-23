package net.ooder.scene.core.dependency;

import net.ooder.scene.core.spi.DependencyChecker;
import net.ooder.scene.core.template.DependenciesConfig.DependencyItem;
import net.ooder.scene.discovery.UnifiedSkillRegistry;
import net.ooder.skills.api.SkillPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * 技能依赖检查器
 * 
 * <p>检查 SKILL 类型的依赖是否满足</p>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class SkillDependencyChecker implements DependencyChecker {

    private static final Logger log = LoggerFactory.getLogger(SkillDependencyChecker.class);

    private final UnifiedSkillRegistry skillRegistry;

    public SkillDependencyChecker(UnifiedSkillRegistry skillRegistry) {
        this.skillRegistry = skillRegistry;
    }

    @Override
    public String getDependencyType() {
        return "SKILL";
    }

    @Override
    public CheckResult check(DependencyItem dependency) {
        if (dependency == null || dependency.getDependencyId() == null) {
            return CheckResult.unsatisfied("依赖配置无效");
        }

        try {
            if (skillRegistry == null) {
                return CheckResult.unsatisfied("技能注册中心不可用");
            }

            CompletableFuture<SkillPackage> skillFuture = skillRegistry.getSkill(dependency.getDependencyId());
            SkillPackage skillPackage = skillFuture.join();
            
            if (skillPackage == null) {
                return CheckResult.unsatisfied("技能未安装: " + dependency.getDependencyId());
            }
            String installedVersion = skillPackage.getVersion();
            String requiredVersion = dependency.getVersionRange();

            if (requiredVersion != null && !requiredVersion.isEmpty()) {
                if (!isVersionSatisfied(installedVersion, requiredVersion)) {
                    CheckResult result = CheckResult.unsatisfied(
                        "版本不满足: 需要 " + requiredVersion + "，已安装 " + installedVersion);
                    Map<String, Object> details = new HashMap<>();
                    details.put("installedVersion", installedVersion);
                    details.put("requiredVersion", requiredVersion);
                    result.setDetails(details);
                    return result;
                }
            }

            CheckResult result = CheckResult.satisfied("技能已安装: " + dependency.getDependencyName());
            Map<String, Object> details = new HashMap<>();
            details.put("version", installedVersion);
            details.put("name", skillPackage.getMetadata() != null 
                ? skillPackage.getMetadata().get("name") : dependency.getDependencyName());
            result.setDetails(details);
            return result;

        } catch (Exception e) {
            log.error("[check] Failed to check skill dependency: {}", dependency.getDependencyId(), e);
            return CheckResult.unsatisfied("检查异常: " + e.getMessage());
        }
    }

    @Override
    public HealthStatus healthCheck(String dependencyId) {
        if (skillRegistry == null) {
            return HealthStatus.UNKNOWN;
        }

        try {
            CompletableFuture<SkillPackage> skillFuture = skillRegistry.getSkill(dependencyId);
            SkillPackage skillPackage = skillFuture.join();
            if (skillPackage == null) {
                return HealthStatus.UNHEALTHY;
            }
            return HealthStatus.HEALTHY;
        } catch (Exception e) {
            return HealthStatus.UNKNOWN;
        }
    }

    private boolean isVersionSatisfied(String installedVersion, String requiredVersion) {
        if (requiredVersion == null || requiredVersion.isEmpty()) {
            return true;
        }
        if (installedVersion == null) {
            return false;
        }

        if (requiredVersion.startsWith(">=")) {
            String minVersion = requiredVersion.substring(2).trim();
            return compareVersions(installedVersion, minVersion) >= 0;
        } else if (requiredVersion.startsWith(">")) {
            String minVersion = requiredVersion.substring(1).trim();
            return compareVersions(installedVersion, minVersion) > 0;
        } else if (requiredVersion.startsWith("<=")) {
            String maxVersion = requiredVersion.substring(2).trim();
            return compareVersions(installedVersion, maxVersion) <= 0;
        } else if (requiredVersion.startsWith("<")) {
            String maxVersion = requiredVersion.substring(1).trim();
            return compareVersions(installedVersion, maxVersion) < 0;
        } else if (requiredVersion.contains(",")) {
            String[] parts = requiredVersion.split(",");
            if (parts.length == 2) {
                String minVersion = parts[0].trim();
                String maxVersion = parts[1].trim();
                return compareVersions(installedVersion, minVersion) >= 0 
                    && compareVersions(installedVersion, maxVersion) <= 0;
            }
        }

        return installedVersion.equals(requiredVersion);
    }

    private int compareVersions(String v1, String v2) {
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");

        int maxLength = Math.max(parts1.length, parts2.length);
        for (int i = 0; i < maxLength; i++) {
            int num1 = i < parts1.length ? parseVersionPart(parts1[i]) : 0;
            int num2 = i < parts2.length ? parseVersionPart(parts2[i]) : 0;

            if (num1 != num2) {
                return num1 - num2;
            }
        }
        return 0;
    }

    private int parseVersionPart(String part) {
        try {
            return Integer.parseInt(part.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
