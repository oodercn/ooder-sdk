package net.ooder.scene.config;

/**
 * Skill 分类
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class SkillCategory {
    
    private String id;
    private String name;
    private String description;
    private int count;
    private String icon;
    
    public SkillCategory() {}
    
    public SkillCategory(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public SkillCategory(String id, String name, int count) {
        this.id = id;
        this.name = name;
        this.count = count;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
}
