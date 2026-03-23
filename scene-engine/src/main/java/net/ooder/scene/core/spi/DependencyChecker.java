package net.ooder.scene.core.spi;

import net.ooder.scene.core.template.DependenciesConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * 依赖检查器接口
 * 
 * <p>扩展点：用于检查依赖项是否满足</p>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface DependencyChecker {
    
    /**
     * 获取检查器类型
     * 
     * @return 依赖类型，如：SKILL, SERVICE, CONFIG
     */
    String getDependencyType();
    
    /**
     * 检查依赖
     * 
     * @param dependency 依赖项
     * @return 检查结果
     */
    CheckResult check(DependenciesConfig.DependencyItem dependency);
    
    /**
     * 健康检查
     * 
     * @param dependencyId 依赖ID
     * @return 健康状态
     */
    HealthStatus healthCheck(String dependencyId);
    
    /**
     * 检查结果
     */
    class CheckResult {
        private boolean satisfied;
        private String message;
        private Map<String, Object> details;
        
        public CheckResult() {
            this.details = new HashMap<>();
        }
        
        public CheckResult(boolean satisfied, String message) {
            this();
            this.satisfied = satisfied;
            this.message = message;
        }
        
        public static CheckResult satisfied(String message) {
            return new CheckResult(true, message);
        }
        
        public static CheckResult unsatisfied(String message) {
            return new CheckResult(false, message);
        }
        
        public boolean isSatisfied() {
            return satisfied;
        }
        
        public void setSatisfied(boolean satisfied) {
            this.satisfied = satisfied;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public Map<String, Object> getDetails() {
            return details;
        }
        
        public void setDetails(Map<String, Object> details) {
            this.details = details != null ? details : new HashMap<>();
        }
        
        public CheckResult addDetail(String key, Object value) {
            this.details.put(key, value);
            return this;
        }
    }
    
    /**
     * 健康状态
     */
    enum HealthStatus {
        HEALTHY("健康"),
        UNHEALTHY("不健康"),
        UNKNOWN("未知"),
        CHECKING("检查中");
        
        private final String displayName;
        
        HealthStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
