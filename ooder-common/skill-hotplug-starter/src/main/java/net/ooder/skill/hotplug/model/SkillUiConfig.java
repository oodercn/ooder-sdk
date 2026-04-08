package net.ooder.skill.hotplug.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Skill UI配置
 * 包含菜单、页面、组件等UI相关配置
 */
public class SkillUiConfig {

    private String skillId;
    private List<SkillMenu> menus = new ArrayList<>();
    private List<SkillPage> pages = new ArrayList<>();
    private List<SkillComponent> components = new ArrayList<>();
    private Map<String, Object> nexusUi;
    private String basePath;

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public List<SkillMenu> getMenus() {
        return menus;
    }

    public void setMenus(List<SkillMenu> menus) {
        this.menus = menus != null ? menus : new ArrayList<>();
    }

    public List<SkillPage> getPages() {
        return pages;
    }

    public void setPages(List<SkillPage> pages) {
        this.pages = pages != null ? pages : new ArrayList<>();
    }

    public List<SkillComponent> getComponents() {
        return components;
    }

    public void setComponents(List<SkillComponent> components) {
        this.components = components != null ? components : new ArrayList<>();
    }

    public Map<String, Object> getNexusUi() {
        return nexusUi;
    }

    public void setNexusUi(Map<String, Object> nexusUi) {
        this.nexusUi = nexusUi;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public boolean hasMenus() {
        return menus != null && !menus.isEmpty();
    }

    public boolean hasPages() {
        return pages != null && !pages.isEmpty();
    }

    public boolean hasComponents() {
        return components != null && !components.isEmpty();
    }

    public List<SkillMenu> getMenusByRole(String role) {
        List<SkillMenu> roleMenus = new ArrayList<>();
        for (SkillMenu menu : menus) {
            if (role.equals(menu.getRole())) {
                roleMenus.add(menu);
            }
        }
        return roleMenus;
    }

    public List<String> getCssDependencies() {
        List<String> allCssDeps = new ArrayList<>();
        for (SkillPage page : pages) {
            allCssDeps.addAll(page.getCssDependencies());
        }
        return allCssDeps;
    }

    public List<String> getJsDependencies() {
        List<String> allJsDeps = new ArrayList<>();
        for (SkillPage page : pages) {
            allJsDeps.addAll(page.getJsDependencies());
        }
        return allJsDeps;
    }

    @Override
    public String toString() {
        return "SkillUiConfig{" +
                "skillId='" + skillId + '\'' +
                ", menus=" + menus.size() +
                ", pages=" + pages.size() +
                ", components=" + components.size() +
                '}';
    }
}
