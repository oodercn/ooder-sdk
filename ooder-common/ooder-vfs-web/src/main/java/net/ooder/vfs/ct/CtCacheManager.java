package net.ooder.vfs.ct;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.ooder.common.FolderState;
import net.ooder.common.FolderType;
import net.ooder.common.JDSException;
import net.ooder.common.cache.Cache;
import net.ooder.common.cache.CacheManagerFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.md5.MD5;
import net.ooder.common.md5.MD5InputStream;
import net.ooder.common.util.CnToSpell;
import net.ooder.common.util.IOUtility;
import net.ooder.common.util.StringUtility;
import net.ooder.config.JDSConfig;
import net.ooder.context.JDSActionContext;
import net.ooder.context.JDSContext;
import net.ooder.hsql.HsqlDbCacheManager;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.org.conf.OrgConstants;
import net.ooder.server.context.MinServerActionContextImpl;
import net.ooder.vfs.*;
import net.ooder.vfs.bigfile.BigFileTools;
import net.ooder.vfs.bigfile.BigFileUtil;
import net.ooder.vfs.service.VFSClientService;
import net.ooder.vfs.service.VFSDiskService;
import net.ooder.vfs.service.VFSStoreService;
import net.ooder.web.LocalMultipartFile;
import net.ooder.web.RemoteConnectionManager;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.concurrent.FutureCallback;

import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class CtCacheManager implements Serializable {
    private static CtCacheManager instance;
    public static final String THREAD_LOCK = "Thread Lock";
    private static final Log log = LogFactory.getLog(OrgConstants.CONFIG_KEY.getType(), CtCacheManager.class);
    private static final String localDiskPath = StringUtility.replace(new File("").getAbsolutePath(), "\\", "/");

    public static CtCacheManager getInstance() {
        if (instance == null) {
            synchronized (THREAD_LOCK) {
                if (instance == null) {
                    instance = new CtCacheManager();
                }
            }
        }
        return instance;
    }


    private boolean cacheEnabled = true;
    public Cache<String, FileInfo> fileCache;
    public Cache<String, FileInfo> hsqlfileCache;


    public Cache<String, Folder> folderCache;
    public Cache<String, Folder> hsqlfolderCache;
    public Cache<String, String> filePathCache;
    public Cache<String, String> hsqlfilePathCache;
    public Cache<String, FileVersion> fileVersionCache;
    public Cache<String, FileVersion> hsqlfileVersionCache;


    public Cache<String, FileLink> fileLinkCache;
    public Cache<String, FileView> fileViewCache;
    public Cache<String, FileObject> fileObjectCache;
    public Cache<String, FileVersion> hsqlfileObjectCache;

    public Cache<String, String> fileHashObjectCache;
    public Cache<String, FileVersion> hsqlfileHashObjectCache;

    public int pageSize = 200;

    public int oncetasksize = 10;

    /**
     * Creates a new cache manager.
     */
    protected CtCacheManager() {
        initCache();
    }

    /**
     * Initializes all caches with the correct size and expiration time.
     */
    private void initCache() {


        if (cacheEnabled) {

            fileCache = CacheManagerFactory.createCache(VFSConstants.CONFIG_CTVFS_KEY, "fileCache", 10 * 1024 * 1024, 1000 * 60 * 60 * 24);

            hsqlfileCache = HsqlDbCacheManager.getCache(VFSConstants.CONFIG_CTVFS_KEY + ".fileCache");

            folderCache = CacheManagerFactory.createCache(VFSConstants.CONFIG_CTVFS_KEY, "folderCache", 10 * 1024 * 1024, 1000 * 60 * 60 * 24);

            hsqlfolderCache = HsqlDbCacheManager.getCache(VFSConstants.CONFIG_CTVFS_KEY + ".folderCache");

            filePathCache = CacheManagerFactory.createCache(VFSConstants.CONFIG_CTVFS_KEY, "filePathCache", 10 * 1024 * 1024, 1000 * 60 * 60 * 24);

            hsqlfilePathCache = HsqlDbCacheManager.getCache(VFSConstants.CONFIG_CTVFS_KEY + ".filePathCache");

            fileVersionCache = CacheManagerFactory.createCache(VFSConstants.CONFIG_CTVFS_KEY, "fileVersionCache", 30 * 1024 * 1024, 1000 * 60 * 60 * 24);

            hsqlfileVersionCache = HsqlDbCacheManager.getCache(VFSConstants.CONFIG_CTVFS_KEY + ".fileVersionCache");


            fileLinkCache = CacheManagerFactory.createCache(VFSConstants.CONFIG_CTVFS_KEY, "fileLinkCache", 10 * 1024 * 1024, 1000 * 60 * 60 * 24);

            fileViewCache = CacheManagerFactory.createCache(VFSConstants.CONFIG_CTVFS_KEY, "fileViewCache", 10 * 1024 * 1024, 1000 * 60 * 60 * 24);

            fileObjectCache = CacheManagerFactory.createCache(VFSConstants.CONFIG_CTVFS_KEY, "fileObjectCache", 10 * 1024 * 1024, 1000 * 60 * 60 * 24);

            hsqlfileObjectCache = HsqlDbCacheManager.getCache(VFSConstants.CONFIG_CTVFS_KEY + ".fileObjectCache");

            fileHashObjectCache = CacheManagerFactory.createCache(VFSConstants.CONFIG_CTVFS_KEY, "fileHashObjectCache", 10 * 1024 * 1024, 1000 * 60 * 60 * 24);

            hsqlfileHashObjectCache = HsqlDbCacheManager.getCache(VFSConstants.CONFIG_CTVFS_KEY + ".fileHashObjectCache");
        }

    }

    public void clearFileVersionCache(String path) throws JDSException {
        FileVersion version = this.getFileVersionByPath(path);
        if (version != null) {
            fileVersionCache.remove(version.getVersionID());
            hsqlfileVersionCache.remove(version.getVersionID());

        }
    }

    public void clearFileObjectCache(String hash) throws JDSException {
        FileObject obj = this.getFileObjectByHash(hash);
        if (obj != null) {
            this.fileHashObjectCache.remove(hash);
            this.fileObjectCache.remove(obj.getID());
            this.hsqlfileHashObjectCache.remove(hash);
            this.hsqlfileObjectCache.remove(obj.getID());

        }


    }


    public void clearFileCache(String path) throws JDSException {
        if (filePathCache.containsKey(path)) {
            FileInfo cfile = this.getFileByPath(path);
            if (cfile != null) {
                Set<String> versionIds = cfile.getVersionIds();
                for (String versionId : versionIds) {
                    fileVersionCache.remove(versionId);
                    hsqlfileVersionCache.remove(versionId);
                }
                fileCache.remove(cfile.getID());
                filePathCache.remove(cfile.getPath());
                hsqlfileCache.remove(cfile.getID());
                hsqlfilePathCache.remove(cfile.getPath());
            }
        }
    }

    public void removeCache(String path) throws JDSException {
        if (filePathCache.containsKey(path)) {
            Folder folder = this.getFolderByPath(path);
            if (folder != null) {
                folderCache.remove(folder.getID());
                filePathCache.remove(folder.getPath());
                hsqlfolderCache.remove(folder.getID());
                hsqlfilePathCache.remove(folder.getPath());
            }
        }
    }

    public void clearCache(String path) throws JDSException {
        clearCache(path, true);
    }

    public void clearCache(String path, boolean clearChild) throws JDSException {
        if (filePathCache.containsKey(path)) {
            Folder folder = this.getFolderByPath(path);
            if (folder != null) {
                for (Folder cfolder : folder.getChildrenList()) {
                    if (cfolder != null) {
                        if (clearChild) {
                            this.clearCache(cfolder.getPath(), clearChild);
                        }
                        folderCache.remove(cfolder.getID());
                        filePathCache.remove(cfolder.getPath());
                        hsqlfolderCache.remove(cfolder.getID());
                        hsqlfilePathCache.remove(cfolder.getPath());
                    }
                }
                for (FileInfo cfile : folder.getFileList()) {
                    if (cfile != null) {
                        clearFileCache(cfile.getPath());
                    }
                }
                filePathCache.remove(path);
                folderCache.remove(folder.getID());

                hsqlfolderCache.remove(folder.getID());
                hsqlfilePathCache.remove(path);
            }
        }
        filePathCache.remove(path);
    }


    /**
     * @param folderId
     * @return
     * @throws JDSException
     */
    public Folder getFolderById(String folderId) throws JDSException {


        Folder folder = null;

        if (!cacheEnabled) {
            folder = getVfsService().getFolderByID(folderId).getData();
        } else { // cache enabled
            synchronized (folderId) {
                folder = folderCache.get(folderId);
                if (folder == null) {
                    folder = getVfsService().getFolderByID(folderId).getData();
                    if (folder != null) {
                        folder = new CtFolder(folder);
                        folderCache.put(folder.getID(), folder);
                        filePathCache.put(folder.getPath(), folder.getID());
                    }
                    //判断是否存在脏读问题
                } else {
                    String cacheFolderId = filePathCache.get(folder.getPath());
                    if (cacheFolderId != null && !cacheFolderId.equals(folderId)) {
                        folder = getFolderById(cacheFolderId);
                        folderCache.remove(folderId);
                        hsqlfolderCache.remove(folderId);
                    }

                }
            }
        }

        return folder;
    }

    /**
     * @param fileLinkId
     * @return
     * @throws JDSException
     */
    public FileLink getFileLinkById(String fileLinkId) throws JDSException {
        FileLink fileLink = null;
        if (!cacheEnabled) {
            fileLink = getVfsService().getFileLinkByID(fileLinkId).getData();
        } else { // cache enabled
            fileLink = (FileLink) fileLinkCache.get(fileLinkId);
            if (fileLink == null) {
                fileLink = getVfsService().getFileLinkByID(fileLinkId).getData();
                fileLink = new CtFileLink(fileLink);
                fileLinkCache.put(fileLink.getID(), fileLink);
            }
        }

        return fileLink;
    }

    /**
     * @param fileObjectId
     * @return
     * @throws JDSException
     */
    public FileObject getFileObjectById(String fileObjectId) throws JDSException {
        FileObject fileObject = null;
        if (fileObjectId != null && !fileObjectId.equals("")) {
            synchronized (fileObjectId) {
                if (!cacheEnabled) {
                    fileObject = getVFSStoreService().getFileObjectByID(fileObjectId).getData();
                } else { // cache enabled
                    fileObject = fileObjectCache.get(fileObjectId);
                    if (fileObject == null) {
                        String objId = fileHashObjectCache.get(fileObjectId);
                        if (objId == null) {
                            fileObject = fileObjectCache.get(objId);
                        }
                    }
                    if (fileObject == null) {
                        fileObject = getVFSStoreService().getFileObjectByID(fileObjectId).getData();
                        if (fileObject == null) {
                            fileObject = getVFSStoreService().getFileObjectByHash(fileObjectId).getData();
                        }
                        if (fileObject != null) {
                            fileObject = new CtFileObject(fileObject);
                        } else {
                            fileObject = new CtFileObject();
                            fileObject.setHash(MD5.getHashString(""));
                            fileObject.setID(fileObjectId);
                            fileObject.setName("NULL");
                            fileObject.setCreateTime(System.currentTimeMillis());
                            fileObject.setLength(0L);
                        }

                        fileObjectCache.put(fileObject.getID(), fileObject);
                        fileHashObjectCache.put(fileObject.getHash(), fileObject.getID());
                    }
                }
            }

        }

        return fileObject;
    }

    /**
     * @param hash
     * @return
     * @throws JDSException
     */
    public FileObject getFileObjectByHash(String hash) throws JDSException {
        synchronized (hash) {
            FileObject fileObject = null;
            if (!cacheEnabled) {
                fileObject = getVFSStoreService().getFileObjectByHash(hash).getData();
            } else { // cache enabled
                String fileObjectId = (String) fileHashObjectCache.get(hash);
                if (fileObjectId == null) {
                    fileObject = getVFSStoreService().getFileObjectByHash(hash).getData();
                    if (fileObject != null) {
                        fileObject = new CtFileObject(fileObject);
                        fileHashObjectCache.put(fileObject.getHash(), fileObject.getID());
                        fileObjectCache.put(fileObject.getID(), fileObject);
                    }
                } else {
                    fileObject = this.getFileObjectById(fileObjectId);
                }
            }
            return fileObject;
        }
    }

    /**
     * @param fileViewId
     * @return
     * @throws JDSException
     */
    public FileView getFileViewById(String fileViewId) throws JDSException {
        FileView fileView = null;

        if (!cacheEnabled) {

            fileView = getVfsService().getFileViewByID(fileViewId).getData();

        } else { // cache enabled
            fileView = (FileView) fileViewCache.get(fileViewId);
            if (fileView == null) {
                fileView = getVfsService().getFileViewByID(fileViewId).getData();
                fileView = new CtFileView(fileView);
                fileViewCache.put(fileView.getID(), fileView);
            }
        }

        return fileView;
    }

    /**
     * @param versionId
     * @return
     * @throws JDSException
     */
    public FileVersion getFileVersionById(String versionId) {
        FileVersion version = null;

        if (!cacheEnabled) {
            version = getVfsService().getVersionById(versionId).getData();

        } else { // cache enabled
            synchronized (versionId) {
                version = fileVersionCache.get(versionId);
                if (version == null) {
                    version = getVfsService().getVersionById(versionId).getData();
                    if (version != null) {
                        version = new CtFileVersion(version);
                        fileVersionCache.put(version.getVersionID(), version);
                        filePathCache.put(version.getPath(), version.getVersionID());
                    }

                }
            }
        }

        return version;
    }

    /**
     * @param fileInfoId
     * @return
     * @throws JDSException
     */
    public FileInfo getFileById(String fileInfoId) throws JDSException {
        FileInfo fileInfo = null;
        if (!cacheEnabled) {
            fileInfo = getVfsService().getFileInfoByID(fileInfoId).getData();
        } else { // cache enabled
            synchronized (fileInfoId) {
                fileInfo = (FileInfo) fileCache.get(fileInfoId);
                if (fileInfo == null) {

                    fileInfo = getVfsService().getFileInfoByID(fileInfoId).getData();
                    if (fileInfo != null) {
                        fileInfo = new CtFile(fileInfo);
                        fileCache.put(fileInfoId, fileInfo);
                    } else {
                        fileCache.put(fileInfoId, null);
                    }
                } else {
                    String cacheId = filePathCache.get(fileInfo.getPath());
                    if (cacheId != null && !cacheId.equals(fileInfoId)) {
                        fileInfo = getFileById(cacheId);
                        fileCache.remove(fileInfoId);
                        hsqlfileCache.remove(fileInfoId);
                    }
                }
            }
        }
        return fileInfo;
    }

//    static FileInfo getFileInfoByPath(String path) {
//        synchronized (path) {
//            ExecutorService service = RemoteConnectionManager.getStaticConntction(path);
//            FileInfo fileInfo = null;
//            try {
//                GetFileByPathTask task = new GetFileByPathTask(path);
//                fileInfo = service.submit(task).get();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }
//            return fileInfo;
//        }
//
//    }


    /**
     * @return Returns the cacheEnabled.
     */
    public boolean isCacheEnabled() {
        return cacheEnabled;
    }


    /**
     * @param path
     * @return
     * @throws JDSException
     */
    public FileInfo getFileByPath(String path) throws JDSException {
        FileInfo fileInfo = null;

        if (path.indexOf(VFSConstants.URLVERSION) > -1) {
            String[] filePaths = path.split(VFSConstants.URLVERSION);
            path = filePaths[0];
        }

        synchronized (path) {
            if (!cacheEnabled) {
                fileInfo = getVFSDiskService().getFileInfoByPath(path).getData();
            } else { // cache enabled
                synchronized (path) {
                    String fileId = filePathCache.get(path);
                    if (fileId != null && !fileId.equals("")) {
                        try {
                            fileInfo = this.getFileById(fileId);
                        } catch (JDSException e) {
                            e.printStackTrace();
                        }
                    }

                    if (fileId == null
                            || fileInfo == null
                            || !fileInfo.getPath().equals(path)
                            || fileInfo.getFolder() == null
                            || fileInfo.getFolder().getFileIdList() == null
                            || !fileInfo.getFolder().getFileIdList().contains(fileId)
                            ) {
                        //  synchronized (path) {
                        filePathCache.remove(path);
                        // fileInfo = getFileInfoByPath(path);
                        fileInfo = getVFSDiskService().getFileInfoByPath(path).getData();
                        if (fileInfo != null) {
                            filePathCache.put(path, fileInfo.getID());
                            fileInfo = new CtFile(fileInfo);
                            this.fileCache.put(fileInfo.getID(), fileInfo);
                        }
                    }
                }
                //   }
            }
            return fileInfo;
        }
    }


    /**
     * @param path
     * @return
     * @throws JDSException
     */
    public Folder getFolderByPath(String path) throws JDSException {
        Folder folder = null;
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        if (!cacheEnabled) {
            folder = getVFSDiskService().getFolderByPath(path).getData();

        } else { // cache enabled
            synchronized (path) {
                String fileId = filePathCache.get(path);
                if (fileId != null && !fileId.equals("")) {
                    try {
                        folder = this.getFolderById(fileId);
                    } catch (JDSException e) {
                        // e.printStackTrace();
                    }
                }

                if (folder == null || !folder.getPath().equals(path)) {

                    filePathCache.remove(path);
                    folder = getVFSDiskService().getFolderByPath(path).getData();
                    if (folder != null) {
                        filePathCache.put(path, folder.getID());
                        folder = new CtFolder(folder);
                        this.folderCache.put(folder.getID(), folder);
                    }
                }
            }

        }

        return folder;
    }

    public Integer writeLine(String objectId, String json) throws JDSException {
        return getVFSStoreService().writeLine(objectId, json).getData();
    }

    public List<String> readLine(String objectId, List<Integer> lineNums) throws JDSException {
        return getVFSStoreService().readLine(objectId, lineNums).getData();
    }

    public FileView createView(String fileVersionId, String fileObjectId, Integer fileIndex) throws JDSException {
        return getVfsService().createViewByVersionId(fileVersionId, fileObjectId, fileIndex).getData();

    }


    public void copyFolder(String spath, String tPath) throws VFSException, JDSException {
        if (tPath.startsWith(spath)) {
            throw new VFSException("不能拷贝到自己目录内！");
        }
        Folder sfolder = getFolderByPath(spath);
        if (sfolder == null) {
            throw new VFSException("源目录不存在！");
        }
        Folder tFolder = mkDir(tPath);

        List<Folder> childs = tFolder.getChildrenList();

        for (Folder folder : childs) {
            if (folder.getName().equals(sfolder.getName())) {
                throw new VFSException("该目录下已存在相同目录，不能覆盖！");
            }
        }

        Boolean isSuccess = getVFSDiskService().copyFolder(spath, tPath).getData();
        if (isSuccess == null || !isSuccess) {
            throw new JDSException("执行错误！");
        }
        if (cacheEnabled) {
            Folder folder = this.getFolderByPath(tPath);
            folderCache.remove(folder.getID());
            filePathCache.remove(tPath);
            hsqlfolderCache.remove(folder.getID());
            hsqlfilePathCache.remove(tPath);
        }

    }


    public void deleteFolder(String folderId) throws JDSException {
        Folder folder = this.getFolderById(folderId);
        if (folder != null) {
            Boolean isSuccess = getVfsService().deleteFolder(folderId).getData();
            if (isSuccess == null || !isSuccess) {
                this.log.warn("remove folder:[" + folder.getPath() + "] is faile");
                //throw new JDSException("folderId not found error");
            }
            filePathCache.remove(folder.getPath());
            hsqlfilePathCache.remove(folder.getPath());
            if (cacheEnabled && folder != null && folder.getParent() != null) {
                this.clearCache(folder.getParent().getPath());

            }
        }

    }

    public void deleteFile(String fileInfoId) throws VFSException {
        FileInfo fileInfo = null;
        try {
            fileInfo = this.getFileById(fileInfoId);
            if (fileInfo == null) {
                this.log.warn("fileInfo  is null");
            } else {
                Boolean isSuccess = getVfsService().deleteFile(new String[]{fileInfoId}).getData();
                filePathCache.remove(fileInfo.getPath());
                hsqlfilePathCache.remove(fileInfo.getPath());
                if (cacheEnabled && fileInfo != null) {
                    if (isSuccess == null || !isSuccess) {
                        this.log.warn("remove file:[" + fileInfo.getPath() + "] is faile");
                        //throw new JDSException("fileInfoId not found error");
                    }

                    if (fileInfo.getFolder() != null) {
                        this.clearCache(fileInfo.getFolder().getPath());
                    }
                    this.clearFileCache(fileInfo.getPath());
                }
                fileCache.remove(fileInfo.getID());
                hsqlfileCache.remove(fileInfo.getID());
            }

        } catch (JDSException e) {
            throw new VFSException(e);
        }

    }

    public void deleteFileVersionByPath(String path) throws VFSException {
        try {
            FileVersion version = this.getFileVersionByPath(path);
            Boolean isSuccess = getVFSDiskService().delete(path).getData();
            if (isSuccess == null || !isSuccess) {
                throw new JDSException("deleteFileVersionById error path:[" + path + "]");
            }
            FileInfo fileInfo = this.getFileById(version.getFileId());
            if (cacheEnabled) {
                this.clearFileCache(fileInfo.getPath());
            }
        } catch (JDSException e) {
            throw new VFSException(e);
        }

    }

    public void deleteFileVersionById(String id) throws VFSException {
        try {
            FileVersion version = this.getFileVersionById(id);
            Boolean isSuccess = getVFSDiskService().delete(version.getPath()).getData();
            if (isSuccess == null || !isSuccess) {
                throw new JDSException("fileVersionId not found error");
            }
            FileInfo fileInfo = this.getFileById(version.getFileId());
            if (cacheEnabled) {
                this.clearFileCache(fileInfo.getPath());
            }
        } catch (JDSException e) {
            throw new VFSException(e);
        }

    }

    public FileInfo createFile(String path) throws JDSException {
        String folderPath = path.substring(0, path.lastIndexOf("/") + 1);
        String name = path.substring(path.lastIndexOf("/") + 1, path.length());
        return CtCacheManager.getInstance().createFile(folderPath, name);

    }

    public FileInfo createFile(String path, String name) throws JDSException {

        return createFile(path, CnToSpell.getFullSpell(name), name);

    }

    public FileInfo updateFileInfo(String path, String name, String description) throws JDSException {
        FileInfo fileInfo = this.getFileByPath(path);
        Boolean isSuccess = getVFSDiskService().updateFileInfo(path, CnToSpell.getFullSpell(name), description).getData();
        if (isSuccess == null || !isSuccess) {
            throw new JDSException("updateFileInfo error");
        }
        if (cacheEnabled) {
            clearCache(fileInfo.getFolder().getPath());
        }
        return this.getFileById(fileInfo.getID());
    }

    public Folder updateFolderInfo(String path, String name, String description, FolderType type) throws JDSException {
        Folder folder = this.getFolderByPath(path);
        String parentPath = folder.getParent().getPath();
        Boolean isSuccess = getVFSDiskService().updateFolderInfo(path, name, description, type).getData();
        if (isSuccess == null || !isSuccess) {
            throw new JDSException("uplaod error");
        }
        if (cacheEnabled) {
            clearCache(parentPath);
        }
        folder = this.getFolderById(folder.getID());
        return folder;
    }

    public Folder updateFolderState(String path, FolderState state) throws JDSException {
        Folder folder = this.getFolderByPath(path);
        folder.setState(state);
        String parentPath = folder.getParent().getPath();
        Boolean isSuccess = getVFSDiskService().updateFolderState(path, state).getData();
        if (isSuccess == null || !isSuccess) {
            throw new JDSException("uplaod error");
        }
        if (cacheEnabled) {
            clearCache(parentPath);
        }
        folder = this.getFolderById(folder.getID());
        return folder;
    }


    public void updateFileVersionInfo(String fileVersionId, String hash) throws JDSException {
        FileVersion fileVersion = this.getFileVersionById(fileVersionId);
        FileInfo fileInfo = this.getFileById(fileVersion.getFileId());
        Boolean isUpload = getVfsService().updateFileVersionInfo(fileVersionId, hash).getData();
//        if (isUpload == null || !isUpload) {
//            throw new JDSException("updateFileVersionInfo error");
//        }

        this.clearFileCache(fileInfo.getPath());

    }


    public void updateFileViewInfo(FileView view) throws JDSException {
        Boolean isSuccess = getVfsService().updateFileViewInfo(view).getData();
        if (isSuccess == null || !isSuccess) {
            throw new JDSException("updateFileViewInfo error");
        }

    }

    class LoadFoldersTask<T extends List<Folder>> implements Callable<T> {
        private final String[] loadIds;

        private MinServerActionContextImpl autoruncontext;

        LoadFoldersTask(String[] loadIds) {
            JDSContext context = JDSActionContext.getActionContext();
            this.autoruncontext = new MinServerActionContextImpl(context.getHttpRequest(), context.getOgnlContext());
            autoruncontext.setParamMap(context.getContext());
            if (context.getSessionId() != null) {
                autoruncontext.setSessionId(context.getSessionId());
                autoruncontext.getSession().put("sessionHandle", context.getSession().get("sessionHandle"));
            }
            autoruncontext.setSessionMap(context.getSession());

            Set<String> idSet = new LinkedHashSet<>();
            for (String id : loadIds) {
                if (id != null) {
                    idSet.add(id);
                }
            }
            this.loadIds = idSet.toArray(new String[idSet.size()]);
        }

        @Override
        public T call() throws Exception {
            JDSActionContext.setContext(autoruncontext);

            List<Folder> loadFolers = getVfsService().loadFolderList(loadIds).getData();
            if (loadFolers != null && loadFolers.size() > 0) {
                for (Folder folder : loadFolers) {
                    if (folder != null) {
                        folder = new CtFolder(folder);
                        folderCache.put(folder.getID(), folder);
                        filePathCache.put(folder.getPath(), folder.getID());
                    }
                }
            }
            return (T) loadFolers;
        }
    }


    public List<Folder> loadFolers(Set<String> folderIds) throws JDSException {
        List<Folder> folders = new ArrayList<Folder>();
        Set<String> loadIds = new LinkedHashSet<String>();
        for (String folderId : folderIds) {
            Folder folder = null;
            if (loadIds != null) {
                if (!cacheEnabled) {
                    loadIds.add(folderId);
                } else { // cache enabled
                    folder = (Folder) folderCache.get(folderId);
                    if (folder == null) {
                        loadIds.add(folderId);
                    }

                }
            }
        }

        if (loadIds.size() > 0) {
            //批量装载数据
            if (loadIds.size() > 0) {
                Integer start = 0;

                int size = loadIds.size();
                String[] delfileInfoIds = loadIds.toArray(new String[loadIds.size()]);
                int page = 0;
                while (page * pageSize < size) {
                    page++;
                }
                List<LoadFoldersTask<List<Folder>>> tasks = new ArrayList<LoadFoldersTask<List<Folder>>>();
                for (int k = 0; k < page; k++) {
                    int end = start + pageSize;
                    if (end >= size) {
                        end = size;
                    }
                    String[] loadFileIds = Arrays.copyOfRange(delfileInfoIds, start, start + pageSize);
                    tasks.add(new LoadFoldersTask(loadFileIds));
                    start = end;
                }

                try {
                    RemoteConnectionManager.initConnection("CTLoadFolderIds", this.oncetasksize);
                    ExecutorService executorService = RemoteConnectionManager.getConntctionService("CTLoadFolderIds");
                    List<Future<List<Folder>>> futures = executorService.invokeAll(tasks);
                    for (Future<List<Folder>> resultFuture : futures) {
                        try {
                            List<Folder> result = resultFuture.get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                    executorService.shutdownNow();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


        for (String id : folderIds) {
            Folder folder = this.getFolderById(id);
            if (folder != null && id.equals(folder.getID())) {
                folders.add(folder);
            }
        }

        return folders;
    }

    public List<FileVersion> loadVersionByIds(Set<String> versionIds) throws JDSException {
        List<FileVersion> versions = new ArrayList<FileVersion>();
        Set<String> loadIds = new LinkedHashSet<>();
        for (String versionid : versionIds) {
            if (versionid != null) {
                FileVersion fileversion = null;
                if (!cacheEnabled) {
                    loadIds.add(versionid);
                } else { // cache enabled
                    fileversion = fileVersionCache.get(versionid);
                    if (fileversion == null) {
                        loadIds.add(versionid);
                    }

                }
            }

        }

        //批量装载数据
        if (loadIds.size() > 0) {
            List<FileVersion> loadversions = getVfsService().loadVersionList(loadIds.toArray(new String[loadIds.size()])).getData();
            if (loadversions != null) {
                for (FileVersion version : loadversions) {
                    if (version != null) {
                        version = new CtFileVersion(version);
                        fileVersionCache.put(version.getVersionID(), version);
                        filePathCache.put(version.getPath(), version.getVersionID());
                    }

                }
            }

        }


        for (String id : versionIds) {
            versions.add(this.getFileVersionById(id));
        }
        return versions;
    }


    class LoadFileObjectTask<T extends List<FileObject>> implements Callable<T> {
        private final String[] loadIds;
        private MinServerActionContextImpl autoruncontext;

        LoadFileObjectTask(String[] loadIds) {
            JDSContext context = JDSActionContext.getActionContext();
            this.autoruncontext = new MinServerActionContextImpl(context.getHttpRequest(), context.getOgnlContext());
            autoruncontext.setParamMap(context.getContext());
            if (context.getSessionId() != null) {
                autoruncontext.setSessionId(context.getSessionId());
                autoruncontext.getSession().put("sessionHandle", context.getSession().get("sessionHandle"));
            }
            autoruncontext.setSessionMap(context.getSession());
            Set<String> idSet = new LinkedHashSet<>();
            for (String id : loadIds) {
                if (id != null) {
                    idSet.add(id);
                }
            }
            this.loadIds = idSet.toArray(new String[idSet.size()]);
        }

        @Override
        public T call() throws Exception {
            JDSActionContext.setContext(autoruncontext);
            List<FileObject> loadFiles = getVFSStoreService().loadFileObjectList(loadIds).getData();


            if (loadFiles != null && loadFiles.size() > 0) {
                for (FileObject fileObject : loadFiles) {
                    if (fileObject != null) {
                        fileObject = new CtFileObject(fileObject);
                        fileObjectCache.put(fileObject.getID(), fileObject);
                        fileHashObjectCache.put(fileObject.getHash(), fileObject.getID());
                    }

                }
            }

            for (String fileObjectId : loadIds) {
                if (!fileObjectCache.containsKey(fileObjectId)) {

                    FileObject fileObject = new CtFileObject();
                    fileObject.setHash(MD5.getHashString(""));
                    fileObject.setID(fileObjectId);
                    fileObject.setName("NULL");
                    fileObject.setCreateTime(System.currentTimeMillis());
                    fileObject.setLength(0L);

                    fileObjectCache.put(fileObject.getID(), fileObject);
                    //fileHashObjectCache.put(fileObject.getHash(), fileObject.getID());
                }
            }


            return (T) loadFiles;
        }
    }


    public synchronized List<FileObject> loadObjects(Set<String> objectIds) throws JDSException {

        List<FileObject> fileObjects = new ArrayList<FileObject>();
        Set<String> loadIds = new LinkedHashSet<String>();
        if (objectIds != null) {
            for (String objectId : objectIds) {
                FileObject view = null;
                if (objectId != null) {
                    if (!cacheEnabled) {
                        loadIds.add(objectId);
                    } else { // cache enabled
                        view = fileObjectCache.get(objectId);
                        if (view == null) {
                            loadIds.add(objectId);
                        }
                    }
                }
            }

            //批量装载数据
            if (loadIds.size() > 0) {
                Integer start = 0;
                int size = loadIds.size();
                String[] delfileInfoIds = loadIds.toArray(new String[loadIds.size()]);
                int page = 0;
                while (page * pageSize < size) {
                    page++;
                }
                List<LoadFileObjectTask<List<FileObject>>> tasks = new ArrayList<LoadFileObjectTask<List<FileObject>>>();
                for (int k = 0; k < page; k++) {
                    int end = start + pageSize;
                    if (end >= size) {
                        end = size;
                    }
                    String[] loadFileIds = Arrays.copyOfRange(delfileInfoIds, start, start + pageSize);
                    tasks.add(new LoadFileObjectTask(loadFileIds));
                    start = end;
                }

                try {


                    RemoteConnectionManager.initConnection("CTLoadObjesIds", oncetasksize);
                    ExecutorService executorService = RemoteConnectionManager.getConntctionService("CTLoadObjesIds");

                    List<Future<List<FileObject>>> futures = RemoteConnectionManager.getConntctionService("CTLoadObjesIds").invokeAll(tasks);
                    for (Future<List<FileObject>> resultFuture : futures) {
                        try {
                            List<FileObject> result = resultFuture.get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                    executorService.shutdownNow();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        for (String id : objectIds) {

            if (id != null) {
                FileObject object = this.getFileObjectById(id);
                if (object != null) {
                    fileObjects.add(object);
                }

            }

        }

        return fileObjects;

    }


    public List<FileView> loadViews(Set<String> viewIds) throws JDSException {
        HashSet<String> fileObjectIds = new HashSet<String>();
        List<FileView> fileViews = new ArrayList<FileView>();
        Set<String> loadIds = new LinkedHashSet<String>();
        if (viewIds != null) {
            for (String viewId : viewIds) {
                FileView view = null;
                if (viewId != null) {
                    if (!cacheEnabled) {
                        loadIds.add(viewId);
                    } else { // cache enabled
                        view = (FileView) fileViewCache.get(viewId);
                        if (view == null) {
                            loadIds.add(viewId);
                        }

                    }
                }
            }

            //批量装载数据
            if (loadIds.size() > 0) {
                List<FileView> loadViews = getVfsService().loadFileViewList(loadIds.toArray(new String[loadIds.size()])).getData();
                if (loadViews != null && loadViews.size() > 0) {
                    for (FileView fileView : loadViews) {
                        fileView = new CtFileView(fileView);
                        fileViewCache.put(fileView.getID(), fileView);
                    }
                }

            }
        }

        for (String id : viewIds) {
            fileViews.add(this.getFileViewById(id));
        }

        for (FileView fileView : fileViews) {
            if (fileView.getFileObjectId() != null) {
                fileObjectIds.add(fileView.getFileObjectId());
            }
        }
        if (fileObjectIds.size() > 0) {
            //预装OBJ数据
            this.loadObjects(fileObjectIds);

        }


        return fileViews;

    }

    class LoadFileInfosTask<T extends List<FileInfo>> implements Callable<T> {
        private final String[] loadIds;
        private MinServerActionContextImpl autoruncontext;

        LoadFileInfosTask(String[] loadIds) {

            JDSContext context = JDSActionContext.getActionContext();
            this.autoruncontext = new MinServerActionContextImpl(context.getHttpRequest(), context.getOgnlContext());
            autoruncontext.setParamMap(context.getContext());
            if (context.getSessionId() != null) {
                autoruncontext.setSessionId(context.getSessionId());
                autoruncontext.getSession().put("sessionHandle", context.getSession().get("sessionHandle"));
            }
            autoruncontext.setSessionMap(context.getSession());

            Set<String> idSet = new LinkedHashSet<>();
            for (String id : loadIds) {
                if (id != null) {
                    idSet.add(id);
                }
            }
            this.loadIds = idSet.toArray(new String[idSet.size()]);
        }

        @Override
        public T call() throws Exception {
            JDSActionContext.setContext(autoruncontext);

            List<FileInfo> loadFiles = getVfsService().loadFileList(loadIds).getData();
            if (loadFiles != null && loadFiles.size() > 0) {

                for (FileInfo fileInfo : loadFiles) {
                    if (fileInfo != null) {
                        fileInfo = new CtFile(fileInfo);
                        fileCache.put(fileInfo.getID(), fileInfo);
                        filePathCache.put(fileInfo.getPath(), fileInfo.getID());
                    }

                }
            }
            return (T) loadFiles;
        }
    }


    public List<FileInfo> loadFiles(Set<String> fileIds) throws JDSException {
        List<FileInfo> fileInfos = new ArrayList<FileInfo>();
        Set<String> loadIds = new LinkedHashSet<String>();
        if (fileIds != null) {
            for (String fileId : fileIds) {

                if (fileId != null) {
                    FileInfo fileInfo = null;
                    if (!cacheEnabled) {
                        loadIds.add(fileId);
                    } else { // cache enabled
                        fileInfo = (FileInfo) fileCache.get(fileId);
                        if (fileInfo == null) {
                            loadIds.add(fileId);
                        }

                    }
                }

            }

            //批量装载数据
            if (loadIds.size() > 0) {
                Integer start = 0;

                int size = loadIds.size();
                String[] delfileInfoIds = loadIds.toArray(new String[loadIds.size()]);
                int page = 0;
                while (page * pageSize < size) {
                    page++;
                }
                List<LoadFileInfosTask<List<FileInfo>>> tasks = new ArrayList<LoadFileInfosTask<List<FileInfo>>>();
                for (int k = 0; k < page; k++) {
                    int end = start + pageSize;
                    if (end >= size) {
                        end = size;
                    }
                    String[] loadFileIds = Arrays.copyOfRange(delfileInfoIds, start, start + pageSize);
                    tasks.add(new LoadFileInfosTask(loadFileIds));
                    start = end;
                }

                try {
                    RemoteConnectionManager.initConnection("CTLoadFileIds", oncetasksize);
                    ExecutorService executorService = RemoteConnectionManager.getConntctionService("CTLoadFileIds");
                    List<Future<List<FileInfo>>> futures = executorService.invokeAll(tasks);
                    for (Future<List<FileInfo>> resultFuture : futures) {
                        try {
                            List<FileInfo> result = resultFuture.get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                    executorService.shutdownNow();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


        for (String id : fileIds) {
            FileInfo fileInfo = this.getFileById(id);
            if (fileInfo != null) {
                fileInfos.add(fileInfo);
            }

        }
        return fileInfos;

    }

    public FileVersion createFileVersion(String path, String filehash) throws JDSException {
        if (path.indexOf(VFSConstants.URLVERSION) > -1) {
            String[] filePaths = path.split(VFSConstants.URLVERSION);
            path = filePaths[0];
        }

        FileVersion fileVersion = getVFSDiskService().createFileVersion(path, filehash).get();
        if (fileVersion != null) {
            fileVersion = new CtFileVersion(fileVersion);
            fileVersionCache.put(fileVersion.getVersionID(), fileVersion);
            this.clearFileCache(path);
        }

        return fileVersion;
    }


    public FileVersion getFileVersionByPath(String path) throws JDSException {
        FileVersion fileVersion = null;
        String versionId = null;
        if (path.indexOf(VFSConstants.URLVERSION) > -1) {
            versionId = filePathCache.get(path);
            if (versionId != null) {
                fileVersion = this.getFileVersionById(versionId);
            } else {
                String[] filePaths = path.split(VFSConstants.URLVERSION);
                FileInfo fileInfo = this.getFileByPath(filePaths[0]);
                if (fileInfo != null) {
                    List<FileVersion> versions = fileInfo.getVersionList();
                    for (FileVersion version : versions) {
                        if (version.getIndex().equals(Integer.valueOf(filePaths[1]))) {
                            fileVersion = version;
                            filePathCache.put(path, fileVersion.getVersionID());
                        }
                    }
                    this.clearCache(fileInfo.getPath());
                }
            }
        } else {
            FileInfo info = this.getFileByPath(path);
            if (info != null) {
                fileVersion = info.getCurrentVersion();
            }
        }


//        if (versionId == null) {
//            if (path.indexOf(VFSConstants.URLVERSION) > -1) {
//                String[] filePaths = path.split(VFSConstants.URLVERSION);
//                FileInfo fileInfo = this.getFileByPath(filePaths[0]);
//                if (fileInfo != null) {
//                    List<FileVersion> versions = fileInfo.getVersionList();
//                    for (FileVersion version : versions) {
//                        if (version.getIndex().equals(Integer.valueOf(filePaths[1]))) {
//                            return version;
//                        }
//                    }
//                    this.clearCache(fileInfo.getPath());
//                }
//            } else {
//                FileInfo info = this.getFileByPath(path);
//                if (info != null) {
//                    fileVersion = info.getCurrentVersion();
//                }
//            }
//        } else {
//            fileVersion = this.getFileVersionById(versionId);
//        }


        return fileVersion;
    }

    public List<FileVersion> getFileVersionsByHash(String hash) throws JDSException {
        Set<String> versionIds = this.getVfsService().getVersionByHash(hash).get();
        return this.loadVersionByIds(versionIds);
    }

    public MD5InputStream downLoadByHash(String hash) throws JDSException {
        FileObject object = this.getFileObjectByHash(hash);
        if (object == null) {
            throw new JDSException("hash error! not frond file!");
        }
        return this.downLoadByObjectId(object.getID());

    }

    public MD5InputStream downLoadByObjectId(String objectId) throws JDSException {
        FileObject object = this.getFileObjectById(objectId);
        if (object == null) {
            throw new JDSException("hash error! not frond file!");
        }
        String localPath = CtVfsFactory.getLocalCachePath();// JDSConfig.Config.tempPath().getPath() + File.separator + "md5hash" + File.separator;
        //本地不保存版本
        File localFile = new File(localPath + object.getHash());
        MD5InputStream md5input = null;
        try {
            if (!localFile.exists() || localFile.length() == 0) {
                InputStream input = getVFSStoreService().downLoadByHash(object.getHash()).getData();
                copyStreamToFile(input, localFile);
            } else {
                String md5 = MD5.getHashString(localFile);
                if (!md5.equals(object.getHash()) && object.getLength() > 0) {
                    InputStream input = getVFSStoreService().downLoadByHash(object.getHash()).getData();
                    copyStreamToFile(input, localFile);
                }
            }
            if (localFile.exists()) {
                md5input = new MD5InputStream(new FileInputStream(localFile));
            }

        } catch (IOException e) {
            throw new JDSException(e);
        }

        return md5input;

    }

    public void updateFileObject(FileObject fileObject) {
        fileHashObjectCache.put(fileObject.getHash(), fileObject.getID());
        this.fileObjectCache.put(fileObject.getID(), fileObject);
        this.getVFSStoreService().updateFileObject(fileObject);

    }

    public void deleteFileObject(String id) {
        FileObject fileObject = null;
        try {
            fileObject = this.getFileObjectById(id);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        if (fileObject != null) {
            this.getVFSStoreService().deleteFileObject(id);
            fileHashObjectCache.remove(fileObject.getHash());
            this.fileObjectCache.remove(fileObject.getID());
            hsqlfileHashObjectCache.remove(fileObject.getHash());
            hsqlfileObjectCache.remove(fileObject.getID());

        }

    }


    class SyncUpload implements Runnable {
        private final FutureCallback<String> callback;
        private String personId;
        private String path;
        private MD5InputStream inputstream;
        private MinServerActionContextImpl autoruncontext;

        SyncUpload(String path, MD5InputStream inputstream, String personId, FutureCallback<String> callback) {
            this.path = path;
            this.inputstream = inputstream;
            this.personId = personId;
            this.callback = callback;
            JDSContext context = JDSActionContext.getActionContext();
            this.autoruncontext = new MinServerActionContextImpl(context.getHttpRequest(), context.getOgnlContext());

            autoruncontext.setParamMap(context.getContext());
            if (context.getSessionId() != null) {
                autoruncontext.setSessionId(context.getSessionId());
                autoruncontext.getSession().put("sessionHandle", context.getSession().get("sessionHandle"));
            }
            autoruncontext.setSessionMap(context.getSession());
        }

        @Override
        public void run() {
            JDSActionContext.setContext(autoruncontext);
            FileInfo fileInfo = null;
            try {
                fileInfo = CtCacheManager.this.getFileByPath(path);
                if (fileInfo == null) {
                    fileInfo = CtCacheManager.this.createFile(path);
                }
                path = CnToSpell.getFullSpell(path);
                FileObject fileObject = createFileObject(inputstream);
                FileVersion fileVersion = getFileVersionByPath(path);

                if (fileVersion != null) {
                    //FileInfo fileInfo = getFileById(fileVersion.getFileId());
                    Long updateTime = System.currentTimeMillis() - fileVersion.getCreateTime();
                    if (fileVersion == null ||
                            personId == null ||
                            fileVersion.getPersonId() == null ||
                            !fileVersion.getPersonId().equals(personId)
                            ) {
                        fileVersion = createFileVersion(fileInfo.getPath(), fileObject.getHash());
                    } else {
                        updateFileVersionInfo(fileVersion.getVersionID(), fileObject.getHash());
                    }

                }


            } catch (JDSException e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.failed(e);
                }
            }

            if (cacheEnabled) {
                filePathCache.remove(fileInfo.getFolder().getPath());
                folderCache.remove(fileInfo.getFolder().getID());
                hsqlfilePathCache.remove(fileInfo.getFolder().getPath());
                hsqlfolderCache.remove(fileInfo.getFolder().getID());
            }
            if (callback != null) {
                callback.completed(path);
            }


        }

    }

    public void syncUpload(final String path, MD5InputStream inputstream, String personId, FutureCallback<String> callback) {

        ExecutorService service = RemoteConnectionManager.getConntctionService("CtCacheManager().syncUpload");
        service.execute(new SyncUpload(path, inputstream, personId, callback));

    }

    public FileObject uploadFileObject(MD5InputStream md5in) {
        FileObject fileObject = getVFSStoreService().createFileObject(new LocalMultipartFile(md5in)).getData();
        if (fileObject != null) {
            fileObject = new CtFileObject(fileObject);
            fileObjectCache.put(fileObject.getID(), fileObject);
        }
        return fileObject;
    }

    public FileObject createFileObject() {
        FileObject fileObject = new CtFileObject();
        fileObjectCache.put(fileObject.getID(), fileObject);
        return fileObject;
    }


    public boolean bigFileUpload(String path, String vfsPath, String personId) throws JDSException {
        boolean isSussece = false;
        try {
            isSussece = BigFileTools.syncUpload(path, vfsPath, personId, null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new JDSException(e);

        }
        return isSussece;

    }

    public FileVersion upload(String path, MD5InputStream md5in, String personId) throws JDSException {
        path = CnToSpell.getFullSpell(path);
        FileVersion fileVersion = this.getFileVersionByPath(path);
        FileObject fileObject = createFileObject(md5in);
        try {

            if (fileVersion == null) {
                FileInfo fileInfo = this.createFile(path);
                fileVersion = this.createFileVersion(fileInfo.getPath(), fileObject.getHash());
            } else if (fileVersion.getFileObject() == null || fileVersion.getFileObject().getLength() == 0 || fileVersion.getFileObject().getHash() == null) {
                this.updateFileVersionInfo(fileVersion.getVersionID(), fileObject.getHash());
            } else if (!fileVersion.getFileObject().getHash().equals(fileObject.getHash())) {
                FileInfo fileInfo = this.getFileById(fileVersion.getFileId());
                Long updateTime = System.currentTimeMillis() - fileVersion.getCreateTime();
                //导入导出时不计算 HASH
                if ((JDSActionContext.getActionContext() instanceof MinServerActionContextImpl) && System.getProperty("NoVersion") != null) {
                    this.updateFileVersionInfo(fileVersion.getVersionID(), fileObject.getHash());
                } else if (personId == null ||
                        fileVersion.getPersonId() == null ||
                        !fileVersion.getPersonId().equals(personId) ||
                        updateTime > 60 * 60 * 1000
                        ) {
                    fileVersion = this.createFileVersion(fileInfo.getPath(), fileObject.getHash());
                } else {
                    this.updateFileVersionInfo(fileVersion.getVersionID(), fileObject.getHash());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {

        }

        return fileVersion;
    }

    public Folder mkDir(String path) throws JDSException {
        return mkDir(path, null, null);
    }

    public FileInfo copyFile(String sfile, String tpaht) throws JDSException {
        FileInfo tempFile = getFileByPath(sfile);
        String fileName = tempFile.getName();
        FileInfo newFile = null;
        if (sfile == null || tempFile == null) {
            throw new JDSException("sfile  is null!");

        }
        Folder tfolder = null;

        if (tpaht == null && tempFile != null) {
            tfolder = tempFile.getFolder();
        } else {
            tfolder = mkDir(tpaht);
        }

        List<FileInfo> files = tfolder.getFileList();
        for (FileInfo fileInfo : files) {
            if (fileInfo.getName().equals(tempFile.getName())) {
                newFile = fileInfo;
            }
        }

        if (newFile == null) {
            newFile = tfolder.createFile(fileName, tempFile.getDescription(), tfolder.getPersonId());
        } else {
            if (fileName.indexOf(".") > -1) {
                String[] fileNames = StringUtility.split(tempFile.getName(), ".");
                String[] descriptions = StringUtility.split(tempFile.getDescription(), ".");
                newFile = tfolder.createFile(fileNames[0] + "1." + fileNames[1], descriptions[0] + "1." + descriptions[1], tfolder.getPersonId());
            } else {
                newFile = tfolder.createFile(fileName + "1", tempFile.getDescription() + "1", tfolder.getPersonId());
            }
        }
        upload(newFile.getPath(), tempFile.getCurrentVersonInputStream(), tfolder.getPersonId());

        if (cacheEnabled) {
            Folder folder = this.getFolderByPath(tpaht);
            folderCache.remove(folder.getID());
            filePathCache.remove(tpaht);
            hsqlfolderCache.remove(folder.getID());
            hsqlfilePathCache.remove(tpaht);
        }

        return newFile;
    }


    private void copyStreamToFile(InputStream input, File file) throws IOException {


        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (file.exists() && !file.canWrite()) {
            final String message = "Unable to open file " + file + " for writing.";
            throw new IOException(message);
        }
        if (input != null) {
            final FileOutputStream output = new FileOutputStream(file);
            IOUtility.copy(input, output);
            IOUtility.shutdownStream(input);
            IOUtility.shutdownStream(output);
        }

    }


    public MD5InputStream downLoad(String paths) throws JDSException {
        MD5InputStream md5input = null;
        synchronized (paths) {
            //优先本地文件处理
            if (paths.startsWith(StringUtility.replace(new File("").getAbsolutePath(), "\\", "/"))) {
                File tempFile = new File(paths);
                if (tempFile.exists() && tempFile.isFile() && tempFile.length() > 0) {
                    try {
                        return new MD5InputStream(new FileInputStream(tempFile));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }

            FileVersion version = this.getFileVersionByPath(paths);
            if (version == null) {
                throw new JDSException("paths error! not frond file!");
            }
            FileInfo fileInfo = this.getFileById(version.getFileId());
            String localPath = JDSConfig.Config.tempPath().getPath() + File.separator;
            File localFile = new File(localPath + fileInfo.getPath());
            //分块文件处理
            if (version.getViews().size() > 1) {
                try {
                    File md5localFile = new File(localPath + fileInfo.getPath() + "##MD5");
                    FileObject object = version.getFileObject();
                    List<FileView> viewList = version.getViews();
                    String json = JSONObject.toJSONString(viewList);


                    if (!md5localFile.exists() || md5localFile.length() == 0) {
                        List<String> localPaths = new ArrayList<>();

                        for (FileView view : viewList) {
                            String blockPath = view.getFileObject().getPath();
                            File file = new File(blockPath);
                            view.getFileObject().downLoad();
                            while (file.exists()) {
                                localPaths.add(view.getFileObject().getPath());
                            }
                        }
                        BigFileUtil.mergeFiles(localPaths, localPath);
                    } else {
                        String md5 = MD5.getHashString(md5localFile);
                        if (!md5.equals(object.getHash()) && object.getLength() > 0) {
                            List<String> localPaths = new ArrayList<>();
                            for (FileView view : viewList) {
                                String blockPath = view.getFileObject().getPath();
                                File file = new File(blockPath);
                                view.getFileObject().downLoad();
                                while (file.exists()) {
                                    localPaths.add(view.getFileObject().getPath());
                                }
                            }
                            BigFileUtil.mergeFiles(localPaths, localPath);
                        }
                    }

                    InputStream input = new ByteArrayInputStream(json.getBytes("utf-8"));
                    if (!md5localFile.exists()) {
                        md5localFile.createNewFile();
                    }
                    FileOutputStream output = new FileOutputStream(md5localFile);
                    IOUtility.copy(input, output);
                    IOUtility.shutdownStream(input);
                    IOUtility.shutdownStream(output);

                    if (localFile.exists()) {
                        md5input = new MD5InputStream(new FileInputStream(localFile));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (version.getFileObject() != null && version.getLength() > 0) {
                md5input = downLoadByHash(version.getFileObject().getHash());
            } else {
                List<FileVersion> versions = fileInfo.getVersionList();
                for (FileVersion childVersion : versions) {
                    if (childVersion.getFileObject() != null && childVersion.getLength() > 0 && paths.equals(childVersion.getPath())) {
                        return downLoad(childVersion.getPath());
                    }
                }
            }
        }

        return md5input;
    }

    public MD5InputStream downLoadVersion(String versionId) throws JDSException {
        FileVersion version = this.getFileVersionById(versionId);
        return this.downLoad(version.getPath());
    }


    public void cloneFolder(String spath, String tPath) throws JDSException {

        Boolean isSuccess = getVFSDiskService().cloneFolder(spath, tPath).getData();
        if (isSuccess == null || !isSuccess) {
            throw new JDSException("cloneFolder error");
        }
        if (cacheEnabled) {
            Folder folder = this.getFolderByPath(tPath);
            clearCache(folder.getParent().getPath());
        }
    }


    public StringBuffer readFileAsString(String path, String encoding) throws JDSException {


        StringBuffer buffer = new StringBuffer();
        InputStream stream = null;
        try {
            stream = this.downLoad(path);
            if (stream != null) {
                if (encoding == null) {
                    encoding = VFSConstants.Default_Encoding;
                }
                String str = IOUtility.toString(stream, encoding);
                buffer.append(str);
            }

        } catch (IOException e) {
            throw new JDSException(e);
        } finally {
            IOUtility.shutdownStream(stream);
        }
        return buffer;


    }

    public FileObject createFileObject(File file) throws VFSException {
        String hash = "";
        FileObject fileObject = null;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            hash = DigestUtils.md5Hex(inputStream);
            fileObject = getFileObjectByHash(hash);
            if (fileObject != null) {
                log.info("fileObject =:  " + JSON.toJSONString(fileObject.getHash()));
            } else {
                fileObject = uploadFileObject(new MD5InputStream(new FileInputStream(file)));
                fileObject.setID(fileObject.getHash());
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new VFSException(e);
        } finally {
            IOUtility.shutdownStream(inputStream);
        }
        if (fileObject == null) {
            throw new VFSException("upload file faile, place check VFSServer");
        }
        return fileObject;
    }


    public FileObject createFileObject(MD5InputStream md5in) throws VFSException {
        String hash = "";
        File temp = null;
        FileOutputStream out = null;
        FileInputStream in = null;
        FileObject fileObject = null;

        try {
            temp = File.createTempFile("" + System.currentTimeMillis(), ".temp");
            out = new FileOutputStream(temp);
            in = new FileInputStream(temp);
            IOUtility.copy(md5in, out);
            hash = DigestUtils.md5Hex(in);
            fileObject = getFileObjectByHash(hash);

            if (fileObject != null) {
                log.info("fileObject =:  " + JSON.toJSONString(fileObject.getHash()));
            } else {
                fileObject = uploadFileObject(new MD5InputStream(new FileInputStream(temp)));
                if (fileObject != null) {
                    fileObject.setID(fileObject.getHash());
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new VFSException(e);
        } finally {
            IOUtility.shutdownStream(in);
            IOUtility.shutdownStream(out);
            temp.deleteOnExit();

        }
        if (fileObject == null) {
            throw new VFSException("upload file faile, place check VFSServer");
        }
        return fileObject;
    }

//
//    public FileObject createFileObject(MD5InputStream md5in) throws VFSException {
//        String hash = "";
//
//        FileObject fileObject = null;
//        try {
//            hash = MD5.getHashString(md5in);
//            temp = File.createTempFile("" + System.currentTimeMillis(), ".temp");
//            out = new FileOutputStream(temp);
//            in = new FileInputStream(temp);
//            IOUtility.copy(md5in, out);
//            hash = DigestUtils.md5Hex(in);
//            fileObject = getFileObjectByHash(hash);
//
//            if (fileObject != null) {
//                log.info("fileObject =:  " + JSON.toJSONString(fileObject));
//            } else {
//                fileObject = uploadFileObject(md5in);
//                if (fileObject != null) {
//                    fileObject.setID(fileObject.getHash());
//                } else {
//                    throw new VFSException("upload fail!");
//                }
//
//            }
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//            throw new VFSException(e);
//        } finally {
//
//        }
//        if (fileObject == null) {
//            throw new VFSException("upload file faile, place check VFSServer");
//        }
//
//        return fileObject;
//    }

    public FileObject createFileObjectAsContent(String content, String encoding) {
        String hash = "";
        File temp = null;
        FileOutputStream out = null;
        FileInputStream in = null;
        FileObject fileObject = null;
        try {
            temp = File.createTempFile("" + System.currentTimeMillis(), ".temp");
            InputStream input = new ByteArrayInputStream(content.getBytes(encoding));
            FileOutputStream output = new FileOutputStream(temp);
            IOUtility.copy(input, output);
            IOUtility.shutdownStream(input);
            IOUtility.shutdownStream(output);
            fileObject = this.createFileObject(temp);

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            temp.deleteOnExit();
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileObject;
    }


    public FileInfo saveFileAsContent(String versionpath, String content, String encoding, String personId) throws JDSException {
        String filePath = versionpath;
        if (versionpath.indexOf(VFSConstants.URLVERSION) > -1) {
            String[] filePaths = versionpath.split(VFSConstants.URLVERSION);
            filePath = filePaths[0];
        }


        FileInfo fileInfo = this.getFileByPath(versionpath);
        if (fileInfo == null) {
            fileInfo = this.createFile(filePath);
        }


        if (fileInfo != null) {
            if (encoding == null) {
                encoding = VFSConstants.Default_Encoding;
            }
            try {
                if (filePath.startsWith(StringUtility.replace(new File("").getAbsolutePath(), "\\", "/"))) {
                    IOUtility.writeBytesToNewFile(content.getBytes(encoding), new File(filePath));
                } else {
                    File temp = File.createTempFile("" + System.currentTimeMillis(), ".temp");
                    InputStream input = new ByteArrayInputStream(content.getBytes(encoding));
                    FileOutputStream output = new FileOutputStream(temp);
                    IOUtility.copy(input, output);
                    IOUtility.shutdownStream(input);
                    IOUtility.shutdownStream(output);
                    upload(versionpath, new MD5InputStream(new FileInputStream(temp)), personId);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new JDSException(e);
            }
        }
        return fileInfo;

    }

    public FileInfo createFile(String path, String name, String description) throws JDSException {

//        FileInfo file = this.getFileByPath(path);
//        //已存在则返回
//        if (file != null && file.getDescrition().equals(descrition)) {
//            return file;
//        }

        Folder folder = this.getFolderByPath(path);
        if (folder == null) {
            folder = this.mkDir(path);
        }

        List<FileInfo> childlist = folder.getFileList();
        if (description == null) {
            description = name;
        }

        for (FileInfo cfileInfo : childlist) {
            if (cfileInfo.getName().equals(name) && cfileInfo.getDescription().equals(description)) {
                return cfileInfo;
            }
        }


        FileInfo fileInfo = getVFSDiskService().createFile2(path, name, description).getData();

        int k = 0;

        while (fileInfo == null && k < 5) {
            k = k + 1;
            fileInfo = getVFSDiskService().createFile2(path, name, description).getData();
        }

        if (cacheEnabled) {
            String folderId = filePathCache.get(path);
            if (folderId != null) {
                folderCache.remove(folderId);
                filePathCache.remove(path);
                hsqlfolderCache.remove(folderId);
                hsqlfilePathCache.remove(path);
            }
        }
        if (fileInfo == null) {
            log.error(path + " create error tryTimes " + k + "次");
            throw new JDSException("server error or path is not valid!");

        }
        return new CtFile(fileInfo);
    }

    public Folder mkDir(String path, String description, FolderType type) throws JDSException {
        Folder folder = null;
        String folderId = null;
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        if (cacheEnabled) {
            folderId = filePathCache.get(path);
        }

        if (folderId == null || this.getFolderById(folderId) == null) {
            Folder rfolder = getVFSDiskService().getFolderByPath(path).getData();
            if (rfolder == null
                    || (type != null && !rfolder.getFolderType().equals(type))
                    || (description != null && !rfolder.getDescription().equals(description)
            )) {
                rfolder = getVFSDiskService().mkDir2(path, description, type).getData();
            }

            if (rfolder != null) {
                folder = new CtFolder(rfolder);
                folderCache.put(folder.getID(), folder);
                filePathCache.put(path, folder.getID());
            } else {
                throw new JDSException("server error path is not valid! [" + path + "]");
            }

            if (cacheEnabled && folder != null) {
                Folder parent = folder.getParent();
                if (parent != null) {
                    while (folderCache.get(parent.getID()) == null) {
                        parent = parent.getParent();
                    }
                    filePathCache.remove(folder.getParent().getPath());
                    folderCache.remove(parent.getID());
                    hsqlfilePathCache.remove(folder.getParent().getPath());
                    hsqlfolderCache.remove(parent.getID());
                }
            }
        } else {
            folder = this.getFolderById(folderId);
            //修改显示名称
            if (folder.getPath().equals(path) && description != null && !folder.getDescription().equals(description)) {
                this.updateFolderInfo(path, folder.getName(), description, type);
            }
        }
        return folder;
    }


    public VFSClientService getVfsService() {
        VFSClientService service = EsbUtil.parExpression(VFSClientService.class);
        return service;
    }

    public VFSStoreService getVFSStoreService() {
        VFSStoreService service = EsbUtil.parExpression(VFSStoreService.class);
        return service;
    }


    public VFSDiskService getVFSDiskService() {
        VFSDiskService service = EsbUtil.parExpression(VFSDiskService.class);
        return service;
    }
}
