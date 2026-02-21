/**
 * $RCSfile: HttpInvocationHandler.java,v $
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

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import net.ooder.cluster.ClusterMananerFactory;
import net.ooder.common.ConfigCode;
import net.ooder.common.JDSConstants;
import net.ooder.common.JDSException;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;
import net.ooder.context.JDSActionContext;
import net.ooder.context.JDSContext;
import net.ooder.server.JDSServer;
import net.ooder.web.*;
import net.ooder.web.util.JSONGenUtil;
import net.ooder.web.util.PageUtil;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import net.sf.cglib.proxy.InvocationHandler;
import org.apache.http.client.fluent.Async;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.util.CharsetUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class HttpInvocationHandler implements InvocationHandler {

    public final static String serverUrlKey = "ServerUrl";
    public final static String TJTOKEN = "tjToken";
    private final static String httpparams = "HTTPPARAMS";
    private String serverKey;
    private CtClass ctClass;


    private final static SerializeConfig config = new SerializeConfig();

    static {
        config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
    }

    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, HttpInvocationHandler.class);

    public HttpInvocationHandler(CtClass ctClass, String serverKey) throws JDSException {
        this.serverKey = serverKey;
        this.ctClass = ctClass;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Request request = null;
        JDSContext jdcontext = JDSActionContext.getActionContext();
        Map context = jdcontext.getContext();

        String requestJson = "";
        String serverUrl = serverKey;
        String sessionId = jdcontext.getSessionId();

        if (!serverKey.startsWith("http://") && JDSServer.getInstance().getClusterClient().getServerNodeById(serverKey) != null) {
            serverUrl = ClusterMananerFactory.getClusterManager(ConfigCode.fromType(serverKey)).getSubServer(sessionId, ConfigCode.fromType(serverKey)).getUrl();
        }

        // 更新用户权限信息
        if (context.get(serverUrlKey) != null && !context.get(serverUrlKey).equals("")) {
            serverUrl = (String) context.get(serverUrlKey);
        }

        Async async = Async.newInstance().use(RemoteConnectionManager.getConntctionService(serverUrl));
        RequestMappingBean mapping = APIConfigFactory.getInstance().getMapping(method, ctClass);
        String url = serverUrl + mapping.getFristUrl();
        String syscode = jdcontext.getSystemCode();
        if ((syscode == null || syscode.equals("null")) && JDSServer.getInstance().
                started() && JDSServer.getInstance().
                getCurrServerBean() != null) {
            syscode = JDSServer.getInstance().getCurrServerBean().getId();
        }

        if (!url.contains("?"))

        {
            url = url + "?" + JDSContext.SYSCODE + "=" + syscode + "&jdstime=" + System.currentTimeMillis();
        } else

        {
            url = url + "&" + JDSContext.SYSCODE + "=" + syscode + "&jdstime=" + System.currentTimeMillis();
        }


        CtMethod[] cms = ctClass.getDeclaredMethods(method.getName());
        CtMethod cm = null;

        for (
                CtMethod ccm : cms)

        {
            if (ccm.getParameterTypes().length == method.getParameterTypes().length) {
                cm = ccm;
            }
        }

        MethodInfo methodInfo = cm.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);

        String[] paramNames = new String[cm.getParameterTypes().length];
        TreeMap<Integer, String> sortMap = new TreeMap<Integer, String>();
        for (
                int i = 0; i < attr.tableLength(); i++)

        {
            sortMap.put(attr.index(i), attr.variableName(i));
            int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
            paramNames = Arrays.copyOfRange(sortMap.values().toArray(new String[0]), pos, paramNames.length + pos);
        }

        if (ListResultModel.class.isAssignableFrom(method.getReturnType())) {
            url = PageUtil.appUrlParams(url, paramNames);
        }

        if (args.length == 0)

        {
            // logger.info(url + " {args  0 }");
            request = Request.Post(url);

        } else if (args.length == 1)

        {
            Object[][] annotations = cm.getParameterAnnotations();
            Object[] annotationa = annotations[0];
            Object obj = args[0];
            for (Object annotation : annotationa) {
                if (RequestBody.class.isAssignableFrom(annotation.getClass())) {
                    String userData = JSONObject.toJSON(obj, config).toString();
                    requestJson = userData;
                    //    logger.info(url + " {args 1 body}");
                    // logger.debug(userData);
                    request = Request.Post(url).bodyString(userData, ContentType.APPLICATION_JSON);
                }

            }
        }
        // 固定参数
        if (request == null)

        {
            Form form = Form.form();
            Boolean isMulti = false;
            for (int i = 0; i < paramNames.length; i++) {
                if (args[i] instanceof MultipartFile) {
                    isMulti = true;
                }
            }

            // 是否包含多文件
            if (isMulti) {
                MultipartEntityBuilder reqEntitybuild = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE).setCharset(CharsetUtils.get("UTF-8"));
                for (int i = 0; i < paramNames.length; i++) {
                    String paramName = paramNames[i];
                    if (args[i] instanceof String || args[i] instanceof Integer || args[i] instanceof Long) {
                        reqEntitybuild.addTextBody(paramName, JSONObject.toJSON(args[i], config).toString());
                    } else if (args[i] instanceof MultipartFile) {
                        LocalMultipartFile part = (LocalMultipartFile) args[i];
                        InputStreamBody fileStream = new InputStreamBody(part.getInputStream(), paramName);
                        reqEntitybuild.addPart(paramName, fileStream);

                    } else if (args[i].getClass().isAssignableFrom(String[].class)
                            || args[i].getClass().isAssignableFrom(Integer[].class)
                            || args[i].getClass().isAssignableFrom(Long[].class)
                            ) {
                        Object[] array = (Object[]) args[i];
                        for (Object obj : array) {
                            reqEntitybuild.addTextBody(paramName, JSONObject.toJSON(args[i], config).toString());
                        }
                    } else {
                        reqEntitybuild.addTextBody(paramName, JSONObject.toJSON(args[i], config).toString());
                    }

                }
                //     logger.info(url + " {args 1 body}");
                request = Request.Post(url).body(reqEntitybuild.build());
            } else {

                for (int i = 0; i < paramNames.length; i++) {
                    String paramName = paramNames[i];
                    String userData = "";
                    if (args[i] != null) {
                        if (args[i] instanceof String || args[i] instanceof Integer || args[i] instanceof Long) {
                            userData = args[i].toString();
                            form.add(paramName, userData);
                        } else if (args[i].getClass().isAssignableFrom(String[].class)
                                || args[i].getClass().isAssignableFrom(Integer[].class)
                                || args[i].getClass().isAssignableFrom(Long[].class)
                                ) {
                            Object[] array = (Object[]) args[i];
                            for (Object obj : array) {
                                form.add(paramName, obj.toString());
                            }
                        } else {
                            userData = JSONObject.toJSON(args[i], config).toString();
                            form.add(paramName, userData);
                        }

                    }

                }
                logger.info(url + " {paramNames " + paramNames.length + "}");
                requestJson = form.build().toString();
                // logger.debug(requestJson);
                request = Request.Post(url).bodyForm(form.build(), Charset.forName("utf-8"));

            }
        }

        Class iClass = JSONGenUtil.getInnerReturnType(method);
        ResultModel resultModel = null;
        //start log
        String token = UUID.randomUUID().toString();
        RuntimeLog log = ConnectionLogFactory.getInstance().createLog(token, serverUrl, mapping.getFristUrl(), sessionId);
        log.setStartTime(System.currentTimeMillis());
        log.setRequestJson(requestJson);


        if (ListResultModel.class.isAssignableFrom(method.getReturnType())) {
            Class tClass = JSONGenUtil.getListReturnType(method);
            resultModel = new RemoteListResultModel(url, token, request, tClass, iClass, async);
        } else {
            resultModel = new RemoteResultModel(url, token, request, iClass, async);
        }

        // // 报送客户端IP
        request.setHeader("Proxy-Client-IP", JDSActionContext.getActionContext().getIpAddr());

        return resultModel;

    }
}
