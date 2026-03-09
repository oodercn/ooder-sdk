package net.ooder.scene.core.secure;

import net.ooder.engine.JDSSessionHandle;
import net.ooder.scene.core.AdminClient;
import net.ooder.scene.core.SceneClient;
import net.ooder.scene.core.SceneEngine;

/**
 * SecureSceneEngineProxy 的安全验证器
 *
 * <p>负责验证session有效性和客户端登录状态。</p>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class SecureSceneEngineValidator {

    private final JDSSessionHandle sessionHandle;
    private final SceneEngine sceneEngine;

    public SecureSceneEngineValidator(JDSSessionHandle sessionHandle, SceneEngine sceneEngine) {
        this.sessionHandle = sessionHandle;
        this.sceneEngine = sceneEngine;
    }

    /**
     * 验证session有效性
     *
     * @throws SecurityException session无效时抛出
     */
    public void validateSession() {
        if (sessionHandle == null) {
            throw new SecurityException("Session handle is null");
        }

        String sessionId = sessionHandle.getSessionID();
        if (sessionId == null || sessionId.isEmpty()) {
            throw new SecurityException("Session ID is empty");
        }

        // 使用SceneEngine验证session
        if (!sceneEngine.validateSession(sessionId)) {
            throw new SecurityException("Session is invalid or expired: " + sessionId);
        }
    }

    /**
     * 验证session和SceneClient
     *
     * @param sceneClient SceneClient实例
     * @throws SecurityException 验证失败时抛出
     */
    public void validateSessionAndClient(SceneClient sceneClient) {
        validateSession();

        if (sceneClient == null) {
            throw new SecurityException("SceneClient is not logged in. Please call login() first.");
        }
    }

    /**
     * 验证session和AdminClient
     *
     * @param adminClient AdminClient实例
     * @throws SecurityException 验证失败时抛出
     */
    public void validateSessionAndAdminClient(AdminClient adminClient) {
        validateSession();

        if (adminClient == null) {
            throw new SecurityException("AdminClient is not logged in. Please call adminLogin() first.");
        }
    }
}
