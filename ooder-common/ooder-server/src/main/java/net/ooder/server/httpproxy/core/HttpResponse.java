package net.ooder.server.httpproxy.core;

import org.apache.http.impl.cookie.BasicClientCookie;

import java.io.*;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.*;

public class HttpResponse extends Response {

    private int statusCode = HttpURLConnection.HTTP_OK;

    private String mimeType = "text/html";

    private HttpHeaders responseHeaders;

    private InternetOutputStream stream;

    private ResponseDataList dataStreamList;

    private HttpRequest request;

    private Map<String,BasicClientCookie> cookieMap=new HashMap<>();

    private boolean keepConnectionOpen;

    private ResponseListener responseListener;


    public HttpResponse(HttpRequest request, OutputStream aStream) {
        this(request, aStream, null);
    }

    public HttpResponse(HttpRequest request, OutputStream aStream, ResponseListener listener) {
        this.stream = new InternetOutputStream(aStream);
        this.request = request;
        this.dataStreamList = new ResponseDataList();
        this.responseHeaders = new HttpHeaders();
        this.keepConnectionOpen = request.isKeepAlive();
        this.responseListener = listener;
    }

    public boolean isKeepAlive() {
        return (keepConnectionOpen && request.isKeepAlive());
    }

    public void addHeader(String key, String value) {
        responseHeaders.put(key, value);
    }


    public BasicClientCookie addCookie(String key, String value) {
        BasicClientCookie clientCookie =cookieMap.get(key);
        if (clientCookie==null){
             clientCookie = new BasicClientCookie(key, value);
            cookieMap.put(key,clientCookie);
        }else{
            clientCookie.setValue(value);
        }
        return clientCookie;
    }

    public BasicClientCookie getCookie(String key) {
        BasicClientCookie clientCookie =cookieMap.get(key);
        return clientCookie;
    }


    public PrintWriter getPrintWriter() {
        return dataStreamList.addPrintWriter();
    }

    public void setMimeType(String aMimeType) {
        mimeType = aMimeType;
    }

    public void sendError(int statusCode, String errorMessage) {
        sendError(statusCode, errorMessage, null);
    }

    public void sendError(int statusCode, String errorMessage, Exception e) {
        keepConnectionOpen = false;
        if (errorMessage == null) {
            errorMessage = "";
        }
        String body = "<html>\n<head>\n"
                + "<title>Error: " + statusCode + "</title>\n"
                + "<body>\n<h1>" + statusCode + " <b>"
                + Http.getStatusPhrase(statusCode)
                + "</b></h1><br>\nThe requested URL <b>"
                + ((request.getUrl() == null) ? "<i>unknown URL</i>" : Http.encodeHtml(request.getUrl()))
                + "</b>\n " + Http.encodeHtml(errorMessage)
                + "\n<hr>";
        if (e != null) {
            StringWriter writer = new StringWriter();
            writer.write("<pre>");
            e.printStackTrace(new PrintWriter(writer));
            writer.write("</pre>");
            body += writer.toString();
        }
        body += "</body>\n</html>";

        this.dataStreamList.reset();
        this.statusCode = statusCode;
        this.mimeType = "text/html";
        PrintWriter out = getPrintWriter();
        out.write(body);
    }

    public void sendJSONResponse(String body) {
        sendResponse(body, "application/json;charset=UTF-8");
    }

    public void sendResponse(String body, String mimeType) {
        if (mimeType != null) {
            this.mimeType = mimeType;
        }

        ByteArrayInputStream input = new ByteArrayInputStream(body.getBytes(Charset.forName("utf-8")));
        try {
            this.sendResponse(input, body.getBytes(Charset.forName("utf-8")).length);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendResponse(String body) {
        this.mimeType = "text/html";
        PrintWriter out = getPrintWriter();
        out.write(body);
    }

    public void sendResponse(InputStream is, int length) throws IOException {
        this.dataStreamList.addResponse(is, length);
    }

    public void sendResponse(InputStream is, long beginning, long ending) throws IOException {
        this.dataStreamList.addResponse(is, beginning, ending - beginning);
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void commitResponse() throws IOException {
        try {
            startTransfer();
            sendHttpReply(statusCode);
            sendHeaders(mimeType, dataStreamList.getTotalLength());
            if (!isHeadMethod()) {
                sendBody();
            }

            endTransfer();

        } catch (IOException e) {
            endTransfer(e);
            throw e;
        }
    }

    private void sendBody() throws IOException {
        dataStreamList.sendData(stream, !request.isProtocolVersionLessThan(1, 1));
    }

    private void sendHttpReply(int code) throws IOException {
        StringBuffer buffer = new StringBuffer(request.getProtocol());
        buffer.append(" ");
        buffer.append(code);
        buffer.append(" ");
        buffer.append(Http.getStatusPhrase(code));
        buffer.append(Http.CRLF);
        stream.write(buffer.toString().getBytes());
    }

    private void sendHeaders(String mimeType, long contentLength) throws IOException {
        responseHeaders.put("Date", Http.getCurrentTime());
        responseHeaders.put("Server", "JDS");
        if (isKeepAlive()) {
            responseHeaders.put("Keep-Alive", "timeout=200");
            if (contentLength >= 0) {
                responseHeaders.put("age", "3600");
                responseHeaders.put("Content-Length", Long.toString(contentLength));
            } else if (!request.isProtocolVersionLessThan(1, 1)) {
                responseHeaders.put("Transfer-Encoding", "chunked");
            }

        } else {
            responseHeaders.put(request.getConnectionHeader(), "close");
        }

        if (cookieMap != null && !cookieMap.isEmpty()) {
            List<String> cookies = new ArrayList<>();
            Collection<BasicClientCookie> cookieCollection=cookieMap.values();
            for (BasicClientCookie clientCookie : cookieCollection) {
                String str = clientCookie.getName() + "=" + clientCookie.getValue();
                if (clientCookie.getPath() != null) {
                    str = str + "; Path=" + clientCookie.getPath();
                }
                if (clientCookie.getDomain() != null) {
                    str = str + "; Domain=" + clientCookie.getPath();
                }
                if (clientCookie.getExpiryDate() != null) {
                    str = str + "; Date=" + clientCookie.getExpiryDate().getTime();
                }
                cookies.add(str);
            }
            responseHeaders.put("Set-Cookie", cookies);
        }


        if (mimeType != null) {
            responseHeaders.put("Content-Type", mimeType);
        }
        responseHeaders.print(stream);
    }

    private boolean isHeadMethod() {
        return "HEAD".equalsIgnoreCase(request.getMethod());
    }

    public OutputStream getOutputStream() {
        return stream;
    }

    protected void startTransfer() {
        if (responseListener != null) {
            responseListener.startTransfer(request);
        }
    }

    protected void notifyListeners(int bytesSent, int length) throws IOException {
        if (responseListener != null) {
            responseListener.notify(request, bytesSent, length);
        }
    }

    protected void endTransfer() {
        endTransfer(null);
    }

    protected void endTransfer(Exception e) {
        if (responseListener != null) {
            responseListener.endTransfer(request, e);
        }
    }
}
