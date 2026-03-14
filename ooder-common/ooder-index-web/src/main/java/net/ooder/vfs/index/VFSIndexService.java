/**
 * $RCSfile: VFSIndexService.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.vfs.index;

import net.ooder.common.Condition;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;
import net.ooder.index.config.JLucene;
import net.ooder.annotation.MethodChinaName;

import java.util.List;

public interface VFSIndexService {

    @MethodChinaName("添加VFS索引")
    public <T extends  JLucene>ResultModel<T> addIndex(T luceneBean);

    @MethodChinaName("删除VFS索引")
    public ResultModel<Boolean> deleteIndex(Condition<FileIndexEnmu, FileIndex> condition);

    @MethodChinaName("搜索VFS索引")
    public ListResultModel<List<FileIndex>> search(Condition<FileIndexEnmu, FileIndex> condition);

}


