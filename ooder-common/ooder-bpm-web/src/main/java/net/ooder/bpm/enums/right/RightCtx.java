package net.ooder.bpm.enums.right;

import net.ooder.annotation.CTXContext;

public enum RightCtx implements CTXContext {

    CONTEXT_ACTIVITYINSTHISTORY("CONTEXT_ACTIVITYINSTHISTORY", "活动实例历史"),

    CONTEXT_CTXLIST("CONTEXT_CTXLIST", "CONTEXT_CTXLIST"),

    CONTEXT_CTX("CONTEXT_CTX", "CONTEXT_CTX"),

    CONTEXT_WAITPERFORMER("CONTEXT_WAITPERFORMER", "WAITPERFORMER"),

    CONTEXT_WAITREADER("CONTEXT_WAITREADER", "WAITREADER"),

    CONTEXT_READER("CONTEXT_READER", "READER"),

    CONTEXT_PERFORMER("CONTEXT_CONTEXT_PERFORMER", "PERFORMER"),

    CURRENT_ACTIVITYINST("CURRENT_ACTIVITYINST", "CURRENT_ACTIVITYINST"),

    CURRENT_PROCESSINST("CURRENT_PROCESSINST", "CURRENT_PROCESSINST"),

    EVENT("EVENT", "EVENT"),

    EVENTS("EVENTS", "EVENTS"),

    CURRENT_EVENT_TYPE("CURRENT_EVENT_TYPE", "CURRENT_EVENT_TYPE"),

    Http_RequestParams("Http_RequestParams", "RequestParams"),

    Http_RequestType("Http_RequestType", "Http_RequestType"),

    Http_Method("Http_Method", "Http_Method"),

    Http_ResponseType("Http_ResponseType", "Http_ResponseType"),

    Http_ResponseBody("Http_ResponseBody", "Http_ResponseBody"),

    SERVICE_URL("SERVICE_URL", "SERVICE_URL"),

    sensorieee("sensorieee", "sensorieee"),

    CURRENT_DEVICEIEEE("CURRENT_DEVICEIEEE", "CURRENT_DEVICEIEEE"),

    ENDPOINTID("ENDPOINTID", "ENDPOINTID"),

    ENDPOINTS("ENDPOINTS", "ENDPOINTS"),

    COMMANDS("COMMANDS", "COMMANDS"),

    SUSPENDORCOMBINE("SUSPENDORCOMBINE", "分裂或者合并"),

    COMBINABLEACTIVITYINSTS("COMBINABLEACTIVITYINSTS", "等待合并活动实列"),

    MODULE("MODULE", "当前模块"),

    COMBINE("COMBINE", "合并"),

    USERID("USERID", "当前用户ID"),

    USERS("USERS", "当前办理用户"),

    CONTEXT("CONTEXT", "CONTEXT"),

    PERFORMERS("PERFORMERS", "PERFORMERS"),

    ORGS("ORGS", "ORGS"),

    READERS("READERS", "READERS"),

    PERMISSION("PERMISSION", "PERMISSION"),

    PERFOMERTYPE("PERFOMERTYPE", "PERFOMERTYPE"),

    INSTANCE_CONDITION("INSTANCE_CONDITION", "CONDITION"),

    ACTIVITYINST_ID("ACTIVITYINST_ID", "ACTIVITYINST_ID"),

    PROCESSINST_ID("PROCESSINST_ID", "PROCESSINST_ID"),

    ACTIVITYINSTHISTORY_ID("ACTIVITYINSTHISTORY_ID", "ACTIVITYINSTHISTORY_ID"),

    PERFORMSEQUENCE("PERFORMSEQUENCE", "PERFORMSEQUENCE");


    private String type;

    private String name;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    RightCtx(String type, String name) {
        this.type = type;
        this.name = name;

    }

    @Override
    public String toString() {
        return type;
    }

    public static RightCtx fromType(String typeName) {
        for (RightCtx type : RightCtx.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return null;
    }

}
