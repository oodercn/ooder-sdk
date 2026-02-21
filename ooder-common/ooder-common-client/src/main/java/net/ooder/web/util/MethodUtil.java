/**
 * $RCSfile: MethodUtil.java,v $
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


import com.alibaba.fastjson.util.TypeUtils;
import net.ooder.annotation.AnnotationType;
import net.ooder.annotation.ESDEntity;
import net.ooder.common.util.ClassUtility;
import net.ooder.common.util.StringUtility;
import net.ooder.common.util.java.DynamicClassLoader;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;
import net.ooder.config.TreeListResultModel;
import net.ooder.annotation.ViewType;
import net.ooder.jds.core.User;
import net.ooder.web.ConstructorBean;
import net.ooder.web.RequestMethodBean;
import net.ooder.web.RequestParamBean;
import javassist.*;
import javassist.Modifier;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;


public class MethodUtil {

    private static String ShiftStr = "\n";
    private static String LNStr = "\n      ";

    public static List<RequestParamBean> getParams(Method sourceMethod) throws NotFoundException, ClassNotFoundException {
        List<RequestParamBean> paramBeans = new ArrayList<>();
        paramBeans = getInnerParams(sourceMethod);
        return paramBeans;
    }

    static boolean checkParams(CtClass[] sclasses, CtClass[] ctClasses) {
        if (sclasses.length != ctClasses.length) {
            return false;
        } else {
            int i = 0;
            for (CtClass sclazz : sclasses) {
                CtClass ctClass = ctClasses[i];
                if (!sclazz.getName().equals(ctClass.getName())) {
                    return false;
                }
                i++;
            }
        }

        return true;
    }

    static boolean checkParams(Class<?>[] classes, CtClass[] ctClasses) {
        if (classes.length != ctClasses.length) {
            return false;
        } else {
            int i = 0;
            for (Class clazz : classes) {
                CtClass ctClass = ctClasses[i];
                if (!clazz.getName().equals(ctClass.getName())) {
                    return false;
                }
                i++;
            }
        }

        return true;
    }

    static boolean checkParams(Class<?>[] classes, Class[] otherClass) {
        if (classes.length != otherClass.length) {
            return false;
        } else {
            int i = 0;
            for (Class clazz : classes) {
                Class ctClass = otherClass[i];
                if (!clazz.getName().equals(ctClass.getName())) {
                    return false;
                }
                i++;
            }
        }

        return true;
    }

    public static Method getEqualMethod(Class clazz, String methodName, Class[] params) {
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(methodName) && checkParams(method.getParameterTypes(), params)) {
                return method;
            }
        }
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName) && checkParams(method.getParameterTypes(), params)) {
                return method;
            }
        }

        return null;

    }


    public static Method getEqualMethod(Class clazz, Method ctmethod) {
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(ctmethod.getName()) && checkParams(method.getParameterTypes(), ctmethod.getParameterTypes())) {
                return method;
            }
        }
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(ctmethod.getName()) && checkParams(method.getParameterTypes(), ctmethod.getParameterTypes())) {
                return method;
            }
        }

        return null;

    }

    public static CtMethod getEqualCtMethod(CtClass clazz, CtMethod ctmethod) {
        for (CtMethod method : clazz.getMethods()) {
            try {
                if (method.getName().equals(ctmethod.getName()) && checkParams(method.getParameterTypes(), ctmethod.getParameterTypes())) {
                    return method;
                }
            } catch (NotFoundException e) {
                // e.printStackTrace();
            }
        }
        for (CtMethod method : clazz.getDeclaredMethods()) {
            try {
                if (method.getName().equals(ctmethod.getName()) && checkParams(method.getParameterTypes(), ctmethod.getParameterTypes())) {
                    return method;
                }
            } catch (NotFoundException e) {
                //e.printStackTrace();
            }
        }

        return null;

    }


    public static boolean isGetMethod(Method method) {
        if (method.getName().startsWith("get")
                && !method.getReturnType().equals(void.class)
                && method.getParameterTypes().length == 0) {
            return true;
        } else if (method.getName().startsWith("is") && (method.getReturnType().equals(boolean.class)
                || method.getReturnType().equals(Boolean.class))
                && method.getParameterTypes().length == 0
                && !method.getReturnType().equals(void.class)) {
            return true;
        }
        return false;
    }

    public static boolean isSetMethod(Method method) {
        if (method.getName().startsWith("set") && method.getReturnType().equals(void.class)) {
            return true;
        }
        return false;
    }

    public static Boolean isFieldName(Method method) {
        Boolean isField = false;
        if (method.getName().startsWith("get") && method.getParameterTypes().length == 0
                && !method.getReturnType().equals(void.class)
                ) {
            isField = true;
        } else if (method.getName().startsWith("is") && (method.getReturnType().equals(boolean.class)
                || method.getReturnType().equals(Boolean.class))
                && method.getParameterTypes().length == 0
                && !method.getReturnType().equals(Void.TYPE)) {
            isField = true;
        } else if (method.getName().startsWith("set") && method.getReturnType().equals(void.class)) {
            isField = true;
        }

        return isField;

    }


    public static String getFieldName(Method method) {
        String fieldName = null;
        if (method.getName().startsWith("get")
                //&& method.getParameterTypes().length == 0
                && !method.getReturnType().equals(void.class)
                ) {
            fieldName = method.getName().substring("get".length());
        } else if (method.getName().startsWith("is") && (method.getReturnType().equals(boolean.class)
                || method.getReturnType().equals(Boolean.class))
                //  && method.getParameterTypes().length == 0
                && !method.getReturnType().equals(void.class)) {
            fieldName = method.getName().substring("is".length());
        } else if (method.getName().startsWith("set") && method.getReturnType().equals(void.class)) {
            fieldName = method.getName().substring("set".length());
        }

        if (fieldName != null) {
            fieldName = StringUtility.formatJavaName(fieldName, false);
        } else {
            fieldName = method.getName();
        }


        return fieldName;

    }


    public static CtMethod getCtMethod(Method method) throws NotFoundException {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClazz = pool.getCtClass(method.getDeclaringClass().getName());

        for (CtMethod ctmethod : ctClazz.getDeclaredMethods()) {
            if (ctmethod.getName().equals(method.getName()) && checkParams(method.getParameterTypes(), ctmethod.getParameterTypes())) {
                return ctmethod;
            }
        }
        return null;

    }


    public static boolean checkType(String clazz) {
        if (clazz == null) {
            return false;
        } else if (clazz.equals("")) {
            return false;
        } else if (clazz.equals("int")) {
            return false;
        } else if (clazz.equals("long")) {
            return false;
        } else if (clazz.equals("short")) {
            return false;
        } else if (clazz.equals("long")) {
            return false;
        } else if (clazz.equals("byte")) {
            return false;
        } else if (clazz.equals("[B")) {
            return false;
        } else if (clazz.equals("float")) {
            return false;
        } else if (clazz.equals("double")) {
            return false;
        } else if (clazz.equals("boolean")) {
            return false;
        } else if (clazz.equals("void")) {
            return false;
        } else if (clazz.startsWith("java.lang")) {
            return false;
        }

        return true;
    }


    public static Method getSourceMethod(CtMethod ctmethod) throws ClassNotFoundException, NotFoundException {
        String name = ctmethod.getDeclaringClass().getName();
        Class clazz = ClassUtility.loadClass(name);
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(ctmethod.getName()) && checkParams(method.getParameterTypes(), ctmethod.getParameterTypes())) {
                return method;
            }
        }
        return null;

    }

    private static List<RequestParamBean> getInnerParams(Method sourceMethod) {
        List<RequestParamBean> paramBeans = new ArrayList<>();
        Object[][] annotations = sourceMethod.getParameterAnnotations();
        Type[] parameterTypes = sourceMethod.getGenericParameterTypes();
        Class[] parameterClasses = sourceMethod.getParameterTypes();
        String[] paramNames = new String[parameterTypes.length];
        //JAVA8获取
        TreeMap<Integer, String> sortMap = new TreeMap<Integer, String>();

        Parameter[] parameters = sourceMethod.getParameters();
        for (int i = 0; i < paramNames.length; i++) {
            String paramName = paramNames[i];
            if (paramName == null || paramName.equals("null") || paramName.equals("")) {
                if (parameters != null && parameters.length > i) {
                    paramName = parameters[i].getName();
                }
            }
            Set<Annotation> annotationSet = new HashSet<>();
            for (Object annotationType : annotations[i]) {
                annotationSet.add((Annotation) annotationType);
            }

            RequestParamBean paramBean = new RequestParamBean(paramName, annotationSet, parameterTypes[i], parameterClasses[i]);
            paramBeans.add(paramBean);
        }
        return paramBeans;

    }


    public static List<RequestParamBean> getCtParams(CtMethod ctmethod) throws ClassNotFoundException, NotFoundException {
        Method sourceMethod = getSourceMethod(ctmethod);
        List<RequestParamBean> paramBeans = new ArrayList<>();
        if (sourceMethod != null) {
            paramBeans = getInnerParams(sourceMethod);
        } else {
            MethodInfo methodInfo = ctmethod.getMethodInfo();
            Object[][] annotations = ctmethod.getParameterAnnotations();
            Type[] parameterTypes = sourceMethod.getGenericParameterTypes();
            Class[] parameterClasses = sourceMethod.getParameterTypes();
            String[] paramNames = new String[parameterTypes.length];

            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
            if (codeAttribute != null) {
                LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
                //JAVA8获取
                TreeMap<Integer, String> sortMap = new TreeMap<Integer, String>();
                int pos = Modifier.isStatic(ctmethod.getModifiers()) ? 0 : 1;
                if (attr != null) {
                    for (int i = 0; i < attr.tableLength(); i++) {
                        sortMap.put(attr.index(i), attr.variableName(i));
                        if (attr.index(i) >= pos && attr.index(i) < paramNames.length + pos) {
                            paramNames[attr.index(i) - pos] = attr.variableName(i);
                        }
                    }
                }
            }
            Parameter[] parameters = sourceMethod.getParameters();
            for (int i = 0; i < paramNames.length; i++) {
                String paramName = paramNames[i];
                if (paramName == null || paramName.equals("null") || paramName.equals("")) {
                    if (parameters != null && parameters.length > i) {
                        paramName = parameters[i].getName();
                    }
                }
                Set<Annotation> annotationSet = new HashSet<>();
                for (Object annotationType : annotations[i]) {
                    annotationSet.add((Annotation) annotationType);
                }
                RequestParamBean paramBean = new RequestParamBean(paramName, annotationSet, parameterTypes[i], parameterClasses[i]);
                paramBeans.add(paramBean);
            }
        }
        return paramBeans;


    }

    public static StringBuffer toParamsStr(ConstructorBean constructorBean) throws ClassNotFoundException {
        StringBuffer methodBuffer = new StringBuffer();
        methodBuffer.append(" (");
        List<RequestParamBean> paramBeans = constructorBean.getParamList();
        if (constructorBean.getFristParam() != null && !paramBeans.contains(constructorBean.getFristParam())) {
            RequestParamBean paramBean = constructorBean.getFristParam();
            if (paramBean.getParamType() != null) {
                methodBuffer = toType(constructorBean.getName(), paramBean.getParamClass().getSimpleName(), methodBuffer, paramBean.getParamType(), false);
            } else {
                methodBuffer.append(paramBean.getParamClass().getSimpleName());
            }
            methodBuffer.append(" " + paramBean.getParamName());
            if (paramBeans.size() > 0) {
                methodBuffer.append(",");
            }
        }

        for (RequestParamBean paramBean : paramBeans) {
            if (paramBean.getAnnotations().size() > 0) {
                methodBuffer.append(AnnotationUtil.toAnnotationStr(paramBean.getAnnotations().iterator().next()));
                methodBuffer.append(" ");
            }
            if (paramBean.getParamType() != null) {
                methodBuffer = toType(constructorBean.getName(), paramBean.getParamClass().getSimpleName(), methodBuffer, paramBean.getParamType(), false);
            } else {
                methodBuffer.append(paramBean.getParamClass().getSimpleName());
            }

            methodBuffer.append(" " + paramBean.getParamName());
            methodBuffer.append(",");
        }
        if (methodBuffer.toString().endsWith(",")) {
            methodBuffer.deleteCharAt(methodBuffer.length() - 1);
        }

        methodBuffer.append(")");
        return methodBuffer;
    }

    public static StringBuffer toConstructorStr(ConstructorBean constructorBean) throws ClassNotFoundException {
        StringBuffer methodBuffer = new StringBuffer();
        methodBuffer.append(StringUtility.formatJavaName(constructorBean.getName(), true));
        methodBuffer.append(toParamsStr(constructorBean));
        return methodBuffer;
    }

    public static StringBuffer genJavaMethodStr(RequestMethodBean bean, ViewType viewType, boolean fllClass) {
        StringBuffer methodBuffer = new StringBuffer();
        Class returnType = bean.getReturnClass();
        String returnClassName = String.class.getSimpleName();
        if (returnType != null) {
            if (fllClass) {
                returnClassName = returnType.getName();
            } else {
                returnClassName = returnType.getSimpleName();
            }
        } else if (bean.getReturnClassName() != null) {
            returnClassName = bean.getReturnClassName();
        }

        if (viewType.getDataClass().equals(TreeListResultModel.class)) {
            methodBuffer.append(TreeListResultModel.class.getSimpleName() + "<");
            methodBuffer.append("List<");
            methodBuffer.append(returnClassName);
            methodBuffer.append(">");
        } else if (viewType.getDataClass().equals(ListResultModel.class)) {
            methodBuffer.append(ListResultModel.class.getSimpleName() + "<");
            methodBuffer.append("List<");
            methodBuffer.append(returnClassName);
            methodBuffer.append(">");
        } else {
            methodBuffer.append(ResultModel.class.getSimpleName() + "<");
            methodBuffer.append(returnClassName);
        }

        methodBuffer.append(">");
        methodBuffer.append("get" + StringUtility.formatUrl(bean.getMethodName()));
        methodBuffer.append(" (");
        Set<RequestParamBean> paramBeans = bean.getParamSet();
        for (RequestParamBean paramBean : paramBeans) {
            if (paramBean.getAnnotations().size() > 0) {
                methodBuffer.append(AnnotationUtil.toAnnotationStr(paramBean.getAnnotations().iterator().next()));
                methodBuffer.append(" ");
            }
            methodBuffer = methodBuffer.append(paramBean.getParamClass().getSimpleName());
            methodBuffer.append(" " + paramBean.getParamName());
            methodBuffer.append(",");
        }
        if (methodBuffer.toString().endsWith(",")) {
            methodBuffer.deleteCharAt(methodBuffer.length() - 1);
        }
        methodBuffer.append(")");

        return methodBuffer;
    }


    public static StringBuffer toMethodStr(RequestMethodBean bean, String javaName, boolean requestBody, List<RequestParamBean> paramBeans) throws ClassNotFoundException {
        StringBuffer methodBuffer = new StringBuffer();
        methodBuffer.append(toReturnStr(bean.getSourceMethod(), null, javaName));
        methodBuffer.append(bean.getSourceMethod().getName());
        methodBuffer.append(" (");
        List<RequestParamBean> paramBeanList = new ArrayList<>(bean.getParamSet());
        paramBeans = checkParams(paramBeanList, paramBeans);
        for (RequestParamBean paramBean : paramBeans) {
            if (paramBean.getAnnotations().size() > 0) {
                methodBuffer.append(AnnotationUtil.toAnnotationStr(paramBean.getAnnotations().iterator().next()));
                methodBuffer.append(" ");
            }
            try {
                methodBuffer = toType(bean.getName(), javaName, methodBuffer, paramBean.getParamType(), false);
            } catch (ClassNotFoundException e) {
                // Class not found, skip parameter type resolution
            }
            methodBuffer.append(" " + paramBean.getParamName());
            methodBuffer.append(",");
        }
        if (methodBuffer.toString().endsWith(",")) {
            methodBuffer.deleteCharAt(methodBuffer.length() - 1);
        }

        methodBuffer.append(")");
        return methodBuffer;
    }


    public static StringBuffer toMethodProxyStr(RequestMethodBean bean, ViewType viewType, String javaName, String sourceMethodName) throws ClassNotFoundException {
        StringBuffer methodBuffer = new StringBuffer();
        if (sourceMethodName == null || sourceMethodName.equals("")) {
            sourceMethodName = "getProxy()";
        }
        ;
        Method method = bean.getSourceMethod();
        StringBuffer allBuffer = new StringBuffer();
        allBuffer.append(LNStr);
        allBuffer.append(toReturnStr(method, viewType, javaName));
        allBuffer.append("resultModel=new ");
        allBuffer.append(toReturnStr(method, viewType, javaName) + "();");
        allBuffer.append(LNStr);
        Class innerClass = method.getReturnType();
        if (innerClass.equals(Void.TYPE)) {
            methodBuffer.append(sourceMethodName + ".");
            methodBuffer.append(toInnerMethodStr(method));
            methodBuffer.append(";");
        } else if (!ResultModel.class.isAssignableFrom(innerClass)) {
            if (innerClass.isArray() || Collection.class.isAssignableFrom(innerClass)) {
                innerClass = JSONGenUtil.getInnerReturnType(method);
                methodBuffer.append("resultModel=PageUtil.getDefaultPageList(" + sourceMethodName + ".");
                methodBuffer.append(toInnerMethodStr(method));
                methodBuffer.append(",");
                methodBuffer.append(javaName + ".class");
                methodBuffer.append(");");
            } else {
                innerClass = JSONGenUtil.getInnerReturnType(method);
                if (!MethodUtil.checkType(innerClass.getSimpleName()) || innerClass.equals(String.class)) {
                    methodBuffer.append("resultModel.setData(");
                    methodBuffer.append(sourceMethodName + ".");
                    methodBuffer.append(toInnerMethodStr(method));
                    methodBuffer.append(");");
                } else {
                    methodBuffer.append("resultModel.setData(new ");
                    methodBuffer.append(javaName + "(" + sourceMethodName + ".");
                    methodBuffer.append(toInnerMethodStr(method));
                    methodBuffer.append(")");
                    methodBuffer.append(");");
                }


            }
        } else {
            if (ListResultModel.class.isAssignableFrom(innerClass)) {
                Class realClass = JSONGenUtil.getInnerReturnType(method);
                methodBuffer.append("resultModel=PageUtil.changPageList(" + sourceMethodName + ".");
                methodBuffer.append(toInnerMethodStr(method));
                methodBuffer.append(javaName + ".class");
                methodBuffer.append(");");
            } else {
                Class realClass = JSONGenUtil.getInnerReturnType(method);
                methodBuffer.append("resultModel.setData(new ");
                methodBuffer.append(javaName + "(" + sourceMethodName + ".");
                methodBuffer.append(toInnerMethodStr(method));
                methodBuffer.append(".getData()));");

            }
        }

        methodBuffer.append(LNStr);
        if (method.getExceptionTypes().length > 0) {
            methodBuffer = toException(methodBuffer);
        }
        allBuffer.append(methodBuffer);
        allBuffer.append(LNStr);
        allBuffer.append("return resultModel;");
        allBuffer.append(LNStr);
        return allBuffer;


    }

    public static StringBuffer toException(StringBuffer methodBuffer) {
        StringBuffer exceptionBuffer = new StringBuffer();
        exceptionBuffer.append(LNStr);
        exceptionBuffer.append("try{");
        exceptionBuffer.append(LNStr + "  ");
        exceptionBuffer.append(methodBuffer);
        exceptionBuffer.append(LNStr);
        exceptionBuffer.append("} catch (Exception e) {");
        exceptionBuffer.append(LNStr);
        exceptionBuffer.append("  e.printStackTrace();");
        exceptionBuffer.append(LNStr);
        exceptionBuffer.append("}");

//
        return exceptionBuffer;
    }

    public static StringBuffer toInnerMethodStr(CtMethod ctmethod) {
        StringBuffer methodBuffer = new StringBuffer();
        methodBuffer.append(ctmethod.getName());
        methodBuffer.append("(");
        List<RequestParamBean> paramBeans = null;
        try {
            paramBeans = getCtParams(ctmethod);
        } catch (NotFoundException | ClassNotFoundException e) {
            // Parameter resolution failed, continue with empty parameter list
        }
        for (RequestParamBean paramBean : paramBeans) {
            methodBuffer.append(paramBean.getParamName());
            methodBuffer.append(",");
        }
        if (methodBuffer.toString().endsWith(",")) {
            methodBuffer.deleteCharAt(methodBuffer.length() - 1);
        }
        methodBuffer.append(")");
        return methodBuffer;
    }

    public static StringBuffer toInnerMethodStr(Method method) {
        StringBuffer methodBuffer = new StringBuffer();
        methodBuffer.append(method.getName());
        methodBuffer.append("(");
        List<RequestParamBean> paramBeans = null;
        try {
            paramBeans = getParams(method);
        } catch (NotFoundException | ClassNotFoundException e) {
            // Parameter resolution failed, continue with empty parameter list
        }
        for (RequestParamBean paramBean : paramBeans) {
            methodBuffer.append(paramBean.getParamName());
            methodBuffer.append(",");
        }
        if (methodBuffer.toString().endsWith(",")) {
            methodBuffer.deleteCharAt(methodBuffer.length() - 1);
        }
        methodBuffer.append(")");
        return methodBuffer;
    }

    public static StringBuffer toMethodProxyStr(Method method, ViewType viewType, String javaName, String sourceMethodName) {

        if (sourceMethodName == null || sourceMethodName.equals("")) {
            sourceMethodName = "getProxy()";
        }
        ;

        StringBuffer allBuffer = new StringBuffer();
        allBuffer.append(LNStr);
        allBuffer.append(toReturnStr(method, viewType, javaName));
        allBuffer.append("resultModel=new ");
        allBuffer.append(toReturnStr(method, viewType, javaName) + "();");
        allBuffer.append(LNStr);
        StringBuffer methodBuffer = new StringBuffer();
        Class innerClass = method.getReturnType();
        if (innerClass.equals(Void.TYPE)) {
            methodBuffer.append("getProxy().");
            methodBuffer.append(toInnerMethodStr(method));
            methodBuffer.append(";");
        } else if (!ResultModel.class.isAssignableFrom(innerClass)) {
            if (innerClass.isArray() || Collection.class.isAssignableFrom(innerClass)) {
                innerClass = JSONGenUtil.getInnerReturnType(method);
                methodBuffer.append("resultModel=PageUtil.getDefaultPageList(" + sourceMethodName + ".");
                methodBuffer.append(toInnerMethodStr(method));
                methodBuffer.append(",");
                methodBuffer.append(javaName + ".class");
                methodBuffer.append(");");

            } else {
                innerClass = JSONGenUtil.getInnerReturnType(method);
                if (!MethodUtil.checkType(innerClass.getSimpleName()) || innerClass.equals(String.class)) {
                    methodBuffer.append("resultModel.setData(");
                    methodBuffer.append(sourceMethodName + ".");
                    methodBuffer.append(toInnerMethodStr(method));
                    methodBuffer.append(");");
                } else {
                    methodBuffer.append("resultModel.setData(new ");
                    methodBuffer.append(javaName + "(" + sourceMethodName + ".");
                    methodBuffer.append(toInnerMethodStr(method));
                    methodBuffer.append(")");
                    methodBuffer.append(");");
                }
            }
        } else {
            if (ListResultModel.class.isAssignableFrom(innerClass)) {
                methodBuffer.append("resultModel=PageUtil.changPageList(" + sourceMethodName + ".");
                methodBuffer.append(toInnerMethodStr(method));
                methodBuffer.append(",");
                methodBuffer.append(javaName + ".class");
                methodBuffer.append(");");
            } else {
                methodBuffer.append("resultModel.setData(new ");
                methodBuffer.append(javaName + "(" + sourceMethodName + ".");
                methodBuffer.append(toInnerMethodStr(method));
                methodBuffer.append(".getData()));");
            }
        }

        methodBuffer.append(LNStr);
        if (method.getExceptionTypes().length > 0) {
            methodBuffer = toException(methodBuffer);
        }
        allBuffer.append(methodBuffer);
        allBuffer.append(LNStr);
        allBuffer.append("return resultModel;");
        allBuffer.append(LNStr);
        return allBuffer;
    }

    public static StringBuffer toFieldStr(Method method, String javaName, String fieldName) {
        StringBuffer methodBuffer = new StringBuffer();
        methodBuffer.append(toReturnStr(method, null, javaName));
        methodBuffer.append(" " + fieldName);
        methodBuffer.append(";");
        return methodBuffer;
    }


    static List<RequestParamBean> checkParams(List<RequestParamBean> sourceParams, List<RequestParamBean> otherParams) {
        List<RequestParamBean> paramBeans = new ArrayList<>();
        for (RequestParamBean paramBean : otherParams) {
            RequestParamBean requestParamBean = getParams(sourceParams, paramBean.getParamName());
            if (requestParamBean != null) {
                paramBeans.add(requestParamBean);
            } else {
                paramBeans.add(paramBean);
            }
        }
        return paramBeans;
    }

    static RequestParamBean getParams(List<RequestParamBean> sourceParams, String paramName) {
        for (RequestParamBean paramBean : sourceParams) {
            if (paramBean.getParamName().equals(paramName)) {
                return paramBean;
            }
        }
        return null;
    }


    public static StringBuffer toMethodStr(Method method, ViewType viewType, String javaName, boolean requestBody, List<RequestParamBean> paramBeans) {
        StringBuffer methodBuffer = new StringBuffer();
        methodBuffer.append(toReturnStr(method, viewType, javaName));
        methodBuffer.append(" " + method.getName());
        methodBuffer.append(" (");
        try {
            paramBeans = checkParams(getParams(method), paramBeans);
            for (RequestParamBean paramBean : paramBeans) {
                if (paramBean != null && paramBean.getParamClass() != null) {
                    if (paramBean.getAnnotations().size() > 0) {
                        methodBuffer.append(AnnotationUtil.toAnnotationStr(paramBean.getAnnotations().iterator().next()));
                        methodBuffer.append(" ");
                    } else if (requestBody && checkType(paramBean.getParamClass().getName())) {
                        methodBuffer.append("@RequestBody ");
                    }

                    methodBuffer = toType(method.getName(), paramBean.getParamClass().getSimpleName(), methodBuffer, paramBean.getParamType(), false);
                    methodBuffer.append(" " + paramBean.getParamName());
                    methodBuffer.append(",");
                }
            }

        } catch (NotFoundException | ClassNotFoundException e) {
            // Type resolution failed, continue with current buffer state
        }


        if (methodBuffer.toString().endsWith(",")) {
            methodBuffer.deleteCharAt(methodBuffer.length() - 1);
        }
        methodBuffer.append(")");
        return methodBuffer;
    }

    public static StringBuffer toReturnPrxoyStr(Method method, String javaName) {
        StringBuffer methodBuffer = new StringBuffer();
        Type type = method.getGenericReturnType();
        Class clazz = method.getReturnType();
        if (!ResultModel.class.isAssignableFrom(clazz)) {
            if (clazz.isArray() || Collection.class.isAssignableFrom(clazz)) {
                methodBuffer.append(ListResultModel.class.getSimpleName() + "<");
            } else {
                methodBuffer.append(ResultModel.class.getSimpleName() + "<");
            }
        }
        try {
            String methodName = StringUtility.formatJavaName(method.getName(), true);
            methodBuffer = toType(methodName, javaName, methodBuffer, type, true);
            if (!ResultModel.class.isAssignableFrom(clazz)) {
                methodBuffer.append(">");
            }
            methodBuffer.append(" ");

        } catch (ClassNotFoundException e) {
            // Return type resolution failed, continue with current buffer state
        }
        return methodBuffer;
    }


    public static StringBuffer toReturnStr(Method method, ViewType viewType, String javaName) {
        StringBuffer methodBuffer = new StringBuffer();
        Type type = method.getGenericReturnType();
        Class clazz = method.getReturnType();
        if (!ResultModel.class.isAssignableFrom(clazz)) {
            if (clazz.isArray() || Collection.class.isAssignableFrom(clazz)) {
                if (viewType != null && (viewType.equals(ViewType.NAVTREE) || viewType.equals(ViewType.TREE))) {
                    methodBuffer.append(TreeListResultModel.class.getSimpleName() + "<");
                } else {
                    methodBuffer.append(ListResultModel.class.getSimpleName() + "<");
                }
            } else {
                methodBuffer.append(ResultModel.class.getSimpleName() + "<");
            }
        }


        try {
            methodBuffer = toType(method.getName(), javaName, methodBuffer, type, true);
            if (!ResultModel.class.isAssignableFrom(clazz)) {
                methodBuffer.append(">");
            }
            methodBuffer.append(" ");

        } catch (ClassNotFoundException e) {
            // Return type resolution failed, continue with current buffer state
        }
        return methodBuffer;
    }


    public ListResultModel<List<User>> test(@RequestBody User arg0, Map<String, User> arg1) throws Exception {
        ListResultModel<List<User>> resultModel = new ListResultModel<List<User>>();

        return resultModel;

    }

    public static StringBuffer toWildcardType(StringBuffer methodBuffer, WildcardType type) throws ClassNotFoundException {

        if (type.getTypeName().equals("?")) {
            methodBuffer.append(type.getTypeName());
        } else if (type.getTypeName().startsWith("?") && type.getUpperBounds().length > 0) {
            methodBuffer.append("? extends ");
            if (type.getUpperBounds()[0] instanceof Class) {
                methodBuffer.append(((Class) type.getUpperBounds()[0]).getSimpleName());
            } else {
                methodBuffer.append((type.getUpperBounds()[0]).getTypeName());
            }

        }
        return methodBuffer;
    }


    public static StringBuffer toType(String methodName, String javaName, StringBuffer methodBuffer, Type type, Boolean isProxy) throws ClassNotFoundException {
        if (type.equals(Void.TYPE)) {
            methodBuffer.append(Boolean.class.getSimpleName());
        } else if (type instanceof ParameterizedType) {
            methodBuffer = toTypeStr(methodName, javaName, methodBuffer, (ParameterizedType) type);
        } else if (type instanceof TypeVariable) {
            TypeVariable typev = (TypeVariable) type;
            if (typev.getAnnotatedBounds().length > 0) {
                methodBuffer.append(typev.getAnnotatedBounds()[0].getType());
            } else {
                methodBuffer.append(typev.getName());
            }

        } else if (type instanceof WildcardType) {
            methodBuffer = toWildcardType(methodBuffer, (WildcardType) type);
        } else if (TypeUtils.loadClass(type.getTypeName()) != null && (type instanceof Class)) {
            ClassLoader classloader = TypeUtils.loadClass(type.getTypeName()).getClassLoader();
            ESDEntity entity = AnnotationUtil.getClassAnnotation((Class) type, ESDEntity.class);
            if (((classloader instanceof DynamicClassLoader) || entity != null) && isProxy) {
                methodBuffer.append(javaName);
            } else {
                methodBuffer.append(TypeUtils.loadClass(type.getTypeName()).getSimpleName());
            }

        } else {
            Class typeClass = TypeUtils.loadClass(type.getTypeName());
            if (typeClass == null) {
                try {
                    typeClass = ClassUtility.loadClass(type.getTypeName());
                } catch (Throwable e) {
                    //e.printStackTrace();
                }
            }
            if (typeClass != null) {
                methodBuffer.append(typeClass.getSimpleName());
            } else {
                methodBuffer.append(type.getTypeName());
            }
        }
        return methodBuffer;
    }

    public static Set<String> getAllAnnotationClass(Annotation annotation, Set<String> classes) throws ClassNotFoundException {
        Class annClass = annotation.annotationType();
        if (!classes.contains(annClass.getName())
                && checkType(annClass.getName())) {
            while (annClass.isArray()) {
                annClass = annClass.getComponentType();
            }
            classes.add(annClass.getName());
            classes = getAllClasses(annClass, classes);
        }
        return classes;

    }

    public static Set<String> getAllImports(Set<Class> clazzs, Set<String> imports) throws ClassNotFoundException {
        for (Class clazz : clazzs) {
            imports = getAllClasses(clazz, imports);
        }
        imports = filterImports(imports);
        return imports;
    }


    public static Set<String> getAllImports(Class clazz, Set<String> imports) throws ClassNotFoundException {
        if (clazz != null) {
            imports = getAllClasses(clazz, imports);
            imports.add(clazz.getName());
            imports = filterImports(imports);
        }
        return imports;
    }

    static Set<String> filterImports(Set<String> imports) {
        Set<String> simpleImports = new LinkedHashSet();
        for (String className : imports) {
            if (className != null && className.indexOf(".") > -1) {
                if (!className.endsWith("*")) {
                    String packageName = className.substring(0, className.lastIndexOf("."));
                    className = packageName + ".*";
                }
                if (!simpleImports.contains(className)) {
                    simpleImports.add(className);
                }
            }
        }
        return simpleImports;

    }

    static Set<String> getAllClasses(Class clazz, Set<String> classes) throws ClassNotFoundException {

        if (classes == null) {
            classes = new LinkedHashSet<>();
        }

        if (clazz == null) {
            return classes;
        }


        while (clazz.isArray()) {
            clazz = clazz.getComponentType();
        }


        AnnotationType annotationType = (AnnotationType) clazz.getAnnotation(AnnotationType.class);
        if (annotationType != null) {
            classes.add(annotationType.clazz().getName());
        }

        TypeVariable[] types = clazz.getTypeParameters();
        for (TypeVariable type : types) {
            classes = getAllClasses(type, classes);
        }

        for (Annotation annotation : clazz.getAnnotations()) {
            classes = getAllAnnotationClass(annotation, classes);
        }


        Field[] declaredfields = clazz.getDeclaredFields();
        for (Field field : declaredfields) {
            Set<Class> typeClasses = new LinkedHashSet<>();
            typeClasses = JSONGenUtil.getAllInnerReturnType(field, typeClasses);
            typeClasses.add(field.getType());

            for (Class typeClass : typeClasses) {
                while (typeClass.isArray()) {
                    typeClass = typeClass.getComponentType();
                }

                if (!classes.contains(typeClass.getName())
                        && checkType(typeClass.getName())) {
                    classes.add(typeClass.getName());
                }
            }

            for (Annotation annotation : field.getAnnotations()) {
                classes = getAllAnnotationClass(annotation, classes);
            }
            classes = getAllClasses(field.getGenericType(), classes);
        }

        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            List<Class> typeClasses = new ArrayList<>();
            typeClasses.add(JSONGenUtil.getInnerReturnType(field));
            typeClasses.add(field.getType());

            for (Class typeClass : typeClasses) {
                while (typeClass.isArray()) {
                    typeClass = typeClass.getComponentType();
                }
                if (!classes.contains(typeClass.getName())
                        && checkType(typeClass.getName())) {
                    classes.add(typeClass.getName());
                }
            }
            for (Annotation annotation : field.getAnnotations()) {
                classes = getAllAnnotationClass(annotation, classes);
            }
            classes = getAllClasses(field.getGenericType(), classes);
        }

        Method[] declaredmethods = clazz.getDeclaredMethods();
        for (Method method : declaredmethods) {
            Set<Class> typeClasses = new LinkedHashSet<>();
            typeClasses = JSONGenUtil.getAllInnerReturnType(method, typeClasses);
            typeClasses.add(method.getReturnType());
            for (Class typeClass : typeClasses) {
                while (typeClass.isArray()) {
                    typeClass = typeClass.getComponentType();
                }
                if (!classes.contains(typeClass.getName())
                        && checkType(typeClass.getName())) {

                    classes.add(typeClass.getName());
                }
            }
            for (Annotation annotation : method.getAnnotations()) {
                classes = getAllAnnotationClass(annotation, classes);
            }


            for (Type type : method.getParameterTypes()) {
                classes = getAllClasses(type, classes);
            }

            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            for (Annotation[] annotations : parameterAnnotations) {
                for (Annotation annotation : annotations) {
                    classes = getAllAnnotationClass(annotation, classes);
                }
            }
            classes = getAllClasses(method.getGenericReturnType(), classes);
        }


        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            for (Annotation annotation : method.getAnnotations()) {
                Class annClass = annotation.annotationType();
                if (!classes.contains(annClass.getName())
                        && checkType(annClass.getName())) {
                    classes.add(annClass.getName());
                    classes = getAllClasses(annClass, classes);
                }
            }


            Set<Class> typeClasses = new LinkedHashSet<>();
            typeClasses = JSONGenUtil.getAllInnerReturnType(method, typeClasses);
            typeClasses.add(method.getReturnType());
            for (Class typeClass : typeClasses) {
                while (typeClass.isArray()) {
                    typeClass = typeClass.getComponentType();
                }
                if (!classes.contains(typeClass.getName())
                        && checkType(typeClass.getName())) {
                    classes.add(typeClass.getName());
                }
            }

            for (Type type : method.getParameterTypes()) {
                classes = getAllClasses(type, classes);
            }

            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            for (Annotation[] annotations : parameterAnnotations) {
                for (Annotation annotation : annotations) {
                    Class annClass = annotation.annotationType();
                    if (!classes.contains(annClass.getName())
                            && checkType(annClass.getName())) {
                        classes.add(annClass.getName());
                        classes = getAllClasses(annClass, classes);
                    }
                }
            }


            classes = getAllClasses(method.getGenericReturnType(), classes);
        }


        return classes;
    }


    static Set<String> getAllClasses(Type type, Set<String> classes) throws ClassNotFoundException {

        if (type instanceof ParameterizedType) {
            ParameterizedType ptype = (ParameterizedType) type;
            if (!classes.contains(ptype.getRawType().getTypeName())
                    && checkType(ptype.getRawType().getTypeName())) {
                classes.add(ptype.getRawType().getTypeName());
            }

        } else if (type instanceof TypeVariable) {
            if (type.getTypeName().startsWith("?") && ((TypeVariable) type).getAnnotatedBounds().length > 0) {
                AnnotatedType[] innerTypes = ((TypeVariable) type).getAnnotatedBounds();
                for (AnnotatedType innerType : innerTypes) {
                    if (!classes.contains(innerType.getType().getTypeName())
                            && checkType(innerType.getType().getTypeName())) {
                        classes.add(innerType.getType().getTypeName());
                    }
                }
            }

        } else if (type instanceof WildcardType) {
            if (type.getTypeName().startsWith("?") && ((WildcardType) type).getUpperBounds().length > 0) {
                Type[] innerTypes = ((WildcardType) type).getUpperBounds();
                for (Type innerType : innerTypes) {
                    if (!classes.contains(innerType.getTypeName())
                            && checkType(innerType.getTypeName())) {
                        classes.add(innerType.getTypeName());
                    }
                }
            }
        } else {
            if (type instanceof Class) {
                Class typeClass = (Class) type;
                if (typeClass.isArray()) {
                    typeClass = typeClass.getComponentType();
                }
                if (!classes.contains(typeClass.getName())
                        && checkType(typeClass.getName())) {
                    classes.add(typeClass.getName());
                }

            } else if (!classes.contains(type.getTypeName())
                    && checkType(type.getTypeName())) {
                classes.add(type.getTypeName());
            }
        }
        return classes;
    }


    public static StringBuffer toTypeStr(String methodName, String javaName, StringBuffer methodBuffer, ParameterizedType type) throws ClassNotFoundException {
        Type rawTye = type.getRawType();
        if (rawTye != null) {
            if (rawTye == Map.class || rawTye == HashMap.class) {
                Type keyType = type.getActualTypeArguments()[0];
                methodBuffer.append(Map.class.getSimpleName());
                methodBuffer.append("<");
                methodBuffer = toType(methodName, javaName, methodBuffer, keyType, true);
                methodBuffer.append(",");
                Type valueType = type.getActualTypeArguments()[1];
                methodBuffer = toType(methodName, javaName, methodBuffer, valueType, true);
                methodBuffer.append(">");
            } else {
                if (ListResultModel.class.isAssignableFrom((Class<?>) rawTye)) {
                    methodBuffer.append(ListResultModel.class.getSimpleName());
                } else if (rawTye == ResultModel.class) {
                    methodBuffer.append(ResultModel.class.getSimpleName());
                } else if (rawTye == TreeListResultModel.class) {
                    methodBuffer.append(TreeListResultModel.class.getSimpleName());
                } else if (rawTye == Set.class
                        || rawTye == HashSet.class //
                        || rawTye == TreeSet.class //
                        || rawTye == Collection.class //
                        || rawTye == List.class //
                        || rawTye == ArrayList.class) {
                    methodBuffer = toType(methodName, javaName, methodBuffer, rawTye, true);
                } else {
                    methodBuffer = toType(methodName, javaName, methodBuffer, rawTye, true);
                }

                if (type.getActualTypeArguments() != null) {
                    methodBuffer.append("<");
                    Type itemType = type.getActualTypeArguments()[0];
                    if (itemType instanceof Class) {
                        methodBuffer.append(javaName);
                    } else {
                        methodBuffer = toType(methodName, javaName, methodBuffer, itemType, false);
                    }
                    methodBuffer.append(">");
                } else {
                    methodBuffer.append(javaName + " ");
                }
            }


        }
        return methodBuffer;
    }


    public static void main(String[] args) {
        try {
            Method method = MethodUtil.class.getMethod("test", new Class[]{User.class, Map.class});
            StringBuffer methodBuffer = MethodUtil.toMethodProxyStr(method, null, "view", null);

            // Test output - method generation test

        } catch (NoSuchMethodException e) {
            // Test method not found, ignore
        }
    }


}
