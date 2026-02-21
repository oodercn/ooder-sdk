/**
 * $RCSfile: FileSyncListener.java,v $
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
package net.ooder.index.listener;

import java.nio.file.*;
import java.util.List;

public class FileSyncListener implements Runnable{

    
    private String path;
    private String vfsPath;    


   public FileSyncListener(String path, String vfsPath){
	this.vfsPath=vfsPath;
	this.path=path;
	
    }
    
   
    void addListener(String path){
  	 Path myDir = Paths.get(path);       
  	        try {
  	           WatchService watcher = myDir.getFileSystem().newWatchService();
  	           myDir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, 
  	           StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
  	           WatchKey watckKey = watcher.take();
  	           List<WatchEvent<?>> events = watckKey.pollEvents();
  	           for (WatchEvent event : events) {
  	                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
						System.out.println("Created: " + event.context().toString());
  	                }
  	                if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
  	                    System.out.println("Delete: " + event.context().toString());
  	                }
  	                if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
  	                    System.out.println("Modify: " + event.context().toString());
  	                }
  	            }
  	           addListener(path); 	         
  	        } catch (Exception e) {
  	            System.out.println("Error: " + e.toString());
  	        }
      }
      
    
    @Override
    public void run() {
	
	addListener(path);
    }

}


