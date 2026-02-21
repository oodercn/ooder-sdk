/**
 * $RCSfile: GetAllOrgs.java,v $
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

import java.util.List;

import net.ooder.common.expression.ParseException;
import net.ooder.common.expression.function.AbstractFunction;
import net.ooder.org.Org;
import net.ooder.server.JDSClientService;
import net.ooder.server.OrgManagerFactory;

public class GetAllOrgs extends AbstractFunction {

    public List<Org> perform(JDSClientService client) throws ParseException {

	List<Org> objs = OrgManagerFactory.getOrgManager(client.getConfigCode()).getOrgs();

	return objs;
    }
}