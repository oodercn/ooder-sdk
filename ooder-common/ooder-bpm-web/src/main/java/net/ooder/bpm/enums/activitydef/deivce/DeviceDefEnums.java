package net.ooder.bpm.enums.activitydef.deivce;


import net.ooder.annotation.AttributeName;
import net.ooder.annotation.Enumstype;
import net.ooder.common.CommonYesNoEnum;

public enum DeviceDefEnums implements AttributeName {

    CanSpecialSend(ActivityDefDeviceAtt.CANSPECIALSEND, CommonYesNoEnum.class),
    //
    SpecialScope(ActivityDefDeviceAtt.SPECIALSENDSCOPE, ActivityDefDeviceSpecial.class),
    //
    // Execution("Execution", "是否同步", ActivityDefExecution.class),

    IntervalTime(ActivityDefDeviceAtt.INTERVALTIME, null),


    //
    DeadLineOperation(ActivityDefDeviceAtt.COMMANDEXECTYPE, ActivityDefDeviceDeadLine.class),

    SelectDelayTime(ActivityDefDeviceAtt.COMMANDDELAYTIME, null),

    ActivityDefDevciePerformSequence(ActivityDefDeviceAtt.PERFORMSEQUENCE, ActivityDefDevicePerformSequence.class),

    ActivityDefDevicePerformtype(ActivityDefDeviceAtt.PERFORMTYPE, net.ooder.bpm.enums.activitydef.deivce.ActivityDefDevicePerformtype.class);

    private String name;
    private Class<? extends Enumstype> clazz;
    private String displayName;

    DeviceDefEnums(ActivityDefDeviceAtt att, Class<? extends Enumstype> clazz) {

	this.name = att.getType();
	this.displayName = att.getName();
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
