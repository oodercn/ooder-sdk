package net.ooder.common.org.api;

import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.common.ContextType;
import net.ooder.common.org.CtOrg;
import net.ooder.common.org.CtPerson;
import net.ooder.common.org.CtRole;
import net.ooder.common.org.service.OrgAdminService;
import net.ooder.annotation.MethodChinaName;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.org.Org;
import net.ooder.org.Person;
import net.ooder.org.Role;
import net.ooder.org.query.OrgCondition;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/api/org/admin/")
@MethodChinaName(cname = "团队管理服务")
@EsbBeanAnnotation(dataType = ContextType.Server)
public class OrgAdminManagerAPI implements OrgAdminService {


    OrgAdminService getOrgAdminService() {
        return (OrgAdminService) EsbUtil.parExpression(OrgAdminService.class);
    }


    @Override
    @RequestMapping(method = RequestMethod.POST, value = "findOrgs")
    @MethodChinaName(cname = "查询部门信息")
    @ResponseBody
    public ListResultModel<List<Org>> findOrgs(@RequestBody OrgCondition condition) {
        return this.getOrgAdminService().findOrgs(condition);
    }

    @RequestMapping(method = RequestMethod.POST, value = "getAllTopOrgs")
    @MethodChinaName(cname = "获取所有顶级组织")
    @ResponseBody
    public ListResultModel<List<Org>> getAllTopOrgs() {
        return this.getOrgAdminService().getAllTopOrgs();
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "findPersons")
    @MethodChinaName(cname = "查询成员")
    @ResponseBody
    public ListResultModel<List<Person>> findPersons(@RequestBody OrgCondition condition) {
        return this.getOrgAdminService().findPersons(condition);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "findRoles")
    @MethodChinaName(cname = "查询角色")
    @ResponseBody
    public ListResultModel<List<Role>> findRoles(@RequestBody OrgCondition condition) {
        return this.getOrgAdminService().findRoles(condition);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "savePerson")
    @MethodChinaName(cname = "更新人员信息")
    @ResponseBody
    public ResultModel<Boolean> savePerson(@RequestBody CtPerson person) {
        ResultModel<Boolean> resultModel = this.getOrgAdminService().savePerson(person);
        return resultModel;
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "addPerson2Org")
    @ResponseBody
    public ResultModel<Boolean> addPerson2Org(String personId, String orgId) {

        return this.getOrgAdminService().addPerson2Org(personId, orgId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "saveOrg")
    @ResponseBody
    public ResultModel<Boolean> saveOrg(@RequestBody CtOrg org) {

        return this.getOrgAdminService().saveOrg(org);
    }


    @Override
    @RequestMapping(method = RequestMethod.POST, value = "saveRole")
    @ResponseBody
    public ResultModel<Boolean> saveRole(@RequestBody CtRole role) {

        return this.getOrgAdminService().saveRole(role);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "addPerson2Role")
    @MethodChinaName(cname = "添加人员角色信息")
    @ResponseBody
    public ResultModel<Boolean> addPerson2Role(String personId, String roleId) {

        return this.getOrgAdminService().addPerson2Role(personId, roleId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "removePerson2Role")
    @MethodChinaName(cname = "移除人员角色信息")
    @ResponseBody
    public ResultModel<Boolean> removePerson2Role(String roleId, String personId) {

        return this.getOrgAdminService().removePerson2Role(roleId, personId);
    }


    @Override
    @RequestMapping(method = RequestMethod.POST, value = "addOrg2Role")
    @MethodChinaName(cname = "添加部门角色信息")
    @ResponseBody
    public ResultModel<Boolean> addOrg2Role(String orgId, String roleId) {

        return this.getOrgAdminService().addOrg2Role(orgId, roleId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "removeOrg2Role")
    @MethodChinaName(cname = "移除部门角色信息")
    @ResponseBody
    public ResultModel<Boolean> removeOrg2Role(String roleId, String orgId) {

        return this.getOrgAdminService().removeOrg2Role(roleId, orgId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "removePerson2Org")
    @MethodChinaName(cname = "移除人员组织信息")
    @ResponseBody
    public ResultModel<Boolean> removePerson2Org(String personId, String orgId) {

        return this.getOrgAdminService().removePerson2Org(personId, orgId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "delOrg")
    @MethodChinaName(cname = "移除组织信息")
    @ResponseBody
    public ResultModel<Boolean> delOrg(String orgId) {

        return this.getOrgAdminService().delOrg(orgId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "delRole")
    @MethodChinaName(cname = "移除角色")
    @ResponseBody
    public ResultModel<Boolean> delRole(String roleId) {

        return this.getOrgAdminService().delRole(roleId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "delPerson")
    @MethodChinaName(cname = "移除人员信息")
    @ResponseBody
    public ResultModel<Boolean> delPerson(String personId) {

        return this.getOrgAdminService().delPerson(personId);
    }
}
