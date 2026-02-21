package net.ooder.vfs.api;

import  net.ooder.annotation.EsbBeanAnnotation;
import  net.ooder.annotation.MethodChinaName;
import  net.ooder.common.ContextType;
import  net.ooder.common.FolderState;
import  net.ooder.common.FolderType;
import  net.ooder.common.TokenType;
import  net.ooder.config.ResultModel;
import  net.ooder.jds.core.esb.EsbUtil;
import  net.ooder.vfs.FileInfo;
import  net.ooder.vfs.FileVersion;
import  net.ooder.vfs.Folder;
import  net.ooder.vfs.service.VFSDiskService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/vfs/disk/")
@MethodChinaName(cname = "目录管理")
@EsbBeanAnnotation(dataType = ContextType.Server, tokenType = TokenType.admin)
public class VFSDiskServiceAPI implements VFSDiskService {


    @RequestMapping(method = RequestMethod.POST, value = "MkDir")
    @MethodChinaName(cname = "创建文件夹")
    public @ResponseBody
    ResultModel<Folder> mkDir(String path) {
        return getVfsService().mkDir(path);
    }

    @RequestMapping(method = RequestMethod.POST, value = "CreateFile")
    @MethodChinaName(cname = "创建文件")
    public @ResponseBody
    ResultModel<FileInfo> createFile(String path, String name) {
        return getVfsService().createFile(path, name);
    }

    @RequestMapping(method = RequestMethod.POST, value = "GetFolderByPath")
    @MethodChinaName(cname = "根据文件夹path获取文件")
    public @ResponseBody
    ResultModel<Folder> getFolderByPath(String path) {
        return getVfsService().getFolderByPath(path);
    }

    @RequestMapping(method = RequestMethod.POST, value = "GetFileInfoByPath")
    @MethodChinaName(cname = "根据逻辑地址获取文件信息")
    public @ResponseBody
    ResultModel<FileInfo> getFileInfoByPath(String path) {
        return getVfsService().getFileInfoByPath(path);
    }

    @RequestMapping(method = RequestMethod.POST, value = "Delete")
    @MethodChinaName(cname = "删除文件")
    public @ResponseBody
    ResultModel<Boolean> delete(String path) {
        return getVfsService().delete(path);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "CopyFolder")
    @MethodChinaName(cname = "COPY文件夹")
    public @ResponseBody
    ResultModel<Boolean> copyFolder(String spath, String tpaht) {
        return getVfsService().copyFolder(spath, tpaht);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "CloneFolder")
    @MethodChinaName(cname = "克隆文件夹")
    public @ResponseBody
    ResultModel<Boolean> cloneFolder(String spath, String tpaht) {
        return getVfsService().cloneFolder(spath, tpaht);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "UpdateFileInfo")
    @MethodChinaName(cname = "更新文件信息")
    public @ResponseBody
    ResultModel<Boolean> updateFileInfo(String path, String name, String description) {
        return getVfsService().updateFileInfo(path, name, description);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "updateFolderInfo")
    @MethodChinaName(cname = "更新文件夹信息")
    public @ResponseBody
    ResultModel<Boolean> updateFolderInfo(String path, String name, String description, FolderType type) {
        return getVfsService().updateFolderInfo(path, name, description, type);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "updateFolderState")
    @ResponseBody
    public ResultModel<Boolean> updateFolderState(String path, FolderState state) {
        return getVfsService().updateFolderState(path, state);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "CopyFile")
    @MethodChinaName(cname = "COPY文件信息")
    public @ResponseBody
    ResultModel<Boolean> copyFile(String path, String path2) {

        return getVfsService().copyFile(path, path2);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "CreateFile2")
    @MethodChinaName(cname = "COPY文件信息并重命名")
    public @ResponseBody
    ResultModel<FileInfo> createFile2(String path, String name, String description) {
        return getVfsService().createFile2(path, name, description);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "MkDir2")
    @MethodChinaName(cname = "创建文件夹")
    public @ResponseBody
    ResultModel<Folder> mkDir2(String path, String description, FolderType type) {
        return getVfsService().mkDir2(path, description, type);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "createFileVersion")
    @MethodChinaName(cname = "创建指定HASH版本")
    public @ResponseBody
    ResultModel<FileVersion> createFileVersion(String path, String filehash) {

        return getVfsService().createFileVersion(path, filehash);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getVersionByPath")
    @MethodChinaName(cname = "获取版本")
    public @ResponseBody
    ResultModel<FileVersion> getVersionByPath(String path) {
        return getVfsService().getVersionByPath(path);
    }


    public VFSDiskService getVfsService() {
        return (VFSDiskService) EsbUtil.parExpression(VFSDiskService.class);
    }

}
