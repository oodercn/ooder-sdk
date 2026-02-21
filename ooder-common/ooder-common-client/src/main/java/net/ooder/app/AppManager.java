/**
 * $RCSfile: AppManager.java,v $
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
package net.ooder.app;

import net.ooder.annotation.MethodChinaName;
import net.ooder.common.ConfigCode;
import net.ooder.org.Person;
import net.ooder.org.PersonNotFoundException;
import net.ooder.org.Role;

import java.util.List;

/**
 * 应用模块管理器。提供各种访问应用模块相关信息的方法
 * <p>
 * Title: 应用模块中间件
 * </p>
 * <p>
 * Description: 3.0 新增总线注入方法
 * </p>
 * <p>
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025-2014
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 * 
 * @author  ooder
 * @version 1.0
 */
public interface AppManager {

    /**
     * 初始化子系统
     * 
     * @param subSystemId
     */
    @MethodChinaName(cname = "取得所有的顶级机构", display = false)
    public abstract void init(ConfigCode subSystemId);

    /**
     * 取得所有应用菜单，只取得菜单的基本信息，忽略其他等信息
     * 
     * @return 应用菜单对象的数组
     */
    @MethodChinaName(cname = "取得所有应用菜单")
    public List<App> getAppList();

    /**
     * 取得所有应用菜单，只取得菜单的基本信息，忽略其他等信息
     * 
     * @return 应用菜单对象的数组
     */
    @MethodChinaName(cname = "取得顶级菜单")
    public List<App> getTopApps();

    @MethodChinaName(cname = "取得所有应用模块")
    public List<AppModule> getModuleList();

    /**
     * 根据应用的ID（标识）取得该应用的实例
     * 
     * @param appId
     * @return @throws OrgNotFoundException
     */
    @MethodChinaName(cname = "根据应用的ID（标识）取得该应用的实例", returnStr = "getAppByID($R('appId'))")
    public App getAppByID(String appId) throws AppNotFoundException;

    /**
     * 根据模块的ID（标识）取得该模块的实例
     * 
     * @param serviceId
     * @return @throws OrgNotFoundException
     */
    @MethodChinaName(cname = "根据模块的服务serviceId取得该模块的实例", returnStr = "getModuleByServiceID($R('moduleId'))")
    public AppModule getModuleByServiceID(String serviceId) throws ModuleNotFoundException;

    /**
     * 根据模块的ID（标识）取得该模块的实例
     * 
     * @param moduleId
     * @return @throws OrgNotFoundException
     */
    @MethodChinaName(cname = "根据模块的ID（标识）取得该模块的实例", returnStr = "getModuleByID($R('moduleId'))")
    public AppModule getModuleByID(String moduleId) throws ModuleNotFoundException;



    /**
     * 根据模块的ID（标识）取得该模块授权角色
     *
     * @param moduleId
     * @return @throws OrgNotFoundException
     */
    @MethodChinaName(cname = "根据模块的ID（标识）取得该模块授权角色", returnStr = "getRoleIdByModuleId($R('moduleId'))")
    public List<Role> getRoleByModuleId(String moduleId) throws ModuleNotFoundException;


    /**
     * 根据模块的ID（标识）取得该模块的实例
     *
     * @param moduleId
     * @return @throws OrgNotFoundException
     */
    @MethodChinaName(cname = "根据模块的ID（标识）取得该模块的授权人员", returnStr = "getPersonIdByModuleId($R('moduleId'))")
    public List<Person> getPersonByModuleId(String moduleId) throws ModuleNotFoundException;


    /**
     * 根据模块的ID（标识）取得该应用授权角色
     *
     * @param moduleId
     * @return @throws OrgNotFoundException
     */
    @MethodChinaName(cname = "根据模块的ID（标识）取得该应用授权角色", returnStr = "getRoleIdByModuleId($R('moduleId'))")
    public List<Role> getRoleByAppId(String moduleId) throws ModuleNotFoundException;


    /**
     * 根据模块的ID（标识）取得该模块的授权人员
     *
     * @param moduleId
     * @return @throws OrgNotFoundException
     */
    @MethodChinaName(cname = "根据模块的ID（标识）取得该模块的授权人员", returnStr = "getPersonIdByModuleId($R('moduleId'))")
    public List<Person> getPersonByAppId(String moduleId) throws ModuleNotFoundException;


   /**
     * 为用户添加服务
     * 
     * @param personId
     * @param moduleId
     */
    @MethodChinaName(cname = "为用户添加服务", returnStr = "addModule2Person($R('personId'),$R('moduleId'))")
    public void addModule2Person(String personId, String moduleId) throws ModuleNotFoundException, PersonNotFoundException;

    /**
     * 为用户移除服务
     * 
     * @param personId
     * @param moduleId
     * @throws PersonNotFoundException
     * @throws ModuleNotFoundException
     */
    @MethodChinaName(cname = "为用户移除服务", returnStr = "removeModule4Person($R('personId'),$R('moduleId'))")
    public void removeModule4Person(String personId, String moduleId) throws ModuleNotFoundException, PersonNotFoundException;

    /**
     * 挂起服务
     * 
     * @param personId
     * @param moduleId
     */
    @MethodChinaName(cname = "挂起服务", returnStr = "suspendModule($R('personId'),$R('moduleId'))")
    public void suspendModule(String personId, String moduleId) throws ModuleNotFoundException, PersonNotFoundException;

    /**
     * 恢复服务
     * 
     * @param personId
     * @param moduleId
     */
    @MethodChinaName(cname = "恢复服务", returnStr = "resumeActivityInst($R('personId'),$R('moduleId'))")
    public void resumeModule(String personId, String moduleId) throws ModuleNotFoundException, PersonNotFoundException;

}