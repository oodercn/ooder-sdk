package net.ooder.server.httpproxy.handler.multipart;

import org.apache.commons.fileupload.RequestContext;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class SimpleRequestContext implements RequestContext {
    private final Charset charset;            // 编码
    private final String contentType;    // contentType
    private final InputStream content;        // 数据

    public SimpleRequestContext(Charset charset, String contentType, InputStream content) {
        this.charset = charset;
        this.contentType = contentType;
        this.content = content;
    }

    @Override
    public String getCharacterEncoding() {
        return this.charset.displayName();
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public int getContentLength() {
        try {
            return this.content.available();
        } catch (IOException e) {
        }
        return 0;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.content;
    }
}