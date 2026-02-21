/**
 * $RCSfile: EsbInvocation.java,v $
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
package net.ooder.esb.event;

import java.io.Serializable;
import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public interface EsbInvocation extends MethodInterceptor,
		Serializable {

	
	public Object getBean(Class clz);

	public Object getBean(Class clz, Class[] argumentTypes, Object[] arguments);

	public Object intercept(Object o, Method method, Object[] args,
							MethodProxy proxy) throws Throwable;


}

