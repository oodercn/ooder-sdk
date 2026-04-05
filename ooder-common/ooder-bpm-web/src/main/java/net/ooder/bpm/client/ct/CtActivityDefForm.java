package net.ooder.bpm.client.ct;

import net.ooder.bpm.client.ActivityDefForm;
import net.ooder.esd.manager.formula.ParticipantSelect;

import java.util.List;

public class CtActivityDefForm implements ActivityDefForm {


    private List<ParticipantSelect> escomSelectedAtt;
    private List<ParticipantSelect> actionSelectedAtt;
    private List<ParticipantSelect> tableSelectedAtt;


    public CtActivityDefForm(ActivityDefForm activityDefForm) {


        if (activityDefForm.getActionSelectedAtt() != null) {
            this.actionSelectedAtt = activityDefForm.getActionSelectedAtt();
        }

        if (activityDefForm.getEscomSelectedAtt() != null) {
            this.escomSelectedAtt = activityDefForm.getEscomSelectedAtt();
        }

        if (activityDefForm.getTableSelectedAtt() != null) {
            this.tableSelectedAtt = activityDefForm.getTableSelectedAtt();
        }

    }



    @Override
    public List<ParticipantSelect> getEscomSelectedAtt() {
        return escomSelectedAtt;
    }

    @Override
    public List<ParticipantSelect> getActionSelectedAtt() {
        return actionSelectedAtt;
    }

    @Override
    public List<ParticipantSelect> getTableSelectedAtt() {
        return tableSelectedAtt;
    }
}
