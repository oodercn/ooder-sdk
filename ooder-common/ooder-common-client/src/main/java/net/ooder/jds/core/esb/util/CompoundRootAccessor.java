/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
/**
 * $RCSfile: CompoundRootAccessor.java,v $
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


import net.ooder.common.util.ClassUtility;
import ognl.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.*;


/**
 * A stack that is able to call methods on objects in the stack.
 *
 * @author $Author: Administrator $
 * @author Rainer Hermanns
 * @version $Revision: 1.1 $
 */
public class CompoundRootAccessor implements PropertyAccessor, MethodAccessor, ClassResolver {

    private final static Log log = LogFactory.getLog(CompoundRootAccessor.class);
    private static Map invalidMethods = new HashMap();


    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        CompoundRoot root = (CompoundRoot) target;
        OgnlContext ognlContext = (OgnlContext) context;

        for (Iterator iterator = root.iterator(); iterator.hasNext(); ) {
            Object o = iterator.next();

            if (o == null) {
                continue;
            }

            try {
                if (OgnlRuntime.hasSetProperty(ognlContext, o, name)) {
                    OgnlRuntime.setProperty(ognlContext, o, name, value);

                    return;
                } else if (o instanceof Map) {
                    Map map = (Map) o;
                    map.put(name, value);
                    return;
                }

            } catch (IntrospectionException e) {
                // this is OK if this happens, we'll just keep trying the next
            }
        }

        Boolean reportError = (Boolean) context.get(OgnlValueStack.REPORT_ERRORS_ON_NO_PROP);

        final String msg = "No object in the CompoundRoot has a publicly accessible property named '" + name + "' (no setter could be found).";

        if ((reportError != null) && (reportError.booleanValue())) {
            log.error(msg);
            throw new OgnlException(msg);
        } else {
            log.debug(msg);
        }
    }

    public Object getProperty(Map context, Object target, Object name) throws OgnlException {
        CompoundRoot root = (CompoundRoot) target;
        OgnlContext ognlContext = (OgnlContext) context;

        if (name instanceof Integer) {
            Integer index = (Integer) name;

            return root.cutStack(index.intValue());
        } else if (name instanceof String) {
            if ("top".equals(name)) {
                if (root.size() > 0) {
                    return root.get(0);
                } else {
                    return null;
                }
            }

            for (Iterator iterator = root.iterator(); iterator.hasNext(); ) {
                Object o = iterator.next();

                if (o == null) {
                    continue;
                }

                try {
                    if ((OgnlRuntime.hasGetProperty(ognlContext, o, name)) || ((o instanceof Map) && ((Map) o).containsKey(name))) {
                        return OgnlRuntime.getProperty(ognlContext, o, name);
                    }
                } catch (OgnlException e) {
                    if (e.getReason() != null) {
                        final String msg = "Caught an Ognl exception while getting property " + name;
                        log.error(msg, e);
                        throw new OgnlException(msg, e);
                    }
                } catch (IntrospectionException e) {
                    // this is OK if this happens, we'll just keep trying the next
                }
            }

            return null;
        } else {
            return null;
        }
    }

    public Object callMethod(Map context, Object target, String name, Object[] objects) throws MethodFailedException {
        CompoundRoot root = (CompoundRoot) target;

        if ("describe".equals(name)) {
            Object v;
            if (objects != null && objects.length == 1) {
                v = objects[0];
            } else {
                v = root.get(0);
            }


            if (v instanceof Collection || v instanceof Map || v.getClass().isArray()) {
                return v.toString();
            }

            try {
                Map descriptors = OgnlRuntime.getPropertyDescriptors(v.getClass());

                int maxSize = 0;
                for (Iterator iterator = descriptors.keySet().iterator(); iterator.hasNext(); ) {
                    String pdName = (String) iterator.next();
                    if (pdName.length() > maxSize) {
                        maxSize = pdName.length();
                    }
                }

                SortedSet set = new TreeSet();
                StringBuffer sb = new StringBuffer();
                for (Iterator iterator = descriptors.values().iterator(); iterator.hasNext(); ) {
                    PropertyDescriptor pd = (PropertyDescriptor) iterator.next();

                    sb.append(pd.getName()).append(": ");
                    int padding = maxSize - pd.getName().length();
                    for (int i = 0; i < padding; i++) {
                        sb.append(" ");
                    }
                    sb.append(pd.getPropertyType().getName());
                    set.add(sb.toString());

                    sb = new StringBuffer();
                }

                sb = new StringBuffer();
                for (Iterator iterator = set.iterator(); iterator.hasNext(); ) {
                    String s = (String) iterator.next();
                    sb.append(s).append("\n");
                }

                return sb.toString();
            } catch (IntrospectionException e) {
                e.printStackTrace();
            } catch (OgnlException e) {
                e.printStackTrace();
            }

            return null;
        }

        for (Iterator iterator = root.iterator(); iterator.hasNext(); ) {
            Object o = iterator.next();

            if (o == null) {
                continue;
            }

            Class clazz = o.getClass();
            Class[] argTypes = getArgTypes(objects);

            MethodCall mc = null;

            if (argTypes != null) {
                mc = new MethodCall(clazz, name, argTypes);
            }

            if ((argTypes == null) || !invalidMethods.containsKey(mc)) {
                try {
                    Object value = OgnlRuntime.callMethod((OgnlContext) context, o, name, name, objects);

                    if (value != null) {
                        return value;
                    }
                } catch (OgnlException e) {
                    // try the next one
                    Throwable reason = e.getReason();

                    if ((mc != null) && (reason != null) && (reason.getClass() == NoSuchMethodException.class)) {
                        invalidMethods.put(mc, Boolean.TRUE);
                    } else if (reason != null) {
                        throw new MethodFailedException(o, name, e.getReason());
                    }
                }
            }
        }

        return null;
    }

    public Object callStaticMethod(Map transientVars, Class aClass, String s, Object[] objects) throws MethodFailedException {
        return null;
    }

    public Class classForName(String className, Map context) throws ClassNotFoundException {
        Object root = Ognl.getRoot(context);

        try {
            if (root instanceof CompoundRoot) {
                if (className.startsWith("vs")) {
                    CompoundRoot compoundRoot = (CompoundRoot) root;

                    if (className.equals("vs")) {
                        return compoundRoot.peek().getClass();
                    }

                    int index = Integer.parseInt(className.substring(2));

                    return compoundRoot.get(index - 1).getClass();
                }
            }
        } catch (Exception e) {
            // just try the old fashioned way
        }

        Class clazz = null;

        try {
            clazz = ClassUtility.loadClass(className);
        } catch (ClassNotFoundException e) {

        }

        try {
            clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {

        }


        return clazz;
    }

    private Class[] getArgTypes(Object[] args) {
        if (args == null) {
            return new Class[0];
        }

        Class[] classes = new Class[args.length];

        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            classes[i] = (arg != null) ? arg.getClass() : Object.class;
        }

        return classes;
    }


    static class MethodCall {
        Class clazz;
        String name;
        Class[] args;
        int hash;

        public MethodCall(Class clazz, String name, Class[] args) {
            this.clazz = clazz;
            this.name = name;
            this.args = args;
            this.hash = clazz.hashCode() + name.hashCode();

            for (int i = 0; i < args.length; i++) {
                Class arg = args[i];
                hash += arg.hashCode();
            }
        }

        public boolean equals(Object obj) {
            MethodCall mc = (MethodCall) obj;

            return (mc.clazz.equals(clazz) && mc.name.equals(name) && Arrays.equals(mc.args, args));
        }

        public int hashCode() {
            return hash;
        }
    }


    public String getSourceAccessor(OgnlContext arg0, Object arg1, Object arg2) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getSourceSetter(OgnlContext arg0, Object arg1, Object arg2) {
        // TODO Auto-generated method stub
        return null;
    }
}
