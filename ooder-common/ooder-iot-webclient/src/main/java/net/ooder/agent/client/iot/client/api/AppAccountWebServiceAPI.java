package net.ooder.agent.client.iot.client.api;


import net.ooder.agent.client.iot.client.AppAccountWebService;
import  net.ooder.config.ResultModel;
import  net.ooder.engine.ConnectInfo;
import  net.ooder.annotation.MethodChinaName;
import  net.ooder.jds.core.User;
import  net.ooder.jds.core.esb.EsbUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
@MethodChinaName(cname="App用户接口")
@Controller
@RequestMapping("/jds/iot/appaccountwebservice/")
public class AppAccountWebServiceAPI implements AppAccountWebService {

	@MethodChinaName(cname="根据帐号获取用户信息",returnStr="GetUserByAccount($R('account'))")
	@RequestMapping(method = RequestMethod.POST, value = "getUserByAccount")
	public @ResponseBody
    ResultModel<User> getUserByAccount(String account) {
		return getAppAccountWebService().getUserByAccount(account);
	}
		
	@MethodChinaName(cname="根据ID获取用户信息",returnStr="GetUserById($R('Id'))")
	@RequestMapping(method = RequestMethod.POST, value = "getUserById")
	public @ResponseBody
    ResultModel<User> getUserById(String Id) {
		return getAppAccountWebService().getUserById(Id);
	}
    
	@MethodChinaName(cname="登录",returnStr="Login($R('userName'),$R('password'),$R('imei'))")
	@RequestMapping(method = RequestMethod.POST, value = "login")
	public @ResponseBody
    ResultModel<ConnectInfo> login(String userName, String password, String imei) {
		return getAppAccountWebService().login(userName, password, imei);
	}
	
	@MethodChinaName(cname="注销",returnStr="Logout()")
	@RequestMapping(method = RequestMethod.POST, value = "logout")
	public @ResponseBody
    ResultModel<Boolean> logout() {
		return getAppAccountWebService().logout();
	}
	
	@MethodChinaName(cname="修改密码",returnStr="ModifyPwd($User)")

	@RequestMapping(method = RequestMethod.POST, value = "modifyPwd")
	public @ResponseBody
    ResultModel<Boolean> modifyPwd(@RequestBody User user) {
		return getAppAccountWebService().modifyPwd(user);
	}
	
	@MethodChinaName(cname="注册",returnStr="Register($User)")
	@RequestMapping(method = RequestMethod.POST, value = "register")
	public @ResponseBody
    ResultModel<User> register(@RequestBody User user) {
		return getAppAccountWebService().register(user);
	}
	
	@MethodChinaName(cname="保存用户信息",returnStr="SaveUser($User)")
	@RequestMapping(method = RequestMethod.POST, value = "saveUser")
	public @ResponseBody
    ResultModel<User> saveUser(@RequestBody User user) {
		return getAppAccountWebService().saveUser(user);
	}
	
	@MethodChinaName(cname="发送编号",returnStr="SendCode($User)")
	@RequestMapping(method = RequestMethod.POST, value = "sendCode")
	public @ResponseBody
	ResultModel<String> sendCode(@RequestBody User userData) {
		return getAppAccountWebService().sendCode(userData);
	}
    
	@MethodChinaName(cname="根据手机号码发送编号",returnStr="SendCodeByPhonenum($R('phonenum'))")
	@RequestMapping(method = RequestMethod.POST, value = "sendCodeByPhonenum")
	public @ResponseBody
    ResultModel<String> sendCodeByPhonenum(String phonenum) {
		return getAppAccountWebService().sendCodeByPhonenum(phonenum);
	}

	AppAccountWebService getAppAccountWebService(){
		return (AppAccountWebService) EsbUtil.parExpression(AppAccountWebService.class);
	}


}
