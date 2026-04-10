package net.ooder.skill.common.discovery;

public class RepositoryDTO {
    
    private String fullName;
    private String name;
    private String description;
    private String htmlUrl;
    private String latestVersion;
    private String defaultBranch;
    private Long stars;
    private Long forks;
    private Boolean isPrivate;
    
    public RepositoryDTO() {}
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getHtmlUrl() { return htmlUrl; }
    public void setHtmlUrl(String htmlUrl) { this.htmlUrl = htmlUrl; }
    
    public String getLatestVersion() { return latestVersion; }
    public void setLatestVersion(String latestVersion) { this.latestVersion = latestVersion; }
    
    public String getDefaultBranch() { return defaultBranch; }
    public void setDefaultBranch(String defaultBranch) { this.defaultBranch = defaultBranch; }
    
    public Long getStars() { return stars; }
    public void setStars(Long stars) { this.stars = stars; }
    
    public Long getForks() { return forks; }
    public void setForks(Long forks) { this.forks = forks; }
    
    public Boolean getIsPrivate() { return isPrivate; }
    public void setIsPrivate(Boolean isPrivate) { this.isPrivate = isPrivate; }
}
