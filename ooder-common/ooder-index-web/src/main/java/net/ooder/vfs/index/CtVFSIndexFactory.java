/**
 * $RCSfile: CtVFSIndexFactory.java,v $
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

import net.ooder.annotation.JLuceneIndex;
import net.ooder.common.Condition;
import net.ooder.config.ListResultModel;
import net.ooder.index.config.IndexConfigFactroy;
import net.ooder.jds.core.esb.EsbUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class CtVFSIndexFactory {
    static CtVFSIndexFactory cacheManager;

    public static final String THREAD_LOCK = "Thread Lock";

    private final IndexConfigFactroy configFactory;

    public static CtVFSIndexFactory getInstance() {
        if (cacheManager == null) {
            synchronized (THREAD_LOCK) {
                cacheManager = new CtVFSIndexFactory();
            }
        }
        return cacheManager;
    }

    CtVFSIndexFactory() {
        this.configFactory = IndexConfigFactroy.getInstance();
    }


    public ListResultModel<List<FileIndex>> search(Condition<FileIndexEnmu, FileIndex> condition) {

        return getService().search(condition);
    }


    public void addIndex(JLuceneIndex index) {
        try {
            getService().addIndex(configFactory.getJLuceneConfig(index));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    public VFSIndexService getService() {
        VFSIndexService service = EsbUtil.parExpression( VFSIndexService.class);
        return service;
    }
}


