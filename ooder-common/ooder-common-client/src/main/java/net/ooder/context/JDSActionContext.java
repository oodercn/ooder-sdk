/**
 * $RCSfile: JDSActionContext.java,v $
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
package net.ooder.context;

import net.ooder.common.ConfigCode;
import net.ooder.common.JDSException;
import net.ooder.common.util.ClassUtility;
import net.ooder.esb.config.manager.EsbBeanFactory;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.jds.core.esb.util.ActionContext;
import net.ooder.server.JDSServer;
import ognl.OgnlContext;

import java.util.HashMap;
import java.util.Map;


public abstract class
JDSActionContext implements JDSContext {
    static ThreadLocal jdsactionContext = new JDSActionContextLocal();

    private Object handle;

    public OgnlContext ognlContext;

    Map pagectxMap = new HashMap();

    public static JDSContext getActionContext() {
        JDSContext context = (JDSContext) jdsactionContext.get();
        if (context == null) {
            context = new RunableActionContextImpl();
            jdsactionContext.set(context);
        }
        return context;
    }





    @Override
    public void setOgnlContext(OgnlContext ognlContext) {
        this.ognlContext = ognlContext;
    }

    @Override
    public Map getPagectx() {
        return pagectxMap;
    }

    @Override
    public void setPagectx(Map map) {
        this.pagectxMap = map;
    }

    /**
     * Sets the action context for the current thread.
     *
     * @param context the action context.
     */
    public static void setContext(JDSContext context) {
        jdsactionContext.set(context);
    }

    @Override
    public void remove() {
        ActionContext.getContext().remove();
        jdsactionContext.remove();
    }

    public <T> T Par(Class<T> clazz) {
        return EsbUtil.parExpression(clazz);
    }

    private static class JDSActionContextLocal extends ThreadLocal {

        protected Object initialValue() {
            JDSActionContext context = null;
            EsbBeanFactory factory = EsbBeanFactory.getInstance();
            String clazz = factory.getEsbBeanConfig().getActionContextClass();
            if (clazz == null) {
                clazz = "net.ooder.server.context.SpringMvcContextImpl";
            }
            try {
                context = (JDSActionContext) ClassUtility.loadClass(clazz).newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return context;
        }
    }

    public ConfigCode getConfigCode() {

        String syscode = (String) getSystemCode();

        ConfigCode configCode = ConfigCode.fromType(syscode);
        if (configCode == null && JDSServer.getClusterClient().getSystem(syscode) != null) {
            configCode = JDSServer.getClusterClient().getSystem(syscode).getConfigname();
        } else {
            try {
                configCode = JDSServer.getInstance().getCurrServerBean().getConfigCode();
            } catch (JDSException e) {
                e.printStackTrace();
            }
        }


        return configCode;
    }


    public String getSystemCode() {

        String syscode = (String) this.getParams(SYSCODE);

        if (syscode == null) {
            try {
                syscode = JDSServer.getInstance().getCurrServerBean().getId();
            } catch (JDSException e) {
                e.printStackTrace();
            }
        }
        this.getContext().put(SYSCODE, syscode);
        return syscode;
    }

    @Override
    public Object getHandle() {
        return handle;
    }

    public void setHandle(Object handle) {
        this.handle = handle;
    }


}
