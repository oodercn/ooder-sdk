/**
 * $RCSfile: TopicEnums.java,v $
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
package net.ooder.msg.mqtt.enums;

import net.ooder.annotation.EventEnums;

public enum TopicEnums implements EventEnums {
    subscriptTopic("订阅主题", "subscriptToppic"),

    unSubscriptTopic("取消订阅", "unSubscriptTopic"),

    createTopic("创建主题", "createTopic"),

    deleteTopic("删除主题", "deleteTopic"),

    publicTopicMsg("发布订阅消息", "publicTopicMsg"),

    clearTopic("清空主题", "clearTopic");


    private String name;


    private String method;


    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    TopicEnums(String name, String method) {

        this.name = name;
        this.method = method;

    }

    @Override
    public String toString() {
        return method.toString();
    }


    public static TopicEnums fromMethod(String method) {
        for (TopicEnums type : TopicEnums.values()) {
            if (type.getMethod().equals(method)) {
                return type;
            }
        }
        return null;
    }

    public static TopicEnums fromType(String method) {
        for (TopicEnums type : TopicEnums.values()) {
            if (type.getMethod().equals(method)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String getType() {
        return method.toString();
    }

}


