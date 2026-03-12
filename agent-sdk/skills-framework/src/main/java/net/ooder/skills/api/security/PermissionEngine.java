package net.ooder.skills.api.security;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 权限引擎接口
 *
 * <p>计算用户数据访问范围，应用到 RAG 检索</p>
 *
 * @author Agent-SDK Team
 * @version 2.4.0
 * @since 2.4.0
 */
public interface PermissionEngine {

    /**
     * 计算用户数据访问范围
     *
     * <p>计算逻辑：</p>
     * <ol>
     *   <li>获取用户角色和权限</li>
     *   <li>获取 Skill 数据要求</li>
     *   <li>计算交集得到实际访问范围</li>
     * </ol>
     *
     * @param userId  用户ID
     * @param skillId Skill ID
     * @return 数据范围
     */
    DataScope calculateDataScope(String userId, String skillId);

    /**
     * 检查用户是否有权限执行操作
     *
     * @param userId    用户ID
     * @param skillId   Skill ID
     * @param operation 操作名称
     * @return 是否有权限
     */
    boolean hasPermission(String userId, String skillId, String operation);

    /**
     * 获取用户在 Skill 中的角色
     *
     * @param userId  用户ID
     * @param skillId Skill ID
     * @return 角色信息
     */
    UserRole getUserRole(String userId, String skillId);

    /**
     * 应用到 RAG 检索请求
     *
     * @param scope   数据范围
     * @param request RAG 检索请求
     */
    void applyToRagSearch(DataScope scope, RagSearchRequest request);

    /**
     * 数据范围
     */
    class DataScope {
        private String userId;
        private String skillId;
        private Set<String> departments;
        private Set<String> resources;
        private Set<String> operations;
        private Map<String, Object> dataFilters;
        private boolean allowAll;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }

        public Set<String> getDepartments() { return departments; }
        public void setDepartments(Set<String> departments) { this.departments = departments; }

        public Set<String> getResources() { return resources; }
        public void setResources(Set<String> resources) { this.resources = resources; }

        public Set<String> getOperations() { return operations; }
        public void setOperations(Set<String> operations) { this.operations = operations; }

        public Map<String, Object> getDataFilters() { return dataFilters; }
        public void setDataFilters(Map<String, Object> dataFilters) { this.dataFilters = dataFilters; }

        public boolean isAllowAll() { return allowAll; }
        public void setAllowAll(boolean allowAll) { this.allowAll = allowAll; }

        /**
         * 创建允许全部的数据范围
         */
        public static DataScope allowAll(String userId, String skillId) {
            DataScope scope = new DataScope();
            scope.setUserId(userId);
            scope.setSkillId(skillId);
            scope.setAllowAll(true);
            return scope;
        }
    }

    /**
     * 用户角色
     */
    class UserRole {
        private String userId;
        private String roleId;
        private String roleName;
        private Set<String> permissions;
        private Set<String> departments;
        private Map<String, Object> attributes;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getRoleId() { return roleId; }
        public void setRoleId(String roleId) { this.roleId = roleId; }

        public String getRoleName() { return roleName; }
        public void setRoleName(String roleName) { this.roleName = roleName; }

        public Set<String> getPermissions() { return permissions; }
        public void setPermissions(Set<String> permissions) { this.permissions = permissions; }

        public Set<String> getDepartments() { return departments; }
        public void setDepartments(Set<String> departments) { this.departments = departments; }

        public Map<String, Object> getAttributes() { return attributes; }
        public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes; }
    }

    /**
     * Skill 权限配置
     */
    class SkillPermission {
        private String skillId;
        private Set<String> requiredRoles;
        private Set<String> departments;
        private Set<String> resources;
        private Map<String, Object> filters;

        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }

        public Set<String> getRequiredRoles() { return requiredRoles; }
        public void setRequiredRoles(Set<String> requiredRoles) { this.requiredRoles = requiredRoles; }

        public Set<String> getDepartments() { return departments; }
        public void setDepartments(Set<String> departments) { this.departments = departments; }

        public Set<String> getResources() { return resources; }
        public void setResources(Set<String> resources) { this.resources = resources; }

        public Map<String, Object> getFilters() { return filters; }
        public void setFilters(Map<String, Object> filters) { this.filters = filters; }
    }

    /**
     * RAG 检索请求
     */
    class RagSearchRequest {
        private String query;
        private int topK = 5;
        private double threshold = 0.7;
        private Map<String, Object> filters;

        public String getQuery() { return query; }
        public void setQuery(String query) { this.query = query; }

        public int getTopK() { return topK; }
        public void setTopK(int topK) { this.topK = topK; }

        public double getThreshold() { return threshold; }
        public void setThreshold(double threshold) { this.threshold = threshold; }

        public Map<String, Object> getFilters() { return filters; }
        public void setFilters(Map<String, Object> filters) { this.filters = filters; }

        public void addFilter(String key, Object value) {
            if (filters == null) {
                filters = new java.util.HashMap<>();
            }
            filters.put(key, value);
        }
    }
}
