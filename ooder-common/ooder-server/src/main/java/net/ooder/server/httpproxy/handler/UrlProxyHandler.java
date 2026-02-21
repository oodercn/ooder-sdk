package net.ooder.server.httpproxy.handler;

import net.ooder.common.JDSConstants;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.StringUtility;
import net.ooder.config.JDSConfig;
import net.ooder.jds.core.esb.util.DownLoadPageTask;
import net.ooder.server.httpproxy.core.*;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.regex.Pattern;


public class UrlProxyHandler extends AbstractHandler {

    private static final Log log = LogFactory.getLog(JDSConstants.CONFIG_KEY, UrlProxyHandler.class);
    public static final ConfigOption RULE_OPTION = new ConfigOption("rule", true, "Regular expression for matching URLs.");
    Pattern rule;

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
        boolean ruleMatches = rule.matcher(request.getUrl()).matches();
        String resource = request.getUrl();
        resource = resource.substring("http://".length());

        String path = resource;

        if (path.indexOf("google.com") > -1 || path.indexOf("oogleapis") > -1) {
            response.sendError(500, "");
            return true;
        }
        if (resource.indexOf("/") == -1) {
            path = path + "/index.html";
        } else if (resource.endsWith("/")) {
            path = path + "index.html";
        }


        path = StringUtility.replace(path, ":", "");


        File file = new File(JDSConfig.getServerHome() + "\\" + path);
        HttpGet get = new HttpGet("http://" + resource);
        org.apache.http.HttpResponse httpresoponse = HttpClients.createDefault().execute(get);


        HttpEntity entity = httpresoponse.getEntity();
        String charSet = EntityUtils.getContentCharSet(entity);
        String mimeType = entity.getContentType().getValue();
        mimeType = StringUtility.split(mimeType, ";")[0];

        InputStream is = entity.getContent();
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (!file.exists()) {
            DownLoadPageTask.getInstance().getFuture("http://" + resource, file);
        } else {
            if (!file.isDirectory()) {
                if (file.length() != httpresoponse.getEntity().getContentLength()) {
                    DownLoadPageTask.getInstance().getFuture("http://" + resource, file);
                }
            }

        }

        response.setMimeType(mimeType);
        response.sendResponse(is, Integer.valueOf(Long.toString(file.length())));
        return true;

    }

    private HttpEntity getParams(HttpRequest request) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        for (Iterator<String> it = request.getParameterNames().iterator(); it.hasNext(); ) {

            String key = it.next();
            builder.addTextBody(key, request.getParameter(key));
        }
        return builder.build();
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

    public static void main(String[] args) {
        Pattern rule = Pattern.compile(".*\\.(action|do|ajax)(|\\?.*)");
        System.out.println("aaa.action?aaa=" + rule.matcher("aaa.action?aaad").matches());


        String rules = rule.matcher("http//192.168.0.7/aaa.action?aaad=&bbba").replaceAll("aaa");
//	   for(int k=0;rules.length>k;k++){
//		   System.out.println("k="+k+rules[k]);
//	   }
        System.out.println(rules);
    }
}
