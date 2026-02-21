/**
 * $RCSfile: GetHttpRequest.java,v $
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
package net.ooder.service;

import net.ooder.common.expression.ParseException;
import net.ooder.common.expression.function.AbstractFunction;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.context.JDSActionContext;


public class GetHttpRequest extends AbstractFunction {
	private static final Log log = LogFactory.getLog("ESB",
			GetHttpRequest.class);
    public  Object perform() throws ParseException
    {
     	
     	 return JDSActionContext.getActionContext().getHttpRequest();
    }
}