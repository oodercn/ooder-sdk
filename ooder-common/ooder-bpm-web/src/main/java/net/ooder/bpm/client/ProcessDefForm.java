package net.ooder.bpm.client;

import net.ooder.bpm.enums.form.LockEnum;
import net.ooder.bpm.enums.form.MarkEnum;
import net.ooder.common.CommonYesNoEnum;

import java.util.List;

public interface ProcessDefForm extends java.io.Serializable {

    public MarkEnum getMark();

    public LockEnum getLock();

    public String getProcessDefVersionId();

    public CommonYesNoEnum getAutoSave();

    public CommonYesNoEnum getNoSqlType();

    public List<String> getTableNames();

    public List<String> getModuleNames();


}
