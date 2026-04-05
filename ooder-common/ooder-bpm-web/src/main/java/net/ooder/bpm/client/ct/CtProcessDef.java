package net.ooder.bpm.client.ct;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.bpm.client.ProcessDef;
import net.ooder.bpm.client.ProcessDefVersion;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.enums.process.ProcessDefAccess;
import net.ooder.bpm.enums.process.ProcessDefVersionStatus;
import net.ooder.common.JDSException;

import java.util.ArrayList;
import java.util.List;

public class CtProcessDef implements ProcessDef {


    private String processDefId;

    private String name;

    private String description;

    private String classification;

    private String systemCode;

    private List<String> versionIds;

    private ProcessDefVersion activerocessDefVersion;

    private ProcessDefAccess accessLevel;


    public CtProcessDef(ProcessDef processDef) {
        this.processDefId = processDef.getProcessDefId();
        this.accessLevel = processDef.getAccessLevel();
        this.classification = processDef.getClassification();
        this.description = processDef.getDescription();
        this.name = processDef.getName();
        this.versionIds = processDef.getAllProcessDefVersionIds();
        this.systemCode = processDef.getSystemCode();
        try {
            this.activerocessDefVersion = processDef.getActiveProcessDefVersion();
        } catch (BPMException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getProcessDefId() {
        return this.processDefId;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getClassification() {
        return this.classification;
    }

    @Override
    public String getSystemCode() {
        return this.systemCode;
    }

    @Override
    public ProcessDefAccess getAccessLevel() {
        return this.accessLevel;
    }

    @Override
    public ProcessDefVersion getProcessDefVersion(int version) throws BPMException {
        List<ProcessDefVersion> processDefVersions = null;
        processDefVersions = this.getAllProcessDefVersions();
        for (ProcessDefVersion processDefVersion : processDefVersions) {
            if (processDefVersion.getVersion() == version) {
                return processDefVersion;
            }
        }
        return null;
    }

    @Override
    public List<String> getAllProcessDefVersionIds() {
        return this.versionIds;
    }

    //    @Override
//    @JsonIgnore
//    @JSONField(serialize = false)
    public List<ProcessDefVersion> getAllProcessDefVersions() throws BPMException {
        List<ProcessDefVersion> processDefVersions = new ArrayList<ProcessDefVersion>();
        List<String> processDefVersionIds = this.getAllProcessDefVersionIds();
        for (String processDefVersionId : processDefVersionIds) {
            try {
                ProcessDefVersion version = CtBPMCacheManager.getInstance().getProcessDefVersion(processDefVersionId);
                processDefVersions.add(version);
            } catch (JDSException e) {
                e.printStackTrace();
            }
        }
        return processDefVersions;
    }

    @Override

    @JSONField(serialize = false)
    public ProcessDefVersion getActiveProcessDefVersion() throws BPMException {
        if (activerocessDefVersion == null && getAllProcessDefVersions().size() > 0) {
            List<ProcessDefVersion> defVersions = this.getAllProcessDefVersions();
            for (ProcessDefVersion version : defVersions) {
                ProcessDefVersionStatus status = version.getPublicationStatus();
                if (status.equals(ProcessDefVersionStatus.RELEASED)) {
                    activerocessDefVersion = version;
                }
            }
        }
        return activerocessDefVersion;
    }

}
