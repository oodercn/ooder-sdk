/**
 * $RCSfile: DBConfig.java,v $
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
package net.ooder.cluster;

import net.ooder.common.CommonConfig;

public class DBConfig {

    String configKey;
     String driver;
    String serverURL;
    String username;
    String password;
    Boolean mysqlUseUnicode;
    Integer minConnections;
    int maxConnections;
    int connectionTimeout;
    String encoding;
    int maxIdleTime;
    int checkIdlePeriod;
    int checkoutTimeout;

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
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

    public Boolean getMysqlUseUnicode() {
        return mysqlUseUnicode;
    }

    public void setMysqlUseUnicode(Boolean mysqlUseUnicode) {
        this.mysqlUseUnicode = mysqlUseUnicode;
    }

    public Integer getMinConnections() {
        return minConnections;
    }

    public void setMinConnections(Integer minConnections) {
        this.minConnections = minConnections;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
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

    DBConfig(String configKey) {
        this.configKey = configKey;
        loadProperties();
    }


    private void loadProperties() {

        this.driver = CommonConfig.getValue(configKey + ".database.driver");
        this.serverURL = CommonConfig.getValue(configKey + ".database.serverURL");
        this.username = CommonConfig.getValue(configKey + ".database.username");
        this.password = CommonConfig.getValue(configKey + ".database.password");

        final String minCons = CommonConfig.getValue(configKey + ".database.minConnections");
        final String maxCons = CommonConfig.getValue(configKey + ".database.maxConnections");
        final String conTimeout = CommonConfig.getValue(configKey + ".database.connectionTimeout");
        final String mit = CommonConfig.getValue(configKey + ".database.maxIdleTime");
        final String cip = CommonConfig.getValue(configKey + ".database.checkIdlePeriod");
        final String coto = CommonConfig.getValue(configKey + ".database.checkOutTimeOut");
        this.mysqlUseUnicode = Boolean.valueOf(CommonConfig.getValue(configKey + ".database.mysql.useUnicode")).booleanValue();
        this.encoding = CommonConfig.getValue(configKey + ".database.mysql.characterEncoding");
        try {
            if (minCons != null) {
                this.minConnections = Integer.parseInt(minCons);
            }
            if (maxCons != null) {
                this.maxConnections = Integer.parseInt(maxCons);
            }
            if (conTimeout != null) {
                this.connectionTimeout = Integer.parseInt(conTimeout);
            }
            if (mit != null) {
                this.maxIdleTime = Integer.parseInt(mit);
            }
            if (cip != null) {
                this.checkIdlePeriod = Integer.parseInt(cip);
            }
            if (coto != null) {
                this.checkoutTimeout = Integer.parseInt(coto);
            }

        } catch (final Exception e) {
            // log.error("Error: could not parse default pool properties. " + "Make sure the values exist and are correct.", e);
            e.printStackTrace();
        }
    }
}
