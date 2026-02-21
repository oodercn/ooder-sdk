/**
 * $RCSfile: MsgType.java,v $
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
package net.ooder.msg;

import net.ooder.annotation.Enumstype;

public enum MsgType implements Enumstype {

    MSG("MSG", "消息", Msg.class),

    LOG("LOG", "日志", LogMsg.class),

    EVENT("EVENT", "日志", LogMsg.class),

    ALARM("ALARM", "报警", AlarmMsg.class),

    SENSOR("SENSOR", "子设备", SensorMsg.class),

    TOPIC("TOPIC", "广播", TopicMsg.class),

    COMMAND("COMMAND", "命令", CommandMsg.class),

    ERRORREPORT("ERRORREPORT", "命令", AlarmMsg.class),

    PASSWORD("PASSWORD", "密码", PasswordCommandMsg.class),

    PYPASSWORD("PYPASSWORD", "动态密码", PasswordCommandMsg.class);

    private String type;

    private String name;

    private Class clazz;

    public String getType() {
        return type;
    }

    public Class getClazz() {
        return clazz;
    }


    public String getName() {
        return name;
    }

    MsgType(String type, String name, Class clazz) {
        this.type = type;
        this.name = name;
        this.clazz = clazz;

    }

    @Override
    public String toString() {
        return type;
    }

    public static MsgType fromType(String typeName) {
        for (MsgType type : MsgType.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return LOG;
    }

    public static MsgType fromClass(Class clazz) {
        for (MsgType type : MsgType.values()) {
            if (type.getClazz().equals(clazz)) {
                return type;
            }
        }
        return LOG;
    }

}


