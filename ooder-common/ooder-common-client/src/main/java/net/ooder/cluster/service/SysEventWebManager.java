/**
 * $RCSfile: SysEventWebManager.java,v $
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
package net.ooder.cluster.service;

import net.ooder.cluster.event.RegistEventBean;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;
import net.ooder.esb.config.manager.ServiceBean;
import net.ooder.web.RuntimeLog;

import java.util.List;

/**
 * 系统管理远程方法
 * <p>
 * Title:
 * </p>
 * *
 * <p>
 * Copyright: Copyright (c) 2003-2019
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 *
 * @author  ooder
 * @version 1.0
 */
public interface SysEventWebManager {


    /**
     * @param url
     * @param body
     * @param sessionId
     * @param time
     * @return
     */
    ListResultModel<List<RuntimeLog>> getRunTimeLogs(String url, String body, String sessionId, Long time);


    /**
     * @param systemCode
     * @param eventKeys
     * @return
     */
    public ResultModel<Boolean> registerEvent(String systemCode, String eventKeys);


    /**
     * @param systemCode
     * @param json
     * @return
     */
    public ResultModel<Boolean> registerEventJSON(String systemCode, String json);


    /**
     * @param systemCode
     * @return
     */
    public ResultModel<Boolean> clearEventKeys(String systemCode);


    /**
     * @param systemCode
     * @param eventKey
     * @return
     */
    public ResultModel<Boolean> removeEvent(String systemCode, String eventKey);


    /**
     * @return
     */
    public ListResultModel<List<? extends ServiceBean>> getRegisterEventByCode(String sysCode);


    /**
     * @return
     */
    public ListResultModel<List<RegistEventBean>> getAllRegisterEvent();


}