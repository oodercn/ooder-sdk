package net.ooder.vfs.ct.admin;

import  net.ooder.common.FolderType;
import  net.ooder.common.JDSException;
import  net.ooder.common.md5.MD5InputStream;
import  net.ooder.server.JDSClientService;
import  net.ooder.vfs.*;
import org.apache.http.concurrent.FutureCallback;

import java.io.File;
import java.util.List;
import java.util.Set;

public interface CtVfsAdminService extends JDSClientService {

    /**
     * @param folderId
     * @return
     */
    public Folder getFolderById(String folderId) throws JDSException;


    /**
     * @param path
     * @return
     */
    public void clearCache(String path) throws JDSException;

    /**
     * @param path
     * @return
     */
    public void clearFileCache(String path) throws JDSException;


    /**
     * @param hash
     * @return
     */
    public void clearFileObjectCache(String hash) throws JDSException;

    /**
     * @param viewIds
     * @return
     * @throws JDSException
     */
    public List<FileView> loadViews(Set<String> viewIds) throws JDSException;

    /**
     * @param path
     * @return
     */
    public void clearFileVersionCache(String path) throws JDSException;


    /**
     * @param folderIds
     * @return
     * @throws JDSException
     */
    public List<Folder> loadFolers(Set<String> folderIds) throws JDSException;

    /**
     * @param fileObjectId
     * @return
     */
    public FileObject getFileObjectById(String fileObjectId) throws JDSException;

    /**
     * @param path
     * @return
     */
    public Folder getFolderByPath(String path) throws JDSException;

    /**
     * @return
     */
    public FileInfo getFileById(String fileId) throws JDSException;


    /**
     * @param fileIds
     * @return
     * @throws JDSException
     */
    public List<FileInfo> loadFiles(Set<String> fileIds) throws JDSException;

    /**
     * @return
     */
    public FileInfo getFileByPath(String path) throws JDSException;

    /**
     * @param versionId
     * @return
     */
    public FileVersion getFileVersionById(String versionId) throws JDSException;


    /**
     * @param path
     * @return
     */
    public FileVersion getFileVersionByPath(String path) throws JDSException;


    /**
     * @param path
     * @return
     */
    public List<FileVersion> getFileVersionsByHash(String path) throws JDSException;


    /**
     * @param versionIds
     * @return
     */
    public List<FileVersion> loadVersionByIds(Set<String> versionIds) throws JDSException;

    /**
     * @param viewId
     * @return
     * @throws JDSException
     */
    public FileView getFileViewById(String viewId) throws JDSException;

    /**
     * @param objectId
     * @param str
     * @return
     * @throws JDSException
     */
    public Integer writeLine(String objectId, String str) throws JDSException;

    /**
     * @param objectId
     * @param lineNums
     * @return
     * @throws JDSException
     */
    public List<String> readLine(String objectId, List<Integer> lineNums) throws JDSException;

    /**
     * @param folderId
     * @throws JDSException
     */
    public void deleteFolder(String folderId) throws JDSException;

    /**
     * @param fileInfoId
     * @throws JDSException
     */
    public void deleteFile(String fileInfoId) throws JDSException;

    /**
     * @param versionId
     * @throws JDSException
     */
    public void deleteFileVersion(String versionId) throws JDSException;


    /**
     * @param fileInfo
     * @param name
     * @param description
     * @throws JDSException
     */
    public void updateFileInfo(FileInfo fileInfo, String name, String description) throws JDSException;

    /**
     * @param folder
     * @param name
     * @param description
     * @throws JDSException
     */
    public void updateFolderInfo(Folder folder, String name, String description) throws JDSException;


    /**
     * @param fileVersionId
     * @param hash
     * @throws JDSException
     */
    public void updateFileVersionInfo(String fileVersionId, String hash) throws JDSException;


    /**
     * @param path
     * @param filehash
     * @throws JDSException
     */
    public FileVersion createFileVersion(String path, String filehash) throws JDSException;


    /**
     * @param view
     * @throws JDSException
     */
    public void updateFileViewInfo(FileView view) throws JDSException;


    /**
     * @param path
     * @param inputstream
     * @param personId
     * @throws JDSException
     */
    public void upload(String path, MD5InputStream inputstream, String personId) throws JDSException;


    /**
     * @param inputstream
     * @throws JDSException
     */
    public FileObject createFileObject(MD5InputStream inputstream) throws JDSException;


    /**
     * @param file
     * @throws JDSException
     */
    public FileObject createFileObject(File file) throws JDSException;


    /**
     * @param path
     * @param inputstream
     * @param personId
     * @throws JDSException
     */
    public void syncUpload(String path, MD5InputStream inputstream, String personId) throws JDSException;


    /**
     * @param path
     * @param inputstream
     * @param personId
     * @param callback
     * @throws JDSException
     */
    public void syncUpload(String path, MD5InputStream inputstream, String personId, FutureCallback callback) throws JDSException;


    /**
     * @param path
     * @param file
     * @param personId
     * @throws JDSException
     */
    public void upload(String path, File file, String personId) throws JDSException;


    /**
     * @param path
     * @param file
     * @param personId
     * @throws JDSException
     */
    public void syncUpload(String path, File file, String personId) throws JDSException;


    /**
     * @param fileVersionId
     * @param fileObjectId
     * @param fileIndex
     * @return
     * @throws JDSException
     */
    public FileView createView(String fileVersionId, String fileObjectId, Integer fileIndex) throws JDSException;

    /**
     * @param fileVersionId
     * @return
     * @throws JDSException
     */
    public MD5InputStream getInputStreamByVersionid(String fileVersionId) throws JDSException;

    /**
     * @param linkId
     * @return
     * @throws JDSException
     */
    public FileLink getFileLinkById(String linkId) throws JDSException;

    /**
     * @param spath
     * @param tPath
     * @throws JDSException
     */
    public void copyFolder(String spath, String tPath) throws JDSException;


    /**
     * @param spath
     * @param tPath
     * @throws JDSException
     */
    public void cloneFolder(String spath, String tPath) throws JDSException;


    /**
     * @param path
     * @return
     * @throws JDSException
     */
    public Folder mkDir(String path) throws JDSException;

    public Folder mkDir(String path, String description, FolderType type) throws JDSException;

    /**
     * @param fileByPath
     * @param tFolder
     * @throws JDSException
     */
    public FileInfo copyFile(FileInfo fileByPath, Folder tFolder) throws JDSException;


    public void pull(String vfspath, String localPath) throws JDSException;

    public void push(String vfspath, String localPath) throws JDSException;

    /**
     * @param path
     * @param name
     * @return
     * @throws JDSException
     */
    public FileInfo createFile(String path, String name) throws JDSException;


    public FileInfo createFile(String path, String name, String description) throws JDSException;


    public void saveFileAsContent(String path, String content, String encoding) throws JDSException;


    public StringBuffer readFileAsString(String path, String encoding) throws JDSException;


    public MD5InputStream downLoad(String path) throws JDSException;


    public MD5InputStream downLoadByHash(String hash) throws JDSException;


    public MD5InputStream downLoadByObjectId(String objectId) throws JDSException;


    public MD5InputStream downLoadVersion(String versionId) throws JDSException;

    public FileObject getFileObjectByHash(String hash);

    public void updateFileObject(FileObject object);

    public void deleteFileObject(String ID);

    public FileObject createFileObject();

    public FileObject createFileObjectAsContent(String content);
}
