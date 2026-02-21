package net.ooder.vfs.ct.mywork;

import  net.ooder.common.JDSException;
import  net.ooder.common.md5.MD5InputStream;
import  net.ooder.server.JDSClientService;
import  net.ooder.vfs.*;
import org.apache.http.concurrent.FutureCallback;

import java.io.File;

public interface CtMyVfsService extends JDSClientService {

    /**
     * @param folderId
     * @return
     */
    public Folder getFolderById(String folderId) throws JDSException;
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
     * @return
     */
    public FileInfo getFileByPath(String path) throws JDSException;


    /**
     * @param path
     * @return
     */
    public FileVersion getFileVersionByPath(String path) throws JDSException;


    /**
     * @param viewId
     * @return
     * @throws JDSException
     */
    public FileView getFileViewById(String viewId) throws JDSException;
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
     * @param path
     * @param inputstream
     * @throws JDSException
     */
    public void upload(String path, MD5InputStream inputstream) throws JDSException;


    /**
     * @param path
     * @param inputstream
     * @throws JDSException
     */
    public void syncUpload(String path, MD5InputStream inputstream) throws JDSException;


    /**
     * @param path
     * @param inputstream
     * @param callback
     * @throws JDSException
     */
    public void syncUpload(String path, MD5InputStream inputstream, FutureCallback callback) throws JDSException;


    /**
     * @param path
     * @param file
     * @throws JDSException
     */
    public void upload(String path, File file) throws JDSException;


    /**
     * @param path
     * @param file
     * @throws JDSException
     */
    public void syncUpload(String path, File file) throws JDSException;


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


    /**
     * @param path
     * @param description
     * @return
     * @throws JDSException
     */
    public Folder mkDir(String path, String description) throws JDSException;

    /**
     * @param fileByPath
     * @param tFolder
     * @throws JDSException
     */
    public FileInfo copyFile(FileInfo fileByPath, Folder tFolder) throws JDSException;


    public void pull(String vfspath, String localPath) throws JDSException;


    /**
     * @param vfspath
     * @param localPath
     * @throws JDSException
     */
    public void push(String vfspath, String localPath) throws JDSException;

    /**
     * @param path
     * @param name
     * @return
     * @throws JDSException
     */
    public FileInfo createFile(String path, String name) throws JDSException;


    /**
     * @param path
     * @param name
     * @param description
     * @return
     * @throws JDSException
     */
    public FileInfo createFile(String path, String name, String description) throws JDSException;


    /**
     * @param path
     * @param content
     * @param encoding
     * @throws JDSException
     */
    public void saveFileAsContent(String path, String content, String encoding) throws JDSException;

    public StringBuffer readFileAsString(String path, String encoding) throws JDSException;

    public MD5InputStream downLoad(String path) throws JDSException;

    public MD5InputStream downLoadByHash(String hash) throws JDSException;

    public MD5InputStream downLoadVersion(String versionId) throws JDSException;

}
