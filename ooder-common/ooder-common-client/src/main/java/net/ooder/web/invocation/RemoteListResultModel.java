/**
 * $RCSfile: RemoteListResultModel.java,v $
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
package net.ooder.web.invocation;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.common.JDSConstants;
import net.ooder.common.JDSException;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.config.ErrorListResultModel;
import net.ooder.config.ErrorResultModel;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;
import net.ooder.context.JDSActionContext;
import net.ooder.web.ConnectionLogFactory;
import net.ooder.web.JSONGenSetInvocationHandler;
import net.ooder.web.RemoteConnectionManager;
import net.ooder.web.RuntimeLog;
import net.ooder.web.util.JSONGenUtil;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.http.client.fluent.Async;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class RemoteListResultModel<T extends Collection> extends ListResultModel<T> {
    private final Class tClass;
    private Future<Content> future;
    private Class iClass;

    @JSONField(serialize = false)
    private ResultModel<T> model;
    private String url;

    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, RemoteListResultModel.class);
    private static final String asyncServiceKey = "async";

    @JSONField(name = "data")
    private JSONArray dataObj;

    RemoteListResultModel(String url, String token, Request request, final Class tClass, final Class iClass, Async async) {
        this.iClass = iClass;
        this.tClass = tClass;
        this.url = url;
        this.token = token;
        if (System.getProperty("JDShttpProxy") != null) {
            request.viaProxy(System.getProperty("JDShttpProxy"));
        }

        this.future = async.execute(request, new FutureCallback<Content>() {
            public void failed(final Exception ex) {
                ex.printStackTrace();
            }

            public void completed(final Content content) {
                if (!InputStream.class.isAssignableFrom(iClass)) {
                    String json = content.asString();
                    logger.debug("return " + json);
                    RuntimeLog log = ConnectionLogFactory.getInstance().getLog(token);
                    if (log != null) {
                        log.setBodyJson(json);
                        log.setEndTime(System.currentTimeMillis());
                        log.setTime(log.getEndTime() - log.getStartTime());
                    }

                }
            }

            public void cancelled() {
            }
        });
    }

    @Override
    public void execute() {
        ExecutorService service = RemoteConnectionManager.getConntctionService(asyncServiceKey);
        service.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    RemoteListResultModel.this.get();
                } catch (JDSException e) {
                    logger.error("url==" + url);
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public int getSize() {
        if (size == -1) {
            try {
                T object = this.get();
                if (object != null) {
                    size = (object).size();
                } else {
                    size = 0;
                }

            } catch (JDSException e) {
                //e.printStackTrace();
                size = 0;
            }
        }
        ;
        return size;
    }


    public int getRequestStatus() {
        int status = -1;
        try {
            status = getModel().getRequestStatus();
        } catch (JDSException e) {
            // e.printStackTrace();
        }
        return status;
    }


    @JSONField(serialize = false)
    public ResultModel<T> getModel() throws JDSException {
        if (model == null) {
            this.get();
        }
        return model;
    }

    @JSONField(serialize = false)
    public T getData() {
        T data = null;
        try {
            data = get();
        } catch (JDSException e) {
            this.setRequestStatus(-1);
        }

        return data;
    }

    public Object getDataObj() {
        getData();
        return dataObj;
    }

    @JSONField(serialize = false)
    Future<Content> getFuture() {
        return future;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
    }

    public T get(long timeout, TimeUnit unit) throws JDSException {
        try {

            Content content = future.get();
            if (InputStream.class.isAssignableFrom(iClass) && content.getType().getMimeType().toString().equals(ContentType.APPLICATION_OCTET_STREAM.toString())) {
                return (T) content.asStream();
            } else {
                String modelStr = content.asString();
                return getResultModel(modelStr).getData();
            }
        } catch (InterruptedException e) {
            throw new JDSException("服务器异常", JDSException.HTTPERROR);
        } catch (ExecutionException e) {
            if (e instanceof JDSException) {
                throw (JDSException) e;
            } else {
                throw new JDSException("服务器异常", JDSException.HTTPERROR);
            }

        }

    }

    public T get() throws JDSException {
        try {
            Content content = future.get();
            if (InputStream.class.isAssignableFrom(iClass) && content.getType().getMimeType().toString().equals(ContentType.APPLICATION_OCTET_STREAM.toString())) {
                return (T) content.asStream();
            } else {
                String modelStr = future.get().asString();
                return getResultModel(modelStr).getData();
            }
        } catch (InterruptedException e) {
            throw new JDSException("服务器异常", JDSException.HTTPERROR);
        } catch (ExecutionException e) {
            if (e instanceof JDSException) {
                throw (JDSException) e;
            }
            throw new JDSException("服务器异常", JDSException.HTTPERROR);
        }
    }


    @JSONField(serialize = false)
    public boolean isCancelled() {
        return future.isCancelled();
    }


    @JSONField(serialize = false)
    public boolean isDone() {
        return future.isDone();
    }


    @JSONField(serialize = false)
    ResultModel<T> getResultModel(String modelStr) throws JDSException {

        T obj = null;

        JSONObject jsonObj = JSONObject.parseObject(modelStr);

        if (jsonObj == null || jsonObj.get("requestStatus") == null) {
            throw new JDSException("服务器格式错误" + modelStr, 500);
        }

        if (Integer.valueOf(jsonObj.get("requestStatus").toString()) == 0) {
            this.requestStatus = Integer.valueOf(jsonObj.get("requestStatus").toString());
            Object data = jsonObj.get("data");
            if (data instanceof JSONArray) {
                Class jsonClass = iClass;
                this.dataObj = (JSONArray) data;
                jsonClass = JSONGenUtil.fillSetMethod(iClass);
                if (!jsonClass.equals(iClass)) {
                    List josnobj = JSONArray.parseArray(jsonObj.getString("data"), jsonClass);
                    List rmObj = new ArrayList();
                    for (Object json : josnobj) {
                        Object proxyObj = null;
                        if (json != null) {
                            proxyObj = Enhancer.create(Object.class/* superClass */,
                                    new Class[]{iClass, jsonClass} /* interface to implement */,
                                    new JSONGenSetInvocationHandler(json)/* callbackMethod to proxy real call */
                            );
                        }
                        rmObj.add(proxyObj);
                        obj = (T) rmObj;
                    }

                } else {
                    obj = (T) JSONArray.parseArray(jsonObj.getString("data"), iClass);
                }

                Integer size = jsonObj.getInteger("size");
                if (size != null) {
                    ListResultModel listResultModel = new ListResultModel();
                    listResultModel.setSize(size);
                    JDSActionContext.getActionContext().getContext().put(iClass.getName() + "[JDS_SIZE]", size);
                    model = listResultModel;
                    this.setSize(size);
                } else {
                    model = new ResultModel();
                    this.setSize(dataObj.size());
                }

                if (tClass != null && Set.class.isAssignableFrom(tClass)) {
                    obj = (T) new LinkedHashSet<T>(obj);
                }

                model.setData(obj);

            } else {
                ErrorResultModel errmodel = (ErrorResultModel) JSONObject.parseObject(modelStr, ErrorResultModel.class);
                model = errmodel;
                throw new JDSException("数据返回类型错误！当前方法只允许，数组类型数据！", 9000);
            }

        } else {
            this.setSize(0);
            ErrorListResultModel errmodel = (ErrorListResultModel) JSONObject.parseObject(modelStr, ErrorListResultModel.class);
            model = errmodel;
            throw new JDSException(errmodel.getErrdes(), errmodel.getErrcode());

        }

        return model;
    }

    /**
     * 获得方法描述
     *
     * @param clazz
     * @return
     */
    private T newProxy(Class<T> clazz) {

        MethodInterceptor staticMethodInterceptor = new MethodInterceptor() {
            public Object intercept(Object obj, Method method, Object[] objects, MethodProxy proxy) {
                String name = method.getName();
                Object returnobj = null;
                try {
                    returnobj = method.invoke(returnobj, objects);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                return returnobj;
            }
        };

        T obj = (T) Enhancer.create(Object.class, new Class[]{clazz}, staticMethodInterceptor);
        return obj;

    }

}
