/**
 * $RCSfile: AbstractInvocationHandler.java,v $
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
package net.ooder.esb.config.invocation;

import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.esb.util.ESBConstants;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.io.Serializable;

public abstract class AbstractInvocationHandler implements MethodInterceptor,
		Serializable {
	
	private static final long serialVersionUID = 1L;

	protected transient static final Log log = LogFactory.getLog(
			ESBConstants.CONFIG_KEY, AbstractInvocationHandler.class);

	private Enhancer enhancer = new Enhancer();

	// 返回DAO的子类
	public Object getBean(Class clz) {
		enhancer.setSuperclass(clz);
		
		enhancer.setCallback(this);
		Object object = null;
		try {
			object = enhancer.create();
		} catch (Exception e) {
			try {
				object = clz.newInstance();
			} catch (Exception e1) {
				log.error(clz + " new err");
			}
		}

		return object;
	}
	
//	 返回DAO的子类
	public Object getBean(Class clz,Class[] argumentTypes,Object[]  arguments) {
		enhancer.setSuperclass(clz);
		
		enhancer.setCallback(this);
		Object object = null;
		try {
			if (argumentTypes.length>0){
				object = enhancer.create(argumentTypes, arguments);
			}else{
				object = enhancer.create();
			}
			
		} catch (Exception e) {
			try {
				object = clz.newInstance();
			} catch (Exception e1) {
				log.error(clz + " new err");
			}
		}

		return object;
	}

	

}

