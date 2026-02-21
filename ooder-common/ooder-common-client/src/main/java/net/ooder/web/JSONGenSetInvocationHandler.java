/**
 * $RCSfile: JSONGenSetInvocationHandler.java,v $
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
package net.ooder.web;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import net.ooder.common.cache.CacheManagerFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.Constants;
import net.ooder.web.util.JSONGenUtil;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 生成Set方法拦截T
 *
 * @author wenzhang
 */
public class JSONGenSetInvocationHandler implements MethodInterceptor, java.io.Serializable {

    private Object josnObj;

    private static Log log = LogFactory.getLog(Constants.COMMON_CONFIGKEY, CacheManagerFactory.class);


    private final static SerializeConfig config = new SerializeConfig();

    static {
        config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
    }


    public JSONGenSetInvocationHandler(Object jsonobj) {
        this.josnObj = jsonobj;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        Object realObj = null;
        if (josnObj != null) {
            try {
                if (method.getName().startsWith("get") ||
                        (method.getName().startsWith("is") &&
                                (method.getGenericReturnType().equals(boolean.class)
                                        || method.getGenericReturnType().equals(Boolean.class)))
                                && (args == null || args.length == 0)) {

                    Class iClass = JSONGenUtil.getInnerReturnType(method);

                    Method proxyMethod = josnObj.getClass().getDeclaredMethod(method.getName(), null);
                    realObj = proxyMethod.invoke(josnObj, null);

                    if (realObj instanceof JSONObject) {

                        realObj = ((JSONObject) realObj).toJavaObject(JSONGenUtil.fillSetMethod(iClass));

                    } else if (realObj instanceof List) {

                        Class jsonClass = JSONGenUtil.fillSetMethod(iClass);
                        if (!jsonClass.equals(iClass)) {
                            try {
                                List josnobj = JSONArray.parseArray(JSONArray.toJSONString(realObj, config), jsonClass);
                                List rmObj = new ArrayList();
                                for (Object json : josnobj) {
                                    Object proxyObj = null;
                                    if (json != null) {
                                        proxyObj = Enhancer.create(Object.class/* superClass */,
                                                new Class[]{iClass} /* interface to implement */,
                                                new JSONGenSetInvocationHandler(json)/* callbackMethod to proxy real call */
                                        );
                                    }
                                    rmObj.add(proxyObj);
                                    realObj = rmObj;
                                }
                            } catch (Throwable e) {
                                log.warn(e);
                                realObj = JSONArray.parseArray(JSONArray.toJSONString(realObj, config), iClass);

                            }


                        } else {
                            realObj = JSONArray.parseArray(JSONArray.toJSONString(realObj, config), iClass);
                        }

                    }

                } else if (method.getName().startsWith("set")) {
                    Method proxyMethod = josnObj.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
                    realObj = proxyMethod.invoke(josnObj, args);

                } else {
                    realObj = proxy.invokeSuper(obj, args);
                }
            } catch (Throwable e) {
                log.error(e);

            }
        } else {
            log.error("Data error, proxyJSON jsonObj is null!");
        }


        return realObj;

    }

    @Override
    public String toString() {
        if (josnObj != null) {
            JSONObject.toJSONString(josnObj, config);
        }
        return this.toString() + " json is null!";
    }

    public Object getJosnObj() {
        return josnObj;
    }

    public void setJosnObj(Object josnObj) {
        this.josnObj = josnObj;
    }
}
