package net.ooder.server.api;

import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.annotation.MethodChinaName;
import net.ooder.cluster.event.RegistEventBean;
import net.ooder.cluster.service.SysEventWebManager;
import net.ooder.common.ContextType;
import net.ooder.common.TokenType;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;
import net.ooder.esb.config.manager.ServiceBean;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.web.RuntimeLog;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Controller
@RequestMapping("/api/sys/")
@MethodChinaName(cname = "集群事件管理服务")
@EsbBeanAnnotation(tokenType = TokenType.admin, dataType = ContextType.Server)
public class SysEventWebManagerAPI implements SysEventWebManager {


    SysEventWebManager getSysEventWebManager() {
        return EsbUtil.parExpression(SysEventWebManager.class);
    }


    @Override
    @MethodChinaName(cname = "获取运行期日志")
    @RequestMapping(method = RequestMethod.POST, value = "getRunTimeLogs")
    public @ResponseBody
    ListResultModel<List<RuntimeLog>> getRunTimeLogs(String url, String body, String sessionId, Long time) {
        return this.getSysEventWebManager().getRunTimeLogs(url, body, sessionId, time);
    }


    @Override
    @RequestMapping(method = RequestMethod.POST, value = "registerEvent")
    @MethodChinaName(cname = "注册事件监听器")
    public @ResponseBody
    ResultModel<Boolean> registerEvent(String systemCode, String eventKey) {
        return this.getSysEventWebManager().registerEvent(systemCode, eventKey);
    }


    @Override
    @RequestMapping(method = RequestMethod.POST, value = "registerEventJSON")
    @MethodChinaName(cname = "5.1接口")
    public @ResponseBody
    ResultModel<Boolean> registerEventJSON(String systemCode, String json) {
        return this.getSysEventWebManager().registerEventJSON(systemCode, json);
    }

    @Override
    @MethodChinaName(cname = "清空监听器")
    @RequestMapping(method = RequestMethod.POST, value = "clearEventKeys")
    public @ResponseBody
    ResultModel<Boolean> clearEventKeys(String systemCode) {
        return this.getSysEventWebManager().clearEventKeys(systemCode);
    }

    @Override
    @MethodChinaName(cname = "移除事件")
    @RequestMapping(method = RequestMethod.POST, value = "removeEvent")
    public @ResponseBody
    ResultModel<Boolean> removeEvent(String systemCode, String eventKey) {
        return this.getSysEventWebManager().removeEvent(systemCode, eventKey);
    }

    @Override
    @MethodChinaName(cname = "获取所有注册事件")
    @RequestMapping(method = RequestMethod.POST, value = "getRegisterEventByCode")
    public @ResponseBody
    ListResultModel<List<? extends ServiceBean>> getRegisterEventByCode(String sysCode) {
        return this.getSysEventWebManager().getRegisterEventByCode(sysCode);
    }


    @Override
    @MethodChinaName(cname = "获取所有注册事件")
    @RequestMapping(method = RequestMethod.POST, value = "getAllRegisterEvent")
    public @ResponseBody
    ListResultModel<List<RegistEventBean>> getAllRegisterEvent() {
        return this.getSysEventWebManager().getAllRegisterEvent();
    }

}
