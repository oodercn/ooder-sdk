/**
 * $RCSfile: RuntimLogEnum.java,v $
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
package net.ooder.cluster.service;

import net.ooder.common.ConditionKey;

public enum RuntimLogEnum implements ConditionKey {

    value("value"),
    url("url"),
    body("body"),
    session("sessionId"),
    time("time");

    private final String searchey;

    RuntimLogEnum(String searchey) {
        this.searchey = searchey;
    }

    @Override
    public String getValue() {
        return searchey;
    }
}
