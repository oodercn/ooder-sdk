package net.ooder.bpm.engine.query;

import net.ooder.bpm.enums.right.RightConditionEnums;
import net.ooder.bpm.enums.right.RightCtx;
import net.ooder.common.Filter;
import net.ooder.common.Page;

import java.util.Map;

/**
 * web传递参数封装
 */
public class WebRightCondition{

    Filter filter;

    Map<RightCtx,Object> ctx;

    Page page=new Page();


    BPMCondition  condition;

    RightConditionEnums rightCondition;


    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public Map<RightCtx, Object> getCtx() {
        return ctx;
    }

    public void setCtx(Map<RightCtx, Object> ctx) {
        this.ctx = ctx;
    }

    public BPMCondition getCondition() {
        return condition;
    }

    public void setCondition(BPMCondition  condition) {
        this.condition = condition;
    }

    public RightConditionEnums getRightCondition() {
        return rightCondition;
    }

    public void setRightCondition(RightConditionEnums rightCondition) {
        this.rightCondition = rightCondition;
    }
}
