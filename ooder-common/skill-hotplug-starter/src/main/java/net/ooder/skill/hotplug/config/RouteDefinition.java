package net.ooder.skill.hotplug.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 路由定义
 */
public class RouteDefinition {

    /**
     * 路径
     */
    private String path;

    /**
     * HTTP方法
     */
    private String method = "GET";

    /**
     * Controller类名
     */
    private String controllerClass;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 参数类型列表
     */
    private String[] parameterTypes;

    /**
     * Consumes (Content-Type)
     */
    private String consumes;

    /**
     * Produces (Accept)
     */
    private String produces;

    /**
     * 参数条件
     */
    private String[] params;

    /**
     * 请求头条件
     */
    private String[] headers;

    /**
     * 从列表解析
     */
    @SuppressWarnings("unchecked")
    public static List<RouteDefinition> fromList(List<Map<String, Object>> list) {
        List<RouteDefinition> routes = new ArrayList<>();
        for (Map<String, Object> data : list) {
            RouteDefinition route = new RouteDefinition();
            route.path = (String) data.get("path");
            route.method = (String) data.getOrDefault("method", "GET");
            route.controllerClass = (String) data.get("controller");
            if (route.controllerClass == null) {
                route.controllerClass = (String) data.get("controllerClass");
            }
            route.methodName = (String) data.get("methodName");

            List<String> params = (List<String>) data.get("parameterTypes");
            if (params != null) {
                route.parameterTypes = params.toArray(new String[0]);
            }

            route.consumes = (String) data.get("consumes");
            route.produces = (String) data.get("produces");

            List<String> paramsList = (List<String>) data.get("params");
            if (paramsList != null) {
                route.params = paramsList.toArray(new String[0]);
            }

            List<String> headersList = (List<String>) data.get("headers");
            if (headersList != null) {
                route.headers = headersList.toArray(new String[0]);
            }

            routes.add(route);
        }
        return routes;
    }

    // Getters and Setters

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getControllerClass() {
        return controllerClass;
    }

    public void setControllerClass(String controllerClass) {
        this.controllerClass = controllerClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(String[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public String getConsumes() {
        return consumes;
    }

    public void setConsumes(String consumes) {
        this.consumes = consumes;
    }

    public String getProduces() {
        return produces;
    }

    public void setProduces(String produces) {
        this.produces = produces;
    }

    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }

    public String[] getHeaders() {
        return headers;
    }

    public void setHeaders(String[] headers) {
        this.headers = headers;
    }

    @Override
    public String toString() {
        return "RouteDefinition{" +
                "path='" + path + '\'' +
                ", method='" + method + '\'' +
                ", controllerClass='" + controllerClass + '\'' +
                ", methodName='" + methodName + '\'' +
                '}';
    }
}
