
/**
 * $RCSfile: JDSSessionHandle.java,v $
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
package net.ooder.engine;

import java.io.Serializable;

/**
 * <p>
 * Title: JDS系统
 * </p>
 * <p>
 * Description: 带有Session信息的处理类
 * </p>
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 * 
 * @author wenzhang li
 * @version 4.0
 */
public class JDSSessionHandle implements Serializable {

    private String sessionID = "";

    private String ip = "";

    private Integer port = 0;

    public String getIp() {
	return ip;
    }

    public Integer getPort() {
	return port;
    }

    public JDSSessionHandle() {

    }

    //
    // public JDSSessionHandle(String ip,Integer port) {
    // this.ip=ip;
    // this.port=port;
    // this.sessionID=UUID.randomUUID().toString();
    // }
    //
    public JDSSessionHandle(String sessionID) {
	this.sessionID = sessionID;
    }

    public String getSessionID() {
	return sessionID;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null)
	    return false;
	if (!(obj instanceof JDSSessionHandle))
	    return false;

        return ((JDSSessionHandle) obj).getSessionID().equals(this.getSessionID());
	//return obj.toString().equals(this.toString());

    }

    @Override
    public String toString() {
//	if (ip != null && port != null) {
//	    return ip + ":" + port + "[" + sessionID + "]";
//	} else {
//	    return sessionID;
//	}
        return sessionID;
    }

    public int hashCode() {
	return sessionID == null ? 1 : sessionID.hashCode();
    }

    public void setIp(String ip) {
	this.ip = ip;
    }

    public void setPort(Integer port) {
	this.port = port;
    }

    public void setSessionID(String sessionID) {
	this.sessionID = sessionID;
    }
}
