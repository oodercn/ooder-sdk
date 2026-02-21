/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
/**
 * $RCSfile: JDSMethodAccessor.java,v $
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


import ognl.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Map;


public class JDSMethodAccessor extends ObjectMethodAccessor {

    private static final Log _log = LogFactory.getLog(JDSMethodAccessor.class);

    public static final String DENY_METHOD_EXECUTION = "jdsesb.MethodAccessor.denyMethodExecution";
    public static final String DENY_INDEXED_ACCESS_EXECUTION = "jdsesb.IndexedPropertyAccessor.denyMethodExecution";


    public Object callMethod(Map context, Object object, String string, Object[] objects) throws MethodFailedException {

        if (objects.length == 1
                && context instanceof OgnlContext) {
            try {
                OgnlContext ogContext = (OgnlContext) context;
                if (OgnlRuntime.hasSetProperty(ogContext, object, string)) {
                    PropertyDescriptor descriptor = OgnlRuntime.getPropertyDescriptor(object.getClass(), string);
                    Class propertyType = descriptor.getPropertyType();
                    if ((Collection.class).isAssignableFrom(propertyType)) {
                        Object propVal = OgnlRuntime.getProperty(ogContext, object, string);
                        PropertyAccessor accessor = OgnlRuntime.getPropertyAccessor(Collection.class);
                        OgnlContextState.setGettingByKeyProperty(ogContext, true);
                        return accessor.getProperty(ogContext, propVal, objects[0]);
                    }
                }
            } catch (Exception oe) {
                _log.error("An unexpected exception occurred", oe);
            }

        }
        if (
                (objects.length == 2 && string.startsWith("set"))
                        ||
                        (objects.length == 1 && string.startsWith("get"))
                ) {
            Boolean exec = (Boolean) context.get(DENY_INDEXED_ACCESS_EXECUTION);
            boolean e = ((exec == null) ? false : exec.booleanValue());
            if (!e) {
                return super.callMethod(context, object, string, objects);
            }
        }
        Boolean exec = (Boolean) context.get(DENY_METHOD_EXECUTION);
        boolean e = ((exec == null) ? false : exec.booleanValue());

        if (!e) {
            return super.callMethod(context, object, string, objects);
        } else {
            return null;
        }
    }

    public Object callStaticMethod(Map context, Class aClass, String string, Object[] objects) throws MethodFailedException {
        Boolean exec = (Boolean) context.get(DENY_METHOD_EXECUTION);
        boolean e = ((exec == null) ? false : exec.booleanValue());

        if (!e) {
            return super.callStaticMethod(context, aClass, string, objects);
        } else {
            return null;
        }
    }
}
