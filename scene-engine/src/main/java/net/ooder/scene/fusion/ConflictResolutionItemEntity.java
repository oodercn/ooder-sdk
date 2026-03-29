package net.ooder.scene.fusion;

import net.ooder.sdk.api.fusion.ConflictResolutionItem;
import net.ooder.sdk.api.fusion.ConflictResolution;

public class ConflictResolutionItemEntity implements ConflictResolutionItem {

    private static final long serialVersionUID = 1L;

    private String conflictId;
    private ConflictResolution resolution;
    private Object resolvedValue;
    private String comment;

    public ConflictResolutionItemEntity() {
    }

    @Override
    public String getConflictId() {
        return conflictId;
    }

    @Override
    public void setConflictId(String conflictId) {
        this.conflictId = conflictId;
    }

    @Override
    public ConflictResolution getResolution() {
        return resolution;
    }

    @Override
    public void setResolution(ConflictResolution resolution) {
        this.resolution = resolution;
    }

    @Override
    public Object getResolvedValue() {
        return resolvedValue;
    }

    @Override
    public void setResolvedValue(Object resolvedValue) {
        this.resolvedValue = resolvedValue;
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }
}
