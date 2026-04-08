package net.ooder.skill.hotplug.model;

/**
 * Skill菜单配置
 */
public class SkillMenu {

    private String id;
    private String name;
    private String icon;
    private String path;
    private int order;
    private boolean visible;
    private String role;
    private String description;
    private String parentId;
    private String skillId;

    public SkillMenu() {
        this.visible = true;
        this.order = 0;
    }

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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public String getFullPath() {
        if (path == null) {
            return null;
        }
        if (path.startsWith("/")) {
            return path;
        }
        return "/" + path;
    }

    @Override
    public String toString() {
        return "SkillMenu{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", order=" + order +
                '}';
    }
}
