/**
 * $RCSfile: Org.java,v $
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

/**
 * 机构接口。
 * <p>
 * Title: ooder组织机构中间件
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 *
 * @author  ooder
 * @version 2.0
 */
@ESDEntity
//@Aggregation(type = AggregationType.ROOT, sourceClass = Org.class, rootClass = Org.class)
public interface Org extends Comparable<Org> {


    /**
     * 取得该机构的标识
     *
     * @return 机构的标识
     */
    @MethodChinaName(cname = "ID")
    @Uid
    public String getOrgId();


    /**
     * 取得该机构的名称
     *
     * @return 机构的名称
     */
    @MethodChinaName(cname = "名称")
    @Caption
    public String getName();

    /**
     * 取得该机构的简要描述
     *
     * @return 机构的简要描述
     */
    @MethodChinaName(cname = "简要描述")
    public String getBrief();


    /**
     * 部门所在城市
     *
     * @return 部门所在城市
     */
    @MethodChinaName(cname = "所属城市")
    public String getCity();

    @MethodChinaName(cname = "机构角色", display = false)
    @Ref(ref = RefType.M2M, view = ViewType.GRID)
    public List<Role> getRoleList();

    @MethodChinaName(cname = "角色ID", display = false)
    public List<String> getRoleIdList();

    /**
     * 取得该机构所属自身的层数[，第一层为0]
     *
     * @return 层数
     */
    @MethodChinaName(cname = "层级")
    public Integer getTier();

    /**
     * 取得该机构领导人的对象
     *
     * @return 领导人的人员对象
     */
    @MethodChinaName(cname = "部门领导")
    @Ref(ref = RefType.O2O, view = ViewType.DIC)
    public Person getLeader();


    @MethodChinaName(cname = "所有人员")
    @Ref(ref = RefType.O2M, view = ViewType.GRID)
    public List<Person> getPersonList();

    @MethodChinaName(cname = "排序", display = false)
    public Integer getIndex();

    /**
     * 取得该机构领导人的对象ID
     *
     * @return 领导人的人员对象ID
     */
    @MethodChinaName(cname = "领导ID")
    public String getLeaderId();


    @MethodChinaName(cname = "所有人员", display = false)
    @Ref(ref = RefType.FIND, view = ViewType.GRID)
    public List<Person> getPersonListRecursively();


    @MethodChinaName(cname = "所有子部门", display = false)
    @Ref(ref = RefType.FIND, view = ViewType.GRID)
    public List<Org> getChildrenRecursivelyList();

    /**
     * 取得父机构对象
     *
     * @return 机构对象
     */
    @MethodChinaName(cname = "父级部门")
    @Ref(ref = RefType.M2O, view = ViewType.DIC)
    public Org getParent();

    /**
     * 取得父机构的标识
     *
     * @return 父机构的标识
     */
    @MethodChinaName(cname = "父级部门ID", display = false)
    @Pid
    public String getParentId();

    @MethodChinaName(cname = "直接子机构")
    @Ref(ref = RefType.F2F, view = ViewType.TREE)
    public List<Org> getChildrenList();

    @MethodChinaName(cname = "直接子机构IDs", display = false)
    public List<String> getChildIdList();


    @MethodChinaName(cname = "成员IDs", display = false)
    public List<String> getPersonIdList();

}