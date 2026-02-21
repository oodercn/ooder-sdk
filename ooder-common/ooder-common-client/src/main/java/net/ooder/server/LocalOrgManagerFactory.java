/**
 * $RCSfile: LocalOrgManagerFactory.java,v $
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
package net.ooder.server;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.ooder.common.JDSException;
import net.ooder.config.UserBean;
import org.apache.http.client.fluent.Async;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.concurrent.FutureCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;


public class LocalOrgManagerFactory extends OrgManagerFactory {


    public static final String APPLICATION_GETALLSYSTEMURL = "/api/sys/GetAllSystemInfo";

    public static final String GetSubSystemInfoURL = "/api/sys/getSubSystemInfo";

    @Override
    public SubSystem getSystemById(String key) {
        try {
            return getRemoteSystem(key);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<SubSystem> getSystems() {
        try {
            return getRemoteAllSystem();
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return null;
    }

    private SubSystem getRemoteSystem(String systemCode) throws JDSException {
        String url = GetSubSystemInfoURL;
        final Request request = Request.Post(UserBean.getInstance().getServerUrl() + url + "?systemCode=" + systemCode);
        Future<Content> future = Async.newInstance().execute(request);
        SubSystem system = null;
        String json = "";
        try {
            json = future.get().asString();
            JSONObject jsonObj = JSONObject.parseObject(json);
            Integer status = Integer.valueOf(jsonObj.get("requestStatus").toString());
            if (status == 0) {
                String data = jsonObj.getString("data");
                system = JSONObject.parseObject(data, SubSystem.class);

            } else {
                throw new JDSException("无法获取系统信息");
            }

        } catch (Exception e) {
            throw new JDSException("无法获取系统信息");
        }

                     return system;
    }


    private List<SubSystem> getRemoteAllSystem() throws JDSException {
        String url = APPLICATION_GETALLSYSTEMURL;
        final Request request = Request.Post(UserBean.getInstance().getServerUrl() + url);
        Future<Content> future = Async.newInstance().execute(request, new FutureCallback<Content>() {
            public void failed(final Exception ex) {
                ex.printStackTrace();
            }

            public void completed(final Content content) {
            }

            public void cancelled() {

            }
        });

        List<SubSystem> systems = new ArrayList<SubSystem>();

        String json = "";
        try {
            json = future.get().asString();
            JSONObject jsonObj = JSONObject.parseObject(json);
            Integer status = Integer.valueOf(jsonObj.get("requestStatus").toString());
            if (status == 0) {
                String data = jsonObj.getString("data");
                List<SubSystem> eisystems = JSONArray.parseArray(data, SubSystem.class);
                for (SubSystem subSystem : eisystems) {
                    systems.add(subSystem);
                }
            } else {
                throw new JDSException("无法获取系统信息");
            }

        } catch (Exception e) {
            throw new JDSException("无法获取系统信息");
        }

        return systems;
    }


}
