package net.ooder.skills.config;

import java.util.Map;

/**
 * 自检配置
 *
 * @author Agent-SDK Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class SelfCheckConfiguration {
    private String name;
    private String type;
    private Map<String, Object> params;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Map<String, Object> getParams() { return params; }
    public void setParams(Map<String, Object> params) { this.params = params; }
}
