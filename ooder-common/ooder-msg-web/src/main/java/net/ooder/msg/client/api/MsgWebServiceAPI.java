/**
 * $RCSfile: MsgWebServiceAPI.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.msg.client.api;

import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.annotation.JLuceneIndex;
import net.ooder.annotation.MethodChinaName;
import net.ooder.common.Condition;
import net.ooder.common.ContextType;
import net.ooder.common.TokenType;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.msg.RMsg;
import net.ooder.msg.client.MsgWebService;
import net.ooder.org.query.MsgConditionKey;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/api/msg/webService")
@MethodChinaName("消息服务")
@EsbBeanAnnotation(dataType = ContextType.Server, tokenType = TokenType.admin)
public class MsgWebServiceAPI implements MsgWebService {

    @MethodChinaName("批量装载消息")
    @Override
    @RequestMapping(method = {RequestMethod.POST}, value = {"/loadMsgIds"})
    @ResponseBody
    public ListResultModel<List<RMsg>> loadMsgs(@RequestBody String[] msgId) {
        return getService().loadMsgs(msgId);
    }

    @MethodChinaName("获取消息")
    @Override
    @RequestMapping(method = {RequestMethod.POST}, value = {"/getMsgById"})
    @ResponseBody
    public ResultModel<RMsg> getMsgById(String msgId) {
        return getService().getMsgById(msgId);
    }

    @MethodChinaName("查询消息")
    @Override
    @RequestMapping(method = {RequestMethod.POST}, value = {"/findMsgIds"})
    @ResponseBody
    public ListResultModel<List<String>> findMsgIds(@RequestBody Condition<MsgConditionKey, JLuceneIndex> condition) {
        return getService().findMsgIds(condition);
    }

    @MethodChinaName("更新消息")
    @Override
    @RequestMapping(method = {RequestMethod.POST}, value = {"/updateMsg"})
    @ResponseBody
    public ResultModel<Boolean> updateMsg(@RequestBody RMsg msg) {
        return getService().updateMsg(msg);
    }

    @MethodChinaName("删除消息")
    @Override
    @RequestMapping(method = {RequestMethod.POST}, value = {"/deleteMsg"})
    @ResponseBody
    public ResultModel<Boolean> deleteMsg(String msgId) {
        return getService().deleteMsg(msgId);
    }

    @MethodChinaName("批量删除")
    @Override
    @RequestMapping(method = {RequestMethod.POST}, value = {"/deleteMsgs"})
    @ResponseBody
    public ResultModel<Boolean> deleteMsgs(@RequestBody String[] msgIds) {
        return getService().deleteMsgs(msgIds);
    }

    <T> MsgWebService getService() {
        return (MsgWebService) EsbUtil.parExpression(MsgWebService.class);
    }
}


