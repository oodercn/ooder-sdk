package net.ooder.scene.llm.context;

import java.util.HashMap;
import java.util.Map;

/**
 * 上下文更新事件
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class ContextUpdate {

    private ContextUpdateType type;
    private String targetId;
    private Map<String, Object> data;
    private boolean replace;

    public ContextUpdate() {
        this.data = new HashMap<>();
        this.replace = false;
    }

    public static ContextUpdate pageChange(String pageId) {
        ContextUpdate update = new ContextUpdate();
        update.setType(ContextUpdateType.PAGE_CHANGE);
        update.setTargetId(pageId);
        return update;
    }

    public static ContextUpdate skillChange(String skillId) {
        ContextUpdate update = new ContextUpdate();
        update.setType(ContextUpdateType.SKILL_CHANGE);
        update.setTargetId(skillId);
        return update;
    }

    public static ContextUpdate stateUpdate(String key, Object value) {
        ContextUpdate update = new ContextUpdate();
        update.setType(ContextUpdateType.STATE_UPDATE);
        update.getData().put(key, value);
        return update;
    }

    public ContextUpdateType getType() {
        return type;
    }

    public void setType(ContextUpdateType type) {
        this.type = type;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data != null ? data : new HashMap<>();
    }

    public boolean isReplace() {
        return replace;
    }

    public void setReplace(boolean replace) {
        this.replace = replace;
    }

    /**
     * 上下文更新类型
     */
    public enum ContextUpdateType {
        PAGE_CHANGE("page_change", "页面跳转"),
        SKILL_CHANGE("skill_change", "技能切换"),
        STATE_UPDATE("state_update", "状态更新"),
        CONTEXT_CLEAR("context_clear", "上下文清空");

        private final String code;
        private final String description;

        ContextUpdateType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }
}
