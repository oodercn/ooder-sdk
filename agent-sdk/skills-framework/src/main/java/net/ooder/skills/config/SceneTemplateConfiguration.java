package net.ooder.skills.config;

import java.util.List;
import java.util.Map;

/**
 * SceneTemplate 统一配置类
 *
 * @author Agent-SDK Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class SceneTemplateConfiguration {

    private String templateId;
    private String templateName;
    private String description;
    private boolean enabled;
    private MainFirstConfiguration mainFirst;
    private Map<String, Object> properties;

    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }

    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public MainFirstConfiguration getMainFirst() { return mainFirst; }
    public void setMainFirst(MainFirstConfiguration mainFirst) { this.mainFirst = mainFirst; }

    public Map<String, Object> getProperties() { return properties; }
    public void setProperties(Map<String, Object> properties) { this.properties = properties; }
}
