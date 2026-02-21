package net.ooder.agent.client.home.listener;

import  net.ooder.annotation.JLuceneIndex;
import  net.ooder.index.config.type.FSDirectoryType;
import  net.ooder.index.config.type.JDocumentType;
import  net.ooder.index.config.type.JFieldType;
import  net.ooder.index.config.type.JIndexWriterType;
import org.apache.lucene.document.Field.Store;

@JDocumentType(name = "lockInfo", fsDirectory = @FSDirectoryType(id = "fslockInfo") , indexWriter = @JIndexWriterType(id = "iwlockInfo") )
public class LockIndex implements JLuceneIndex {

    @JFieldType(store = Store.YES)
    String sn;
    @JFieldType(store = Store.NO)
    String userid;
    @JFieldType(store = Store.YES)
    String gwSN;
    @JFieldType(store = Store.NO)
    String valuetype;
    @JFieldType(store = Store.NO)
    String value;
    @JFieldType(store = Store.NO)
    String event;
    @JFieldType(store = Store.YES)
    Integer lineNum;
    
    @JFieldType(store = Store.YES)
    Long eventtime;

    String userId;

    String uuid;

    String path;
    


    public String getSn() {
	return sn;
    }

    public void setSn(String sn) {
	this.sn = sn;
    }

    public String getUserid() {
	return userid;
    }

    public void setUserid(String userid) {
	this.userid = userid;
    }

    public String getGwSN() {
	return gwSN;
    }

    public void setGwSN(String gwSN) {
	this.gwSN = gwSN;
    }
    

    public Integer getLineNum() {
        return lineNum;
    }

    public void setLineNum(Integer lineNum) {
        this.lineNum = lineNum;
    }

    public Long getEventtime() {
        return eventtime;
    }

    public void setEventtime(Long eventtime) {
        this.eventtime = eventtime;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public String getValuetype() {
	return valuetype;
    }

    public void setValuetype(String valuetype) {
	this.valuetype = valuetype;
    }

    public String getValue() {
	return value;
    }

    public void setValue(String value) {
	this.value = value;
    }

    public String getEvent() {
	return event;
    }

    public void setEvent(String event) {
	this.event = event;
    }


    @Override
    public String getUserId() {
	return userId;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(String uuid) {
this.uuid=uuid;
    }

    @Override
    public void setUserId(String userId) {
	this.userId = userId;
    }

    @Override
    public String getPath() {
	return path;
    }

}
