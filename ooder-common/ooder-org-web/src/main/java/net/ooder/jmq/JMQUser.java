package net.ooder.jmq;

import java.io.Serializable;

public class JMQUser implements Serializable {
    String id;
    String account;
    String name;
    String phonenum;
    String email;
    String systemCode;
    String sessionId;

    JMQConfig jmqConfig = new JMQConfig();


    public JMQUser() {
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

    public String getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }

    public JMQConfig getJmqConfig() {
        return jmqConfig;
    }

    public void setJmqConfig(JMQConfig jmqConfig) {
        this.jmqConfig = jmqConfig;
    }
}
