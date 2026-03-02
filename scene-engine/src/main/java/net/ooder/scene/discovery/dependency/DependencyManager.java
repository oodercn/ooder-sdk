package net.ooder.scene.discovery.dependency;

import net.ooder.scene.discovery.api.DiscoveryService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 依赖管理器接口
 * 
 * 管理Skill的依赖关系：
 * - 依赖检查
 * - 依赖解析
 * - 依赖安装
 * - 依赖树分析
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public interface DependencyManager {
    
    /**
     * 检查依赖
     *
     * @param skillId Skill ID
     * @return 检查结果
     */
    CompletableFuture<DiscoveryService.DependencyCheckResult> checkDependencies(String skillId);
    
    /**
     * 检查依赖（指定版本）
     *
     * @param skillId Skill ID
     * @param version 版本号
     * @return 检查结果
     */
    CompletableFuture<DiscoveryService.DependencyCheckResult> checkDependencies(String skillId, String version);
    
    /**
     * 解析依赖树
     *
     * @param skillId Skill ID
     * @return 依赖树
     */
    CompletableFuture<DependencyTree> resolveDependencyTree(String skillId);
    
    /**
     * 获取安装顺序
     *
     * @param skillId Skill ID
     * @return 安装顺序列表
     */
    CompletableFuture<List<String>> getInstallOrder(String skillId);
    
    /**
     * 安装依赖
     *
     * @param skillId Skill ID
     * @return 安装结果
     */
    CompletableFuture<DiscoveryService.DependencyInstallResult> installDependencies(String skillId);
    
    /**
     * 安装指定依赖
     *
     * @param skillId Skill ID
     * @param dependencyId 依赖ID
     * @return 安装结果
     */
    CompletableFuture<Boolean> installDependency(String skillId, String dependencyId);
    
    /**
     * 卸载依赖
     *
     * @param skillId Skill ID
     * @param dependencyId 依赖ID
     * @return 是否成功
     */
    CompletableFuture<Boolean> uninstallDependency(String skillId, String dependencyId);
    
    /**
     * 检查循环依赖
     *
     * @param skillId Skill ID
     * @return 循环依赖信息
     */
    CompletableFuture<List<List<String>>> detectCircularDependencies(String skillId);
    
    // ========== 数据类定义 ==========
    
    /**
     * 依赖树节点
     */
    class DependencyTree {
        private String skillId;
        private String version;
        private List<DependencyNode> dependencies;
        
        // Getters and Setters
        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public List<DependencyNode> getDependencies() { return dependencies; }
        public void setDependencies(List<DependencyNode> dependencies) { this.dependencies = dependencies; }
    }
    
    /**
     * 依赖节点
     */
    class DependencyNode {
        private String skillId;
        private String version;
        private String versionRange;
        private boolean optional;
        private List<DependencyNode> dependencies;
        
        // Getters and Setters
        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public String getVersionRange() { return versionRange; }
        public void setVersionRange(String versionRange) { this.versionRange = versionRange; }
        public boolean isOptional() { return optional; }
        public void setOptional(boolean optional) { this.optional = optional; }
        public List<DependencyNode> getDependencies() { return dependencies; }
        public void setDependencies(List<DependencyNode> dependencies) { this.dependencies = dependencies; }
    }
}
