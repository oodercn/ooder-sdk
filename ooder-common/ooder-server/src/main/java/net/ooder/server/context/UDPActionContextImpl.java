package net.ooder.server.context;

import net.ooder.common.ConfigCode;
import net.ooder.context.JDSUDPContext;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.jds.core.esb.util.ActionContext;
import net.ooder.server.JDSClientService;
import ognl.OgnlContext;

import java.util.HashMap;
import java.util.Map;


public class UDPActionContextImpl implements JDSUDPContext {

    Map paramMap = new HashMap();
    Map sessionMap = new HashMap();
    String ipAddr = null;
    String sessionId = null;
    Map contextMap = new HashMap();
    private Integer port;
    private ConfigCode configCode;
    private String systemCode;
    private Object request;
    private Object response;
    private Object handle;
    public OgnlContext ognlContext;
    public UDPActionContextImpl(String ip, Integer port, String systemCode, ConfigCode configCode) {
        this.ipAddr = ip;
        this.port = port;
        this.systemCode = systemCode;
        this.configCode = configCode;
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


    public Object Par(String expression) {
        return EsbUtil.parExpression(expression);
    }

    @Override
    public <T> T Par(Class<T> clazz) {
        return EsbUtil.parExpression(clazz);
    }


    public JDSClientService getClient() {
        // TODO Auto-generated method stub
        return null;
    }

    public Map getContext() {
        return contextMap;
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
    public OgnlContext getOgnlContext() {
        return ognlContext;
    }

    @Override
    public void setOgnlContext(OgnlContext ognlContext) {
        this.ognlContext = ognlContext;
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
        return sessionId;
    }

    @Override
    public Object Par(String expression, Class clazz) {
        return EsbUtil.parExpression(clazz);
    }

    @Override
    public Object Par(String expression, Class clazz, Object source) {
        return EsbUtil.parExpression(clazz);
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


    public String getSystemCode() {
        if (systemCode == null) {
            systemCode = (String) this.getSession().get(SYSCODE);
            if (systemCode == null) {
                systemCode = (String) this.getParams(SYSCODE);
                this.getSession().put(SYSCODE, systemCode);
            }
        }

        return systemCode;
    }

    @Override
    public ConfigCode getConfigCode() {
        return configCode;
    }

    @Override
    public void remove() {
        paramMap.clear();
        sessionMap.clear();
        contextMap.clear();
    }


    public Integer getPort() {
        return port;
    }

    public String getHost() {

        return null;
    }


}
