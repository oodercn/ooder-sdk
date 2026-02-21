package net.ooder.server.httpproxy.core;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public class PrintWriterResponseData implements ResponseData {
    PrintWriter writer;
    StringWriter backEnd;

    public PrintWriterResponseData() {
        backEnd = new StringWriter();
        writer = new PrintWriter( backEnd );
    }

    public long getLength() {
        writer.flush();
        return backEnd.getBuffer().length();
    }

    public PrintWriter getPrintWriter() {
        return writer;
    }

    public void send(OutputStream os) throws IOException {
        writer.flush();
        os.write( backEnd.toString().getBytes() );
    }
}
