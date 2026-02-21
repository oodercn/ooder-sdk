package net.ooder.agent.client.iot.client.api;

import net.ooder.agent.client.command.Command;
import net.ooder.agent.client.iot.client.GWAccountWebService;
import net.ooder.agent.client.iot.json.device.GWUser;
import net.ooder.agent.client.iot.json.device.Gateway;
import net.ooder.agent.client.iot.json.device.GatewayErrorReport;
import net.ooder.annotation.MethodChinaName;
import net.ooder.config.ResultModel;
import  net.ooder.jds.core.esb.EsbUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/jds/iot/accountservice/")
@MethodChinaName(cname = "网关账号管理接口")
public class GWAccountWebServiceAPI implements GWAccountWebService {

    public GWAccountWebServiceAPI() {

    }

    @MethodChinaName(cname = "网关注册", returnStr = "Register($Gateway)")
    @RequestMapping(method = RequestMethod.POST, value = "register")
    public @ResponseBody
    ResultModel<Gateway> register(@RequestBody Gateway gateway) {

        return getGWAccountWebService().register(gateway);
    }

    @MethodChinaName(cname = "网关激活", returnStr = "Activate($Gateway)")
    @RequestMapping(method = RequestMethod.POST, value = "activate")
    public @ResponseBody
    ResultModel<Gateway> activate(@RequestBody Gateway gateway) {
        return getGWAccountWebService().activate(gateway);

    }

    @MethodChinaName(cname = "网关错误报告", returnStr = "GatewayErrorReport($GatewayErrorReport)")
    @RequestMapping(method = RequestMethod.POST, value = "gatewayErrorReport")
    public @ResponseBody
    ResultModel<List<Command>> gatewayErrorReport(@RequestBody
                                                          GatewayErrorReport errorPort) {

        return getGWAccountWebService().gatewayErrorReport(errorPort);
    }

    @MethodChinaName(cname = "登录", returnStr = "Login($GWUser)")
    @RequestMapping(method = RequestMethod.POST, value = "login")
    public @ResponseBody
    ResultModel<GWUser> login(@RequestBody GWUser userInfo) {
        return getGWAccountWebService().login(userInfo);
    }

    @MethodChinaName(cname = "注销", returnStr = "Logout()")

    @RequestMapping(method = RequestMethod.POST, value = "logout")
    public @ResponseBody
    ResultModel<Boolean> logout() {
        return getGWAccountWebService().logout();
    }

    @MethodChinaName(cname = "网关登录", returnStr = "GWLogin($R('gwIeee'))")

    @RequestMapping(method = RequestMethod.POST, value = "GWLogin")
    public @ResponseBody
    ResultModel<GWUser> gwLogin(String gwIeee) {
        return getGWAccountWebService().gwLogin(gwIeee);
    }

    GWAccountWebService getGWAccountWebService() {
        return (GWAccountWebService) EsbUtil.parExpression(GWAccountWebService.class);
    }


}
