package net.ooder.skill.common.api;

import java.util.List;
import java.util.Map;

public interface SceneContextApi {

    String getSceneGroupId();

    String getSceneType();

    String getSkillId();

    Object getConfig(String key);

    void setConfig(String key, Object value);

    Object invokeCapability(String capabilityId, Map<String, Object> params);

    List<ParticipantInfo> getParticipants();

    ParticipantInfo getParticipant(String participantId);

    void publishEvent(String eventType, Map<String, Object> data);

    interface ParticipantInfo {
        String getParticipantId();
        String getUserId();
        String getRole();
        String getStatus();
        List<String> getCapabilities();
    }
}
