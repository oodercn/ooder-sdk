/**
 * $RCSfile: CtMsgClient.java,v $
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
package net.ooder.msg.ct;

import net.ooder.annotation.JLuceneIndex;
import net.ooder.annotation.JoinOperator;
import net.ooder.annotation.Operator;
import net.ooder.common.Condition;
import net.ooder.common.JDSException;
import net.ooder.config.ListResultModel;
import net.ooder.msg.Msg;
import net.ooder.msg.MsgClient;
import net.ooder.org.query.MsgConditionKey;

import java.util.List;

public class CtMsgClient<V extends Msg> implements MsgClient<V> {

    private final String personId;
    private final Class<V> clazz;

    public CtMsgClient(String personId, Class<V> clazz) {
        this.personId=personId;
        this.clazz=clazz;
    }




    public V getMsgById(String msgId){
        V msg= null;
        try {
            msg = CtMsgCacheManager.getInstance().getMsgById(msgId,clazz);
        } catch (JDSException e) {
            e.printStackTrace();
        }

        return msg;
    }

    @Override
    public <T extends List<V>>ListResultModel<T> getAllSendMsg() throws JDSException {
        Condition maincon = new Condition(MsgConditionKey.MSG_SEND, Operator.EQUALS, personId);

        Condition rightmaincon= new Condition();

        Condition receivecon = new Condition(MsgConditionKey.MSG_RECEIVE, Operator.EQUALS, personId);
        Condition sendcon = new Condition(MsgConditionKey.MSG_SEND, Operator.EQUALS, personId);
        rightmaincon.addCondition(receivecon, JoinOperator.JOIN_OR);
        rightmaincon.addCondition(sendcon, JoinOperator.JOIN_OR);

        maincon.addCondition(rightmaincon, JoinOperator.JOIN_AND);

        return getMsgList(maincon);
    }

    @Override
    public<T extends List<V>> ListResultModel<T> getSendMsgByPerson(String sendPersonId) throws JDSException {

        Condition maincon = new Condition(MsgConditionKey.MSG_SEND, Operator.EQUALS, personId);
        Condition rightmaincon= new Condition();
        Condition receivecon = new Condition(MsgConditionKey.MSG_RECEIVE, Operator.EQUALS, sendPersonId);
        Condition sendcon = new Condition(MsgConditionKey.MSG_SEND, Operator.EQUALS, personId);
        rightmaincon.addCondition(receivecon, JoinOperator.JOIN_OR);
        rightmaincon.addCondition(sendcon, JoinOperator.JOIN_OR);

        maincon.addCondition(rightmaincon, JoinOperator.JOIN_AND);

        return getMsgList(maincon);
    }

    @Override
    public <T extends List<V>> ListResultModel<T> getAllReceiveMsg() throws JDSException {
        Condition maincon = new Condition(MsgConditionKey.MSG_SEND, Operator.EQUALS, personId);
        Condition receivecon = new Condition(MsgConditionKey.MSG_RECEIVE, Operator.EQUALS, personId);
        maincon.addCondition(receivecon, JoinOperator.JOIN_OR);


        return getMsgList(maincon);
    }

    @Override
    public <T extends List<V>> ListResultModel<T> getReceiveMsgByPerson(String fromPersonId) throws JDSException {
        Condition maincon = new Condition(MsgConditionKey.MSG_SEND, Operator.EQUALS, personId);
        Condition rightmaincon= new Condition();
        Condition receivecon = new Condition(MsgConditionKey.MSG_RECEIVE, Operator.EQUALS, personId);
        Condition sendcon = new Condition(MsgConditionKey.MSG_SEND, Operator.EQUALS, fromPersonId);
        rightmaincon.addCondition(receivecon, JoinOperator.JOIN_OR);
        rightmaincon.addCondition(sendcon, JoinOperator.JOIN_OR);
        maincon.addCondition(rightmaincon, JoinOperator.JOIN_AND);
        return getMsgList(maincon);
    }

    @Override
    public V creatMsg2Person(String toPersonId) throws JDSException {
      V msg=  CtMsgCacheManager.getInstance().creatMsg(clazz);
      msg.setReceiver(toPersonId);
      msg.setFrom(this.personId);
        return msg;
    }

    @Override
    public V creatMsg() throws JDSException {
        V msg=  CtMsgCacheManager.getInstance().creatMsg(clazz);
        msg.setFrom(this.personId);
        return msg;
    }

    @Override
    public<T extends List<V>> ListResultModel<T> getMsgList(Condition<MsgConditionKey,JLuceneIndex> condition) throws JDSException {
        return CtMsgCacheManager.getInstance().findMsgs(condition,clazz);
    }


    @Override
    public void sendMassMsg(V msg,  List<String> personIds) throws JDSException {
      CtMsgCacheManager.getInstance().sendMassMsg(msg,personIds);
    }

    @Override
    public V cloneMsg(Msg msg) {
       V cMsg= CtMsgCacheManager.getInstance().cloneMsg(msg,clazz);
        return cMsg;
    }

    @Override
    public void updateMsg(V msg) {
       CtMsgCacheManager.getInstance().updateMsg(msg);
    }



    @Override
    public void deleteMsg(String msgId) {
            CtMsgCacheManager.getInstance().deleteMsg(msgId,clazz);
    }


}


