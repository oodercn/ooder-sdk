/**
 * $RCSfile: TopicMsgIndex.java,v $
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

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.annotation.JLuceneIndex;
import net.ooder.common.MsgStatus;
import net.ooder.index.config.type.*;
import net.ooder.msg.TopicMsg;
import net.ooder.org.conf.OrgConstants;
import org.apache.lucene.document.Field.Store;

@JDocumentType(name = "TopicMsgIndex", fsDirectory = @FSDirectoryType(id = "fsLogIndex"), vfsJson = @VFSJsonType(vfsPath = "mqtt/topic/", fileName = "topicMsgIndexData.js"), indexWriter = @JIndexWriterType(id = "iwSensorIndex"))
public class TopicMsgIndex implements JLuceneIndex, TopicMsg {
    @JFieldType(store = Store.YES)
    String sn;
    @JFieldType(store = Store.NO)
    String userId;
    @JFieldType(store = Store.YES)
    String gwSN;
    @JFieldType(store = Store.NO)
    String body;
    @JFieldType(store = Store.NO)
    String event;
    @JFieldType(store = Store.YES)
    Integer lineNum;

    @JFieldType(store = Store.YES)
    Long eventtime;

    @JFieldType(store = Store.YES)
    String path;


    @JFieldType(store = Store.YES)
    String topic;
    @JFieldType(store = Store.YES)
    Integer qos;
    @JFieldType(store = Store.YES)
    Boolean retained;


    @JFieldType(store = Store.YES)
    private String systemCode = OrgConstants.CLUSTERCONFIG_KEY.getType();
    @JFieldType(store = Store.YES)
    private String id;
    @JFieldType(store = Store.YES)
    private String type;
    @JFieldType(store = Store.YES)
    private String receiver;
    @JFieldType(store = Store.YES)
    private String title;
    @JFieldType(store = Store.NO)
    private Integer times = 1;
    @JFieldType(store = Store.YES)
    private String from;

    @JFieldType(store = Store.YES)
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Long arrivedTime;

    @JFieldType(store = Store.YES)
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Long eventTime;

    @JFieldType(store = Store.YES)
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Long receiveTime;


    private MsgStatus status = MsgStatus.NORMAL;

    String uuid;

    public TopicMsgIndex(TopicMsg msg) {

        this.event = msg.getTitle();
        this.userId = msg.getReceiver();
        this.body = msg.getBody();
        this.qos = msg.getQos();
        this.topic = msg.getTopic();
        this.receiveTime = msg.getReceiveTime();
        this.retained = msg.getRetained();

        this.arrivedTime = msg.getArrivedTime();
        this.from = msg.getFrom();
        this.eventTime = msg.getEventTime();
        this.id = msg.getId();
        this.status = msg.getStatus();
        this.title = msg.getTitle();
        this.type = msg.getType();
        this.path = msg.getTopic() == null ? msg.getReceiver() : msg.getTopic();

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


    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getQos() {
        return qos;
    }

    public void setQos(Integer qos) {
        this.qos = qos;
    }

    public Boolean getRetained() {
        return retained;
    }

    public void setRetained(Boolean retained) {
        this.retained = retained;
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

        this.uuid = uuid;
    }

    @Override
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String getPath() {
        return path;
    }


    @Override
    public String getBody() {
        return body;
    }

    @Override
    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String getSystemCode() {
        return systemCode;
    }

    @Override
    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getReceiver() {
        return receiver;
    }

    @Override
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    @Override
    public Long getReceiveTime() {
        return receiveTime;
    }

    @Override
    public void setReceiveTime(Long receiveTime) {
        this.receiveTime = receiveTime;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public Integer getTimes() {
        return times;
    }

    @Override
    public void setTimes(Integer times) {
        this.times = times;
    }

    @Override
    public String getFrom() {
        return from;
    }

    @Override
    public void setFrom(String from) {
        this.from = from;
    }

    @Override
    public Long getArrivedTime() {
        return arrivedTime;
    }

    public void setArrivedTime(Long arrivedTime) {
        this.arrivedTime = arrivedTime;
    }

    @Override
    public Long getEventTime() {
        return eventTime;
    }

    @Override
    public void setEventTime(Long eventTime) {
        this.eventTime = eventTime;
    }

    @Override
    public MsgStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(MsgStatus status) {
        this.status = status;
    }
}


