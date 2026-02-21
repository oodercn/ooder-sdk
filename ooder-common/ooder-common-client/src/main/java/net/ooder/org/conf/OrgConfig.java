/**
 * $RCSfile: OrgConfig.java,v $
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
package net.ooder.org.conf;

import net.ooder.common.CommonConfig;
import net.ooder.common.ConfigCode;
import net.ooder.common.JDSException;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.CaselessStringKeyHashMap;
import net.ooder.common.util.ClassUtility;
import net.ooder.common.util.Constants;
import net.ooder.common.util.StringUtility;
import net.ooder.config.JDSConfig.Config;
import net.ooder.msg.MsgAdapter;
import net.ooder.vfs.adapter.FileAdapter;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * <p>
 * Title: ooder组织机构
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003-2008
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 *
 * @author  ooder
 * @version 2.0
 */
public class OrgConfig implements Serializable {

    protected OrgConfig(ConfigCode configCode) throws JDSException {
        loadConfig(configCode);
    }

    private static final String configXMLPath = "org_config.xml";
    private static Map<ConfigCode, OrgConfig> orgConfigMap = new HashMap<ConfigCode, OrgConfig>();

    private Map supportsMap = new CaselessStringKeyHashMap();

    private Map permissionsMap = new CaselessStringKeyHashMap();

    private Map queriesMap = new CaselessStringKeyHashMap();

    private Map fileAdapterMap = new CaselessStringKeyHashMap();

    private Map msgAdapterMap = new CaselessStringKeyHashMap();

    private MsgAdapter msgAdapter;

    private ConfigCode configCode;
    private List<String> serverURLList;
    private Map<String, List<String>> serverPathMap = new HashMap<String, List<String>>();

    private static final Log logger = LogFactory.getLog(Constants.CONFIG_KEY, OrgConfig.class);

    public static OrgConfig getInstance() {
        return getInstance(OrgConstants.CONFIG_KEY);
    }

    public static OrgConfig getInstance(ConfigCode configCode) {
        OrgConfig orgConfig = orgConfigMap.get(configCode);
        if (orgConfig == null) {
            try {
                orgConfig = new OrgConfig(configCode);
            } catch (JDSException e) {
                e.printStackTrace();
            }
            if (orgConfig != null) {
                orgConfigMap.put(configCode, orgConfig);
            }

        }
        return orgConfig;
    }

    private void loadConfig(ConfigCode configCode) throws JDSException {
        SAXBuilder builder = new SAXBuilder();

        String orgConfigXMLPath = configXMLPath;
        try {
            if (configCode == null || configCode.equals(OrgConstants.CONFIG_KEY)) {
                configCode = OrgConstants.CONFIG_KEY;
                orgConfigXMLPath = CommonConfig.getValue(configCode.getType() + ".OrgConfigXMLPath");
                if (orgConfigXMLPath == null) {
                    String subOrgConfigXMLPath = CommonConfig.getValue(OrgConstants.CONFIG_KEY.getType() + ".database.SubOrgConfigXMLPath");
                    if (subOrgConfigXMLPath != null) {
                        orgConfigXMLPath = subOrgConfigXMLPath;
                    } else {
                        orgConfigXMLPath = configXMLPath;
                    }
                }

            }

            Document configDoc = null;
            InputStream is = null;
            File engineConfigFile = new File(Config.configPath(), orgConfigXMLPath);
            if (engineConfigFile.exists()) {
                try {
                    configDoc = builder.build(engineConfigFile);
                    // properties = new XMLProperties(engineConfigFile);
                } catch (FileNotFoundException e) {
                    logger.error("Org OrgConfig [" + engineConfigFile.getAbsolutePath() + "] load err ");
                } catch (JDOMException e) {
                    e.printStackTrace();
                }
            } else if (new File(Config.publicConfigPath(), orgConfigXMLPath).exists()) {
                engineConfigFile = new File(Config.publicConfigPath(), orgConfigXMLPath);
                configDoc = builder.build(engineConfigFile);
            } else {
                is = ClassUtility.loadResource(orgConfigXMLPath);
                if (is != null) {
                    configDoc = builder.build(is);
                } else {
                    logger.error("Org OrgConfig [" + orgConfigXMLPath + "] NotFound ");
                    throw new JDSException("Org OrgConfig [" + orgConfigXMLPath + "] NotFound ");
                }

            }


            Element root = configDoc.getRootElement();
            // supports
            Element e = root.getChild("Supports");
            if (e != null) {
                List elements = e.getChildren();
                for (int i = 0; i < elements.size(); i++) {
                    Element ee = (Element) elements.get(i);
                    supportsMap.put(ee.getAttributeValue("name"), ee.getAttributeValue("value"));
                }
            }
            // permissions
            e = root.getChild("Permissions");
            if (e != null) {
                List elements = e.getChildren();
                for (int i = 0; i < elements.size(); i++) {
                    Element ee = (Element) elements.get(i);
                    permissionsMap.put(ee.getAttributeValue("name"), ee.getAttributeValue("value"));
                }
            }
            // FileAdapter
            fileAdapterMap.clear();
            e = root.getChild("FileAdapter");
            if (e != null) {
                List elements = e.getChildren();
                for (int i = 0; i < elements.size(); i++) {
                    Element ee = (Element) elements.get(i);
                    fileAdapterMap.put(ee.getName(), ee.getTextTrim());
                }
            }
            fileAdapterMap.put("systemCode", configCode.getType());

            msgAdapterMap.clear();
            e = root.getChild("MsgAdapter");
            if (e != null) {
                List elements = e.getChildren();
                for (int i = 0; i < elements.size(); i++) {
                    Element ee = (Element) elements.get(i);
                    msgAdapterMap.put(ee.getName(), ee.getTextTrim());
                }
            }
            msgAdapterMap.put("systemCode", configCode.getType());

            // queriesMap
            e = root.getChild("Queries");
            if (e != null) {
                List elements = e.getChildren("Query");
                for (int i = 0; i < elements.size(); i++) {
                    Element ee = (Element) elements.get(i);
                    Query query = new Query();

                    List clauses = ee.getChildren("SqlClause");
                    Map sqlClauses = new CaselessStringKeyHashMap();
                    for (int j = 0; j < clauses.size(); j++) {
                        Element clause = (Element) clauses.get(j);
                        Query.SqlClause sqlClause = query.new SqlClause();
                        sqlClause.setType(clause.getAttributeValue("type"));
                        String strMainClause = clause.getChildText("MainClause");
                        String topIds = clause.getChildText("topIds");
                        sqlClause.setTopIds(topIds);
                        // 如果查询语句为空，则忽略该SqlClause
                        if (strMainClause == null || strMainClause.equals(""))
                            continue;
                        sqlClause.setMainClause(strMainClause);
                        String strInsertClause = clause.getChildText("InsertClause");
                        sqlClause.setInsertClause(strInsertClause);

                        String strDeleteClause = clause.getChildText("DeleteClause");
                        sqlClause.setDeleteClause(strDeleteClause);

                        sqlClause.setUpdataClause(clause.getChildText("UpdataClause"));

                        sqlClause.setOrderClause(clause.getChildText("OrderClause"));

                        sqlClause.setTableName(clause.getChildText("TableName"));

                        sqlClause.setTopIds(clause.getChildText("TopIds"));

                        String whereClause = clause.getChildText("WhereClause");
                        whereClause = StringUtility.replace(whereClause, "[SYSTEMID]", configCode.getType());
                        // System.out.println(whereClause);
                        sqlClause.setWhereClause(whereClause);
                        if (clause.getChild("ColumnMappings") == null)
                            continue;

                        List columnMappings = clause.getChild("ColumnMappings").getChildren();
                        if (columnMappings.size() == 0) {
                            continue;
                        }

                        Map map = new CaselessStringKeyHashMap();
                        for (int k = 0; k < columnMappings.size(); k++) {
                            Element mapping = (Element) columnMappings.get(k);
                            Query.ColumnMapping colMapping = query.new ColumnMapping();
                            String property = mapping.getAttributeValue("property");
                            colMapping.setProperty(property);
                            String columnAlias = mapping.getAttributeValue("columnAlias");
                            String column = mapping.getAttributeValue("column");
                            if (columnAlias != null && !columnAlias.equals("")) {
                                colMapping.setColumnAlias(columnAlias);
                            } else {
                                if (column != null && !column.equals("")) {
                                    colMapping.setColumn(column);
                                }
                            }
                            if (column != null && !column.equals("")) {
                                colMapping.setColumn(column);
                            } else {
                                if (columnAlias != null && !columnAlias.equals("")) {
                                    colMapping.setColumn(columnAlias);
                                }
                            }
                            map.put(property, colMapping);
                        }
                        sqlClause.setColumnMappings(map);
                        sqlClauses.put(sqlClause.getType(), sqlClause);
                    }
                    query.setType(ee.getAttributeValue("type"));
                    query.setSqlClauses(sqlClauses);

                    queriesMap.put(ee.getAttributeValue("type"), query);
                }
                if (is != null) {
                    is.close();
                }
            }


        } catch (JDOMException ex) {
        } catch (IOException ex) {
        }
    }

    public boolean isSupportPersonRole() {
        Object o = supportsMap.get("supportPersonRole");
        if (o != null && o.toString().equalsIgnoreCase("true")) {
            return true;
        }
        return false;
    }

    public boolean isSupportOrgRole() {
        Object o = supportsMap.get("supportOrgRole");
        if (o != null && o.toString().equalsIgnoreCase("true")) {
            return true;
        }
        return false;
    }

    public boolean isSupportOrgModule() {
        Object o = supportsMap.get("supportOrgModule");
        if (o != null && o.toString().equalsIgnoreCase("true")) {
            return true;
        }
        return false;
    }

    public boolean isSupportOrgApp() {
        Object o = supportsMap.get("supportOrgApp");
        if (o != null && o.toString().equalsIgnoreCase("true")) {
            return true;
        }
        return false;
    }

    public boolean isSupportOrgLevel() {
        Object o = supportsMap.get("supportOrgLevel");
        if (o != null && o.toString().equalsIgnoreCase("true")) {
            return true;
        }
        return false;
    }

    public boolean isSupportPersonLevel() {
        Object o = supportsMap.get("supportPersonLevel");
        if (o != null && o.toString().equalsIgnoreCase("true")) {
            return true;
        }
        return false;
    }

    public boolean isSupportPersonDuty() {
        Object o = supportsMap.get("supportPersonDuty");
        if (o != null && o.toString().equalsIgnoreCase("true")) {
            return true;
        }
        return false;
    }

    public boolean isSupportPersonPosition() {
        Object o = supportsMap.get("supportPersonPosition");
        if (o != null && o.toString().equalsIgnoreCase("true")) {
            return true;
        }
        return false;
    }

    public boolean isSupportPersonGroup() {
        Object o = supportsMap.get("supportPersonGroup");
        if (o != null && o.toString().equalsIgnoreCase("true")) {
            return true;
        }
        return false;
    }

    public boolean isSupportPersonPrivateGroup() {
        Object o = supportsMap.get("supportPersonPrivateGroup");
        if (o != null && o.toString().equalsIgnoreCase("true")) {
            return true;
        }
        return false;
    }

    public boolean isSupportModuleProperty() {
        Object o = supportsMap.get("supportModelProperty");
        if (o != null && o.toString().equalsIgnoreCase("true")) {
            return true;
        }
        return false;
    }

    public boolean isSupportPersonModuleProperty() {
        Object o = supportsMap.get("supportPersonModelProperty");
        if (o != null && o.toString().equalsIgnoreCase("true")) {
            return true;
        }
        return false;
    }

    public boolean isSupportDeskTopProperty() {
        Object o = supportsMap.get("supportDeskTopProperty");
        if (o != null && o.toString().equalsIgnoreCase("true")) {
            return true;
        }
        return false;
    }

    public boolean isSupportPersonMsg() {
        Object o = supportsMap.get("supportPersonMsg");
        if (o != null && o.toString().equalsIgnoreCase("true")) {
            return true;
        }
        return false;
    }

    public boolean isSupportModule() {
        Object o = supportsMap.get("supportModule");
        if (o != null && o.toString().equalsIgnoreCase("true")) {
            return true;
        }
        return false;
    }

    public boolean isSupportApp() {
        Object o = supportsMap.get("supportApp");
        if (o != null && o.toString().equalsIgnoreCase("true")) {
            return true;
        }
        return false;
    }

    public boolean isSelfOrgOnly() {
        Object o = permissionsMap.get("selfOrgOnly");
        if (o != null && o.toString().equalsIgnoreCase("true")) {
            return true;
        }
        return false;
    }

    public boolean isSupportSSO() {
        Object o = supportsMap.get("supportSSO");
        if (o != null && o.toString().equalsIgnoreCase("true")) {
            return true;
        }
        return false;
    }

    public boolean isSupportVFS() {
        Object o = supportsMap.get("supportVFS");
        if (o != null && o.toString().equalsIgnoreCase("true")) {
            return true;
        }
        return false;
    }

    public boolean isSupports(String name) {
        Object o = supportsMap.get(name);
        if (o != null && o.toString().equalsIgnoreCase("true")) {
            return true;
        }
        return false;
    }

    public boolean isPermits(String name) {
        Object o = permissionsMap.get(name);
        if (o != null && o.toString().equalsIgnoreCase("true")) {
            return true;
        }
        return false;
    }

    public Query getQuery(String type) {
        return (Query) queriesMap.get(type);
    }

    public FileAdapter getFileAdapter() {

        return getFileAdapter(null);
    }

    public FileAdapter getFileAdapter(String rootPath) {

        FileAdapter fileAdapter = null;
        if (rootPath == null) {
            rootPath = getServerURLList("root").get(0);
        }
        try {
            Class clazz = null;
            if (rootPath.startsWith("smb://")) {
                clazz = ClassUtility.loadClass("net.ooder.vfs.store.adapter.SmbFileAdapter");
                fileAdapter = (FileAdapter) clazz.getConstructor(new Class[]{String.class}).newInstance(rootPath);
            } else if (rootPath.startsWith("ftp://")) {
                clazz = ClassUtility.loadClass("net.ooder.vfs.store.adapter.FTPFileAdapter");
                fileAdapter = (FileAdapter) clazz.getConstructor(new Class[]{String.class}).newInstance(rootPath);
            } else if (rootPath.startsWith("hdfs://")) {
                clazz = ClassUtility.loadClass("net.ooder.vfs.store.adapter.HdfsFileAdapter");
                fileAdapter = (FileAdapter) clazz.getConstructor(new Class[]{String.class}).newInstance(rootPath);
            } else if (rootPath.startsWith("vfs://")) {
                clazz = ClassUtility.loadClass("net.ooder.vfs.store.adapter.ClusterFileAdapter");
                fileAdapter = (FileAdapter) clazz.getConstructor(new Class[]{String.class}).newInstance(rootPath);


            } else {
                clazz = ClassUtility.loadClass("net.ooder.vfs.store.adapter.LocalFileAdapter");
                fileAdapter = (FileAdapter) clazz.getConstructor(new Class[]{String.class}).newInstance(rootPath);

            }
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException e) {

            e.printStackTrace();
        }

        return fileAdapter;
    }

    public synchronized List<String> getServerURLList(String path) {

        if (this.serverURLList == null) {
            String serverURL = (String) getFileAdapterMap().get("serverURL");
            this.serverURLList = new ArrayList();
            if (serverURL.split(",").length > 1) {
                String[] usls = serverURL.split(",");
                for (String url : usls) {
                    serverURLList.add(url);
                    serverPathMap.put(url, new ArrayList());
                }
            } else {
                serverURLList.add(serverURL);
                serverPathMap.put(serverURL, new ArrayList());
            }
        }
        Collections.sort(serverURLList, new Comparator() {
            public int compare(final Object o1, final Object o2) {
                final String server1 = (String) o1;
                final String server2 = (String) o2;
                if (!getFileAdapter().testConnection(server1)) {
                    return -1;
                }
                if (serverPathMap.get(server1).size() < serverPathMap.get(server2).size()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        serverPathMap.get(serverURLList.get(0)).add(path);
        return serverURLList;
    }

    // public FileAdapter getFileAdapterByPath(String adapter, String rootPath) {
    //
    // Class[] parameter = null;
    // Object[] objs = null;
    //
    // try {
    // parameter = new Class[] { String.class };
    // objs = new Object[] { rootPath };
    // Constructor constructor = ClassUtility.loadClass(adapter).getConstructor(parameter);
    // fileAdapter = (FileAdapter) constructor.newInstance(objs);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    //
    // return fileAdapter;
    // }

    public MsgAdapter getMsgdapter() {
        Class[] parameter = null;
        Object[] objs = null;

        if (msgAdapter == null) {
            try {


                String className = (String) msgAdapterMap.get("className");
                String serverUrl = (String) msgAdapterMap.get("serverUrl");

                if (className != null && serverUrl != null) {
                    Integer threadPoolSize = Integer.parseInt((String) msgAdapterMap.get("threadPoolSize"));


                    if (serverUrl == null || serverUrl.equals("")) {
                        parameter = new Class[]{String.class, Integer.class};
                        objs = new Object[]{configCode.getType(), threadPoolSize};
                    } else {
                        parameter = new Class[]{String.class, String.class, Integer.class};
                        objs = new Object[]{configCode.getType(), serverUrl, threadPoolSize};
                    }
                    Constructor constructor = ClassUtility.loadClass(className).getConstructor(parameter);
                    msgAdapter = (MsgAdapter) constructor.newInstance(objs);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return msgAdapter;
    }

    public boolean isSupportPerson() {
        Object o = supportsMap.get("supportPerson");
        if (o != null && o.toString().equalsIgnoreCase("true")) {
            return true;
        }
        return false;
    }

    public boolean isSupportPersonOrg() {
        Object o = supportsMap.get("supportPersonOrg");
        if (o != null && o.toString().equalsIgnoreCase("true")) {
            return true;
        }
        return false;
    }

    public Map getFileAdapterMap() {
        return fileAdapterMap;
    }

    public static void main(String[] args) {
        System.out.println(getInstance(null).isSupportPersonRole());
        System.out.println(getInstance(null).getQuery("Person").getSqlClause("BASIC").getMainClause());
    }
}