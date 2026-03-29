package net.ooder.sdk.api.procedure;

import net.ooder.sdk.api.PublicAPI;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Procedure Step Interface
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public interface ProcedureStep extends Serializable {

    String getStepId();
    void setStepId(String stepId);

    String getName();
    void setName(String name);

    String getDescription();
    void setDescription(String description);

    String getStepType();
    void setStepType(String stepType);

    int getOrder();
    void setOrder(int order);

    List<String> getRoleIds();
    void setRoleIds(List<String> roleIds);

    boolean isRequired();
    void setRequired(boolean required);

    boolean isSkippable();
    void setSkippable(boolean skippable);

    String getExecutorType();
    void setExecutorType(String executorType);

    Map<String, Object> getConfig();
    void setConfig(Map<String, Object> config);

    List<String> getDependencies();
    void setDependencies(List<String> dependencies);

    Map<String, Object> getExtensions();
    void setExtensions(Map<String, Object> extensions);
}
