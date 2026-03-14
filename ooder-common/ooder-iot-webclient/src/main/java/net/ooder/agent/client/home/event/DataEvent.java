package net.ooder.agent.client.home.event;

import net.ooder.agent.client.iot.enums.DataEventEnums;
import  net.ooder.msg.index.DataIndex;
import  net.ooder.server.JDSClientService;

/**
 * <p>
 * Title: GW管理系统
 * </p>
 * <p>
 * Description: 数据上报事件
 * </p>
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * <p>
 * Company: www.bjgreenlive.com
 * </p>
 *
 * @author  ooder
 * @version 1.0
 */
@SuppressWarnings("all")
public class DataEvent<T extends DataIndex> extends HomeEvent<T> {


    /**
     * GetwayEvent
     *
     * @param path
     * @param eventID
     */
    public DataEvent(T dataIndex, JDSClientService client, DataEventEnums eventID, String sysCode) {
        super(dataIndex, null);
        id = eventID;
        this.systemCode = sysCode;
        this.setClientService(client);

    }


    @Override
    public T getSource() {
        return super.getSource();
    }


    @Override
    public DataEventEnums getID() {
        return (DataEventEnums) id;
    }

}
