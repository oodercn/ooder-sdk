/**
 * $RCSfile: AppConfig.java,v $
 * $Revision: 1.8 $
 * $Date: 2015/11/04 14:29:10 $
 * <p>
 * Copyright (C) 2025 ooder, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of ooder, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: AppConfig.java,v $
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


import net.ooder.common.FormulaType;
import net.ooder.common.PluginType;
import net.ooder.server.OrgManagerFactory;
import net.ooder.server.SubSystem;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;

import java.io.Serializable;
import java.util.*;

/**
 * <p>
 * Title: 应用配置解析器
 * </p>
 * <p>
 * Description: 应用配置文件解析器
 * <p>
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
public class AppConfig implements Serializable {

    private CApplication application;
    public static final String FLOWTYPENAME = "FlowType";

    /**
     * Creates a new application configuration object.
     */
    public AppConfig(InputSource is) {
        if (is == null) {
            throw new IllegalArgumentException("Parameter 'is' can't be null.");
        }

        try {
            SAXBuilder builder = new SAXBuilder();
            init(builder.build(is));
        } catch (Exception e) {
            System.out.println("Error parsing XML stream file.");
            e.printStackTrace();
        }
    }

    private void init(Document doc) {
        Element root = doc.getRootElement();
        Element element;
        // Application
        String appcode = root.getAttributeValue("code");
        String appname = root.getAttributeValue("name");
        application = new CApplication();
        application.setConfigCode(appcode);
        application.setName(appname);

        if ((element = root.getChild("ProcessClassification")) != null) {
            application.setProcessClassification(element.getTextTrim());
        }

        List<Element> elements = root.getChildren();

        // RightEngine
        if ((element = root.getChild("RightEngine")) != null) {
            CAJDSService rightEngine = new CAJDSService();
            rightEngine.setImplementation(element.getText());
            application.setRightEngine(rightEngine);
        }
        // DataEngine
        if ((element = root.getChild("DataEngine")) != null) {
            CAJDSService dataEngine = new CAJDSService();
            dataEngine.setImplementation(element.getText());
            application.setDataEngine(dataEngine);
        }

        // FileEngine
        if ((element = root.getChild("FileEngine")) != null) {
            CAJDSService fileEngine = new CAJDSService();
            fileEngine.setImplementation(element.getText());
            application.setFileEngine(fileEngine);
        }

        // AdminService
        if ((element = root.getChild("AdminService")) != null) {
            CAJDSService adminService = new CAJDSService();
            adminService.setImplementation(element.getText());
            application.setAdminService(adminService);
        }

        // VFSService
        if ((element = root.getChild("VFSService")) != null) {
            CAJDSService vfsService = new CAJDSService();
            vfsService.setImplementation(element.getText());
            application.setVfsService(vfsService);
        }

        //

        // msgEngine
        if ((element = root.getChild("MsgEngine")) != null) {
            CAJDSService bssEngine = new CAJDSService();
            bssEngine.setImplementation(element.getText());
            application.setMsgEngine(bssEngine);
        }

        // msgService
        if ((element = root.getChild("MsgService")) != null) {
            CAJDSService bssService = new CAJDSService();
            bssService.setImplementation(element.getText());
            application.setMsgService(bssService);
        }

        // GWService
        if ((element = root.getChild("GWService")) != null) {
            CAJDSService gwService = new CAJDSService();
            gwService.setImplementation(element.getText());
            application.setGwService(gwService);
        }

        // AppService
        if ((element = root.getChild("AppService")) != null) {
            CAJDSService appService = new CAJDSService();
            appService.setImplementation(element.getText());
            application.setAppService(appService);
        }

        // GWEngine
        if ((element = root.getChild("GWEngine")) != null) {
            CAJDSService gwEngine = new CAJDSService();
            gwEngine.setImplementation(element.getText());
            application.setGwEngine(gwEngine);
        }

        // workflowService
        if ((element = root.getChild("WorkflowService")) != null) {
            CAJDSService workflowService = new CAJDSService();
            workflowService.setImplementation(element.getText());
            application.setWorkflowService(workflowService);
            ;
        }

        // jdsService
        if ((element = root.getChild("JDSService")) != null) {
            CAJDSService jdsService = new CAJDSService();
            jdsService.setImplementation(element.getText());
            application.setJdsService(jdsService);
            ;
        }

        // jdsService
        if ((element = root.getChild("ConnectionHandle")) != null) {
            CAJDSService connectionHandle = new CAJDSService();
            connectionHandle.setImplementation(element.getText());
            application.setConnectionHandle(connectionHandle);
            ;
        }

        List<Element> flowTypes = root.getChildren(FLOWTYPENAME);
        Map<BPDProjectConfig, String> refMap = new HashMap<BPDProjectConfig, String>();

        for (int k = 0; k < flowTypes.size(); k++) {
            Element flowTypeElement = flowTypes.get(k);
            BPDProjectConfig cFlowType = new BPDProjectConfig();
            String code = flowTypeElement.getAttributeValue("code");
            String name = flowTypeElement.getAttributeValue("name");

            String esbkeylist = flowTypeElement.getAttributeValue("esbKeyList");
            cFlowType.setEsbkeylist(esbkeylist);
            cFlowType.setSysId(appcode);
            cFlowType.setCode(code);
            cFlowType.setName(name);

            Element ref = flowTypeElement.getChild("ref");

            if (ref != null && !ref.getValue().equals("")) {
                refMap.put(cFlowType, ref.getValue());
                continue;
            }
            Map<String, BPDPlugin> bpdElementMap = this.fromXMLBPDElement(flowTypeElement, code);

            bpdElementMap.forEach((key, value) -> {
                cFlowType.getBpdElementsList().add(value);
            });


            cFlowType.setProcessListeners(this.fromXMLWPListener(flowTypeElement, code));
            cFlowType.setBpdListeners(this.fromXMLBPDListener(flowTypeElement, code));

            cFlowType.setRightListeners(this.fromXMLRightListener(flowTypeElement, code));

            cFlowType.setActivityListeners(this.fromXMLActListener(flowTypeElement, code));
            application.getBPDProjectConfigMap().put(code, cFlowType);

        }

        List<SubSystem> systems = OrgManagerFactory.getInstance().getSystems();
        for (SubSystem system : systems) {
            String defauleType = system.getConfigname().getType();
            if (defauleType == null || defauleType.equals("")) {
                defauleType = "default";
            }
            BPDProjectConfig flowType = new BPDProjectConfig();
            BPDProjectConfig sourceflowType = application.getBPDProjectConfigMap().get(defauleType);
            if (sourceflowType != null && system.getConfigname().equals(application.getConfigCode())) {
                flowType.setSysId(system.getSysId());
                flowType.setCode(system.getSysId());
                flowType.setName(system.getName());
                flowType.setActivityListeners(sourceflowType.getActivityListeners());
                flowType.setProcessListeners(sourceflowType.getProcessListeners());
                flowType.getBpdElementsList().addAll(sourceflowType.getBpdElementsList());
                application.getBPDProjectConfigMap().put(flowType.getCode(), flowType);
            }
        }
    }

    private Map<String, BPDPlugin> fromXMLBPDElement(Element flowTypeElement, String code) {
        Map<String, BPDPlugin> bpdElementMap = new HashMap<String, BPDPlugin>();
        Element bpdXMLElements = flowTypeElement.getChild("BPDElements");
        if (bpdXMLElements != null) {
            List pdtElementList = bpdXMLElements.getChildren();
            for (int i = 0; i < pdtElementList.size(); i++) {
                Element bpdXMLElement = (Element) pdtElementList.get(i);
                BPDPlugin bpdElement = new BPDPlugin();

                bpdElement.setName(bpdXMLElement.getAttributeValue("name"));
                String type = bpdXMLElement.getAttributeValue("type");

                bpdElement.setActivityType(ActivityDefImpl.fromType(type));

                String plugintype = bpdXMLElement.getAttributeValue("plugintype");

                bpdElement.setPluginType(PluginType.fromType(plugintype));

                bpdElement.setDisplayname(bpdXMLElement.getAttributeValue("displayname"));
                String height = bpdXMLElement.getAttributeValue("height");
                if (height != null) {
                    bpdElement.setHeight(Integer.parseInt(bpdXMLElement.getAttributeValue("height")));
                }
                String width = bpdXMLElement.getAttributeValue("width");
                if (width != null) {
                    bpdElement.setWidth(Integer.parseInt(bpdXMLElement.getAttributeValue("width")));
                }
                bpdElement.setImplementation(bpdXMLElement.getAttributeValue("implementation"));


                // Parameters
                List paramList = bpdXMLElement.getChildren("Parameter");
                for (int j = 0; j < paramList.size(); j++) {
                    Element paramXMLElement = (Element) paramList.get(j);
                    CParameter param = new CParameter();
                    param.setName(paramXMLElement.getAttributeValue("name"));
                    param.setParameterValue(paramXMLElement.getAttributeValue("value"));
                    param.setParameterId(UUID.randomUUID().toString());
                    bpdElement.putParameter(param.getName(), param);

                }


                List formulaList = bpdXMLElement.getChildren("Formula");
                for (int j = 0; j < formulaList.size(); j++) {
                    Element paramXMLElement = (Element) formulaList.get(j);
                    CFormula param = new CFormula();
                    param.setParameterId(UUID.randomUUID().toString());
                    param.setName(paramXMLElement.getAttributeValue("name"));
                    param.setParameterValue(FormulaType.fromType(paramXMLElement.getAttributeValue("value")));
                    bpdElement.putFormula(param.getName(), param);

                }


                bpdElement.setExtendedAttributes(this.fromXMLExtendedAttribute(bpdXMLElement, bpdElement.getPluginId()));
                bpdElement.setBrowserElements(this.fromXMLBrowser(bpdXMLElement, bpdElement.getPluginId()));
                bpdElementMap.put(bpdElement.getName(), bpdElement);
            }

        }
        return bpdElementMap;

    }

    private List<CBPDBrowserElement> fromXMLBrowser(Element bpdXMLElement, String pluginId) {
        // UrlConfig
        List<CBPDBrowserElement> borwserlist = new ArrayList<CBPDBrowserElement>();
        List urlConfigList = bpdXMLElement.getChildren("BrowserConfig");
        for (int j = 0; j < urlConfigList.size(); j++) {
            Element uiConfigXMLElement = (Element) urlConfigList.get(j);
            CBPDBrowserElement browser = new CBPDBrowserElement();
            browser.setName(uiConfigXMLElement.getAttributeValue("name"));
            browser.setBaseurl(uiConfigXMLElement.getAttributeValue("baseurl"));
            browser.setDisplayname(uiConfigXMLElement.getAttributeValue("displayname"));
            browser.setToxml(uiConfigXMLElement.getAttributeValue("toxml"));
            browser.setPluginId(pluginId);
            // Parameters
            List urlParamList = uiConfigXMLElement.getChildren("Parameter");
            for (int f = 0; f < urlParamList.size(); f++) {
                Element urlParamXMLElement = (Element) urlParamList.get(f);
                CParameter param = new CParameter();
                param.setName(urlParamXMLElement.getAttributeValue("name"));
                param.setParameterValue(urlParamXMLElement.getAttributeValue("value"));

                browser.putParameter(param.getName(), param);
            }
            borwserlist.add(browser);
        }
        return borwserlist;

    }

    private Map<String, CExtendedAttribute> fromXMLExtendedAttribute(Element bpdXMLElement, String pluginId) {
        // CExtendedAttribute
        Map<String, CExtendedAttribute> extendedAttributeMap = new HashMap<String, CExtendedAttribute>();
        List extendedAttributeList = bpdXMLElement.getChildren("ExtendedAttribute");
        for (int j = 0; j < extendedAttributeList.size(); j++) {
            Element extendedXMLelement = (Element) extendedAttributeList.get(j);
            CExtendedAttribute extendedAttribute = new CExtendedAttribute();
            extendedAttribute.setName(extendedXMLelement.getAttributeValue("name"));
            extendedAttribute.setValue(extendedXMLelement.getAttributeValue("value"));
            extendedAttribute.setType(extendedXMLelement.getAttributeValue("type"));

            extendedAttributeMap.put(extendedAttribute.getName(), extendedAttribute);
        }
        return extendedAttributeMap;
    }

    private List<CListener> fromXMLWPListener(Element flowTypeElement, String code) {
        // ProcessListeners
        List<CListener> listenerList = new ArrayList<CListener>();
        Element listenerPXMLElement = flowTypeElement.getChild("ProcessListeners");
        if (listenerPXMLElement != null) {
            List listeners = listenerPXMLElement.getChildren();
            CListener defaultlistener = new CListener();
            defaultlistener.setImplementation("net.ooder.workflow.engine.event.DefaultProcessListenerExpressionPar");
            defaultlistener.setName("表达式解析器");
            listenerList.add(defaultlistener);
            for (int i = 0; i < listeners.size(); i++) {
                Element element = (Element) listeners.get(i);
                CListener listener = new CListener();
                listener.setProjectId(code);
                listener.setName(element.getAttributeValue("name"));
                listener.setImplementation(element.getText());
                listenerList.add(listener);
            }
        }
        return listenerList;
    }

    private List<CListener> fromXMLBPDListener(Element flowTypeElement, String code) {
        // ProcessListeners
        List<CListener> listenerList = new ArrayList<CListener>();
        Element listenerPXMLElement = flowTypeElement.getChild("BPDListeners");
        if (listenerPXMLElement != null) {
            List listeners = listenerPXMLElement.getChildren();
            for (int i = 0; i < listeners.size(); i++) {
                Element element = (Element) listeners.get(i);
                CListener listener = new CListener();

                listener.setProjectId(code);
                listener.setName(element.getAttributeValue("name"));
                listener.setImplementation(element.getText());
                listenerList.add(listener);
            }
        }
        return listenerList;
    }

    private List<CListener> fromXMLRightListener(Element flowTypeElement, String code) {
        // ProcessListeners
        List<CListener> listenerList = new ArrayList<CListener>();
        Element listenerPXMLElement = flowTypeElement.getChild("RightListeners");
        if (listenerPXMLElement != null) {
            List listeners = listenerPXMLElement.getChildren();
            for (int i = 0; i < listeners.size(); i++) {
                Element element = (Element) listeners.get(i);
                CListener listener = new CListener();

                listener.setProjectId(code);
                listener.setName(element.getAttributeValue("name"));
                listener.setImplementation(element.getText());
                listenerList.add(listener);
            }
        }
        return listenerList;
    }

    private List<CListener> fromXMLActListener(Element flowTypeElement, String code) {
        // ActivityListeners
        List<CListener> activityListenerList = new ArrayList<CListener>();
        Element listenerAXMLElement = flowTypeElement.getChild("ProcessListeners");
        if (listenerAXMLElement != null) {
            List listeners = listenerAXMLElement.getChildren();
            CListener defaultlistener = new CListener();
            defaultlistener.setImplementation("net.ooder.workflow.engine.event.DefaultActivityListenerExpressionPar");
            defaultlistener.setName("表达式解析器");
            activityListenerList.add(defaultlistener);
            for (int i = 0; i < listeners.size(); i++) {
                Element element = (Element) listeners.get(i);
                CListener listener = new CListener();
                listener.setProjectId(code);
                listener.setName(element.getAttributeValue("name"));
                listener.setImplementation(element.getText());
                activityListenerList.add(listener);
            }

        }
        return activityListenerList;
    }

    public CApplication getApplication() {
        return application;
    }

    public String getRightEngine() {
        return application.getRightEngine() == null ? null : application.getRightEngine().getImplementation();
    }

    public String getDataEngine() {
        return application.getDataEngine() == null ? null : application.getDataEngine().getImplementation();
    }

    public String getFileEngine() {
        return application.getFileEngine() == null ? null : application.getFileEngine().getImplementation();
    }

    public String getVFSEngine() {
        return application.getVfsEngine() == null ? null : application.getVfsEngine().getImplementation();
    }

    public String getVFSService() {
        return application.getVfsService() == null ? null : application.getVfsService().getImplementation();
    }

    public String getAdminService() {
        return application.getAdminService() == null ? null : application.getAdminService().getImplementation();
    }

}