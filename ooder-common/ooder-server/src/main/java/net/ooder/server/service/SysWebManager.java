package net.ooder.server.service;

import com.alibaba.fastjson.JSONObject;
import net.ooder.cluster.ServerNodeList;
import net.ooder.config.CApplication;
import net.ooder.annotation.MethodChinaName;
import net.ooder.config.ResultModel;
import net.ooder.esb.config.manager.ExpressionTempBean;
import net.ooder.jds.core.User;
import net.ooder.server.ServerStatus;
import net.ooder.server.SubSystem;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 系统管理远程方法
 * <p>
 * Title:
 * </p>
 * *
 * <p>
 * Copyright: Copyright (c) 2003-2019
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 *
 * @author  ooder
 * @version 1.0
 */
public interface SysWebManager {


    /**
     * 获取集群配置信息
     *
     * @return
     */
    public ResultModel<ServerNodeList> GetAllSystemBeanList(String code);

    /**
     * 获取所有应用信息
     *
     * @return
     */
    public ResultModel<List<CApplication>> GetAppLications();

    /**
     * 获取远程注册服务
     *
     * @return
     */
    public ResultModel<List<ExpressionTempBean>> getClusterService();


    /**
     * 获取服务器列表信息
     *
     * @return
     */
    public ResultModel<List<SubSystem>> getAllSystemInfo();


    /**
     * 获取服务器列表信息
     *
     * @return
     */
    public ResultModel<List<SubSystem>> getAllSAASSystemInfo();


    /**
     * 获取服务器列表信息
     *
     * @return
     */
    public ResultModel<Boolean> saveSystemInfo(SubSystem eiSubSystem);


    /**
     * 获取子系统
     *
     * @return
     */
    @MethodChinaName(cname = "获取子系统", display = false)
    public ResultModel<SubSystem> getSubSystemInfo(String systemCode);

    /**
     * 服务器登录
     *
     * @param userName
     * @param password
     * @param systemCode
     * @return
     */
    public ResultModel<User> syslogin(String userName, String password, String systemCode);


    /**
     * 开发账户登录
     *
     * @param userName
     * @param password
     * @return
     */
    public ResultModel<User> clientLogin(String userName, String password);


    /**
     * 刷新配置信息
     *
     * @return
     */
    public ResultModel<Boolean> reLoadAll();


    /**
     * 获取服务器状态
     *
     * @return
     */
    public @ResponseBody
    ResultModel<List<ServerStatus>> getAllSystemStatus();


    /**
     * 获取所有服务状态,为zibbix脚本提供几口
     *
     * @return JSONObject
     * @author eric
     * @date 7:34 PM 2019/7/3
     */
    public JSONObject getAllServiceStatus();


}