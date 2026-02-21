package net.ooder.agent.client.iot.json;

import java.io.Serializable;

public class SensorAliasInfo implements Serializable{

	String id;
	String serialno;
	String aliasno;
	String type;
	public String getAliasno() {
		return aliasno;
	}
	public void setAliasno(String aliasno) {
		this.aliasno = aliasno;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSerialno() {
		return serialno;
	}
	public void setSerialno(String serialno) {
		this.serialno = serialno;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
