package net.ooder.vfs.service;

import  net.ooder.annotation.MethodChinaName;
import  net.ooder.config.ListResultModel;
import  net.ooder.config.ResultModel;
import  net.ooder.vfs.*;

import java.util.List;
import java.util.Set;

public interface VFSClientService {


    @MethodChinaName(cname = "根据文件夹ID获取文件")
    public ResultModel<Folder> getFolderByID(String folderId);

    @MethodChinaName(cname = "根据文件ID获取文件")
    public ResultModel<FileInfo> getFileInfoByID(String fileId);


    @MethodChinaName(cname = "获取文件实体")
    public ResultModel<FileCopy> getFileCopyById(String id);

    @MethodChinaName(cname = "获取文件版本")
    public ResultModel<FileVersion> getVersionById(String versionId);

    @MethodChinaName(cname = "批量删除文件")
    public ResultModel<Boolean> deleteFile(String[] fileIds);

    @MethodChinaName(cname = "批量删除文件夹")
    public ResultModel<Boolean> deleteFolder(String folderId);

    @MethodChinaName(cname = "COPY块文件")
    public ResultModel<Boolean> copyView(List<FileView> views, FileVersion newVersion);


    @MethodChinaName(cname = "获取已删除文件")
    public ListResultModel<List<FileInfo>> getPersonDeletedFile(String userId);

    @MethodChinaName(cname = "获取已删除文件夹")
    public ListResultModel<List<Folder>> getPersonDeletedFolder(String userId);


    @MethodChinaName(cname = "获取已删除文件")
    public ResultModel<FileInfo> getDeletedFile(String fileId);

    @MethodChinaName(cname = "获取已删除文件夹")
    public ResultModel<Folder> getDeletedFolder(String folderId);

    @MethodChinaName(cname = "移除文件")
    public ResultModel<Boolean> removeFileInfo(String fileId);

    @MethodChinaName(cname = "获取文件链接")
    public ResultModel<FileLink> getFileLinkByID(String linkId);

    @MethodChinaName(cname = "获取子文件")
    public ListResultModel<List<FileInfo>> getChiledFileList(String id);

    @MethodChinaName(cname = "获取递归文件")
    public ListResultModel<List<FileInfo>> getChiledFileRecursivelyList(String id);

    @MethodChinaName(cname = "获取递归文件夹")
    public ListResultModel<List<Folder>> getChildrenFolderRecursivelyList(String id);

    @MethodChinaName(cname = "获取子文件夹")
    public ListResultModel<List<Folder>> getChildrenFolderList(String id);

    @MethodChinaName(cname = "批量装载文件夹")
    public ListResultModel<List<Folder>> loadFolderList(String[] ids);

    @MethodChinaName(cname = "批量装载文件")
    public ListResultModel<List<FileInfo>> loadFileList(String[] ids);

    @MethodChinaName(cname = "批量装载视图")
    public ListResultModel<List<FileView>> loadFileViewList(String[] ids);

    @MethodChinaName(cname = "批量装载版本")
    public ListResultModel<List<FileVersion>> loadVersionList(String[] ids);


    @MethodChinaName(cname = "获取视图")
    public ResultModel<FileView> getFileViewByID(String fileViewId);

    @MethodChinaName(cname = "创建文件块")
    public ResultModel<FileView> createViewByVersionId(String versionId, String fileObjectId, Integer fileIndex);


    @MethodChinaName(cname = "更新文件版本信息")
    public ResultModel<Boolean> updateFileVersionInfo(String fileVersionId, String hash);



    @MethodChinaName(cname = "更新文件视图信息")
    public ResultModel<Boolean> updateFileViewInfo(FileView view);

    @MethodChinaName(cname = "获取所有同源文件")
    public ListResultModel<Set<String>> getVersionByHash(String hash);


}
