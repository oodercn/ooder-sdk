/**
 * $RCSfile: Base64JsonHttpMessageConverter.java,v $
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
 
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.FileCopyUtils;
 
import java.io.IOException;
import java.util.List;
 

public class Base64JsonHttpMessageConverter implements HttpMessageConverter {
    @Override public boolean canRead(Class clazz, MediaType mediaType) {
        return false;
    }
 
    @Override public boolean canWrite(Class clazz, MediaType mediaType) {
        return false;
    }
 
    @Override public List<MediaType> getSupportedMediaTypes() {
        return null;
    }
 
    @Override public Object read(Class clazz, HttpInputMessage inputMessage)
        throws IOException, HttpMessageNotReadableException {
        return null;
    }
 
    @Override
    public void write(Object object, MediaType contentType, HttpOutputMessage outputMessage)
        throws IOException, HttpMessageNotWritableException {
        byte[] bytes = object.toString().getBytes();
        FileCopyUtils.copy(Base64.encodeBase64(bytes), outputMessage.getBody());
    }
}