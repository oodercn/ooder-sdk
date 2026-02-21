package net.ooder.vfs.ct.admin;

import  net.ooder.common.*;
import  net.ooder.common.md5.MD5InputStream;
import  net.ooder.context.JDSActionContext;
import  net.ooder.context.JDSContext;
import  net.ooder.engine.ConnectInfo;
import  net.ooder.engine.ConnectionHandle;
import  net.ooder.engine.JDSSessionHandle;
import  net.ooder.jds.core.esb.EsbUtil;
import  net.ooder.server.JDSClientService;
import  net.ooder.server.JDSServer;
import  net.ooder.vfs.*;
import  net.ooder.vfs.bigfile.BigFileUtil;
import  net.ooder.vfs.ct.CtCacheManager;
import  net.ooder.vfs.ct.CtVfsService;
import  net.ooder.vfs.sync.SyncFactory;
import org.apache.http.concurrent.FutureCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

public class CtAdminVfsServiceImpl implements CtVfsService {

    private JDSClientService clientService;

    public CtAdminVfsServiceImpl() {

        try {
            if (JDSServer.getInstance().getAdminUser() != null) {
                this.clientService = JDSServer.getInstance().getAdminClient();
                JDSActionContext.getActionContext().getContext().put(JDSContext.SYSCODE, clientService.getSystemCode());
                JDSActionContext.getActionContext().getContext().put(JDSContext.JSESSIONID, clientService.getSessionHandle().getSessionID());
            }
        } catch (JDSException e) {

            e.printStackTrace();
        }


    }

    public ConnectInfo connectInfo;
    private ConnectionHandle connectionHandle;

    @Override
    public void clearCache(String path) throws JDSException {
        CtCacheManager.getInstance().clearCache(path);
    }


    @Override
    public void removeCache(String path) throws JDSException {
        CtCacheManager.getInstance().removeCache(path);
    }
    @Override
    public void clearFileCache(String path) throws JDSException {
        CtCacheManager.getInstance().clearFileCache(path);
    }

    @Override
    public void clearFileObjectCache(String hash) throws JDSException {
        CtCacheManager.getInstance().clearFileObjectCache(hash);
    }

    @Override
    public void clearFileVersionCache(String path) throws JDSException {
        CtCacheManager.getInstance().clearFileVersionCache(path);
    }

    @Override
    public Folder getFolderById(String folderId) throws JDSException {
        return CtCacheManager.getInstance().getFolderById(folderId);
    }

    @Override
    public List<Folder> loadFolers(Set<String> folderIds) throws JDSException {
        return CtCacheManager.getInstance().loadFolers(folderIds);
    }

    @Override
    public List<FileVersion> loadVersionByIds(Set<String> versionIds) throws JDSException {
        return CtCacheManager.getInstance().loadVersionByIds(versionIds);
    }


    @Override
    public List<FileObject> loadObjects(Set<String> objectIds) throws JDSException {
        return CtCacheManager.getInstance().loadObjects(objectIds);
    }

    @Override
    public Folder getFolderByPath(String path) throws JDSException {

        return CtCacheManager.getInstance().getFolderByPath(path);
    }

    @Override
    public FileInfo getFileById(String fileId) throws JDSException {

        return CtCacheManager.getInstance().getFileById(fileId);
    }

    @Override
    public List<FileInfo> loadFiles(Set<String> loadFiles) throws JDSException {
        return CtCacheManager.getInstance().loadFiles(loadFiles);
    }

    @Override
    public List<FileView> loadViews(Set<String> viewIds) throws JDSException {
        return CtCacheManager.getInstance().loadViews(viewIds);
    }

    @Override
    public FileInfo getFileByPath(String path) throws JDSException {

        return CtCacheManager.getInstance().getFileByPath(path);
    }

    @Override
    public FileVersion getFileVersionById(String versionId) throws JDSException {

        return CtCacheManager.getInstance().getFileVersionById(versionId);
    }

    @Override
    public FileVersion getFileVersionByPath(String path) throws JDSException {
        return CtCacheManager.getInstance().getFileVersionByPath(path);
    }

    @Override
    public List<FileVersion> getFileVersionsByHash(String hash) throws JDSException {
        return CtCacheManager.getInstance().getFileVersionsByHash(hash);
    }


    @Override
    public FileObject getFileObjectById(String fileObjectId) throws JDSException {

        return CtCacheManager.getInstance().getFileObjectById(fileObjectId);
    }

    @Override
    public FileView getFileViewById(String viewId) throws JDSException {

        return CtCacheManager.getInstance().getFileViewById(viewId);
    }

    @Override
    public Integer writeLine(String objectId, String str) throws JDSException {

        return CtCacheManager.getInstance().writeLine(objectId, str);
    }

    @Override
    public List<String> readLine(String objectId, List<Integer> lineNums) throws JDSException {
        return CtCacheManager.getInstance().readLine(objectId, lineNums);
    }

    @Override
    public FileView createView(String fileVersionId, String fileObjectId, Integer fileIndex) throws JDSException {
        return CtCacheManager.getInstance().createView(fileVersionId, fileObjectId, fileIndex);
    }

    @Override
    public FileLink getFileLinkById(String linkId) throws JDSException {
        return CtCacheManager.getInstance().getFileLinkById(linkId);
    }

    @Override
    public void deleteFolder(String folderId) throws JDSException {
        CtCacheManager.getInstance().deleteFolder(folderId);
    }

    @Override
    public void deleteFile(String fileInfoId) throws JDSException {
        CtCacheManager.getInstance().deleteFile(fileInfoId);
    }

    @Override
    public void deleteFileVersion(String versionId) throws JDSException {
        CtCacheManager.getInstance().deleteFileVersionById(versionId);
    }

    @Override
    public MD5InputStream getInputStreamByVersionid(String fileVersionId) throws JDSException {

        return CtCacheManager.getInstance().downLoadVersion(fileVersionId);
    }

    @Override
    public void copyFolder(String spath, String tPath) throws JDSException {
        CtCacheManager.getInstance().copyFolder(spath, tPath);
    }

    @Override
    public void cloneFolder(String spath, String tPath) throws JDSException {
        CtCacheManager.getInstance().cloneFolder(spath, tPath);
    }

    @Override
    public FileInfo createFile(String path) throws JDSException {
        return CtCacheManager.getInstance().createFile(path);

    }

    @Override
    public FileInfo createFile(String path, String name) throws JDSException {
        return CtCacheManager.getInstance().createFile(path, name);

    }

    @Override
    public FileInfo createFile(String path, String name, String description) throws JDSException {
        return CtCacheManager.getInstance().createFile(path, name, description);
    }

    @Override
    public FileInfo saveFileAsContent(String path, String content, String encoding) throws JDSException {
        return CtCacheManager.getInstance().saveFileAsContent(path, content, encoding, clientService.getConnectInfo().getUserID());
    }

    @Override
    public StringBuffer readFileAsString(String path, String encoding) throws JDSException {
        return CtCacheManager.getInstance().readFileAsString(path, encoding);
    }

    @Override
    public FileInfo updateFileInfo(FileInfo fileInfo, String name, String description) throws JDSException {
        return CtCacheManager.getInstance().updateFileInfo(fileInfo.getPath(), name, description);
    }

    @Override
    public Folder updateFolderInfo(Folder folder, String name, String description, FolderType type) throws JDSException {
        return CtCacheManager.getInstance().updateFolderInfo(folder.getPath(), name, description, type);
    }


    @Override
    public Folder updateFolderInfo(Folder folder, String name, String description) throws JDSException {
        return CtCacheManager.getInstance().updateFolderInfo(folder.getPath(), name, description, folder.getFolderType());
    }

    @Override
    public Folder updateFolderState(Folder folder, FolderState state) throws JDSException {
        return CtCacheManager.getInstance().updateFolderState(folder.getPath(), state);
    }

    @Override
    public void updateFileVersionInfo(String fileVersionId, String hash) throws JDSException {
        CtCacheManager.getInstance().updateFileVersionInfo(fileVersionId, hash);

    }

    @Override
    public FileVersion createFileVersion(String path, String filehash) throws JDSException {
        return CtCacheManager.getInstance().createFileVersion(path, filehash);
    }


    @Override
    public void updateFileViewInfo(FileView view) throws JDSException {
        CtCacheManager.getInstance().updateFileViewInfo(view);
    }

    @Override
    public FileVersion upload(String path, MD5InputStream inputstream, String personId) throws JDSException {
        return CtCacheManager.getInstance().upload(path, inputstream, personId);
    }

    @Override
    public FileObject createFileObject(MD5InputStream inputstream) throws JDSException {
        return CtCacheManager.getInstance().createFileObject(inputstream);
    }

    @Override
    public FileObject createFileObject(File file) throws JDSException {
        return CtCacheManager.getInstance().createFileObject(file);
    }

    @Override
    public void syncUpload(String path, MD5InputStream inputstream, String personId) throws JDSException {
        CtCacheManager.getInstance().syncUpload(path, inputstream, personId, null);
    }

    @Override
    public void syncUpload(String path, MD5InputStream inputstream, String personId, FutureCallback callback) throws JDSException {

        CtCacheManager.getInstance().syncUpload(path, inputstream, personId, callback);
    }


    @Override
    public void syncUpload(String path, File file, String personId) throws JDSException {
        try {
            if (file.exists() && file.length() > BigFileUtil.bigfileSize) {
                CtCacheManager.getInstance().bigFileUpload(file.getAbsolutePath(), path, personId);
            } else {
                CtCacheManager.getInstance().syncUpload(path, new MD5InputStream(new FileInputStream(file)), personId, null);
            }

        } catch (FileNotFoundException e) {
            throw new JDSException(e);
        }
    }

    @Override
    public FileVersion upload(String path, File file, String personId) throws JDSException {
        try {
            return CtCacheManager.getInstance().upload(path, new MD5InputStream(new FileInputStream(file)), personId);
        } catch (FileNotFoundException e) {
            throw new JDSException(e);
        }
    }

    @Override
    public Folder mkDir(String path) throws JDSException {
        return CtCacheManager.getInstance().mkDir(path);
    }

    @Override
    public Folder mkDir(String path, String description) throws JDSException {
        return CtCacheManager.getInstance().mkDir(path, description, FolderType.folder);
    }

    @Override
    public Folder mkDir(String path, String description, FolderType type) throws JDSException {
        return CtCacheManager.getInstance().mkDir(path, description, type);
    }

    @Override
    public FileInfo copyFile(FileInfo fileByPath, Folder tFolder) throws JDSException {
        return CtCacheManager.getInstance().copyFile(fileByPath.getPath(), tFolder.getPath());
    }

    @Override
    public void pull(String vfspath, String localPath) throws JDSException {
        try {
            SyncFactory.getInstance().pull(Paths.get(localPath), vfspath);
        } catch (IOException e) {
            throw new JDSException(e);
        }
    }

    @Override
    public void push(String vfspath, String localPath) throws JDSException {
        try {
            SyncFactory.getInstance().push(Paths.get(localPath), vfspath);
        } catch (Exception e) {
            throw new JDSException(e);
        }
    }

    @Override
    public MD5InputStream downLoad(String path) throws JDSException {
        return CtCacheManager.getInstance().downLoad(path);
    }

    @Override
    public MD5InputStream downLoadByHash(String hash) throws JDSException {
        return CtCacheManager.getInstance().downLoadByHash(hash);
    }

    @Override
    public MD5InputStream downLoadByObjectId(String objectId) throws JDSException {
        return CtCacheManager.getInstance().downLoadByObjectId(objectId);
    }

    @Override
    public MD5InputStream downLoadVersion(String versionId) throws JDSException {
        return CtCacheManager.getInstance().downLoadVersion(versionId);
    }

    @Override
    public FileObject getFileObjectByHash(String hash) {
        FileObject fileObject = null;
        try {
            fileObject = CtCacheManager.getInstance().getFileObjectByHash(hash);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return fileObject;

    }

    @Override
    public void updateFileObject(FileObject object) {
        CtCacheManager.getInstance().updateFileObject(object);
    }

    @Override
    public void deleteFileObject(String ID) {
        CtCacheManager.getInstance().deleteFileObject(ID);
    }


    @Override
    public FileObject createFileObject() {
        return CtCacheManager.getInstance().createFileObject();
    }

    @Override
    public FileObject createFileObjectAsContent(String content) {
        return CtCacheManager.getInstance().createFileObjectAsContent(content, VFSConstants.Default_Encoding);
    }


    @Override
    public ConfigCode getConfigCode() {
        return this.getJdsClient().getConfigCode();
    }

    @Override
    public JDSSessionHandle getSessionHandle() {
        return null;
    }

    @Override
    public void connect(ConnectInfo connInfo) throws JDSException {
        JDSClientService client = getJdsClient();
        this.connectInfo = connInfo;
        client.connect(connInfo);
    }

    @Override
    public ReturnType disconnect() throws JDSException {
        this.connectInfo = null;
        return getJdsClient().disconnect();
    }

    @Override
    public ConnectInfo getConnectInfo() {
        JDSClientService client = getJdsClient();
        if (connectInfo == null && client != null) {
            connectInfo = client.getConnectInfo();
        }
        return this.connectInfo;

    }

    @Override
    public ConnectionHandle getConnectionHandle() {
        JDSClientService client = getJdsClient();
        if (connectionHandle == null && client != null) {
            this.connectionHandle = client.getConnectionHandle();
        }
        return connectionHandle;
    }

    @Override
    public void setConnectionHandle(ConnectionHandle handle) {
        JDSClientService client = getJdsClient();
        this.connectionHandle = handle;
        if (client != null) {
            client.setConnectionHandle(handle);
        }

    }


    public JDSClientService getJdsClient() {
        JDSClientService client = EsbUtil.parExpression(JDSClientService.class);
        return client;
    }

    @Override
    public JDSContext getContext() {
        return JDSActionContext.getActionContext();
    }

    @Override
    public void setContext(JDSContext context) {
        JDSActionContext.setContext(context);
    }

    @Override
    public String getSystemCode() {
        return getContext().getSystemCode();
    }
}
