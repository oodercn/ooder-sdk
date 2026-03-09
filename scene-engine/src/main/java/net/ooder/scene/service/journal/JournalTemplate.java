package net.ooder.scene.service.journal;

import java.util.ArrayList;
import java.util.List;

/**
 * 日志模板
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class JournalTemplate {

    private String templateId;
    private String sceneId;
    private String name;
    private String description;
    private String contentTemplate;
    private List<String> sections = new ArrayList<>();
    private boolean isDefault = false;
    private boolean enabled = true;

    public JournalTemplate() {}

    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }

    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContentTemplate() { return contentTemplate; }
    public void setContentTemplate(String contentTemplate) { this.contentTemplate = contentTemplate; }

    public List<String> getSections() { return sections; }
    public void setSections(List<String> sections) { this.sections = sections; }

    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean aDefault) { isDefault = aDefault; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public void addSection(String section) {
        sections.add(section);
    }

    public static JournalTemplate weeklyReport() {
        JournalTemplate template = new JournalTemplate();
        template.setName("周报模板");
        template.addSection("本周工作");
        template.addSection("下周计划");
        template.addSection("问题与风险");
        template.setContentTemplate("## 本周工作\n\n[请填写本周工作内容]\n\n## 下周计划\n\n[请填写下周计划]\n\n## 问题与风险\n\n[请填写遇到的问题和风险]");
        return template;
    }

    public static JournalTemplate dailyReport() {
        JournalTemplate template = new JournalTemplate();
        template.setName("日报模板");
        template.addSection("今日工作");
        template.addSection("明日计划");
        template.setContentTemplate("## 今日工作\n\n[请填写今日工作内容]\n\n## 明日计划\n\n[请填写明日计划]");
        return template;
    }
}
