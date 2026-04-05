package net.ooder.bpm.index;

import net.ooder.common.Condition;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;
import net.ooder.index.config.JLucene;

import java.util.List;

public interface BPMIndexService {

    public ResultModel<JLucene> addIndex(JLucene luceneBean);

    public ResultModel<Boolean> deleteIndex(JLucene luceneBean);


    public ResultModel<Boolean> deleteAllIndex(Condition<ActivityInstIndexEnmu, ActivityHistoryIndex> condition);

    public ListResultModel<List<ActivityHistoryIndex>> search(Condition<ActivityInstIndexEnmu, ActivityHistoryIndex> condition);

}
