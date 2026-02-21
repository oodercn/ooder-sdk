package net.ooder.server.context;

import net.ooder.context.JDSActionContext;
import net.ooder.esb.util.EsbFactory;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.jds.core.esb.util.ActionContext;
import net.ooder.server.httpproxy.core.HttpRequest;
import ognl.OgnlContext;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class LocalServerActionContextImpl<T> extends JDSActionContext {

    Map paramMap = new HashMap();
    Map sessionMap = new HashMap();
    String ipAddr = null;
    String sessionId = null;
    Map contextMap = new HashMap();
    private Object request;
    private Object response;

    public LocalServerActionContextImpl() {

    }


    public Map getContextMap() {
        return contextMap;
    }

    public void setContextMap(Map contextMap) {
        this.contextMap = contextMap;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public Map getContext() {

        return paramMap;
    }

    @Override
    public OgnlContext getOgnlContext() {
        HttpRequest request= (HttpRequest) this.getHttpRequest();
        return request.getOgnlContext();
    }

    @Override
    public Object getParams(String param) {
        String value = null;
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

        if (obj == null) {
            ActionContext.getContext().getValueStack().getRoot().push(paramMap);
            obj = ActionContext.getContext().getValueStack().findString(param);
            ActionContext.getContext().getValueStack().getRoot().remove(paramMap);
        }


        if (obj != null) {
            if (obj.getClass().isArray()) {
                Object[] objs = (Object[]) obj;
                if (objs.length > 0 && objs[0] != null) {
                    value = objs[0].toString();
                }

            } else {
                value = obj.toString();
            }
        }
        return value;
    }

    @Override
    public String getIpAddr() {
        if (this.ipAddr == null) {

            try {
                ipAddr = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        return ipAddr;
    }

    @Override
    public Map getSession() {
        Map session = ActionContext.getContext().getSession();
        if (session == null) {
            session = sessionMap;
            ActionContext.getContext().setSession(sessionMap);
        }
        return session;
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
    public <T> T Par(Class<T> clazz) {
        return EsbUtil.parExpression(clazz);
    }

    @Override
    public <T> T Par(final String expression, final Class<T> clazz) {
        return Par(expression, clazz, null);
    }

    @Override
    public <T> T Par(final String expression, final Class<T> clazz, final Object source) {
        final T o = EsbFactory.par(expression, ActionContext.getContext().getContextMap(), source, clazz);
        return o;
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
        getSession().putAll(sessionMap);
        this.sessionMap = sessionMap;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getHost() {

        return null;
    }


    @Override
    public Object Par(final String expression) {
        Object obj = null;
        try {
            obj = Par(expression, null);
        } catch (final Exception e) {

        }
        return obj;
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

}
