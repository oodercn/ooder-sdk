package net.ooder.bpm.enums.activitydef;


import net.ooder.annotation.AttributeName;
import net.ooder.annotation.Enumstype;
import net.ooder.common.CommonYesNoEnum;

public enum ActivityDefEnums implements AttributeName {

    CanSpecialSend("CanSpecialSend", "是否允许特送", CommonYesNoEnum.class),

    SpecialScope("SpecialScope", "特送范围", ActivityDefSpecialSendScope.class),

    Execution("Execution", "是否同步", ActivityDefExecution.class),

    Join("Type", "等待合并", ActivityDefJoin.class),

    Split("Type", "并行处理", ActivityDefSplit.class),

    CanRouteBack("CanRouteBack", "是否允许退回", CommonYesNoEnum.class),

    DeadLineOperation("DeadLineOperation", "到期处理办法", ActivityDefDeadLineOperation.class),

    RouteBackMethod("RouteBackMethod", "退回路径", ActivityDefRouteBackMethod.class);

    private String name;
    private Class<? extends Enumstype> clazz;
    private String displayName;

    ActivityDefEnums(String name, String displayName, Class<? extends Enumstype> clazz) {

	this.name = name;
	this.displayName = displayName;
	this.clazz = clazz;

    }

    public String getName() {
	return name;
    }


    public void setName(String name) {
	this.name = name;
    }

    public Class<? extends Enumstype> getClazz() {
	return clazz;
    }

    public void setClazz(Class<? extends Enumstype> clazz) {
	this.clazz = clazz;
    }

    public String getDisplayName() {
	return displayName;
    }

    public void setDisplayName(String displayName) {
	this.displayName = displayName;
    }

    @Override
    public String getType() {
	return name;
    }

}
