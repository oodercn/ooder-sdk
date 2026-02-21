package net.ooder.common.org;

import net.ooder.common.org.service.OrgWebManager;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.org.Org;
import net.ooder.org.OrgNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CtOrgFactory {

    private static Org loadFromServer(String value) throws InterruptedException, ExecutionException {
        if (value == null) {
            new Exception("loadFromServer OrgId is null ");
        }
        synchronized (value) {
            OrgWebManager orgWebManager = (OrgWebManager) EsbUtil.parExpression(OrgWebManager.class);
            Org org = orgWebManager.getOrgByID(value).get();
            if (org instanceof CtOrg) {
                return org;
            }
            CtOrg ctOrg = toClient(org);
            return ctOrg;
        }

    }

    /**
     * @param wsOrg
     * @return
     */
    public static CtOrg toClient(Org wsOrg) {
        if (wsOrg == null) {
            return null;
        }
        if (wsOrg instanceof CtOrg) {
            return (CtOrg) wsOrg;
        }
        CtOrg org = new CtOrg(wsOrg);

        return org;
    }

    public static List<Org> toClient(List<Org> wsOrgs) {

        List<Org> orgList = new ArrayList<Org>();

        if (wsOrgs != null) {

            for (Org org : wsOrgs) {
                orgList.add(toClient(org));
            }
        }

        return orgList;
    }

    /**
     * @param orgId
     * @return
     */
    public static Org getOrg(String orgId) throws OrgNotFoundException {
        Org org = null;

        try {
            org = loadFromServer(orgId);
        } catch (InterruptedException | ExecutionException re) {
            throw new OrgNotFoundException("remote exception occured when load org from server!");
        }
        return org;
    }
}
