package net.ooder.skills.api.context;

import java.util.List;
import java.util.Map;

/**
 * 组织上下文
 *
 * <p>描述组织的结构、系统和数据资产信息</p>
 *
 * @author Agent-SDK Team
 * @version 2.4.0
 * @since 2.4.0
 */
public class OrganizationContext {

    private String orgId;
    private String orgName;
    private String industry;
    private String description;
    private List<Department> departments;
    private List<SystemInfo> systems;
    private List<DataAsset> dataAssets;
    private UserScale userScale;
    private Map<String, Object> metadata;

    public String getOrgId() { return orgId; }
    public void setOrgId(String orgId) { this.orgId = orgId; }

    public String getOrgName() { return orgName; }
    public void setOrgName(String orgName) { this.orgName = orgName; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Department> getDepartments() { return departments; }
    public void setDepartments(List<Department> departments) { this.departments = departments; }

    public List<SystemInfo> getSystems() { return systems; }
    public void setSystems(List<SystemInfo> systems) { this.systems = systems; }

    public List<DataAsset> getDataAssets() { return dataAssets; }
    public void setDataAssets(List<DataAsset> dataAssets) { this.dataAssets = dataAssets; }

    public UserScale getUserScale() { return userScale; }
    public void setUserScale(UserScale userScale) { this.userScale = userScale; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    /**
     * 部门信息
     */
    public static class Department {
        private String id;
        private String name;
        private String parentId;
        private int level;
        private List<String> functions;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getParentId() { return parentId; }
        public void setParentId(String parentId) { this.parentId = parentId; }

        public int getLevel() { return level; }
        public void setLevel(int level) { this.level = level; }

        public List<String> getFunctions() { return functions; }
        public void setFunctions(List<String> functions) { this.functions = functions; }
    }

    /**
     * 系统信息
     */
    public static class SystemInfo {
        private String id;
        private String name;
        private String type;
        private String version;
        private String status;
        private List<String> integrations;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public List<String> getIntegrations() { return integrations; }
        public void setIntegrations(List<String> integrations) { this.integrations = integrations; }
    }

    /**
     * 数据资产
     */
    public static class DataAsset {
        private String id;
        private String name;
        private String type;
        private String description;
        private String schema;
        private String sensitivity;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getSchema() { return schema; }
        public void setSchema(String schema) { this.schema = schema; }

        public String getSensitivity() { return sensitivity; }
        public void setSensitivity(String sensitivity) { this.sensitivity = sensitivity; }
    }

    /**
     * 用户规模
     */
    public static class UserScale {
        private int totalUsers;
        private int activeUsers;
        private int concurrentUsers;

        public int getTotalUsers() { return totalUsers; }
        public void setTotalUsers(int totalUsers) { this.totalUsers = totalUsers; }

        public int getActiveUsers() { return activeUsers; }
        public void setActiveUsers(int activeUsers) { this.activeUsers = activeUsers; }

        public int getConcurrentUsers() { return concurrentUsers; }
        public void setConcurrentUsers(int concurrentUsers) { this.concurrentUsers = concurrentUsers; }
    }
}
