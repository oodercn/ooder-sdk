/**
 * $RCSfile: App.java,v $
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

import java.util.List;


/**
 * 应用接口。
 * <p>
 * Title: ooder系统管理系统
 * </p>
 * <p>
 * Description: 应用接口，提供应用管理相关功能
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 * 
 * @author ooder team
 * @version 1.0
 * @since 2025-08-25
 */
public interface App {

    @MethodChinaName(cname = "取得应用下所有模块")
    public List<AppModule> getModuleList();

    /**
     * 取得该机构（包括其所有子机构）下的所有人员。
     * 
     * @return 人员数组
     */

    @MethodChinaName(cname = "取得该应用（包括其所有子应用）下的所有模块", display = false)
    public List<AppModule> getModuleListRecursively();

    /**
     * 取得该应用的所有子应用（包括间接子应用）。
     * 
     * @return 应用数组
     */

    @MethodChinaName(cname = "取得该应用的所有子应用（包括间接子应用）", display = false)
    public List<App> getChildrenRecursivelyList();

    /**
     * 取得父应用对象
     * 
     * @return 应用对象
     */
    @MethodChinaName(cname = "取得父应用对象", display = false)
    public App getParent();

    /**
     * 取得指定APP的直接上级和间接上级的APP（递归）
     * 
     * @return
     */
    @MethodChinaName(cname = "取得指定APP的直接上级和间接上级的APP（递归）", display = false)
    public List<App> getAllParent();

    /**
     * 取得父应用的标识
     * 
     * @return 父应用的标识
     */
    @MethodChinaName(cname = "取得父应用的标识")
    public String getParentId();

    /**
     * 取得该应用的所有直接子应用
     * 
     * @return 应用数组
     */

    @MethodChinaName(cname = "取得该应用的所有直接子应用")
    public List<App> getChildrenList();

    /**
     * 取得该应用图标。
     * 
     * @return 应用的图标
     */
    @MethodChinaName(cname = "取得该应用的图标")
    public String getIcon();

    /**
     * 取得该应用的标识
     * 
     * @return 应用的标识
     */
    @MethodChinaName(cname = "取得该应用的标识")
    public String getID();

    /**
     * 取得该应用的排序
     * 
     * @return 应用的排序
     */
    @MethodChinaName(cname = "取得该应用的排序")
    public Integer getIndex();

    /**
     * 取得该应用的名称
     * 
     * @return 应用的名称
     */
    @MethodChinaName(cname = "取得该应用的名称")
    public String getName();

    /**
     * 取得该机构所属自身的层数[，第一层为0]
     * 
     * @return 层数
     */
    @MethodChinaName(cname = " 取得该应用所属自身的层数[，第一层为0]")
    public int getTier();

    /**
     * 取得该应用的简要描述。
     * 
     * @return 应用的简要描述
     */
    @MethodChinaName(cname = "取得该应用的简要描述")
    public String getBrief();

    public int compareTo(App o);
}