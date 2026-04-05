package net.ooder.bpm.bpd.service.api;

import net.ooder.bpm.bpd.service.BPDService;
import net.ooder.bpm.client.ProcessDef;
import net.ooder.config.BPDProjectConfig;
import net.ooder.config.CApplication;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;
import net.ooder.annotation.MethodChinaName;

import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.org.Person;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/api/bpd/bpdservice/")
@MethodChinaName(cname = "流程定义管理接口")
public class BPDServiceAPI implements BPDService {

    public BPDService getService() {
        BPDService service = EsbUtil.parExpression(BPDService.class);

        return service;
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "GetAppLication")
    @MethodChinaName(cname = "获取所有应用")
    public @ResponseBody
    ResultModel<List<CApplication>> getAppLications() {
        return getService().getAppLications();
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "GetTempProcessDefList")
    @MethodChinaName(cname = "获取所有流程模板")
    public @ResponseBody
    ListResultModel<List<ProcessDef>> getTempProcessDefList() {
        return getService().getTempProcessDefList();
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "GetProcessClassifications")
    @MethodChinaName(cname = "获取所有流程类型")
    public @ResponseBody
    ListResultModel<List<BPDProjectConfig>> getProcessClassifications() {
        return getService().getProcessClassifications();
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "GetProcessDefList")
    @MethodChinaName(cname = "获取所有流程定义")
    public @ResponseBody
    ListResultModel<List<ProcessDef>> getProcessDefList(String projectName) {
        return getService().getProcessDefList(projectName);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "SaveProcessDefListToDB")
    @MethodChinaName(cname = "保存流程")
    public @ResponseBody
    ResultModel<Boolean> saveProcessDefListToDB(String xpdlString) {
        return getService().saveProcessDefListToDB(xpdlString);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "DeleteProcessDefListToDB")
    @MethodChinaName(cname = "删除流程")
    public @ResponseBody
    ResultModel<Boolean> deleteProcessDefListToDB(String versionIdsString) {
        return getService().deleteProcessDefListToDB(versionIdsString);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "GetProcessDefListFromDB")
    @MethodChinaName(cname = "读取流程")
    public @ResponseBody
    ResultModel<String> getProcessDefListFromDB(String versionIdsString) {
        return getService().getProcessDefListFromDB(versionIdsString);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "ActivateProcessDefVersion")
    @MethodChinaName(cname = "激活流程")
    public @ResponseBody
    ResultModel<Boolean> activateProcessDefVersion(String versionId) {
        return getService().activateProcessDefVersion(versionId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "FreezeProcessDefVersion")
    @MethodChinaName(cname = "冻结流程")
    public @ResponseBody
    ResultModel<Boolean> freezeProcessDefVersion(String processDefVersionId) {
        return getService().freezeProcessDefVersion(processDefVersionId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "SaveCommission")
    @MethodChinaName(cname = "保存权限")
    public @ResponseBody
    ResultModel<Boolean> saveCommission(String processId, String group, String personIds) {
        return getService().saveCommission(processId, group, personIds);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "GetCommissions")
    @MethodChinaName(cname = "获取权限")
    public @ResponseBody
    ResultModel<List<Person>> getCommissions(String processId, String group) {
        return getService().getCommissions(processId, group);
    }

}
