/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
/**
 * $RCSfile: JDSConverter.java,v $
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
import ognl.DefaultTypeConverter;
import ognl.OgnlRuntime;
import ognl.TypeConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Member;
import java.util.*;


public class JDSConverter extends DefaultTypeConverter {

    private static JDSConverter instance;
    protected static final Log LOG = LogFactory.getLog(JDSConverter.class);
    public static final String REPORT_CONVERSION_ERRORS = "report.conversion.errors";
    public static final String CONVERSION_PROPERTY_FULLNAME = "conversion.property.fullName";
    public static final String CONVERSION_ERROR_PROPERTY_PREFIX = "invalid.fieldvalue.";
    public static final String CONVERSION_COLLECTION_PREFIX = "Collection_";

    public static final String LAST_BEAN_CLASS_ACCESSED = "last.bean.accessed";
    public static final String LAST_BEAN_PROPERTY_ACCESSED = "last.property.accessed";

    /**
     * Target class conversion Mappings.
     * <pre>
     * Map<Class, Map<String, Object>>
     *  - Class -> convert to class
     *  - Map<String, Object>
     *    - String -> property name
     *                eg. Element_property, property etc.
     *    - Object -> String to represent properties
     *                eg. value part of
     *                    KeyProperty_property=id
     *             -> TypeConverter to represent an Ognl TypeConverter
     *                eg. value part of
     *                    property=foo.bar.MyConverter
     *             -> Class to represent a class
     *                eg. value part of
     *                    Element_property=foo.bar.MyObject
     * </pre>
     */
    protected HashMap<String, Map<String, Object>> mappings = new HashMap<String, Map<String, Object>>(); // action

    /**
     * Unavailable target class conversion mappings, serves as a simple cache.
     */
    protected LinkedHashSet<String> noMapping = new LinkedHashSet<String>(); // action

    /**
     * Record class and its type converter mapping.
     * <pre>
     * - String - classname as String
     * - TypeConverter - instance of TypeConverter
     * </pre>
     */
    protected HashMap<String, TypeConverter> defaultMappings = new HashMap<String, TypeConverter>();  // non-action (eg. returned value)

    /**
     * Record classes that doesn't have conversion mapping defined.
     * <pre>
     * - String -> classname as String
     * </pre>
     */
    protected LinkedHashSet<String> unknownMappings = new LinkedHashSet<String>();    // non-action (eg. returned value)

    protected TypeConverter defaultTypeConverter = new JDSBasicConverter();
    protected ObjectTypeDeterminer objectTypeDeterminer = null;


    protected JDSConverter() {
        try {
            // note: this file is deprecated
            loadConversionProperties("jdsesd-default-conversion.properties");
        } catch (Exception e) {
        }

        try {
            loadConversionProperties("jdsesd-conversion.properties");
        } catch (Exception e) {
        }
    }

    public static String getConversionErrorMessage(String propertyName, ValueStack stack) {
        String message = (String) stack.findValue(propertyName);

        return message;
    }


    public static JDSConverter getInstance() {
        if (instance == null) {
            instance = new JDSConverter();
        }

        return instance;
    }


    public static void setInstance(JDSConverter instance) {
        JDSConverter.instance = instance;
    }

    public static String buildConverterFilename(Class clazz) {
        String className = clazz.getName();
        String resource = className.replace('.', '/') + "-conversion.properties";

        return resource;
    }

    public static void resetInstance() {
        instance = null;
    }

    public void setDefaultConverter(TypeConverter defaultTypeConverter) {
        this.defaultTypeConverter = defaultTypeConverter;
    }

    public Object convertValue(Map map, Object o, Class aClass) {
        return convertValue(map, null, null, null, o, aClass);
    }

    /**
     * Convert value from one form to another.
     * Minimum requirement of arguments:
     * <ul>
     * <li>supplying context, toClass and value</li>
     * <li>supplying context, target and value.</li>
     * </ul>
     *
     * @see ognl.TypeConverter#convertValue(Map, Object, Member, String, Object, Class)
     */
    public Object convertValue(Map context, Object target, Member member, String property, Object value, Class toClass) {
        //
        // Process the conversion using the default mappings, if one exists
        //
        TypeConverter tc = null;
        if ((value != null) && (toClass == value.getClass())) {
            return value;
        }

        // allow this method to be called without any context
        // i.e. it can be called with as little as "Object value" and "Class toClass"
        if (target != null) {
            Class clazz = target.getClass();
            Object[] classProp = null;
            // this is to handle weird issues with setValue with a different type
            if ((target instanceof CompoundRoot) && (context != null)) {
                classProp = getClassProperty(context);
            }

            if (classProp != null) {
                clazz = (Class) classProp[0];
                property = (String) classProp[1];
            }

            tc = (TypeConverter) getConverter(clazz, property);

            if (LOG.isDebugEnabled())
                LOG.debug("field-level type converter for property [" + property + "] = " + (tc == null ? "none found" : tc));
        }

        if (tc == null && context != null) {
            // ok, let's see if we can look it up by path as requested in XW-297
            Object lastPropertyPath = context.get(OgnlContextState.CURRENT_PROPERTY_PATH);
            Class clazz = (Class) context.get(JDSConverter.LAST_BEAN_CLASS_ACCESSED);
            if (lastPropertyPath != null && clazz != null) {
                String path = lastPropertyPath + "." + property;
                tc = (TypeConverter) getConverter(clazz, path);
            }
        }

        if (tc == null) {
            if (toClass.equals(String.class) && (value != null) && !(value.getClass().equals(String.class) || value.getClass().equals(String[].class))) {
                // when converting to a string, use the source target's class's converter
                tc = lookup(value.getClass());
            } else {
                // when converting from a string, use the toClass's converter
                tc = lookup(toClass);
            }

            if (LOG.isDebugEnabled())
                LOG.debug("global-level type converter for property [" + property + "] = " + (tc == null ? "none found" : tc));
        }


        if (tc != null) {
            try {
                return tc.convertValue(context, target, member, property, value, toClass);
            } catch (Exception e) {
                handleConversionException(context, property, value, target);

                return OgnlRuntime.NoConversionPossible;
            }
        }

        if (defaultTypeConverter != null) {
            try {
                if (LOG.isDebugEnabled())
                    LOG.debug("falling back to default type converter [" + defaultTypeConverter + "]");
                return defaultTypeConverter.convertValue(context, target, member, property, value, toClass);
            } catch (Exception e) {
                handleConversionException(context, property, value, target);

                return OgnlRuntime.NoConversionPossible;
            }
        } else {
            try {
                if (LOG.isDebugEnabled())
                    LOG.debug("falling back to Ognl's default type conversion");
                return super.convertValue(context, target, member, property, value, toClass);
            } catch (Exception e) {
                handleConversionException(context, property, value, target);

                return OgnlRuntime.NoConversionPossible;
            }
        }
    }

    /**
     * Looks for a TypeConverter in the default mappings.
     *
     * @param className name of the class the TypeConverter must handle
     * @return a TypeConverter to handle the specified class or null if none can be found
     */
    public TypeConverter lookup(String className) {
        if (unknownMappings.contains(className)) {
            return null;
        }
        TypeConverter result = (TypeConverter) defaultMappings.get(className);
        if (result == null) {
            Class clazz = null;

            try {
                clazz = ClassUtility.loadClass(className);
            } catch (ClassNotFoundException cnfe) {
            }

            result = lookupSuper(clazz);

            if (result != null) {
                //Register now, the next lookup will be faster
                registerConverter(className, result);
            } else {
                // if it isn't found, never look again (also faster)
                registerConverterNotFound(className);
            }
        }

        return result;
    }

    /**
     * Looks for a TypeConverter in the default mappings.
     *
     * @param clazz the class the TypeConverter must handle
     * @return a TypeConverter to handle the specified class or null if none can be found
     */
    public TypeConverter lookup(Class clazz) {
        return lookup(clazz.getName());
    }

    protected Object getConverter(Class clazz, String property) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Property: " + property);
            LOG.debug("Class: " + clazz.getName());
        }
        synchronized (clazz) {
            if ((property != null) && !noMapping.contains(clazz.getName())) {
                try {
                    Map<String, Object> mapping = mappings.get(clazz);

                    if (mapping == null) {
                        mapping = buildConverterMapping(clazz);
                    } else {
                        mapping = conditionalReload(clazz, mapping);
                    }

                    Object converter = mapping.get(property);
                    if (LOG.isDebugEnabled() && converter == null) {
                        LOG.debug("converter is null for property " + property + ". Mapping size: " + mapping.size());
                        Iterator<String> iter = mapping.keySet().iterator();
                        while (iter.hasNext()) {
                            String next = iter.next();
                            LOG.debug(next + ":" + mapping.get(next));
                        }
                    }
                    return converter;
                } catch (Throwable t) {
                    noMapping.add(clazz.getName());
                }
            }
        }

        return null;
    }

    protected void handleConversionException(Map context, String property, Object value, Object object) {
        if ((Boolean.TRUE.equals(context.get(REPORT_CONVERSION_ERRORS)))) {
            String realProperty = property;
            String fullName = (String) context.get(CONVERSION_PROPERTY_FULLNAME);

            if (fullName != null) {
                realProperty = fullName;
            }

            Map conversionErrors = (Map) context.get(ActionContext.CONVERSION_ERRORS);

            if (conversionErrors == null) {
                conversionErrors = new HashMap();
                context.put(ActionContext.CONVERSION_ERRORS, conversionErrors);
            }

            conversionErrors.put(realProperty, value);
        }
    }

    public synchronized void registerConverter(String className, TypeConverter converter) {
        defaultMappings.put(className, converter);
    }

    public synchronized void registerConverterNotFound(String className) {
        unknownMappings.add(className);
    }

    private Object[] getClassProperty(Map context) {
        return (Object[]) context.get("__link");
    }

    /**
     * not used
     */
    private Object acceptableErrorValue(Class toClass) {
        if (!toClass.isPrimitive()) {
            return null;
        }

        if (toClass == int.class) {
            return new Integer(0);
        } else if (toClass == double.class) {
            return new Double(0);
        } else if (toClass == long.class) {
            return new Long(0);
        } else if (toClass == boolean.class) {
            return Boolean.FALSE;
        } else if (toClass == short.class) {
            return new Short((short) 0);
        } else if (toClass == float.class) {
            return new Float(0);
        } else if (toClass == byte.class) {
            return new Byte((byte) 0);
        } else if (toClass == char.class) {
            return new Character((char) 0);
        }

        return null;
    }

    /**
     * Looks for converter mappings for the specified class and adds it to an existing map.  Only new converters are
     * added.  If a converter is defined on a key that already exists, the converter is ignored.
     *
     * @param mapping an existing map to add new converter mappings to
     * @param clazz   class to look for converter mappings for
     */
    void addConverterMapping(Map<String, Object> mapping, Class clazz) {
        try {
            String converterFilename = buildConverterFilename(clazz);
            InputStream is = FileManager.loadFile(converterFilename, clazz);

            if (is != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("processing conversion file [" + converterFilename + "] [class=" + clazz + "]");
                }

                Properties prop = new Properties();
                prop.load(is);

                Iterator it = prop.entrySet().iterator();

                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next();
                    String key = (String) entry.getKey();

                    if (mapping.containsKey(key)) {
                        break;
                    }
                    // for keyProperty of Set
                    if (key.startsWith(DefaultObjectTypeDeterminer.KEY_PROPERTY_PREFIX)
                            || key.startsWith(DefaultObjectTypeDeterminer.CREATE_IF_NULL_PREFIX)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("\t" + key + ":" + entry.getValue() + "[treated as String]");
                        }
                        mapping.put(key, entry.getValue());
                    }
                    //for properties of classes
                    else if (!(key.startsWith(DefaultObjectTypeDeterminer.ELEMENT_PREFIX) ||
                            key.startsWith(DefaultObjectTypeDeterminer.KEY_PREFIX) ||
                            key.startsWith(DefaultObjectTypeDeterminer.DEPRECATED_ELEMENT_PREFIX))
                            ) {
                        TypeConverter _typeConverter = createTypeConverter((String) entry.getValue());
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("\t" + key + ":" + entry.getValue() + "[treated as TypeConverter " + _typeConverter + "]");
                        }
                        mapping.put(key, _typeConverter);
                    }
                    //for keys of Maps
                    else if (key.startsWith(DefaultObjectTypeDeterminer.KEY_PREFIX)) {

                        Class converterClass = Thread.currentThread().getContextClassLoader().loadClass((String) entry.getValue());

                        //check if the converter is a type converter if it is one
                        //then just put it in the map as is. Otherwise
                        //put a value in for the type converter of the class
                        if (converterClass.isAssignableFrom(TypeConverter.class)) {
                            TypeConverter _typeConverter = createTypeConverter((String) entry.getValue());
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("\t" + key + ":" + entry.getValue() + "[treated as TypeConverter " + _typeConverter + "]");
                            }
                            mapping.put(key, _typeConverter);
                        } else {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("\t" + key + ":" + entry.getValue() + "[treated as Class " + converterClass + "]");
                            }
                            mapping.put(key, converterClass);
                        }
                    }
                    //elements(values) of maps / lists
                    else {
                        Class _c = Thread.currentThread().getContextClassLoader().loadClass((String) entry.getValue());
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("\t" + key + ":" + entry.getValue() + "[treated as Class " + _c + "]");
                        }
                        mapping.put(key, _c);
                    }
                }
            }
        } catch (Exception ex) {
            LOG.error("Problem loading properties for " + clazz.getName(), ex);
        }

    }

    /**
     * Looks for converter mappings for the specified class, traversing up its class hierarchy and interfaces and adding
     * any additional mappings it may find.  Mappings lower in the hierarchy have priority over those higher in the
     * hierarcy.
     *
     * @param clazz the class to look for converter mappings for
     * @return the converter mappings
     */
    private Map<String, Object> buildConverterMapping(Class clazz) throws Exception {
        Map<String, Object> mapping = new HashMap<String, Object>();

        // check for conversion mapping associated with super classes and any implemented interfaces
        Class curClazz = clazz;

        while (!curClazz.equals(Object.class)) {
            // add current class' mappings
            addConverterMapping(mapping, curClazz);

            // check interfaces' mappings
            Class[] interfaces = curClazz.getInterfaces();

            for (int x = 0; x < interfaces.length; x++) {
                addConverterMapping(mapping, interfaces[x]);
            }

            curClazz = curClazz.getSuperclass();
        }

        if (mapping.size() > 0) {
            mappings.put(clazz.getName(), mapping);
        } else {
            noMapping.add(clazz.getName());
        }

        return mapping;
    }

    private Map<String, Object> conditionalReload(Class clazz, Map<String, Object> oldValues) throws Exception {
        Map<String, Object> mapping = oldValues;

        if (FileManager.isReloadingConfigs()) {
            if (FileManager.fileNeedsReloading(buildConverterFilename(clazz))) {
                mapping = buildConverterMapping(clazz);
            }
        }

        return mapping;
    }

    TypeConverter createTypeConverter(String className) throws Exception {
        // type converters are used across users
        return (TypeConverter) ObjectFactory.getObjectFactory().buildBean(ClassUtility.loadClass(className), null);
    }

    public void loadConversionProperties(String propsName) throws IOException {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(propsName);
        Properties props = new Properties();
        props.load(is);

        if (LOG.isDebugEnabled()) {
            LOG.debug("processing conversion file [" + propsName + "]");
        }

        for (Iterator iterator = props.entrySet().iterator();
             iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String key = (String) entry.getKey();

            try {
                TypeConverter _typeConverter = createTypeConverter((String) entry.getValue());
                if (LOG.isDebugEnabled()) {
                    LOG.debug("\t" + key + ":" + entry.getValue() + " [treated as TypeConverter " + _typeConverter + "]");
                }
                defaultMappings.put(key, _typeConverter);
            } catch (Exception e) {
                LOG.error("Conversion registration error", e);
            }
        }
    }

    /**
     * Recurses through a class' interfaces and class hierarchy looking for a TypeConverter in the default mapping that
     * can handle the specified class.
     *
     * @param clazz the class the TypeConverter must handle
     * @return a TypeConverter to handle the specified class or null if none can be found
     */
    TypeConverter lookupSuper(Class clazz) {
        TypeConverter result = null;

        if (clazz != null) {
            result = (TypeConverter) defaultMappings.get(clazz.getName());

            if (result == null) {
                // Looks for direct interfaces (depth = 1 )
                Class[] interfaces = clazz.getInterfaces();

                for (int i = 0; i < interfaces.length; i++) {
                    if (defaultMappings.containsKey(interfaces[i].getName())) {
                        result = (TypeConverter) defaultMappings.get(interfaces[i].getName());
                        break;
                    }
                }

                if (result == null) {
                    // Looks for the superclass
                    // If 'clazz' is the Object class, an interface, a primitive type or void then clazz.getSuperClass() returns null
                    result = lookupSuper(clazz.getSuperclass());
                }
            }
        }

        return result;
    }

    public ObjectTypeDeterminer getObjectTypeDeterminer() {
        if (objectTypeDeterminer == null) {
            return ObjectTypeDeterminerFactory.getInstance();
        } else {
            return objectTypeDeterminer;
        }
    }

    /**
     * @param determiner
     */
    public void setObjectTypeDeterminer(ObjectTypeDeterminer determiner) {
        objectTypeDeterminer = determiner;
    }

}
