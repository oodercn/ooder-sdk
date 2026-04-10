package net.ooder.skill.common.api;

import java.util.List;
import java.util.Map;

public interface CapabilityRegisterApi {

    String registerCapability(CapabilityRegistration registration);

    void unregisterCapability(String capabilityId);

    CapabilityInfo getCapability(String capabilityId);

    List<CapabilityInfo> listCapabilities(String sceneGroupId);

    List<CapabilityInfo> findCapabilitiesByType(String capabilityType);

    List<CapabilityInfo> findCapabilitiesBySceneType(String sceneType);

    void updateCapabilityStatus(String capabilityId, String status);

    interface CapabilityRegistration {
        String getCapabilityId();
        String getName();
        String getDescription();
        String getCategory();
        String getConnectorType();
        List<ParamDefinition> getParameters();
        ReturnDefinition getReturns();
        Map<String, Object> getConfig();
    }

    interface CapabilityInfo {
        String getCapabilityId();
        String getName();
        String getDescription();
        String getCategory();
        String getConnectorType();
        String getStatus();
        String getProviderId();
        String getProviderType();
    }

    interface ParamDefinition {
        String getName();
        String getType();
        boolean isRequired();
        Object getDefaultValue();
        String getDescription();
    }

    interface ReturnDefinition {
        String getType();
        String getDescription();
    }
}
