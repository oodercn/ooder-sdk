/**
 * $RCSfile: LocalMultipartFile.java,v $
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
package net.ooder.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

public class LocalMultipartFile implements MultipartFile {

    InputStream inputStream;

    String contentType;

    String name;

    String originalFilename;

    long size;

    
    
    public LocalMultipartFile(File file) {
	try {
	    this.inputStream=new FileInputStream(file);
	    this.name=file.getName();
	    this.size=file.length();
	    this.originalFilename=file.getAbsolutePath();
	 
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
	
    }

    public LocalMultipartFile(InputStream inputStream) {
	this.inputStream = inputStream;
	
    }

    @Override
    public byte[] getBytes() throws IOException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public String getContentType() {
	return contentType;
    }

    public void setInputStream(InputStream inputStream) {
	this.inputStream = inputStream;
    }

    public void setContentType(String contentType) {
	this.contentType = contentType;
    }

    public void setName(String name) {
	this.name = name;
    }

    public void setOriginalFilename(String originalFilename) {
	this.originalFilename = originalFilename;
    }

    public void setSize(Integer size) {
	this.size = size;
    }

    @Override
    public InputStream getInputStream() throws IOException {
	return inputStream;
    }

    @Override
    public String getName() {
	return name;
    }

    @Override
    public String getOriginalFilename() {
	return originalFilename;
    }

    @Override
    public long getSize() {
	return size;
    }

    @Override
    public boolean isEmpty() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public void transferTo(File arg0) throws IOException, IllegalStateException {
	// TODO Auto-generated method stub

    }

}
