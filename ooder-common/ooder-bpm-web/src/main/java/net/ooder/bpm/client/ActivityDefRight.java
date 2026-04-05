package net.ooder.bpm.client;

import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.enums.activitydef.ActivityDefPerformSequence;
import net.ooder.bpm.enums.activitydef.ActivityDefPerformtype;
import net.ooder.bpm.enums.activitydef.ActivityDefSpecialSendScope;
import net.ooder.bpm.enums.right.RightGroupEnums;
import net.ooder.esd.manager.formula.ParticipantSelect;
import net.ooder.org.Person;

import java.util.List;

public interface ActivityDefRight extends java.io.Serializable{


    public  List<ParticipantSelect> getPerformerSelectedAtt();


    /**
     *
     * @return
     */
    public List<ParticipantSelect>  getReaderSelectedAtt();

    /**
     *
     * @return
     */
    public List<ParticipantSelect>  getInsteadSignAtt();

    /**
     * 获取办理类型
     * @return
     */
    public  ActivityDefPerformtype getPerformType();

    /**
     * 获取办理顺序
     * @return
     */
    public  ActivityDefPerformSequence getPerformSequence();

    /**
     * 是否能够代签
     * @return
     */
    public  Boolean isCanInsteadSign();

    /**
     * 是否可以
     * @return
     */
    public  Boolean isCanTakeBack();

    /**
     * 退回范围处理办法
     * @return
     */
    public   ActivityDefSpecialSendScope getSpecialSendScope();

    /**
     * 是否可以补发
     * @return
     */
    public   Boolean  isCanReSend();

    /**
     *解析办理人员公式
     * @return
     * @throws BPMException
     */
    public  List<Person> getPerFormPersons() throws BPMException ;

    /**
     * 解析阅办人员公式
     * @return
     * @throws BPMException
     */
    public  List<Person> getReaderPersons()throws BPMException ;


    /**
     * 解析代签人员公式
     * @return
     * @throws BPMException
     */
    public  List<Person>  getInsteadSignPersons()throws BPMException ;


    /**
     * 办理后权限
     *
     * @return
     */
    public  RightGroupEnums getMovePerformerTo();


    /**
     * 阅办后权限
     * @return
     */
    public   RightGroupEnums getMoveReaderTo();


}
