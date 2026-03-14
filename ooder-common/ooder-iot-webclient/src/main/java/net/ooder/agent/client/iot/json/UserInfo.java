package net.ooder.agent.client.iot.json;

import  net.ooder.context.JDSActionContext;
import  net.ooder.jds.core.User;

import java.io.Serializable;

public class UserInfo implements Serializable {

    String id;
    String account;
    String name;
    String phonenum;

    String email;
    String keyword;
    String systemCode;
    String sessionId;

    String udpIP;
    Integer udpPort;

    public String getUdpIP() {
        return udpIP;
    }

    public void setUdpIP(String udpIP) {
        this.udpIP = udpIP;
    }

    public Integer getUdpPort() {
        return udpPort;
    }

    public void setUdpPort(Integer udpPort) {
        this.udpPort = udpPort;
    }

    public UserInfo() {
    }

    public UserInfo(User user) {
        udpIP = user.getUdpIP();
        udpPort = user.getUdpPort();
        this.id = user.getId();
        this.account = user.getAccount().toLowerCase();
        this.email = user.getEmail();
        this.keyword = "";
        this.phonenum = user.getPhone();
        this.systemCode = JDSActionContext.getActionContext().getSystemCode();
        this.sessionId = JDSActionContext.getActionContext().getSessionId();
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }
}
