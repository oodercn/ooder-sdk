/**
 * $RCSfile: ServerBeanManager.java,v $
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
package net.ooder.cluster;


import com.alibaba.fastjson.util.TypeUtils;
import net.ooder.common.JDSConstants;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.server.SubSystem;
import net.sf.cglib.beans.BeanMap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class ServerBeanManager {
    private static final Log logger = LogFactory.getLog(
            JDSConstants.CONFIG_KEY, ServerBeanManager.class);

    private static final String configId = "configid";

    public static Map<String, ServerNodeList> esbBeanListBeanmap = new HashMap<String, ServerNodeList>();

    public static ServerNodeList getEsbBeanList(String configName, SubSystem subSystem) {
        ServerNodeList esbBeanListBean = esbBeanListBeanmap.get(configName);
        if (esbBeanListBean == null || esbBeanListBean.getReload().equals("true")) {
            esbBeanListBean = newEsbBeanList(configName, subSystem);
            esbBeanListBeanmap.put(configName, esbBeanListBean);
        }
        return esbBeanListBean;
    }


    private static ServerNodeList newEsbBeanList(String configName, SubSystem subSystem) {
        ServerServiceConfig config = new ServerServiceConfig(configName);
        ServerNodeList esbbeanlist = new ServerNodeList();
        BeanMap esbbeanListMap = BeanMap.create(esbbeanlist);
        Iterator it = esbbeanListMap.keySet().iterator();
        for (; it.hasNext(); ) {
            String key = (String) it.next();
            Class clazz = esbbeanListMap.getPropertyType(key);
            if (clazz.isAssignableFrom(String.class)) {
                esbbeanListMap.put(key, config.getValue(key));
            } else {
                Map<String, ServerNode> esbBeanMap = new LinkedHashMap<String, ServerNode>();
                String[] esbbeanIds = config.getValues(configId);
                for (int i = 0; esbbeanIds.length > i; i++) {
                    ServerNode eabBean = new ServerNode(subSystem);
                    BeanMap beanmap = BeanMap.create(eabBean);
                    Iterator esbit = beanmap.keySet().iterator();
                    for (; esbit.hasNext(); ) {
                        String esbkey = (String) esbit.next();
                        String value = config.getValue(esbbeanIds[i] + "." + esbkey);

                        if (value != null) {
                            beanmap.put(esbkey, TypeUtils.castToJavaBean(value, beanmap.getPropertyType(esbkey)));
                        }
                    }
                    esbBeanMap.put(esbbeanIds[i], eabBean);
                    // logger.info("Server["+eabBean.getName()+"] all PersonSize="+eabBean.par().size());

                }
                esbbeanListMap.put(key, esbBeanMap);
            }
        }
        return esbbeanlist;

    }
}
