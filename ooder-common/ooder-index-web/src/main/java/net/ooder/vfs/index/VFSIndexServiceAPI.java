/**
 * $RCSfile: VFSIndexServiceAPI.java,v $
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

import net.ooder.annotation.MethodChinaName;
import net.ooder.common.Condition;
import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;
import net.ooder.index.config.JLucene;
import net.ooder.index.service.IndexService;
import net.ooder.jds.core.esb.EsbUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/api/vfs/index/")
@EsbBeanAnnotation(id = "VFSIndexService")
@MethodChinaName("检查")
public class VFSIndexServiceAPI implements VFSIndexService {

    public IndexService getService() {
        IndexService service = EsbUtil.parExpression(IndexService.class);
        return service;
    }

    @RequestMapping(method = RequestMethod.POST, value = "addIndex")
    @MethodChinaName("添加索引")
    public @ResponseBody
    ResultModel<JLucene> addIndex(@RequestBody JLucene luceneBean) {
        return getService().addIndex(luceneBean);
    }


    @RequestMapping(method = RequestMethod.POST, value = "deleteIndex")
    @MethodChinaName("删除索引")
    public @ResponseBody
    ResultModel<Boolean> deleteIndex(@RequestBody Condition<FileIndexEnmu, FileIndex> condition) {
        return getService().deleteAllIndex(condition);
    }


    @RequestMapping(method = RequestMethod.POST, value = "search")
    @MethodChinaName("条件索引")
    public @ResponseBody
    ListResultModel<List<FileIndex>> search(@RequestBody Condition<FileIndexEnmu, FileIndex> condition) {
        return getService().search(condition);
    }
}


