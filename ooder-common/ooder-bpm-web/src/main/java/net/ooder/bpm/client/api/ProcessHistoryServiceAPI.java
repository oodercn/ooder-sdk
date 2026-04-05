package net.ooder.bpm.client.api;

import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.bpm.client.ActivityInstHistory;
import net.ooder.bpm.client.AttributeInst;
import net.ooder.bpm.client.service.ProcessHistoryService;
import net.ooder.bpm.engine.query.WebRightCondition;
import net.ooder.bpm.enums.activityinsthistory.ActivityInstHistoryAtt;
import net.ooder.common.ContextType;
import net.ooder.common.ReturnType;
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
@RequestMapping("/api/ids/ProcessHistoryService/")
@MethodChinaName(cname = "流程历史信息服务")
@EsbBeanAnnotation(dataType = ContextType.Server,tokenType = TokenType.user)
public class ProcessHistoryServiceAPI implements ProcessHistoryService {
    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getActivityInstListByOutActvityInstHistory")
    @MethodChinaName(cname = "获取指定节点输出历史信息")
    public @ResponseBody
    ListResultModel<List<String>> getActivityInstListByOutActvityInstHistory(String activityInstHistoryId) {
        return getService().getActivityInstListByOutActvityInstHistory(activityInstHistoryId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getActivityInstHistoryListByActvityInst")
    @MethodChinaName(cname = "获取指定节点历史信息")
    public @ResponseBody ListResultModel<List<String>> getActivityInstHistoryListByActvityInst(String actvityInstId) {
        return getService().getActivityInstHistoryListByActvityInst(actvityInstId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getActivityInstHistoryList")
    @MethodChinaName(cname = "获取活动历史")
    public @ResponseBody ListResultModel<List<String>> getActivityInstHistoryList(@RequestBody WebRightCondition condition) {
        return getService().getActivityInstHistoryList(condition);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getLastActivityInstHistoryListByActvityInst")
    @MethodChinaName(cname = "最后一步历史")
    public @ResponseBody ListResultModel<List<String>> getLastActivityInstHistoryListByActvityInst(String actvityInstId)  {
        return getService().getLastActivityInstHistoryListByActvityInst(actvityInstId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getAllOutActivityInstHistoryByActvityInstHistory")
    @MethodChinaName(cname = "从此节点发出历史")
    public @ResponseBody ListResultModel<List<String>> getAllOutActivityInstHistoryByActvityInstHistory(String historyHisroryId, Boolean noSplit) {
        return getService().getAllOutActivityInstHistoryByActvityInstHistory(historyHisroryId,noSplit);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getActivityInstHistoryListByProcessInst")
    @MethodChinaName(cname = "指定流程历史")
    public @ResponseBody ListResultModel<List<String>> getActivityInstHistoryListByProcessInst(String processInstId) {
        return getService().getActivityInstHistoryListByProcessInst(processInstId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "deleteHistory")
    @MethodChinaName(cname = "删除历史")
    public @ResponseBody ResultModel<ReturnType> deleteHistory(String activityInstHistoryID) {
        return getService().deleteHistory(activityInstHistoryID);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "restoreHistory")
    @MethodChinaName(cname = "回复历史")
    public @ResponseBody ResultModel<ReturnType> restoreHistory(String activityInstHistoryID) {
        return getService().restoreHistory(activityInstHistoryID);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "clearHistory")
    @MethodChinaName(cname = "清空历史")
    public @ResponseBody ResultModel<ReturnType> clearHistory(String activityInstHistoryID) {
        return getService().restoreHistory(activityInstHistoryID);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "loadHistoryActivityInstHistoryList")
    @MethodChinaName(cname = "批量装载")
    public @ResponseBody ListResultModel<List<ActivityInstHistory>> loadHistoryActivityInstHistoryList(@RequestBody String[] activityInstHistoryIds) {
        return getService().loadHistoryActivityInstHistoryList(activityInstHistoryIds);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "loadHistoryAttribute")
    @MethodChinaName(cname = "历史属性")
    public @ResponseBody ListResultModel<List<String>> loadHistoryAttribute(String activityInstHistoryId) {
        return getService().loadHistoryAttribute(activityInstHistoryId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "addPersonTagToHistory")
    @MethodChinaName(cname = "添加个人标签")
    public @ResponseBody ResultModel<ReturnType> addPersonTagToHistory(String activityInstHistoryID, ActivityInstHistoryAtt tagName) {
        return getService().addPersonTagToHistory(activityInstHistoryID,tagName);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "deletePersonTagToHistory")
    @MethodChinaName(cname = "删除个人标签")
    public @ResponseBody ResultModel<ReturnType> deletePersonTagToHistory(String activityInstHistoryID, ActivityInstHistoryAtt tagName) {
        return getService().deletePersonTagToHistory(activityInstHistoryID,tagName);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "loadActivityInstHistoryArrtibuteIds")
    @MethodChinaName(cname = "批量装载标签")
    public @ResponseBody ListResultModel<List<String>> loadActivityInstHistoryArrtibuteIds(String historyId) {
        return getService().loadActivityInstHistoryArrtibuteIds(historyId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "loadActivityInstHistoryArrtibutes")
    @MethodChinaName(cname = "批量装载属性")
    public @ResponseBody ListResultModel<List<AttributeInst>> loadActivityInstHistoryArrtibutes(@RequestBody String[] attributeIds) {
        return getService().loadActivityInstHistoryArrtibutes(attributeIds);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getRouteBackActivityHistoryInstList")
    @MethodChinaName(cname = "可退回的历史")
    public @ResponseBody ListResultModel<List<String>> getRouteBackActivityHistoryInstList(String activityInstId) {
        return getService().getRouteBackActivityHistoryInstList(activityInstId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "setActivityHistoryAttribute")
    @MethodChinaName(cname = "设置流程历史属性")
    public @ResponseBody ResultModel<Boolean> setActivityHistoryAttribute(String activityDefId, String name, String value) {
        return getService().setActivityHistoryAttribute(activityDefId,name,value);
    }

    ProcessHistoryService getService(){
        return (ProcessHistoryService) EsbUtil.parExpression("$ProcessHistoryService");
    }
}
