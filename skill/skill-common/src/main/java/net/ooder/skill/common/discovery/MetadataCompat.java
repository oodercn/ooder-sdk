package net.ooder.skill.common.discovery;

import java.util.Map;

public final class MetadataCompat {
    
    private MetadataCompat() {}
    
    public static String getVisibility(Map<String, Object> skill) {
        if (skill == null) {
            return "public";
        }
        
        String visibility = (String) skill.get("visibility");
        if (visibility != null) {
            return visibility;
        }
        
        Object labelsObj = skill.get("labels");
        if (labelsObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> labels = (Map<String, Object>) labelsObj;
            Object sceneVisibility = labels.get("scene.visibility");
            if (sceneVisibility != null) {
                return String.valueOf(sceneVisibility);
            }
        }
        
        return "public";
    }
    
    public static String getSkillId(Map<String, Object> skill) {
        if (skill == null) {
            return null;
        }
        String id = (String) skill.get("skillId");
        if (id == null) {
            id = (String) skill.get("id");
        }
        return id;
    }
    
    public static boolean isSceneSkill(Map<String, Object> skill) {
        if (skill == null) {
            return false;
        }
        
        Object typeObj = skill.get("type");
        String type = typeObj != null ? String.valueOf(typeObj) : null;
        
        Object sceneIdObj = skill.get("sceneId");
        
        return "SCENE".equals(type) || "scene".equals(type) || "scene-skill".equals(type) || sceneIdObj != null;
    }
}
