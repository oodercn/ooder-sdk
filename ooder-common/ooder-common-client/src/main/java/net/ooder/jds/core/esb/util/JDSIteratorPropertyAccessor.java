/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
/**
 * $RCSfile: JDSIteratorPropertyAccessor.java,v $
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


import java.util.Map;

import ognl.IteratorPropertyAccessor;
import ognl.ObjectPropertyAccessor;
import ognl.OgnlException;


/**
 * @author plightbo
 */
public class JDSIteratorPropertyAccessor extends IteratorPropertyAccessor {

    ObjectPropertyAccessor opa = new ObjectPropertyAccessor();


    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        opa.setProperty(context, target, name, value);
    }
}
