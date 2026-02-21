/**
 * $RCSfile: RuntimeLog.java,v $
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
package net.ooder.web;

import net.ooder.common.MsgStatus;
import net.ooder.common.cache.CacheSizes;
import net.ooder.common.cache.Cacheable;

public class RuntimeLog implements Comparable<RuntimeLog>, Cacheable {

    String serviceKey;
    String url;
    MsgStatus status = MsgStatus.NORMAL;
    String token;
    String sessionId;
    long startTime;
    long arrivedTime;
    long endTime;
    long exetime;
    long time;
    String requestJson;
    String bodyJson;
    String params;

    RuntimeLog() {

    }

    RuntimeLog(String token, String serviceKey, String url, String sessionId) {

        this.serviceKey = serviceKey;
        this.url = url;
        this.sessionId = sessionId;
        this.token = token;

    }

    public long getExetime() {
        return exetime;
    }

    public void setExetime(long exetime) {
        this.exetime = exetime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getArrivedTime() {
        return arrivedTime;
    }

    public void setArrivedTime(long arrivedTime) {
        this.arrivedTime = arrivedTime;
    }

    public MsgStatus getStatus() {
        return status;
    }

    public void setStatus(MsgStatus status) {
        this.status = status;
    }


    public String getServiceKey() {
        return serviceKey;
    }

    public void setServiceKey(String serviceKey) {
        this.serviceKey = serviceKey;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }


    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getRequestJson() {
        return requestJson;
    }

    public void setRequestJson(String requestJson) {
        this.requestJson = requestJson;
    }

    public String getBodyJson() {
        return bodyJson;
    }

    public void setBodyJson(String bodyJson) {
        this.bodyJson = bodyJson;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    @Override
    public int compareTo(RuntimeLog o) {
        return o.getStartTime() > this.getStartTime() ? -1 : 0;
    }

    @Override
    public int getCachedSize() {
        int size = 20;
        size += CacheSizes.sizeOfString(serviceKey);
        size += CacheSizes.sizeOfString(url);
        size += CacheSizes.sizeOfString(token);
        size += CacheSizes.sizeOfString(sessionId);
        size += CacheSizes.sizeOfString(requestJson);
        size += CacheSizes.sizeOfString(bodyJson);
        size += CacheSizes.sizeOfString(params);
        return size;
    }
}
