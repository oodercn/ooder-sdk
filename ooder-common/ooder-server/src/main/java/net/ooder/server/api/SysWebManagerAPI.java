package net.ooder.server.api;

import com.alibaba.fastjson.JSONObject;
import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.annotation.MethodChinaName;
import net.ooder.cluster.ServerNodeList;
import net.ooder.common.ContextType;
import net.ooder.config.CApplication;
import net.ooder.config.ResultModel;
import net.ooder.esb.config.manager.ExpressionTempBean;
import net.ooder.jds.core.User;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.server.ServerStatus;
import net.ooder.server.SubSystem;
import net.ooder.server.service.SysWebManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Controller
@RequestMapping("/api/sys/")
@MethodChinaName(cname = "集群管理服务")
@EsbBeanAnnotation(dataType = ContextType.Server)
public class SysWebManagerAPI implements SysWebManager {


    SysWebManager getSysWebManager() {
        return (SysWebManager) EsbUtil.parExpression(SysWebManager.class);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "GetAllSystemBeanList")
    @MethodChinaName(cname = "获取所有系统")
    public @ResponseBody
    ResultModel<ServerNodeList> GetAllSystemBeanList(String code) {
        return this.getSysWebManager().GetAllSystemBeanList(code);
    }

    @Override
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, value = "GetAppLications")
    @MethodChinaName(cname = "获取所有应用配置")
    public @ResponseBody
    ResultModel<List<CApplication>> GetAppLications() {
        return this.getSysWebManager().GetAppLications();

    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getClusterService")
    @MethodChinaName(cname = "获取所有集群服务")
    public @ResponseBody
    ResultModel<List<ExpressionTempBean>> getClusterService() {
        return this.getSysWebManager().getClusterService();
    }

    @Override
    @MethodChinaName(cname = "获取所有系统信息")
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, value = "GetAllSystemInfo")
    public @ResponseBody
    ResultModel<List<SubSystem>> getAllSystemInfo() {

        return this.getSysWebManager().getAllSystemInfo();
    }

    @Override
    @MethodChinaName(cname = "获取所有SAAS租户信息")
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, value = "getAllDevSystemInfo")
    @ResponseBody
    public ResultModel<List<SubSystem>> getAllSAASSystemInfo() {
        return this.getSysWebManager().getAllSAASSystemInfo();
    }

    @Override
    @MethodChinaName(cname = "更新系统信息")
    @RequestMapping(method = RequestMethod.POST, value = "updateSystemInfo")
    public @ResponseBody
    ResultModel<Boolean> saveSystemInfo(@RequestBody SubSystem eiSubSystem) {
        return this.getSysWebManager().saveSystemInfo(eiSubSystem);
    }

    @Override
    @MethodChinaName(cname = "获取系统详细信息")
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, value = "getSubSystemInfo")
    public @ResponseBody
    ResultModel<SubSystem> getSubSystemInfo(String systemCode) {
        return this.getSysWebManager().getSubSystemInfo(systemCode);
    }

    @Override
    @MethodChinaName(cname = "系统登录")
    @RequestMapping(method = RequestMethod.POST, value = "syslogin")
    public @ResponseBody
    ResultModel<User> syslogin(String userName, String password, String systemCode) {
        return this.getSysWebManager().syslogin(userName, password, systemCode);
    }


    @Override
    @MethodChinaName(cname = "开发客户端登录")
    @RequestMapping(method = RequestMethod.POST, value = "clientLogin")
    public @ResponseBody
    ResultModel<User> clientLogin(String userName, String password) {
        return this.getSysWebManager().clientLogin(userName, password);
    }

    @Override
    @MethodChinaName(cname = "刷新全部配置")
    @RequestMapping(method = RequestMethod.POST, value = "reLoadAll")
    public @ResponseBody
    ResultModel<Boolean> reLoadAll() {
        return this.getSysWebManager().reLoadAll();
    }


    @Override
    @MethodChinaName(cname = "获取所有系统状态")
    @RequestMapping(method = RequestMethod.POST, value = "getAllSystemStatus")
    public @ResponseBody
    ResultModel<List<ServerStatus>> getAllSystemStatus() {
        return this.getSysWebManager().getAllSystemStatus();
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getAllServiceStatus")
    @MethodChinaName(cname = "获取所有系统监控状态")
    public @ResponseBody
    JSONObject getAllServiceStatus() {
        return this.getSysWebManager().getAllServiceStatus();
    }

}
