package net.ooder.skills.config;

import java.util.Map;

/**
 * 自启动配置
 *
 * @author Agent-SDK Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class SelfStartConfiguration {
    private String name;
    private String condition;
    private int priority;
    private Map<String, Object> params;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public Map<String, Object> getParams() { return params; }
    public void setParams(Map<String, Object> params) { this.params = params; }
}
