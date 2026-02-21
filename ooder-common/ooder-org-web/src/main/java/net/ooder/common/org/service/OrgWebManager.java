package net.ooder.common.org.service;

import net.ooder.annotation.MethodChinaName;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;
import net.ooder.msg.PersonPrivateGroup;
import net.ooder.org.Org;
import net.ooder.org.Person;
import net.ooder.org.Role;
import net.ooder.annotation.RoleType;

import java.util.List;

/**
 * 组织机构管理器。提供各种访问组织机构相关信息的方法。
 * <p>
 * Title: ooder组织机构中间件
 * </p>
 * *
 * <p>
 * Copyright: Copyright (c) 2025
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 *
 * @author  ooder
 * @version 1.0
 */
public interface OrgWebManager {


    /**
     * 判断人员的用户名口令是否合法
     *
     * @param account  用户名或帐号
     * @param password 密码
     * @return true-通过验证, 反之返回false
     * <p>
     * 用户名或帐号不存在时抛出
     */
    @MethodChinaName(cname = "判断人员的用户名口令是否合法", returnStr = "verifyPerson($R('name'),$R('password'))")
    public ResultModel<Boolean> verifyPerson(String account, String password);


    /**
     * 取得所有的顶级机构
     *
     * @return 顶级机构的数组
     */
    @MethodChinaName(cname = "取得所有的顶级机构", display = false)
    public ResultModel<List<Org>> getTopOrgs(String sysId);


    /**
     * 根据机构的ID（标识）取得该机构的实例。
     *
     * @param orgID
     * @return 相应的组织机构实例
     */
    @MethodChinaName(cname = "根据机构的ID（标识）取得该机构的实例", returnStr = "getOrgByID($R('orgId'))")
    public ResultModel<Org> getOrgByID(String orgID);

    /**
     * 根据组织机构级别的标识取得组织机构级别
     *
     * @param orgLevelId 机构级别的标识
     * @return 相应的机构级别对象，如果不支持组织机构级别，返回空数组
     */
    @MethodChinaName(cname = "根据组织机构级别的标识取得组织机构级别", returnStr = "getOrgLevelByID($R('orgLevelId'))", display = false)
    public ResultModel<Role> getOrgLevelByID(String orgLevelId);

    /**
     * 根据组织机构级别的名称取得组织机构级别
     *
     * @param orgLevelName 组织机构级别的名称
     * @return 相应的组织机构级别，如果不支持组织机构级别，返回空数组
     */
    @MethodChinaName(cname = "根据组织机构级别的名称取得组织机构级别", returnStr = "getOrgLevelByName($R('orgLevelName'))")
    public ResultModel<Role> getOrgLevelByName(String orgLevelName, String sysId);

    /**
     * 取得所有的组织机构角色
     *
     * @return 所有的组织机构角色
     */
    @MethodChinaName(cname = "取得所有的组织机构角色", display = false)
    public ListResultModel<List<Role>> getOrgRoles(String sysId);

    /**
     * 取得所有组织机构
     *
     * @return 组织机构对象的数组
     */
    @MethodChinaName(cname = "取得所有组织机构", display = false)
    public ListResultModel<List<Org>> getOrgs(String sysId);

    @MethodChinaName(cname = "取得所有组织机构", display = false)
    public ListResultModel<List<String>> getOrgIds(String sysId);

    /**
     * 根据机构级别的标识取得所有机构对象
     *
     * @param levelId 机构级别的标识
     * @return 组织机构数组
     */
    @MethodChinaName(cname = "根据机构级别的标识取得所有机构对象", display = false)
    public ListResultModel<List<Org>> getOrgsByOrgLevelID(String levelId);

    /**
     * 根据机构级别的名称取得所有机构对象
     *
     * @param levelName 机构级别的的名称
     * @return 组织机构数组
     */
    @MethodChinaName(cname = "根据机构级别的名称取得所有机构对象", display = false)
    public ListResultModel<List<Org>> getOrgsByOrgLevelName(String levelName, String sysId);

    /**
     * 根据机构角色的标识取得所有机构对象
     *
     * @param roleId 机构角色的标识
     * @return 组织机构数组
     */
    @MethodChinaName(cname = "根据机构角色的标识取得所有机构对象", display = false)
    public ListResultModel<List<Org>> getOrgsByRoleID(String roleId);

    /**
     * 根据机构角色的名称取得所有机构对象
     *
     * @param roleName 机构角色的名称
     * @return 组织机构数组
     */
    @MethodChinaName(cname = "根据机构角色的名称取得所有机构对象", display = false)
    public ListResultModel<List<Org>> getOrgsByRoleName(String roleName, String sysId);

    /**
     * 根据人员的帐号取得该人员对象
     *
     * @param personAccount 人员的帐号
     * @return 人员对象
     */
    @MethodChinaName(cname = "根据人员的帐号取得该人员对象", returnStr = "getPersonByAccount($R('personAccount'))")
    public ResultModel<Person> getPersonByAccount(String personAccount);

    /**
     * 根据人员的标识取得该人员对象
     *
     * @param personId 人员的标识
     * @return 人员对象
     */
    @MethodChinaName(cname = "根据人员的ID取得该人员对象", returnStr = "getPersonByID($R('personId'))", display = false)
    public ResultModel<Person> getPersonByID(String personId);

    /**
     * 根据人员的手机号码取得该人员对象
     *
     * @param mobilenum 手机号码
     * @return 人员对象
     */
    @MethodChinaName(cname = "根据人员的手机号码取得该人员对象", returnStr = "getPersonByMobile($R('mobilenum'))", display = false)
    public ResultModel<Person> getPersonByMobile(String mobilenum);

    @MethodChinaName(cname = "根据人员Email得该人员对象", returnStr = "getPersonByEmail($R('email'))", display = false)
    public ResultModel<Person> getPersonByEmail(String email);

    /**
     * 取得所有的人员职务
     *
     * @return 所有人员职务的列表
     */
    @MethodChinaName(cname = "取得所有的人员职务", display = false)
    public ListResultModel<List<Role>> getPersonDuties(String sysId);

    /**
     * 根据人员职务的级别取得人员职务对象
     *
     * @return 相应的人员职务对象的数组
     */
    @MethodChinaName(cname = "根据人员职务的级别取得人员职务对象", display = false)
    public ListResultModel<List<Role>> getPersonDutiesByNum(String personDutyNumm, String sysId);

    /**
     * 根据人员职务的ID取得人员职务对象
     *
     * @param personDutyId 人员职务ID
     * @return 人员职务对象
     */
    @MethodChinaName(cname = " 根据人员职务的ID取得人员职务对象", returnStr = "getPersonDutyByID($R('personDutyId'))", display = false)
    public ResultModel<Role> getPersonDutyByID(String personDutyId);

    /**
     * 根据人员职务的名称取得人员职务对象
     *
     * @param personDutyName 人员职务的名称
     * @return 相应的人员职务对象
     */
    @MethodChinaName(cname = "根据人员职务的名称取得人员职务对象", returnStr = "getPersonDutyByName($R('personDutyName'))")
    public ResultModel<Role> getPersonDutyByName(String personDutyName, String sysId);

    /**
     * 根据用户组的代码取得用户组
     *
     * @param personGroupId 用户组的代码
     * @return 该用户组代码所对应的用户组
     */
    @MethodChinaName(cname = "根据用户组的代码取得用户组", returnStr = "getPersonGroupByID($R('personGroupId'))")
    public ResultModel<Role> getPersonGroupByID(String personGroupId);

    /**
     * 根据用户组的名称取得用户组
     *
     * @param personGroupName 用户组的名称
     * @return 该用户组名称所对应的用户组
     */
    @MethodChinaName(cname = "根据用户组的名称取得用户组", returnStr = "getPersonGroupByName($R('personGroupName'))", display = false)
    public ResultModel<Role> getPersonGroupByName(String personGroupName, String sysId);

    /**
     * 取得所有的用户组
     *
     * @return 所有的用户组的列表
     */
    @MethodChinaName(cname = "取得所有的用户组", display = false)
    public ListResultModel<List<Role>> getPersonGroups(String sysId);

    /**
     * 根据人员级别的标识取得人员级别
     *
     * @param personLevelId 人员级别的标识
     * @return 所对应的人员级别对象
     */
    @MethodChinaName(cname = "根据人员级别的标识取得人员级别", returnStr = "getPersonLevelByID($R('personLevelId'))", display = false)
    public ResultModel<Role> getPersonLevelByID(String personLevelId);

    /**
     * 根据人员职级的名称取得人员职级
     *
     * @param personLevelName 人员职级的名称
     * @return 人员职级对象
     */
    @MethodChinaName(cname = "根据人员职级的名称取得人员职级", returnStr = "getPersonLevelByName($R('personLevelName'))", display = false)
    public ResultModel<Role> getPersonLevelByName(String personLevelName, String sysId);

    /**
     * 取得所有的人员职级
     *
     * @return 所有的人员职级列表
     */
    @MethodChinaName(cname = "取得所有的人员职级", display = false)
    public ListResultModel<List<Role>> getPersonLevels(String sysId);

    /**
     * 根据人员职级的级别取得人员职级
     *
     * @param personLevelNum 人员职级的级别
     * @return 人员级别数组
     */
    @MethodChinaName(cname = "根据人员职级的级别取得人员职级", display = false)
    public ListResultModel<List<Role>> getPersonLevelsByNum(String personLevelNum, String sysId);

    /**
     * 根据人员岗位的代码取得人员岗位
     *
     * @param personPositionId 人员岗位的代码
     * @return 人员岗位对象
     */
    @MethodChinaName(cname = "根据人员岗位的代码取得人员岗位", returnStr = "getPersonPositionByID($R('personPositionId'))", display = false)
    public ResultModel<Role> getPersonPositionByID(String personPositionId);

    /**
     * 根据人员岗位的名称取得人员岗位
     *
     * @param personPositionName 人员岗位的名称
     * @return 人员岗位对象
     */
    @MethodChinaName(cname = "根据人员岗位的名称取得人员岗位", returnStr = "getPersonPositionByName($R('personPositionName'))")
    public ResultModel<Role> getPersonPositionByName(String personPositionName, String sysId);

    /**
     * 取得所有的人员岗位
     *
     * @return 人员岗位数组
     */
    @MethodChinaName(cname = "取得所有的人员岗位", display = false)
    public ListResultModel<List<Role>> getPersonPositions(String sysId);

    /**
     * 根据人员角色的代码取得人员角色
     *
     * @param personRoleId 人员角色的代码
     * @return 人员角色对象
     */
    @MethodChinaName(cname = "根据人员角色的代码取得人员角色", returnStr = "getPersonRoleByID($R('personRoleId'))", display = false)
    public ResultModel<Role> getPersonRoleByID(String personRoleId);

    /**
     * 根据人员角色的名称取得人员角色
     *
     * @param personRoleName 人员角色的名称
     * @return 人员角色对象
     */
    @MethodChinaName(cname = "根据人员角色的名称取得人员角色", returnStr = "getPersonRoleByName($R('personRoleName'))")
    public ResultModel<Role> getPersonRoleByName(String personRoleName, String sysId);

    /**
     * 取得所有的人员角色
     *
     * @return 人员角色数组
     */
    @MethodChinaName(cname = "取得所有的人员角色", display = false)
    public ListResultModel<List<Role>> getPersonRoles(String sysId);

    /**
     * 取得所有组织的所有人员
     *
     * @return 人员数组
     */
    @MethodChinaName(cname = "取得所有组织的所有人员", display = false)
    public ListResultModel<List<Person>> getPersons(String sysId);

    /**
     * 根据组织机构的ID取得该机构所有的人员
     *
     * @param orgId 组织机构的ID
     * @return 人员数组
     */
    @MethodChinaName(cname = "根据组织机构的ID取得该机构所有的人员", display = false)
    public ListResultModel<List<Person>> getPersonsByOrgID(String orgId);

    /**
     * 根据人员职务的标识取得该职务所对应的所有人员
     *
     * @param personDutyId 指定的人员职务标识
     * @return 人员数组
     */
    @MethodChinaName(cname = "根据人员职务的标识取得该职务所对应的所有人员", display = false)
    public ListResultModel<List<Person>> getPersonsByPersonDutyID(String personDutyId);

    /**
     * 根据人员职务的名称取得该职务所对应的所有人员
     *
     * @param personDutyName 指定的人员职务名称
     * @return 人员数组
     */
    @MethodChinaName(cname = "根据人员职务的名称取得该职务所对应的所有人员", display = false)
    public ListResultModel<List<Person>> getPersonsByPersonDutyName(String personDutyName, String sysId);

    /**
     * 根据工作组的标识取得该工作组所对应的所有人员
     *
     * @param personGroupId 指定的工作组标识
     * @return 人员数组
     */
    @MethodChinaName(cname = "根据工作组的标识取得该工作组所对应的所有人员", display = false)
    public ListResultModel<List<Person>> getPersonsByPersonGroupID(String personGroupId);

    /**
     * 根据工作组的名称取得该工作组所对应的所有人员
     *
     * @param personGroupName 指定的工作组名称
     * @return 人员数组
     */
    @MethodChinaName(cname = "根据工作组的名称取得该工作组所对应的所有人员", display = false)
    public ListResultModel<List<Person>> getPersonsByPersonGroupName(String personGroupName, String sysId);

    /**
     * 根据人员级别的标识取得该级别所对应的所有人员
     *
     * @param personLevelId 指定的人员级别标识
     * @return 人员数组
     */
    @MethodChinaName(cname = "根据工作组的名称取得该工作组所对应的所有人员", display = false)
    public ListResultModel<List<Person>> getPersonsByPersonLevelID(String personLevelId);

    /**
     * 根据人员级别的名称取得该级别所对应的所有人员
     *
     * @param personLevelName 指定的人员级别名称
     * @return 人员数组
     */
    @MethodChinaName(cname = "根据人员级别的名称取得该级别所对应的所有人员", display = false)
    public ListResultModel<List<Person>> getPersonsByPersonLevelName(String personLevelName, String sysId);

    /**
     * 根据人员岗位的标识取得该岗位所对应的所有人员
     *
     * @param personPositionId 指定的人员岗位标识
     * @return 人员数组
     */
    @MethodChinaName(cname = "根据人员岗位的标识取得该岗位所对应的所有人员", display = false)
    public ListResultModel<List<Person>> getPersonsByPersonPositionID(String personPositionId);

    /**
     * 根据人员岗位的名称取得该岗位所对应的所有人员
     *
     * @param personPositionName 指定的人员岗位名称
     * @return 人员数组
     */
    @MethodChinaName(cname = "根据人员岗位的名称取得该岗位所对应的所有人员", display = false)
    public ListResultModel<List<Person>> getPersonsByPersonPositionName(String personPositionName, String sysId);

    /**
     * 根据人员角色的标识取得该角色所对应的所有人员
     *
     * @param personRoleId 指定的人员角色标识
     * @return 人员数组
     */
    @MethodChinaName(cname = "根据人员角色的标识取得该角色所对应的所有人员", display = false)
    public ListResultModel<List<Person>> getPersonsByPersonRoleID(String personRoleId);

    /**
     * 根据人员角色的名称取得该角色所对应的所有人员
     *
     * @param personRoleName 指定的人员角色名称
     * @return 人员数组
     */
    @MethodChinaName(cname = "根据人员角色的名称取得该角色所对应的所有人员", display = false)
    public ListResultModel<List<Person>> getPersonsByPersonRoleName(String personRoleName, String sysId);

    /**
     * 是否支持组织机构级别
     *
     * @return true-支持，false-不支持。
     */
    @MethodChinaName(cname = "是否支持组织机构级别", display = false)
    public ResultModel<Boolean> isSupportOrgLevel(String sysId);

    /**
     * 是否支持组织机构角色
     *
     * @return true-支持，false-不支持。
     */
    @MethodChinaName(cname = "是否支持组织机构角色", display = false)
    public ResultModel<Boolean> isSupportRole(String sysId);

    /**
     * 是否支持人员职务
     *
     * @return true-支持，false-不支持。
     */
    @MethodChinaName(cname = "是否支持人员职务", display = false)
    public ResultModel<Boolean> isSupportPersonDuty(String sysId);

    /**
     * 是否支持用户组
     *
     * @return true-支持，false-不支持。
     */
    @MethodChinaName(cname = "是否支持用户组", display = false)
    public ResultModel<Boolean> isSupportPersonGroup(String sysId);

    /**
     * 是否支持人员职级
     *
     * @return true-支持，false-不支持。
     */
    @MethodChinaName(cname = "是否支持人员职级", display = false)
    public ResultModel<Boolean> isSupportPersonLevel(String sysId);

    /**
     * 是否人员岗位
     *
     * @return true-支持，false-不支持。
     */
    @MethodChinaName(cname = "是否支持人员岗位", display = false)
    public ResultModel<Boolean> isSupportPersonPosition(String sysId);

    /**
     * 是否支持人员角色
     *
     * @return true-支持，false-不支持。
     */
    @MethodChinaName(cname = "是否支持人员角色", display = false)
    public ResultModel<Boolean> isSupportPersonRole(String sysId);

    /**
     * 根据用户私有组的代码取得私有用户组
     *
     * @param personGroupId 用户组的代码
     * @return 该私有用户组代码所对应的私有用户组
     */
    @MethodChinaName(cname = "根据私有用户组的代码取得用户组", returnStr = "getPrivateGroupById($R('personGroupId'))", display = false)
    public ResultModel<PersonPrivateGroup> getPrivateGroupById(String personGroupId);


    /**
     * @return
     */
    public ResultModel<Boolean> isSupportOrgRole(String sysId);

    /**
     * @param roleName
     * @return
     */
    public ListResultModel<List<Org>> getOrgsByOrgRoleName(String roleName, String sysId);

    /**
     * @param roleId
     * @return
     */
    public ListResultModel<List<Org>> getOrgsByOrgRoleID(String roleId);

    /**
     * @param orgRoleName
     * @return
     */
    public ResultModel<Role> getOrgRoleByName(String orgRoleName, String sysId);

    /**
     * @param orgRoleId
     * @return
     */
    public ResultModel<Role> getOrgRoleByID(String orgRoleId);

    /**
     * @return
     */
    public ListResultModel<List<Role>> getOrgLevels(String sysId);


    /**
     * @param orgLevelNum
     * @return
     */
    public ListResultModel<List<Role>> getOrgLevelsByNum(String orgLevelNum, String sysId);


    /**
     * @param roleId
     * @return
     */
    public ResultModel<Role> getRoleByID(String roleId);

    /**
     * @param type
     * @param roleName
     * @return
     */
    public ResultModel<Role> getRoleByName(RoleType type, String roleName, String sysId);

    public ResultModel<Boolean> reLoadAll();

    ;

    public ResultModel<Person> registerPerson(String accountName, String enName, String sysId);


    public ListResultModel<List<Role>> getAllRoles(String sysId);
}