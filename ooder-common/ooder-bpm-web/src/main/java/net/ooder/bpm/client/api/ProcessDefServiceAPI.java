package net.ooder.bpm.client.api;

import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.bpm.client.AttributeDef;
import net.ooder.bpm.client.Listener;
import net.ooder.bpm.client.ProcessDef;
import net.ooder.bpm.client.ProcessDefVersion;
import net.ooder.bpm.client.service.ProcessDefService;
import net.ooder.bpm.engine.query.BPMCondition;
import net.ooder.common.ContextType;
import net.ooder.common.TokenType;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;
import net.ooder.annotation.MethodChinaName;

import net.ooder.jds.core.esb.EsbUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/api/ids/ProcessDefService/")
@MethodChinaName(cname = "流程定义服务")
@EsbBeanAnnotation(dataType = ContextType.Server,tokenType = TokenType.user)
public class ProcessDefServiceAPI implements ProcessDefService {
    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getProcessDefVersionIdList")
    @MethodChinaName(cname = "获取流程定义版本")
    public @ResponseBody
    ListResultModel<List<String>> getProcessDefVersionIdList(@RequestBody BPMCondition condition) {
        return getService().getProcessDefVersionIdList(condition);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getProcessDefIdList")
    @MethodChinaName(cname = "获取流程定义")
    public @ResponseBody
    ListResultModel<List<String>> getProcessDefIdList(@RequestBody BPMCondition condition) {
        return getService().getProcessDefIdList(condition);
    }


    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getProcessDefVersionList")
    @MethodChinaName(cname = "装载流程版本")
    public @ResponseBody
    ListResultModel<List<ProcessDefVersion>> getProcessDefVersionList(@RequestBody String[] processDefVersionIds) {
        return getService().getProcessDefVersionList(processDefVersionIds);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getProcessDefList")
    @MethodChinaName(cname = "装载流程")
    public @ResponseBody
    ListResultModel<List<ProcessDef>> getProcessDefList(@RequestBody String[] processDefIds) {
        return getService().getProcessDefList(processDefIds);
    }


    @Override
    @RequestMapping(method = RequestMethod.POST, value = "loadProcessDefArrtibutes")
    @MethodChinaName(cname = "装载流程定义属性")
    public @ResponseBody
    ListResultModel<List<AttributeDef>> loadProcessDefArrtibutes(String processDefVersionId) {
        return getService().loadProcessDefArrtibutes(processDefVersionId);
    }


    @Override
    @RequestMapping(method = RequestMethod.POST, value = "loadActivityDefArrtibutes")
    @MethodChinaName(cname = "装载活动定义属性")
    public @ResponseBody
    ListResultModel<List<AttributeDef>> loadActivityDefArrtibutes(String activityDefId) {
        return getService().loadActivityDefArrtibutes(activityDefId);
    }


    @Override
    @RequestMapping(method = RequestMethod.POST, value = "loadRouteDefArrtibutes")
    @MethodChinaName(cname = "装载活动路由定义属性")
    public @ResponseBody
    ListResultModel<List<AttributeDef>> loadRouteDefArrtibutes(String routeDefId) {
        return getService().loadRouteDefArrtibutes(routeDefId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getActivityListeners")
    @MethodChinaName(cname = "装载活动监听器")
    public @ResponseBody
    ListResultModel<List<Listener>> getActivityListeners(String activityDefId) {
        return getService().getActivityListeners(activityDefId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getFirstActivityDefInProcess")
    @MethodChinaName(cname = "获取流程第一个节点")
    public @ResponseBody
    ResultModel<String> getFirstActivityDefInProcess(String processDefVersionId) {
        return getService().getFirstActivityDefInProcess(processDefVersionId);
    }


    ProcessDefService getService() {
        return (ProcessDefService) EsbUtil.parExpression("$ProcessDefService");
    }

}
