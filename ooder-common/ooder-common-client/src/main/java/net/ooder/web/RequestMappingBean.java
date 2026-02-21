/**
 * $RCSfile: RequestMappingBean.java,v $
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

import net.ooder.annotation.AnnotationType;
import net.ooder.annotation.CustomBean;
import net.ooder.web.util.AnnotationUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@AnnotationType(clazz = RequestMapping.class)
public class RequestMappingBean implements CustomBean {

    String name;

    String fristValue;

    String parentPath;

    String fristUrl;

    Set<String> value = new HashSet<>();

    Set<String> path;

    Set<RequestMethod> method = new HashSet<>();

    Set<String> params;

    Set<String> headers;

    Set<String> consumes;

    Set<String> produces;


    public RequestMappingBean() {

    }

    public RequestMappingBean(RequestMapping mapping) {
        this(mapping, "");
    }

    public RequestMappingBean(GetMapping mapping) {
        this(mapping, "");
    }

    public RequestMappingBean(PostMapping mapping) {
        this(mapping, "");
    }

    public RequestMappingBean(String pathName, String parentPath) {
        value.add(pathName);
        this.fristValue = pathName;

        if (fristValue == null) {
            fristValue = "";
        }
        if (fristValue.startsWith("/")) {
            fristValue = fristValue.substring(1);
        }

        if (parentPath == null || parentPath.equals("null")) {
            parentPath = "/";
        } else if (!parentPath.endsWith("/")) {
            if (!parentPath.endsWith("/")) {
                parentPath = parentPath + "/";
            }
        }
        this.parentPath = parentPath;

        if (!fristValue.startsWith(parentPath)) {
            this.fristUrl = parentPath + fristValue;
        }


    }

    public RequestMappingBean(PostMapping mapping, String parentPath) {
        if (mapping.value().length > 0) {
            value = new LinkedHashSet<>();
            //this.name = mapping.value()[0];
            this.fristValue = mapping.value()[0];
            for (String valueName : mapping.value()) {
                value.add(valueName);
            }
        } else if (mapping.path().length > 0) {
            value = new LinkedHashSet<>();
            //  this.name = mapping.path()[0];
            this.fristValue = mapping.path()[0];
            for (String pathName : mapping.path()) {
                value.add(pathName);
            }

        } else if (!mapping.name().equals("")) {
            this.fristValue = mapping.name();
            value.add(mapping.name());
        }


        if (mapping.params().length > 0) {
            params = new LinkedHashSet<>();
            for (String value : mapping.params()) {
                params.add(value);
            }
        }
        if (mapping.headers().length > 0) {
            headers = new LinkedHashSet<>();
            for (String value : mapping.headers()) {
                headers.add(value);
            }
        }
        if (mapping.consumes().length > 0) {
            consumes = new LinkedHashSet<>();

            for (String value : mapping.consumes()) {
                consumes.add(value);
            }
        }
        if (mapping.produces().length > 0) {
            produces = new LinkedHashSet<>();
            for (String value : mapping.produces()) {
                produces.add(value);
            }
        }


        if (fristValue == null) {
            fristValue = "";
        }
        if (fristValue.startsWith("/")) {
            fristValue = fristValue.substring(1);
        }

        if (parentPath == null || parentPath.equals("null")) {
            parentPath = "/";
        } else if (!parentPath.endsWith("/")) {
            if (!parentPath.endsWith("/")) {
                parentPath = parentPath + "/";
            }
        }
        this.parentPath = parentPath;
        this.fristUrl = parentPath + fristValue;
        if (!mapping.name().equals("")) {
            this.name = mapping.name();
        }
    }

    public RequestMappingBean(GetMapping mapping, String parentPath) {
        if (mapping.value().length > 0) {
            value = new LinkedHashSet<>();
            //this.name = mapping.value()[0];
            this.fristValue = mapping.value()[0];
            for (String valueName : mapping.value()) {
                value.add(valueName);
            }
        } else if (mapping.path().length > 0) {
            value = new LinkedHashSet<>();
            //  this.name = mapping.path()[0];
            this.fristValue = mapping.path()[0];
            for (String pathName : mapping.path()) {
                value.add(pathName);
            }

        } else if (!mapping.name().equals("")) {
            this.fristValue = mapping.name();
            value.add(mapping.name());
        }


        if (mapping.params().length > 0) {
            params = new LinkedHashSet<>();
            for (String value : mapping.params()) {
                params.add(value);
            }
        }
        if (mapping.headers().length > 0) {
            headers = new LinkedHashSet<>();
            for (String value : mapping.headers()) {
                headers.add(value);
            }
        }
        if (mapping.consumes().length > 0) {
            consumes = new LinkedHashSet<>();

            for (String value : mapping.consumes()) {
                consumes.add(value);
            }
        }
        if (mapping.produces().length > 0) {
            produces = new LinkedHashSet<>();
            for (String value : mapping.produces()) {
                produces.add(value);
            }
        }


        if (fristValue == null) {
            fristValue = "";
        }
        if (fristValue.startsWith("/")) {
            fristValue = fristValue.substring(1);
        }

        if (parentPath == null || parentPath.equals("null")) {
            parentPath = "/";
        } else if (!parentPath.endsWith("/")) {
            if (!parentPath.endsWith("/")) {
                parentPath = parentPath + "/";
            }
        }
        this.parentPath = parentPath;
        this.fristUrl = parentPath + fristValue;
        if (!mapping.name().equals("")) {
            this.name = mapping.name();
        }
    }

    public RequestMappingBean(RequestMapping mapping, String parentPath) {

        if (mapping.value().length > 0) {
            value = new LinkedHashSet<>();
            //this.name = mapping.value()[0];
            this.fristValue = mapping.value()[0];
            for (String valueName : mapping.value()) {
                value.add(valueName);
            }
        } else if (mapping.path().length > 0) {
            value = new LinkedHashSet<>();
            //  this.name = mapping.path()[0];
            this.fristValue = mapping.path()[0];
            for (String pathName : mapping.path()) {
                value.add(pathName);
            }

        } else if (!mapping.name().equals("")) {
            this.fristValue = mapping.name();
            value.add(mapping.name());
        }


        if (mapping.params().length > 0) {
            params = new LinkedHashSet<>();
            for (String value : mapping.params()) {
                params.add(value);
            }
        }
        if (mapping.headers().length > 0) {
            headers = new LinkedHashSet<>();
            for (String value : mapping.headers()) {
                headers.add(value);
            }
        }
        if (mapping.consumes().length > 0) {
            consumes = new LinkedHashSet<>();

            for (String value : mapping.consumes()) {
                consumes.add(value);
            }
        }
        if (mapping.produces().length > 0) {
            produces = new LinkedHashSet<>();
            for (String value : mapping.produces()) {
                produces.add(value);
            }
        }


        if (fristValue == null) {
            fristValue = "";
        }
        if (fristValue.startsWith("/")) {
            fristValue = fristValue.substring(1);
        }

        if (parentPath == null || parentPath.equals("null")) {
            parentPath = "/";
        } else if (!parentPath.endsWith("/")) {
            if (!parentPath.endsWith("/")) {
                parentPath = parentPath + "/";
            }
        }
        this.parentPath = parentPath;
        this.fristUrl = parentPath + fristValue;
        if (!mapping.name().equals("")) {
            this.name = mapping.name();
        }
    }

    public void reSetUrl(String url) {
        value = new HashSet<>();
        value.add(url);
    }

    public String getFristUrl() {
        return fristUrl;
    }

    public void setFristUrl(String fristUrl) {
        this.fristUrl = fristUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getValue() {
        return value;
    }

    public void setValue(Set<String> value) {
        this.value = value;
    }

    public Set<String> getPath() {
        return path;
    }

    public void setPath(Set<String> path) {
        this.path = path;
    }

    public Set<RequestMethod> getMethod() {
        return method;
    }

    public void setMethod(Set<RequestMethod> method) {
        this.method = method;
    }

    public Set<String> getParams() {
        return params;
    }

    public void setParams(Set<String> params) {
        this.params = params;
    }

    public Set<String> getHeaders() {
        return headers;
    }

    public void setHeaders(Set<String> headers) {
        this.headers = headers;
    }

    public Set<String> getConsumes() {
        return consumes;
    }

    public void setConsumes(Set<String> consumes) {
        this.consumes = consumes;
    }

    public Set<String> getProduces() {
        return produces;
    }

    public void setProduces(Set<String> produces) {
        this.produces = produces;
    }

    public String getFristValue() {
        return fristValue;
    }

    public void setFristValue(String fristValue) {
        this.fristValue = fristValue;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public String toAnnotationStr() {
        RequestMappingBean requestMappingBean = new RequestMappingBean();
        Set<String> values = new HashSet<>();
        String url = this.getValue().iterator().next();
        if (parentPath != null && !parentPath.equals("/")) {
            String modulePath = parentPath;
            if (modulePath.startsWith("/")) {
                modulePath = modulePath.substring(1, modulePath.length());
            }
            if (url.startsWith("/")) {
                url = url.substring(1, url.length());
            }
            while (url.startsWith(modulePath)) {
                url = url.replace(modulePath, "");
                if (url.startsWith("/")) {
                    url = url.substring(1, url.length());
                }
            }

        }
        values.add(url);
        requestMappingBean.setValue(values);
        requestMappingBean.setMethod(this.getMethod());
        requestMappingBean.setConsumes(this.getConsumes());
        requestMappingBean.setHeaders(this.getHeaders());
        requestMappingBean.setPath(this.getPath());
        requestMappingBean.setName(this.getName());
        requestMappingBean.setProduces(this.getProduces());
        requestMappingBean.setParams(this.getParams());
        return AnnotationUtil.toAnnotationStr(requestMappingBean);
    }

}
