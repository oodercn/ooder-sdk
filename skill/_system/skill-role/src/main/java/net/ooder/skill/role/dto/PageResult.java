package net.ooder.skill.role.dto;

import java.util.List;

public class PageResult<T> {
    
    private List<T> list;
    private int total;
    private int pageNum;
    private int pageSize;

    public List<T> getList() { return list; }
    public void setList(List<T> list) { this.list = list; }
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
    public int getPageNum() { return pageNum; }
    public void setPageNum(int pageNum) { this.pageNum = pageNum; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
}
