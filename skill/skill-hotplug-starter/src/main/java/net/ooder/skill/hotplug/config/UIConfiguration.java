package net.ooder.skill.hotplug.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * UI配置
 */
public class UIConfiguration {

    /**
     * UI类型 (html, vue, react等)
     */
    private String type;

    /**
     * 入口文件
     */
    private String entry;

    /**
     * 静态资源路径
     */
    private List<String> staticResources;

    /**
     * CDN依赖
     */
    private List<String> cdnDependencies;

    /**
     * 从Map解析
     */
    @SuppressWarnings("unchecked")
    public static UIConfiguration fromMap(Map<String, Object> data) {
        UIConfiguration ui = new UIConfiguration();
        ui.type = (String) data.get("type");
        ui.entry = (String) data.get("entry");
        ui.staticResources = (List<String>) data.get("staticResources");
        ui.cdnDependencies = (List<String>) data.get("cdnDependencies");
        return ui;
    }

    // Getters and Setters

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public List<String> getStaticResources() {
        return staticResources;
    }

    public void setStaticResources(List<String> staticResources) {
        this.staticResources = staticResources;
    }

    public List<String> getCdnDependencies() {
        return cdnDependencies;
    }

    public void setCdnDependencies(List<String> cdnDependencies) {
        this.cdnDependencies = cdnDependencies;
    }
}
