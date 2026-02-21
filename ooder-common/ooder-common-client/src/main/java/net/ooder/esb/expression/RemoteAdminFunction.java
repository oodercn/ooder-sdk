/**
 * $RCSfile: RemoteAdminFunction.java,v $
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

import net.ooder.common.JDSException;
import net.ooder.common.expression.ParseException;
import net.ooder.common.expression.function.AbstractFunction;
import net.ooder.common.util.ClassUtility;
import net.ooder.web.invocation.HttpAdminInvocationHandler;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import net.sf.cglib.proxy.Proxy;


public class RemoteAdminFunction extends AbstractFunction {

    private final String clazzName;
    private final String serverUrl;

    public RemoteAdminFunction(String clazzName, String serverUrl) {
        numberOfParameters = 2;
        this.clazzName = clazzName;
        this.serverUrl = serverUrl;
    }


    public Object perform() throws ParseException {
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
            e.printStackTrace();
            throw new ParseException(e.getMessage());
        }

        HttpAdminInvocationHandler handler = null;
        try {
            handler = new HttpAdminInvocationHandler(ct, serverUrl);
        } catch (JDSException e) {
            e.printStackTrace();
            throw new ParseException(e.getMessage());
        }
        Object service = Proxy.newProxyInstance(interfaces.getClassLoader(), new Class[]{interfaces}, handler);
        return service;

    }

}
