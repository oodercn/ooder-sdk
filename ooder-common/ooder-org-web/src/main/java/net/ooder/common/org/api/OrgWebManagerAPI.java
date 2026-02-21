package net.ooder.common.org.api;

import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.annotation.MethodChinaName;
import net.ooder.annotation.RoleType;
import net.ooder.common.ContextType;
import net.ooder.common.org.service.OrgWebManager;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.msg.PersonPrivateGroup;
import net.ooder.org.Org;
import net.ooder.org.Person;
import net.ooder.org.Role;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/api/org/orgManager/")
@MethodChinaName(cname = "系统权限表达式接口")
@EsbBeanAnnotation(dataType = ContextType.Server)
public class OrgWebManagerAPI implements OrgWebManager {

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "verifyPerson")
    @MethodChinaName(cname = "验证用户信息")
    public @ResponseBody
    ResultModel<Boolean> verifyPerson(String account, String password) {
        return this.getOrgWebManager().verifyPerson(account, password);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getTopOrgs")
    @MethodChinaName(cname = "获取顶级组织")
    public @ResponseBody
    ResultModel<List<Org>> getTopOrgs(String sysId) {
        return this.getOrgWebManager().getTopOrgs(sysId);
    }


    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getOrgByID")
    @MethodChinaName(cname = "获取部门信息")
    public @ResponseBody
    ResultModel<Org> getOrgByID(String orgID) {
        return this.getOrgWebManager().getOrgByID(orgID);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getOrgLevelByID")
    @MethodChinaName(cname = "获取部门级别")
    public @ResponseBody
    ResultModel<Role> getOrgLevelByID(String orgLevelId) {
        return this.getOrgWebManager().getOrgLevelByID(orgLevelId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getOrgLevelByName")
    @MethodChinaName(cname = "获取部门职能")
    public @ResponseBody
    ResultModel<Role> getOrgLevelByName(String orgLevelName, String sysId) {
        return this.getOrgWebManager().getOrgLevelByName(orgLevelName, sysId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getOrgRoles")
    @MethodChinaName(cname = "获取部门角色")
    public @ResponseBody
    ListResultModel<List<Role>> getOrgRoles(String sysId) {
        return this.getOrgWebManager().getOrgRoles(sysId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getOrgs")
    @MethodChinaName(cname = "装载部门信息")
    public @ResponseBody
    ListResultModel<List<Org>> getOrgs(String sysId) {
        return this.getOrgWebManager().getOrgs(sysId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getOrgIds")
    @MethodChinaName(cname = "获取部门ID")
    public @ResponseBody
    ListResultModel<List<String>> getOrgIds(String sysId) {
        return this.getOrgWebManager().getOrgIds(sysId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getOrgsByOrgLevelID")
    @MethodChinaName(cname = "根据级别获取部门")
    public @ResponseBody
    ListResultModel<List<Org>> getOrgsByOrgLevelID(String levelId) {
        return this.getOrgWebManager().getOrgsByOrgLevelID(levelId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getOrgsByOrgLevelName")
    @MethodChinaName(cname = "根据级别名称获取部门")
    public @ResponseBody
    ListResultModel<List<Org>> getOrgsByOrgLevelName(String levelName, String sysId) {
        return this.getOrgWebManager().getOrgsByOrgLevelName(levelName, sysId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getOrgsByRoleID")
    @MethodChinaName(cname = "根据角色ID称获取部门")
    public @ResponseBody
    ListResultModel<List<Org>> getOrgsByRoleID(String roleId) {
        return this.getOrgWebManager().getOrgsByOrgRoleID(roleId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getOrgsByRoleName")
    @MethodChinaName(cname = "根据角色名称获取部门")
    public @ResponseBody
    ListResultModel<List<Org>> getOrgsByRoleName(String roleName, String sysId) {
        return this.getOrgWebManager().getOrgsByRoleName(roleName, sysId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getPersonByAccount")
    @MethodChinaName(cname = "根据账号获取用户")
    public @ResponseBody
    ResultModel<Person> getPersonByAccount(String personAccount) {
        return this.getOrgWebManager().getPersonByAccount(personAccount);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getPersonByID")
    @MethodChinaName(cname = "根据ID获取用户")
    public @ResponseBody
    ResultModel<Person> getPersonByID(String personId) {
        return this.getOrgWebManager().getPersonByID(personId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getPersonByMobile")
    @MethodChinaName(cname = "根据手机号码获取用户")
    public @ResponseBody
    ResultModel<Person> getPersonByMobile(String mobilenum) {
        return this.getOrgWebManager().getPersonByMobile(mobilenum);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getPersonByEmail")
    @MethodChinaName(cname = "根据邮箱获取用户")
    public @ResponseBody
    ResultModel<Person> getPersonByEmail(String email) {
        return this.getOrgWebManager().getPersonByEmail(email);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getPersonDuties")
    @MethodChinaName(cname = "根据职位获取用户")
    public @ResponseBody
    ListResultModel<List<Role>> getPersonDuties(String sysId) {
        return this.getOrgWebManager().getPersonDuties(sysId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getPersonDutiesByNum")
    @MethodChinaName(cname = "根据职位号获取用户")
    public @ResponseBody
    ListResultModel<List<Role>> getPersonDutiesByNum(String personDutyNum, String sysId) {
        return this.getOrgWebManager().getPersonDutiesByNum(personDutyNum, sysId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getPersonDutyByID")
    @MethodChinaName(cname = "根据职位ID获取用户")
    public @ResponseBody
    ResultModel<Role> getPersonDutyByID(String personDutyId) {
        return this.getOrgWebManager().getPersonDutyByID(personDutyId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getPersonDutyByName")
    @MethodChinaName(cname = "根据职位名称获取用户")
    public @ResponseBody
    ResultModel<Role> getPersonDutyByName(String personDutyName, String sysId) {
        return this.getOrgWebManager().getPersonDutyByName(personDutyName, sysId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getPersonGroupByID")
    @MethodChinaName(cname = "获取用户组")
    public @ResponseBody
    ResultModel<Role> getPersonGroupByID(String personGroupId) {
        return this.getOrgWebManager().getPersonGroupByID(personGroupId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getPersonGroupByName")
    @MethodChinaName(cname = "根据组名称获取用户组")
    public @ResponseBody
    ResultModel<Role> getPersonGroupByName(String personGroupName, String sysId) {
        return this.getOrgWebManager().getPersonGroupByName(personGroupName, sysId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getPersonGroups")
    @MethodChinaName(cname = "装置用户组")
    public @ResponseBody
    ListResultModel<List<Role>> getPersonGroups(String sysId) {
        return this.getOrgWebManager().getPersonGroups(sysId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getPersonLevelByID")
    @MethodChinaName(cname = "获取级别")
    public @ResponseBody
    ResultModel<Role> getPersonLevelByID(String personLevelId) {
        return this.getOrgWebManager().getPersonLevelByID(personLevelId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getPersonLevelByName")
    @MethodChinaName(cname = "根据名称获取级别")
    public @ResponseBody
    ResultModel<Role> getPersonLevelByName(String personLevelName, String sysId) {
        return this.getOrgWebManager().getPersonLevelByName(personLevelName, sysId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getPersonLevels")
    @MethodChinaName(cname = "缓存人员级别")
    public @ResponseBody
    ListResultModel<List<Role>> getPersonLevels(String sysId) {
        return this.getOrgWebManager().getPersonLevels(sysId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getPersonLevelsByNum")
    @MethodChinaName(cname = "根据级别代码获取")
    public @ResponseBody
    ListResultModel<List<Role>> getPersonLevelsByNum(String personLevelNum, String sysId) {
        return this.getOrgWebManager().getPersonLevelsByNum(personLevelNum, sysId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getPersonPositionByID")
    @MethodChinaName(cname = "获取岗位信息")
    public @ResponseBody
    ResultModel<Role> getPersonPositionByID(String personPositionId) {
        return this.getOrgWebManager().getPersonPositionByID(personPositionId);
    }

    @Override
    @MethodChinaName(cname = "根据岗位名称获取岗位")
    @RequestMapping(method = RequestMethod.POST, value = "getPersonPositionByName")
    public @ResponseBody
    ResultModel<Role> getPersonPositionByName(String personPositionName, String sysId) {
        return this.getOrgWebManager().getPersonPositionByName(personPositionName, sysId);
    }

    @Override
    @MethodChinaName(cname = "缓存岗位信息")
    @RequestMapping(method = RequestMethod.POST, value = "getPersonPositions")
    public @ResponseBody
    ListResultModel<List<Role>> getPersonPositions(String sysId) {
        return this.getOrgWebManager().getPersonPositions(sysId);
    }

    @Override
    @MethodChinaName(cname = "获取角色信息")
    @RequestMapping(method = RequestMethod.POST, value = "getPersonRoleByID")
    public @ResponseBody
    ResultModel<Role> getPersonRoleByID(String personRoleId) {
        return this.getOrgWebManager().getPersonRoleByID(personRoleId);
    }

    @Override
    @MethodChinaName(cname = "根据角色信息获取")
    @RequestMapping(method = RequestMethod.POST, value = "getPersonRoleByName")
    public @ResponseBody
    ResultModel<Role> getPersonRoleByName(String personRoleName, String sysId) {
        return this.getOrgWebManager().getPersonRoleByName(personRoleName, sysId);
    }

    @Override
    @MethodChinaName(cname = "缓存角色信息")
    @RequestMapping(method = RequestMethod.POST, value = "getPersonRoles")
    public @ResponseBody
    ListResultModel<List<Role>> getPersonRoles(String sysId) {
        return this.getOrgWebManager().getPersonRoles(sysId);
    }

    @Override
    @MethodChinaName(cname = "获取人员")
    @RequestMapping(method = RequestMethod.POST, value = "getPersons")
    public @ResponseBody
    ListResultModel<List<Person>> getPersons(String sysId) {
        return this.getOrgWebManager().getPersons(sysId);
    }

    @Override
    @MethodChinaName(cname = "获取部门人员")
    @RequestMapping(method = RequestMethod.POST, value = "getPersonsByOrgID")
    public @ResponseBody
    ListResultModel<List<Person>> getPersonsByOrgID(String orgId) {
        return this.getOrgWebManager().getPersonsByOrgID(orgId);
    }

    @Override
    @MethodChinaName(cname = "获取指定职位人员")
    @RequestMapping(method = RequestMethod.POST, value = "getPersonsByPersonDutyID")
    public @ResponseBody
    ListResultModel<List<Person>> getPersonsByPersonDutyID(String personDutyId) {
        return this.getOrgWebManager().getPersonsByPersonDutyID(personDutyId);
    }

    @Override
    @MethodChinaName(cname = "根据职位名称获取用户")
    @RequestMapping(method = RequestMethod.POST, value = "getPersonsByPersonDutyName")
    public @ResponseBody
    ListResultModel<List<Person>> getPersonsByPersonDutyName(String personDutyName, String sysId) {
        return this.getOrgWebManager().getPersonsByPersonDutyName(personDutyName, sysId);
    }

    @Override
    @MethodChinaName(cname = "根据人员组获取用户")
    @RequestMapping(method = RequestMethod.POST, value = "getPersonsByPersonGroupID")
    public @ResponseBody
    ListResultModel<List<Person>> getPersonsByPersonGroupID(String personGroupId) {
        return this.getOrgWebManager().getPersonsByPersonGroupID(personGroupId);
    }

    @Override
    @MethodChinaName(cname = "根据组名称获取人员")
    @RequestMapping(method = RequestMethod.POST, value = "getPersonsByPersonGroupName")
    public @ResponseBody
    ListResultModel<List<Person>> getPersonsByPersonGroupName(String personGroupName, String sysId) {
        return this.getOrgWebManager().getPersonsByPersonGroupName(personGroupName, sysId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getPersonsByPersonLevelID")
    @MethodChinaName(cname = "根据人员级别获取人员")
    public @ResponseBody
    ListResultModel<List<Person>> getPersonsByPersonLevelID(String personLevelId) {
        return this.getOrgWebManager().getPersonsByPersonLevelID(personLevelId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getPersonsByPersonLevelName")
    @MethodChinaName(cname = "根据人员级别名称获取人员")
    public @ResponseBody
    ListResultModel<List<Person>> getPersonsByPersonLevelName(String personLevelName, String sysId) {
        return this.getOrgWebManager().getPersonsByPersonLevelName(personLevelName, sysId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getPersonsByPersonPositionID")
    @MethodChinaName(cname = "根据人员岗位ID获取人员")
    public @ResponseBody
    ListResultModel<List<Person>> getPersonsByPersonPositionID(String personPositionId) {
        return this.getOrgWebManager().getPersonsByPersonPositionID(personPositionId);
    }

    @Override
    @MethodChinaName(cname = "根据人员岗位名称获取人员")
    @RequestMapping(method = RequestMethod.POST, value = "getPersonsByPersonPositionName")
    public @ResponseBody
    ListResultModel<List<Person>> getPersonsByPersonPositionName(String personPositionName, String sysId) {
        return this.getOrgWebManager().getPersonsByPersonPositionName(personPositionName, sysId);
    }

    @Override
    @MethodChinaName(cname = "根据人员角色ID获取人员")
    @RequestMapping(method = RequestMethod.POST, value = "getPersonsByPersonRoleID")
    public @ResponseBody
    ListResultModel<List<Person>> getPersonsByPersonRoleID(String personRoleId) {
        return this.getOrgWebManager().getPersonsByPersonRoleID(personRoleId);
    }

    @Override
    @MethodChinaName(cname = "根据人员角色名称获取人员")
    @RequestMapping(method = RequestMethod.POST, value = "getPersonsByPersonRoleName")
    public @ResponseBody
    ListResultModel<List<Person>> getPersonsByPersonRoleName(String personRoleName, String sysId) {
        return this.getOrgWebManager().getPersonsByPersonRoleName(personRoleName, sysId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "isSupportOrgLevel")
    @MethodChinaName(cname = "是否支持部门级别")
    public @ResponseBody
    ResultModel<Boolean> isSupportOrgLevel(String sysId) {
        return this.getOrgWebManager().isSupportOrgLevel(sysId);
    }

    @Override
    @MethodChinaName(cname = "是否支持角色")
    @RequestMapping(method = RequestMethod.POST, value = "isSupportRole")
    public @ResponseBody
    ResultModel<Boolean> isSupportRole(String sysId) {
        return this.getOrgWebManager().isSupportRole(sysId);
    }

    @Override
    @MethodChinaName(cname = "是否支持职位")
    @RequestMapping(method = RequestMethod.POST, value = "isSupportPersonDuty")
    public @ResponseBody
    ResultModel<Boolean> isSupportPersonDuty(String sysId) {
        return this.getOrgWebManager().isSupportPersonDuty(sysId);
    }

    @Override
    @MethodChinaName(cname = "是否支持用户组")
    @RequestMapping(method = RequestMethod.POST, value = "isSupportPersonGroup")
    public @ResponseBody
    ResultModel<Boolean> isSupportPersonGroup(String sysId) {
        return this.getOrgWebManager().isSupportPersonGroup(sysId);
    }

    @Override
    @MethodChinaName(cname = "是否支持人员级别")
    @RequestMapping(method = RequestMethod.POST, value = "isSupportPersonLevel")
    public @ResponseBody
    ResultModel<Boolean> isSupportPersonLevel(String sysId) {
        return this.getOrgWebManager().isSupportPersonLevel(sysId);
    }

    @Override
    @MethodChinaName(cname = "是否支持人员岗位")
    @RequestMapping(method = RequestMethod.POST, value = "isSupportPersonPosition")
    public @ResponseBody
    ResultModel<Boolean> isSupportPersonPosition(String sysId) {
        return this.getOrgWebManager().isSupportPersonPosition(sysId);
    }

    @Override
    @MethodChinaName(cname = "是否支持人员角色")
    @RequestMapping(method = RequestMethod.POST, value = "isSupportPersonRole")
    public @ResponseBody
    ResultModel<Boolean> isSupportPersonRole(String sysId) {
        return this.getOrgWebManager().isSupportPersonRole(sysId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getPrivateGroupById")
    @MethodChinaName(cname = "自定义朋友圈")
    public @ResponseBody
    ResultModel<PersonPrivateGroup> getPrivateGroupById(String personGroupId) {
        return this.getOrgWebManager().getPrivateGroupById(personGroupId);
    }


    @Override
    @RequestMapping(method = RequestMethod.POST, value = "isSupportOrgRole")
    @MethodChinaName(cname = "是否支持部门角色")
    public @ResponseBody
    ResultModel<Boolean> isSupportOrgRole(String sysId) {
        return this.getOrgWebManager().isSupportOrgRole(sysId);
    }

    @Override
    @MethodChinaName(cname = "根据部门角色名称获取部门")
    @RequestMapping(method = RequestMethod.POST, value = "getOrgsByOrgRoleName")
    public @ResponseBody
    ListResultModel<List<Org>> getOrgsByOrgRoleName(String roleName, String sysId) {
        return this.getOrgWebManager().getOrgsByOrgRoleName(roleName, sysId);
    }

    @Override
    @MethodChinaName(cname = "根据部门角色ID获取部门")
    @RequestMapping(method = RequestMethod.POST, value = "getOrgsByOrgRoleID")
    public @ResponseBody
    ListResultModel<List<Org>> getOrgsByOrgRoleID(String roleId) {
        return this.getOrgWebManager().getOrgsByOrgRoleID(roleId);
    }

    @Override
    @MethodChinaName(cname = "根据部门角色名称获取角色")
    @RequestMapping(method = RequestMethod.POST, value = "getOrgRoleByName")
    public @ResponseBody
    ResultModel<Role> getOrgRoleByName(String orgRoleName, String sysId) {
        return this.getOrgWebManager().getOrgRoleByName(orgRoleName, sysId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getOrgRoleByID")
    @MethodChinaName(cname = "根据部门角色ID获取角色")
    public @ResponseBody
    ResultModel<Role> getOrgRoleByID(String orgRoleId) {
        return this.getOrgWebManager().getOrgRoleByID(orgRoleId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getOrgLevels")
    @MethodChinaName(cname = "缓存部门级别")
    public @ResponseBody
    ListResultModel<List<Role>> getOrgLevels(String sysId) {
        return this.getOrgWebManager().getOrgLevels(sysId);
    }


    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getOrgLevelsByNum")
    @MethodChinaName(cname = "读取指定级别部门")
    public ListResultModel<List<Role>> getOrgLevelsByNum(String orgLevelNum, String sysId) {
        return this.getOrgWebManager().getOrgLevelsByNum(orgLevelNum, sysId);
    }


    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getRoleByID")
    @MethodChinaName(cname = "获取角色")
    public @ResponseBody
    ResultModel<Role> getRoleByID(String roleId) {
        return this.getOrgWebManager().getRoleByID(roleId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getRoleByName")
    @MethodChinaName(cname = "获取指定角色")
    public @ResponseBody
    ResultModel<Role> getRoleByName(RoleType type, String roleName, String sysId) {
        return this.getOrgWebManager().getRoleByName(type, roleName, sysId);
    }


    @Override
    @RequestMapping(method = RequestMethod.POST, value = "reLoadAll")
    @MethodChinaName(cname = "集群刷新")
    public @ResponseBody
    ResultModel<Boolean> reLoadAll() {
        return this.getOrgWebManager().reLoadAll();
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "registerPerson")
    @MethodChinaName(cname = "注册新用户")
    public @ResponseBody
    ResultModel<Person> registerPerson(String accountName, String enName, String systemCode) {
        return this.getOrgWebManager().registerPerson(accountName, enName, systemCode);
    }

    @RequestMapping(method = RequestMethod.POST, value = "getAllRoles")
    public @ResponseBody
    ListResultModel<List<Role>> getAllRoles(String sysId) {
        return this.getOrgWebManager().getAllRoles(sysId);
    }

    OrgWebManager getOrgWebManager() {
        return (OrgWebManager) EsbUtil.parExpression(OrgWebManager.class);
    }
}
