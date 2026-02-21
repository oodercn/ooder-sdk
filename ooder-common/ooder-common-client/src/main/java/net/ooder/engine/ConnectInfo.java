
/**
 * $RCSfile: ConnectInfo.java,v $
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

import net.ooder.common.md5.MD5;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 客户端连接信息，这些信息在应用调用<code>WorkflowClientService.connect(ConnectInfo conInfo)</code>
 * 方法时传入，并缓存在WorkflowClientService实例对象中。
 * </p>
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 * 
 * @author wenzhang li
 * @version 6.0
 */
public class ConnectInfo implements Serializable {
    // The identification of the workflow participant
    // on whose behalf the Workflow Application will
    // be operating. The value specified may represent
    // a human, a device, etc. This identification is
    // normally used for security checking, accounting, etc.
    private String userID;

    private String loginName;

    private String password;

    // public ConnectInfo() {}
    public ConnectInfo(String userID, String loginName, String password) {
	this.userID = userID;

	this.loginName = loginName;
	this.password = password;
    }

    public String getLoginName() {
	return loginName;
    }

    public String getPassword() {
	return password;
    }

    public String getUserID() {
	return userID;
    }

    @Override
    public String toString() {
	return userID + "[" + loginName + "]" ;
    }

    @Override
    public boolean equals(Object obj) {

	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (!(obj instanceof ConnectInfo))
	    return false;
	ConnectInfo conObj = (ConnectInfo) obj;
	if (userID != null && !userID.equals(conObj.getUserID()))
	    return false;
	if (userID == null && conObj.getUserID() != null)
	    return false;
	if (loginName != null && !loginName.equals(conObj.getLoginName()))
	    return false;
	if (loginName == null && conObj.getLoginName() != null)
	    return false;
	if (password != null && !password.equals(conObj.getPassword()))
	    return false;
	if (password == null && conObj.getPassword() != null)
	    return false;

	return true;
    }

    public int hashCode() {
	return loginName == null ? 1 : loginName.hashCode();
    }
}
