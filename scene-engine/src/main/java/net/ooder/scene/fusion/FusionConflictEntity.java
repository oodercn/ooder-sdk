package net.ooder.scene.fusion;

import net.ooder.sdk.api.fusion.*;

/**
 * 融合冲突实体实现
 *
 * @author SE SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class FusionConflictEntity implements FusionConflict {

    private static final long serialVersionUID = 1L;

    private String conflictId;
    private String field;
    private ConflictType type;
    private Object enterpriseValue;
    private Object skillValue;
    private ConflictResolution resolution;
    private Object resolvedValue;
    private String resolvedBy;
    private Long resolvedAt;
    private String comment;

    @Override
    public String getConflictId() {
        return conflictId;
    }

    @Override
    public void setConflictId(String conflictId) {
        this.conflictId = conflictId;
    }

    @Override
    public String getField() {
        return field;
    }

    @Override
    public void setField(String field) {
        this.field = field;
    }

    @Override
    public ConflictType getType() {
        return type;
    }

    @Override
    public void setType(ConflictType type) {
        this.type = type;
    }

    @Override
    public Object getEnterpriseValue() {
        return enterpriseValue;
    }

    @Override
    public void setEnterpriseValue(Object enterpriseValue) {
        this.enterpriseValue = enterpriseValue;
    }

    @Override
    public Object getSkillValue() {
        return skillValue;
    }

    @Override
    public void setSkillValue(Object skillValue) {
        this.skillValue = skillValue;
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
    public String getResolvedBy() {
        return resolvedBy;
    }

    @Override
    public void setResolvedBy(String resolvedBy) {
        this.resolvedBy = resolvedBy;
    }

    @Override
    public Long getResolvedAt() {
        return resolvedAt;
    }

    @Override
    public void setResolvedAt(Long resolvedAt) {
        this.resolvedAt = resolvedAt;
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
