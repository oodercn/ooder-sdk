/**
 * $RCSfile: HsqlDbServer.java,v $
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
package net.ooder.hsql;

import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.org.conf.OrgConstants;
import org.hsqldb.Server;

import java.sql.Connection;
import java.sql.DriverManager;

public class HsqlDbServer {

    private static final Log logger = LogFactory.getLog(OrgConstants.CONFIG_KEY.getType(), HsqlDbServer.class);

    static {
        try {
            Class.forName("org.hsqldb.jdbcDriver");
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    private String path;
    private final String dbName;

    private Integer port = 10523;

    private String serverProps;
    private String url;
    private String user;
    private String password;
    private boolean logEnabled;
    private Server server;

    public HsqlDbServer(String url, String user, String password, String path, String dbName, Integer port, boolean logEnabled) {
        this.url = url;
        this.user = user;
        this.password = password;
        if (port != null) {
            this.port = port;
        }
        this.path = path;

        this.dbName = dbName;
        if (serverProps != null) {
            this.serverProps = serverProps;

        }
        this.logEnabled = logEnabled;
    }


    public void startup() {
        server = new Server();
        server.setDatabaseName(0, dbName);


        if (!path.endsWith("/")) {
            path = path + "/";
        }

        server.setDatabasePath(0, path + dbName);
        server.setSilent(true);

        server.setPort(port);
        server.start();
    }

    public void shutdown() {
        server.stop();
        server = null;
    }

    public Connection newConnection() {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            logger.error("", e);
            return null;
        }
    }
}
