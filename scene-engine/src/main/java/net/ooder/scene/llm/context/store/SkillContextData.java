package net.ooder.scene.llm.context.store;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 可持久化的技能上下文数据
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class SkillContextData implements Serializable {

    private static final long serialVersionUID = 1L;

    private String skillId;
    private String systemPrompt;
    private Map<String, Object> roleConfig;
    private Map<String, Object> knowledgeConfig;
    private Map<String, Object> functionConfig;
    private long createTime;
    private long updateTime;

    public SkillContextData() {
        this.roleConfig = new HashMap<>();
        this.knowledgeConfig = new HashMap<>();
        this.functionConfig = new HashMap<>();
        this.createTime = System.currentTimeMillis();
        this.updateTime = this.createTime;
    }

    public static SkillContextData create(String skillId) {
        SkillContextData data = new SkillContextData();
        data.setSkillId(skillId);
        return data;
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
        this.updateTime = System.currentTimeMillis();
    }

    public Map<String, Object> getRoleConfig() {
        return roleConfig;
    }

    public void setRoleConfig(Map<String, Object> roleConfig) {
        this.roleConfig = roleConfig != null ? roleConfig : new HashMap<>();
        this.updateTime = System.currentTimeMillis();
    }

    public Map<String, Object> getKnowledgeConfig() {
        return knowledgeConfig;
    }

    public void setKnowledgeConfig(Map<String, Object> knowledgeConfig) {
        this.knowledgeConfig = knowledgeConfig != null ? knowledgeConfig : new HashMap<>();
        this.updateTime = System.currentTimeMillis();
    }

    public Map<String, Object> getFunctionConfig() {
        return functionConfig;
    }

    public void setFunctionConfig(Map<String, Object> functionConfig) {
        this.functionConfig = functionConfig != null ? functionConfig : new HashMap<>();
        this.updateTime = System.currentTimeMillis();
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
}
