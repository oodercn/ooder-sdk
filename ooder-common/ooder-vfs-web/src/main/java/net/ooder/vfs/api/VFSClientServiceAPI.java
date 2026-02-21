package net.ooder.vfs.api;

import  net.ooder.annotation.EsbBeanAnnotation;
import  net.ooder.annotation.MethodChinaName;
import  net.ooder.common.ContextType;
import  net.ooder.common.TokenType;
import  net.ooder.config.ListResultModel;
import  net.ooder.config.ResultModel;
import  net.ooder.jds.core.esb.EsbUtil;
import  net.ooder.vfs.*;
import  net.ooder.vfs.service.VFSClientService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Set;

/**
 * VFSClientServiceAPI 提供分布式存储服务的REST API端点。
 */
@Controller
@RequestMapping("/api/vfs/clientservice/")
@MethodChinaName(cname = "分布式存储服务")
@EsbBeanAnnotation(dataType = ContextType.Server, tokenType = TokenType.admin)
public class VFSClientServiceAPI implements VFSClientService {

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "GetFolderByID")
    @MethodChinaName(cname = "获取文件夹")
    public @ResponseBody
    ResultModel<Folder> getFolderByID(String folderId) {
        return getVfsService().getFolderByID(folderId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "GetFileInfoByID")
    @MethodChinaName(cname = "获取文件")
    public @ResponseBody
    ResultModel<FileInfo> getFileInfoByID(String fileId) {
        return getVfsService().getFileInfoByID(fileId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "GetFileCopyById")
    @MethodChinaName(cname = "获取文件副本")
    public @ResponseBody
    ResultModel<FileCopy> getFileCopyById(String id) {
        return getVfsService().getFileCopyById(id);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "GetVersionById")
    @MethodChinaName(cname = "获取文件版本")
    public @ResponseBody
    ResultModel<FileVersion> getVersionById(String versionId) {
        return getVfsService().getVersionById(versionId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "DeleteFile")
    @MethodChinaName(cname = "删除文件")
    public @ResponseBody
    ResultModel<Boolean> deleteFile(@RequestBody String[] fileIds) {
        return getVfsService().deleteFile(fileIds);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "DeleteFolder")
    @MethodChinaName(cname = "删除文件夹")
    public @ResponseBody
    ResultModel<Boolean> deleteFolder(String folderId) {
        return getVfsService().deleteFolder(folderId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "CopyView")
    @MethodChinaName(cname = "复制视图")
    public @ResponseBody
    ResultModel<Boolean> copyView(@RequestBody List<FileView> views, @RequestBody FileVersion newVersion) {
        return getVfsService().copyView(views, newVersion);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "GetPersonDeletedFile")
    @MethodChinaName(cname = "获取回收站文件", display = false)
    public @ResponseBody
    ListResultModel<List<FileInfo>> getPersonDeletedFile(String userId) {
        return getVfsService().getPersonDeletedFile(userId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "GetPersonDeletedFolder")
    @MethodChinaName(cname = "获取个人回收站文件", display = false)
    public @ResponseBody
    ListResultModel<List<Folder>> getPersonDeletedFolder(String userId) {
        return getVfsService().getPersonDeletedFolder(userId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "GetDeletedFile")
    @MethodChinaName(cname = "获取已删除文件", display = false)
    public @ResponseBody
    ResultModel<FileInfo> getDeletedFile(String fileId) {
        return getVfsService().getDeletedFile(fileId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "GetDeletedFolder")
    @MethodChinaName(cname = "获取已删除文件夹", display = false)
    public @ResponseBody
    ResultModel<Folder> getDeletedFolder(String folderId) {
        return getVfsService().getDeletedFolder(folderId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "RemoveFileInfo")
    @MethodChinaName(cname = "删除文件", display = false)
    public @ResponseBody
    ResultModel<Boolean> removeFileInfo(String fileId) {
        return getVfsService().removeFileInfo(fileId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "GetFileLinkByID")
    @MethodChinaName(cname = "获取文件链接")
    public @ResponseBody
    ResultModel<FileLink> getFileLinkByID(String linkId) {
        return getVfsService().getFileLinkByID(linkId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "GetChiledFileList")
    @MethodChinaName(cname = "获取文件列表")
    public @ResponseBody
    ListResultModel<List<FileInfo>> getChiledFileList(String id) {
        return getVfsService().getChiledFileList(id);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "GetChildrenFolderRecursivelyList")
    @MethodChinaName(cname = "递归子文件夹")
    public @ResponseBody
    ListResultModel<List<Folder>> getChildrenFolderRecursivelyList(String id) {
        return getVfsService().getChildrenFolderRecursivelyList(id);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "GetChiledFileRecursivelyList")
    @MethodChinaName(cname = "递归文件")
    public @ResponseBody
    ListResultModel<List<FileInfo>> getChiledFileRecursivelyList(String id) {
        return getVfsService().getChiledFileRecursivelyList(id);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "GetChildrenFolderList")
    @MethodChinaName(cname = "获取子文件夹")
    public @ResponseBody
    ListResultModel<List<Folder>> getChildrenFolderList(String id) {
        return getVfsService().getChildrenFolderList(id);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "LoadFolderList")
    @MethodChinaName(cname = "批量装载文件夹", display = false)
    public @ResponseBody
    ListResultModel<List<Folder>> loadFolderList(@RequestBody String[] ids) {
        return getVfsService().loadFolderList(ids);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "LoadFileList")
    @MethodChinaName(cname = "批量装载文件", display = false)
    public @ResponseBody
    ListResultModel<List<FileInfo>> loadFileList(@RequestBody String[] ids) {
        return getVfsService().loadFileList(ids);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "LoadVersionList")
    @MethodChinaName(cname = "批量装载文件版本", display = false)
    public @ResponseBody
    ListResultModel<List<FileVersion>> loadVersionList(@RequestBody String[] ids) {
        return getVfsService().loadVersionList(ids);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "GetFileViewByID")
    @MethodChinaName(cname = "获取视图")
    public @ResponseBody
    ResultModel<FileView> getFileViewByID(String fileViewId) {
        return getVfsService().getFileViewByID(fileViewId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "CreateViewByVersionId")
    @MethodChinaName(cname = "创建视图")
    public @ResponseBody
    ResultModel<FileView> createViewByVersionId(String versionId, String objectId, Integer fileIndex) {
        return getVfsService().createViewByVersionId(versionId, objectId, fileIndex);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "UpdateFileVersionInfo")
    @MethodChinaName(cname = "更新版本信息")
    public @ResponseBody
    ResultModel<Boolean> updateFileVersionInfo(String fileVersionId, String hash) {
        return getVfsService().updateFileVersionInfo(fileVersionId, hash);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "UpdateFileViewInfo")
    @MethodChinaName(cname = "更新视图信息")
    public @ResponseBody
    ResultModel<Boolean> updateFileViewInfo(@RequestBody FileView view) {
        return getVfsService().updateFileViewInfo(view);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "loadFileViewList")
    @MethodChinaName(cname = "批量装载视图信息", display = false)
    public @ResponseBody
    ListResultModel<List<FileView>> loadFileViewList(@RequestBody String[] ids) {
        return getVfsService().loadFileViewList(ids);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "GetVersionByHash")
    @MethodChinaName(cname = "根据HASH查询版本", display = false)
    public @ResponseBody
    ListResultModel<Set<String>> getVersionByHash(String hash) {
        return getVfsService().getVersionByHash(hash);
    }

    /**
     * 获取VFS服务实例。
     * @return VFSClientService实例
     */
    public VFSClientService getVfsService() {
        return (VFSClientService) EsbUtil.parExpression(VFSClientService.class);
    }

}
