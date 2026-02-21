package net.ooder.vfs.api;

import  net.ooder.common.md5.MD5InputStream;
import  net.ooder.annotation.MethodChinaName;
import  net.ooder.config.ResultModel;
import  net.ooder.jds.core.esb.EsbUtil;
import  net.ooder.vfs.FileInfo;
import  net.ooder.vfs.Folder;
import  net.ooder.vfs.service.LocalSyncService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.nio.file.Path;

@Controller
@RequestMapping("/api/vfs/syncservice/")
@MethodChinaName(cname = "网盘管理")
public class LocalSyncServiceAPI implements LocalSyncService {

    @MethodChinaName(cname = "创建目录")
    @RequestMapping(method = RequestMethod.POST, value = "MkDir")
    public @ResponseBody
    ResultModel<Folder> mkDir(String path) {
        return getVfsService().mkDir(path);
    }

    @MethodChinaName(cname = "创建文件")
    @RequestMapping(method = RequestMethod.POST, value = "CreatFile")
    public @ResponseBody
    ResultModel<FileInfo> creatFile(String path) {
        return getVfsService().creatFile(path);
    }

    @MethodChinaName(cname = "获取目录")
    @RequestMapping(method = RequestMethod.POST, value = "GetFolderByPath")
    public @ResponseBody
    ResultModel<Folder> getFolderByPath(String path) {
        return getVfsService().getFolderByPath(path);
    }

    @MethodChinaName(cname = "获取文件")
    @RequestMapping(method = RequestMethod.POST, value = "GetFileByPath")
    public @ResponseBody
    ResultModel<FileInfo> getFileByPath(String path) {
        return getVfsService().getFileByPath(path);
    }

    @MethodChinaName(cname = "删除文件")
    @RequestMapping(method = RequestMethod.POST, value = "Delete")
    public @ResponseBody
    ResultModel<Integer> delete(String path) {
        return getVfsService().delete(path);
    }

    @MethodChinaName(cname = "上传")
    @RequestMapping(method = RequestMethod.POST, value = "Upload")
    public @ResponseBody
    ResultModel<Boolean> upload(String path, MD5InputStream in, String userId) {
        return getVfsService().upload(path, in, userId);
    }

    @MethodChinaName(cname = "下载")
    @RequestMapping(method = RequestMethod.POST, value = "DownLoad")
    public ResultModel<Boolean> downLoad(Path path, String vfsPaht) {
        return getVfsService().downLoad(path, vfsPaht);
    }

    @MethodChinaName(cname = "异步上传")
    @RequestMapping(method = RequestMethod.POST, value = "SyncUpload")
    public ResultModel<Boolean> syncUpload(Path path, String vfsPaht) {
        return getVfsService().syncUpload(path, vfsPaht);
    }

    public LocalSyncService getVfsService() {

        return (LocalSyncService) EsbUtil.parExpression(LocalSyncService.class);

    }

}
