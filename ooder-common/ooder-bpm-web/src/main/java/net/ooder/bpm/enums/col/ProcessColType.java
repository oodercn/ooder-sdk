package net.ooder.bpm.enums.col;

import net.ooder.annotation.Enumstype;
import net.ooder.bpm.enums.process.ProcessInstStatus;

import net.ooder.annotation.ui.ComboInputType;

import java.util.Date;

public enum ProcessColType implements Enumstype {
    processDefName("流程名称", String.class, ComboInputType.input, "6em", ""),
    activityDefName("所在步骤", String.class, ComboInputType.input, "6em", ""),
    state("状态", ProcessInstStatus.class, ComboInputType.input, "6em", ""),
    title("标题", String.class, ComboInputType.input, "6em", ""),
    runStatus("运行状态", ProcessInstStatus.class, ComboInputType.input, "6em", ""),
    processStartTime("流程启动时间", Date.class, ComboInputType.input, "6em", ""),
    endTime("结束时间", Date.class, ComboInputType.input, "6em", ""),
    startPersonName("发起人", String.class, ComboInputType.input, "6em", "");

    private final String name;
    private final String capition;

    private final String headerStyle;

    private final ComboInputType inputType;

    private final Class clazz;

    private final String width;


    ProcessColType(String name, Class clazz, ComboInputType inputType, String width, String headerStyle) {
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
