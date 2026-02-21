/**
 * $RCSfile: ClusterMananer.java,v $
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

import net.ooder.common.ConfigCode;

import java.util.List;
import java.util.Map;

/**
 * 集群管理器接口。
 * <p>
 * Title: ooder系统管理系统
 * </p>
 * <p>
 * Description: 集群管理器接口，提供集群节点管理功能
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 *
 * @author ooder team
 * @version 1.0
 * @since 2025-08-25
 */
public interface ClusterMananer {

	public ServerNode getSubServer(String sessionId, ConfigCode systemCode);

	public Map<String, ServerNode> getServerMap(ConfigCode configCode);

	public List<ServerNode> getServerNodeListOrderByPersonCount(ConfigCode configCode);

	public boolean experssPar(String expressStr);

	public void clearUser(String personId);

	public String getServerIdBySessionId(String sessionId);

}
