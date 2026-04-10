package net.ooder.skill.common.api;

import java.util.List;
import java.util.Map;

public interface LinkApi {

    String createLink(LinkCreation creation);

    void removeLink(String linkId);

    LinkInfo getLink(String linkId);

    List<LinkInfo> listLinks(String sceneGroupId);

    List<LinkInfo> listLinksByAgent(String agentId);

    void updateLinkStatus(String linkId, String status);

    void reconnect(String linkId);

    LinkStats getLinkStats(String linkId);

    interface LinkCreation {
        String getSourceAgentId();
        String getTargetAgentId();
        String getLinkType();
        String getConnectorType();
        Map<String, Object> getConfig();
    }

    interface LinkInfo {
        String getLinkId();
        String getSceneGroupId();
        String getSourceAgentId();
        String getTargetAgentId();
        String getLinkType();
        String getConnectorType();
        String getStatus();
        long getCreateTime();
        long getLastActiveTime();
    }

    interface LinkStats {
        long getTotalMessages();
        long getTotalBytes();
        long getAvgLatency();
        long getErrorCount();
        double getSuccessRate();
    }
}
