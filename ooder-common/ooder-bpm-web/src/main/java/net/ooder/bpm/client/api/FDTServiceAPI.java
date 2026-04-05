package net.ooder.bpm.client.api;

import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.bpm.client.ProcessDefForm;
import net.ooder.bpm.client.data.FormClassBean;
import net.ooder.bpm.client.service.FDTService;
import net.ooder.common.ContextType;
import net.ooder.common.TokenType;
import net.ooder.config.ResultModel;
import net.ooder.annotation.MethodChinaName;

import net.ooder.jds.core.esb.EsbUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Controller
@RequestMapping("/api/ids/FDTService/")
@MethodChinaName(cname = "表单服务")
@EsbBeanAnnotation(dataType = ContextType.Server,tokenType = TokenType.user)
public class FDTServiceAPI implements FDTService {


    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getActivityMainFormDef")
    @MethodChinaName(cname = "获取所有活动表单信息")
    public @ResponseBody
    ResultModel<FormClassBean> getActivityMainFormDef(String activityDefId) {
        return getService().getActivityMainFormDef(activityDefId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getAllActivityDataFormDef")
    @MethodChinaName(cname = "获取所有活动表单信息")
    public @ResponseBody
    ResultModel<List<FormClassBean>> getAllActivityDataFormDef(String activityDefId) {
        return getService().getAllActivityDataFormDef(activityDefId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getProcessDefForm")
    @MethodChinaName(cname = "获取所有流程表单信息")
    public @ResponseBody
    ResultModel<ProcessDefForm> getProcessDefForm(String processDefVersionId) {
        return getService().getProcessDefForm(processDefVersionId);
    }

    FDTService getService() {
        return (FDTService) EsbUtil.parExpression("$FDTService");
    }


}
