package net.ooder.bpm.index;

import net.ooder.annotation.JLuceneIndex;
import net.ooder.common.Condition;
import net.ooder.common.JDSException;
import net.ooder.config.ListResultModel;
import net.ooder.index.config.IndexConfigFactroy;
import net.ooder.index.config.JLucene;
import net.ooder.jds.core.esb.EsbUtil;


import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class CtBPMIndexFactory {
    static CtBPMIndexFactory cacheManager;

    public static final String THREAD_LOCK = "Thread Lock";

    private final IndexConfigFactroy configFactory;

    public static CtBPMIndexFactory getInstance() {
        if (cacheManager == null) {
            synchronized (THREAD_LOCK) {
                cacheManager = new CtBPMIndexFactory();
            }
        }
        return cacheManager;
    }

    CtBPMIndexFactory() {
        this.configFactory = IndexConfigFactroy.getInstance();
    }


    public ListResultModel<List<ActivityHistoryIndex>> search(Condition<ActivityInstIndexEnmu, ActivityHistoryIndex> condition) {

        return getService().search(condition);
    }


    public void deleteAllIndex(Condition<ActivityInstIndexEnmu, ActivityHistoryIndex> condition ) {
            getService().deleteAllIndex(condition);

    }


    public void deleteIndex( JLuceneIndex index ) {
        try {
            getService().deleteIndex(configFactory.getJLuceneConfig(index));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

    }

    public <T extends JLuceneIndex> T addIndex(T index) throws JDSException {
        try {
            JLucene lucene=  getService().addIndex(configFactory.getJLuceneConfig(index)).get();
            index.setUuid(lucene.getUuid());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return index;
    }

    public BPMIndexService getService() {
        BPMIndexService service = EsbUtil.parExpression("$BPMIndexService", BPMIndexService.class);
        return service;
    }
}
