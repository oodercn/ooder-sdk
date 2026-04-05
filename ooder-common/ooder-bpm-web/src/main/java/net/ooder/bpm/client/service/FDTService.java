package net.ooder.bpm.client.service;

import net.ooder.bpm.client.ProcessDefForm;

import net.ooder.bpm.client.data.FormClassBean;
import net.ooder.config.ResultModel;

import java.util.List;

public interface FDTService {


    public ResultModel<FormClassBean> getActivityMainFormDef(String activityDefId);

    public ResultModel<List<FormClassBean>> getAllActivityDataFormDef(String activityDefId);

    public ResultModel<ProcessDefForm> getProcessDefForm(String processDefVersionId);

}
