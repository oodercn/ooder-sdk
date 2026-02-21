package net.ooder.common;


import java.util.Map;

public interface StaticBean {

    public Object getSource();

    public Object setSource(Object source);

    public Object getRequest();

    public Map getCtx();
}
