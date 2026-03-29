package net.ooder.sdk.api.fusion;

import net.ooder.sdk.api.PublicAPI;

import java.io.Serializable;
import java.util.Map;

/**
 * Capability Binding Definition Interface
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public interface CapabilityBindingDef extends Serializable {

    String getCapabilityId();
    void setCapabilityId(String capabilityId);

    String getCapabilityName();
    void setCapabilityName(String capabilityName);

    String getBindingType();
    void setBindingType(String bindingType);

    String getRoleId();
    void setRoleId(String roleId);

    Map<String, Object> getConfig();
    void setConfig(Map<String, Object> config);
}
