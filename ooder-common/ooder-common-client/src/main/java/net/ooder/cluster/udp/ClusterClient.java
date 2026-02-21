/**
 * $RCSfile: ClusterClient.java,v $
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
package net.ooder.cluster.udp;

import net.ooder.cluster.ServerNode;
import net.ooder.cluster.ServerNodeList;
import net.ooder.common.ConfigCode;
import net.ooder.common.SystemStatus;
import net.ooder.config.CApplication;
import net.ooder.org.Person;
import net.ooder.server.SubSystem;

import java.util.List;
import java.util.Map;

public interface ClusterClient {

    void login();

    void stop();

    void start();

    UDPClient getUDPClient();

    void login(Boolean init);

    boolean isLogin();

    boolean send(String msgStr);

    void updateTaskStatus(String id, String readed);

    void reboot();

    public void reLoadConfig();

    List<ServerNode> getAllServer();

    ServerNode getServerNodeById(String nodeId);

    ServerNodeList getServerNodeListByConfigCode(ConfigCode configCode);

    Map<String, ServerNode> getAllServerMap();

    CApplication getApplication(ConfigCode systemCode);

    List<CApplication> getApplications();

    Map<ConfigCode, CApplication> getApplicationMap();

    SubSystem getSystem(String systemCode);

    List<SubSystem> getAllSystem();

    public SystemStatus getSystemStatus(String systemCode);

    public Person getAdminPerson(String systemCode);

}
