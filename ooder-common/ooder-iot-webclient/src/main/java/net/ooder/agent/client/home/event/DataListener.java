package net.ooder.agent.client.home.event;

import net.ooder.agent.client.iot.HomeException;

/**
 * <p>
 * Title: HOME管理系统
 * </p>
 * <p>
 * Description: 数据处理监听器接口
 * </p>
 * <p>
 * Copyright: Copyright (c) 2018
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 * 
 * @author  ooder
 * @version 4.0
 */
public interface DataListener extends java.util.EventListener {

    /**
     * 数据上报
     * 
     * @param event
     * @return
     * @throws HomeException
     */
    public void dataReport(DataEvent event) throws HomeException;

    /**
     * 报警信息上报
     * 
     * @param event
     * @return
     * @throws HomeException
     */
    public void alarmReport(DataEvent event) throws HomeException;

    /**
     * 属性变更上报
     * 
     * @param event
     * @return
     * @throws HomeException
     */
    public void attributeReport(DataEvent event) throws HomeException;

    /**
     * 得到系统Code
     * 
     * @return
     */
    public String getSystemCode();

}
