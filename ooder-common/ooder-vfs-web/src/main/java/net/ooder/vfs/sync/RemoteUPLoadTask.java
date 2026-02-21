package net.ooder.vfs.sync;

import  net.ooder.web.RemoteConnectionManager;
import org.apache.http.HttpEntity;
import org.apache.http.client.fluent.Async;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.util.CharsetUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.Future;

public class RemoteUPLoadTask implements Runnable {

    private final static String PATHNAME = "path";
    private String path;
    private File localFile;
    private String url;

    public RemoteUPLoadTask(String url, String path, File localFile) {
        this.path = path;
        this.url = url;
        this.localFile = localFile;
    }

    public void run() {
        Async async = Async.newInstance().use(RemoteConnectionManager.getConntctionService(url));
        Request request = Request.Post(url);
        HttpEntity reqEntity = null;
        try {
            FileBody fileBody = new FileBody(localFile);
            reqEntity = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE).addTextBody(PATHNAME, path)
                    .addPart("file", fileBody).setCharset(CharsetUtils.get("utf-8")).build();
            request.body(reqEntity);
            Future<Content> future = async.execute(request, new FutureCallback<Content>() {
                public void failed(final Exception ex) {
                    ex.printStackTrace();
                }

                public void completed(final Content content) {
                    System.out.println(content.toString());
                }

                public void cancelled() {
                }
            });

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
