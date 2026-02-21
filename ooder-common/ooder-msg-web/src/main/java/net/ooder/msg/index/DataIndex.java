/**
 * $RCSfile: DataIndex.java,v $
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
package net.ooder.msg.index;

import net.ooder.annotation.JLuceneIndex;
import net.ooder.index.config.type.*;
import org.apache.lucene.document.Field.Store;

@JDocumentType(name = "DataIndex", vfsJson = @VFSJsonType(vfsPath = "iot/json/") , fsDirectory = @FSDirectoryType(id = "fsDataIndex") , indexWriter = @JIndexWriterType(id = "iwDataIndex") )
public class DataIndex implements JLuceneIndex {
    @JFieldType(store = Store.YES)
    String sn;
    @JFieldType(store = Store.YES)
    String msgId;
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
    @JFieldType(store = Store.NO)
    String userId;

    String path;
    String uuid;

    public DataIndex() {

    }

    public DataIndex(String path) {
	this.path = path;
    }

    public String getSn() {
	return sn;
    }

    public void setSn(String sn) {
	this.sn = sn;
    }

    public String getGwSN() {
	return gwSN;
    }

    public void setGwSN(String gwSN) {
	this.gwSN = gwSN;
    }

    public String getMsgId() {
	return msgId;
    }

    public void setMsgId(String msgId) {
	this.msgId = msgId;
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


