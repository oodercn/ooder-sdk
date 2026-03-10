package net.ooder.scene.core.template;

import java.io.Serializable;
import java.util.List;

/**
 * 依赖配置
 * 
 * <p>定义场景技能的依赖项，包括必需依赖和可选依赖</p>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class DependenciesConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private List<DependencyItem> required;   // 必需依赖
    private List<DependencyItem> optional;   // 可选依赖
    
    public DependenciesConfig() {
    }
    
    // Getters and Setters
    
    public List<DependencyItem> getRequired() {
        return required;
    }
    
    public void setRequired(List<DependencyItem> required) {
        this.required = required;
    }
    
    public List<DependencyItem> getOptional() {
        return optional;
    }
    
    public void setOptional(List<DependencyItem> optional) {
        this.optional = optional;
    }
    
    /**
     * 依赖项
     */
    public static class DependencyItem implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String dependencyId;     // 依赖ID
        private String dependencyName;   // 依赖名称
        private String dependencyType;   // 依赖类型（SKILL, SERVICE, CONFIG）
        private String versionRange;     // 版本范围
        private boolean autoInstall;     // 是否自动安装
        private String description;      // 描述
        
        public DependencyItem() {
        }
        
        public DependencyItem(String dependencyId, String dependencyName, String dependencyType) {
            this.dependencyId = dependencyId;
            this.dependencyName = dependencyName;
            this.dependencyType = dependencyType;
        }
        
        // Getters and Setters
        
        public String getDependencyId() {
            return dependencyId;
        }
        
        public void setDependencyId(String dependencyId) {
            this.dependencyId = dependencyId;
        }
        
        public String getDependencyName() {
            return dependencyName;
        }
        
        public void setDependencyName(String dependencyName) {
            this.dependencyName = dependencyName;
        }
        
        public String getDependencyType() {
            return dependencyType;
        }
        
        public void setDependencyType(String dependencyType) {
            this.dependencyType = dependencyType;
        }
        
        public String getVersionRange() {
            return versionRange;
        }
        
        public void setVersionRange(String versionRange) {
            this.versionRange = versionRange;
        }
        
        public boolean isAutoInstall() {
            return autoInstall;
        }
        
        public void setAutoInstall(boolean autoInstall) {
            this.autoInstall = autoInstall;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        @Override
        public String toString() {
            return "DependencyItem{" +
                    "dependencyId='" + dependencyId + '\'' +
                    ", dependencyName='" + dependencyName + '\'' +
                    ", dependencyType='" + dependencyType + '\'' +
                    ", autoInstall=" + autoInstall +
                    '}';
        }
    }
}
