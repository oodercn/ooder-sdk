package net.ooder.common.org.service;

import net.ooder.common.org.CtOrg;
import net.ooder.common.org.CtPerson;
import net.ooder.common.org.CtRole;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;
import net.ooder.org.Org;
import net.ooder.org.Person;
import net.ooder.org.Role;
import net.ooder.org.query.OrgCondition;

import java.util.List;

public interface OrgAdminService {


    /**
     * @param condition
     * @return
     */
    public ListResultModel<List<Org>> findOrgs(OrgCondition condition);


    /**
     * @return
     */
    public ListResultModel<List<Org>> getAllTopOrgs();


    /**
     * @param condition
     * @return
     */
    public ListResultModel<List<Person>> findPersons(OrgCondition condition);


    /**
     * @param condition
     * @return
     */
    public ListResultModel<List<Role>> findRoles(OrgCondition condition);

    /**
     * @param person
     * @return
     */
    public ResultModel<Boolean> savePerson(CtPerson person);

    /**
     * @param personId
     * @param orgId
     * @return
     */
    public ResultModel<Boolean> addPerson2Org(String personId, String orgId);

    /**
     * @param org
     * @return
     */
    public ResultModel<Boolean> saveOrg(CtOrg org);


    /**
     * @param role
     * @return
     */
    public ResultModel<Boolean> saveRole(CtRole role);

    /**
     * @param personId
     * @param roleId
     * @return
     */
    public ResultModel<Boolean> addPerson2Role(String personId, String roleId);

    /**
     * @param personId
     * @param roleId
     * @return
     */
    public ResultModel<Boolean> removePerson2Role(String personId, String roleId);


    /**
     * @param personId
     * @param roleId
     * @return
     */
    public ResultModel<Boolean> addOrg2Role(String personId, String roleId);

    /**
     * @param personId
     * @param roleId
     * @return
     */
    public ResultModel<Boolean> removeOrg2Role(String personId, String roleId);


    /**
     * @param personId
     * @param orgId
     * @return
     */
    public ResultModel<Boolean> removePerson2Org(String personId, String orgId);

    /**
     * @param orgId
     * @return
     */
    public ResultModel<Boolean> delOrg(String orgId);

    /**
     * @return
     */
    public ResultModel<Boolean> delRole(String roleId);

    /**
     * @param personId
     * @return
     */
    public ResultModel<Boolean> delPerson(String personId);


}
