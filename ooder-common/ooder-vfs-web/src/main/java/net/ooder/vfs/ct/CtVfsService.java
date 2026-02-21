package net.ooder.vfs.ct;

import  net.ooder.common.FolderState;
import  net.ooder.common.FolderType;
import  net.ooder.common.JDSException;
import  net.ooder.common.md5.MD5InputStream;
import  net.ooder.server.JDSClientService;
import  net.ooder.vfs.*;
import org.apache.http.concurrent.FutureCallback;

import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * VFS 服务核心接口
 * 定义了虚拟文件系统的核心功能，包括文件夹管理、文件操作、缓存管理等
 * 
 * @author ooder team
 * @version 2.0
 * @since 2025-08-25
 */
public interface CtVfsService extends JDSClientService {

    /**
     * 根据文件夹ID获取文件夹信息。
     * @param folderId 文件夹ID
     * @return Folder 文件夹对象
     * @throws JDSException JDS异常
     */
    Folder getFolderById(String folderId) throws JDSException;

    /**
     * 清理指定路径的缓存。
     * @param path 路径
     * @throws JDSException JDS异常
     */
    void clearCache(String path) throws JDSException;

    /**
     * 移除指定路径的缓存。
     * @param path 路径
     * @throws JDSException JDS异常
     */
    void removeCache(String path) throws JDSException;

    /**
     * 清理指定路径的文件缓存。
     * @param path 路径
     * @throws JDSException JDS异常
     */
    void clearFileCache(String path) throws JDSException;

    /**
     * 清理指定哈希值的文件对象缓存。
     * @param hash 文件哈希值
     * @throws JDSException JDS异常
     */
    void clearFileObjectCache(String hash) throws JDSException;

    /**
     * 加载多个视图信息。
     * @param viewIds 视图ID集合
     * @return List<FileView> 视图列表
     * @throws JDSException JDS异常
     */
    List<FileView> loadViews(Set<String> viewIds) throws JDSException;

    /**
     * 清理指定路径的文件版本缓存。
     * @param path 路径
     * @throws JDSException JDS异常
     */
    void clearFileVersionCache(String path) throws JDSException;

    /**
     * 加载多个文件夹信息。
     * @param folderIds 文件夹ID集合
     * @return List<Folder> 文件夹列表
     * @throws JDSException JDS异常
     */
    List<Folder> loadFolers(Set<String> folderIds) throws JDSException;

    /**
     * 根据文件对象ID获取文件对象。
     * @param fileObjectId 文件对象ID
     * @return FileObject 文件对象
     * @throws JDSException JDS异常
     */
    FileObject getFileObjectById(String fileObjectId) throws JDSException;

    /**
     * 加载多个文件对象。
     * @param objectIds 文件对象ID集合
     * @return List<FileObject> 文件对象列表
     * @throws JDSException JDS异常
     */
    List<FileObject> loadObjects(Set<String> objectIds) throws JDSException;

    /**
     * 根据路径获取文件夹信息。
     * @param path 路径
     * @return Folder 文件夹对象
     * @throws JDSException JDS异常
     */
    Folder getFolderByPath(String path) throws JDSException;

    /**
     * 根据文件ID获取文件信息。
     * @param fileId 文件ID
     * @return FileInfo 文件信息
     * @throws JDSException JDS异常
     */
    FileInfo getFileById(String fileId) throws JDSException;

    /**
     * 加载多个文件信息。
     * @param fileIds 文件ID集合
     * @return List<FileInfo> 文件信息列表
     * @throws JDSException JDS异常
     */
    List<FileInfo> loadFiles(Set<String> fileIds) throws JDSException;

    /**
     * 根据路径获取文件信息。
     * @param path 路径
     * @return FileInfo 文件信息
     * @throws JDSException JDS异常
     */
    FileInfo getFileByPath(String path) throws JDSException;

    /**
     * 根据版本ID获取文件版本。
     * @param versionId 版本ID
     * @return FileVersion 文件版本
     * @throws JDSException JDS异常
     */
    FileVersion getFileVersionById(String versionId) throws JDSException;

    /**
     * 根据路径获取文件版本。
     * @param path 路径
     * @return FileVersion 文件版本
     * @throws JDSException JDS异常
     */
    FileVersion getFileVersionByPath(String path) throws JDSException;

    /**
     * 根据哈希值获取文件版本列表。
     * @param path 路径
     * @return List<FileVersion> 文件版本列表
     * @throws JDSException JDS异常
     */
    List<FileVersion> getFileVersionsByHash(String path) throws JDSException;

    /**
     * 加载多个文件版本。
     * @param versionIds 版本ID集合
     * @return List<FileVersion> 文件版本列表
     * @throws JDSException JDS异常
     */
    List<FileVersion> loadVersionByIds(Set<String> versionIds) throws JDSException;

    /**
     * 根据视图ID获取视图信息。
     * @param viewId 视图ID
     * @return FileView 视图信息
     * @throws JDSException JDS异常
     */
    FileView getFileViewById(String viewId) throws JDSException;

    /**
     * 向文件对象写入一行内容。
     * @param objectId 文件对象ID
     * @param str 要写入的字符串
     * @return Integer 写入结果
     * @throws JDSException JDS异常
     */
    Integer writeLine(String objectId, String str) throws JDSException;

    /**
     * 读取文件对象的指定行。
     * @param objectId 文件对象ID
     * @param lineNums 行号列表
     * @return List<String> 读取的行内容列表
     * @throws JDSException JDS异常
     */
    List<String> readLine(String objectId, List<Integer> lineNums) throws JDSException;

    /**
     * 删除文件夹。
     * @param folderId 文件夹ID
     * @throws JDSException JDS异常
     */
    void deleteFolder(String folderId) throws JDSException;

    /**
     * 删除文件。
     * @param fileInfoId 文件信息ID
     * @throws JDSException JDS异常
     */
    void deleteFile(String fileInfoId) throws JDSException;

    /**
     * 删除文件版本。
     * @param versionId 版本ID
     * @throws JDSException JDS异常
     */
    void deleteFileVersion(String versionId) throws JDSException;

    /**
     * 更新文件信息。
     * @param fileInfo 文件信息
     * @param name 文件名
     * @param description 文件描述
     * @return FileInfo 更新后的文件信息
     * @throws JDSException JDS异常
     */
    FileInfo updateFileInfo(FileInfo fileInfo, String name, String description) throws JDSException;

    /**
     * 更新文件夹信息。
     * @param folder 文件夹对象
     * @param name 文件夹名
     * @param description 文件夹描述
     * @return Folder 更新后的文件夹对象
     * @throws JDSException JDS异常
     */
    Folder updateFolderInfo(Folder folder, String name, String description) throws JDSException;

    /**
     * 更新文件夹状态。
     * @param folder 文件夹对象
     * @param state 文件夹状态
     * @return Folder 更新后的文件夹对象
     * @throws JDSException JDS异常
     */
    Folder updateFolderState(Folder folder, FolderState state) throws JDSException;

    /**
     * 更新文件夹信息，包括类型。
     * @param folder 文件夹对象
     * @param name 文件夹名
     * @param description 文件夹描述
     * @param type 文件夹类型
     * @return Folder 更新后的文件夹对象
     * @throws JDSException JDS异常
     */
    Folder updateFolderInfo(Folder folder, String name, String description, FolderType type) throws JDSException;

    /**
     * 更新文件版本信息。
     * @param fileVersionId 文件版本ID
     * @param hash 文件哈希值
     * @throws JDSException JDS异常
     */
    void updateFileVersionInfo(String fileVersionId, String hash) throws JDSException;

    /**
     * 创建文件版本。
     * @param path 路径
     * @param filehash 文件哈希值
     * @return FileVersion 创建的文件版本
     * @throws JDSException JDS异常
     */
    FileVersion createFileVersion(String path, String filehash) throws JDSException;

    /**
     * 更新文件视图信息。
     * @param view 文件视图
     * @throws JDSException JDS异常
     */
    void updateFileViewInfo(FileView view) throws JDSException;

    /**
     * 上传文件。
     * @param path 路径
     * @param inputstream MD5输入流
     * @param personId 用户ID
     * @return FileVersion 创建的文件版本
     * @throws JDSException JDS异常
     */
    FileVersion upload(String path, MD5InputStream inputstream, String personId) throws JDSException;

    /**
     * 从MD5输入流创建文件对象。
     * @param inputstream MD5输入流
     * @return FileObject 创建的文件对象
     * @throws JDSException JDS异常
     */
    FileObject createFileObject(MD5InputStream inputstream) throws JDSException;

    /**
     * 从本地文件创建文件对象。
     * @param file 本地文件
     * @return FileObject 创建的文件对象
     * @throws JDSException JDS异常
     */
    FileObject createFileObject(File file) throws JDSException;

    /**
     * 同步上传文件。
     * @param path 路径
     * @param inputstream MD5输入流
     * @param personId 用户ID
     * @throws JDSException JDS异常
     */
    void syncUpload(String path, MD5InputStream inputstream, String personId) throws JDSException;

    /**
     * 同步上传文件，带回调。
     * @param path 路径
     * @param inputstream MD5输入流
     * @param personId 用户ID
     * @param callback 回调函数
     * @throws JDSException JDS异常
     */
    void syncUpload(String path, MD5InputStream inputstream, String personId, FutureCallback callback) throws JDSException;

    /**
     * 上传本地文件。
     * @param path 路径
     * @param file 本地文件
     * @param personId 用户ID
     * @return FileVersion 创建的文件版本
     * @throws JDSException JDS异常
     */
    FileVersion upload(String path, File file, String personId) throws JDSException;

    /**
     * 同步上传本地文件。
     * @param path 路径
     * @param file 本地文件
     * @param personId 用户ID
     * @throws JDSException JDS异常
     */
    void syncUpload(String path, File file, String personId) throws JDSException;

    /**
     * 创建文件视图。
     * @param fileVersionId 文件版本ID
     * @param fileObjectId 文件对象ID
     * @param fileIndex 文件索引
     * @return FileView 创建的文件视图
     * @throws JDSException JDS异常
     */
    FileView createView(String fileVersionId, String fileObjectId, Integer fileIndex) throws JDSException;

    /**
     * 根据版本ID获取输入流。
     * @param fileVersionId 文件版本ID
     * @return MD5InputStream MD5输入流
     * @throws JDSException JDS异常
     */
    MD5InputStream getInputStreamByVersionid(String fileVersionId) throws JDSException;

    /**
     * 根据链接ID获取文件链接。
     * @param linkId 链接ID
     * @return FileLink 文件链接
     * @throws JDSException JDS异常
     */
    FileLink getFileLinkById(String linkId) throws JDSException;

    /**
     * 复制文件夹。
     * @param spath 源路径
     * @param tPath 目标路径
     * @throws JDSException JDS异常
     */
    void copyFolder(String spath, String tPath) throws JDSException;

    /**
     * 克隆文件夹。
     * @param spath 源路径
     * @param tPath 目标路径
     * @throws JDSException JDS异常
     */
    void cloneFolder(String spath, String tPath) throws JDSException;

    /**
     * 创建文件夹。
     * @param path 路径
     * @return Folder 创建的文件夹
     * @throws JDSException JDS异常
     */
    Folder mkDir(String path) throws JDSException;

    /**
     * 创建带描述的文件夹。
     * @param path 路径
     * @param description 描述
     * @return Folder 创建的文件夹
     * @throws JDSException JDS异常
     */
    Folder mkDir(String path, String description) throws JDSException;

    /**
     * 创建带描述和类型的文件夹。
     * @param path 路径
     * @param description 描述
     * @param type 类型
     * @return Folder 创建的文件夹
     * @throws JDSException JDS异常
     */
    Folder mkDir(String path, String description, FolderType type) throws JDSException;

    /**
     * 复制文件。
     * @param fileByPath 源文件
     * @param tFolder 目标文件夹
     * @return FileInfo 复制后的文件信息
     * @throws JDSException JDS异常
     */
    FileInfo copyFile(FileInfo fileByPath, Folder tFolder) throws JDSException;

    /**
     * 从VFS拉取文件到本地。
     * @param vfspath VFS路径
     * @param localPath 本地路径
     * @throws JDSException JDS异常
     */
    void pull(String vfspath, String localPath) throws JDSException;

    /**
     * 从本地上推文件到VFS。
     * @param vfspath VFS路径
     * @param localPath 本地路径
     * @throws JDSException JDS异常
     */
    void push(String vfspath, String localPath) throws JDSException;

    /**
     * 创建文件。
     * @param path 路径
     * @param name 文件名
     * @return FileInfo 创建的文件信息
     * @throws JDSException JDS异常
     */
    FileInfo createFile(String path, String name) throws JDSException;

    /**
     * 创建文件。
     * @param filePath 文件路径
     * @return FileInfo 创建的文件信息
     * @throws JDSException JDS异常
     */
    FileInfo createFile(String filePath) throws JDSException;

    /**
     * 创建带描述的文件。
     * @param path 路径
     * @param name 文件名
     * @param description 描述
     * @return FileInfo 创建的文件信息
     * @throws JDSException JDS异常
     */
    FileInfo createFile(String path, String name, String description) throws JDSException;

    /**
     * 将内容保存为文件。
     * @param path 路径
     * @param content 内容
     * @param encoding 编码
     * @return FileInfo 创建的文件信息
     * @throws JDSException JDS异常
     */
    FileInfo saveFileAsContent(String path, String content, String encoding) throws JDSException;

    /**
     * 读取文件内容为字符串。
     * @param path 路径
     * @param encoding 编码
     * @return StringBuffer 文件内容
     * @throws JDSException JDS异常
     */
    StringBuffer readFileAsString(String path, String encoding) throws JDSException;

    /**
     * 下载文件。
     * @param path 路径
     * @return MD5InputStream MD5输入流
     * @throws JDSException JDS异常
     */
    MD5InputStream downLoad(String path) throws JDSException;

    /**
     * 根据哈希值下载文件。
     * @param hash 文件哈希值
     * @return MD5InputStream MD5输入流
     * @throws JDSException JDS异常
     */
    MD5InputStream downLoadByHash(String hash) throws JDSException;

    /**
     * 根据文件对象ID下载文件。
     * @param objectId 文件对象ID
     * @return MD5InputStream MD5输入流
     * @throws JDSException JDS异常
     */
    MD5InputStream downLoadByObjectId(String objectId) throws JDSException;

    /**
     * 下载文件版本。
     * @param versionId 版本ID
     * @return MD5InputStream MD5输入流
     * @throws JDSException JDS异常
     */
    MD5InputStream downLoadVersion(String versionId) throws JDSException;

    /**
     * 根据哈希值获取文件对象。
     * @param hash 文件哈希值
     * @return FileObject 文件对象
     */
    FileObject getFileObjectByHash(String hash);

    /**
     * 更新文件对象。
     * @param object 文件对象
     */
    void updateFileObject(FileObject object);

    /**
     * 删除文件对象。
     * @param ID 文件对象ID
     */
    void deleteFileObject(String ID);

    /**
     * 创建空文件对象。
     * @return FileObject 创建的文件对象
     */
    FileObject createFileObject();

    /**
     * 根据内容创建文件对象。
     * @param content 文件内容
     * @return FileObject 创建的文件对象
     */
    FileObject createFileObjectAsContent(String content);
}
