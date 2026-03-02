package net.ooder.skills.api;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 版本兼容性检查器
 * 用于检查Skill版本是否满足依赖要求
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public interface VersionCompatibilityChecker {
    
    /**
     * 检查单个Skill的版本兼容性
     *
     * @param skillId Skill ID
     * @param requiredVersion 需要的版本范围（如 "1.0.0", ">=1.0.0", "^1.0.0"）
     * @return 兼容性检查结果
     */
    CompatibilityResult check(String skillId, String requiredVersion);
    
    /**
     * 检查单个Skill的版本兼容性（异步）
     *
     * @param skillId Skill ID
     * @param requiredVersion 需要的版本范围
     * @return CompletableFuture<兼容性检查结果>
     */
    CompletableFuture<CompatibilityResult> checkAsync(String skillId, String requiredVersion);
    
    /**
     * 批量检查依赖列表的版本兼容性
     *
     * @param dependencies 依赖列表
     * @return 批量兼容性检查结果
     */
    BatchCompatibilityResult checkAll(List<SkillManifest.Dependency> dependencies);
    
    /**
     * 批量检查依赖列表的版本兼容性（异步）
     *
     * @param dependencies 依赖列表
     * @return CompletableFuture<批量兼容性检查结果>
     */
    CompletableFuture<BatchCompatibilityResult> checkAllAsync(List<SkillManifest.Dependency> dependencies);
    
    /**
     * 解析版本范围
     *
     * @param versionRange 版本范围字符串（如 ">=1.0.0 <2.0.0"）
     * @return 解析后的版本范围对象
     */
    VersionRange parseVersionRange(String versionRange);
    
    /**
     * 比较两个版本号
     *
     * @param version1 版本1
     * @param version2 版本2
     * @return 比较结果：-1表示v1<v2，0表示相等，1表示v1>v2
     */
    int compareVersions(String version1, String version2);
    
    /**
     * 获取建议的兼容版本
     *
     * @param skillId Skill ID
     * @param requiredVersion 需要的版本范围
     * @return 建议的版本号，如果不存在则返回null
     */
    String suggestVersion(String skillId, String requiredVersion);
    
    /**
     * 检查是否存在版本冲突
     *
     * @param dependencies 依赖列表
     * @return 冲突检测结果
     */
    ConflictDetectionResult detectConflicts(List<SkillManifest.Dependency> dependencies);
}
