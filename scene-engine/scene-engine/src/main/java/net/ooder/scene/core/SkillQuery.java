package net.ooder.scene.core;

/**
 * Skill查询条件
 */
public class SkillQuery {
    private String keyword;
    private String category;
    private String subCategory;
    private String tag;
    private String author;
    private String status;
    private String source;
    private String license;
    private String sortBy;
    private String sortOrder;
    private int pageNum = 1;
    private int pageSize = 20;

    public SkillQuery() {}

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getSubCategory() { return subCategory; }
    public void setSubCategory(String subCategory) { this.subCategory = subCategory; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getLicense() { return license; }
    public void setLicense(String license) { this.license = license; }
    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    public String getSortOrder() { return sortOrder; }
    public void setSortOrder(String sortOrder) { this.sortOrder = sortOrder; }
    public int getPageNum() { return pageNum; }
    public void setPageNum(int pageNum) { this.pageNum = pageNum; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
}
