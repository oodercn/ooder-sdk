package net.ooder.server.udp;

import net.ooder.engine.JDSSessionHandle;

import java.util.ArrayList;
import java.util.List;

/**
 * UDP 用户类
 * 封装UDP连接用户的信息和会话句柄，用于管理UDP连接状态
 * 
 * @author ooder team
 * @version 2.0
 * @since 2025-08-25
 */
public class UDPUser implements java.io.Serializable {
    String account;

    List<JDSSessionHandle> handles = new ArrayList<JDSSessionHandle>();

    public UDPUser(String account) {
	this.account = account;
    }

    public String getAccount() {
	return account;
    }

    public List<JDSSessionHandle> getHandles() {
	return handles;
    }

    public void setHandles(List<JDSSessionHandle> handles) {
	this.handles = handles;
    }

    public void setAccount(String account) {
	this.account = account;
    }

}
