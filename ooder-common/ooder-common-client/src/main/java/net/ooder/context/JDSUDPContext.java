/**
 * $RCSfile: JDSUDPContext.java,v $
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
package net.ooder.context;

import net.ooder.server.JDSClientService;


public interface JDSUDPContext  extends JDSContext{
	public  Integer getPort();
	public  JDSClientService getClient();
	
}
