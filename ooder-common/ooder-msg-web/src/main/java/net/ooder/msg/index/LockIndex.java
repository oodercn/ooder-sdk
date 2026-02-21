/**
 * $RCSfile: LockIndex.java,v $
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

@JDocumentType(name = "LockIndex", vfsJson = @VFSJsonType(fileName = "lock.json", vfsPath = "iot/lock/json/") , fsDirectory = @FSDirectoryType(id = "fsLockIndex", vfsPath = "index") , indexWriter = @JIndexWriterType(id = "iwLockIndex") )
public class LockIndex implements JLuceneIndex {
    @JFieldType(store = Store.YES)
    String sn;

    @JFieldType(store = Store.YES)
    String commandId;

    @JFieldType(store = Store.YES)
    String event;

    @JFieldType(store = Store.YES)
    String passId;

    @JFieldType(store = Store.YES)
    Long eventtime;

    @JFieldType(store = Store.YES)
    String userId;

    String path;
    private String uuid;

    public LockIndex(String path) {
	this.path = path;
    }

    public String getPassId() {
	return passId;
    }

    public void setPassId(String passId) {
	this.passId = passId;
    }

    public String getSn() {
	return sn;
    }

    public void setSn(String sn) {
	this.sn = sn;
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

    public String getCommandId() {
	return commandId;
    }

    public void setCommandId(String commandId) {
	this.commandId = commandId;
    }

    public String getEvent() {
	return event;
    }

    public void setEvent(String event) {
	this.event = event;
    }
}


