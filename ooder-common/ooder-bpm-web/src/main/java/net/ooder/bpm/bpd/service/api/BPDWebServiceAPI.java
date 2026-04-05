package net.ooder.bpm.bpd.service.api;

import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.bpm.bpd.service.BPDWebService;
import net.ooder.common.ContextType;
import net.ooder.common.TokenType;
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
@RequestMapping("/api/bpd/bpdwebservice/")
@MethodChinaName(cname = "流程定义WEB服务")
@EsbBeanAnnotation(dataType = ContextType.Server,tokenType = TokenType.user)
public class BPDWebServiceAPI implements BPDWebService {

    public BPDWebService getService() {
        BPDWebService service = (BPDWebService) EsbUtil.parExpression("$BPDWebService");
        return service;
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "SaveProcessDefListToDB")
    @MethodChinaName(cname = "保存流程")
    public @ResponseBody
    ResultModel<Boolean> saveProcessDefListToDB(String xpdlString, String personId) {
        return getService().saveProcessDefListToDB(xpdlString, personId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "DeleteProcessDefListToDB")
    @MethodChinaName(cname = "删除流程")
    public @ResponseBody
    ResultModel<Boolean> deleteProcessDefListToDB(String versionIdsString, String personId) {
        return getService().deleteProcessDefListToDB(versionIdsString, personId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "GetProcessDefListFromDB")
    @MethodChinaName(cname = "装载流程")
    public @ResponseBody
    ResultModel<String> getProcessDefListFromDB(String versionIdsString, String personId) {
        return getService().getProcessDefListFromDB(versionIdsString, personId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "ActivateProcessDefVersion")
    @MethodChinaName(cname = "激活流程")
    public @ResponseBody
    ResultModel<Boolean> activateProcessDefVersion(String versionId, String personId) {
        return getService().activateProcessDefVersion(versionId, personId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "FreezeProcessDefVersion")
    @MethodChinaName(cname = "冻结流程")
    public @ResponseBody
    ResultModel<Boolean> freezeProcessDefVersion(String processDefVersionId, String personId) {
        return getService().freezeProcessDefVersion(processDefVersionId, personId);
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
