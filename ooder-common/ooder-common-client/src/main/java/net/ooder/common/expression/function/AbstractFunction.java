/**
 * $RCSfile: AbstractFunction.java,v $
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
package net.ooder.common.expression.function;

import net.ooder.common.expression.ParseException;

import java.lang.reflect.Method;
import java.util.Stack;

/**
 * 总线注入的Abstract实现
 *
 * @author Administrator
 */
public abstract class AbstractFunction<T> extends Function {

    private static final String METHODNAME = "perform";
    public Method method;

    public Method getPerformMethod() {
        return method;

    }

    /***
     * 默认构造函数
     * 读取实现类的所有方法[ Method ] 并找到perform 方法,记录其引用到
     * @see net.ooder.common.expression.function.AbstractFunction#METHODNAME
     * 并记录此方法的参数个数
     */
    public AbstractFunction() {

        Method[] methods = this.getClass().getDeclaredMethods();
        for (int k = 0; k < methods.length; k++) {
            Method method = methods[k];
            if (method.getName().equals(METHODNAME)) {
                this.method = methods[k];
                numberOfParameters = method.getParameterTypes().length;
            }
        }
    }


    public void run(Stack stack) throws ParseException {
        if (method == null) {
            throw new ParseException("[" + this.getClass().getName() + "]需要实现[" + METHODNAME + "]方法");
        }

        checkStack(stack);

        Object[] objs = new Object[method.getParameterTypes().length];
        for (int k = objs.length; k > 0; k--) {
            objs[k - 1] = stack.pop();
        }
        try {
            Object obj = method.invoke(this, objs);
            stack.push(obj);

        } catch (Exception e) {
            e.printStackTrace();
            //throw new ParseException(e.getMessage());
        }
    }

}