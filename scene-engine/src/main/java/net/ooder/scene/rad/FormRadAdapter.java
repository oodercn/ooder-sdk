package net.ooder.scene.rad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 表单适配器
 *
 * <p>将表单提交事件转换为场景触发</p>
 *
 * <p>配置示例：</p>
 * <pre>
 * scene.engine.rad.form-mapping:
 *   user_register: scene_user_onboarding
 *   leave_apply: scene_leave_approval
 * </pre>
 *
 * @author Ooder Team
 * @since 3.0.0
 */
@Component
public class FormRadAdapter implements RadAdapter {

    private static final Logger log = LoggerFactory.getLogger(FormRadAdapter.class);

    private final Map<String, String> formSceneMapping = new HashMap<>();

    public FormRadAdapter() {
        formSceneMapping.put("user_register", "scene_user_onboarding");
        formSceneMapping.put("leave_apply", "scene_leave_approval");
        formSceneMapping.put("expense_claim", "scene_expense_approval");
    }

    @Override
    public String getName() {
        return "form-adapter";
    }

    @Override
    public String getType() {
        return "form";
    }

    @Override
    public String triggerScene(String eventType, Map<String, Object> eventData) {
        String sceneId = formSceneMapping.get(eventType);
        if (sceneId == null) {
            log.warn("No scene mapping for form: {}", eventType);
            return null;
        }

        log.info("Triggering scene {} for form {}", sceneId, eventType);

        return "scene-instance-" + System.currentTimeMillis();
    }

    @Override
    public boolean supports(String eventType) {
        return formSceneMapping.containsKey(eventType);
    }

    public void addMapping(String formId, String sceneId) {
        formSceneMapping.put(formId, sceneId);
    }
}
