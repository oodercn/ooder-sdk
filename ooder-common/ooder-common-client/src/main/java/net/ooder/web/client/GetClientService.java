/**
 * $RCSfile: GetClientService.java,v $
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
package net.ooder.web.client;

import java.util.Stack;

import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.common.JDSException;
import net.ooder.web.invocation.HttpInvocationHandler;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import net.ooder.common.expression.ParseException;
import net.ooder.common.expression.function.Function;
import net.ooder.common.util.ClassUtility;
import net.sf.cglib.proxy.Proxy;


@EsbBeanAnnotation(id = "GetClientService",
        name = "装载远程应用",
        expressionArr = "GetClientService(\"clazz\",\"serverUrl\")",
        desc = "装载远程应用")

public class GetClientService extends Function {


    public GetClientService() {
        numberOfParameters = 2;
    }


    public Object perform(String clazzName, String serverUrl) throws ParseException {
        ClassPool pool = ClassPool.getDefault();

        Class interfaces = null;
        CtClass ct = null;
        try {
            ct = pool.getCtClass(clazzName);
            String iclassName = ct.getInterfaces()[0].getName();
            interfaces = ClassUtility.loadClass(iclassName);
        } catch (NotFoundException e) {
            e.printStackTrace();
            throw new ParseException(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new ParseException(e.getMessage());
        }


        HttpInvocationHandler handler = null;
        try {
            handler = new HttpInvocationHandler(ct, serverUrl);
        } catch (JDSException e) {
            throw new ParseException(e.getMessage());
        }

        Object service = Proxy.newProxyInstance(interfaces.getClassLoader(), new Class[]{interfaces}, handler);

        return service;

    }
    public void run(Stack stack) throws ParseException {
        checkStack(stack);
        String clazzName = (String) stack.pop();
        String path = (String) stack.pop();
        stack.push(perform(path, clazzName));

    }

}
