package net.ooder.scene.skill.audit;

import java.util.List;

/**
 * 审计日志查询结果
 *
 * @author ooder
 * @since 2.3
 */
public class AuditLogQueryResult {
    
    /** 日志列表 */
    private List<AuditEntry> entries;
    
    /** 总条数 */
    private long totalCount;
    
    /** 查询起始位置 */
    private int offset;
    
    /** 查询限制 */
    private int limit;
    
    public AuditLogQueryResult() {}
    
    public AuditLogQueryResult(List<AuditEntry> entries, long totalCount) {
        this.entries = entries;
        this.totalCount = totalCount;
    }
    
    // Getters and Setters
    public List<AuditEntry> getEntries() { return entries; }
    public void setEntries(List<AuditEntry> entries) { this.entries = entries; }
    
    public long getTotalCount() { return totalCount; }
    public void setTotalCount(long totalCount) { this.totalCount = totalCount; }
    
    public int getOffset() { return offset; }
    public void setOffset(int offset) { this.offset = offset; }
    
    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }
}
