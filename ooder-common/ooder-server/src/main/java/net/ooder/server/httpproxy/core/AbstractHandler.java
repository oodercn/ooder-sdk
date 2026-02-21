package net.ooder.server.httpproxy.core;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import net.ooder.common.JDSConstants;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.ClassUtility;
import net.ooder.common.util.IOUtility;
import net.ooder.common.util.StringUtility;
import net.ooder.config.JDSUtil;
import net.ooder.context.JDSActionContext;
import net.ooder.context.JDSContext;
import net.ooder.annotation.RequestType;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.jds.core.esb.util.ActionContext;
import net.ooder.server.context.MinServerActionContextImpl;
import net.ooder.server.httpproxy.handler.multipart.CommonsMultipartFile;
import net.ooder.server.httpproxy.handler.multipart.SimpleRequestContext;
import net.ooder.template.JDSFreemarkerResult;
import net.ooder.web.APIConfigFactory;
import net.ooder.web.BaseParamsEnums;
import net.ooder.web.RequestMethodBean;
import net.ooder.web.RequestParamBean;
import net.ooder.annotation.Aggregation;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.portlet.PortletFileUpload;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public abstract class AbstractHandler implements Handler {

    public static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, AbstractHandler.class);
    protected Server server;
    protected String handlerName;
    protected String urlPrefix;
    private HashMap contextMap;
    private OgnlContext ognlContext;


    public static final ConfigOption RESOURCE_MOUNT_OPTION = new ConfigOption("resourceMount", "/", "A path within the classpath to the root of the folder to share.");
    public static final ConfigOption DEFAULT_RESOURCE_OPTION = new ConfigOption("default", "index.html", "The default resource name.");
    public static final ConfigOption RULE_OPTION = new ConfigOption("rule", true, "Regular expression for matching URLs.");
    public static final ConfigOption URL_PREFIX_OPTION = new ConfigOption("url-prefix", "/", "URL prefix path for this handler.  Anything that matches starts with this prefix will be handled by this handler.");

    public AbstractHandler() {
    }

    public boolean initialize(String handlerName, Server server) {
        this.server = server;
        this.handlerName = handlerName;
        this.urlPrefix = URL_PREFIX_OPTION.getProperty(server, handlerName);

        return true;
    }


    public String getName() {
        return handlerName;
    }


    public boolean handle(Request aRequest, Response aResponse) throws IOException {
        if (aRequest instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) aRequest;
            HttpResponse response = (HttpResponse) aResponse;
            String mimeType = getMimeType(request.getPath());
            if (mimeType == null || mimeType.startsWith("text/html") || request.getPath().endsWith(".cls")) {
                MinServerActionContextImpl context = (MinServerActionContextImpl) JDSActionContext.getActionContext();
                context.setHandle(this);
                context.setHttpRequest(request);
                context.setHttpResponse(aResponse);
                String sessionId = null;
                if (request.getParameterMap().containsKey(JDSContext.JSESSIONID)) {
                    sessionId = request.getParameterMap().get(JDSContext.JSESSIONID).toString();
                } else {
                    sessionId = request.getCookie().get(JDSContext.JSESSIONID.toUpperCase());
                }
//            if (sessionId == null) {
//                try {
//                    sessionId = JDSServer.getInstance().getAdminUser().getSessionId();
//                    context.setSessionId(sessionId);
//                } catch (JDSException e) {
//                    e.printStackTrace();
//                }
//            }
                if (sessionId == null) {
                    sessionId = UUID.createUUID().toString();
                }

                BasicClientCookie clientCookie = response.addCookie(JDSContext.JSESSIONID, sessionId);
                clientCookie.setPath("/");


                context.getParamMap().putAll(request.getParameterMap());

                if (sessionId != null) {
                    context.setSessionId(sessionId);
                    Map session = server.getSession().get(sessionId);
                    if (session == null) {
                        session = new HashMap();
                        server.getSession().put(sessionId, session);
                    }
                    request.setSession(session);
                    context.getSessionMap().putAll(session);
                }


                String query = "";
                String referer = request.getRequestHeader(RefererHeard);
                if (referer != null) {
                    int queryIndex = referer.indexOf('?');
                    if (queryIndex > 0 && !referer.endsWith("?")) {
                        query = referer.substring(queryIndex + 1);
                    }
                }
                request.getParameterMap().putAll(this.createQueryMap(query));

                String sysId = server.getProperty(JDSContext.SYSCODE);
                if (sysId != null) {
                    context.getContext().put(JDSContext.SYSCODE, sysId);
                }


                this.ognlContext = ((HttpRequest) aRequest).getOgnlContext();
            }

            // mime.cls=application/x-javascript

            return handleBody(request, response);
        }
        return false;
    }

    public Object invokMethod(RequestMethodBean methodBean) {
        Object object = null;
        HttpRequest request = (HttpRequest) JDSActionContext.getActionContext().getHttpRequest();

        HttpResponse response = (HttpResponse) JDSActionContext.getActionContext().getHttpResponse();
        try {
            object = invokMethod(methodBean, request, response);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (OgnlException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return object;

    }

    public Object invokMethod(RequestMethodBean methodBean, HttpRequest request, HttpResponse response) throws ClassNotFoundException, OgnlException, IOException {
        Object object = null;
        Object service = this.getService(methodBean, request);
        Map<String, Object> allParamsMap = this.getAllParamMap(methodBean, request);
        Map<String, String> paramsMap = methodBean.getParamsMap();
        Set<RequestParamBean> keySet = methodBean.getParamSet();
        Object[] objects = new Object[paramsMap.size()];
        Class[] objectTyps = new Class[paramsMap.size()];

        int k = 0;
        for (RequestParamBean paramBean : keySet) {
            String key = paramBean.getParamName();
            Class ctClass = ClassUtility.loadClass(paramsMap.get(paramBean.getParamName()));

            Class paramClass = paramBean.getParamClass();
            if (paramClass.isInterface()) {
                paramClass = EsbUtil.guessRealClass(paramClass);
            }

            String iClassName = ctClass.getName();
            Class iClass = ClassUtility.loadClass(iClassName);
            Object value = null;
            RequestBody requestBody = null;
            for (Annotation annotation : paramBean.getAnnotations()) {
                if (annotation.annotationType().equals(RequestBody.class)) {
                    requestBody = (RequestBody) annotation;
                }
            }
            if (requestBody != null) {
                if (methodBean.getRequestType().equals(RequestType.JSON)) {
                    JSONObject requestBodyValue = JSON.parseObject(new String(request.getPostData()));
                    for (BaseParamsEnums baseParams : BaseParamsEnums.values()) {
                        if (requestBodyValue.containsKey(baseParams.name())) {
                            JDSActionContext.getActionContext().getContext().put(baseParams.name(), requestBodyValue.get(baseParams.name()));
                        }
                    }
                    value = JSONObject.parseObject(JSON.toJSONString(requestBodyValue), paramClass);
                }
            } else if (Request.class.isAssignableFrom(iClass)) {
                value = request;

            } else if (Response.class.isAssignableFrom(iClass)) {
                value = response;
            } else {
                try {
                    switch (methodBean.getRequestType()) {
                        case FORM:
                            if (iClass.isArray()) {
                                if (JSONObject.parseArray(allParamsMap.get(key).toString()).size() > 0) {
                                    value = JSONObject.parseArray(allParamsMap.get(key).toString(), iClass.getComponentType()).toArray();
                                } else {
                                    value = Array.newInstance(iClass.getComponentType(), 0);
                                }
                            } else {
                                if (paramBean.getJsonData()) {
                                    if (iClass.isArray() || Collection.class.isAssignableFrom(iClass)) {
                                        value = JSONArray.parseObject(allParamsMap.get(key).toString(), paramBean.getParamType());
                                    } else {
                                        value = JSONObject.parseObject(allParamsMap.get(key).toString(), paramBean.getParamType());
                                    }

                                } else {
                                    value = TypeUtils.castToJavaBean(allParamsMap.get(key), iClass);
                                }
                            }
                            break;
                        case JSON:
                            JSONObject jsonObject = JSON.parseObject(new String(request.getPostData()));
                            if (jsonObject != null) {
                                for (BaseParamsEnums baseParams : BaseParamsEnums.values()) {
                                    if (jsonObject.containsKey(baseParams.name())) {
                                        JDSActionContext.getActionContext().getContext().put(baseParams.name(), jsonObject.get(baseParams.name()));
                                    }
                                }

                                String paramName = paramBean.getParamName();
                                Object obj = jsonObject.get(paramName);
                                if (obj != null) {
                                    value = TypeUtils.castToJavaBean(obj, iClass);
                                    JDSActionContext.getActionContext().getContext().put(paramName, value);
                                }

                            }
                            break;
                        default:
                            value = TypeUtils.cast(allParamsMap.get(key), iClass, null);
                            break;
                    }
                } catch (Throwable e) {
                    logger.error("params [" + key + "] convertValue err " + e.getMessage());
                }
            }

            objectTyps[k] = iClass;
            objects[k] = value;
            k = k + 1;
        }


        if (service != null) {
            object = OgnlRuntime.callMethod(getOgnlContext(), service, methodBean.getMethodName(), objects);
        }
        return object;
    }


    public boolean sendFtl(HttpRequest request, HttpResponse response, String resource) throws IOException {
        String responseStr = "";
        try {
            JDSFreemarkerResult result = new JDSFreemarkerResult();
            StringWriter stringWriter = (StringWriter) result.doExecute(resource, null);
            responseStr = stringWriter.toString();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
        response.setMimeType("text/html");
        response.sendResponse(getInputStream(responseStr, "utf-8"), -1);
        return true;
        // return executeResponse(response, remoteRequest);
    }

    public boolean sendJSON(RequestMethodBean methodBean, HttpResponse response, String json) {
        Set<String> produces = methodBean.getMappingBean().getProduces();
        StringBuffer mimeType = new StringBuffer();
        if (produces != null && produces.size() > 0) {
            for (String produce : produces) {
                mimeType.append(produce);
                mimeType.append(";");
            }
            response.sendResponse(json, mimeType.toString());
        } else {
            switch (methodBean.getResponseType()) {
                case JSON:
                    response.sendJSONResponse(json);
                    break;
                case TEXT:
                    response.addHeader("Content-Type", "text/html");
                    response.sendResponse(json);
                    break;
                case XML:
                    response.sendResponse(json);
                    break;
                default:
                    response.sendResponse(json);
                    break;
            }

        }
        return true;
    }


    public Object upload(RequestMethodBean methodBean, HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        Object object = null;
        Set<RequestParamBean> paramBeanSet = methodBean.getParamSet();
        Boolean isMulti = false;
        for (RequestParamBean paramBean : paramBeanSet) {
            if (MultipartFile.class.isAssignableFrom(paramBean.getParamClass())) {
                isMulti = true;
            }
        }
        List<Object> paramObjs = new ArrayList<>();
        // 是否包含多文件操作
        if (isMulti) {
            RequestContext requestContext = new SimpleRequestContext(StandardCharsets.UTF_8, this.getContentType(request), new ByteArrayInputStream(request.getPostData()));
            // 解析器创建
            FileUploadBase fileUploadBase = new PortletFileUpload();
            FileItemFactory fileItemFactory = new DiskFileItemFactory();
            fileUploadBase.setFileItemFactory(fileItemFactory);
            fileUploadBase.setHeaderEncoding("utf-8");


            // 解析出所有的部件
            List<FileItem> fileItems = null;
            try {
                fileItems = fileUploadBase.parseRequest(requestContext);
            } catch (FileUploadException e) {
                e.printStackTrace();
            }

            for (RequestParamBean paramBean : paramBeanSet) {
                Object obj = null;
                for (FileItem f : fileItems) {
                    if (f.getFieldName().equals(paramBean.getParamName())) {
                        if (f.isFormField()) {
                            obj = new String(f.getString().getBytes("iso8859-1"), "UTF-8");
                        } else {
                            CommonsMultipartFile commonsMultipartFile = new CommonsMultipartFile(f);
                            obj = commonsMultipartFile;
                        }
                    }
                }
                paramObjs.add(obj);
            }
        }


        try {
            Object service = getService(methodBean, request);
            object = OgnlRuntime.callMethod(getOgnlContext(), service, methodBean.getMethodName(), paramObjs.toArray());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return object;


    }


    public String getProjectName(HttpRequest request) throws MalformedURLException {
        String projectName = request.getParameter(ProjectVersionName);

        if (isNullParams(projectName)) {
            URL parenturl = null;
            if (request.getHeaders().get(RefererHeard) != null) {
                parenturl = new URL(request.getHeaders().get(RefererHeard).toString());
                projectName = createQueryMap(parenturl.getQuery()).get(ProjectVersionName);
                if (projectName == null || projectName.equals("")) {
                    String path = this.formatPath(parenturl.toString(), projectName);
                    if (path.indexOf("/") > -1) {
                        projectName = path.substring(0, path.indexOf("/"));
                    }
                }
            }
        }


        if (isNullParams(projectName)) {
            String urlStr = request.getUrl();
            String path = this.formatPath(urlStr, projectName);
            if (path.indexOf("/") > -1) {
                projectName = path.substring(0, path.indexOf("/"));
            }
        }


        if (isNullParams(projectName)) {
            if (this.getServer().getProxyHost() != null) {
                projectName = this.getServer().getProxyHost().getProjectName();
            }
        }

        if (projectName == null || projectName.equals("") || (projectName.equals("projectManager") && request.getParameter(ProjectName) != null)) {
            projectName = request.getParameter(ProjectName);
        }


        request.getParameterMap().put(ProjectName, projectName);
        request.getParameterMap().put(ProjectVersionName, projectName);

        return projectName;
    }

    public boolean isNullParams(String paramName) {
        if (paramName == null || paramName.equals("") || paramName.equals("[object Object]") || paramName.equals("@{projectName}")) {
            return true;
        }
        return false;
    }

    public String formatPath(String urlStr, String projectName) throws MalformedURLException {
        String path = urlStr;
        if (urlStr.startsWith("http")) {
            URL url = new URL(urlStr);
            path = url.getPath();
        }
        if (path.startsWith("/")) {
            path = path.substring(1, path.length());
        }
        for (String ckey : CKEY) {
            String key = ckey + "/";
            if (path.startsWith(key)) {
                path = path.substring(key.length());
            }
        }
        if (projectName != null) {
            if (projectName.indexOf(VVVERSION) > -1) {
                projectName = projectName.split(VVVERSION)[0];
            }
            if ((path.startsWith(projectName + "/") || path.startsWith(projectName + VVVERSION))) {
                path = path.substring(path.indexOf("/") + 1);
            }
        }


        return path;
    }

    public InputStream getInputStream(String content, String charSet) {
        if (content == null) {
            return null;
        }
        try {
            return new ByteArrayInputStream(content.getBytes(charSet));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected Map<String, String> createQueryMap(String query) {
        Map queryMap = new TreeMap();
        if (query == null) {
            return queryMap;
        }

        query = query.replace('+', ' ');
        StringTokenizer st = new StringTokenizer(query, "&");
        try {
            while (st.hasMoreTokens()) {
                String field = st.nextToken();
                int index = field.indexOf('=');
                if (index < 0) {
                    queryMap.put(URLDecoder.decode(field, "UTF-8"), "");
                } else {
                    queryMap.put(URLDecoder.decode(field.substring(0, index), "UTF-8"),
                            URLDecoder.decode(field.substring(index + 1), "UTF-8"));
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return queryMap;
    }

    public Map<String, Object> getPathParamsValue(String methodUrl, String url) {

        Map<String, Object> pathParams = new HashMap<String, Object>();

        while (methodUrl.indexOf("{") > -1 && methodUrl.indexOf("}") > -1) {
            int start = methodUrl.indexOf("{");
            int end = methodUrl.indexOf("}");
            String paramName = methodUrl.substring(start, end + 1);
            methodUrl = url.substring(start);
            String value = methodUrl.substring(0, methodUrl.indexOf("/"));
            pathParams.put(paramName.substring(1, paramName.length() - 1), value);
            // configUrl = StringUtility.replace(methodUrl, methodUrl.substring(start, end + 1), ".*?");
        }

        return pathParams;

    }


    public String getExtStr(String ftl) throws IOException, TemplateException {
        StringWriter stringWriter = new StringWriter();
        freemarker.template.Configuration configuration = new freemarker.template.Configuration();
        try {
            String path = JDSUtil.getJdsRealPath();
            path = path + "/ftl/";
            configuration.setDirectoryForTemplateLoading(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Template template = configuration.getTemplate(ftl);
        template.process(this.getContextMap(), stringWriter);
        String str = stringWriter.toString();
        return str;
    }

    public String getContentType(HttpRequest request) {
        String contentType = request.getRequestHeader("Content-Type");
        if (contentType == null) {
            contentType = request.getRequestHeader("Content-type");
        }
        return contentType;
    }

    public OgnlContext getOgnlContext() {

        return ognlContext;
    }


    public Map<String, Object> getAllParamMap(RequestMethodBean methodBean, HttpRequest request) {
        Map<String, Object> allParamsMap = new HashMap<String, Object>();
        if (methodBean != null) {
            allParamsMap.putAll(getPathParamsValue(methodBean.getUrl(), request.getPath()));
            if (methodBean.getRequestType().equals(RequestType.JSON) && request.getMethod().equals("POST")) {
                JSONObject jsonObject = JSON.parseObject(new String(request.getPostData()));
                allParamsMap.putAll(jsonObject);
            }
        }


        allParamsMap.putAll(request.getParameterMap());
        allParamsMap.putAll(getRefererHeardMap(request));

        if (allParamsMap.get(ProjectVersionName) == null || allParamsMap.get(ProjectVersionName).equals("")) {
            try {
                allParamsMap.put(ProjectVersionName, this.getProjectName(request));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        return allParamsMap;
    }


    private Map<String, Object> getRefererHeardMap(HttpRequest request) {
        Map refererHeardMap = new HashMap();
        if (request.getHeaders().get(RefererHeard) != null) {
            URL parenturl = null;
            try {
                parenturl = new URL(request.getHeaders().get(RefererHeard).toString());
                refererHeardMap.putAll(createQueryMap(parenturl.getQuery()));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return refererHeardMap;
    }

    Object getRealService(Class clazz) throws OgnlException {
        Object service = null;
        if (clazz.getInterfaces().length > 0) {
            service = EsbUtil.parExpression(clazz.getInterfaces()[0]);
        } else {
            service = EsbUtil.parExpression(clazz);
        }

        if (service == null) {
            if (clazz.isInterface()) {
                Aggregation aggregation = (Aggregation) clazz.getAnnotation(Aggregation.class);
                if (aggregation != null && !aggregation.rootClass().equals(Void.class) && !aggregation.rootClass().equals(clazz)) {
                    clazz = aggregation.rootClass();
                    service = getRealService(clazz);
                }
            } else {
                service = OgnlRuntime.callConstructor(getOgnlContext(), clazz.getName(), new Object[]{});
            }

        }

        return service;
    }

    public Object getService(RequestMethodBean methodBean, HttpRequest request) throws ClassNotFoundException, OgnlException {
        Map<String, Object> allParamsMap = this.getAllParamMap(methodBean, request);
        Class clazz = ClassUtility.loadClass(methodBean.getClassName());
        Object service = getRealService(clazz);
        for (Field field : clazz.getDeclaredFields()) {
            if (allParamsMap.get(field.getName()) != null) {
                try {
                    OgnlRuntime.setProperty(this.getOgnlContext(), service, field.getName(), TypeUtils.castToJavaBean(allParamsMap.get(field.getName()), field.getType()));
                } catch (OgnlException e) {
                }
            }
        }
        return service;
    }

    protected boolean isRequestdForHandler(HttpRequest request) {
        return request.getUrl().startsWith(getUrlPrefix());
    }

    protected boolean handleBody(HttpRequest request, HttpResponse response) throws IOException {
        return false;
    }

    public boolean shutdown(Server server) {
        return true;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public Map getContextMap() {
        if (this.contextMap == null) {
            this.contextMap = new HashMap();
            Iterator it = ActionContext.getContext().getValueStack().getRoot().iterator();
            for (; it.hasNext(); ) {
                Object obj = it.next();
                if (obj instanceof Map) {
                    contextMap.putAll((Map) obj);
                }
            }
        }
        return contextMap;

    }

    protected String getMimeType(String filename) {
        int index = filename.lastIndexOf(".");
        String mimeType = null;
        if (index > 0) {
            mimeType = server.getProperty("mime" + filename.substring(index).toLowerCase());
        }

        return mimeType;
    }


    public void copyStreamToFile(InputStream input, File file) throws IOException {


        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (file.exists() && !file.canWrite()) {
            final String message = "Unable to open file " + file + " for writing.";
            throw new IOException(message);
        }
        if (input != null) {
            final FileOutputStream output = new FileOutputStream(file);
            IOUtility.copy(input, output);
            IOUtility.shutdownStream(input);
            IOUtility.shutdownStream(output);
        }

    }


    public void copyStreamToFile(String url, File file) throws IOException {
        URL source = new URL(url);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (file.exists() && !file.canWrite()) {
            final String message = "Unable to open file " + file + " for writing.";
            throw new IOException(message);
        }
        final InputStream input = source.openStream();
        final FileOutputStream output = new FileOutputStream(file);
        IOUtility.copy(input, output);
        IOUtility.shutdownStream(input);
        IOUtility.shutdownStream(output);
    }

    private RequestMethodBean findMethodBean(String path) {
        RequestMethodBean methodBean = APIConfigFactory.getInstance().getRequestMappingBean(path);
        if (methodBean == null) {
            methodBean = APIConfigFactory.getInstance().findMethodBean(path);
        }
        return methodBean;
    }


    public RequestMethodBean getRequestMethodBean(HttpRequest request) throws MalformedURLException {
        String url = request.getUrl();
        RequestMethodBean methodBean = findMethodBean(url);
        String projectName = this.getProjectName(request);
        String path = this.formatPath(url, projectName);

        if (methodBean == null) {
            for (String patt : pattArr) {
                if (path.endsWith(patt)) {
                    path = path.substring(0, path.length() - patt.length());
                }
            }
            path = StringUtility.replace(path, ".", "/");
            methodBean = findMethodBean(path);
        }
        if (methodBean == null && projectName != null) {
            if (projectName.indexOf("VVV") > -1) {
                projectName = projectName.split("VVV")[0];
            }
            methodBean = findMethodBean(projectName + "/" + path);
        }

        return methodBean;
    }


    public void setOgnlContext(OgnlContext ognlContext) {
        this.ognlContext = ognlContext;
    }
}
