package net.ooder.scene.core.secure;

import net.ooder.common.JDSCommand;
import net.ooder.common.JDSException;
import net.ooder.context.JDSContext;
import net.ooder.engine.ConnectInfo;
import net.ooder.engine.ConnectionHandle;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.msg.Msg;
import net.ooder.server.JDSClientService;

/**
 * SecureSceneEngine的ConnectionHandle实现
 *
 * <p>用于JDSServer管理SceneEngine连接生命周期。</p>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class SecureSceneConnectionHandle implements ConnectionHandle {

    private static final long serialVersionUID = 1L;

    private final JDSClientService clientService;
    private JDSSessionHandle sessionHandle;
    private final String systemCode;
    private ConnectInfo connectInfo;
    private boolean connected = false;

    /**
     * 构造函数（供JDSServer反射创建使用）
     *
     * @param clientService JDSClientService实例
     * @param sessionHandle Session句柄
     * @param systemCode    系统代码
     */
    public SecureSceneConnectionHandle(JDSClientService clientService, JDSSessionHandle sessionHandle, String systemCode) {
        this.clientService = clientService;
        this.sessionHandle = sessionHandle;
        this.systemCode = systemCode;
        this.connectInfo = clientService != null ? clientService.getConnectInfo() : null;
        this.connected = this.connectInfo != null;
    }

    @Override
    public JDSClientService getClient() throws JDSException {
        return this.clientService;
    }

    @Override
    public ConnectInfo getConnectInfo() {
        return this.connectInfo;
    }

    @Override
    public void connect(JDSContext context) throws JDSException {
        // SceneEngine的连接逻辑在SecureSceneEngineProxy中处理
        this.connected = true;
    }

    @Override
    public boolean isconnect() throws JDSException {
        return this.connected;
    }

    @Override
    public void disconnect() throws JDSException {
        // 断开连接时的清理逻辑
        if (clientService instanceof SecureSceneEngineProxy) {
            SecureSceneEngineProxy proxy = (SecureSceneEngineProxy) clientService;
            if (proxy.isSceneClientLoggedIn()) {
                try {
                    proxy.getSceneClient().getSessionId();
                } catch (Exception e) {
                    // 忽略异常
                }
            }
        }
        this.connected = false;
    }

    @Override
    public void receive(String receiveStr) throws JDSException {
        // SceneEngine不需要处理receive
    }

    @Override
    public boolean send(String msgStr) throws JDSException {
        // SceneEngine不需要处理send
        return true;
    }

    @Override
    public boolean repeatMsg(Msg msg, JDSSessionHandle handle) throws JDSException {
        // SceneEngine不需要处理repeatMsg
        return true;
    }

    @Override
    public boolean repeatCommand(JDSCommand command, JDSSessionHandle handle) throws JDSException {
        // SceneEngine不需要处理repeatCommand
        return true;
    }

    @Override
    public boolean send(JDSCommand command) throws JDSException {
        // SceneEngine不需要处理send
        return true;
    }

    /**
     * 获取SessionHandle
     *
     * @return JDSSessionHandle
     */
    public JDSSessionHandle getSessionHandle() {
        return this.sessionHandle;
    }

    /**
     * 设置SessionHandle
     *
     * @param sessionHandle JDSSessionHandle
     */
    public void setSessionHandle(JDSSessionHandle sessionHandle) {
        this.sessionHandle = sessionHandle;
    }

    /**
     * 获取系统代码
     *
     * @return 系统代码
     */
    public String getSystemCode() {
        return this.systemCode;
    }
}
