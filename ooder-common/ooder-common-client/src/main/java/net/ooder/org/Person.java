/**
 * $RCSfile: Person.java,v $
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
package net.ooder.org;


import net.ooder.annotation.*;
import net.ooder.annotation.ViewType;

import java.util.List;
import java.util.Set;

/**
 * 人员接口
 * 定义了ooder组织机构中人员的基本信息和操作，包括人员属性、所属部门、角色等
 * 
 * @author ooder team
 * @version 2.0
 * @since 2025-08-25
 */
@ESDEntity
public interface Person extends Comparable<Person> {

    /**
     * 取得人员的标识
     *
     * @return 人员的标识
     */
    @MethodChinaName(cname = "人员标识")
    @Uid
    public String getID();

    /**
     * 取得人员的状态
     *
     * @return 人员的状态
     */
    @MethodChinaName(cname = "当前状态")
    public String getStatus();

    /**
     * 取得人员的组织机构ID
     *
     * @return 人员的组织机构ID
     */
    @MethodChinaName(cname = "组织机构ID")
    @Pid
    public String getOrgId();

    /**
     * 取得人员的帐号
     *
     * @return 人员的帐号
     */
    @MethodChinaName(cname = "帐号")
    public String getAccount();

    /**
     * 取得人员的序号
     *
     * @return 人员的序号
     */
    @MethodChinaName(cname = "排序", display = false)
    public Integer getIndex();

    /**
     * 取得人员的口令
     *
     * @return 人员的口令
     */
    @MethodChinaName(cname = "密码", display = false)
    public String getPassword();

    /**
     * 取得人员的名称
     *
     * @return 人员的名称
     */
    @MethodChinaName(cname = "姓名")
    @Caption
    public String getName();

    /**
     * 取得人员的移动电话号码
     *
     * @return 移动电话号码
     */
    @MethodChinaName(cname = "手机号码")
    public String getMobile();

    /**
     * 昵称
     *
     * @return 昵称
     */
    @MethodChinaName(cname = "昵称")
    public String getNickName();

    /**
     * 取得人员的Email地址
     *
     * @return Email地址
     */
    @MethodChinaName(cname = "电子邮件")
    public String getEmail();

    @MethodChinaName(cname = "所有部门")
    @Ref(ref = RefType.REF, view = ViewType.GRID)
    public List<Org> getOrgList();

    @Ref(ref = RefType.M2O, view = ViewType.DIC)
    public Org getOrg();

    public String getCloudDiskPath();

    public Set<String> getRoleIdList();

    public Set<String> getOrgIdList();


    @Ref(ref = RefType.M2M, view = ViewType.GRID)
    public List<Role> getRoleList();

}