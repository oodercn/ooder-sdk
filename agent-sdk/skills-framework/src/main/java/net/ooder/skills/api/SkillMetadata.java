package net.ooder.skills.api;

import java.util.Map;

/**
 * 技能元数据
 *
 * @author Agent-SDK Team
 * @version 3.0
 * @since 3.0
 */
public class SkillMetadata {
    private String author;
    private String license;
    private String homepage;
    private String repository;
    private Map<String, Object> extra;
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public String getLicense() { return license; }
    public void setLicense(String license) { this.license = license; }
    
    public String getHomepage() { return homepage; }
    public void setHomepage(String homepage) { this.homepage = homepage; }
    
    public String getRepository() { return repository; }
    public void setRepository(String repository) { this.repository = repository; }
    
    public Map<String, Object> getExtra() { return extra; }
    public void setExtra(Map<String, Object> extra) { this.extra = extra; }
}
