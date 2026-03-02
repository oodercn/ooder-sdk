package net.ooder.skills.api;

import java.util.ArrayList;
import java.util.List;

/**
 * 版本范围
 * 支持语义化版本范围表达式
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class VersionRange {
    
    private String rawExpression;
    private List<VersionConstraint> constraints;
    
    public VersionRange() {
        this.constraints = new ArrayList<>();
    }
    
    public VersionRange(String rawExpression) {
        this();
        this.rawExpression = rawExpression;
    }
    
    // Getters and Setters
    
    public String getRawExpression() {
        return rawExpression;
    }
    
    public void setRawExpression(String rawExpression) {
        this.rawExpression = rawExpression;
    }
    
    public List<VersionConstraint> getConstraints() {
        return constraints;
    }
    
    public void setConstraints(List<VersionConstraint> constraints) {
        this.constraints = constraints;
    }
    
    public void addConstraint(VersionConstraint constraint) {
        if (this.constraints == null) {
            this.constraints = new ArrayList<>();
        }
        this.constraints.add(constraint);
    }
    
    /**
     * 检查版本是否满足此范围
     */
    public boolean satisfies(String version) {
        if (constraints == null || constraints.isEmpty()) {
            return true;
        }
        return constraints.stream().allMatch(c -> c.satisfies(version));
    }
    
    @Override
    public String toString() {
        return "VersionRange{" +
            "rawExpression='" + rawExpression + '\'' +
            ", constraints=" + constraints +
            '}';
    }
    
    /**
     * 版本约束
     */
    public static class VersionConstraint {
        
        public enum Operator {
            EQ("="),      // 等于
            GT(">"),      // 大于
            GTE(">="),    // 大于等于
            LT("<"),      // 小于
            LTE("<="),    // 小于等于
            TILDE("~"),   // 约等于（兼容次要版本）
            CARET("^");   // 插入号（兼容主要版本）
            
            private final String symbol;
            
            Operator(String symbol) {
                this.symbol = symbol;
            }
            
            public String getSymbol() {
                return symbol;
            }
        }
        
        private Operator operator;
        private String version;
        
        public VersionConstraint() {
        }
        
        public VersionConstraint(Operator operator, String version) {
            this.operator = operator;
            this.version = version;
        }
        
        // Getters and Setters
        
        public Operator getOperator() {
            return operator;
        }
        
        public void setOperator(Operator operator) {
            this.operator = operator;
        }
        
        public String getVersion() {
            return version;
        }
        
        public void setVersion(String version) {
            this.version = version;
        }
        
        /**
         * 检查版本是否满足此约束
         */
        public boolean satisfies(String targetVersion) {
            int comparison = compareVersions(targetVersion, version);
            
            switch (operator) {
                case EQ:
                    return comparison == 0;
                case GT:
                    return comparison > 0;
                case GTE:
                    return comparison >= 0;
                case LT:
                    return comparison < 0;
                case LTE:
                    return comparison <= 0;
                case TILDE:
                    return satisfiesTilde(targetVersion, version);
                case CARET:
                    return satisfiesCaret(targetVersion, version);
                default:
                    return false;
            }
        }
        
        private boolean satisfiesTilde(String targetVersion, String baseVersion) {
            // ~1.2.3 := >=1.2.3 <1.3.0
            String[] targetParts = targetVersion.split("\\.");
            String[] baseParts = baseVersion.split("\\.");
            
            if (targetParts.length < 2 || baseParts.length < 2) {
                return false;
            }
            
            return targetParts[0].equals(baseParts[0]) && 
                   targetParts[1].equals(baseParts[1]) &&
                   compareVersions(targetVersion, baseVersion) >= 0;
        }
        
        private boolean satisfiesCaret(String targetVersion, String baseVersion) {
            // ^1.2.3 := >=1.2.3 <2.0.0
            String[] targetParts = targetVersion.split("\\.");
            String[] baseParts = baseVersion.split("\\.");
            
            if (targetParts.length == 0 || baseParts.length == 0) {
                return false;
            }
            
            if (!targetParts[0].equals(baseParts[0])) {
                return false;
            }
            
            return compareVersions(targetVersion, baseVersion) >= 0;
        }
        
        private int compareVersions(String v1, String v2) {
            String[] parts1 = v1.split("\\.");
            String[] parts2 = v2.split("\\.");
            
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
            // 处理预发布版本标记，如 "1.0.0-beta" 中的 "0-beta"
            String numericPart = part.replaceAll("-.*$", "");
            try {
                return Integer.parseInt(numericPart);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        
        @Override
        public String toString() {
            return operator.getSymbol() + version;
        }
    }
}
