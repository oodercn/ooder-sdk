package net.ooder.skill.common.api;

import java.util.List;

public interface ParticipantApi {

    String joinScene(String sceneGroupId, ParticipantRegistration registration);

    void leaveScene(String sceneGroupId, String participantId);

    void changeRole(String sceneGroupId, String participantId, String newRole);

    ParticipantInfo getParticipant(String sceneGroupId, String participantId);

    List<ParticipantInfo> listParticipants(String sceneGroupId);

    List<ParticipantInfo> listParticipantsByRole(String sceneGroupId, String role);

    void updateParticipantStatus(String sceneGroupId, String participantId, String status);

    void heartbeat(String sceneGroupId, String participantId);

    interface ParticipantRegistration {
        String getUserId();
        String getRole();
        String getAgentId();
        String getParticipantType();
        List<String> getCapabilities();
    }

    interface ParticipantInfo {
        String getParticipantId();
        String getUserId();
        String getRole();
        String getStatus();
        String getParticipantType();
        String getAgentId();
        List<String> getCapabilities();
        long getJoinTime();
        long getLastHeartbeat();
    }
}
