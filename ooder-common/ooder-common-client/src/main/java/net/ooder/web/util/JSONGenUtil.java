package net.ooder.web.util;

import net.ooder.annotation.RequestType;
import net.ooder.common.util.ClassUtility;
import net.ooder.common.util.FileClassLoader;
import net.ooder.common.util.java.DynamicClassLoader;
import net.ooder.web.RequestMethodBean;
import net.sf.cglib.core.Signature;
import net.sf.cglib.core.TypeUtils;
import net.sf.cglib.proxy.InterfaceMaker;

import java.lang.reflect.*;
import java.util.*;

/**
 * JSONGenUtil 用于生成和处理 JSON 相关的工具类。
 */
public class JSONGenUtil {


    static Map<Class, Class> classMap = new HashMap<>();

    public static Class fillSetMethod(Class iClass) {
        Class clazz = classMap.get(iClass);
        if (clazz == null) {
            if (iClass.isInterface()) {
                InterfaceMaker im = new InterfaceMaker();
                Method[] methods = iClass.getDeclaredMethods();
                im.add(iClass);
                for (Method imethod : methods) {
                    if (imethod.getName().startsWith("get") || (imethod.getName().startsWith("is") &&
                            (imethod.getGenericReturnType().equals(boolean.class)
                                    || imethod.getGenericReturnType().equals(Boolean.class)))) {
                        String setMethodName = "set" + imethod.getName().substring(3, imethod.getName().length());

                        if (imethod.getName().startsWith("is")) {
                            setMethodName = "set" + imethod.getName().substring(2, imethod.getName().length());
                        }

                        Method setMethod = null;
                        try {
                            setMethod = iClass.getMethod(setMethodName, new Class[]{imethod.getReturnType()});
                        } catch (NoSuchMethodException e) {

                        }
                        if (setMethod == null) {
                            org.objectweb.asm.Type[] argumentTypes = TypeUtils.getTypes(new Class[]{imethod.getReturnType()});
                            Signature sig = new Signature(setMethodName, org.objectweb.asm.Type.VOID_TYPE, argumentTypes);
                            im.add(sig, null);
                        }
                    }

                }
                clazz = im.create();
                classMap.put(iClass, clazz);
            } else {
                clazz = iClass;
                classMap.put(iClass, clazz);
            }
        }
        return clazz;

    }


    public static Class getListReturnType(Method method) {
        Class iClass = null;
        Type type = null;
        try {
            method.getGenericReturnType();
            if (type instanceof ParameterizedType) {
                ParameterizedType ptype = (ParameterizedType) type;
                if (ptype.getActualTypeArguments()[0] instanceof ParameterizedType) {
                    ParameterizedType itype = (ParameterizedType) ptype.getActualTypeArguments()[0];
                    iClass = (Class) itype.getRawType();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (type == null) {
            iClass = method.getReturnType();
        }
        return iClass;
    }


    public static Class getInnerReturnType(Field field) {
        Type type = null;
        try {
            type = field.getGenericType();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (type == null) {
            type = field.getType();
        }


        return getInnerType(type);
    }

    public static Set<Class> getAllInnerReturnType(Field field, Set<Class> classSet) {
        if (classSet == null) {
            classSet = new LinkedHashSet<>();
        }
        Type type = field.getGenericType();
        return getInnerType(type, classSet);
    }

    public static Class getInnerRequestClass(RequestMethodBean methodBean) throws ClassNotFoundException {
        Class inclazz = null;
        if (methodBean.getRequestType().equals(RequestType.JSON)) {
            inclazz = methodBean.getSourceMethod().getParameterTypes()[0];
        } else {
            inclazz = JSONGenUtil.getInnerReturnType(methodBean.getSourceMethod());
        }
        return inclazz;
    }

    public static Class getInnerReturnType(RequestMethodBean methodBean) throws ClassNotFoundException {
        Class inclazz = JSONGenUtil.getInnerReturnType(methodBean.getSourceMethod());
        return inclazz;
    }


    public static Class getInnerReturnType(Method method) {
        Type type = null;
        try {
            type = method.getGenericReturnType();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (type == null) {
            type = method.getReturnType();
        }

        return getInnerType(type);
    }

    public static Type getRealType(Class clazz, Class typeClass) {
        TypeVariable[] types = clazz.getTypeParameters();
        Type realType = null;
        for (TypeVariable typeVariable : types) {
            Class innerClass = getInnerType(typeVariable);
            if (innerClass instanceof Class && typeClass.isAssignableFrom((Class<?>) innerClass)) {
                realType = innerClass;
            }
        }


        if (realType == null) {
            Type type = clazz.getAnnotatedSuperclass().getType();
            if (type instanceof Class) {
                realType = type;//getRealType((Class) type,typeClass);
            } else if (type instanceof ParameterizedType) {
                ParameterizedType ptype = (ParameterizedType) type;
                Type[] superTypes = ptype.getActualTypeArguments();
                for (Type innertype : superTypes) {
                    if (innertype instanceof Class && typeClass.isAssignableFrom((Class<?>) innertype)) {
                        realType = innertype;
                    }
                }
            } else if (type instanceof TypeVariable) {
                TypeVariable ptype = (TypeVariable) type;
                for (AnnotatedType typeVariable : ptype.getAnnotatedBounds()) {
                    if (typeVariable.getType().equals(type) && typeClass.isAssignableFrom((Class<?>) type)) {
                        realType = typeVariable.getType();
                    }
                }
            } else if (type instanceof WildcardType) {
                WildcardType ptype = (WildcardType) type;
                for (Type wildcardType : ptype.getUpperBounds()) {
                    if (wildcardType.equals(type) && typeClass.isAssignableFrom((Class<?>) type)) {
                        realType = wildcardType;
                    }
                }
            }
        }
        if (realType instanceof Class && !((Class) typeClass).isAssignableFrom((Class<?>) realType)) {
            if (!realType.equals(clazz)) {
                realType = getRealType((Class) realType, typeClass);
            }

        }

        return realType;
    }

    public static Set<Class> getAllInnerReturnType(Method method, Set<Class> classSet) {
        if (classSet == null) {
            classSet = new LinkedHashSet<>();
        }

        try {
            Type type = method.getGenericReturnType();
            return getInnerType(type, classSet);
        } catch (Throwable e) {
            //编译不通过，停止
            return classSet;
        }


    }

    public static Class getInnerReturnType(Class clazz) {
        Type type = clazz.getTypeParameters()[0];
        return getInnerType(type);
    }


    public static Set<Class> getInnerType(Type type, Set<Class> classSet) {

        if (type instanceof Class) {
            classSet.add((Class) type);
        } else if (type instanceof ParameterizedType) {
            ParameterizedType ptype = (ParameterizedType) type;
            classSet.add((Class) ptype.getRawType());
            for (Type parameterizedType : ptype.getActualTypeArguments()) {
                if (!parameterizedType.equals(type)) {
                    classSet = getInnerType(parameterizedType, classSet);
                }
            }
        } else if (type instanceof TypeVariable) {
            TypeVariable ptype = (TypeVariable) type;
            for (AnnotatedType typeVariable : ptype.getAnnotatedBounds()) {
                if (!typeVariable.getType().equals(type)) {
                    classSet = getInnerType(typeVariable.getType(), classSet);
                }
            }
        } else if (type instanceof WildcardType) {
            WildcardType ptype = (WildcardType) type;
            for (Type wildcardType : ptype.getUpperBounds()) {
                if (!wildcardType.equals(type)) {
                    classSet = getInnerType(wildcardType, classSet);
                }
            }
        } else {
            classSet.add(type.getClass());
        }

        return classSet;
    }

    public static Class getInnerType(Type type) {
        return getRealInnerType(type, null);
    }

    public static Class getRealInnerType(Type type, List<Type> types) {
        Class iClass = null;
        if (types == null) {
            types = new ArrayList<>();
        }
        if (type instanceof Class) {
            iClass = (Class) type;
        } else if (type instanceof ParameterizedType &&  ((ParameterizedType) type).getActualTypeArguments().length>0) {
            ParameterizedType ptype = (ParameterizedType) type;
            iClass = getRealInnerType(ptype.getActualTypeArguments()[0], types);
        } else if (type instanceof TypeVariable &&  ((TypeVariable) type).getAnnotatedBounds().length>0) {
            iClass = getRealInnerType(((TypeVariable) type).getAnnotatedBounds()[0].getType(), types);
        } else if (type instanceof WildcardType&&  ((WildcardType) type).getUpperBounds().length>0) {
            iClass = getRealInnerType(((WildcardType) type).getUpperBounds()[0], types);
        } else {
            iClass = type.getClass();
        }
        if (iClass != null && iClass.isArray()) {
            iClass = iClass.getComponentType();
        }

        try {
            //强制装载动态编译类
            if (iClass != null && isDynClass(iClass)) {
                iClass = ClassUtility.loadClass(iClass.getName());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (!types.contains(type)) {
            types.add(type);
        } else {
            iClass = type.getClass();
        }

        return iClass;
    }

    static boolean isDynClass(Class iClass) {
        if (iClass.getClassLoader() != null) {
            ClassLoader classLoader = iClass.getClassLoader();
            if (classLoader.getClass().getName().equals(DynamicClassLoader.class.getName())) {
                return true;
            }
            if (classLoader.getClass().getName().equals(FileClassLoader.class.getName())) {
                return true;
            }
        }
        return false;
    }

}
