/**
 * $RCSfile: GetAppClient.java,v $
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
package net.ooder.service.app;

import net.ooder.app.AppManager;
import net.ooder.common.expression.ParseException;
import net.ooder.common.expression.function.AbstractFunction;
import net.ooder.server.JDSClientService;
import net.ooder.server.OrgManagerFactory;



public class GetAppClient extends AbstractFunction {
	
	public AppManager perform(JDSClientService client) throws ParseException
    {
		
        return OrgManagerFactory.getInstance().getAppManager(client.getConfigCode());
    }    
}