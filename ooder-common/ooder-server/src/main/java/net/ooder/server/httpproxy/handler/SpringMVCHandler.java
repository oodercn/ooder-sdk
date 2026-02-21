package net.ooder.server.httpproxy.handler;


import com.alibaba.fastjson.JSONObject;
import net.ooder.common.JDSConstants;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.md5.MD5;
import net.ooder.config.ErrorResultModel;
import net.ooder.config.JDSUtil;
import net.ooder.config.ResultModel;
import net.ooder.server.httpproxy.core.*;
import net.ooder.web.RequestMethodBean;
import net.ooder.web.util.JSONGenUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;


public class SpringMVCHandler extends AbstractHandler {
    public static final ConfigOption RULE_OPTION = new ConfigOption("rule", true, "Regular expression for matching URLs.");
    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, SpringMVCHandler.class);
    Pattern rule;

    public boolean initialize(String handlerName, Server server) {
        super.initialize(handlerName, server);
        rule = Pattern.compile(RULE_OPTION.getProperty(server, handlerName));
        return true;
    }


    protected boolean handleBody(HttpRequest request, HttpResponse response) throws IOException {
        boolean ruleMatches = rule.matcher(request.getUrl()).matches();
        if (!ruleMatches) {
            return false;
        }
        RequestMethodBean methodBean = this.getRequestMethodBean(request);
        if (methodBean == null) {
            return false;
        }

        try {
            logger.info("request url " + request.getUrl());
            String contentType = this.getContentType(request);
            Class iClass = JSONGenUtil.getInnerReturnType(methodBean.getSourceMethod());
            if (!InputStream.class.isAssignableFrom(iClass)) {
                Object object = null;
                if (contentType != null && contentType.indexOf("multipart/form-data") > -1) {
                    object = upload(methodBean, request, response);
                } else {
                    object = this.invokMethod(methodBean, request, response);
                }

                if (object != null) {
                    String json = object.toString();
                    if (json.endsWith(".ftl")) {
                        return this.sendFtl(request, response, json);
                    } else {
                        if (methodBean.getResponseBody() != null) {
                            json = JSONObject.toJSONString(object);
                        }
                        return sendJSON(methodBean, response, json);
                    }
                }

            } else {
                ResultModel<InputStream> resultModel = (ResultModel<InputStream>) this.invokMethod(methodBean, request, response);
                InputStream inputStream = resultModel.get();
                if (inputStream != null) {
                    String mdrStr = MD5.getHashString(inputStream);
                    File file = new File(JDSUtil.getJdsRealPath() + "temp" + File.separator + mdrStr + ".temp");
                    response.addHeader("Content-disposition", "filename=" + new String(file.getName().getBytes("utf-8"), "ISO8859-1"));
                    response.addHeader("Content-Length", String.valueOf(file.length()));
                    response.sendResponse(inputStream, Integer.valueOf(Long.toString(file.length())));
                }
                return true;

            }

        } catch (Exception e) {
            e.printStackTrace();
            ErrorResultModel resultModel = new ErrorResultModel();
            resultModel.setErrdes(e.getMessage());
            return sendJSON(methodBean, response, JSONObject.toJSONString(resultModel, false));
        }
        return true;
    }


}
