package net.ooder.bpm.client.api;

import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.bpm.client.ActivityDefRight;
import net.ooder.bpm.client.service.RightService;
import net.ooder.bpm.enums.activityinst.ActivityInstRightAtt;
import net.ooder.bpm.enums.activityinsthistory.ActivityInstHistoryAtt;
import net.ooder.bpm.enums.right.RightGroupEnums;
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
@RequestMapping("/api/ids/RightService/")
@MethodChinaName(cname = "流程权限服务")
@EsbBeanAnnotation(dataType = ContextType.Server,tokenType = TokenType.user)
public class RightServiceAPI implements RightService {
//    @Override
//    @RequestMapping(method = RequestMethod.POST, value = "getParticipantSelect")
//    @MethodChinaName(cname = "获取权限表达式参数")
//    public @ResponseBody
//    ResultModel<ParticipantSelect> getParticipantSelect(String selectedId) {
//        return getService().getParticipantSelect(selectedId);
//    }
//
//    @Override
//    @RequestMapping(method = RequestMethod.POST, value = "getFormulas")
//    @MethodChinaName(cname = "获取权限表达式")
//    public @ResponseBody  ListResultModel<List<ParticipantSelect>> getFormulas(String type) {
//        return getService().getFormulas(type);
//    }
//
//    @Override
//    @RequestMapping(method = RequestMethod.POST, value = "getFormulaParameters")
//    @MethodChinaName(cname = "获取权限表达式参数")
//    public @ResponseBody ListResultModel<List<ExpressionParameter>> getFormulaParameters() {
//        return getService().getFormulaParameters();
//    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getActivityDefRight")
    @MethodChinaName(cname = "获取活动权限定义")
    public @ResponseBody
    ResultModel<ActivityDefRight> getActivityDefRight(String activityDefId) {
        return getService().getActivityDefRight(activityDefId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getActivityInstRightAttribute")
    @MethodChinaName(cname = "获取活动实例权限")
    public @ResponseBody
    ResultModel<List<String>> getActivityInstRightAttribute(String activityInstId, ActivityInstRightAtt group) {
        return getService().getActivityInstRightAttribute(activityInstId, group);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getActivityInstHistoryRightAttribute")
    @MethodChinaName(cname = "获取历史实例权限")
    public @ResponseBody
    ResultModel<List<String>> getActivityInstHistoryRightAttribute(String activityInstHistoryId, ActivityInstHistoryAtt group) {
        return getService().getActivityInstHistoryRightAttribute(activityInstHistoryId, group);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "queryAllPermissionToActivityInst")
    @MethodChinaName(cname = "查询所有权限")
    public @ResponseBody
    ResultModel<List<RightGroupEnums>> queryAllPermissionToActivityInst(String activityInstHistoryId) {
        return getService().queryAllPermissionToActivityInst(activityInstHistoryId);
    }

    RightService getService() {
        return (RightService) EsbUtil.parExpression("$RightService");
    }
}
