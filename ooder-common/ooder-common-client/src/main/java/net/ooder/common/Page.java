/**
 * $RCSfile: Page.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.common;

import net.ooder.context.JDSActionContext;
import net.ooder.web.BaseParamsEnums;

public class Page {
    Integer pageIndex = 1;
    Integer pageSize = 20;


    public Page(int pageIndex, int pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }


    public Page() {
        Object npageIndex = JDSActionContext.getActionContext().getParams(BaseParamsEnums.pageIndex.toString());
        Object npageSize = JDSActionContext.getActionContext().getParams(BaseParamsEnums.pageSize.toString());

        if (npageIndex == null) {
            this.pageIndex = 1;
        } else {
            this.pageIndex = Integer.valueOf(npageIndex.toString());
        }
        if (npageSize == null) {
            this.pageSize = 20;
        } else {
            this.pageSize = Integer.valueOf(npageSize.toString());

        }
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
