package net.ooder.bpm.client.ct;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.bpm.client.ProcessDefForm;
import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.bpm.enums.form.LockEnum;
import net.ooder.bpm.enums.form.MarkEnum;
import net.ooder.common.CommonYesNoEnum;
import net.ooder.jds.core.esb.EsbUtil;


import java.util.ArrayList;
import java.util.List;

public class CtProcessDefForm implements ProcessDefForm, java.io.Serializable {


    private CommonYesNoEnum autoSave = null;

    private CommonYesNoEnum noSqlType = null;

    private String processDefVersioId = null;

    private MarkEnum mark = MarkEnum.ProcessInst;

    private LockEnum lock = LockEnum.Last;

    private List<String> tableNames;

    private List<String> moduleNames;


    public CtProcessDefForm(ProcessDefForm processDefForm) {
        this.processDefVersioId = processDefForm.getProcessDefVersionId();
        this.autoSave = processDefForm.getAutoSave();
        this.noSqlType = processDefForm.getNoSqlType();
        this.mark = processDefForm.getMark();
        this.lock = processDefForm.getLock();
        this.tableNames = processDefForm.getTableNames() == null ? new ArrayList<>() : processDefForm.getTableNames();
        this.moduleNames = processDefForm.getModuleNames() == null ? new ArrayList<>() : processDefForm.getModuleNames();
        ;

    }


    @Override
    public CommonYesNoEnum getAutoSave() {
        return autoSave;
    }

    @Override
    public CommonYesNoEnum getNoSqlType() {
        return noSqlType;
    }


    @Override
    @JSONField(serialize = false)
    public List<String> getModuleNames() {
        return moduleNames;
    }


    @Override
    public MarkEnum getMark() {
        return mark;
    }

    @Override
    public LockEnum getLock() {
        return lock;
    }

    @Override
    public String getProcessDefVersionId() {
        return processDefVersioId;
    }

    @Override
    public List<String> getTableNames() {
        return tableNames;
    }


    @JSONField(serialize = false)
    /**
     * @return
     */
    public WorkflowClientService getBPMClient() {

        WorkflowClientService client = ((WorkflowClientService) EsbUtil.parExpression("$BPMC"));

        return client;
    }
}
