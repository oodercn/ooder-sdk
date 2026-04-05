package net.ooder.bpm.client;

import net.ooder.esd.manager.formula.ParticipantSelect;

import java.util.List;

public interface ActivityDefForm extends java.io.Serializable {


    public List<ParticipantSelect> getEscomSelectedAtt();

    /**
     * @return
     */
    public List<ParticipantSelect> getActionSelectedAtt();

    /**s
     * @return
     */
    public List<ParticipantSelect> getTableSelectedAtt();


}
