package net.ooder.bpm.client.api;

import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.annotation.MethodChinaName;
import net.ooder.bpm.client.ActivityInst;
import net.ooder.bpm.client.AttributeInst;
import net.ooder.bpm.client.ProcessInst;
import net.ooder.bpm.client.service.ProcessInstService;
import net.ooder.bpm.engine.query.WebRightCondition;
import net.ooder.common.ContextType;
import net.ooder.common.TokenType;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;
import net.ooder.jds.core.esb.EsbUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/api/ids/ProcessInstService/")
@MethodChinaName(cname = "流程实例接口")
@EsbBeanAnnotation(dataType = ContextType.Server, tokenType = TokenType.user)
public class ProcessInstServiceAPI implements ProcessInstService {
    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getProcessInstList")
    @MethodChinaName(cname = "获取流程实例")
    public @ResponseBody
    ListResultModel<List<String>> getProcessInstList(@RequestBody WebRightCondition condition) {
        return getService().getProcessInstList(condition);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "loadProcessInstLists")
    @MethodChinaName(cname = "装载流程实例")
    public @ResponseBody
    ListResultModel<List<ProcessInst>> loadProcessInstLists(@RequestBody String[] processInstIds) {
        return getService().loadProcessInstLists(processInstIds);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getActivityInstList")
    @MethodChinaName(cname = "获取活动实例")
    public @ResponseBody
    ListResultModel<List<String>> getActivityInstList(@RequestBody WebRightCondition condition) {
        return getService().getActivityInstList(condition);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "loadActivityInstList")
    @MethodChinaName(cname = "装载活动实例")
    public @ResponseBody
    ListResultModel<List<ActivityInst>> loadActivityInstList(@RequestBody String[] activityInstIds) {
        return getService().loadActivityInstList(activityInstIds);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getActivityInstListByOutActvityInstHistory")
    @MethodChinaName(cname = "获取可执行的路由")
    public @ResponseBody
    ListResultModel<List<String>> getActivityInstListByOutActvityInstHistory(@RequestBody String activityInstHistoryId) {
        return getService().getActivityInstListByOutActvityInstHistory(activityInstHistoryId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getNextRoutes")
    @MethodChinaName(cname = "获取下一个路由")
    public @ResponseBody
    ListResultModel<List<String>> getNextRoutes(String startActivityInstID) {
        return getService().getNextRoutes(startActivityInstID);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "loadProcessInstArrtibutes")
    @MethodChinaName(cname = "装载流程实例属性")
    public @ResponseBody
    ListResultModel<List<AttributeInst>> loadProcessInstArrtibutes(String processInstId) {
        return getService().loadProcessInstArrtibutes(processInstId);
    }


    @Override
    @RequestMapping(method = RequestMethod.POST, value = "loadActivityInstArrtibutes")
    @MethodChinaName(cname = "装载活动实例属性")
    public @ResponseBody
    ListResultModel<List<AttributeInst>> loadActivityInstArrtibutes(String activityInstId) {
        return getService().loadActivityInstArrtibutes(activityInstId);
    }


    @Override
    @RequestMapping(method = RequestMethod.POST, value = "setActivityInstAttribute")
    @MethodChinaName(cname = "设置活动实例属性")
    public @ResponseBody
    ResultModel<Boolean> setActivityInstAttribute(String activityInstId, String name, String value) {
        return getService().setActivityInstAttribute(activityInstId, name, value);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "setProcessInstAttribute")
    @MethodChinaName(cname = "设置流程实例属性")
    public @ResponseBody
    ResultModel<Boolean> setProcessInstAttribute(String processInstId, String name, String value) {
        return getService().setProcessInstAttribute(processInstId, name, value);
    }

    ProcessInstService getService() {
        return (ProcessInstService) EsbUtil.parExpression("$ProcessInstService");
    }
}
