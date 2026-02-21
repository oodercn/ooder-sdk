/**
 * $RCSfile: JDSContext.java,v $
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
import ognl.OgnlContext;

import java.util.Map;

public interface JDSContext {


    public static final String SYSCODE = "SYSID";

    public static final String JSESSIONID = "JSESSIONID";

    public final static String JDSUSERID = "JDSUSERID";

    public Map getContext();

    public OgnlContext getOgnlContext();

    public void setOgnlContext(OgnlContext ognlContext);

    public Map getPagectx();

    public void setPagectx(Map<String, Object> map);

    public Object getHttpRequest();

    public Object getHttpResponse();

    public Object getHandle();

    public Object getParams(String param);

    public Map getSession();

    public String getSystemCode();

    public ConfigCode getConfigCode();

    public void remove();

    public String getIpAddr();

    public String getSessionId();

    public String getHost();

    public Object Par(String expression);

    public <T> T Par(String expression, Class<T> clazz, Object source);

    public <T> T Par(String expression, Class<T> clazz);

    public <T> T Par(Class<T> clazz);
}
