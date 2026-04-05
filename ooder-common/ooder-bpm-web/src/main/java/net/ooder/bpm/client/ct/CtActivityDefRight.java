package net.ooder.bpm.client.ct;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.bpm.client.ActivityDefRight;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.enums.activitydef.ActivityDefPerformSequence;
import net.ooder.bpm.enums.activitydef.ActivityDefPerformtype;
import net.ooder.bpm.enums.activitydef.ActivityDefSpecialSendScope;
import net.ooder.bpm.enums.right.RightGroupEnums;
import net.ooder.common.JDSException;
import net.ooder.esd.manager.formula.ParticipantSelect;
import net.ooder.org.Person;

import java.util.List;

public class CtActivityDefRight implements ActivityDefRight {


    private Boolean canInsteadSign;
    private Boolean canTakeBack;
    private Boolean canReSend;
    private RightGroupEnums movePerformerTo;
    private RightGroupEnums moveReaderTo;
    private ActivityDefSpecialSendScope specialSendScope;

    private List<ParticipantSelect> performerSelectedAtt;
    private List<ParticipantSelect> readerSelectedAtt;
    private List<ParticipantSelect> insteadSignSelectedAtt;

    private ActivityDefPerformSequence performSequence;
    private ActivityDefPerformtype performType;

    public CtActivityDefRight(ActivityDefRight activityDefRight) {
        this.performType = activityDefRight.getPerformType();
        this.performSequence = activityDefRight.getPerformSequence();
        this.canInsteadSign = activityDefRight.isCanInsteadSign();
        this.canTakeBack = activityDefRight.isCanTakeBack();
        this.canReSend = activityDefRight.isCanReSend();
        this.specialSendScope = activityDefRight.getSpecialSendScope();
        this.movePerformerTo = activityDefRight.getMovePerformerTo();
        this.moveReaderTo = activityDefRight.getMoveReaderTo();
        this.movePerformerTo = activityDefRight.getMovePerformerTo();
        this.canReSend = activityDefRight.isCanReSend();
        this.canReSend = activityDefRight.isCanTakeBack();
        this.canInsteadSign = activityDefRight.isCanInsteadSign();
        this.canTakeBack = activityDefRight.isCanTakeBack();


        if (activityDefRight.getPerformerSelectedAtt() != null) {
            this.performerSelectedAtt = activityDefRight.getPerformerSelectedAtt();
        }

        if (activityDefRight.getReaderSelectedAtt() != null) {
            this.readerSelectedAtt = activityDefRight.getReaderSelectedAtt();
        }

        if (activityDefRight.getInsteadSignAtt() != null) {
            this.insteadSignSelectedAtt = activityDefRight.getInsteadSignAtt();
        }

    }

    @Override
    public List<ParticipantSelect> getPerformerSelectedAtt() {
        return performerSelectedAtt;
    }

    @Override
    public ActivityDefPerformtype getPerformType() {
        return performType;
    }

    @Override
    public ActivityDefPerformSequence getPerformSequence() {
        return performSequence;
    }

    @Override
    public Boolean isCanInsteadSign() {
        return canInsteadSign;
    }

    @Override
    public Boolean isCanTakeBack() {
        return canTakeBack;
    }

    @Override
    public ActivityDefSpecialSendScope getSpecialSendScope() {
        return specialSendScope;
    }

    @Override
    public Boolean isCanReSend() {
        return canReSend;
    }


    @Override
    public RightGroupEnums getMovePerformerTo() {
        return movePerformerTo;
    }

    @Override
    public RightGroupEnums getMoveReaderTo() {
        return moveReaderTo;
    }

    @Override
    public List<ParticipantSelect> getReaderSelectedAtt() {
        return readerSelectedAtt;
    }

    @Override
    public List<ParticipantSelect> getInsteadSignAtt() {
        return insteadSignSelectedAtt;
    }


    @Override
    @JSONField(serialize = false)
    public List<Person> getPerFormPersons() throws BPMException {
        List<Person> persons = null;
        try {
            persons = CtRightEngine.getInstance().getParticipant(getPerformerSelectedAtt(), null);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return persons;
    }

    @Override
    @JSONField(serialize = false)
    public List<Person> getReaderPersons() throws BPMException {
        List<Person> persons = null;
        try {
            persons = CtRightEngine.getInstance().getParticipant(getReaderSelectedAtt(), null);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return persons;
    }

    @Override
    @JSONField(serialize = false)
    public List<Person> getInsteadSignPersons() throws BPMException {
        List<Person> persons = null;
        try {
            persons = CtRightEngine.getInstance().getParticipant(getInsteadSignAtt(), null);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return persons;
    }

    public Boolean getCanInsteadSign() {
        return canInsteadSign;
    }

    public void setCanInsteadSign(Boolean canInsteadSign) {
        this.canInsteadSign = canInsteadSign;
    }

    public Boolean getCanTakeBack() {
        return canTakeBack;
    }

    public void setCanTakeBack(Boolean canTakeBack) {
        this.canTakeBack = canTakeBack;
    }

    public Boolean getCanReSend() {
        return canReSend;
    }

    public void setCanReSend(Boolean canReSend) {
        this.canReSend = canReSend;
    }

    public void setMovePerformerTo(RightGroupEnums movePerformerTo) {
        this.movePerformerTo = movePerformerTo;
    }

    public void setMoveReaderTo(RightGroupEnums moveReaderTo) {
        this.moveReaderTo = moveReaderTo;
    }

    public void setSpecialSendScope(ActivityDefSpecialSendScope specialSendScope) {
        this.specialSendScope = specialSendScope;
    }

    public void setPerformerSelectedAtt(List<ParticipantSelect> performerSelectedAtt) {
        this.performerSelectedAtt = performerSelectedAtt;
    }

    public void setReaderSelectedAtt(List<ParticipantSelect> readerSelectedAtt) {
        this.readerSelectedAtt = readerSelectedAtt;
    }

    public List<ParticipantSelect> getInsteadSignSelectedAtt() {
        return insteadSignSelectedAtt;
    }

    public void setInsteadSignSelectedAtt(List<ParticipantSelect> insteadSignSelectedAtt) {
        this.insteadSignSelectedAtt = insteadSignSelectedAtt;
    }

    public void setPerformSequence(ActivityDefPerformSequence performSequence) {
        this.performSequence = performSequence;
    }

    public void setPerformType(ActivityDefPerformtype performType) {
        this.performType = performType;
    }
}
