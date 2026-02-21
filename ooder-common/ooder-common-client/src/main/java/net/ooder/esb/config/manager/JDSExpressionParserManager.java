/**
 * $RCSfile: JDSExpressionParserManager.java,v $
 * $Revision: 1.3 $
 * $Date: 2016/06/11 03:49:40 $
 * <p>
 * Copyright (C) 2025 ooder, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of ooder, Inc.
 * Use is subject to license terms.
 */

/**
 * $RCSfile: JDSExpressionParserManager.java,v $
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
package net.ooder.esb.config.manager;

import net.ooder.common.ContextType;
import net.ooder.common.expression.ExpressionParser;
import net.ooder.common.expression.ExpressionParserManager;
import net.ooder.common.expression.function.Function;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.ClassUtility;
import net.ooder.common.util.DateUtility;
import net.ooder.config.JDSConfig;
import net.ooder.esb.config.invocation.AbstractInvocationHandler;
import net.ooder.esb.expression.DefaultFunction;
import net.ooder.esb.expression.EVALFunction;
import net.ooder.esb.expression.RemoteFunction;
import net.ooder.esb.util.ESBConstants;
import net.ooder.esb.util.EsbFactory;
import net.ooder.jds.core.esb.util.ActionContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description:
 * </p>
 * +*-
 * <p>
 * Copyright: ooder Copyright (c) 2025
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 *
 * @version 2.0
 */
public class JDSExpressionParserManager {
    static Log log = LogFactory.getLog(ESBConstants.staticName, JDSExpressionParserManager.class);

    /**
     * 环境上下文
     */
    public static final String CONSTANST_JDS_CONTEXT = "JDS_CONTEXT";

    /**
     * 系统时间
     */
    public static final String CONSTANST_CURRENT_TIME = "CURRENT_TIME";

    /**
     * 当前日期(字符串，如 2004-03-31)
     */
    public static final String CONSTANST_CURRENT_DATE = "CURRENT_DATE";

    public static final String CONSTANST_BOOLEAN_TRUE = "true";

    public static final String OBJECT = "object";

    public static final String CONSTANST_BOOLEAN_FALSE = "false";

    /* 表达式解析器,默认实现为 JEP */
    public static ExpressionParser parser = ExpressionParserManager.getExpressionParser();
    ;
    /**
     * import reference:
     *
     * @see ActionContext#getContext()
     */
    public static Map expressionTypeMap = new HashMap();

    public static Map typeMap = new HashMap();
    public static Map class2Map = new HashMap();
    public static Map fun2Map = new HashMap();
    public static Map name2functionMap = new HashMap();

    private static List<EsbInvacationBean> esbInvacationList = null;
    
    public static ExpressionParser createExpressionParser(Map ctx) {
        if (ctx == null) {
            ctx = new HashMap();
        }
        ExpressionParser parser = ExpressionParserManager.getExpressionParser();
        addConstants(parser, ctx);
        addFunctions(parser);
        if (!ctx.isEmpty()) {
            log.info("ctxInfo:" + ctx);
            // 添加常量
        }

        return parser;
    }

    public static ExpressionParser getExpressionParser(Map ctx) {
        if (ctx == null) {
            ctx = new HashMap();
        }

        if (parser == null) {
            parser = ExpressionParserManager.getExpressionParser();
        }
        addConstants(parser, ctx);
        addFunctions(parser);
        if (!ctx.isEmpty()) {
            log.info("ctxInfo:" + ctx);
            // 添加常量
        }

        return parser;

    }

    /**
     * @param parser
     */
    private static void addConstants(ExpressionParser parser, Map ctx) {
        parser.addVariableAsObject(JDSExpressionParserManager.CONSTANST_JDS_CONTEXT, ctx);
        parser.addVariableAsObject(JDSExpressionParserManager.CONSTANST_CURRENT_TIME, new java.sql.Timestamp(System.currentTimeMillis()));
        parser.addVariableAsObject(JDSExpressionParserManager.CONSTANST_CURRENT_DATE, DateUtility.getCurrentDate());
        parser.addVariableAsObject(JDSExpressionParserManager.CONSTANST_BOOLEAN_TRUE, true);
        parser.addVariableAsObject(JDSExpressionParserManager.OBJECT, new Object());
        parser.addVariableAsObject(JDSExpressionParserManager.CONSTANST_BOOLEAN_FALSE, false);
        // 当前活动实例常量

    }

    public static Object invacationExpression(Class clazz, Constructor constructor, Object[] arguments) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Object obj = null;
        if (esbInvacationList == null) {
            esbInvacationList = new ArrayList<EsbInvacationBean>();
            List<? extends ServiceBean> expressionList = EsbBeanFactory.getInstance().loadAllServiceBean();
            for (int i = 0; i < expressionList.size(); i++) {
                ServiceBean bean = expressionList.get(i);
                if (bean instanceof EsbInvacationBean && !esbInvacationList.contains(bean)) {
                    esbInvacationList.add((EsbInvacationBean) bean);
                }
            }
        }

        for (int k = 0; k < esbInvacationList.size(); k++) {
            EsbInvacationBean esbInvacationBean = esbInvacationList.get(k);
            String filterName = esbInvacationBean.getFilter();
            try {
                Class filterClazz = ClassUtility.loadClass(filterName);
                if (filterClazz.isAssignableFrom(clazz)) {
                    Class invocation = ClassUtility.loadClass(esbInvacationBean.getClazz());
                    if (AbstractInvocationHandler.class.isAssignableFrom(invocation)) {
                        AbstractInvocationHandler handler = (AbstractInvocationHandler) invocation.newInstance();
                        obj = handler.getBean(clazz, constructor.getParameterTypes(), arguments);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (obj == null) {
            if (constructor == null || constructor.getParameterTypes().length == 0) {
                try {
                    obj = clazz.getDeclaredConstructor(null).newInstance(arguments);
                } catch (Exception e) {
                    obj = constructor.newInstance(arguments);
                }
            } else {
                obj = constructor.newInstance(arguments);
            }
        }
        return obj;
    }


    static void fullBean(ExpressionTempBean bean) {
        String className = bean.getMainClass();
        if (className == null) {
            className = bean.getClazz();
        }
        Class clazz = null;
        try {
            clazz = ClassUtility.loadClass(className);
        } catch (Throwable e) {
            log.warn("when EsbBus load static " + className + " not found '" + bean.getId() + "[" + bean.getClazz() + "]'in path '" + bean + "'");
            return;
        }
        try {

            Function function = null;
            if (clazz != null) {

                if (Function.class.isAssignableFrom(clazz)) {
                    /* 因为是 Function 的子类，因此可以直接转换为 SuperClass */
                    function = (Function) ClassUtility.loadClass(className).newInstance();
                    if (bean.getReturntype() != null) {
                        parser.addFunction(bean.getReturntype(), function);
                        Class returnTypeClz = findClassByKey(bean.getReturntype());
                        // class2Map.put(bean.getId(),function.getClass());
                        fun2Map.put(bean.getId(), clazz);
                        typeMap.put(bean.getId(), clazz);
                        expressionTypeMap.put(bean.getId(), returnTypeClz);
                    }
                } else if (bean.getDataType().equals(ContextType.Server) && bean.getServerUrl() != null && !bean.getServerUrl().equals("")) {
                    function = new RemoteFunction(clazz.getName(), bean.getServerUrl());
                    class2Map.put(bean.getId(), clazz);
                    typeMap.put(bean.getId(), clazz);
                    parser.addFunction(clazz.getSimpleName(), function);
                    expressionTypeMap.put(bean.getId(), clazz);
                } else {
                    function = new DefaultFunction(clazz, bean.getExpressionArr());
                    class2Map.put(bean.getId(), clazz);
                    typeMap.put(bean.getId(), clazz);
                    parser.addFunction(clazz.getSimpleName(), function);
                    expressionTypeMap.put(bean.getId(), clazz);

                }
            }



            /* KeyPoint We Save the Expression to EVAL from here */
            Function $function = new EVALFunction(bean.getExpressionArr());
            parser.addFunction("$" + bean.getId(), $function);
            name2functionMap.put(bean.getReturntype(), function);
            name2functionMap.put("$" + bean.getId(), $function);
            name2functionMap.put("get$" + bean.getId(), $function);


        } catch (Exception e) {
//            Map<String, List<? extends ServiceBean>> configListMap = EsbBeanFactory.getInstance().getConfigListMap();
//            Iterator<String> it = configListMap.keySet().iterator();
//            String pathName = "";
//            for (; it.hasNext(); ) {
//                String key = it.next();
//                List eList = configListMap.get(key);
//                if (eList.contains(bean)) {
//                    pathName = key;
//                    continue;
//                }
//            }

            log.warn("when EsbBus load static data not found '" + bean.getId() + "[" + bean.getClazz() + "]", e);
            //e.printStackTrace();
        }


    }


    /***
     * [keyPoint]加载所有静态数据
     *
     * @param reload
     *            是否重载
     * @return map of expression
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public static Map loadStaticAllData(boolean reload) throws InstantiationException, IllegalAccessException, ClassNotFoundException {

        if (parser == null) {
            parser = ExpressionParserManager.getExpressionParser();
        }
        Map functionMap = parser.getFunctionTable();

        Map valueMap = (Map) EsbFactory.getDefauleRoot();

        if (valueMap == null || valueMap.size() == 0 || reload) {
            List<? extends ServiceBean> expressionList = EsbBeanFactory.getInstance().loadAllServiceBean();
            List<ExpressionTempBean> tempBeans = new ArrayList<ExpressionTempBean>();
            for (ServiceBean bean : expressionList) {
                if (bean instanceof ExpressionTempBean) {
                    fullBean((ExpressionTempBean) bean);
                }
            }

        }
        return valueMap;
    }

    /**
     * 内部方法 根据模板ID返回定义实体的类型
     *
     * @param key 服务的名称，亦服务的接口名
     * @return
     * @throws Exception
     */
    private static Class findClassByKey(String key) throws SecurityException, NoSuchMethodException {
        Class clazz = null;
        if (key != null) {

            Map expression = parser.getFunctionTable();

            // 这里一定有,因为在调用此方法前，刚刚 put 进去了 Function 实例.
            if (expression != null && expression.containsKey(key.toLowerCase())) {

                clazz = expression.get(key).getClass();

                try {
                    Method[] methods = clazz.getDeclaredMethods();
                    for (int i = 0; i < methods.length; i++) {
                        Method method = methods[i];
                        // 如果需要返回的类型定义了 [perform] 方法,重新返回 perform 方法的返回类型
                        if (method.getName().equals("perform")) {
                            clazz = method.getReturnType();
                        }
                    }
                } catch (Exception e) {

                }
            }
        }
        return clazz;
    }

    /**
     * [keyPoint] 添加engine_config.xml中配置的 function 类的实例到 Parser 中
     *
     * @param parser 待处理解析器
     */
    private static void addFunctions(ExpressionParser parser) {
        String[] functions = JDSConfig.getChildrenProperties("functions");

        for (int i = 0; i < functions.length; i++) {
            String className = JDSConfig.getValue("functions." + functions[i] + ".class");
            try {
                Function function = (Function) Class.forName(className).newInstance();
                parser.addFunction(functions[i], function);
            } catch (ClassNotFoundException e) {
                log.error("ClassNotFoundException", e);
                e.printStackTrace();
            } catch (IllegalAccessException e1) {
                log.error("IllegalAccessException", e1);
                e1.printStackTrace();
            } catch (InstantiationException e2) {
                log.error("InstantiationException", e2);
                e2.printStackTrace();
            }
        }
    }

    public static String verifyExpression(String expression, List parameterList) {
        Map ctx = new HashMap();
        ExpressionParser parser = getExpressionParser(ctx);
        boolean result = parser.parseExpression(expression);

        return result ? "OK" : parser.getErrorInfo();
    }

    public static Map getExpressionTypeMap() {
        return expressionTypeMap;
    }

    public static Map getClass2Map() {
        return class2Map;
    }

    public static void setClass2Map(Map class2Map) {
        JDSExpressionParserManager.class2Map = class2Map;
    }

    public static Map getFun2Map() {
        return fun2Map;
    }

    public static void setFun2Map(Map fun2Map) {
        JDSExpressionParserManager.fun2Map = fun2Map;
    }

    public static Map getTypeMap() {
        return typeMap;
    }

    public static void setTypeMap(Map typeMap) {
        JDSExpressionParserManager.typeMap = typeMap;
    }

    public static Map getName2functionMap() {
        return name2functionMap;
    }

    public static void setName2functionMap(Map name2functionMap) {
        JDSExpressionParserManager.name2functionMap = name2functionMap;
    }

}
