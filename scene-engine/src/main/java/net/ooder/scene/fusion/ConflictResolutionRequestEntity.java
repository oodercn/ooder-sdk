package net.ooder.scene.fusion;

import net.ooder.sdk.api.fusion.ConflictResolutionItem;
import net.ooder.sdk.api.fusion.ConflictResolutionRequest;

import java.util.ArrayList;
import java.util.List;

public class ConflictResolutionRequestEntity implements ConflictResolutionRequest {

    private static final long serialVersionUID = 1L;

    private List<ConflictResolutionItem> resolutions = new ArrayList<>();
    private String resolvedBy;

    public ConflictResolutionRequestEntity() {
    }

    @Override
    public List<ConflictResolutionItem> getResolutions() {
        return resolutions;
    }

    @Override
    public void setResolutions(List<ConflictResolutionItem> resolutions) {
        this.resolutions = resolutions != null ? resolutions : new ArrayList<>();
    }

    @Override
    public String getResolvedBy() {
        return resolvedBy;
    }

    @Override
    public void setResolvedBy(String resolvedBy) {
        this.resolvedBy = resolvedBy;
    }
}
