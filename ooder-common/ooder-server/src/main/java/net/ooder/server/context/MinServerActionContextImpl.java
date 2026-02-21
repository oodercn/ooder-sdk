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

public class MinServerActionContextImpl<T> extends JDSActionContext {

    private Object request;
    private Object response;
    private Object handle;
    Map paramMap = new HashMap();

    Map sessionMap = new HashMap();
    String ipAddr = null;
    String sessionId = null;
    public OgnlContext ognlContext;

    Map contextMap = new HashMap();

    public MinServerActionContextImpl() {
        // this.request = JDSActionContext.getActionContext().getHttpRequest();
    }

    public MinServerActionContextImpl(Object request, OgnlContext ognlContext) {

        this.request = request;
        this.ognlContext = ognlContext;
    }

    @Override
    public OgnlContext getOgnlContext() {
        return ognlContext;
    }

    @Override
    public void setOgnlContext(OgnlContext ognlContext) {
        this.ognlContext = ognlContext;
    }

    public Object Par(String expression) {
        return Par(expression, null);
    }

    @Override
    public <T> T Par(Class<T> clazz) {
        return EsbUtil.parExpression(clazz);
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
        if (obj == null) {
            if (request != null && request instanceof HttpRequest) {
                obj = ((HttpRequest) request).getParameter(param);
            }
        }

//        if (obj == null) {
//            obj = OgnlValueStackFactory.getFactory().createValueStack().findString(param);
//        }

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
    public Object Par(String expression, Class clazz) {
        return Par(expression, null, null);

    }


    @Override
    public <T> T Par(final String expression, final Class<T> clazz, final Object source) {

        final T o = EsbFactory.par(expression, JDSActionContext.getActionContext().getContext(), source, clazz);
//
//        ActionContext.getContext().getValueStack().getRoot().push(getContext());
//
//        if (source != null) {
//            ActionContext.getContext().getValueStack().getRoot().push(source);
//        }
//        Object  o= ActionContext.getContext().getValueStack().findValue(expression, clazz);
//
//        if (source != null) {
//            ActionContext.getContext().getValueStack().getRoot().remove(source);
//        }
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

    @Override
    public Object getHandle() {
        return handle;
    }


    public void setHandle(Object handle) {
        this.handle = handle;
    }
}
