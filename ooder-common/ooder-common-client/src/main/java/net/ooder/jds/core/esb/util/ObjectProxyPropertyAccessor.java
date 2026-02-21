/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

/**
 * $RCSfile: ObjectProxyPropertyAccessor.java,v $
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

import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;

/**
 * Is able to access (set/get) properties on a given object.
 * <p/>
 * Uses Ognl internal.
 *
 * @author Gabe
 */
public class ObjectProxyPropertyAccessor implements PropertyAccessor {
    public Object getProperty(Map context, Object target, Object name) throws OgnlException {
        ObjectProxy proxy = (ObjectProxy) target;
        setupContext(context, proxy);

        return OgnlRuntime.getPropertyAccessor(proxy.getValue().getClass()).getProperty(context, target, name);

    }

    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        ObjectProxy proxy = (ObjectProxy) target;
        setupContext(context, proxy);

        OgnlRuntime.getPropertyAccessor(proxy.getValue().getClass()).setProperty(context, target, name, value);
    }

    /**
     * Sets up the context with the last property and last class
     * accessed.
     *
     * @param context
     * @param proxy
     */
    private void setupContext(Map context, ObjectProxy proxy) {
        OgnlContextState.setLastBeanClassAccessed(context, proxy.getLastClassAccessed());
        OgnlContextState.setLastBeanPropertyAccessed(context, proxy.getLastPropertyAccessed());
    }

	public String getSourceAccessor(OgnlContext arg0, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSourceSetter(OgnlContext arg0, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		return null;
	}
}
