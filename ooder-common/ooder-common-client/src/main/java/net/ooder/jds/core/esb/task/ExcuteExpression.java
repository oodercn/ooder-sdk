/**
 * $RCSfile: ExcuteExpression.java,v $
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
package net.ooder.jds.core.esb.task;

import net.ooder.context.JDSActionContext;
import net.ooder.context.JDSContext;
import net.ooder.context.RunableActionContextImpl;
import net.ooder.jds.core.esb.EsbUtil;

import java.util.concurrent.Callable;

public class ExcuteExpression<T extends ExcuteObj> implements Callable<T> {

    private final ExcuteObj obj;
    private RunableActionContextImpl autoruncontext;

    public ExcuteExpression(ExcuteObj obj) {
        JDSContext context = JDSActionContext.getActionContext();
        this.autoruncontext = new RunableActionContextImpl();
        autoruncontext.setParamMap(context.getContext());
        this.obj = obj;
        if (context.getSessionId() != null) {
            autoruncontext.setSessionId(context.getSessionId());
            autoruncontext.getSession().put("sessionHandle", context.getSession().get("sessionHandle"));
        }
        autoruncontext.setSessionMap(context.getSession());

    }

    @Override
    public T call() throws Exception {
        JDSActionContext.setContext(autoruncontext);
        Object valueObj = EsbUtil.parExpression(obj.getExpression(), obj.getClazz());
        obj.setObj(valueObj);
        return (T) obj;

    }
}