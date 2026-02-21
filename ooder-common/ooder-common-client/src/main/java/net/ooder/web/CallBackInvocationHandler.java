/**
 * $RCSfile: CallBackInvocationHandler.java,v $
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
package net.ooder.web;

import java.lang.reflect.Method;

import net.ooder.common.JDSConstants;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.sf.cglib.proxy.InvocationHandler;

public class CallBackInvocationHandler implements InvocationHandler {

    private Class clazz;

    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, CallBackInvocationHandler.class);

    public CallBackInvocationHandler(Class clazz)  {

	this.clazz = clazz;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

	return method.invoke(proxy, args);

    }
}
