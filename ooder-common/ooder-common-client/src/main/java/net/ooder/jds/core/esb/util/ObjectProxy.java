/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

/**
 * $RCSfile: ObjectProxy.java,v $
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
package net.ooder.jds.core.esb.util;

/**
 * An Object to use within OGNL to proxy other Objects
 * usually Collections that you set in a different place
 * on the ValueStack but want to retain the context information
 * about where they previously were.
 *
 * @author Gabe
 */
public class ObjectProxy {
    private Object value;
    private Class lastClassAccessed;
    private String lastPropertyAccessed;

    public Class getLastClassAccessed() {
        return lastClassAccessed;
    }

    public void setLastClassAccessed(Class lastClassAccessed) {
        this.lastClassAccessed = lastClassAccessed;
    }

    public String getLastPropertyAccessed() {
        return lastPropertyAccessed;
    }

    public void setLastPropertyAccessed(String lastPropertyAccessed) {
        this.lastPropertyAccessed = lastPropertyAccessed;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
