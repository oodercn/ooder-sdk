/**
 * $RCSfile: HsqlDbCache.java,v $
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
package net.ooder.hsql;

import net.ooder.common.cache.Cache;
import net.ooder.common.cache.CacheSizes;
import net.ooder.common.cache.Cacheable;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * 
 * @author wenzhang
 *
 */
public class HsqlDbCache implements Cache {
	protected Connection conn;
    
	public static final String TABLE_NAME = HsqlDbCacheManager.TABLE_NAME;
	public static final String DELETE_CACHE_SQL = "DELETE FROM " + TABLE_NAME + " WHERE CACHE_NAME=? AND CACHE_KEY=? ";
	public static final String DELETE_CACHE_BYTIME_SQL = "DELETE FROM " + TABLE_NAME + " WHERE CACHE_NAME=? AND UPDATE_TIME<? ";
	public static final String DELETE_CACHE_BYNAME_SQL = "DELETE FROM " + TABLE_NAME + " WHERE CACHE_NAME=? ";
	public static final String INSERT_CACHE_SQL = "INSERT INTO " + TABLE_NAME + " (CACHE_NAME, CACHE_KEY, CACHE_VALUE, UPDATE_TIME) VALUES(?, ?, ?, ?)";

	public static final String LOAD_ALL_CACHE_SQL = "SELECT CACHE_NAME, CACHE_KEY, CACHE_VALUE, UPDATE_TIME FROM " + TABLE_NAME;
	public static final String LOAD_CACHE_SQL = "SELECT CACHE_NAME, CACHE_KEY, CACHE_VALUE, UPDATE_TIME FROM " + TABLE_NAME + " WHERE CACHE_NAME=? AND CACHE_KEY=?";
	public static final String LOAD_CACHE_BYNAME_SQL = "SELECT CACHE_NAME, CACHE_KEY, CACHE_VALUE, UPDATE_TIME FROM " + TABLE_NAME + " WHERE CACHE_NAME=?";
	public static final String LOAD_CACHE_COUNT_SQL = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE CACHE_NAME=?";
	public static final String LOAD_CACHE_BYNAME_ORDERED_SQL = "SELECT CACHE_NAME, CACHE_KEY, CACHE_VALUE, UPDATE_TIME FROM " + TABLE_NAME + " WHERE CACHE_NAME=? ORDER BY UPDATE_TIME";

   /**
	* Maximum size in bytes that the cache can grow to.
	*/
	protected int maxCacheSize;

	/**
	 * Maintains the current size of the cache in bytes.
	 */
	protected int cacheSize = 0;

	/**
	 * Maximum length of time objects can exist in cache before expiring.
	 */
	protected long maxLifetime;

	/**
	 * Maintain the number of cache hits and misses. A cache hit occurs every
	 * time the get method is called and the cache contains the requested
	 * object. A cache miss represents the opposite occurence.<p>
	 *
	 * Keeping track of cache hits and misses lets one measure how efficient
	 * the cache is; the higher the percentage of hits, the more efficient.
	 */
	protected long cacheHits, cacheMisses = 0L;

	/**
	 * The name of the cache.
	 */
	protected String name;

	private boolean isFull;
	protected boolean isStopPutWhenFull;
	
	/**
	 * Create a new cache and specify the maximum size of for the cache in
	 * bytes, and the maximum lifetime of objects.
	 *
	 * @param name a name for the cache.
	 * @param maxSize the maximum size of the cache in bytes. -1 means the cache
	 *      has no max size.
	 * @param maxLifetime the maximum amount of time objects can exist in
	 *      cache before being deleted. -1 means objects never expire.
	 */
	public HsqlDbCache(String name, int maxSize, long maxLifetime, HsqlDbServer db) {
		this.name = name;
		this.maxCacheSize = maxSize;
		this.maxLifetime = maxLifetime;
		this.conn = db.newConnection();
	}

	public synchronized Object put(Object key, Object value) {
        if (key == null) {
            return null;
        }
		if (isStopPutWhenFull) {
		    if (isFull) {
		        return value;
		    }
		}
		int objectSize = calculateSize(value);

		// If the object is bigger than the entire cache, simply don't add it.
		if (maxCacheSize > 0 && objectSize > maxCacheSize * .90) {
//			  System.out.println("Cache: " + name + " -- object with key " + key + " is too large to fit in cache. Size is " + objectSize);
			return value;
		}
		cacheSize += objectSize;
		
	
		if (isStopPutWhenFull) { 
		    if (maxCacheSize > 0 && cacheSize > maxCacheSize) {
		        isFull = true;
		        return value;
		    }
		}
		
		// Delete an old entry if it exists.
		remove(key);

		addToDb(key, value);
		
		if (!isStopPutWhenFull) {
			// If cache is too full, remove least used cache entries until it is
			// not too full.
			cullCache();
		}

		return value;
	}

	/**
     * @param key
     * @param value
     */
    private void addToDb(Object key, Object value) {
        PreparedStatement pst = null;
        try {
	        pst = conn.prepareStatement(INSERT_CACHE_SQL);
			pst.setString(1, name);
			pst.setString(2, key.toString());
			pst.setObject(3, value);
			pst.setLong(4, System.currentTimeMillis());
			pst.executeUpdate();
        } catch(Exception e) {
        } finally {
            try {
	            pst.close();
            } catch(Exception ex) {
            	ex.printStackTrace();
			}
        }
    }

    private Object[] loadAllFromDb() {
	PreparedStatement pst = null;
	List result = new ArrayList();
	try {
	    pst = conn.prepareStatement(LOAD_CACHE_BYNAME_SQL);
	    pst.setString(1, name);
	    ResultSet rs = pst.executeQuery();
	    if (rs != null) {
		while (rs.next()) {
		    result.add(rs.getObject(3));
		}
		rs.close();
	    }
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
	    try {
		pst.close();
	    } catch (Exception ex) {
	    }
	}
	return result.toArray();
    }
    
    private Object loadFromDb(Object key) {
        PreparedStatement pst = null;
        Object o = null;
        try {
	        pst = conn.prepareStatement(LOAD_CACHE_SQL);
			pst.setString(1, name);
			pst.setString(2, key.toString());
			ResultSet rs = pst.executeQuery();
			if (rs != null && rs.next()) {
			    o = rs.getObject(3);
			    rs.close();
			}
        } catch(Exception e) {
        } finally {
            try {
	            pst.close();
            } catch(Exception ex) {}
        }
        
        return o;
    }
    
    private Object loadAgestFromDb() {
      PreparedStatement pst = null;
      Object o = null;
      try {
	        pst = conn.prepareStatement(LOAD_CACHE_BYNAME_ORDERED_SQL);
			pst.setString(1, name);
			ResultSet rs = pst.executeQuery();
			if (rs != null && rs.next()) {
			    o = rs.getObject(3);
			    rs.close();
			}
      } catch(Exception e) {
      } finally {
          try {
	            pst.close();
          } catch(Exception ex) {}
      }
      
      return o;
  }
    private void removeFromDb(Object key) {
        PreparedStatement pst = null;
        try {
	        pst = conn.prepareStatement(DELETE_CACHE_SQL);
			pst.setString(1, name);
			pst.setString(2, key.toString());
			pst.executeUpdate();
        } catch(Exception e) {
        } finally {
            try {
	            pst.close();
            } catch(Exception ex) {}
        }
    }
    
    public synchronized Object get(Object key) {
        if (key == null) {
            return null;
        }
		// First, clear all entries that have been in cache longer than the
		// maximum defined age.
		deleteExpiredEntries();

		return loadFromDb(key);
	}

	public synchronized Object remove(Object key) {
        if (key == null) {
            return null;
        }
		Object object = loadFromDb(key);
		// If the object is not in cache, stop trying to remove it.
		if (object == null) {
			return null;
		}
		// remove from the hash map
		removeFromDb(key);
	    isFull = false;
	    cacheSize -= calculateSize(object);
		return object;
	}

	public synchronized void clear() {
        PreparedStatement pst = null;
        try {
	        pst = conn.prepareStatement(DELETE_CACHE_BYNAME_SQL);
	        pst.setString(1, name);
			pst.executeUpdate();
        } catch(Exception e) {
        } finally {
            try {
	            pst.close();
            } catch(Exception ex) {}
        }

		cacheSize = 0;
		cacheHits = 0;
		cacheMisses = 0;
		isFull = false;
	}

	public int size() {
		// First, clear all entries that have been in cache longer than the
		// maximum defined age.
		deleteExpiredEntries();

        PreparedStatement pst = null;
        int size = 0;
        try {
	        pst = conn.prepareStatement(LOAD_CACHE_COUNT_SQL);
			pst.setString(1, name);
			ResultSet rs = pst.executeQuery();
			if (rs != null && rs.next()) {
			    size = rs.getInt(1);
			    rs.close();
			}
        } catch(Exception e) {
        } finally {
            try {
	            pst.close();
            } catch(Exception ex) {}
        }
        
		return size;
	}

	public boolean isEmpty() {
		// First, clear all entries that have been in cache longer than the
		// maximum defined age.
		deleteExpiredEntries();

		return false;
	}

	public Collection values() {
		// First, clear all entries that have been in cache longer than the
		// maximum defined age.
		deleteExpiredEntries();

		Object [] objects = loadAllFromDb();
		Object [] values = new Object[objects.length];
		for (int i=0; i<objects.length; i++) {
			values[i] = objects[i];
		}
		return Collections.unmodifiableList(Arrays.asList(values));
	}

	public boolean containsKey(Object key) {
		// First, clear all entries that have been in cache longer than the
		// maximum defined age.
		deleteExpiredEntries();

		return (loadFromDb(key) != null);
	}

	public void putAll(Map map) {
		for (Iterator i=map.keySet().iterator(); i.hasNext(); ) {
			Object key = i.next();
			Object value = map.get(key);
			put(key, value);
		}
	}

	public boolean containsValue(Object value) {
		// First, clear all entries that have been in cache longer than the
		// maximum defined age.
		deleteExpiredEntries();

		int objectSize = calculateSize(value);
//		CacheObject cacheObject = new CacheObject(value, objectSize);
		return false;//map.containsValue(cacheObject);
	}

	public Set entrySet() {
		// First, clear all entries that have been in cache longer than the
		// maximum defined age.
		deleteExpiredEntries();

//		return Collections.unmodifiableSet(map.entrySet());
		return null;
	}

	public Set keySet() {
		// First, clear all entries that have been in cache longer than the
		// maximum defined age.
		deleteExpiredEntries();

//		return Collections.unmodifiableSet(map.keySet());
		return null;
	}

	/**
	 * Implementation methods of Cache interface
	 */
	public String getName() {
		return name;
	}

	public long getCacheHits() {
		return cacheHits;
	}

	public long getCacheMisses() {
		return cacheMisses;
	}

	public int getCacheSize() {
		return cacheSize;
	}

	public int getMaxCacheSize() {
		return maxCacheSize;
	}

	public void setMaxCacheSize(int maxCacheSize) {
		this.maxCacheSize = maxCacheSize;
		// It's possible that the new max size is smaller than our current cache
		// size. If so, we need to delete infrequently used items.
		cullCache();
	}

	public long getMaxLifetime() {
		return maxLifetime;
	}

	public void setMaxLifetime(long maxLifetime) {
		this.maxLifetime = maxLifetime;
	}

	/**
	 * Returns the size of an object in bytes. Determining size by serialization
	 * is only used as a last resort.
	 *
	 * @return the size of an object in bytes.
	 */
	protected int calculateSize(Object object) {
	    if (object == null) {
	        return 4;
	    }
		// If the object is Cacheable, ask it its size.
		if (object instanceof Cacheable) {
			return ((Cacheable)object).getCachedSize();
		} else if (object instanceof String) {
		    return CacheSizes.sizeOfString((String)object);
		}
		// Coherence puts DataInputStream objects in cache.
		else if (object instanceof DataInputStream) {
			int size = 1;
			try {
				size = ((DataInputStream)object).available();
			}
			catch (IOException ioe) { }
			return size;
		}
		// Check for other common types of objects put into cache.
		else if (object instanceof Long) {
			return CacheSizes.sizeOfLong();
		}
		else if (object instanceof Integer) {
			return CacheSizes.sizeOfObject() + CacheSizes.sizeOfInt();
		}
		else if (object instanceof Boolean) {
			return CacheSizes.sizeOfObject() + CacheSizes.sizeOfBoolean();
		}
		else if (object instanceof long []) {
			long [] array = (long [])object;
			return CacheSizes.sizeOfObject() + array.length * CacheSizes.sizeOfLong();
		}
		// Default behavior -- serialize the object to determine its size.
		else {
			int size = 1;
//			try {
//				// Default to serializing the object out to determine size.
//				NullOutputStream out = new NullOutputStream();
//				ObjectOutputStream outObj = new ObjectOutputStream(out);
//				outObj.writeObject(object);
//				size = out.size();
//			}
//			catch (IOException ioe) {
//				ioe.printStackTrace();
//			}
			return size;
		}
	}

	/**
	 * Clears all entries out of cache where the entries are older than the
	 * maximum defined age.
	 */
	protected void deleteExpiredEntries() {
		// Check if expiration is turned on.
		if (maxLifetime <= 0) {
			return;
		}
		// Remove all old entries. To do this, we remove objects from the end
		// of the linked list until they are no longer too old. We get to avoid
		// any hash lookups or looking at any more objects than is strictly
		// neccessary.
		long expireTime = System.currentTimeMillis() - maxLifetime;
		PreparedStatement pst = null;
		try {
		    pst = conn.prepareStatement(DELETE_CACHE_BYTIME_SQL);
		    pst.setString(1, name);
		    pst.setLong(2, expireTime);
		    pst.executeUpdate();
		} catch(Exception e) {
            try {
	            pst.close();
            } catch(Exception ex) {}
		}
	}

	/**
	 * Removes objects from cache if the cache is too full. "Too full" is
	 * defined as within 3% of the maximum cache size. Whenever the cache is
	 * is too big, the least frequently used elements are deleted until the
	 * cache is at least 10% empty.
	 */
	protected void cullCache() {
		// Check if a max cache size is defined.
		if (maxCacheSize < 0) {
			return;
		}
		// See if the cache size is within 3% of being too big. If so, clean out
		// cache until it's 10% free.
		if (cacheSize >= maxCacheSize * .97) {
			// First, delete any old entries to see how much memory that frees.
			deleteExpiredEntries();
			int desiredSize = (int)(maxCacheSize * .95);
			while (cacheSize > desiredSize) {
				// Get the key and invoke the remove method on it.
				remove(loadAgestFromDb());
			}
		}
	}

	public void setStopPutWhenFull(boolean flag) {
	    this.isStopPutWhenFull = flag;
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
