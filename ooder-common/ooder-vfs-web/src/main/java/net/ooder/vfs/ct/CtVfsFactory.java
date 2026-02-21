package net.ooder.vfs.ct;

import  net.ooder.common.JDSException;
import  net.ooder.config.JDSConfig;
import  net.ooder.context.JDSActionContext;
import  net.ooder.server.JDSClientService;
import  net.ooder.vfs.VFSConstants;
import  net.ooder.vfs.ct.admin.CtAdminVfsServiceImpl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * CtVfsFactory 用于创建和获取 VFS 服务实例。
 */
public class CtVfsFactory {

    static Map<JDSClientService, CtVfsService> clientMap = new HashMap<>();

    /**
     * 获取 CtVfsService 实例。
     * @return CtVfsService 实例
     */
    public static CtVfsService getCtVfsService() {
        CtVfsService vfsService = (CtVfsService) JDSActionContext.getActionContext().getContext().get(VFSConstants.VFSContextKey);
        if (vfsService == null) {
            vfsService = new CtAdminVfsServiceImpl();
            JDSActionContext.getActionContext().getContext().put(VFSConstants.VFSContextKey, vfsService);
        }
        return vfsService;
    }

    /**
     * 获取本地缓存路径。
     * @return 本地缓存路径
     */
    public static String getLocalCachePath() {
        String localPath = JDSConfig.Config.tempPath().getPath() + File.separator + "md5hash" + File.separator;
        return localPath;
    }

    /**
     * 获取指定客户端的 VFS 服务实例。
     * @param client JDS客户端服务
     * @return CtVfsService 实例
     * @throws JDSException JDS异常
     */
    public static CtVfsService getMyVfsClient(JDSClientService client) throws JDSException {
        CtVfsService vfsService = clientMap.get(client);
        if (client == null) {
            vfsService = new CtVfsServiceImpl(client);
            clientMap.put(client, vfsService);
        }
        return vfsService;
    }
}
