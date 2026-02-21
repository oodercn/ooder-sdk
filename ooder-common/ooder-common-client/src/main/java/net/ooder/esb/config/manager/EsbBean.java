/**
 * $RCSfile: EsbBean.java,v $
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
package net.ooder.esb.config.manager;

import net.ooder.common.ContextType;
import net.ooder.common.EsbBeanType;
import net.ooder.common.TokenType;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.ClassUtility;
import net.ooder.esb.config.annotation.ExpressionTempAnnotationProxy;
import net.ooder.esb.config.xml.ExpressionTempXmlProxy;
import net.ooder.esb.util.ESBConstants;
import net.ooder.org.Person;
import net.ooder.org.PersonNotFoundException;
import net.ooder.server.OrgManagerFactory;
import net.ooder.server.SubSystem;

/**
 * time20200930
 * 增加，集群调用参数 设置
 *
 * @author wenzhang
 */

public class EsbBean {
    protected transient static final Log log = LogFactory.getLog(
            ESBConstants.CONFIG_KEY, EsbBean.class);
    private String id;
    private String cnname;
    private TokenType tokenType = TokenType.guest;
    private ContextType type;
    private EsbBeanType esbtype = EsbBeanType.Local;
    private String vfsPath;
    private String desc;
    private String path;



    private String username;
    private String driver;
    private String connectionProvider;
    private String maxconnection = "2000";
    private String minconnection = "200";
    private String timeout = "60000";
    private String userexpression;

    private String password;
    private String serverUrl;
    private String serverKey;
    private String expressionTemManager;
    private String formClassManager;
    private ServiceConfigManager manager;

    public EsbBean() {

    }

    public EsbBean(SubSystem system) {
        this.id = system.getEnname();
        this.cnname = system.getName();
        this.tokenType = system.getTokenType();
        this.esbtype = EsbBeanType.Cluster;
        this.serverUrl = system.getUrl();
        this.vfsPath = system.getVfsPath();
        this.serverKey = system.getEnname();
        this.type = ContextType.Server;
        this.path = system.getUrl();
        this.vfsPath = system.getVfsPath();
        try {
            Person person = OrgManagerFactory.getOrgManager(system.getConfigname()).getPersonByID(system.getAdminId());
            this.username = person.getName();
        } catch (PersonNotFoundException e) {
            e.printStackTrace();
        }


    }
    public String getVfsPath() {
        return vfsPath;
    }

    public void setVfsPath(String vfsPath) {
        this.vfsPath = vfsPath;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getConnectionProvider() {
        return connectionProvider;
    }

    public void setConnectionProvider(String connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public String getMaxconnection() {
        return maxconnection;
    }

    public void setMaxconnection(String maxconnection) {
        this.maxconnection = maxconnection;
    }

    public String getMinconnection() {
        return minconnection;
    }

    public void setMinconnection(String minconnection) {
        this.minconnection = minconnection;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public String getUserexpression() {
        return userexpression;
    }

    public void setUserexpression(String userexpression) {
        this.userexpression = userexpression;
    }

    public void setManager(ServiceConfigManager manager) {
        this.manager = manager;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }


    public String getCnname() {
        return cnname;
    }

    public void setCnname(String cnname) {
        this.cnname = cnname;
    }


    public String getExpressionTemManager() {
        return expressionTemManager;
    }

    public void setExpressionTemManager(String expressionTemManager) {
        this.expressionTemManager = expressionTemManager;
    }

    public String getFormClassManager() {
        return formClassManager;
    }

    public void setFormClassManager(String formClassManager) {
        this.formClassManager = formClassManager;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ContextType getType() {
        return type;
    }

    public void setType(ContextType type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EsbBeanType getEsbtype() {
        return esbtype;
    }

    public void setEsbtype(EsbBeanType esbtype) {
        this.esbtype = esbtype;
    }

    public ServiceConfigManager getManager() {
        if (manager == null) {
            try {
                if (this.getExpressionTemManager() != null) {
                    Class managerClass = ClassUtility.loadClass(this.getExpressionTemManager());
                    manager = (ServiceConfigManager) managerClass.getConstructor(new Class[]{EsbBean.class}).newInstance(this);
                } else if (this.getPath() != null && this.getPath().trim().endsWith("xml")) {
                    manager = new ExpressionTempXmlProxy(this);
                } else {
                    manager = new ExpressionTempAnnotationProxy(this);
                }
            } catch (Exception e) {
                log.error("ExpressionTempManager Reflect Construct failed!!!!", e);
                e.printStackTrace();
            }
        }
        return manager;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getServerKey() {
        return serverKey;
    }

    public void setServerKey(String serverKey) {
        this.serverKey = serverKey;
    }

}