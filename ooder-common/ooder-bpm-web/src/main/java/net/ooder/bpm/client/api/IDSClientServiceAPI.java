package net.ooder.bpm.client.api;

import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.bpm.client.*;
import net.ooder.bpm.client.service.IDSClientService;
import net.ooder.common.ContextType;
import net.ooder.common.ReturnType;
import net.ooder.common.TokenType;
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
@RequestMapping("/api/ids/IDSClientService/")
@MethodChinaName(cname = "流程服务")
@EsbBeanAnnotation(dataType = ContextType.Server,tokenType = TokenType.user)
public class IDSClientServiceAPI implements IDSClientService {
    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getActivityInst")
    @MethodChinaName(cname = "获取活动实例")
    public @ResponseBody
    ResultModel<ActivityInst> getActivityInst(String activityInstID) {
        return getService().getActivityInst(activityInstID);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getProcessDef")
    @MethodChinaName(cname = "获取流程定义")
    public @ResponseBody
    ResultModel<ProcessDef> getProcessDef(String processDefID) {
        return getService().getProcessDef(processDefID);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getActivityDef")
    @MethodChinaName(cname = "获取活动定义")
    public @ResponseBody
    ResultModel<ActivityDef> getActivityDef(String activityDefID) {
        return getService().getActivityDef(activityDefID);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getRouteDef")
    @MethodChinaName(cname = "获取路由定义")
    public @ResponseBody
    ResultModel<RouteDef> getRouteDef(String routeDefId) {
        return getService().getRouteDef(routeDefId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "newProcess")
    @MethodChinaName(cname = "创建流程")
    public @ResponseBody
    ResultModel<ActivityInst> newProcess(String processDefId, String processInstName) {
        return getService().newProcess(processDefId, processInstName);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "updateProcessInstName")
    @MethodChinaName(cname = "更新流程名称")
    public @ResponseBody
    ResultModel<ReturnType> updateProcessInstName(String processInstId, String name) {
        return getService().updateProcessInstName(processInstId, name);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getProcessDefVersion")
    @MethodChinaName(cname = "获取流程版本")
    public @ResponseBody
    ResultModel<ProcessDefVersion> getProcessDefVersion(String processDefVersionID) {
        return getService().getProcessDefVersion(processDefVersionID);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getProcessInst")
    @MethodChinaName(cname = "获取流程实例")
    public @ResponseBody
    ResultModel<ProcessInst> getProcessInst(String processInstID) {
        return getService().getProcessInst(processInstID);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "updateProcessInstUrgency")
    @MethodChinaName(cname = "更新紧急程度")
    public @ResponseBody
    ResultModel<ReturnType> updateProcessInstUrgency(String processInstId, String urgency) {
        return getService().updateProcessInstUrgency(processInstId, urgency);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "mrouteto")
    @MethodChinaName(cname = "并行发送")
    public @ResponseBody
    ResultModel<ReturnType> mrouteto(@RequestBody RouteToBean routeToBean) {
        return getService().mrouteto(routeToBean);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "routeTo")
    @MethodChinaName(cname = "发送")
    public @ResponseBody
    ResultModel<ReturnType> routeto(@RequestBody RouteBean routeBeans) {
        return getService().routeto(routeBeans);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "copyActivityInstByHistory")
    @MethodChinaName(cname = "重新发送")
    public @ResponseBody
    ResultModel<ActivityInst> copyActivityInstByHistory(String activityHistoryInstId, Boolean isnew) {
        return getService().copyActivityInstByHistory(activityHistoryInstId, isnew);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "newActivityInstByActivityDefId")
    @MethodChinaName(cname = "从指定节点发起流程")
    public @ResponseBody
    ResultModel<ActivityInst> newActivityInstByActivityDefId(String processInstId, String activityDefId) {
        return getService().newActivityInstByActivityDefId(processInstId, activityDefId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "copyTo")
    @MethodChinaName(cname = "抄送")
    public @ResponseBody
    ResultModel<ReturnType> copyTo(String activityHistoryInstId, String[] readers) {
        return getService().copyTo(activityHistoryInstId, readers);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "canTakeBack")
    @MethodChinaName(cname = "是否有权限收回")
    public @ResponseBody
    ResultModel<Boolean> canTakeBack(String activityInstID) {
        return getService().canTakeBack(activityInstID);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "takeBack")
    @MethodChinaName(cname = "收回")
    public @ResponseBody
    ResultModel<ReturnType> takeBack(String activityInstID) {
        return getService().takeBack(activityInstID);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "canEndRead")
    @MethodChinaName(cname = "是否有权限阅毕")
    public @ResponseBody
    ResultModel<Boolean> canEndRead(String activityInstID) {
        return getService().canEndRead(activityInstID);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "endRead")
    @MethodChinaName(cname = "阅毕")
    public @ResponseBody
    ResultModel<ReturnType> endRead(String activityInstID) {
        return getService().endRead(activityInstID);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "endTask")
    @MethodChinaName(cname = "结束任务")
    public @ResponseBody
    ResultModel<ReturnType> endTask(String activityInstID) {
        return getService().endTask(activityInstID);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "abortedTask")
    @MethodChinaName(cname = "终止任务")
    public ResultModel<ReturnType> abortedTask(String activityInstID) {
        return getService().abortedTask(activityInstID);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "routeBack")
    @MethodChinaName(cname = "退回")
    public @ResponseBody
    ResultModel<ReturnType> routeBack(String fromActivityInstID, String toActivityInstHistoryID) {
        return getService().routeBack(fromActivityInstID, toActivityInstHistoryID);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "canPerform")
    @MethodChinaName(cname = "是否可退回")
    public @ResponseBody
    ResultModel<Boolean> canPerform(String activityInstID) {
        return getService().canPerform(activityInstID);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "canSignReceive")
    @MethodChinaName(cname = "是否可签收")
    public @ResponseBody
    ResultModel<Boolean> canSignReceive(String activityInstID) {
        return getService().canSignReceive(activityInstID);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "signReceive")
    @MethodChinaName(cname = "签收")
    public @ResponseBody
    ResultModel<ReturnType> signReceive(String activityInstID) {
        return getService().signReceive(activityInstID);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "suspendActivityInst")
    @MethodChinaName(cname = "暂停")
    public @ResponseBody
    ResultModel<ReturnType> suspendActivityInst(String activityInstID) {
        return getService().suspendActivityInst(activityInstID);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "resumeActivityInst")
    @MethodChinaName(cname = "恢复")
    public @ResponseBody
    ResultModel<ReturnType> resumeActivityInst(String activityInstID) {
        return getService().resumeActivityInst(activityInstID);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "suspendProcessInst")
    @MethodChinaName(cname = "暂停流程")
    public @ResponseBody
    ResultModel<ReturnType> suspendProcessInst(String processInstID) {
        return getService().suspendProcessInst(processInstID);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "resumeProcessInst")
    @MethodChinaName(cname = "恢复流程")
    public @ResponseBody
    ResultModel<ReturnType> resumeProcessInst(String processInstID) {
        return getService().resumeProcessInst(processInstID);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "abortProcessInst")
    @MethodChinaName(cname = "终止流程")
    public @ResponseBody
    ResultModel<ReturnType> abortProcessInst(String processInstID) {
        return getService().abortProcessInst(processInstID);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "completeProcessInst")
    @MethodChinaName(cname = "完成流程")
    public @ResponseBody
    ResultModel<ReturnType> completeProcessInst(String processInstID) {
        return getService().completeProcessInst(processInstID);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "deleteProcessInst")
    @MethodChinaName(cname = "强制删除流程")
    public @ResponseBody
    ResultModel<ReturnType> deleteProcessInst(String processInstID) {
        return getService().deleteProcessInst(processInstID);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "display")
    @MethodChinaName(cname = "开始工作")
    public @ResponseBody
    ResultModel<ReturnType> display(String activityInstId) {
        return getService().display(activityInstId);
    }



    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getActivityInstHistory")
    @MethodChinaName(cname = "获取历史办理记录")
    public @ResponseBody
    ResultModel<ActivityInstHistory> getActivityInstHistory(String activityInstHistoryID) {
        return getService().getActivityInstHistory(activityInstHistoryID);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getRouteInst")
    @MethodChinaName(cname = "获取路由列表")
    public @ResponseBody
    ResultModel<RouteInst> getRouteInst(String routeInstId) {
        return getService().getRouteInst(routeInstId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "canRouteBack")
    @MethodChinaName(cname = "是否可退回")
    public @ResponseBody
    ResultModel<Boolean> canRouteBack(String activityInstID) {
        return getService().canRouteBack(activityInstID);
    }

    IDSClientService getService() {
        return (IDSClientService) EsbUtil.parExpression("$IDSClientService");
    }
}
