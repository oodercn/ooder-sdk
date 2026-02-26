package net.ooder.sdk.plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Skill 元数据
 */
public class SkillMetadata {
    private String id;
    private String name;
    private String version;
    private String description;
    private String author;
    private String type;
    private String category;
    private List<String> tags = new ArrayList<>();
    private List<SkillDependency> dependencies = new ArrayList<>();
    private List<java.util.Map<String, Object>> capabilities = new ArrayList<>();
    private List<java.util.Map<String, Object>> routes = new ArrayList<>();
    private List<java.util.Map<String, Object>> services = new ArrayList<>();
    private java.util.Map<String, Object> ui = new java.util.HashMap<>();
    private List<java.util.Map<String, Object>> permissions = new ArrayList<>();
    private List<java.util.Map<String, Object>> configuration = new ArrayList<>();
    private java.util.Map<String, Object> healthCheck = new java.util.HashMap<>();
    private java.util.Map<String, Object> metrics = new java.util.HashMap<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags != null ? tags : new ArrayList<>();
    }

    public List<SkillDependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<SkillDependency> dependencies) {
        this.dependencies = dependencies != null ? dependencies : new ArrayList<>();
    }

    public List<java.util.Map<String, Object>> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<java.util.Map<String, Object>> capabilities) {
        this.capabilities = capabilities != null ? capabilities : new ArrayList<>();
    }

    public List<java.util.Map<String, Object>> getRoutes() {
        return routes;
    }

    public void setRoutes(List<java.util.Map<String, Object>> routes) {
        this.routes = routes != null ? routes : new ArrayList<>();
    }

    public List<java.util.Map<String, Object>> getServices() {
        return services;
    }

    public void setServices(List<java.util.Map<String, Object>> services) {
        this.services = services != null ? services : new ArrayList<>();
    }

    public java.util.Map<String, Object> getUi() {
        return ui;
    }

    public void setUi(java.util.Map<String, Object> ui) {
        this.ui = ui != null ? ui : new java.util.HashMap<>();
    }

    public List<java.util.Map<String, Object>> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<java.util.Map<String, Object>> permissions) {
        this.permissions = permissions != null ? permissions : new ArrayList<>();
    }

    public List<java.util.Map<String, Object>> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(List<java.util.Map<String, Object>> configuration) {
        this.configuration = configuration != null ? configuration : new ArrayList<>();
    }

    public java.util.Map<String, Object> getHealthCheck() {
        return healthCheck;
    }

    public void setHealthCheck(java.util.Map<String, Object> healthCheck) {
        this.healthCheck = healthCheck != null ? healthCheck : new java.util.HashMap<>();
    }

    public java.util.Map<String, Object> getMetrics() {
        return metrics;
    }

    public void setMetrics(java.util.Map<String, Object> metrics) {
        this.metrics = metrics != null ? metrics : new java.util.HashMap<>();
    }
}
