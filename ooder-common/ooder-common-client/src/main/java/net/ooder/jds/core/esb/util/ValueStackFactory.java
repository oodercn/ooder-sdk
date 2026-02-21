/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
/**
 * $RCSfile: ValueStackFactory.java,v $
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
 * Factory that creates a value stack, defaulting to the OgnlValueStackFactory
 */
public abstract class ValueStackFactory {
    private static ValueStackFactory factory = new OgnlValueStackFactory();
    
    public static void setFactory(ValueStackFactory factory) {
        ValueStackFactory.factory = factory;
    }
    
    public static ValueStackFactory getFactory() {
        return factory;
    }

    public abstract ValueStack createValueStack();
    
    public abstract ValueStack createValueStack(ValueStack stack);
    
}
