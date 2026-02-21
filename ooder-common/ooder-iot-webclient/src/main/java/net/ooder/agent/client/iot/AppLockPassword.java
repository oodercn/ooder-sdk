package net.ooder.agent.client.iot;

import  net.ooder.common.CommandEventEnums;

public class AppLockPassword implements java.io.Serializable {
    private static final long serialVersionUID = -6271394759641455143L;

    String msgId;
    String ieee;
    String gwserialno;
    String seed;
    String interval;
    String phone;
    Integer passwordType;
    String modeId;
    Integer passId;
    String password;
    String oldpassword;
    Long startTime;
    Long endTime;
    CommandEventEnums status;

    public String getOldpassword() {
        return oldpassword;
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }


    public void setOldpassword(String oldpassword) {
        this.oldpassword = oldpassword;
    }


    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(final Long endTime) {
        this.endTime = endTime;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public Integer getPasswordType() {
        return passwordType;
    }

    public void setPasswordType(final Integer passwordType) {
        this.passwordType = passwordType;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(final Long startTime) {
        this.startTime = startTime;
    }

    public CommandEventEnums getStatus() {
        return status;
    }

    public void setStatus(final CommandEventEnums status) {
        this.status = status;
    }

    public String getModeId() {
        return modeId;
    }

    public void setModeId(final String modeId) {
        this.modeId = modeId;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(final String msgId) {
        this.msgId = msgId;
    }

    public String getGwserialno() {
        return gwserialno;
    }

    public void setGwserialno(final String gwserialno) {
        this.gwserialno = gwserialno;
    }

    public Integer getPassId() {
        return passId;
    }

    public void setPassId(final Integer passId) {
        this.passId = passId;
    }

    public String getIeee() {
        return ieee;
    }

    public void setIeee(final String ieee) {
        this.ieee = ieee;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "AppLockPassword{" + "msgId='" + msgId + '\'' + ", ieee='"
                + ieee + '\'' + ", gwserialno='" + gwserialno + '\'' + ", passwordType=" + passwordType
                + ", modeId='" + modeId + '\'' + ", passId=" + passId + ", password='" + password + '\''
                + ", startTime=" + startTime + ", endTime=" + endTime + ", status=" + status + '}';
    }
}
