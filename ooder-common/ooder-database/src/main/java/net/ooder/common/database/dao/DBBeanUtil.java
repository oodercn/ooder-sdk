/**
 * $RCSfile: DBBeanUtil.java,v $
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

import net.ooder.common.database.metadata.ColInfo;
import net.ooder.common.database.metadata.TableInfo;
import net.ooder.common.database.util.TypeMapping;
import net.ooder.common.util.CaselessStringKeyHashMap;
import net.sf.cglib.core.Signature;
import net.sf.cglib.core.TypeUtils;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InterfaceMaker;
import org.objectweb.asm.Type;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBBeanUtil {

    static Map<String, Class> classMap = new HashMap<String, Class>();
    static final String[] tableFields = new String[]{"tableName", "pkName", "pkValue", "configKey"};
    static final String[] dynType = new String[]{"java.io.Reader", "java.io.InputStream"};

    static <T> T genBean(TableInfo info, CaselessStringKeyHashMap valueMap, Class<T> tClass) {
        Class dbbase = getDefaultBeanClass(info, false);
        T proxyObj = null;
        if (tClass != null && !tClass.equals(DBMap.class)) {
            if (tClass.isInterface()) {
                proxyObj = (T) Enhancer.create(Object.class/* superClass */,
                        new Class[]{dbbase, tClass} /* interface to implement */,
                        new DAOInvocationHandler(info, valueMap)/* callbackMethod to proxy real call */
                );
            } else {
                proxyObj = (T) Enhancer.create(tClass/* superClass */,
                        new Class[]{dbbase} /* interface to implement */,
                        new DAOInvocationHandler(info, valueMap)/* callbackMethod to proxy real call */
                );
            }
        } else {
            proxyObj = (T) Enhancer.create(Object.class/* superClass */,
                    new Class[]{dbbase, DBMap.class} /* interface to implement */,
                    new DAOInvocationHandler(info, valueMap)/* callbackMethod to proxy real call */
            );
        }


        return proxyObj;

    }

    static <T> T genBean(TableInfo info, String uuid, Class<T> tClass) {
        CaselessStringKeyHashMap<String, Object> valueMap = new CaselessStringKeyHashMap<String, Object>();
        if (info.getPkName() != null) {
            valueMap.put(info.getCoInfoByName(info.getPkName()).getFieldname(), uuid);
        }
        return genBean(info, valueMap, tClass);


    }

    ;


    static Class getDefaultBeanClass(TableInfo info, boolean mix) {
        String tableName = info.getName();
        if (mix) {
            tableName = tableName + "Mix";
        }
        Class clazz = classMap.get(tableName);
        if (clazz == null) {
            clazz = genClass(info, mix);
            classMap.put(tableName, clazz);
        }

        return clazz;
    }

    ;

    static Class genClass(TableInfo info, boolean mix) {
        String key = info.getName();
        Class clazz = null;
        synchronized (key) {
            InterfaceMaker im = new InterfaceMaker();
            List<ColInfo> colInfos = info.getColList();

            for (ColInfo colInfo : colInfos) {

                String objType = TypeMapping.getMappedType(colInfo);

                if (objType == null || Arrays.asList(dynType).contains(objType)) {
                    objType = String.class.getName();
                }

                Type[] nullargumentTypes = new Type[0];
                Type retuntype = TypeUtils.getType(objType);
                Signature getSig = new Signature(getGetMethod(colInfo.getFieldname()), retuntype, nullargumentTypes);
                im.add(getSig, null);
                Type[] paramsargumentTypes = new Type[1];
                paramsargumentTypes[0] = TypeUtils.getType(objType);
                Signature setSig = new Signature(getSetMethod(colInfo.getFieldname()), Type.VOID_TYPE, paramsargumentTypes);
                im.add(setSig, null);
            }
            if (mix) {
                for (String fieldName : tableFields) {
                    Type[] nullargumentTypes = new Type[0];
                    Signature getSig = new Signature(getGetMethod(fieldName), TypeUtils.getType(String.class.getName()), nullargumentTypes);
                    im.add(getSig, null);
                    Type[] paramsargumentTypes = new Type[1];
                    paramsargumentTypes[0] = TypeUtils.getType(String.class.getName());
                    Signature setSig = new Signature(getSetMethod(fieldName), Type.VOID_TYPE, paramsargumentTypes);
                    im.add(setSig, null);
                }


            }
            clazz = im.create();
            return clazz;
        }

    }

    static String getGetMethod(String fileName) {
        String getMethodName = "get" + fileName.substring(0, 1).toUpperCase();
        getMethodName = getMethodName + fileName.substring(1, fileName.length());
        return getMethodName;
    }

    static String getSetMethod(String fileName) {
        String getMethodName = "set" + fileName.substring(0, 1).toUpperCase();
        getMethodName = getMethodName + fileName.substring(1, fileName.length());
        return getMethodName;
    }
}


