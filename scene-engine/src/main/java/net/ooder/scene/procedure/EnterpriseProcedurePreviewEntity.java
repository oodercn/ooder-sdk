package net.ooder.scene.procedure;

import net.ooder.sdk.api.procedure.EnterpriseProcedure;
import net.ooder.sdk.api.procedure.EnterpriseProcedurePreview;

import java.util.ArrayList;
import java.util.List;

public class EnterpriseProcedurePreviewEntity implements EnterpriseProcedurePreview {

    private static final long serialVersionUID = 1L;

    private EnterpriseProcedure procedure;
    private double confidence;
    private List<String> warnings = new ArrayList<>();
    private List<String> missingFields = new ArrayList<>();
    private List<String> suggestions = new ArrayList<>();

    public EnterpriseProcedurePreviewEntity() {
    }

    @Override
    public EnterpriseProcedure getProcedure() {
        return procedure;
    }

    @Override
    public void setProcedure(EnterpriseProcedure procedure) {
        this.procedure = procedure;
    }

    @Override
    public double getConfidence() {
        return confidence;
    }

    @Override
    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    @Override
    public List<String> getWarnings() {
        return warnings;
    }

    @Override
    public void setWarnings(List<String> warnings) {
        this.warnings = warnings != null ? warnings : new ArrayList<>();
    }

    @Override
    public List<String> getMissingFields() {
        return missingFields;
    }

    @Override
    public void setMissingFields(List<String> missingFields) {
        this.missingFields = missingFields != null ? missingFields : new ArrayList<>();
    }

    @Override
    public List<String> getSuggestions() {
        return suggestions;
    }

    @Override
    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions != null ? suggestions : new ArrayList<>();
    }
}
