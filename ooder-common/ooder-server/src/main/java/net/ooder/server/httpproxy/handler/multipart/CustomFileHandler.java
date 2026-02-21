package net.ooder.server.httpproxy.handler.multipart;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.ooder.config.ErrorResultModel;
import net.ooder.server.httpproxy.core.*;
import net.ooder.web.RequestMethodBean;
import net.ooder.web.RequestParamBean;
import ognl.OgnlRuntime;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.portlet.PortletFileUpload;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;


public class CustomFileHandler extends AbstractHandler {

    public static final ConfigOption RULE_OPTION = new ConfigOption("rule", true, "Regular expression for matching URLs.");

    Pattern rule;

    public boolean initialize(String handlerName, Server server) {
        super.initialize(handlerName, server);
        rule = Pattern.compile(RULE_OPTION.getProperty(server, handlerName));
        return true;
    }


    protected boolean handleBody(HttpRequest request, HttpResponse response) throws IOException {
        String resource = request.getPath();
        boolean ruleMatches = rule.matcher(resource).matches();
        if (!ruleMatches) {
            return false;
        }
        String mimeType = getMimeType(resource);
        if (mimeType != null) {
            response.setMimeType(mimeType);
        }
        String contentType = this.getContentType(request);
        if (contentType != null && contentType.indexOf("multipart/form-data") > -1) {
            return this.sendMultiparPostProxy(request, response);
        }

        return true;
    }


    public boolean sendMultiparPostProxy(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        // 获取服务器响应的IO流
        String json = "";

        RequestMethodBean methodBean = this.getRequestMethodBean(request);

        if (methodBean == null) {
            return false;
        }

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
                for (FileItem f : fileItems) {
                    if (f.getFieldName().equals(paramBean.getParamName())) {
                        if (f.isFormField()) {
                            paramObjs.add(f.getString());
                        } else {
                            CommonsMultipartFile commonsMultipartFile = new CommonsMultipartFile(f);
                            paramObjs.add(commonsMultipartFile);
                        }
                    }
                }
            }
        }


        try {
            Object service = getService(methodBean, request);
            ;
            if (service != null) {
                Object obj = OgnlRuntime.callMethod(getOgnlContext(), service, methodBean.getMethodName(), paramObjs.toArray());
                if (obj instanceof String) {
                    json = obj.toString();
                } else {
                    json = JSONObject.toJSONString(obj, false);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            ErrorResultModel resultModel = new ErrorResultModel();
            resultModel.setErrdes(e.getMessage());
            json = JSON.toJSONString(resultModel);
        }

        return this.sendJSON(methodBean, response, json);


    }


}
