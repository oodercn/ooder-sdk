package net.ooder.sdk.api.procedure;

import net.ooder.sdk.api.PublicAPI;

import java.io.Serializable;
import java.util.List;

/**
 * Enterprise Procedure Preview Interface
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public interface EnterpriseProcedurePreview extends Serializable {

    EnterpriseProcedure getProcedure();
    void setProcedure(EnterpriseProcedure procedure);

    double getConfidence();
    void setConfidence(double confidence);

    List<String> getWarnings();
    void setWarnings(List<String> warnings);

    List<String> getMissingFields();
    void setMissingFields(List<String> missingFields);

    List<String> getSuggestions();
    void setSuggestions(List<String> suggestions);
}
