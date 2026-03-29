package net.ooder.sdk.api.procedure;

import net.ooder.sdk.api.PublicAPI;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Procedure Role Definition Interface
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public interface ProcedureRole extends Serializable {

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

    List<String> getPositionIds();
    void setPositionIds(List<String> positionIds);

    List<String> getPermissionIds();
    void setPermissionIds(List<String> permissionIds);

    List<String> getRequiredCapabilities();
    void setRequiredCapabilities(List<String> requiredCapabilities);

    List<ActivationStepRef> getActivationSteps();
    void setActivationSteps(List<ActivationStepRef> activationSteps);

    List<String> getMenuIds();
    void setMenuIds(List<String> menuIds);

    Map<String, Object> getExtensions();
    void setExtensions(Map<String, Object> extensions);
}
