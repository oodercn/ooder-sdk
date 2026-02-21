/**
 * $RCSfile: ClientVersion.java,v $
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
package net.ooder.cluster;

import java.io.IOException;

import net.ooder.common.property.Properties;
import net.ooder.common.util.ClassUtility;

public class ClientVersion {
	
	/**
     * 读取服务器地址
     * */
    public static String getClientVersion()
    {
     String clientVersion = "";
  	  Properties props = new Properties();
  		try { 
  			props.load(ClassUtility.loadResource("update.properties"));
  			clientVersion = props.getProperty("version");
  		} catch (IOException e) {
  			e.printStackTrace();
  		}
  		return clientVersion;
    }
	

}
