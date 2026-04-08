package net.ooder.skill.hotplug.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Skill页面配置
 */
public class SkillPage {

    private String id;
    private String name;
    private String path;
    private String htmlPath;
    private String jsPath;
    private String cssPath;
    private List<String> jsDependencies = new ArrayList<>();
    private List<String> cssDependencies = new ArrayList<>();
    private String role;
    private boolean visible;
    private boolean cacheable;
    private String skillId;

    public SkillPage() {
        this.visible = true;
        this.cacheable = true;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getHtmlPath() {
        return htmlPath;
    }

    public void setHtmlPath(String htmlPath) {
        this.htmlPath = htmlPath;
    }

    public String getJsPath() {
        return jsPath;
    }

    public void setJsPath(String jsPath) {
        this.jsPath = jsPath;
    }

    public String getCssPath() {
        return cssPath;
    }

    public void setCssPath(String cssPath) {
        this.cssPath = cssPath;
    }

    public List<String> getJsDependencies() {
        return jsDependencies;
    }

    public void setJsDependencies(List<String> jsDependencies) {
        this.jsDependencies = jsDependencies != null ? jsDependencies : new ArrayList<>();
    }

    public List<String> getCssDependencies() {
        return cssDependencies;
    }

    public void setCssDependencies(List<String> cssDependencies) {
        this.cssDependencies = cssDependencies != null ? cssDependencies : new ArrayList<>();
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isCacheable() {
        return cacheable;
    }

    public void setCacheable(boolean cacheable) {
        this.cacheable = cacheable;
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public String getResourceBasePath() {
        if (path == null) {
            return "static";
        }
        String base = path;
        if (base.startsWith("/")) {
            base = base.substring(1);
        }
        return "static/" + base;
    }

    public String getDefaultHtmlPath() {
        if (htmlPath != null) {
            return htmlPath;
        }
        return getResourceBasePath() + "/index.html";
    }

    public String getDefaultJsPath() {
        if (jsPath != null) {
            return jsPath;
        }
        return getResourceBasePath() + "/js/main.js";
    }

    public String getDefaultCssPath() {
        if (cssPath != null) {
            return cssPath;
        }
        return getResourceBasePath() + "/css/style.css";
    }

    @Override
    public String toString() {
        return "SkillPage{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
