package net.ooder.bpm.enums.formula;

import net.ooder.annotation.Enumstype;
import net.ooder.command.CmdRule;
import net.ooder.annotation.Enums;

import net.ooder.common.CommandEventEnums;
import net.ooder.agent.client.enums.CommandEnums;
import net.ooder.agent.client.iot.enums.DeviceDataTypeKey;
import net.ooder.agent.client.iot.enums.DeviceEventEnums;
import net.ooder.agent.client.iot.enums.DeviceZoneStatus;
import net.ooder.agent.client.iot.enums.IRVauleEnums;

public enum FormulaParamsDIC implements Enumstype {


    DEVICEEVENT("DEVICEEVENT", "设备事件", DeviceEventEnums.class),

    DEVICEDATAEVENT("DEVICEDATAEVENT", "数据上报", DeviceEventEnums.class),

    DeviceDataTypeKey("DeviceDataTypeKey", "数据上报KEY", DeviceDataTypeKey.class),

    IRValue("IRValue", "空调控制", IRVauleEnums.class),

    COMMAND("COMMAND", "设备命令", CommandEnums.class),

    COMMANDEVENT("COMMANDEVENT", "命令执行结果", CommandEventEnums.class),

    UNKNOW("UNKNOW", "未知类型", CmdRule.class),

    ZONESTATUS("ZONESTATUS", "报警值", DeviceZoneStatus.class);

    private String type;

    private String name;

    private Class<? extends Enums> dicClass;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    FormulaParamsDIC(String type, String name, Class<? extends Enums> dicClass) {
        this.type = type;
        this.name = name;
        this.dicClass = dicClass;

    }

    public Class<? extends Enums> getDicClass() {
        return dicClass;
    }

    public void setDicClass(Class<? extends Enums> dicClass) {
        this.dicClass = dicClass;
    }

    @Override
    public String toString() {
        return type;
    }

    public static FormulaParamsDIC fromType(String typeName) {
        if (typeName == null) {
            return UNKNOW;
        }
        for (FormulaParamsDIC type : FormulaParamsDIC.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return UNKNOW;
    }

}
