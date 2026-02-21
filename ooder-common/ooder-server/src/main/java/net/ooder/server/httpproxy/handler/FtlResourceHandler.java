package net.ooder.server.httpproxy.handler;

import net.ooder.common.JDSConstants;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.config.JDSUtil;
import net.ooder.jds.core.esb.util.ActionContext;
import net.ooder.server.httpproxy.core.*;
import net.ooder.template.JDSFreemarkerResult;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

public class FtlResourceHandler extends AbstractHandler implements Handler {
    private static final Log log = LogFactory.getLog(JDSConstants.CONFIG_KEY, FtlResourceHandler.class);
    public static final ConfigOption RULE_OPTION = new ConfigOption("rule", true, "Regular expression for matching URLs.");


    Pattern rule;

    private HashMap contextMap;

    public boolean initialize(String handlerName, Server server) {
        try {
            super.initialize(handlerName, server);
            rule = Pattern.compile(RULE_OPTION.getProperty(server, handlerName));
            log.debug("Rule=" + rule.pattern());
            return true;
        } catch (IllegalArgumentException e) {

            e.printStackTrace();
            log.error(e.toString());
            return false;
        }
    }

    protected boolean handleBody(HttpRequest request, HttpResponse response) throws IOException {
        String resource = request.getPath();


        if (!rule.matcher(resource).matches()) {
            return false;
        }
        ;
        String responseStr = "";
        try {
            JDSFreemarkerResult result = new JDSFreemarkerResult();
            resource = "/ftl" + resource;
            StringWriter stringWriter = (StringWriter) result.doExecute(resource, null);
            responseStr = stringWriter.toString();

        } catch (TemplateException e) {
            e.printStackTrace();
        }


        for (Iterator<String> it = request.getHeaders().iterator(); it.hasNext(); ) {
            String key = it.next();
            response.addHeader(key, request.getParameter(key));
        }
        response.setMimeType("text/html;charset=utf-8");
        response.sendResponse(getInputStream(responseStr, "utf-8"), -1);

        return true;
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
}
