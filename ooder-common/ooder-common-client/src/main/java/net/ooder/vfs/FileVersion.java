package net.ooder.vfs;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.annotation.ESDEntity;
import net.ooder.annotation.MethodChinaName;
import net.ooder.common.md5.MD5InputStream;
import net.ooder.common.md5.MD5OutputStream;

import java.util.List;
import java.util.Set;

/**
 * FileVersion 接口定义了VFS文件版本的基本信息和操作。
 */
@ESDEntity
public interface FileVersion extends java.io.Serializable {

    /**
     * 获取版本文件ID。
     * @return 版本文件ID
     */
    @MethodChinaName(cname = "获取版本文件Id")
    String getVersionID();

    /**
     * 获取源版本ID。
     * @return 源版本ID
     */
    @MethodChinaName(cname = "源版本ID")
    String getSourceId();

    /**
     * 获取源文件ID。
     * @return 源文件ID
     */
    @MethodChinaName(cname = "获取源文件Id")
    String getFileId();

    /**
     * 取得版本名称。
     * @return 版本名称
     */
    @MethodChinaName(cname = "取得版本名称")
    String getVersionName();

    /**
     * 获取文件名称。
     * @return 文件名称
     */
    @MethodChinaName(cname = "获取文件名称")
    String getFileName();

    /**
     * 获取版本索引位置。
     * @return 版本索引位置
     */
    @MethodChinaName(cname = "获取版本索引位置")
    Integer getIndex();

    /**
     * 获取文件长度。
     * @return 文件长度
     */
    @MethodChinaName(cname = "获取文件长度")
    Long getLength();

    /**
     * 获取文件对象ID。
     * @return 文件对象ID
     */
    @MethodChinaName(cname = "获取文件对象ID")
    String getFileObjectId();

    /**
     * 设置文件对象ID。
     * @param objectId 文件对象ID
     */
    @MethodChinaName(cname = "设置文件对象ID")
    void setFileObjectId(String objectId);

    /**
     * 获取人员ID。
     * @return 人员ID
     */
    @MethodChinaName(cname = "获取人员ID")
    String getPersonId();

    /**
     * 获取源文件。
     * @return 文件对象
     */
    @MethodChinaName(cname = "获取源文件")
    @JSONField(serialize = false)
    FileObject getFileObject();

    /**
     * 获取源文件路径。
     * @return 文件路径
     */
    @MethodChinaName(cname = "获取源文件路径")
    String getPath();

    /**
     * 写入一行内容。
     * @param str 要写入的字符串
     * @return 写入结果
     */
    @MethodChinaName(cname = "写入一行内容")
    Integer writeLine(String str);

    /**
     * 创建视图。
     * @param objectId 对象ID
     * @param fileIndex 文件索引
     * @return 创建的视图
     */
    @JSONField(serialize = false)
    @MethodChinaName(cname = "创建视图")
    FileView createView(String objectId, Integer fileIndex);

    /**
     * 取得当前版本文件MD5流。
     * @return MD5输入流
     */
    @JSONField(serialize = false)
    @MethodChinaName(cname = "取得当前版本文件MD5流")
    MD5InputStream getInputStream();

    /**
     * 取得当前版本文件MD5输出流。
     * @return MD5输出流
     */
    @JSONField(serialize = false)
    @MethodChinaName(cname = "取得当前版本文件MD5输出流")
    MD5OutputStream getOutputStream();

    /**
     * 获取视图列表。
     * @return 视图列表
     */
    @JSONField(serialize = false)
    @MethodChinaName(cname = "获取视图列表")
    List<FileView> getViews();

    /**
     * 获取版本创建时间。
     * @return 创建时间戳
     */
    @MethodChinaName(cname = "获取版本创建时间")
    Long getCreateTime();

    /**
     * 获取视图ID集合。
     * @return 视图ID集合
     */
    @MethodChinaName(cname = "获取视图ID集合")
    Set<String> getViewIds();

}
