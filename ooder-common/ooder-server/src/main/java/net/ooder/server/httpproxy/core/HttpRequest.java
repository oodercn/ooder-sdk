package net.ooder.server.httpproxy.core;

import net.ooder.common.util.IOUtility;
import net.ooder.jds.core.esb.util.ActionContext;
import net.ooder.jds.core.esb.util.JDSConverter;
import net.ooder.jds.core.esb.util.OgnlValueStack;
import ognl.OgnlContext;
import ognl.OgnlRuntime;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.*;


public class HttpRequest extends Request {

    private static int nextRequestId = 0;

    private static final String DEFAULT_UDPCODE = "utf-8";

    private Integer requestId = null;

    private String scheme;

    private String method;

    private Map session;

    private String url;

    private String query;

    private String protocol;

    private int major;

    private OgnlContext context;

    private int minor;

    private HttpHeaders headers;

    private byte[] postData;

    private Map httpVariableMap;

    private String connectionHeader;

    private long timeStamp;

    Map<String, String> cookieMap;

    public HttpRequest(String aScheme, Socket aConnection, Properties serverConfig) {
        super(aConnection, serverConfig);
        scheme = aScheme;
        connection = aConnection;
        init();
    }

    public HttpRequest(String url, Properties serverConfig, boolean isInternal) {
        super(null, serverConfig);
        init();
        method = "GET";
        scheme = "http";
        parseUrl(url);
        protocol = "HTTP/1.0";
        major = 1;
        minor = 0;
        headers = new HttpHeaders();
        setIsInternal(isInternal);
    }

    public FileInputStream readStream(InputStream aStream) throws IOException {
        InternetInputStream stream = new InternetInputStream(aStream);
        String startLine = null;
        File file = Files.createTempFile(String.valueOf(System.currentTimeMillis()), "temp").toFile();
        try {
            startLine = readHttpCommand(stream);
            if (startLine == null) {
                return null;
            }
            if (protocol.equals("HTTP/1.0")) {
                major = 1;
                minor = 0;
            } else if (protocol.equals("HTTP/1.1")) {
                major = 1;
                minor = 1;
            } else {
                throw new HttpProtocolException(HttpURLConnection.HTTP_VERSION, "Protocol " + protocol + " not supported.");
            }

            headers = new HttpHeaders(stream);

            final FileOutputStream output = new FileOutputStream(file);
            IOUtility.copy(stream, output);
            IOUtility.shutdownStream(output);
        } catch (NoSuchElementException e) {
            throw new HttpProtocolException(HttpURLConnection.HTTP_NOT_FOUND, "Bad request " + startLine);
        } catch (NumberFormatException e) {
            throw new HttpProtocolException(HttpURLConnection.HTTP_LENGTH_REQUIRED, "Content Length was not a number or not supplied.");
        }
        return new FileInputStream(file);
    }


    public boolean readRequest(InputStream aStream) throws IOException {
        InternetInputStream stream = new InternetInputStream(aStream);
        String startLine = null;
        try {
            startLine = readHttpCommand(stream);
            if (startLine == null) {
                return false;
            }
            if (protocol.equals("HTTP/1.0")) {
                major = 1;
                minor = 0;
            } else if (protocol.equals("HTTP/1.1")) {
                major = 1;
                minor = 1;
            } else {
                throw new HttpProtocolException(HttpURLConnection.HTTP_VERSION, "Protocol " + protocol + " not supported.");
            }

            headers = new HttpHeaders(stream);

            readPostData(stream);
        } catch (NoSuchElementException e) {
            throw new HttpProtocolException(HttpURLConnection.HTTP_NOT_FOUND, "Bad request " + startLine);
        } catch (NumberFormatException e) {
            throw new HttpProtocolException(HttpURLConnection.HTTP_LENGTH_REQUIRED, "Content Length was not a number or not supplied.");
        }
        return true;
    }

    private void init() {
        method = null;
        url = null;
        query = null;
        protocol = null;
        connectionHeader = null;
        postData = null;
        httpVariableMap = null;
        timeStamp = System.currentTimeMillis();
        connectionHeader = "Connection";
        requestId = new Integer(nextRequestId++);
    }

    public Integer getRequestId() {
        return requestId;
    }

    private String readHttpCommand(InternetInputStream stream) throws IOException {
        String startLine = null;
        do {
            startLine = stream.readline();
            if (startLine == null) return null;
        } while (startLine.trim().length() == 0);

        StringTokenizer tokenizer = new StringTokenizer(startLine);
        method = tokenizer.nextToken();
        parseUrl(tokenizer.nextToken());
        protocol = tokenizer.nextToken();

        return startLine;
    }


    public Map<String, String> getCookie() {
        if (cookieMap == null) {
            cookieMap = new HashMap<>();
        }
        Object cookieObj = getHeaders().get("Cookie");
        if (cookieObj != null) {
            String cookieStr = cookieObj.toString();
            if (cookieStr.toString().indexOf(";") > -1) {
                String[] cookies = cookieStr.split(";");
                for (String cookie : cookies) {
                    if (cookie.indexOf("=") > -1) {
                        String[] keyVlaue = cookie.split("=");
                        cookieMap.put(keyVlaue[0].trim().toUpperCase(), keyVlaue[1].trim());
                    }
                }
            } else {
                if (cookieStr.indexOf("=") > -1) {
                    String[] keyVlaue = cookieStr.split("=");
                    cookieMap.put(keyVlaue[0].trim().toUpperCase(), keyVlaue[1].trim());
                }
            }
        }
        return cookieMap;
    }

    private void readPostData(InternetInputStream stream) throws IOException {
        String contenLength = getRequestHeader("Content-Length");
        if (contenLength == null) return;

        int postLength = Integer.parseInt(contenLength);
        postData = new byte[postLength];

        int length = -1;
        int offset = stream.read(postData);
        while (offset >= 0 && offset < postData.length) {
            length = stream.read(postData, offset, postData.length - offset);
            if (length == -1) {
                break;
            }
            offset += length;
        }
    }

    public void parseUrl(String aUrl) {
        int queryIndex = aUrl.indexOf('?');
        if (queryIndex < 0) {
            url = aUrl;
        } else {
            url = aUrl.substring(0, queryIndex);
            if ((queryIndex + 1) < aUrl.length())
                query = aUrl.substring(queryIndex + 1);
        }
    }

    public String getRequestHeader(String key) {
        if (headers.get(key) != null) {
            return headers.get(key).toString();
        }
        return null;
    }

    public String getRequestHeader(String key, String defaultValue) {
        Object val = getRequestHeader(key);
        return (val == null) ? defaultValue : val.toString();
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getQuery() {
        return query;
    }


    public String getContentType() {
        String contentType = getRequestHeader("Content-Type");
        if (contentType == null) {
            contentType = getRequestHeader("Content-type");
        }
        return contentType;
    }


    public Map getParameterMap() {
        if (httpVariableMap == null) {
            httpVariableMap = createQueryMap(query);
            if (postData != null) {
                String contentType = this.getContentType();
                if (contentType != null && contentType.indexOf("multipart/form-data") == -1 && contentType.indexOf("application/json") == -1) {
                    httpVariableMap.putAll(createQueryMap(new String(postData)));
                }
            }
        }
        return httpVariableMap;
    }

    public OgnlContext getOgnlContext() {
        if (context == null) {
            OgnlValueStack valueStack = (OgnlValueStack) ActionContext.getContext().getValueStack();
            Map<String, Object> valueMap = new HashMap();
            if (this.httpVariableMap != null) {
                valueMap.putAll(this.httpVariableMap);
            }
            if (this.session != null) {
                valueMap.putAll(this.session);
            }
            OgnlRuntime.clearCache();
            context = new OgnlContext(OgnlValueStack.getAccessor(), JDSConverter.getInstance(), null, valueMap);

        }
        return context;
    }

    public String getPath() {
        String path = this.getUrl();
        try {
            URL url = new URL(this.getUrl());
            path = url.getPath();
        } catch (MalformedURLException e) {
            //e.printStackTrace();
        }
        return path;
    }

    public String getParameter(String key) {
        httpVariableMap = this.getParameterMap();
        return (String) httpVariableMap.get(key);
    }

    public Set getParameterNames() {
        return getParameterMap().keySet();
    }

    public Map createQueryMap(String query) {
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
                    queryMap.put(URLDecoder.decode(field, DEFAULT_UDPCODE), "");
                } else {
                    queryMap.put(URLDecoder.decode(field.substring(0, index), DEFAULT_UDPCODE),
                            URLDecoder.decode(field.substring(index + 1), DEFAULT_UDPCODE));
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return queryMap;
    }

    public String getScheme() {
        return scheme;
    }

    public String getProtocol() {
        return protocol;
    }

    public byte[] getPostData() {
        return postData;
    }

    public boolean isKeepAlive() {
        if ("Keep-Alive".equalsIgnoreCase(getRequestHeader(connectionHeader))) {
            return true;
        } else if ("close".equalsIgnoreCase(getRequestHeader(connectionHeader))) {
            return false;
        } else if (major >= 1 && minor > 0) {
            return true;
        } else {
            return false;
        }
    }

    public int getMajorVersion() {
        return major;
    }

    public int getMinorVersion() {
        return minor;
    }

    public String getConnectionHeader() {
        return connectionHeader;
    }

    public long getTimestamp() {
        return timeStamp;
    }

    public String getProperty(String key) {
        return getProperty(key, null);
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String toString() {
        return method + " " + url + ((query != null) ? "?" + query : "") + " " + protocol;
    }

    public String serverUrl() {
        return getProperty("url");
    }

    public String createUrl(String absolutePath) throws IOException {
        return absolutePath;
    }

    public boolean isProtocolVersionLessThan(int aMajor, int aMinor) {
        return (major <= aMajor && minor < aMinor);
    }

    public Map getSession() {
        return session;
    }

    public void setSession(Map session) {
        this.session = session;
    }

}
