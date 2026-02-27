package net.ooder.scene.core;

import java.util.List;

/**
 * 分页请求
 */
public class PageRequest {
    private int pageNum = 1;
    private int pageSize = 20;
    private String sortBy;
    private String sortOrder = "DESC";

    public PageRequest() {}

    public PageRequest(int pageNum, int pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public int getPageNum() { return pageNum; }
    public void setPageNum(int pageNum) { this.pageNum = pageNum; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    public String getSortOrder() { return sortOrder; }
    public void setSortOrder(String sortOrder) { this.sortOrder = sortOrder; }

    public int getOffset() { return (pageNum - 1) * pageSize; }
}
