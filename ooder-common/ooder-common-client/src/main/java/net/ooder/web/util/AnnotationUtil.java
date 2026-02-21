/**
 * $RCSfile: AnnotationUtil.java,v $
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
package net.ooder.web.util;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.util.TypeUtils;
import javassist.CtClass;
import javassist.CtMethod;
import net.ooder.annotation.AnnotationType;
import net.ooder.annotation.CustomBean;
import net.ooder.annotation.NotNull;
import net.ooder.common.JDSConstants;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.ClassUtility;
import net.ooder.common.util.StringUtility;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;
import net.ooder.config.TreeListResultModel;
import net.ooder.jds.core.esb.util.OgnlUtil;
import net.sf.cglib.beans.BeanMap;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.Callable;

public class AnnotationUtil {
    protected static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, AnnotationUtil.class);
    static final String[] baseMethodName = new String[]{"annotationType", "toString", "hashCode", "id", "dsmId"};
    // static final String[] baseMethodName = new String[]{"annotationType", "toString", "hashCode", "id", "dsmId", "repositoryId", "viewInstId", "domainId"};
    static Map<Class<? extends Annotation>, Map<String, Object>> defualtValue = new HashMap<>();

    static Map<Method, Map<Class<Annotation>, Annotation>> annCacheMap = new HashMap<>();

    //原生扩展

    public static Map<Class<Annotation>, Annotation> getMethodAnnotationMap(Method method) {
        Map<Class<Annotation>, Annotation> annMap = annCacheMap.get(method);
        if (annMap == null) {
            annMap = new HashMap<>();
            Class clazz = method.getDeclaringClass();
            Class[] insClasses = clazz.getInterfaces();
            for (Class insClass : insClasses) {
                if (!isInnerClass(insClass)) {
                    try {
                        insClass = ClassUtility.loadClass(insClass.getName());
                        //Method sMethod = insClass.getMethod(method.getName(), method.getParameterTypes());
                        Method sMethod = MethodUtil.getEqualMethod(insClass, method);
                        if (sMethod != null) {
                            for (Annotation annotationClass : sMethod.getAnnotations()) {
                                annMap.put((Class<Annotation>) annotationClass.annotationType(), annotationClass);
                            }
                        }

                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
                for (Annotation annotationClass : method.getAnnotations()) {
                    annMap.put((Class<Annotation>) annotationClass.annotationType(), annotationClass);
                }
            }
            annCacheMap.put(method, annMap);
        }


        return annMap;
    }


    //原生扩展
    public static <T extends Annotation> T[] getMethodAnnotations(Method method, Class<T> annotationClass) {
        Class clazz = method.getDeclaringClass();
        T[] annotation = method.getAnnotationsByType(annotationClass);
        if (annotation == null) {
            Class[] insClasses = clazz.getInterfaces();
            for (Class insClass : insClasses) {

                if (!isInnerClass(insClass)) {
                    try {
                        insClass = ClassUtility.loadClass(insClass.getName());
                        Method sMethod = MethodUtil.getEqualMethod(insClass, method);
                        if (sMethod != null && sMethod.getAnnotationsByType(annotationClass) != null) {
                            return sMethod.getAnnotationsByType(annotationClass);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return annotation;
    }


    //原生扩展
    public static <T extends Annotation> T getMethodAnnotation(Method method, Class<T> annotationClass) {
        Class clazz = method.getDeclaringClass();
        T annotation = method.getAnnotation(annotationClass);
        if (annotation == null) {
            Class[] insClasses = clazz.getInterfaces();
            for (Class insClass : insClasses) {

                if (!isInnerClass(insClass)) {
                    try {
                        insClass = ClassUtility.loadClass(insClass.getName());
                        Method sMethod = MethodUtil.getEqualMethod(insClass, method);
                        // Method sMethod = insClass.getMethod(method.getName(), method.getParameterTypes());
                        if (sMethod != null && sMethod.getAnnotation(annotationClass) != null) {
                            return sMethod.getAnnotation(annotationClass);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();

                    }
                }

            }
        }
        return annotation;
    }


    //原生扩展
    public static Annotation[] getParameterAnnotations(Method method, int k) {
        Class clazz = method.getDeclaringClass();
        Annotation[] annotation = new Annotation[]{};
        Annotation[][] annotations = method.getParameterAnnotations();
        if (annotations.length > k) {
            annotation = annotations[k];
            if (annotation == null || annotation.length == 0) {
                Class[] insClasses = clazz.getInterfaces();
                for (Class insClass : insClasses) {
                    if (!isInnerClass(insClass)) {
                        try {
                            insClass = ClassUtility.loadClass(insClass.getName());
                            Method sMethod = MethodUtil.getEqualMethod(insClass, method);
                            if (sMethod != null && sMethod.getParameterAnnotations() != null && sMethod.getParameterAnnotations()[k] != null) {
                                annotation = sMethod.getParameterAnnotations()[k];
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();

                        }
                    }

                }
            }
        }
        return annotation;
    }


    public static Map getDefaultAnnMap(Class<? extends Annotation> annotationClass) {
        Map valueMap = new HashMap<>();
        for (int k = 0; k < annotationClass.getDeclaredMethods().length; k++) {
            Method method = annotationClass.getDeclaredMethods()[k];
            Object obj = getDefaultValue(annotationClass, method.getName());
            valueMap.put(method.getName(), obj);
        }
        return valueMap;

    }


    //原生扩展
    public static <T extends Annotation> T getMethodAnnotation(CtMethod method, Class<T> annotationClass) throws ClassNotFoundException {
        CtClass clazz = method.getDeclaringClass();
        T annotation = (T) method.getAnnotation(annotationClass);
        try {
            if (annotation == null) {
                CtClass[] insClasses = clazz.getInterfaces();
                for (CtClass insClass : insClasses) {
                    if (!isInnerClass(insClass)) {
                        //   CtMethod sMethod = insClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
                        CtMethod sMethod = MethodUtil.getEqualCtMethod(insClass, method);
                        if (sMethod != null && sMethod.getAnnotation(annotationClass) != null) {
                            return (T) sMethod.getAnnotation(annotationClass);
                        }
                    }

                }
            }
        } catch (Throwable e) {

        }
        return annotation;
    }


    public static <T extends Annotation> T getConstructorAnnotation(Constructor constructor, Class<T> annotationClass) {
        T annotation = (T) constructor.getAnnotation(annotationClass);
        return annotation;
    }

    public static Set<Annotation> getAllConstructorAnnotations(Constructor constructor) {
        Set<Annotation> annotations = new HashSet<>();
        annotations.addAll(Arrays.asList(constructor.getDeclaredAnnotations()));
        return annotations;
    }


    public static <T extends Annotation> T getClassAnnotation(Class cClass, Class<T> annotationClass) {
        T annotation = (T) cClass.getAnnotation(annotationClass);
        try {
            if (annotation == null) {
                Class[] insClasses = cClass.getInterfaces();
                for (Class insClass : insClasses) {
                    insClass = ClassUtility.loadClass(insClass.getName());
                    if (insClass.getAnnotation(annotationClass) != null) {
                        return (T) insClass.getAnnotation(annotationClass);
                    }

                }
            }
        } catch (Throwable e) {

        }

        return annotation;
    }

    public static <T extends Annotation> T getClassAnnotation(CtClass cClass, Class<T> annotationClass) throws ClassNotFoundException {
        T annotation = (T) cClass.getAnnotation(annotationClass);
        try {
            if (annotation == null) {
                CtClass[] insClasses = cClass.getInterfaces();
                for (CtClass insClass : insClasses) {
                    if (insClass.getAnnotation(annotationClass) != null) {
                        return (T) insClass.getAnnotation(annotationClass);
                    }

                }
            }
        } catch (Throwable e) {

        }
        return annotation;
    }


    public static Set<Annotation> getAllAnnotations(Method method, boolean hasField) {
        Class clazz = method.getDeclaringClass();
        Set<Annotation> annotations = new HashSet<>();
        annotations.addAll(Arrays.asList(method.getDeclaredAnnotations()));
        if (hasField) {
            Field field = getField(method);
            if (field != null) {
                annotations.addAll(Arrays.asList(field.getAnnotations()));
            }
        }

        Class[] insClasses = clazz.getInterfaces();
        for (Class insClass : insClasses) {
            try {
                insClass = ClassUtility.loadClass(insClass.getName());
                Method sMethod = MethodUtil.getEqualMethod(insClass, method);
                if (sMethod != null) {
                    annotations.addAll(Arrays.asList(sMethod.getDeclaredAnnotations()));
                }
            } catch (Throwable e) {
                //  e.printStackTrace();
            }
        }

        return annotations;
    }

    public static Field getField(Method method) {
        Field field = null;
        if (method.getName().startsWith("get")) {
            String fieldName = StringUtility.getFieldName(method.getName());
            try {
                field = method.getDeclaringClass().getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                try {
                    field = method.getDeclaringClass().getField(fieldName);
                } catch (NoSuchFieldException e1) {
                    //  e1.printStackTrace();
                }
            }
        }
        return field;
    }


    public static <T extends Annotation> List<T> getAllAnnotations(Method method, Class<T> annotationClass, boolean hasField) {
        Class clazz = method.getDeclaringClass();
        List<T> annotations = new ArrayList<T>();
        for (Annotation tannotation : method.getAnnotationsByType(annotationClass)) {
            annotations.add((T) tannotation);
        }

        if (hasField) {
            Field field = getField(method);
            if (field != null) {
                for (Annotation tannotation : field.getAnnotationsByType(annotationClass)) {
                    annotations.add((T) tannotation);
                }
            }
        }


        Class[] insClasses = clazz.getInterfaces();
        for (Class insClass : insClasses) {
            try {
                insClass = ClassUtility.loadClass(insClass.getName());
                Method sMethod = MethodUtil.getEqualMethod(insClass, method);
                if (sMethod != null) {
                    for (Annotation tannotation : sMethod.getAnnotationsByType(annotationClass)) {
                        annotations.add((T) tannotation);
                    }
                }
            } catch (Throwable e) {
            }
        }

        return annotations;
    }


    public static <T extends Annotation> List<T> getAllClassAnnotations(Class cClass, Class<T> annotationClass) {
        List<T> annotations = new ArrayList<T>();
        for (Annotation tannotation : cClass.getAnnotationsByType(annotationClass)) {
            annotations.add((T) tannotation);
        }
        Class[] insClasses = cClass.getInterfaces();
        for (Class insClass : insClasses) {
            for (Annotation tannotation : insClass.getAnnotationsByType(annotationClass)) {
                annotations.add((T) tannotation);
            }
        }
        return annotations;
    }

    public static Object getValue(Annotation annotation, String name) {
        Object obj = null;
        Class enumType = annotation.annotationType();
        try {
            obj = enumType.getMethod(name, null).invoke(annotation, null);
        } catch (Throwable e) {

            // e.printStackTrace();
        }
        return obj;

    }


    public static Object[] getValues(Annotation annotation, String name) {
        Object[] obj = null;
        Class enumType = annotation.annotationType();
        Method method = null;
        try {
            method = enumType.getMethod(name, null);
            obj = (Object[]) method.invoke(annotation, null);
        } catch (Throwable e) {
            // e.printStackTrace();
        }

        return obj;

    }

    public static <T> T fillBean(Annotation annotation, T bean) {
        fillObject(annotation, bean);
        return bean;
    }


    public static Object getDefaultValue(Class<? extends Annotation> enumType, String name) {
        for (int k = 0; k < enumType.getDeclaredMethods().length; k++) {
            Method method = enumType.getDeclaredMethods()[k];
            if (method.getName().equals(name)) {
                return method.getDefaultValue();
            }
        }
        return null;

    }

    public static Object[] getDefaultValues(Class<? extends Annotation> enumType, String name) {
        for (int k = 0; k < enumType.getDeclaredMethods().length; k++) {
            Method method = enumType.getDeclaredMethods()[k];
            if (method.getName().equals(name)) {
                Object[] objs = new Object[]{};
                if (!method.getDefaultValue().getClass().isArray()) {
                    objs = new Object[]{method.getDefaultValue()};
                } else {
                    objs = (Object[]) method.getDefaultValue();
                }
                return objs;
            }
        }
        return null;

    }


    public static <T> T fillDefaultValue(Class<? extends Annotation> enumType, T object) {
        synchronized (enumType) {
            Map valueMap = defualtValue.get(enumType);
            if (valueMap == null) {
                valueMap = new HashMap();
                for (int k = 0; k < enumType.getDeclaredMethods().length; k++) {
                    Method method = enumType.getDeclaredMethods()[k];
                    Object obj = method.getDefaultValue();
                    if (obj != null && (obj.getClass().isArray() || method.getAnnotation(NotNull.class) != null)) {
                        Field[] declaredFields = object.getClass().getDeclaredFields();
                        Field field = TypeUtils.getField(object.getClass(), method.getName(), declaredFields);
                        if (field == null) {
                            field = TypeUtils.getField(object.getClass(), method.getName(), object.getClass().getFields());
                        }
                        try {
                            if (field != null) {
                                if (obj.getClass().isArray() && !JSONArray.parseArray(JSONObject.toJSONString(obj)).isEmpty()) {
                                    valueMap.put(field.getName(), JSONObject.parseObject(JSONObject.toJSONString(obj), field.getGenericType()));
                                } else {
                                    valueMap.put(method.getName(), TypeUtils.cast(obj, field.getGenericType(), ParserConfig.getGlobalInstance()));
                                }
                            } else {
                                logger.error(enumType.getClass().getName() + "[" + method.getName() + "] not from in " + object.getClass().getName());
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                            logger.error(enumType.getClass().getName() + "[" + method.getName() + "] of " + object.getClass().getName() + " error [ " + e + "]");
                            logger.error(method.getName() + " error in " + e);
                        }
                    }
                }
                defualtValue.put(enumType, valueMap);
            }
            OgnlUtil.setProperties(valueMap, object, false);
        }

        return object;

    }

    public static Object fillObject(Annotation annotation, Object object) {
        Class enumType = annotation.annotationType();
        Map valueMap = new HashMap<>();
        for (int k = 0; k < enumType.getDeclaredMethods().length; k++) {
            Method method = enumType.getDeclaredMethods()[k];
            try {
                Object obj = method.invoke(annotation, null);
                if ((!obj.equals(method.getDefaultValue()) || (obj.getClass().isArray() || method.getAnnotation(NotNull.class) != null))) {
                    Field[] declaredFields = object.getClass().getDeclaredFields();
                    Field field = TypeUtils.getField(object.getClass(), method.getName(), declaredFields);
                    if (field == null) {
                        field = TypeUtils.getField(object.getClass(), method.getName(), object.getClass().getFields());
                    }
                    if (field != null) {
                        if (obj.getClass().isArray() && !JSONArray.parseArray(JSONObject.toJSONString(obj)).isEmpty()) {
                            valueMap.put(field.getName(), JSONObject.parseObject(JSONObject.toJSONString(obj), field.getGenericType()));
                        } else {
                            valueMap.put(method.getName(), TypeUtils.cast(obj, field.getGenericType(), null));
                        }
                    }
                }


            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        OgnlUtil.setProperties(valueMap, object, false);
        return object;
    }

    public static <T> T fillBean(Annotation annotation, Class<? extends T> tClass) {
        Class enumType = annotation.annotationType();
        Map valueMap = new HashMap<>();
        for (int k = 0; k < enumType.getDeclaredMethods().length; k++) {
            Method method = enumType.getDeclaredMethods()[k];
            try {
                valueMap.put(method.getName(), method.invoke(annotation, null));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        T bean = JSONObject.parseObject(JSONObject.toJSONString(valueMap), tClass);

        return bean;

    }

    static boolean isInnerClass(Class insClass) {
        Class[] innerClasss = new Class[]{Serializable.class, Callable.class, Comparable.class};
        for (Class innerClass : innerClasss) {
            if (insClass.equals(innerClass)) {
                return true;
            }
        }
        return false;
    }

    static boolean isInnerClass(CtClass insClass) {
        Class[] innerClasss = new Class[]{Serializable.class, Callable.class, Comparable.class};
        for (Class innerClass : innerClasss) {
            if (insClass.equals(innerClass)) {
                return true;
            }
        }
        return false;
    }

    private static StringBuffer toEnumsString(StringBuffer buffer, Object obj) {

        AnnotationType annotationType = AnnotationUtil.getClassAnnotation(obj.getClass(), AnnotationType.class);
        if (annotationType != null) {
            buffer.append(toAnnotationStr(obj));
        } else if (obj instanceof String) {
            String objStr = obj.toString().replace("\\", "\\\\");
            objStr = objStr.replace("\n", "\\n");
            buffer.append("\"" + objStr + "\"");
        } else if (obj.getClass().isEnum()) {
            buffer.append(obj.getClass().getSimpleName() + "." + ((Enum) obj).name());
        } else if (obj instanceof Annotation) {
            toAnnotationStr(buffer, (Annotation) obj);
        } else if (obj instanceof Class) {
            //统一采用全路径控制绑定类
            buffer.append(((Class) obj).getName() + ".class");
            // buffer.append(((Class) obj).getSimpleName() + ".class");
        } else {
            buffer.append(obj);
        }
        return buffer;
    }

    private static StringBuffer toEnumsArr(StringBuffer buffer, Object[] objs) {
        if (objs.length > 1) {
            buffer.append("{");
            for (Object obj : objs) {
                toEnumsString(buffer, obj);
                buffer.append(",");
            }
            if (buffer.toString().endsWith(",")) {
                buffer.deleteCharAt(buffer.length() - 1);
            }
            buffer.append("}");
        } else if (objs[0] != null) {
            toEnumsString(buffer, objs[0]);
        } else {
            buffer.append("{}");
        }


        return buffer;
    }

    private static StringBuffer toEnumsArr(StringBuffer buffer, Collection objs) {
        if (objs.size() > 1) {
            buffer.append("{");
            for (Object obj : objs) {
                if (obj != null) {
                    toEnumsString(buffer, obj);
                    buffer.append(",");
                }
            }
            if (buffer.toString().endsWith(",")) {
                buffer.deleteCharAt(buffer.length() - 1);
            }
            buffer.append("}");
        } else {
            Object obj = objs.iterator().next();
            if (obj != null) {
                toEnumsString(buffer, obj);
            } else {
                buffer.append("{}");
            }


        }
        return buffer;
    }


    private static String toAnnotationStr(Class clazz, Map<String, Object> map) {
        StringBuffer buffer = new StringBuffer("@");
        buffer.append(clazz.getSimpleName());
        if (map.size() > 0) {
            buffer.append("(");
            List<String> methodSet = Arrays.asList(baseMethodName);
            for (String key : map.keySet()) {
                if (map.get(key) != null) {
                    if (!methodSet.contains(key)) {
                        buffer.append(key + "=");
                        if (map.get(key) instanceof Collection) {
                            Collection objs = (Collection) map.get(key);
                            if (objs.isEmpty()) {
                                objs = (Collection) map.get(key + "Class");
                            }
                            toEnumsArr(buffer, objs);
                        } else if (map.get(key).getClass().isArray()) {
                            Object[] objs = (Object[]) map.get(key);
                            toEnumsArr(buffer, objs);
                        } else {
                            Object obj = map.get(key);
                            toEnumsString(buffer, obj);
                        }
                        buffer.append(",");
                    }
                }
            }
            if (buffer.toString().endsWith(",")) {
                buffer.deleteCharAt(buffer.length() - 1);
            }

            buffer.append(")");
        }

        return buffer.toString();
    }

    public static String toAnnotationStr(Class<Annotation> annotationType, List objs) {

        Map<String, Object> sourceMap = new HashMap();

        for (Object obj : objs) {
            BeanMap beanMap = BeanMap.create(obj);
            sourceMap.putAll(beanMap);
        }
        Map<String, Object> valueMap = new HashMap<>();
        for (int k = 0; k < annotationType.getDeclaredMethods().length; k++) {
            Method method = annotationType.getDeclaredMethods()[k];
            List<String> methodSet = Arrays.asList(baseMethodName);
            if (!methodSet.contains(method.getName())) {
                if (method.getReturnType().isArray()) {
                    Object[] values = (Object[]) sourceMap.get(method.getName());
                    if (values != null && values.length > 0) {
                        if (!method.getDefaultValue().equals(values)) {
                            valueMap.put(method.getName(), values);
                        }
                    }
                } else {
                    Object value = sourceMap.get(method.getName());
                    if (value != null && !value.equals(method.getDefaultValue())) {
                        valueMap.put(method.getName(), value);
                    }
                }
            }

        }
        return toAnnotationStr(annotationType, valueMap);
    }

    public static String toAnnotationStr(Object obj, Map<String, Object> defalutMap) {
        Class enumType = AnnotationUtil.getClassAnnotation(obj.getClass(), AnnotationType.class).clazz();

        BeanMap beanMap = BeanMap.create(obj);
        Map valueMap = new HashMap<>();
        for (int k = 0; k < enumType.getDeclaredMethods().length; k++) {
            Method method = enumType.getDeclaredMethods()[k];
            List<String> methodSet = Arrays.asList(baseMethodName);
            if (beanMap.get(method.getName()) != null && !methodSet.contains(method.getName())) {
                if (method.getReturnType().isArray()) {
                    if (beanMap.get(method.getName()).getClass().isArray()) {
                        Object[] valueArr = (Object[]) beanMap.get(method.getName());
                        if (valueArr.length > 0) {
                            Object[] objects = (Object[]) defalutMap.get(method.getName());
                            if (!equalObjects(objects, valueArr)) {
                                valueMap.put(method.getName(), valueArr);
                            }
                        }
                    } else {
                        Object values = beanMap.get(method.getName());
                        if (values instanceof Collection) {
                            if (((Collection) values).size() > 0) {
                                Object[] objects = (Object[]) defalutMap.get(method.getName());
                                Object[] valueArr = ((Collection) values).toArray();
                                Class objType = JSONGenUtil.getInnerType(objects.getClass());
                                Class valueType = JSONGenUtil.getInnerType(valueArr.getClass());
                                if (!objType.equals(valueType)) {
                                    valueArr = JSONArray.parseArray(JSONArray.toJSONString(values), objType).toArray();
                                }
                                if (!equalObjects(objects, valueArr)) {
                                    valueMap.put(method.getName(), values);
                                }
                            }
                        }
                    }

                } else {
                    Object value = beanMap.get(method.getName());
                    if (value != null) {
                        Object defaultValue = TypeUtils.castToJavaBean(defalutMap.get(method.getName()), value.getClass());
                        if (defaultValue == null) {
                            valueMap.put(method.getName(), value);
                        } else if (!value.equals(defaultValue)) {
                            if (defaultValue.toString().equals("auto") || defaultValue.toString().endsWith("em") || defaultValue.toString().endsWith("px")) {
                                if (!value.toString().equals("")) {
                                    valueMap.put(method.getName(), value);
                                }
                            } else {
                                valueMap.put(method.getName(), value);
                            }
                        }
                    }
                }
            }
        }
        return toAnnotationStr(enumType, valueMap);
    }


    public static String toAnnotationStr(Object obj) {
        String annotationStr = null;
        AnnotationType annotation = AnnotationUtil.getClassAnnotation(obj.getClass(), AnnotationType.class);
        if (annotation == null) {
            try {
                StringBuffer stringBuffer = new StringBuffer();
                List<CustomBean> customBeans = (List<CustomBean>) OgnlUtil.getValue("annotationBeans", new HashMap(), obj);
                for (CustomBean customBean : customBeans) {
                    stringBuffer.append(customBean.toAnnotationStr());
                }
                annotationStr = stringBuffer.toString();
            } catch (Throwable e) {
                e.printStackTrace();
            }

        } else {
            Class enumType = annotation.clazz();
            Map valueMap = getAnnotationMap(obj);
            annotationStr = toAnnotationStr(enumType, valueMap);
        }
        return annotationStr;

    }


    public static Map getAnnotationMap(Object obj) {

        Class enumType = AnnotationUtil.getClassAnnotation(obj.getClass(), AnnotationType.class).clazz();
        BeanMap beanMap = null;
        try {
            beanMap = BeanMap.create(obj);
        } catch (Throwable e) {
            beanMap = BeanMap.create(obj);
        }

        Map valueMap = new HashMap<>();

        for (int k = 0; k < enumType.getDeclaredMethods().length; k++) {
            Method method = enumType.getDeclaredMethods()[k];
            List<String> methodSet = Arrays.asList(baseMethodName);

            if (beanMap.get(method.getName()) != null && !methodSet.contains(method.getName())) {
                if (method.getReturnType().isArray()) {
                    Object values = beanMap.get(method.getName());
                    if (beanMap.get(method.getName()).getClass().isArray()) {

                        Object[] valueArr = (Object[]) beanMap.get(method.getName());
                        if (valueArr.length > 0) {
                            Object[] objects = (Object[]) method.getDefaultValue();
                            if (!equalObjects(objects, valueArr)) {
                                valueMap.put(method.getName(), valueArr);
                            }
                        }

                    } else if (values instanceof Collection) {
                        if (((Collection) values).size() > 0) {
                            Object[] objects = (Object[]) method.getDefaultValue();
                            Object[] valueArr = ((Collection) values).toArray();
                            if (objects != null) {
                                Class objType = JSONGenUtil.getInnerType(objects.getClass());
                                Class valueType = JSONGenUtil.getInnerType(valueArr.getClass());
                                if (!objType.equals(valueType)) {
                                    valueArr = JSONArray.parseArray(JSONArray.toJSONString(values), objType).toArray();
                                }
                                if (!equalObjects(objects, valueArr)) {
                                    valueMap.put(method.getName(), values);
                                }
                            } else {
                                valueMap.put(method.getName(), valueArr);
                            }

                        }

                    }
                } else if (method.getReturnType() == boolean.class) {
                    Boolean defaultValue = Boolean.valueOf((Boolean) method.getDefaultValue());
                    Object value = beanMap.get(method.getName());
                    if (value != null && !Boolean.valueOf((Boolean) value).equals(defaultValue)) {
                        valueMap.put(method.getName(), value);
                    }

                } else {
                    Object value = beanMap.get(method.getName());
                    if (value != null) {
                        Object defaultValue = TypeUtils.castToJavaBean(method.getDefaultValue(), value.getClass());
                        if (defaultValue == null) {
                            valueMap.put(method.getName(), value);
                        } else if (!value.equals(defaultValue)) {
                            if (defaultValue.toString().equals("auto") || defaultValue.toString().endsWith("em") || defaultValue.toString().endsWith("%") || defaultValue.toString().endsWith("px")) {
                                if (!value.toString().equals("")) {
                                    valueMap.put(method.getName(), value);
                                }
                            } else {
                                valueMap.put(method.getName(), value);
                            }
                        }
                    }
                }
            }

        }
        return valueMap;
    }


    public static Map getAnnotationOtherValue(Annotation annotation) {
        Class enumType = annotation.annotationType();
        Map valueMap = new HashMap<>();
        for (int k = 0; k < enumType.getDeclaredMethods().length; k++) {
            Method method = enumType.getDeclaredMethods()[k];
            List<String> methodSet = Arrays.asList(baseMethodName);
            try {
                if (!methodSet.contains(method.getName())) {
                    if (method.getReturnType().isArray()) {
                        Object values = method.invoke(annotation, null);
                        if (values instanceof Collection) {
                            if (((Collection) values).size() > 0) {
                                if (!method.getDefaultValue().equals(values)) {
                                    valueMap.put(method.getName(), values);
                                }
                            }
                        }

                    } else {
                        Object value = method.invoke(annotation, null);
                        if (!value.equals(method.getDefaultValue())) {
                            valueMap.put(method.getName(), value);
                        }
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        return valueMap;
    }


    public static String toAnnotationStr(Annotation annotation) {
        StringBuffer buffer = new StringBuffer();
        Map<String, Object> valueMap = getAnnotationOtherValue(annotation);
        buffer.append(toAnnotationStr(annotation.annotationType(), valueMap));
        return buffer.toString();
    }

    public static StringBuffer toAnnotationStr(StringBuffer buffer, Annotation annotation) {
        if (buffer == null) {
            buffer = new StringBuffer();
        }
        Map<String, Object> valueMap = getAnnotationOtherValue(annotation);
        buffer.append(toAnnotationStr(annotation.annotationType(), valueMap));
        return buffer;
    }


    public static StringBuffer toTypeStr(StringBuffer methodBuffer, ParameterizedType type) {
        Type rawTye = type.getRawType();
        if (rawTye != null) {
            if (rawTye == Map.class || rawTye == HashMap.class) {
                Type keyType = type.getActualTypeArguments()[0];
                methodBuffer.append("Map");
                methodBuffer.append("<");
                methodBuffer = toType(methodBuffer, keyType);
                methodBuffer.append(",");
                Type valueType = type.getActualTypeArguments()[1];
                methodBuffer = toType(methodBuffer, valueType);
                methodBuffer.append(">");
            } else {
                if (ListResultModel.class.isAssignableFrom((Class<?>) rawTye)) {
                    methodBuffer.append("ListResultModel");
                } else if (rawTye == ResultModel.class) {
                    methodBuffer.append("ResultModel");
                } else if (rawTye == TreeListResultModel.class) {
                    methodBuffer.append("TreeListResultModel");
                } else if (rawTye == Set.class
                        || rawTye == HashSet.class //
                        || rawTye == TreeSet.class //
                        || rawTye == Collection.class //
                        || rawTye == List.class //
                        || rawTye == ArrayList.class) {
                    methodBuffer = toType(methodBuffer, rawTye);
                } else {
                    methodBuffer = toType(methodBuffer, rawTye);
                }
                if (type.getActualTypeArguments() != null && type.getActualTypeArguments().length > 0) {
                    methodBuffer.append("<");
                    Type itemType = type.getActualTypeArguments()[0];
                    if (itemType instanceof Class) {
                        methodBuffer = toType(methodBuffer, itemType);
                    } else {
                        methodBuffer = toType(methodBuffer, itemType);
                    }
                    methodBuffer.append(">");
                }
            }


        }
        return methodBuffer;
    }

    public static StringBuffer toType(Type type) {
        StringBuffer methodBuffer = new StringBuffer("");
        return toType(methodBuffer, type);
    }

    public static StringBuffer toType(Class type, Type genericType) {
        StringBuffer methodBuffer = new StringBuffer("");
        if (genericType != null && !genericType.equals(type)) {
            methodBuffer = toType(methodBuffer, genericType);
        } else {
            methodBuffer.append(type.getSimpleName());
        }
        return methodBuffer;
    }


    public static StringBuffer toReturnStr(Method method) {
        StringBuffer methodBuffer = new StringBuffer();
        Type type = method.getGenericReturnType();
        Class clazz = method.getReturnType();
        methodBuffer.append(clazz.getSimpleName());
        if (type != null) {
            methodBuffer.append("<");
            methodBuffer = toType(methodBuffer, type);
            methodBuffer.append(">");
        }
        return methodBuffer;
    }


    public static StringBuffer toReturnStr(Field field) {
        StringBuffer methodBuffer = new StringBuffer();
        Type type = field.getGenericType();
        Class clazz = field.getType();
        methodBuffer.append(clazz.getSimpleName());
        if (type != null) {
            methodBuffer.append("<");
            methodBuffer = toType(methodBuffer, type);
            methodBuffer.append(">");
        }
        return methodBuffer;
    }


    public static StringBuffer toType(StringBuffer methodBuffer, Type type) {
        if (type instanceof ParameterizedType) {
            methodBuffer = toTypeStr(methodBuffer, (ParameterizedType) type);
        } else if (type instanceof WildcardType) {
            methodBuffer = toWildcardType(methodBuffer, (WildcardType) type);
        } else if (TypeUtils.loadClass(type.getTypeName()) != null) {
            methodBuffer.append(TypeUtils.loadClass(type.getTypeName()).getSimpleName());
        } else {
            methodBuffer.append(type.getTypeName() + " ");
        }
        return methodBuffer;
    }

    public static StringBuffer toWildcardType(StringBuffer methodBuffer, WildcardType type) {

        if (type.getTypeName().equals("?")) {
            methodBuffer.append(type.getTypeName());
        } else if (type.getTypeName().startsWith("?") && type.getUpperBounds().length > 0) {
            methodBuffer.append("? extends ");
            methodBuffer.append(((Class) type.getUpperBounds()[0]).getSimpleName());
        }
        return methodBuffer;
    }


    public static boolean equalObjects(Object[] a, Object[] a2) {
        if (a == a2)
            return true;
        if (a == null || a2 == null)
            return false;

        int length = a.length;
        if (a2.length != length)
            return false;

        List a2List = Arrays.asList(a2);
        for (int i = 0; i < length; i++) {
            Object o1 = a[i];
            if (!a2List.contains(o1)) {
                return false;
            }
        }
        return true;
    }
}
