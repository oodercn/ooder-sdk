package net.ooder.sdk.api.fusion;

import net.ooder.sdk.api.PublicAPI;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Fused Role Interface
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public interface FusedRole extends Serializable {

    String getRoleId();
    void setRoleId(String roleId);

    String getName();
    void setName(String name);

    String getDescription();
    void setDescription(String description);

    int getPriority();
    void setPriority(int priority);

    boolean isRequired();
    void setRequired(boolean required);

    int getMinCount();
    void setMinCount(int minCount);

    int getMaxCount();
    void setMaxCount(int maxCount);

    List<String> getRequiredCapabilities();
    void setRequiredCapabilities(List<String> requiredCapabilities);

    String getSource();
    void setSource(String source);

    Map<String, Object> getExtensions();
    void setExtensions(Map<String, Object> extensions);
}
