package net.ooder.scene.core;

import java.util.List;
import java.util.Map;

/**
 * Skill信息
 */
public class SkillInfo {
    private String skillId;
    private String name;
    private String description;
    private String version;
    private String author;
    private String status;
    private String category;
    private String subCategory;
    private String categoryIcon;
    private int categorySort;
    private List<String> tags;
    private List<CapabilityInfo> capabilities;
    private Map<String, Object> metadata;
    private long installCount;
    private long createdAt;
    private long updatedAt;
    private String source;
    private String repository;
    private String license;
    private String homepage;

    public SkillInfo() {}

    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getSubCategory() { return subCategory; }
    public void setSubCategory(String subCategory) { this.subCategory = subCategory; }
    public String getCategoryIcon() { return categoryIcon; }
    public void setCategoryIcon(String categoryIcon) { this.categoryIcon = categoryIcon; }
    public int getCategorySort() { return categorySort; }
    public void setCategorySort(int categorySort) { this.categorySort = categorySort; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public List<CapabilityInfo> getCapabilities() { return capabilities; }
    public void setCapabilities(List<CapabilityInfo> capabilities) { this.capabilities = capabilities; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    public long getInstallCount() { return installCount; }
    public void setInstallCount(long installCount) { this.installCount = installCount; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getRepository() { return repository; }
    public void setRepository(String repository) { this.repository = repository; }
    public String getLicense() { return license; }
    public void setLicense(String license) { this.license = license; }
    public String getHomepage() { return homepage; }
    public void setHomepage(String homepage) { this.homepage = homepage; }
}
