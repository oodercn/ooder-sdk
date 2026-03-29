package net.ooder.scene.procedure;

import net.ooder.sdk.api.procedure.*;

/**
 * 企业规范流程查询请求实体实现
 *
 * @author SE SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class EnterpriseProcedureQueryRequestEntity implements EnterpriseProcedureQueryRequest {

    private String organizationId;
    private String category;
    private ProcedureStatus status;
    private ProcedureSource source;
    private String keyword;
    private int minCompleteness;
    private int page = 1;
    private int pageSize = 20;

    @Override
    public String getOrganizationId() {
        return organizationId;
    }

    @Override
    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public ProcedureStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(ProcedureStatus status) {
        this.status = status;
    }

    @Override
    public ProcedureSource getSource() {
        return source;
    }

    @Override
    public void setSource(ProcedureSource source) {
        this.source = source;
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
    public int getMinCompleteness() {
        return minCompleteness;
    }

    @Override
    public void setMinCompleteness(int minCompleteness) {
        this.minCompleteness = minCompleteness;
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
