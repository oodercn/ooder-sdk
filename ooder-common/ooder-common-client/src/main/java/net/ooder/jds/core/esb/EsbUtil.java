/**
 * $RCSfile: EsbUtil.java,v $
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
package net.ooder.jds.core.esb;

import net.ooder.common.util.ClassUtility;
import net.ooder.common.util.IOUtility;
import net.ooder.context.JDSActionContext;
import net.ooder.esb.config.manager.EsbBeanFactory;
import net.ooder.esb.config.manager.ExpressionTempBean;
import net.ooder.esb.config.manager.ServiceBean;
import net.ooder.esb.util.EsbFactory;
import net.ooder.jds.core.esb.task.ExcuteExpression;
import net.ooder.jds.core.esb.task.ExcuteObj;
import net.ooder.web.RemoteConnectionManager;
import ognl.OgnlContext;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

public class EsbUtil {

    /**
     * 获取指定名称的HTTP参数
     *
     * @return
     */


    public static Object parExpression(String expression) {
        synchronized (expression) {
            return parExpression(expression, Object.class);
        }
   }


    /**
     * 批量执行解析任务
     *
     * @param expressions
     * @return
     */
    public static List<ExcuteObj> beathParExpressions(String... expressions) {
        List<ExcuteObj> result = new ArrayList<>();
        List<ExcuteObj> objs = new ArrayList<ExcuteObj>();
        for (String expression : expressions) {
            ExcuteObj obj = new ExcuteObj(expression);
            objs.add(obj);
        }
        return parExpression(objs);
    }

    public static List<ExcuteObj> parExpression(List<ExcuteObj> ExcuteObj) {
        List<ExcuteObj> result = new ArrayList<>();
        List<ExcuteExpression<ExcuteObj>> tasks = new ArrayList<ExcuteExpression<ExcuteObj>>();
        for (ExcuteObj obj : ExcuteObj) {
            ExcuteExpression task = new ExcuteExpression(obj);
            tasks.add(task);
        }

        List<Future<ExcuteObj>> futures = null;
        try {
            futures = RemoteConnectionManager.getConntctionService("ExeExpression").invokeAll(tasks);
            for (Future<ExcuteObj> resultFuture : futures) {
                try {
                    result.add(resultFuture.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 执行表达式
     *
     * @param expression
     * @return
     */
    public static <T> T parExpression(String expression, Class<? extends T> clazz) {
        synchronized (expression) {
            T obj = (T) EsbFactory.par(expression, clazz);
            return obj;
        }

    }



    /**
     * 执行表达式
     *
     * @param expression
     * @return
     */
    public static <T> T parExpression(String expression, Map context, Object source, Class<? extends T> clazz) {
        synchronized (expression) {
            T obj = (T) EsbFactory.par(expression, context, source, clazz);
            return obj;
        }

    }

    public static <T> T parExpression(Class<? extends T> clazz) {
        T object = null;
        ExpressionTempBean bean = EsbBeanFactory.getInstance().getDefaultServiceBean(clazz);
        if (bean != null) {
            String expression = EsbBeanFactory.getInstance().getDefaultServiceBean(clazz).getId();
            object = parExpression("$" + expression, clazz);
        }

        return object;

    }


    public static <T> Class<? extends T>  guessRealClass(Class<? extends T> clazz) {
        Class<? extends T> realClass = clazz;
        ExpressionTempBean bean = EsbBeanFactory.getInstance().getDefaultServiceBean(clazz);
        if (bean!=null){
            try {
                realClass= ClassUtility.loadClass( bean.getClazz());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return realClass;

    }

    static List<ExpressionTempBean> getListServiceBean(String ruleStr, String esbType, Class clazz) {
        List<? extends ServiceBean> list = EsbBeanFactory.getInstance().getAllServiceBeanByEsbKey(esbType);
        List<ExpressionTempBean> filterList = new ArrayList<ExpressionTempBean>();

        for (int k = 0; k < list.size(); k++) {
            ExpressionTempBean bean = (ExpressionTempBean) list.get(k);

            try {
                if (clazz.isAssignableFrom(ClassUtility.loadClass(bean.getClazz()))) {
                    if (bean.getFilter() != null && !bean.getFilter().equals("")) {
                        Pattern rule = Pattern.compile(bean.getFilter());
                        if (rule.matcher(ruleStr).matches()) {
                            filterList.add(bean);
                        }
                    } else {
                        filterList.add(bean);
                    }
                }
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return filterList;
    }

    static void copyStringToFile(String content, File file, String charSet) throws IOException {
        if (charSet == null) {
            charSet = "utf-8";
        }
        final InputStream input = new ByteArrayInputStream(content.getBytes(charSet));
        final FileOutputStream output = new FileOutputStream(file);
        IOUtility.copy(input, output);
        IOUtility.shutdownStream(input);
        IOUtility.shutdownStream(output);
    }

    static Object getProxyInstance(Class clazz, Object eiObj) {
        Object defaultproxy = null;
        Constructor con = null;
        try {
            for (int k = 0; k < clazz.getConstructors().length; k++) {
                Constructor constructor = clazz.getConstructors()[k];
                if (constructor.getParameterTypes().length == 1) {
                    Class clazz1 = constructor.getParameterTypes()[0];
                    if (clazz1.isAssignableFrom(eiObj.getClass())) {
                        con = constructor;
                    }
                }
            }
            if (con != null) {
                defaultproxy = con.newInstance(eiObj);
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return defaultproxy;
    }

}
