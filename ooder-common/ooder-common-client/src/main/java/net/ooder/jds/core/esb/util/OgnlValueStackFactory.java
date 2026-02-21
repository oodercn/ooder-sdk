/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
/**
 * $RCSfile: OgnlValueStackFactory.java,v $
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
 * Creates an Ognl value stack
 */
public class OgnlValueStackFactory extends ValueStackFactory {

    @Override
    public ValueStack createValueStack() {
        return new OgnlValueStack();
    }

    @Override
    public ValueStack createValueStack(ValueStack stack) {
        return new OgnlValueStack(stack);
    }

}
