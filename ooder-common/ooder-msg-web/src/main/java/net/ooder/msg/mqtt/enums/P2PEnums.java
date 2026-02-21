/**
 * $RCSfile: P2PEnums.java,v $
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

public enum P2PEnums implements EventEnums {

    send2Person("指定用户", "send2Person"),

    send2Client("指定客户端消息", "send2Session"),

    send2PersonMsg("指定用户消息", "createTopic");


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

    P2PEnums(String name, String method) {

        this.name = name;
        this.method = method;

    }

    @Override
    public String toString() {
        return method.toString();
    }


    public static P2PEnums fromMethod(String method) {
        for (P2PEnums type : P2PEnums.values()) {
            if (type.getMethod().equals(method)) {
                return type;
            }
        }
        return null;
    }

    public static P2PEnums fromType(String method) {
        for (P2PEnums type : P2PEnums.values()) {
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


