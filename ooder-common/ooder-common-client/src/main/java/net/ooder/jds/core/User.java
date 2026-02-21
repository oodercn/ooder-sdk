/**
 * $RCSfile: User.java,v $
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
package net.ooder.jds.core;


import net.ooder.common.ConfigCode;

public class User implements java.io.Serializable {
    private static final long serialVersionUID = -3422770962875816826L;

    String account;
    String id;
    ConfigCode configName;
    String password;
    String newPassword;
    String name;
    String systemCode;
    String phone;
    String email;
    String sessionId;
    String imei;
    String udpIP;
    Integer udpPort;
    String code;


    public User() {

    }


    public ConfigCode getConfigName() {
        return configName;
    }

    public void setConfigName(ConfigCode configName) {
        this.configName = configName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(final String account) {
        this.account = account;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(final String phone) {
        this.phone = phone;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(final String newPassword) {
        this.newPassword = newPassword;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(final String imei) {
        this.imei = imei;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(final String systemCode) {
        this.systemCode = systemCode;
    }

    public String getUdpIP() {
        return udpIP;
    }

    public void setUdpIP(final String udpIP) {
        this.udpIP = udpIP;
    }

    public Integer getUdpPort() {
        return udpPort;
    }

    public void setUdpPort(final Integer udpPort) {
        this.udpPort = udpPort;
    }

    @Override
    public String toString() {
        return "User{" + "account='" + account + '\'' + ", id='" + id + '\'' + ", password='" + password + '\''
                + ", newPassword='" + newPassword + '\'' + ", name='" + name + '\'' + ", systemCode='" + systemCode
                + '\'' + ", phone='" + phone + '\'' + ", email='" + email + '\'' + ", sessionId='" + sessionId + '\''
                + ", imei='" + imei + '\'' + ", udpIP='" + udpIP + '\'' + ", udpPort=" + udpPort + ", code='" + code
                + '\'' + '}';
    }
}
