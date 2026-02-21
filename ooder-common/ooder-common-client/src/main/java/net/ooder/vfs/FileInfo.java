package net.ooder.vfs;

import net.ooder.annotation.*;
import net.ooder.common.cache.Cacheable;
import net.ooder.common.md5.MD5InputStream;
import net.ooder.annotation.ViewType;

import java.util.List;
import java.util.Set;

/**
 * FileInfo 接口定义了VFS文件的基本信息和操作。
 */
@ESDEntity
public interface FileInfo extends Cacheable {

    /**
     * 取得文件标识。
     * @return 文件标识
     */
    @MethodChinaName(cname = "取得文件标识")
    @Uid
    String getID();

    /**
     * 取得文件名称。
     * @return 文件名称
     */
    @MethodChinaName(cname = "取得文件名称")
    String getName();

    /**
     * 取得文件路径。
     * @return 文件路径
     */
    @MethodChinaName(cname = "取得文件路径")
    String getPath();

    /**
     * 获取文件上传者。
     * @return 文件上传者ID
     */
    @MethodChinaName(cname = "获取文件上传者")
    String getPersonId();

    /**
     * 获取文件类型。
     * @return 文件类型
     */
    @MethodChinaName(cname = "获取文件类型")
    Integer getFileType();

    /**
     * 文件创建时间。
     * @return 创建时间戳
     */
    @MethodChinaName(cname = "文件创建时间")
    Long getCreateTime();

    /**
     * 取得文件所有版本信息。
     * @return 版本列表
     */
    @MethodChinaName(cname = "取得文件所有版本信息")
    @Ref(ref = RefType.O2M, view = ViewType.GRID)
    List<FileVersion> getVersionList();

    /**
     * 获取当前视图ID集合。
     * @return 视图ID集合
     */
    @MethodChinaName(cname = "获取当前视图ID集合")
    Set<String> getCurrentViewIds();

    /**
     * 取得当前视图。
     * @return 当前视图列表
     */
    @MethodChinaName(cname = "取得当前视图")
    @Ref(ref = RefType.O2M, view = ViewType.GRID)
    List<FileView> getCurrentViews();

    /**
     * 获取文件链接ID集合。
     * @return 链接ID集合
     */
    @MethodChinaName(cname = "获取文件链接ID集合")
    Set<String> getLinkIds();

    /**
     * 获取文件链接列表。
     * @return 链接列表
     */
    @MethodChinaName(cname = "获取文件链接列表")
    @Ref(ref = RefType.O2M, view = ViewType.GRID)
    List<FileLink> getLinks();

    /**
     * 文件描述。
     * @return 文件描述
     */
    @MethodChinaName(cname = "文件描述")
    String getDescription();

    /**
     * 获取旧文件夹ID。
     * @return 旧文件夹ID
     */
    @MethodChinaName(cname = "获取旧文件夹ID")
    String getOldFolderId();

    /**
     * 设置旧文件夹ID（移动使用）。
     * @param oldFolderId 旧文件夹ID
     */
    @MethodChinaName(cname = "设置旧文件夹ID")
    void setOldFolderId(String oldFolderId);

    /**
     * 获取权限信息。
     * @return 权限信息
     */
    @MethodChinaName(cname = "获取权限信息")
    String getRight();

    /**
     * 获取文件夹ID。
     * @return 文件夹ID
     */
    @Pid
    @MethodChinaName(cname = "获取文件夹ID")
    String getFolderId();

    /**
     * 取得文件所有版本ID集合。
     * @return 版本ID集合
     */
    @MethodChinaName(cname = "取得文件所有版本ID集合")
    Set<String> getVersionIds();

    /**
     * 取得当前版本ID。
     * @return 当前版本ID
     */
    @MethodChinaName(cname = "取得当前版本ID")
    String getCurrentVersonId();

    /**
     * 取得当前版本文件hash。
     * @return 文件hash值
     */
    @MethodChinaName(cname = "取得当前版本文件hash")
    String getCurrentVersonFileHash();

    /**
     * 取得当前版本。
     * @return 当前版本
     */
    @MethodChinaName(cname = "取得当前版本")
    FileVersion getCurrentVersion();

    /**
     * 取得该文件所属文件夹。
     * @return 文件夹对象
     */
    @MethodChinaName(cname = "取得该文件所属文件夹")
    @Ref(ref = RefType.M2O, view = ViewType.DIC)
    Folder getFolder();

    /**
     * 取得当前版本文件流。
     * @return MD5输入流
     */
    @MethodChinaName(cname = "取得当前版本文件流")
    MD5InputStream getCurrentVersonInputStream();
}
