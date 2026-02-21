/**
 * $RCSfile: CacheSizes.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/07/08 00:25:49 $
 *
 * Copyright (C) 2003 spk, Inc. All rights reserved.
 *
 * This software is the proprietary information of spk, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: CacheSizes.java,v $
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
package net.ooder.common.cache;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: 常用代码打包</p>
 * <p>Description: 
 * Utility class for determining the sizes in bytes of commonly used objects.
 * Classes implementing the Cacheable interface should use this class to
 * determine their size.
 * <p>
 * 
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: raddev.cn</p>
 * @author wenzhang li
 * @version 1.0
 */
public class CacheSizes {

    /**
     * Returns the size in bytes of a basic Object. This method should only
     * be used for actual Object objects and not classes that extend Object.
     *
     * @return the size of an Object.
     */
    public static int sizeOfObject() {
        return 4;
    }
    
    public static int sizeOfObject(Object obj) {
    	if(obj == null)
    	{
    		return 4;
    	}
    	int size = 0;
    	if (obj instanceof String) {
    		size += sizeOfString((String)obj);
    	}
    	else if (obj instanceof Long) {
    		size += sizeOfLong() + sizeOfObject();
    	}
    	else if (obj instanceof Integer) {
    		size += sizeOfInt() + sizeOfObject();
    	}
    	else if(obj instanceof Double) {
    		size += sizeOfDouble() + sizeOfObject();
    	}
    	else if(obj instanceof Boolean) {
    		size += sizeOfBoolean() + sizeOfObject();
    	}
    	else if(obj instanceof Character) {
    		size += sizeOfChar() + sizeOfObject();
    	}
    	else if(obj instanceof Map) {
    		size += sizeOfMap((Map) obj);
    	}
    	else if(obj instanceof List) {
    		size += sizeOfList((List) obj);
    	}
    	else if(obj instanceof Cacheable) {
    		size += ((Cacheable)obj).getCachedSize();
    	}
    	else {
    		try {
    			// Default to serializing the object out to determine size.
    			NullOutputStream out = new NullOutputStream();
    			ObjectOutputStream outObj = new ObjectOutputStream(out);
    			outObj.writeObject(obj);
    			size += out.size();
    		}
    		catch (IOException ioe) {
    			ioe.printStackTrace();
    			size += 4;
    		}
    	}
    	return size;
    	
    }
    
    /**
     * Returns the size in bytes of a String.
     *
     * @param string the String to determine the size of.
     * @return the size of a String.
     */
    public static int sizeOfString(String string) {
        if (string == null) {
            return 4;
        }
        return 4 + string.length() * 2;
    }

    /**
     * Returns the size in bytes of a primitive int.
     *
     * @return the size of a primitive int.
     */
    public static int sizeOfInt() {
        return 4;
    }

    /**
     * Returns the size in bytes of a primitive char.
     *
     * @return the size of a primitive char.
     */
    public static int sizeOfChar() {
        return 2;
    }

    /**
     * Returns the size in bytes of a primitive boolean.
     *
     * @return the size of a primitive boolean.
     */
    public static int sizeOfBoolean() {
        return 1;
    }

    /**
     * Returns the size in bytes of a primitive long.
     *
     * @return the size of a primitive long.
     */
    public static int sizeOfLong() {
        return 8;
    }

    /**
     * Returns the size in bytes of a primitive double.
     *
     * @return the size of a primitive double.
     */
    public static int sizeOfDouble() {
        return 8;
    }

    /**
     * Returns the size in bytes of a Date.
     *
     * @return the size of a Date.
     */
    public static int sizeOfDate() {
        return 12;
    }

    /**
     * Returns the size in bytes of a Map object. All keys and
     * values <b>must be Strings</b>.
     *
     * @param map the Map object to determine the size of.
     * @return the size of the Map object.
     */
    public static int sizeOfMap(Map map) {
        if (map == null) {
            return 4;
        }
        // Base map object -- should be something around this size.
        int size = 36;
        // Add in size of each value
        Object[] values = map.values().toArray();
        for (int i = 0; i < values.length; i++) {
            size += sizeOfObject(values[i]);
        }
        Object[] keys = map.keySet().toArray();
        // Add in each key
        for (int i = 0; i < keys.length; i++) {
            size += sizeOfObject(keys[i]);
        }
        return size;
    }

    /**
     * Returns the size in bytes of a List object. Elements are assumed to be
     * <tt>String</tt>s, <tt>Long</tt>s or <tt>Cacheable</tt> objects.
     *
     * @param list the List object to determine the size of.
     * @return the size of the List object.
     */
    public static int sizeOfList(List list) {
        if (list == null) {
            return 4;
        }
        // Base list object (approximate)
        int size = 36;
        // Add in size of each value
        Object[] values = list.toArray();
        for (int i = 0; i < values.length; i++) {
            Object obj = values[i];
            size += sizeOfObject(obj);
        }
        return size;
    }
    
    /**
     * An extension of OutputStream that does nothing but calculate the number
     * of bytes written through it.
     */
    private static class NullOutputStream extends OutputStream {

    	int size = 0;

    	public void write(int b) throws IOException  {
    		size++;
    	}

    	public void write(byte[] b) throws IOException {
    		size += b.length;
    	}

    	public void write(byte[] b, int off, int len) {
    		size += len;
    	}

    	/**
    	 * Returns the number of bytes written out through the stream.
    	 *
    	 * @return the number of bytes written to the stream.
    	 */
    	public int size() {
    		return size;
    	}
    }
}

