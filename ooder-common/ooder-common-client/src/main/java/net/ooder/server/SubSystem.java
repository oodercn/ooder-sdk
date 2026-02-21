/**
 * $RCSfile: SubSystem.java,v $
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
package net.ooder.server;

import net.ooder.annotation.MethodChinaName;
import net.ooder.common.ConfigCode;
import net.ooder.common.SystemType;
import net.ooder.common.TokenType;

public interface SubSystem {


    /**
     * 获取子系统统ID
     *
     * @return 子系统统ID
     */

    public String getSysId();

    /**
     * 获取子系统中文名称
     * A
     *
     * @RETURN 子系统中文名称
     */
    @MethodChinaName(cname = "获取子系统中文名称")
    public String getName();

    /**
     * 获取子系统英文名称
     *
     * @return 子系统英文名称
     */
    @MethodChinaName(cname = "获取子系统英文名称")
    public String getEnname();

    /**
     * 获得分类
     *
     * @return
     */
    @MethodChinaName(cname = "获得分类")
    public SystemType getType();

    /**
     * 获取管理员ID
     *
     * @return 管理员ID
     */
    @MethodChinaName(cname = "获取管理员ID")
    public String getAdminId();

    /**
     * 获取对应的Org_Common.xml文件名
     *
     * @return 对应的Org_Common.xml文件名
     */
    @MethodChinaName(cname = "获取对应的Org_Common.xml文件名")
    public ConfigCode getConfigname();

    /**
     * 获取是否允许Guest用户
     *
     * @return 是否允许Guest用户
     */
    @MethodChinaName(cname = "获取是否允许Guest用户")
    public TokenType getTokenType();

    /**
     * 获取显示图标
     *
     * @return 显示图标
     */
    @MethodChinaName(cname = "获取显示图标")
    public String getIcon();


    /**
     * 获取绑定组织ID
     *
     * @return 获取绑定组织ID
     */
    @MethodChinaName(cname = "获取绑定组织ID")
    public String getOrgId();

    /**
     * 获取子系统URL
     *
     * @return 子系统URL
     */
    @MethodChinaName(cname = "获取子系统URL")
    public String getUrl();

    /**
     * 获取描述
     *
     * @return 描述
     */
    @MethodChinaName(cname = "获取存储路径")
    public String getVfsPath();


    /**
     * 获取排序
     *
     * @return 排序
     */
    @MethodChinaName(cname = "获取排序")
    public Integer getSerialindex();

    public void setSysId(String sysId);

    public void setUrl(String url);

    public void setName(String name);

    public void setType(SystemType type);

    public void setConfigname(ConfigCode configname);

    public void setEnname(String enname);

    public void setTokenType(TokenType tokenType);

    public void setIcon(String icon);

    public void setVfsPath(String vfsPath);

    public void setOrgId(String orgId);

    public void setAdminId(String adminId);

    public void setSerialindex(Integer serialindex);


}
