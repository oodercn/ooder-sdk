package net.ooder.vfs;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.annotation.*;
import net.ooder.common.FolderState;
import net.ooder.common.FolderType;
import net.ooder.common.cache.Cacheable;
import net.ooder.annotation.ViewType;

import java.util.List;
import java.util.Set;

/**
 * VFS 文件夹接口
 * 定义了虚拟文件系统中文件夹的基本信息和操作，包括文件夹层级、状态、权限等
 * 
 * @author ooder team
 * @version 2.0
 * @since 2025-08-25
 */
@ESDEntity
public interface Folder extends Cacheable {

    /**
     * 取得文件夹标识。
     * @return 文件夹标识
     */
    @MethodChinaName(cname = "取得文件夹标识")
    @Uid
    String getID();

    /**
     * 取得该文件夹所有拥有的文件列表。
     * @return 文件ID集合
     */
    @MethodChinaName(cname = "取得该文件夹所有拥有的文件列表")
    Set<String> getFileIdList();

    /**
     * 取得文件夹名称。
     * @return 文件夹名称
     */
    @MethodChinaName(cname = "取得文件夹名称")
    String getName();

    /**
     * 取得文件夹资源逻辑地址。
     * @return 文件夹路径
     */
    @MethodChinaName(cname = "取得文件夹资源逻辑地址")
    String getPath();

    /**
     * 取得父文件夹的标识。
     * @return 父文件夹标识
     */
    @MethodChinaName(cname = "取得父文件夹的标识")
    @Pid
    String getParentId();

    /**
     * 取得文件夹创建人。
     * @return 创建人ID
     */
    @MethodChinaName(cname = "取得文件夹创建人")
    String getPersonId();

    /**
     * 设置文件夹名称。
     * @param name 文件夹名称
     */
    @MethodChinaName(cname = "设置文件夹名称", display = false)
    void setName(String name);

    /**
     * 设置父节点。
     * @param parentId 父节点ID
     */
    @MethodChinaName(cname = "设置父节点", display = false)
    void setParentId(String parentId);

    /**
     * 设置人员ID。
     * @param personId 人员ID
     */
    @MethodChinaName(cname = "设置人员ID", display = false)
    void setPersonId(String personId);

    /**
     * 设置文件夹类型。
     * @param type 文件夹类型
     */
    @MethodChinaName(cname = "设置文件夹类型", display = false)
    void setFolderType(FolderType type);

    /**
     * 获取文件夹类型。
     * @return 文件夹类型
     */
    @MethodChinaName(cname = "获取文件夹类型", display = false)
    FolderType getFolderType();

    /**
     * 获取文件夹大小。
     * @return 文件夹大小
     */
    @MethodChinaName(cname = "文件夹大小", display = false)
    Long getFolderSize();

    /**
     * 获取排序号。
     * @return 排序号
     */
    @MethodChinaName(cname = "排序", display = false)
    int getOrderNum();

    /**
     * 设置排序号。
     * @param orderNum 排序号
     */
    @MethodChinaName(cname = "设置排序", display = false)
    void setOrderNum(int orderNum);

    /**
     * 获取是否删除标识。
     * @return 删除标识（0：未删除，1：已删除）
     */
    @MethodChinaName(cname = "是否删除", display = false)
    int getRecycle();

    /**
     * 设置状态。
     * @param state 文件夹状态
     */
    @MethodChinaName(cname = "设置状态", display = false)
    void setState(FolderState state);

    /**
     * 获取状态。
     * @return 文件夹状态
     */
    @MethodChinaName(cname = "获取状态", display = false)
    FolderState getState();

    /**
     * 获取创建时间。
     * @return 创建时间戳
     */
    @MethodChinaName(cname = "创建时间")
    long getCreateTime();

    /**
     * 取得父文件夹对象。
     * @return 父文件夹对象
     */
    @JSONField(serialize = false)
    @MethodChinaName(cname = "取得父文件夹对象")
    @Ref(ref = RefType.O2O, view = ViewType.DIC)
    Folder getParent();

    /**
     * 取得该应用的所有直接子节点ID列表。
     * @return 子节点ID集合
     */
    @MethodChinaName(cname = "取得该应用的所有直接子节点ID列表")
    Set<String> getChildrenIdList();

    /**
     * 取得该应用的所有直接子节点列表。
     * @return 子节点列表
     */
    @JSONField(serialize = false)
    @MethodChinaName(cname = "取得该应用的所有直接子节点列表")
    List<Folder> getChildrenList();

    /**
     * 取得该应用的所有子文件夹（递归）。
     * @return 子文件夹列表
     */
    @JSONField(serialize = false)
    @MethodChinaName(cname = "取得该应用的所有子文件夹（递归)", display = false)
    @Ref(ref = RefType.REF, view = ViewType.GRID)
    List<Folder> getChildrenRecursivelyList();

    /**
     * 取得该文件夹所有拥有的文件列表。
     * @return 文件列表
     */
    @JSONField(serialize = false)
    @MethodChinaName(cname = "取得该文件夹所有拥有的文件列表")
    @Ref(ref = RefType.O2M, view = ViewType.GRID)
    List<FileInfo> getFileList();

    /**
     * 取得该文件夹所有拥有的文件列表(递归)。
     * @return 文件列表
     */
    @JSONField(serialize = false)
    @MethodChinaName(cname = "取得该文件夹所有拥有的文件列表(递归)", display = false)
    @Ref(ref = RefType.FIND, view = ViewType.GRID)
    List<FileInfo> getFileListRecursively();

    /**
     * 增加子文件。
     * @param name 文件名
     * @param createPersonId 创建人ID
     * @return 创建的文件信息
     */
    @MethodChinaName(cname = "增加子文件")
    FileInfo createFile(String name, String createPersonId);

    /**
     * 增加子文件。
     * @param name 文件名
     * @param description 文件描述
     * @param createPersonId 创建人ID
     * @return 创建的文件信息
     */
    @MethodChinaName(cname = "增加子文件")
    FileInfo createFile(String name, String description, String createPersonId);

    /**
     * 增加子文件夹。
     * @param name 文件夹名
     * @param createPersonId 创建人ID
     * @return 创建的文件夹
     */
    @MethodChinaName(cname = "增加子文件夹")
    Folder createChildFolder(String name, String createPersonId);

    /**
     * 增加子文件夹。
     * @param name 文件夹名
     * @param description 文件夹描述
     * @param createPersonId 创建人ID
     * @return 创建的文件夹
     */
    @MethodChinaName(cname = "增加子文件夹")
    Folder createChildFolder(String name, String description, String createPersonId);

    /**
     * 获取描述。
     * @return 文件夹描述
     */
    @MethodChinaName(cname = "描述")
    String getDescription();

    /**
     * 设置描述。
     * @param description 文件夹描述
     */
    @MethodChinaName(cname = "设置描述")
    void setDescription(String description);

    /**
     * 获取系统代码。
     * @return 系统代码
     */
    @MethodChinaName(cname = "获取系统代码")
    String getSystemCode();

    /**
     * 设置系统代码。
     * @param sysCode 系统代码
     */
    @MethodChinaName(cname = "设置系统代码")
    void setSystemCode(String sysCode);

    /**
     * 设置索引。
     * @param index 索引值
     */
    @MethodChinaName(cname = "设置索引")
    void setIndex(int index);

    /**
     * 获取索引。
     * @return 索引值
     */
    @MethodChinaName(cname = "获取索引")
    int getIndex();

    /**
     * 获取更新时间。
     * @return 更新时间戳
     */
    @MethodChinaName(cname = "获取更新时间")
    Long getUpdateTime();

    /**
     * 获取缓存大小。
     * @return 缓存大小
     */
    int getCachedSize();

    /**
     * 设置命中次数。
     * @param hit 命中次数
     */
    @MethodChinaName(cname = "设置命中次数")
    void setHit(Integer hit);

    /**
     * 获取命中次数。
     * @return 命中次数
     */
    @MethodChinaName(cname = "获取命中次数")
    Integer getHit();

}
