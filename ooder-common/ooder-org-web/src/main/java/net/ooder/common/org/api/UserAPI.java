package net.ooder.common.org.api;

import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.common.ContextType;
import net.ooder.common.TokenType;
import net.ooder.common.org.service.UserService;
import net.ooder.annotation.MethodChinaName;
import net.ooder.config.ResultModel;
import net.ooder.jds.core.User;
import net.ooder.jds.core.esb.EsbUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/org/user/")
@MethodChinaName(cname = "用户注册")
@EsbBeanAnnotation(dataType = ContextType.Server,tokenType = TokenType.admin)
public class UserAPI implements UserService {

    public UserAPI() {
    }

    @MethodChinaName(cname = "登录", returnStr = "login($User)")
    @RequestMapping(method = RequestMethod.POST, value = "login")
    public @ResponseBody
    ResultModel<User> login(@RequestBody
                                    User user) {
        return getUserService().login(user);
    }

    @MethodChinaName(cname = "注销", returnStr = "logout()")
    @RequestMapping(method = RequestMethod.POST, value = "logout")
    public @ResponseBody
    ResultModel<User> logout() {
        return getUserService().logout();
    }

    @MethodChinaName(cname = "注册", returnStr = "register($User)")
    @RequestMapping(method = RequestMethod.POST, value = "register")
    public @ResponseBody
    ResultModel<User> register(@RequestBody
                                       User user) {
        return getUserService().register(user);
    }

    @MethodChinaName(cname = "发送号码", returnStr = "sendCode($R('mobile'))")

    @RequestMapping(method = RequestMethod.POST, value = "sendCode")
    public @ResponseBody
    ResultModel<User> sendCode(String mobile) {
        return getUserService().sendCode(mobile);
    }

    @MethodChinaName(cname = "修改用户信息", returnStr = "updateUserInfo($User)")

    @RequestMapping(method = RequestMethod.POST, value = "updateUserInfo")
    public @ResponseBody
    ResultModel<User> updateUserInfo(@RequestBody
                                             User user) {
        return getUserService().updateUserInfo(user);
    }

    @MethodChinaName(cname = "重置密码", returnStr = "restPw($R('account'),$R('newpassword'),$R('code'))")

    @RequestMapping(method = RequestMethod.POST, value = "restPw")
    public @ResponseBody
    ResultModel<User> restPw(String account, String newpassword, String code) {
        return getUserService().restPw(account, newpassword, code);
    }

    @MethodChinaName(cname = "修改密码", returnStr = "updatePassword($R('oldpassword'),$R('newpassword'),$R('userId'))")

    @RequestMapping(method = RequestMethod.POST, value = "updatePassword")
    public @ResponseBody
    ResultModel<User> updatePassword(String oldpassword,
                                     String newpassword, String userId) {
        return getUserService()
                .updatePassword(oldpassword, newpassword, userId);
    }

    UserService getUserService() {
        return (UserService) EsbUtil.parExpression(UserService.class);
    }

}
