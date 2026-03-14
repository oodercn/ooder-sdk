package net.ooder.agent.client.iot;

import java.io.Serializable;

public class LockHistoryDataInfo implements Serializable {

    String datetime;

    String passId = "0";

    String status = "0";

    String msgId;

    public String getMsgId() {
	return msgId;
    }

    public void setMsgId(String msgId) {
	this.msgId = msgId;
    }

    public LockHistoryDataInfo() {

    }

    public String getDatetime() {
	return datetime;
    }

    public void setDatetime(String datetime) {
	this.datetime = datetime;
    }

    public String getStatus() {
	return status;
    }

    public void setStatus(String status) {
	this.status = status;
    }

    public String getPassId() {
	return passId;
    }

    public void setPassId(String passId) {
	this.passId = passId;
    }

}
