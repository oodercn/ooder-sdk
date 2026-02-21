/**
 * $RCSfile: CApplication.java,v $
 * $Revision: 1.8 $
 * $Date: 2015/11/04 14:29:10 $
 * <p>
 * Copyright (C) 2025 ooder, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of ooder, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: CApplication.java,v $
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
package net.ooder.config;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 对应应用配置文件中的Application元素
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 *
 * @author wenzhang li
 * @version 1.0
 */
public class CApplication implements Serializable {


    @JSONField(name = "code")
    private String configCode;


    private String sysId;

    private String name;

    private String desc;

    private CAJDSService rightEngine;

    private CAJDSService dataEngine;

    private CAJDSService fileEngine;

    private CAJDSService msgEngine;

    private CAJDSService msgService;

    private CAJDSService gwService;

    private CAJDSService gwEngine;

    private CAJDSService appService;

    private CAJDSService vfsService;

    private CAJDSService vfsEngine;

    private CAJDSService workflowService;

    private CAJDSService jdsService;

    private CAJDSService connectionHandle;

    private CAJDSService adminService;

    private String processClassification;

    private String configPath;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @JSONField(name = "wfClassificationsConfigMap")
    private Map<String, BPDProjectConfig> bpdProjectConfigMap = new HashMap<String, BPDProjectConfig>();


    public String getConfigCode() {
        return configCode;
    }

    public void setConfigCode(String configCode) {
        this.configCode = configCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CAJDSService getRightEngine() {
        return rightEngine;
    }

    public void setRightEngine(CAJDSService rightEngine) {
        this.rightEngine = rightEngine;
    }


    public Map<String, BPDProjectConfig> getBPDProjectConfigMap() {
        return bpdProjectConfigMap;
    }

    public void setBPDProjectConfigMap(Map<String, BPDProjectConfig> bpdProjectConfigMap) {
        this.bpdProjectConfigMap = bpdProjectConfigMap;
    }

    public CAJDSService getAdminService() {
        return adminService;
    }


    public void setAdminService(CAJDSService adminService) {
        this.adminService = adminService;
    }

    public CAJDSService getDataEngine() {
        return dataEngine;
    }

    public void setDataEngine(CAJDSService dataEngine) {
        this.dataEngine = dataEngine;
    }


    public String getProcessClassification() {
        return processClassification;
    }

    public void setProcessClassification(String processClassification) {
        this.processClassification = processClassification;
    }

    public CAJDSService getFileEngine() {
        return fileEngine;
    }

    public void setFileEngine(CAJDSService fileEngine) {
        this.fileEngine = fileEngine;
    }


    public String getConfigPath() {
        if (configPath == null || configPath.equals("")) {
            configPath = "org";
        }
        return configPath;
    }

    public CAJDSService getWorkflowService() {
        return workflowService;
    }

    public void setWorkflowService(CAJDSService jdsService) {
        this.workflowService = jdsService;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public CAJDSService getVfsEngine() {
        return vfsEngine;
    }

    public void setVfsEngine(CAJDSService vfsEngine) {
        this.vfsEngine = vfsEngine;
    }

    public CAJDSService getVfsService() {
        return vfsService;
    }

    public void setVfsService(CAJDSService vfsService) {
        this.vfsService = vfsService;
    }

    public CAJDSService getJdsService() {
        return jdsService;
    }

    public void setJdsService(CAJDSService jdsService) {
        this.jdsService = jdsService;
    }

    public CAJDSService getGwEngine() {
        return gwEngine;
    }

    public void setGwEngine(CAJDSService gwEngine) {
        this.gwEngine = gwEngine;
    }

    public CAJDSService getGwService() {
        return gwService;
    }

    public void setGwService(CAJDSService gwService) {
        this.gwService = gwService;
    }

    public CAJDSService getAppService() {
        return appService;
    }

    public void setAppService(CAJDSService appService) {
        this.appService = appService;
    }

    public CAJDSService getMsgEngine() {
        return msgEngine;
    }

    public void setMsgEngine(CAJDSService msgEngine) {
        this.msgEngine = msgEngine;
    }

    public CAJDSService getMsgService() {
        return msgService;
    }

    public void setMsgService(CAJDSService msgService) {
        this.msgService = msgService;
    }

    public CAJDSService getConnectionHandle() {
        return connectionHandle;
    }

    public void setConnectionHandle(CAJDSService connectionHandle) {
        this.connectionHandle = connectionHandle;
    }

    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }

}
