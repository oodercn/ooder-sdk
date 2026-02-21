/**
 * $RCSfile: RegistEventBean.java,v $
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
package net.ooder.cluster.event;

import net.ooder.esb.config.manager.ExpressionTempBean;
import net.ooder.esb.config.manager.ServiceBean;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class RegistEventBean {
    String sysCode;

    Set<ExpressionTempBean> eventService = new LinkedHashSet<ExpressionTempBean>();


    public RegistEventBean(String sysCode, Set<ExpressionTempBean> eventService) {

        this.sysCode = sysCode;
        this.eventService = eventService;

    }

    public String getSysCode() {
        return sysCode;
    }

    public void setSysCode(String sysCode) {
        this.sysCode = sysCode;
    }

    public Set<ExpressionTempBean> getEventService() {
        return eventService;
    }

    public void setEventService(Set<ExpressionTempBean> eventService) {
        this.eventService = eventService;
    }


}
