
/**
 * $RCSfile: OgnlValueStack.java,v $
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
package net.ooder.jds.core.esb.util;



import ognl.ObjectPropertyAccessor;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.io.Serializable;
import java.util.*;


public class OgnlValueStack implements ValueStack, Serializable {
	
	private static final long serialVersionUID = 370737852934925530L;
	
	private static CompoundRootAccessor accessor;
    private static Log LOG = LogFactory.getLog(OgnlValueStack.class);
    private static boolean devMode;

    static {
        reset();
    }

    public static void reset() {
        accessor = new CompoundRootAccessor();
        OgnlRuntime.setPropertyAccessor(CompoundRoot.class, accessor);
        OgnlRuntime.setPropertyAccessor(Object.class, new ObjectAccessor());
        OgnlRuntime.setPropertyAccessor(Iterator.class, new JDSIteratorPropertyAccessor());
        OgnlRuntime.setPropertyAccessor(Enumeration.class, new JDSEnumerationAcccessor());
        OgnlRuntime.setPropertyAccessor(List.class, new JDSListPropertyAccessor());
        OgnlRuntime.setPropertyAccessor(Map.class, new JDSMapPropertyAccessor());
        OgnlRuntime.setPropertyAccessor(Collection.class, new JDSCollectionPropertyAccessor());
        OgnlRuntime.setPropertyAccessor(Set.class, new JDSCollectionPropertyAccessor());
        OgnlRuntime.setPropertyAccessor(ObjectProxy.class, new ObjectProxyPropertyAccessor());
        OgnlRuntime.setMethodAccessor(Object.class, new JDSMethodAccessor());
        OgnlRuntime.setMethodAccessor(CompoundRoot.class, accessor);
        OgnlRuntime.setNullHandler(Object.class, new InstantiatingNullHandler());
    }

    public static class ObjectAccessor extends ObjectPropertyAccessor {
        public Object getProperty(Map map, Object o, Object o1) throws OgnlException {
            Object obj = super.getProperty(map, o, o1);
            link(map, o.getClass(), (String) o1);

            map.put(JDSConverter.LAST_BEAN_CLASS_ACCESSED, o.getClass());
            map.put(JDSConverter.LAST_BEAN_PROPERTY_ACCESSED, o1.toString());
            OgnlContextState.updateCurrentPropertyPath(map, o1);
            return obj;
        }

        public void setProperty(Map map, Object o, Object o1, Object o2) throws OgnlException {
            super.setProperty(map, o, o1, o2);
        }
    }

    public static void link(Map context, Class clazz, String name) {
        context.put("__link", new Object[]{clazz, name});
    }


    CompoundRoot root;
    transient Map context;
    Class defaultType;
    Map overrides;


    public OgnlValueStack() {
        setRoot(new CompoundRoot());
       
    }
    public OgnlValueStack(CompoundRoot root) {
        setRoot(root);
       
    }

    public OgnlValueStack(ValueStack vs) {
        setRoot(new CompoundRoot(vs.getRoot()));
    }


    public static CompoundRootAccessor getAccessor() {
        return accessor;
    }
    
   
    public static void setDevMode(String mode) {
        devMode = "true".equals(mode);
    }

    /* (non-Javadoc)
     * @see net.ooder.jds.core.esb.util.ValueStack#getContext()
     */
    public Map getContext() {
        return context;
    }

    /* (non-Javadoc)
     * @see net.ooder.jds.core.esb.util.ValueStack#setDefaultType(java.lang.Class)
     */
    public void setDefaultType(Class defaultType) {
        this.defaultType = defaultType;
    }

    /* (non-Javadoc)
     * @see net.ooder.jds.core.esb.util.ValueStack#setExprOverrides(java.util.Map)
     */
    public void setExprOverrides(Map overrides) {
    	if (this.overrides == null) {
    		this.overrides = overrides;
    	}
    	else {
    		this.overrides.putAll(overrides);
    	}
    }
    
    /* (non-Javadoc)
     * @see net.ooder.jds.core.esb.util.ValueStack#getExprOverrides()
     */
    public Map getExprOverrides() {
    	return this.overrides;
    }

    /* (non-Javadoc)
     * @see net.ooder.jds.core.esb.util.ValueStack#getRoot()
     */
    public CompoundRoot getRoot() {
        return root;
    }

    /* (non-Javadoc)
     * @see net.ooder.jds.core.esb.util.ValueStack#setValue(java.lang.String, java.lang.Object)
     */
    public void setValue(String expr, Object value) {
        setValue(expr, value, devMode);
    }

    /* (non-Javadoc)
     * @see net.ooder.jds.core.esb.util.ValueStack#setValue(java.lang.String, java.lang.Object, boolean)
     */
    public void setValue(String expr, Object value, boolean throwExceptionOnFailure) {
        Map context = getContext();

        try {
            context.put(JDSConverter.CONVERSION_PROPERTY_FULLNAME, expr);
            context.put(REPORT_ERRORS_ON_NO_PROP, (throwExceptionOnFailure) ? Boolean.TRUE : Boolean.FALSE);
            OgnlUtil.setValue(expr, context, root, value);
        } catch (OgnlException e) {
            if (throwExceptionOnFailure) {
                String msg = "Error setting expression '" + expr + "' with value '" + value + "'";
                try {
					throw new OgnlException(msg, e);
				} catch (OgnlException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Error setting value", e);
                }
            }
        } catch (RuntimeException re) { //XW-281
            if (throwExceptionOnFailure) {
                String msg = "Error setting expression '" + expr + "' with value '" + value + "'";
                try {
					throw new OgnlException(msg, re);
				} catch (OgnlException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Error setting value", re);
                }
            }
        } finally {
            OgnlContextState.clear(context);
            context.remove(JDSConverter.CONVERSION_PROPERTY_FULLNAME);
            context.remove(REPORT_ERRORS_ON_NO_PROP);
        }
    }

    /* (non-Javadoc)
     * @see net.ooder.jds.core.esb.util.ValueStack#findString(java.lang.String)
     */
    public String findString(String expr) {
        return (String) findValue(expr, String.class);
    }

    /* (non-Javadoc)
     * @see net.ooder.jds.core.esb.util.ValueStack#findValue(java.lang.String)
     */
    public Object findValue(String expr) {
        try {
            if (expr == null) {
                return null;
            }

            if ((overrides != null) && overrides.containsKey(expr)) {
                expr = (String) overrides.get(expr);
            }

            if (defaultType != null) {
                return findValue(expr, defaultType);
            }

            Object value = OgnlUtil.getValue(expr, context, root);
            if (value != null) {
                return value;
            } else {
                return findInContext(expr);
            }
        } catch (OgnlException e) {
            return findInContext(expr);
        } catch (Exception e) {
            logLookupFailure(expr, e);
            return findInContext(expr);
        } finally {
            OgnlContextState.clear(context);
        }
    }

    /* (non-Javadoc)
     * @see net.ooder.jds.core.esb.util.ValueStack#findValue(java.lang.String, java.lang.Class)
     */
    public Object findValue(String expr, Class asType) {
        try {
            if (expr == null) {
                return null;
            }

            if ((overrides != null) && overrides.containsKey(expr)) {
                expr = (String) overrides.get(expr);
            }

            Object value = OgnlUtil.getValue(expr, context, root, asType);
            if (value != null) {
                return value;
            } else {
                return findInContext(expr);
            }
        } catch (OgnlException e) {
            return findInContext(expr);
        } catch (Exception e) {
            logLookupFailure(expr, e);

            return findInContext(expr);
        } finally {
            OgnlContextState.clear(context);
        }
    }

    private Object findInContext(String name) {
        return getContext().get(name);
    }

    /**
     * Log a failed lookup, being more verbose when devMode=true.
     *
     * @param expr The failed expression
     * @param e    The thrown exception.
     */
    private void logLookupFailure(String expr, Exception e) {
        StringBuffer msg = new StringBuffer();
        msg.append("Caught an exception while evaluating expression '").append(expr).append("' against value stack");
        if (devMode && LOG.isWarnEnabled()) {
            LOG.warn(msg, e);
            LOG.warn("NOTE: Previous warning message was issued due to devMode set to true.");
        } else if (LOG.isDebugEnabled()) {
            LOG.debug(msg, e);
        }
    }

    /* (non-Javadoc)
     * @see net.ooder.jds.core.esb.util.ValueStack#peek()
     */
    public Object peek() {
        return root.peek();
    }

    /* (non-Javadoc)
     * @see net.ooder.jds.core.esb.util.ValueStack#pop()
     */
    public Object pop() {
        return root.pop();
    }

    /* (non-Javadoc)
     * @see net.ooder.jds.core.esb.util.ValueStack#push(java.lang.Object)
     */
    public void push(Object o) {
        root.push(o);
    }
    /* (non-Javadoc)
     * @see net.ooder.jds.core.esb.util.ValueStack#set(java.lang.String, java.lang.Object)
     */
    public void set(String key, Object o) {
    	//set basically is backed by a Map
    	//pushed on the stack with a key 
    	//being put on the map and the 
    	//Object being the value
    	
    	Map setMap=null;
    	
    	//check if this is a Map 
    	//put on the stack  for setting
    	//if so just use the old map (reduces waste)
    	Object topObj=peek();
    	if (topObj instanceof Map 
    			&&((Map)topObj).get(MAP_IDENTIFIER_KEY)!=null) {
    		
    		setMap=(Map)topObj;
    	}	else {
    		setMap=new HashMap();
    		//the map identifier key ensures
    		//that this map was put there
    		//for set purposes and not by a user
    		//whose data we don't want to touch
    		setMap.put(MAP_IDENTIFIER_KEY,"");
    		push(setMap);
    	}
    	setMap.put(key,o);
    	
    }
    
    
    private static final String MAP_IDENTIFIER_KEY="net.ooder.jds.core.esb.util.OgnlValueStack.MAP_IDENTIFIER_KEY";
    
    /* (non-Javadoc)
     * @see net.ooder.jds.core.esb.util.ValueStack#size()
     */
    public int size() {
        return root.size();
    }

    private void setRoot(CompoundRoot compoundRoot) {
        this.root = compoundRoot;
        this.context = Ognl.createDefaultContext(this.root, accessor, JDSConverter.getInstance());
        context.put(VALUE_STACK, this);
        Ognl.setClassResolver(context, accessor);
        ((OgnlContext) context).setTraceEvaluations(false);
        ((OgnlContext) context).setKeepLastEvaluation(false);
    }

    private Object readResolve() {
        OgnlValueStack aStack = new OgnlValueStack();
        aStack.setRoot(this.root);

        return aStack;
    }
}
