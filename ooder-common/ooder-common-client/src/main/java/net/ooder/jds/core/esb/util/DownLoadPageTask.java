/**
 * $RCSfile: DownLoadPageTask.java,v $
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
package net.ooder.jds.core.esb.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.ooder.common.util.IOUtility;

import net.ooder.thread.JDSThreadFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.fluent.Async;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.CharsetUtils;

public class DownLoadPageTask {

	//默认并行线程数
   static ExecutorService threadpool = Executors.newFixedThreadPool(100,new JDSThreadFactory("DownLoadPageTask.threadpool"));
	 
     Async async = Async.newInstance().use(threadpool);
     
     static DownLoadPageTask task;
     
     public static DownLoadPageTask getInstance(){
    	 if (task==null){
    		 task=new DownLoadPageTask();
    	 }
    	 return task;
     }
  
     
      
     public Future<Content> getFuture(final String url, final File file){
    	 Request request= Request.Get(url);
    	
    	 Future<Content> future = async.execute(request, new FutureCallback<Content>() {
             public void failed(final Exception ex) {
            	 //错误处理
             }
             public void completed(final Content content) {
            	 try {
            		// System.out.println("url="+url+" save file" +file.getName());
					copyStreamToFile(content.asStream(),file);
				} catch (IOException e) {
					e.printStackTrace();
				}
             }
             public void cancelled() {
             }
         });
    	
		return future;
     }
     
     public void shutdown(){
    	 threadpool.shutdown();
     }
     
	 private void copyStreamToFile(InputStream input ,File file) throws IOException{
	     if (file.getParentFile() != null && !file.getParentFile().exists()) {
	       	   file.getParentFile().mkdirs();
	          }
	          if (file.exists() && !file.canWrite()) {
	              final String message = "Unable to open file " + file + " for writing.";
	              throw new IOException(message);
	          }	      
	          final FileOutputStream output = new FileOutputStream(file);
	          IOUtility.copy(input, output);
	          IOUtility.shutdownStream(input);
	          IOUtility.shutdownStream(output);
	    }
	 
	 
	 public static void main(String[] args) {
		 
		 
		 
		 HttpClient httpclient = new DefaultHttpClient();  
		 
			CloseableHttpResponse httpResponse = null;
			String localFilePath="C:\\Users\\wenzhang\\workspace\\bsisoft\\itjds\\vfs\\net\\itjds\\vfs\\Folder.java";
				
				// 把文件转换成流对象FileBody
				File localFile = new File(localFilePath);
				FileBody fileBody = new FileBody(localFile);
				// 以浏览器兼容模式运行，防止文件名乱码。
				HttpEntity reqEntity;
				try {
					reqEntity = MultipartEntityBuilder.create()
					.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
					.addPart("Image_card", fileBody)
				//		.addPart("Image_best", fileBody)
						//	.addPart("Image_env", fileBody)
								//	.addPart("houseID", new StringBody("houseId"))
							//			.addPart("delta", new StringBody("delta"))
									
					.setCharset(CharsetUtils.get("UTF-8")).build();
					// uploadFile对应服务端类的同名属性File类型>
					// .addPart("uploadFileName", uploadFileName)
					// uploadFileName对应服务端类的同名属性String类型>

					HttpPost httpPost = new HttpPost("http://smart.fvt.tjia.com/UploadServlet");
					httpPost.setEntity(reqEntity);
					httpResponse = (CloseableHttpResponse) httpclient.execute(httpPost);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
	
	 }
		 
	
//		 HttpClient httpclient = new DefaultHttpClient();  
//		  HttpPost post = new HttpPost("http://localhost:8080/action.jsp");  
//		 FileBody fileBody = new FileBody(new File("/home/sendpix0.jpg"));  
//		StringBody stringBody = new StringBody("文件的描述");  
//		 MultipartEntity entity = new MultipartEntity();  
//		 entity.addPart("file", fileBody);  
//		   entity.addPart("desc", stringBody);  
//		 post.setEntity(entity);  
//		  HttpResponse response;
//		try {
//			response = httpclient.execute(post);
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}  
//		if(HttpStatus.SC_OK==response.getStatusLine().getStatusCode()){    
//		    
//		  HttpEntity entitys = response.getEntity();  
//		   if (entity != null) {  
//		              System.out.println(entity.getContentLength());  
//		               System.out.println(EntityUtils.toString(entitys));  
//		         }  
//		      }  
//		        httpclient.getConnectionManager().shutdown();  
//
//		 
//	 }
}
