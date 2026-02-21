/**
 * $RCSfile: MsgIndexServiceAPI.java,v $
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
package net.ooder.msg.index;

import net.ooder.annotation.MethodChinaName;
import net.ooder.common.Condition;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;
import net.ooder.index.config.JLucene;
import net.ooder.index.service.IndexService;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.org.query.MsgConditionKey;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/api/msg/index/")
@MethodChinaName("消息索引")
public class MsgIndexServiceAPI {

    @MethodChinaName("添加消息索引")
    @RequestMapping(method = RequestMethod.POST, value = "addIndex")
    public @ResponseBody
    <T extends JLucene> ResultModel<T> addIndex(@RequestBody T luceneBean) {
        return getService().addIndex(luceneBean);
    }

    @MethodChinaName("移除消息索引")
    @RequestMapping(method = RequestMethod.POST, value = "deleteDataIndex")
    public @ResponseBody
    ResultModel<Boolean> deleteDataIndex(@RequestBody Condition<MsgConditionKey, DataIndex> condition) {
        return getService().deleteAllIndex(condition);
    }

    @MethodChinaName("移除事件消息索引")
    @RequestMapping(method = RequestMethod.POST, value = "searchDataIndex")
    public @ResponseBody
    ListResultModel<List<DataIndex>> searchDataIndex(@RequestBody Condition<MsgConditionKey, DataIndex> condition) {
        return getService().search(condition);
    }

    @MethodChinaName("移除事件消息索引")
    @RequestMapping(method = RequestMethod.POST, value = "deleteEventIndex")
    public @ResponseBody
    ResultModel<Boolean> deleteEventIndex(@RequestBody Condition<MsgConditionKey, EventIndex> condition) {
        return getService().deleteAllIndex(condition);
    }

    @MethodChinaName("检索事件消息索引")
    @RequestMapping(method = RequestMethod.POST, value = "searchEventIndex")
    public @ResponseBody
    ListResultModel<List<EventIndex>> searchEventIndex(@RequestBody Condition<MsgConditionKey, EventIndex> condition) {
        return getService().search(condition);
    }

    @MethodChinaName("移除日志索引")
    @RequestMapping(method = RequestMethod.POST, value = "deleteLogIndex")
    public @ResponseBody
    ResultModel<Boolean> deleteLogIndex(@RequestBody Condition<MsgConditionKey, LogIndex> condition) {
        return getService().deleteAllIndex(condition);
    }

    @MethodChinaName("查询日志索引")
    @RequestMapping(method = RequestMethod.POST, value = "searchLogIndex")
    public @ResponseBody
    ListResultModel<List<LogIndex>> searchLogIndex(@RequestBody Condition<MsgConditionKey, LogIndex> condition) {
        return getService().search(condition);
    }

    @MethodChinaName("查询数据索引")
    @RequestMapping(method = RequestMethod.POST, value = "searchSensorMsg")
    public @ResponseBody
    ListResultModel<List<SensorMsgIndex>> SensorMsgIndex(@RequestBody Condition<MsgConditionKey, SensorMsgIndex> condition) {
        return getService().search(condition);
    }

    @MethodChinaName("移除日志索引")
    @RequestMapping(method = RequestMethod.POST, value = "deleteSensorMsg")
    public @ResponseBody
    ResultModel<Boolean> deleteSensorMsg(@RequestBody Condition<MsgConditionKey, SensorMsgIndex> condition) {
        return getService().deleteAllIndex(condition);
    }

    public IndexService getService() {
        IndexService service = EsbUtil.parExpression(IndexService.class);
        return service;
    }

}


