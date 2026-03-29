package net.ooder.scene.fusion;

import net.ooder.sdk.api.fusion.FusionTemplateQueryRequest;
import net.ooder.sdk.api.fusion.TemplateStatus;

public class FusionTemplateQueryRequestEntity implements FusionTemplateQueryRequest {

    private static final long serialVersionUID = 1L;

    private String skillId;
    private String procedureId;
    private TemplateStatus status;
    private String keyword;
    private int page = 1;
    private int pageSize = 20;

    public FusionTemplateQueryRequestEntity() {
    }

    @Override
    public String getSkillId() {
        return skillId;
    }

    @Override
    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    @Override
    public String getProcedureId() {
        return procedureId;
    }

    @Override
    public void setProcedureId(String procedureId) {
        this.procedureId = procedureId;
    }

    @Override
    public TemplateStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(TemplateStatus status) {
        this.status = status;
    }

    @Override
    public String getKeyword() {
        return keyword;
    }

    @Override
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public void setPage(int page) {
        this.page = page > 0 ? page : 1;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize > 0 ? pageSize : 20;
    }
}
