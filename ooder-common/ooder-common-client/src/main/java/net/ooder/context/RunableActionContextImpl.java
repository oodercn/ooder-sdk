/**
 * $RCSfile: RunableActionContextImpl.java,v $
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
import net.ooder.esb.util.EsbFactory;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.jds.core.esb.util.ActionContext;
import net.ooder.jds.core.esb.util.JDSConverter;
import net.ooder.jds.core.esb.util.OgnlValueStack;
import net.ooder.jds.core.esb.util.ValueStack;
import net.ooder.server.JDSServer;
import ognl.OgnlContext;
import ognl.OgnlRuntime;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class RunableActionContextImpl implements JDSContext {

    Map paramMap = new HashMap();
    Map sessionMap = new HashMap();
    String ipAddr = null;
    String sessionId = null;
    Map contextMap = new HashMap();
    private Object request;
    private Object response;
    private Object handle;
    public OgnlContext ognlContext;


    public Object Par(String expression) {
        ValueStack strack = ActionContext.getContext().getValueStack();
        strack.getRoot().push(contextMap);
        Object obj = strack.findValue(expression);
        strack.getRoot().remove(contextMap);
        return obj;
    }

    Map pagectxMap = new HashMap();

    @Override
    public Map getPagectx() {
        return pagectxMap;
    }

    @Override
    public void setPagectx(Map map) {
        this.pagectxMap = map;
    }



    @Override
    public OgnlContext getOgnlContext() {

        return ognlContext;
    }

    @Override
    public void setOgnlContext(OgnlContext ognlContext) {
        this.ognlContext = ognlContext;
    }

    @Override
    public <T> T Par(Class<T> clazz) {
        return EsbUtil.parExpression(clazz);
    }


    public Map getContext() {
        return contextMap;
    }


    @Override
    public Object getParams(String param) {
        //  String value = null;
        Object obj = paramMap.get(param);
        if (obj == null) {
            obj = sessionMap.get(param);
        }
        if (obj == null) {
            obj = contextMap.get(param);
        }

        if (obj == null) {
            obj = getSession().get(param);
        }
        if (obj == null) {
            obj = ActionContext.getContext().getContextMap().get(param);
        }

        if (obj != null) {
            if (obj.getClass().isArray()) {
                Object[] objs = (Object[]) obj;
                if (objs.length > 0 && objs[0] != null) {
                    obj = objs[0].toString();
                }

            }
        }
        return obj;
    }

    @Override
    public String getIpAddr() {
        return ipAddr;
    }

    @Override
    public Map getSession() {
        return sessionMap;
    }

    @Override
    public String getSessionId() {

        if (sessionId == null) {
            sessionId = (String) getSession().get(this.JSESSIONID);
        }
        if (sessionId == null) {
            sessionId = (String) getParams(this.JSESSIONID);
        }

        return sessionId;
    }

    @Override
    public Object Par(String expression, Class clazz) {
        return Par(expression, null, null);

    }

    @Override
    public <T> T Par(final String expression, final Class<T> clazz, final Object source) {

        final T o = EsbFactory.par(expression, JDSActionContext.getActionContext().getContext(), source, clazz);

        return o;
    }


    public void setRequest(Object request) {
        this.request = request;
    }

    public void setHandle(Object handle) {
        this.handle = handle;
    }

    public Map getPagectxMap() {
        return pagectxMap;
    }

    public void setPagectxMap(Map pagectxMap) {
        this.pagectxMap = pagectxMap;
    }


    @Override
    public Object getHandle() {
        return handle;
    }

    public Map getContextMap() {
        return contextMap;
    }

    public void setContextMap(Map contextMap) {
        this.contextMap = contextMap;
    }

    public Map getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map paramMap) {
        this.paramMap = paramMap;
    }

    public Map getSessionMap() {
        return sessionMap;
    }

    public void setSessionMap(Map sessionMap) {
        this.sessionMap = sessionMap;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }


    @Override
    public void remove() {
        paramMap.clear();
        sessionMap.clear();
        contextMap.clear();
    }


    public String getSystemCode() {
        String syscode = (String) this.getSession().get(SYSCODE);
        if (syscode == null) {
            syscode = (String) this.getParams(SYSCODE);
            this.getSession().put(SYSCODE, syscode);
        }
        return syscode;
    }

    @Override
    public ConfigCode getConfigCode() {

        String syscode = getSystemCode();
        ConfigCode configCode = ConfigCode.fromType(syscode);
        if (configCode == null) {
            configCode = JDSServer.getClusterClient().getSystem(syscode).getConfigname();
        }

        return configCode;
    }

    public Object getHttpResponse() {
        return response;
    }

    public void setHttpResponse(Object response) {
        this.response = response;
    }

    public void setHttpRequest(Object request) {
        this.request = request;
    }

    @Override
    public Object getHttpRequest() {
        return request;
    }

    public String getHost() {
        return null;
    }


}
