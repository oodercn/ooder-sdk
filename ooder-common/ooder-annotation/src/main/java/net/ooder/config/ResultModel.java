package net.ooder.config;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.common.JDSException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ResultModel<T> implements Future<T> {

    public int requestStatus;

    protected String token;

    protected  String title;

    protected  String message;

    private T data = null;

    public Map<String, Object> ctx;

    @JSONField(serialize = false)
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public T getData() {
        return data;
    }


    public void execute() {
        new Thread() {
            @Override
            public void run() {
                try {
                    ResultModel.this.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();

    }

    public void setData(T data) {
        this.data = data;
    }

    public ResultModel() {
        this.requestStatus = 0;
    }


    public void addCtx(String name, Object value) {
        if (ctx == null) {
            ctx = new HashMap<>();
        }
        ctx.put(name, value);
    }

    public Map<String, Object> getCtx() {
        return ctx;
    }

    public void setCtx(Map<String, Object> ctx) {
        this.ctx = ctx;
    }

    public int getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(int requestStatus) {
        this.requestStatus = requestStatus;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {

        return false;
    }

    @Override
    public T get() throws JDSException {

        return data;
    }

    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return data;
    }

    @JSONField(serialize = false)
    public boolean isCancelled() {
        return false;
    }

    public void setCancelled(Boolean cancelled) {

    }

    public void setDone() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @JSONField(serialize = false)
    public boolean isDone() {
        return false;
    }




}
