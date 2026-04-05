package net.ooder.bpm.enums.formula;

import net.ooder.command.BindClusterIdParamEnums;
import net.ooder.command.OperationCommandTypeEnum;
import net.ooder.annotation.Enums;
import net.ooder.agent.client.iot.enums.DeviceDataTypeKey;

public enum CommandParams {

    sourcedev("sourcedev", "源设备", DeviceDataTypeKey.class),

    destdev("sourcedev", "目标设备", DeviceDataTypeKey.class),

    clusterid("clusterid", "操作选项", BindClusterIdParamEnums.class),

    sensorieees("sensorieees", "指定设备", BindClusterIdParamEnums.class),

    oldsensorieee("oldsensorieee", "原设备", BindClusterIdParamEnums.class),

    newsensorieee("newsensorieee", "新设备", BindClusterIdParamEnums.class),

    commandType("commandType", "", OperationCommandTypeEnum.class),

    operation("operation", "设备类型", OperationCommandTypeEnum.class),

    passId("passId", "密码组", OperationCommandTypeEnum.class),

    passType("passType", "密码类型", OperationCommandTypeEnum.class),

    startTime("startTime", "生效时间", OperationCommandTypeEnum.class),

    endTime("endTime", "失效时间", OperationCommandTypeEnum.class),

    passVal1("passVal1", "密码值", OperationCommandTypeEnum.class),

    passVal2("passVal2", "密码值", OperationCommandTypeEnum.class);

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

    CommandParams(String type, String name) {
	this.type = type;
	this.name = name;
    }

    CommandParams(String type, String name, Class<? extends Enums> dicClass) {
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

    public static CommandParams fromType(String typeName) {
	for (CommandParams type : CommandParams.values())
        if (type.getType().equals(typeName)) {
            return type;
        }
	return null;
    }

}
