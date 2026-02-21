/**
 * $RCSfile: CFlowType.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/07/08 00:25:44 $
 * <p>
 * Copyright (C) 2005 ooder, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of ooder, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: BPDProjectConfig.java,v $
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class BPDProjectConfig implements Serializable {

    public BPDProjectConfig() {

    }


    private String sysId;

    private String code;

    private String projectId;

    private String name;

    private String esbkeylist;

    private List<String> expression = new ArrayList<>();

    private List<BPDPlugin> bpdElementsList = new ArrayList<>();

    private List<CListener> processListeners = new ArrayList<CListener>();

    private List<CListener> activityListeners = new ArrayList<CListener>();

    private List<CListener> rightListeners = new ArrayList<CListener>();

    private List<CListener> bpdListeners = new ArrayList<CListener>();



    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CListener> getProcessListeners() {
        return processListeners;
    }

    public void setProcessListeners(List<CListener> processListeners) {
        this.processListeners = processListeners;
    }

    public void addProcessListener(CListener listener) {
        processListeners.add(listener);
    }

    public List<CListener> getActivityListeners() {
        return activityListeners;
    }

    public void setActivityListeners(List<CListener> activityListeners) {
        this.activityListeners = activityListeners;
    }

    public void addActivityListener(CListener listener) {
        activityListeners.add(listener);
    }

    public void addBPDListener(CListener listener) {
       bpdListeners.add(listener);
    }

    public void addRightListener(CListener listener) {
        rightListeners.add(listener);
    }

    public List<String> getExpression() {
        return expression;
    }

    public void setExpression(List<String> expression) {
        this.expression = expression;
    }


    public BPDPlugin getPluginById(String pluginId) {
        for (BPDPlugin bpdPlugin : bpdElementsList) {
            if (bpdPlugin!=null &&bpdPlugin.getPluginId()!=null && bpdPlugin.getPluginId().equals(pluginId)) {
                return bpdPlugin;
            }
        }
        return null;
    }


    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }

    public String getEsbkeylist() {
        return esbkeylist;
    }

    public void setEsbkeylist(String esbkeylist) {
        this.esbkeylist = esbkeylist;
    }

    public BPDPlugin getElementByName(String name) {
        for (BPDPlugin plugin : bpdElementsList) {
            if (plugin.getName() != null && plugin.getName().equals(name)) {
                return plugin;
            }
        }
        return null;
    }


    public List<BPDPlugin> getBpdElementsList() {
        return bpdElementsList;
    }

    public void setBpdElementsList(List<BPDPlugin> bpdElementsList) {
        this.bpdElementsList = bpdElementsList;
    }

    public BPDProjectConfig clone() {
        BPDProjectConfig flowType = new BPDProjectConfig();
        flowType.setActivityListeners(activityListeners);
        flowType.setSysId(sysId);
        flowType.setBpdElementsList(bpdElementsList);
        //flowType.setBpdElements(new HashMap<>(bpdElements));
        flowType.setCode(code);
        flowType.setBpdListeners(bpdListeners);
        flowType.setRightListeners(rightListeners);
        flowType.setEsbkeylist(esbkeylist);
        flowType.setName(name);
        flowType.setProcessListeners(processListeners);
        return flowType;
    }


    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public List<CListener> getRightListeners() {
        return rightListeners;
    }

    public void setRightListeners(List<CListener> rightListeners) {
        this.rightListeners = rightListeners;
    }

    public List<CListener> getBpdListeners() {
        return bpdListeners;
    }

    public void setBpdListeners(List<CListener> bpdListeners) {
        this.bpdListeners = bpdListeners;
    }
}
