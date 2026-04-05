package net.ooder.bpm.index;

import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.annotation.MethodChinaName;
import net.ooder.common.Condition;
import net.ooder.common.ContextType;
import net.ooder.common.TokenType;
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
@RequestMapping("/jds/bpm/index/")
@MethodChinaName(cname = "流程全文检索接口")
@EsbBeanAnnotation(dataType = ContextType.Server, tokenType = TokenType.user)
public class BPMIndexServiceAPI implements BPMIndexService {

    public IndexService getService() {
        IndexService service = EsbUtil.parExpression(IndexService.class);
        return service;
    }

    @RequestMapping(method = RequestMethod.POST, value = "addIndex")
    @MethodChinaName(cname = "添加索引")
    public @ResponseBody
    ResultModel<JLucene> addIndex(@RequestBody JLucene luceneBean) {
        return getService().addIndex(luceneBean);
    }


    @RequestMapping(method = RequestMethod.POST, value = "deleteAllIndex")
    @MethodChinaName(cname = "根据条件删除索引")
    public @ResponseBody
    ResultModel<Boolean> deleteAllIndex(@RequestBody Condition<ActivityInstIndexEnmu, ActivityHistoryIndex> condition) {
        return getService().deleteAllIndex(condition);
    }

    @RequestMapping(method = RequestMethod.POST, value = "deleteIndex")
    @MethodChinaName(cname = "删除索引")
    public @ResponseBody
    ResultModel<Boolean> deleteIndex(@RequestBody JLucene luceneBean) {
        return getService().deleteIndex(luceneBean);
    }

    @RequestMapping(method = RequestMethod.POST, value = "search")
    @MethodChinaName(cname = "查询")
    public @ResponseBody
    ListResultModel<List<ActivityHistoryIndex>> search(@RequestBody Condition<ActivityInstIndexEnmu, ActivityHistoryIndex> condition) {
        return getService().search(condition);
    }
}
