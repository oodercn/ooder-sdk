package net.ooder.sdk.api.procedure;

import net.ooder.sdk.api.PublicAPI;

import java.io.Serializable;
import java.util.Map;

/**
 * Activation Step Reference Interface
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public interface ActivationStepRef extends Serializable {

    String getStepId();
    void setStepId(String stepId);

    String getStepName();
    void setStepName(String stepName);

    boolean isRequired();
    void setRequired(boolean required);

    Map<String, Object> getConfig();
    void setConfig(Map<String, Object> config);
}
