package net.ooder.vfs;

import net.ooder.annotation.ESDEntity;
import net.ooder.annotation.MethodChinaName;
import net.ooder.annotation.Pid;
import net.ooder.common.JDSException;
import net.ooder.common.md5.MD5InputStream;

import java.util.List;

/**
 * FileObject 接口定义了VFS文件实体的基本信息和操作。
 */
@ESDEntity
public interface FileObject extends java.io.Serializable {
    /**
     * 取得文件标识。
     * @return 文件标识
     */
    @MethodChinaName(cname = "取得文件标识")
    @Pid
    String getID();

    /**
     * 设置文件标识。
     * @param id 文件标识
     */
    @MethodChinaName(cname = "设置文件标识")
    void setID(String id);

    /**
     * 取得文件名称。
     * @return 文件名称
     */
    @MethodChinaName(cname = "取得文件名称")
    String getName();

    /**
     * 设置文件名称。
     * @param name 文件名称
     */
    @MethodChinaName(cname = "设置文件名称")
    void setName(String name);

    /**
     * 取得根目录地址。
     * @return 根目录地址
     */
    @MethodChinaName(cname = "取得根目录地址")
    String getRootPath();

    /**
     * 设置根目录地址。
     * @param path 根目录地址
     */
    @MethodChinaName(cname = "设置根目录地址")
    void setRootPath(String path);

    /**
     * 取得文件读取适配器。
     * @return 文件读取适配器
     */
    @MethodChinaName(cname = "文件读取适配器")
    String getAdapter();

    /**
     * 设置文件读取适配器。
     * @param adapter 文件读取适配器
     */
    @MethodChinaName(cname = "设置文件读取适配器")
    void setAdapter(String adapter);

    /**
     * 取得文件大小。
     * @return 文件大小
     */
    @MethodChinaName(cname = "取得文件大小")
    Long getLength();

    /**
     * 设置文件大小。
     * @param length 文件大小
     */
    @MethodChinaName(cname = "设置文件大小")
    void setLength(Long length);

    /**
     * 取得文件hash。
     * @return 文件hash值
     */
    @MethodChinaName(cname = "取得文件hash")
    String getHash();

    /**
     * 设置文件hash。
     * @param hash 文件hash值
     */
    @MethodChinaName(cname = "设置文件hash")
    void setHash(String hash);

    /**
     * 取得文件物理路径。
     * @return 文件物理路径
     */
    @MethodChinaName(cname = "取得文件物理路径")
    String getPath();

    /**
     * 设置文件物理路径。
     * @param path 文件物理路径
     */
    @MethodChinaName(cname = "设置文件物理路径")
    void setPath(String path);

    /**
     * 取得创建时间。
     * @return 创建时间戳
     */
    @MethodChinaName(cname = "取得创建时间")
    Long getCreateTime();

    /**
     * 设置创建时间。
     * @param createTime 创建时间戳
     */
    @MethodChinaName(cname = "设置创建时间")
    void setCreateTime(Long createTime);

    /**
     * 下载片段。
     * @return MD5输入流
     * @throws JDSException JDS异常
     */
    @MethodChinaName(cname = "下载片段")
    MD5InputStream downLoad() throws JDSException;

    /**
     * 追加片段。
     * @param str 要追加的字符串
     * @return 追加结果
     * @throws JDSException JDS异常
     */
    @MethodChinaName(cname = "追加片段")
    Integer writeLine(String str) throws JDSException;

    /**
     * 获取指定行数。
     * @param lineNums 行号列表
     * @return 指定行的内容列表
     * @throws JDSException JDS异常
     */
    @MethodChinaName(cname = "获取指定行数")
    List<String> readLine(List<Integer> lineNums) throws JDSException;

}
