/**
 * $RCSfile: DAOInvocationHandler.java,v $
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
package net.ooder.common.database.dao;

import com.alibaba.fastjson.util.TypeUtils;
import net.ooder.common.cache.CacheManagerFactory;
import net.ooder.common.database.metadata.ColInfo;
import net.ooder.common.database.metadata.TableInfo;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.CaselessStringKeyHashMap;
import net.ooder.common.util.Constants;
import net.ooder.common.util.IOUtility;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;

/**
 * 生成Set方法拦截T
 *
 * @author wenzhang
 */
public class DAOInvocationHandler implements MethodInterceptor, java.io.Serializable {

    private final TableInfo tableInfo;
    private Map<String, Object> valueMap;

    private static Log log = LogFactory.getLog(Constants.COMMON_CONFIGKEY, CacheManagerFactory.class);

    public DAOInvocationHandler(TableInfo tableInfo, Map<String, Object> dataMap) {
        this.valueMap = new CaselessStringKeyHashMap();
        valueMap.putAll(dataMap);
        this.tableInfo = tableInfo;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        Object realObj = null;

        if (valueMap != null) {
            try {
                String pkName = tableInfo.getPkName();
                ColInfo colInfo = tableInfo.getCoInfoByName(pkName);
                if (method.getName().equals("getPkValue")) {
                    return valueMap.get(colInfo.getFieldname());
                } else if (method.getName().startsWith("getConfigKey")) {
                    return tableInfo.getConfigKey();
                } else if (method.getName().startsWith("getPkName")) {
                    return colInfo.getFieldname();
                } else if (method.getName().startsWith("getTableName")) {
                    return tableInfo.getName();
                } else if (method.getName().startsWith("setPkValue")) {
                    valueMap.put(colInfo.getFieldname(), args[0]);
                } else if (method.getName().startsWith("get") && !method.getName().equals("get")) {

                    Object valueObj = valueMap.get(getFieldName(method.getName()));
                    if (valueObj != null) {
                        if (method.getReturnType().equals(Reader.class) && !valueObj.getClass().equals(Reader.class)) {
                            valueObj = new StringReader(valueObj.toString());
                        } else if (method.getReturnType().equals(InputStream.class) && !valueObj.getClass().equals(InputStream.class)) {
                            valueObj = new ByteArrayInputStream(IOUtility.toByteArray(valueObj.toString()));
                        } else {
                            valueObj = TypeUtils.castToJavaBean(valueObj, method.getReturnType());
                        }
                    }
                    // OgnlUtil.setProperty(this.getFieldName(method.getName()), valueObj, obj, new HashMap());
                    // return proxy.invoke(obj, null);
                    return valueObj;
                } else if (method.getName().startsWith("set") && !method.getName().equals("set")) {
                    Class objClass = method.getParameterTypes()[0];
                    if (objClass.equals(Reader.class) && !args[0].getClass().equals(Reader.class)) {
                        if (args[0] != null) {
                            Reader reader = new StringReader(args[0].toString());
                            valueMap.put(getFieldName(method.getName()), reader);
                        } else {
                            valueMap.put(getFieldName(method.getName()), args[0]);
                        }
                    } else if (objClass.equals(InputStream.class) && !args[0].getClass().equals(InputStream.class)) {
                        InputStream valueObj = new ByteArrayInputStream(IOUtility.toByteArray(args[0].toString()));
                        valueMap.put(getFieldName(method.getName()), args[0]);
                    } else {
                        valueMap.put(getFieldName(method.getName()), TypeUtils.castToJavaBean(args[0], objClass));
                    }
                } else if (method.getName().equals("clone")) {
                    CaselessStringKeyHashMap newMap = new CaselessStringKeyHashMap();
                    newMap.putAll(valueMap);
                    newMap.put(colInfo.getFieldname(), UUID.randomUUID().toString());
                    return DBBeanUtil.genBean(tableInfo, newMap, obj.getClass());
                } else {
                    realObj = proxy.invoke(valueMap, args);
                }
            } catch (Throwable e) {
                log.error(e);
            }
        } else

        {
            log.error("Data error, proxyJSON jsonObj is null!");
        }


        return realObj;

    }

    String getFieldName(String methodName) {
        String fileName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
        return fileName;
    }
}


