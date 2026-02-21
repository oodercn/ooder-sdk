/**
 * $RCSfile: GetCurrPerson.java,v $
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
package net.ooder.service.org;

import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.common.expression.ParseException;
import net.ooder.common.expression.function.AbstractFunction;
import net.ooder.org.Person;
import net.ooder.server.JDSClientService;
import net.ooder.server.JDSServer;
import net.ooder.server.OrgManagerFactory;

@EsbBeanAnnotation(expressionArr = "currPerson(JDSC())", id = "currPerson", desc = "获取当前用户")
public class GetCurrPerson extends AbstractFunction {


    public Person perform(JDSClientService client) throws ParseException {
        Person person = null;

        try {
            if (client == null || client.getConnectInfo() == null) {
                person = OrgManagerFactory.getOrgManager().getPersonByID(JDSServer.getInstance().getAdminUser().getId());
            } else {
                person = OrgManagerFactory.getOrgManager(client.getConfigCode()).getPersonByID(client.getConnectInfo().getUserID());
            }

        } catch (Exception e) {
            throw new ParseException(e);
        }
        if (person == null) {
            throw new ParseException("session is null");
        }

        return person;
    }
}