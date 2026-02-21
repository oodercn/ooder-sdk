/**
 * $RCSfile: DefaultFunction.java,v $
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
package net.ooder.esb.expression;

import net.ooder.common.expression.ParseException;
import net.ooder.common.expression.function.Function;
import net.ooder.common.util.StringUtility;
import net.ooder.web.ConstructorBean;

import java.lang.reflect.Constructor;
import java.util.Stack;

/**
 * 总线注入的Abstract实现
 *
 * @author Administrator
 */
public class DefaultFunction extends Function {


    private Constructor constructor;
    private String expressStr;
    private Class clazz;

    public DefaultFunction(Class clazz, Object[] objs) {
        Class[] types = new Class[objs.length];
        this.clazz = clazz;
        numberOfParameters = objs.length;
        boolean isNull = false;
        for (int f = 0; objs.length > f; f++) {
            if (objs[f] != null) {
                types[f] = objs[f].getClass();
            } else {
                isNull = true;
                types[f] = null;
            }
        }
        ;
        if (isNull) {
            Constructor[] constructors = clazz.getDeclaredConstructors();
            boolean isType = true;
            for (int k = 0; k < constructors.length; k++) {
                Constructor constructor = constructors[k];
                if (constructor.getParameterTypes().length == numberOfParameters) {
                    for (int f = 0; objs.length > f; f++) {
                        Class type = constructor.getParameterTypes()[f];
                        if (objs[f] != null && !type.isAssignableFrom(objs[f].getClass())) {
                            isType = false;
                        }
                    }
                    if (isType) {
                        this.constructor = constructor;
                        return;
                    }
                }
            }
        } else {
            try {
                constructor = clazz.getConstructor(types);
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {

                e.printStackTrace();
            }
        }


    }

    /***
     * 默认 Function 类的包装实体
     * @param clazz 要包装的对象
     * @param expressStr 使用的表达式:示例[GetClientService("net.ooder.iot.api.AdminAPI","http://service.tujiasmart.com:82")]
     */
    public DefaultFunction(Class clazz, String expressStr) {
        Constructor[] constructors = clazz.getDeclaredConstructors();
        this.clazz = clazz;
        //只有一个构造函数或者没有表达式，默认取第一个构造函数且取第一个构造函数的参数个数.
        if (constructors.length < 2 || expressStr == null) {
            constructor = constructors[0];
            numberOfParameters = constructors[0].getParameterTypes().length;
        } else {
            numberOfParameters = StringUtility.split(expressStr, ",").length - 1;
            for (int k = 0; k < constructors.length; k++) {
                Constructor constructor = constructors[k];
                if (constructor.getParameterTypes().length == numberOfParameters) {
                    this.constructor = constructor;
                }
            }
        }
    }

    public void run(Stack stack) throws ParseException {
        if (constructor == null) {
            throw new ParseException("[" + this.getClass().getName() + "]没有[" + expressStr + "]方法");
        }
        checkStack(stack);
        Object[] objs = new Object[numberOfParameters];
        for (int k = objs.length; k > 0; k--) {
            objs[k - 1] = stack.pop();
        }
        try {
            ConstructorBean constructorBean = new ConstructorBean(constructor);
            Object obj = constructorBean.invokArr(objs);
//
//            Object obj = JDSExpressionParserManager.invacationExpression(clazz, constructor, objs);
//            value = TypeUtils.cast(allParamsMap.get(paramBean.getParamName()), iClass, null);
            stack.push(obj);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ParseException(e);
        }


    }

}