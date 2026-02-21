package net.ooder.server.context;

import net.ooder.common.JDSException;
import net.ooder.context.JDSActionContext;
import net.ooder.esb.util.EsbFactory;
import net.ooder.jds.core.esb.util.ActionContext;
import net.ooder.jds.core.esb.util.JDSConverter;
import net.ooder.jds.core.esb.util.OgnlValueStack;
import net.ooder.server.JDSServer;
import ognl.OgnlContext;
import ognl.OgnlRuntime;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class SpringMvcContextImpl extends JDSActionContext {

    Map contextMap = new HashMap();

    public SpringMvcContextImpl() {

    }


    @Override
    public OgnlContext getOgnlContext() {
        HttpServletRequest request = (HttpServletRequest) this.getHttpRequest();
        if (ognlContext == null && request != null) {
            Map<String, Object> objectMap = this.getContext();
            OgnlRuntime.clearCache();
            ognlContext = new OgnlContext(OgnlValueStack.getAccessor(), JDSConverter.getInstance(), null, objectMap);

        }
        return ognlContext;
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

    @Override
    public Map getContext() {
        if (RequestContextHolder.getRequestAttributes() != null) {
            final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes()).getRequest();
            Map<String, String[]> httpVariableMap = request.getParameterMap();
            if (httpVariableMap != null) {
                Set<String> keSet = httpVariableMap.keySet();
                for (String key : keSet) {
                    String[] objects = httpVariableMap.get(key);
                    if (objects != null) {
                        if (objects.length == 1) {
                            contextMap.put(key, objects[0]);
                        } else {
                            contextMap.put(key, objects);
                        }
                    }
                }
            }
            if (this.getSession() != null) {
                contextMap.putAll(this.getSession());
            }
        }
        return contextMap;
    }

    @Override
    public Object getParams(final String param) {
        String value = null;
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            final HttpServletRequest request = requestAttributes.getRequest();
            if (request != null) {
                value = (String) request.getAttribute(param);
                if (value == null) {
                    value = request.getParameter(param);
                }
                if (value == null || value.equals("")) {
                    final Object obj = getContext().get(param);
                    if (obj != null) {
                        if (obj.getClass().isArray()) {
                            final Object[] objs = (Object[]) getContext().get(param);
                            if (objs.length > 0 && objs[0] != null) {
                                value = objs[0].toString();
                            }
                        } else {
                            value = obj.toString();
                        }
                    }
                }
                if (value == null) {
                    value = (String) this.getSession().get(param);
                }

            }
        }

        return value;
    }


    @Override
    public String getIpAddr() {
        String ip = null;
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (requestAttributes != null) {
            final HttpServletRequest request = requestAttributes.getRequest();
            ip = request.getHeader("X-Forwarded-For");

            final String gatewayip = request.getHeader("gateway-ip");
            if ((gatewayip != null) && (!gatewayip.equals(""))) {
                ip = gatewayip;
            }

            if ((ip == null) || (ip.length() == 0)
                    || ("unknown".equalsIgnoreCase(ip))) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if ((ip == null) || (ip.length() == 0)
                    || ("unknown".equalsIgnoreCase(ip))) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if ((ip == null) || (ip.length() == 0)
                    || ("unknown".equalsIgnoreCase(ip))) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if ((ip == null) || (ip.length() == 0)
                    || ("unknown".equalsIgnoreCase(ip))) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if ((ip == null) || (ip.length() == 0)
                    || ("unknown".equalsIgnoreCase(ip))) {
                ip = request.getRemoteAddr();
            }
        }
        return ip;
    }

    @Override
    public Map getSession() {
        Map sessionMap = new HashMap();
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            final HttpServletRequest request = requestAttributes.getRequest();
            if (request != null) {
                final HttpSession session = request.getSession(true);
                sessionMap = ((Map) session.getAttribute("sessionMap"));
                if (sessionMap == null) {
                    sessionMap = new HashMap();
                    session.putValue("sessionMap", sessionMap);
                }
            }
        }

        return sessionMap;
    }

    @Override
    public String getSessionId() {
        String sessionid = null;
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            final HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            sessionid = (String) getParams(JDSActionContext.JSESSIONID);

            final String rSessionId = (String) getParams("RSESSIONID");
            if ((rSessionId != null) && (!rSessionId.equals(""))) {
                sessionid = rSessionId;
            }
            if ((sessionid == null) || (sessionid.equals("") || sessionid.equals("\"\""))) {
                sessionid = request.getSession(true).getId();
                //	sessionid = UUID.randomUUID().toString().toUpperCase();
                getSession().put(JDSActionContext.JSESSIONID, sessionid);
                request.getSession().setAttribute(JDSActionContext.JSESSIONID, sessionid);
            }
        }
        return sessionid;

    }

    @Override
    public <T> T Par(final String expression, final Class<T> clazz) {
        return Par(expression, clazz, null);
    }

    @Override
    public <T> T Par(Class<T> clazz) {
        final T o = EsbFactory.par(clazz);
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            final HttpServletRequest request = requestAttributes.getRequest();
            WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());
            webApplicationContext.getAutowireCapableBeanFactory().autowireBean(o);
        }
        return o;
    }

    @Override
    public <T> T Par(final String expression, final Class<T> clazz, final Object source) {
        final T o = EsbFactory.par(expression, ActionContext.getContext().getContextMap(), source, clazz);

        if (o != null) {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
                WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());
                webApplicationContext.getAutowireCapableBeanFactory().autowireBean(o);
            }


        }

        return o;
    }


    @Override
    public Object getHttpRequest() {
        HttpServletRequest request = null;
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            request = requestAttributes.getRequest();
        }

        return request;
    }

    @Override
    public Object getHttpResponse() {
        HttpServletResponse response = null;
        if (RequestContextHolder.getRequestAttributes() != null) {
            response = ((ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes()).getResponse();
        }
        return response;
    }


    @Override
    public String getHost() {
        try {
            return JDSServer.getInstance().getCurrServerBean().getUrl();
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return null;
    }


}
