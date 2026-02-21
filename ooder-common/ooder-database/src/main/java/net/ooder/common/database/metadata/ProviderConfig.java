/**
 * $RCSfile: ProviderConfig.java,v $
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
package net.ooder.common.database.metadata;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.common.CommonConfig;
import net.ooder.common.SystemStatus;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.Constants;

import java.util.UUID;


public class ProviderConfig {
    private String configName;
    private String configKey;
    private String driver;
    private String serverURL;
    private String username;
    private String password;
    private int minConnections = 5;
    private int maxConnections = 50;
    private int maxIdleTime = 300;
    private int checkIdlePeriod = 300;
    private int checkoutTimeout = 30000;

    SystemStatus status = SystemStatus.OFFLINE;


    private int connectionTimeout = 30 * 1000;

    private boolean mysqlUseUnicode;

    private String encoding;

    protected static Log log = LogFactory.getLog(Constants.COMMON_CONFIGKEY, ProviderConfig.class);


    public ProviderConfig(String configKey) {
        this.configKey = configKey;
        loadProperties();
    }

    public ProviderConfig() {
        this.configKey = UUID.randomUUID().toString();

    }


    @JSONField(serialize = false)
    public SystemStatus getStatus() {
        return status;
    }

    public void setStatus(SystemStatus status) {
        this.status = status;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public int getMinConnections() {
        return minConnections;
    }

    public void setMinConnections(int minConnections) {
        this.minConnections = minConnections;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public int getMaxIdleTime() {
        return maxIdleTime;
    }

    public void setMaxIdleTime(int maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
    }

    public int getCheckIdlePeriod() {
        return checkIdlePeriod;
    }

    public void setCheckIdlePeriod(int checkIdlePeriod) {
        this.checkIdlePeriod = checkIdlePeriod;
    }

    public int getCheckoutTimeout() {
        return checkoutTimeout;
    }

    public void setCheckoutTimeout(int checkoutTimeout) {
        this.checkoutTimeout = checkoutTimeout;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public boolean isMysqlUseUnicode() {
        return mysqlUseUnicode;
    }

    public void setMysqlUseUnicode(boolean mysqlUseUnicode) {
        this.mysqlUseUnicode = mysqlUseUnicode;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }


    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getServerURL() {
        return serverURL;
    }

    public void setServerURL(String serverURL) {
        this.serverURL = serverURL;
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

    private void loadProperties() {

        driver = CommonConfig.getValue(configKey + ".database.driver");
        serverURL = CommonConfig.getValue(configKey + ".database.serverURL");
        username = CommonConfig.getValue(configKey + ".database.username");
        password = CommonConfig.getValue(configKey + ".database.password");
        final String minCons = CommonConfig.getValue(configKey + ".database.minConnections");
        final String maxCons = CommonConfig.getValue(configKey + ".database.maxConnections");
        final String conTimeout = CommonConfig.getValue(configKey + ".database.connectionTimeout");
        final String mit = CommonConfig.getValue(configKey + ".database.maxIdleTime");
        final String cip = CommonConfig.getValue(configKey + ".database.checkIdlePeriod");
        final String coto = CommonConfig.getValue(configKey + ".database.checkOutTimeOut");
        mysqlUseUnicode = Boolean.valueOf(CommonConfig.getValue(configKey + ".database.mysql.useUnicode")).booleanValue();
        encoding = CommonConfig.getValue(configKey + ".database.mysql.characterEncoding");
        try {
            if (minCons != null) {
                minConnections = Integer.parseInt(minCons);
            }
            if (maxCons != null) {
                maxConnections = Integer.parseInt(maxCons);
            }
            if (conTimeout != null) {
                connectionTimeout = Integer.parseInt(conTimeout);
            }
            if (mit != null) {
                maxIdleTime = Integer.parseInt(mit);
            }
            if (cip != null) {
                checkIdlePeriod = Integer.parseInt(cip);
            }
            if (coto != null) {
                checkoutTimeout = Integer.parseInt(coto);
            }

        } catch (final Exception e) {
            log.error("Error: could not parse default pool properties. " + "Make sure the values exist and are correct.", e);
            e.printStackTrace();
        }

    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object != null && object instanceof ProviderConfig) {
            if (configKey != null && ((ProviderConfig) object).getConfigKey() != null) {
                return configKey.equals(((ProviderConfig) object).getConfigKey());
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

}


