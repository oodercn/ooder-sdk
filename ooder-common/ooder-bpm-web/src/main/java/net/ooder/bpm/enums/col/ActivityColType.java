package net.ooder.bpm.enums.col;

import net.ooder.annotation.Enumstype;
import net.ooder.bpm.enums.activityinst.ActivityInstReceiveMethod;
import net.ooder.bpm.enums.activityinst.ActivityInstRunStatus;
import net.ooder.bpm.enums.activityinst.ActivityInstStatus;

import net.ooder.annotation.ui.ComboInputType;

import java.util.Date;

public enum ActivityColType implements Enumstype {
    processDefName("流程名称", String.class, ComboInputType.input, "6em", ""),
    activityDefName("所在步骤", String.class, ComboInputType.input, "6em", ""),
    state("状态", ActivityInstStatus.class, ComboInputType.input, "6em", ""),
    title("标题", String.class, ComboInputType.input, "6em", ""),
    runStatus("运行状态", ActivityInstRunStatus.class, ComboInputType.input, "6em", ""),
    arrivedTime("到达时间", Date.class, ComboInputType.input, "6em", ""),
    alertTime("预警时间", Date.class, ComboInputType.input, "6em", ""),
    endTime("结束时间", Date.class, ComboInputType.input, "6em", ""),
    startTime("开始时间", Date.class, ComboInputType.input, "6em", ""),
    processStartTime("流程启动时间", Date.class, ComboInputType.input, "6em", ""),
    personName("办理人", String.class, ComboInputType.input, "6em", ""),
    startPersonName("发起人", String.class, ComboInputType.input, "6em", ""),
    receiveMethod("receiveMethod", ActivityInstReceiveMethod.class, ComboInputType.input, "6em", "");

    private final String name;
    private final String capition;

    private final String headerStyle;

    private final ComboInputType inputType;

    private final Class clazz;

    private final String width;


    ActivityColType(String name, Class clazz, ComboInputType inputType, String width, String headerStyle) {
        this.name = name;
        this.capition = name;
        this.inputType = inputType;
        this.headerStyle = headerStyle;
        this.clazz = clazz;
        this.width = width;

    }

    public String getCapition() {
        return capition;
    }

    public String getHeaderStyle() {
        return headerStyle;
    }

    public ComboInputType getInputType() {
        return inputType;
    }

    public Class getClazz() {
        return clazz;
    }

    public String getWidth() {
        return width;
    }

    @Override
    public String toString() {
        return name();
    }

    @Override
    public String getType() {
        return name();
    }

    @Override
    public String getName() {
        return name;
    }
}
