/**
 * $RCSfile: AIGCPromptBean.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.ai.bean;

import net.ooder.annotation.AIGCPrompt;
import net.ooder.annotation.AnnotationType;
import net.ooder.annotation.CustomBean;
import net.ooder.web.util.AnnotationUtil;

/**
 * AIGCPrompt注解对应的Bean类
 */
@AnnotationType(clazz = AIGCPrompt.class)
public class AIGCPromptBean implements CustomBean {
    private String template;
    private String[] variables;
    private int maxTokens;
    private float temperature;
    private String version;
    private boolean cacheable;

    // Getter和Setter方法
    public String getTemplate() { return template; }
    public void setTemplate(String template) { this.template = template; }

    public String[] getVariables() { return variables; }
    public void setVariables(String[] variables) { this.variables = variables; }

    public int getMaxTokens() { return maxTokens; }
    public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }

    public float getTemperature() { return temperature; }
    public void setTemperature(float temperature) { this.temperature = temperature; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public boolean isCacheable() { return cacheable; }
    public void setCacheable(boolean cacheable) { this.cacheable = cacheable; }

    @Override
    public String toAnnotationStr() {
        return AnnotationUtil.toAnnotationStr(this);
    }
}