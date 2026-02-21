package net.ooder.common.org;

import net.ooder.common.JDSException;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.org.OrgManager;
import net.ooder.annotation.Permissions;
import net.ooder.org.Person;
import net.ooder.server.OrgManagerFactory;
import net.ooder.server.SubSystem;
import net.ooder.server.ct.CtSubSystem;
import net.ooder.server.service.SysWebManager;

import java.util.ArrayList;
import java.util.List;


public class CtOrgManagerFactory extends OrgManagerFactory {
    private static CtOrgManagerFactory factory = null;
    private static OrgManager orgManager;

    public CtOrgManagerFactory() {
        init();
    }

    private void init() {
        orgManager = new CtOrgManager();
        log.debug("OrgManager initialized...");
    }

    public OrgManager getOrgManager(Person person) {
        throw new UnsupportedOperationException();
    }


    @Override
    public List<SubSystem> getSystems() {
        List<SubSystem> systems = new ArrayList<>();
        try {
            List<SubSystem> remoteSystems = getSysWebManager().getAllSystemInfo().get();
            for (SubSystem system : remoteSystems) {
                CtSubSystem ctSubSystem = new CtSubSystem(system);
                systems.add(ctSubSystem);
                getSystemMap().put(system.getSysId(), ctSubSystem);
            }
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return systems;
    }

    @Override
    public SubSystem getSystemById(String key) {
        SubSystem system = getSystemMap().get(key);
        if (system == null) {
            try {
                system = getSysWebManager().getSubSystemInfo(key).get();
                getSystemMap().put(system.getSysId(), new CtSubSystem(system));
            } catch (JDSException e1) {

            }
        }
        return system;
    }

    SysWebManager getSysWebManager() {
        return (SysWebManager) EsbUtil.parExpression(SysWebManager.class);
    }


    public Permissions getPermissions(Person person) {
        return new Permissions();
    }
}
