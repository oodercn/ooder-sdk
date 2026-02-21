/**
 * $RCSfile: AppModule.java,v $
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

import java.util.List;

import net.ooder.annotation.MethodChinaName;
import net.ooder.org.Person;
import net.ooder.org.Role;

/**
 * 模块接口类
 * <p>
 * Title: ooder组织机构中间件
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025-2008
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 * 
 * @author  ooder
 * @version 2.0
 */
public interface AppModule {
    /**
     * 取得模块标识
     * 
     * @return 取得模块标识
     */
    @MethodChinaName(cname = "取得模块标识")
    public String getID();

    @MethodChinaName(cname = "取得模块排序")
    public Integer getIndex();

    @MethodChinaName(cname = "取得模块英文名称")
    public String getEnName();

    @MethodChinaName(cname = "取得模块资源地址")
    public String getUrl();

    /**
     * 取得模块名称
     * 
     * @return 模块名称
     */
    @MethodChinaName(cname = " 取得模块名称")
    public String getName();

    /**
     * 取得模块图标
     * 
     * @return 模块图标
     */
    @MethodChinaName(cname = " 取得模块图标")
    public String getIcon();

    /**
     * 该模块是否可用
     * 
     * @return 该模块是否可用
     */

    @MethodChinaName(cname = "该模块是否可用")
    public Integer getEnabled();

    /**
     * 该模块是否授权访问
     * 
     * @return 该模块是否授权访问
     */

    @MethodChinaName(cname = "该模块是否授权访问")
    public Integer getNeedRight();

    /**
     * 取得拥有该模块权限的所有角色
     * 
     * @return 人员角色的数组
     */
    @MethodChinaName(cname = "取得拥有该模块权限的所有角色")
    public List<Role> getRoleList();

    /**
     * 取得该模块所属所有应用
     * 
     * @return 应用的数组
     */

    @MethodChinaName(cname = "取得该模块所属所有应用")
    public List<App> getAppList();

    @MethodChinaName(cname = "取得该模块的禁用的人员", display = false)
    public List<Person> getDisablePersonList();

    @MethodChinaName(cname = "拥有该模块权限的所有人员")
    public List<Person> getAllRightPerson();

    public int compareTo(AppModule o);
}
