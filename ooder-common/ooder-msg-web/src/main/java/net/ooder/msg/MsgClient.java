/**
 * $RCSfile: MsgClient.java,v $
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
package net.ooder.msg;

import net.ooder.annotation.JLuceneIndex;
import net.ooder.common.Condition;
import net.ooder.common.JDSException;
import net.ooder.config.ListResultModel;
import net.ooder.org.query.MsgConditionKey;
import net.ooder.annotation.MethodChinaName;

import java.util.List;

/**
 * 消息客户端接口
 * 提供消息的发送、接收、查询和管理功能
 * 
 * @author ooder team
 * @version 2.0
 * @since 2025-08-25
 */
public interface MsgClient<V extends Msg> {



    @MethodChinaName("根据ID获取消息")
    public   V  getMsgById(String msgId);

    /**
     * 获取所有消息（包含已发送及接受的）
     *
     * @return
     * @throws JDSException
     */
    @MethodChinaName("获取所有发送消息")
    public  <T extends List<V>> ListResultModel<T> getAllSendMsg() throws JDSException;

    /**
     * 获取指定参与者信息列表
     *
     * @param personId
     * @return
     * @throws JDSException
     */
    @MethodChinaName("根据发送者获取消息")
    public  <T extends List<V>> ListResultModel<T> getSendMsgByPerson(String personId) throws JDSException;

    /**
     * 获取所有接收到的消息V
     *
     * @return
     * @throws JDSException
     */
    @MethodChinaName("获取所有接收消息")
    public  <T extends List<V>> ListResultModel<T> getAllReceiveMsg() throws JDSException;

    /**
     * 获取指定接收对象的消息集合
     *
     * @param fromPersonId
     * @return
     * @throws JDSException
     */
    @MethodChinaName("根据接收者获取消息")
    public <T extends List<V>> ListResultModel<T> getReceiveMsgByPerson(String fromPersonId) throws JDSException;

    /**
     * 创建向指定用户发送的消息
     *
     * @param toPersonId
     * @return
     * @throws JDSException
     */
    @MethodChinaName("创建发送给指定用户的消息")
    public   V creatMsg2Person(String toPersonId) throws JDSException;

    /**
     * 创建消息
     *
     * @return
     * @throws JDSException
     */
    @MethodChinaName("创建消息")
    public V creatMsg() throws JDSException;

    /**
     * 查询消息
     *
     * @param condition
     * @return
     * @throws JDSException
     */
    @MethodChinaName("根据条件查询消息列表")
    public <T extends List<V>>  ListResultModel<T> getMsgList(Condition<MsgConditionKey,JLuceneIndex> condition) throws JDSException;



    /**
     * 群发信息
     *
     * @param msg
     * @param personIds
     * @throws JDSException
     */
    @MethodChinaName("发送群发消息")
    public void sendMassMsg(V msg, List<String> personIds) throws JDSException;


    @MethodChinaName("克隆消息")
    public   V cloneMsg(Msg msg);

    @MethodChinaName("更新消息")
    public void updateMsg(V msg);

    @MethodChinaName("删除消息")
    public void deleteMsg(String msgId);


}


