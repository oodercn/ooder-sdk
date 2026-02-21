/**
 * $RCSfile: IndexServiceAPI.java,v $
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
package net.ooder.index.web;

import net.ooder.annotation.JLuceneIndex;
import net.ooder.annotation.MethodChinaName;
import net.ooder.common.Condition;
import net.ooder.common.ConditionKey;
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
@RequestMapping("/api/fts/index")
@MethodChinaName("索引服务")
//@EsbBeanAnnotation(id = "IndexService")
public class IndexServiceAPI implements IndexService {

    public IndexService getService() {
        IndexService service = EsbUtil.parExpression(IndexService.class);
        return service;
    }
    @MethodChinaName("添加索引")
    @RequestMapping(method = RequestMethod.POST, value = "/addIndex")
    public @ResponseBody ResultModel<JLucene> addIndex(@RequestBody JLucene luceneBean) {
        return getService().addIndex(luceneBean);
    }

    @MethodChinaName("删除索引")
    @RequestMapping(method = RequestMethod.POST, value = "/deleteIndex")
    public@ResponseBody
    ResultModel<Boolean> deleteIndex(@RequestBody JLucene luceneBean) {
        return getService().deleteIndex(luceneBean);
    }
    @MethodChinaName("批量删除索引")
    @RequestMapping(method = RequestMethod.POST, value = "/deleteAllIndex")
    public @ResponseBody  <V extends JLuceneIndex, T extends ConditionKey> ResultModel<Boolean> deleteAllIndex(@RequestBody Condition<T, V> condition) {
        return getService().deleteAllIndex(condition);
    }
    @MethodChinaName("条件查询")
    @RequestMapping(method = RequestMethod.POST, value = "/search")
    public @ResponseBody <V extends JLuceneIndex, T extends ConditionKey> ListResultModel<List<V>> search(@RequestBody Condition<T, V> condition) {
        return getService().search(condition);
    }
}


