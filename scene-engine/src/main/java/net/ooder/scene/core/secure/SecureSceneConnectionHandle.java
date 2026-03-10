package net.ooder.scene.core.secure;

import net.ooder.common.JDSCommand;
import net.ooder.common.JDSException;
import net.ooder.context.JDSContext;
import net.ooder.engine.ConnectInfo;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.server.JDSClientService;

/**
 * SecureSceneEngine的ConnectionHandle实现
 *
 * <p>用于JDSServer管理SceneEngine连接生命周期。</p>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.0
 */
public class SecureSceneConnectionHandle {

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

    public JDSClientService getClient() throws JDSException {
        return this.clientService;
    }

    public ConnectInfo getConnectInfo() {
        return this.connectInfo;
    }

    public void connect(JDSContext context) throws JDSException {
        this.connected = true;
    }

    public boolean isconnect() throws JDSException {
        return this.connected;
    }

    public void disconnect() throws JDSException {
        this.connected = false;
    }

    public void onException(JDSException exception) {
        // 处理异常情况
    }

    public void onCommand(JDSCommand command) {
        // 处理命令
    }

    public JDSSessionHandle getSessionHandle() {
        return this.sessionHandle;
    }

    public void setSessionHandle(JDSSessionHandle sessionHandle) {
        this.sessionHandle = sessionHandle;
    }

    public String getSystemCode() {
        return this.systemCode;
    }

    public boolean isConnected() {
        return this.connected;
    }

    public void close() throws JDSException {
        disconnect();
    }
}
