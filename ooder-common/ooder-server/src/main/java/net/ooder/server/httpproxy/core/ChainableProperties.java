package net.ooder.server.httpproxy.core;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

public class ChainableProperties extends Properties {

    Properties parent;

    public ChainableProperties() {
        parent = null;
    }

    public ChainableProperties( Properties parent ) {
        this.parent = parent;
    }

    public Properties getParent() {
        return parent;
    }

    public String getProperty(String key) {
        String value = super.getProperty(key);
        if( value == null && parent != null ) {
            value = parent.getProperty( key );
        }
        value = resolveVariables(value, null);
        return value;
    }

    public String getProperty(String key, String defaultValue) {
        String value = super.getProperty(key, defaultValue );
        if( value == null && parent != null ) {
            value = parent.getProperty( key, defaultValue );
        }
        value = resolveVariables(value, defaultValue);
        return value;
    }

    private String resolveVariables(String value, String defaultValue) {
        if( value != null ) {
            int start = value.indexOf("${"); //todo provide a mechanism to escape sequence the ${
            StringBuffer buf = new StringBuffer( value.substring( 0, (start >= 0 ? start : value.length()) ) );
            while( start >= 0 ) {
                int end = value.indexOf('}', start );
                String tmp = getProperty( value.substring( start + 2, end ), defaultValue );
                if( tmp != null ) {
                    buf.append( tmp );
                } else {
                    buf.append( value.substring( start, end + 1 ) );
                }
                start = value.indexOf( "${", end );
                buf.append( value.substring( end + 1, (start >= 0 ? start : value.length()) ) );
            }
            return buf.toString();
        } else {
            return defaultValue;
        }
    }

    public Enumeration propertyNames() {
        Vector set = new Vector( parent.keySet() );
        set.addAll( super.keySet() );
        return set.elements();
    }

    public synchronized Object get(Object key) {
        Object value = super.get( key );
        if( value == null && parent != null ) {
            value = parent.get( key );
        }
//        if( value instanceof String ) {
//            value = resolveVariables( (String)value, null );
//        }
        return value;
    }

    public synchronized boolean containsKey(Object key) {
        boolean contains = super.containsKey( key );
        if( !contains && parent != null ) {
            contains = parent.containsKey( key );
        }
        return contains;
    }

    public boolean containsValue(Object value) {
        boolean contains = super.containsValue( value );
        if( !contains && parent != null ) {
            contains = parent.containsValue( value );
        }
        return contains;
    }

    public synchronized boolean contains(Object value) {
        boolean contains = super.contains( value );
        if( !contains && parent != null ) {
            contains = parent.contains( value );
        }
        return contains;
    }

    public void list(PrintStream out) {
        super.list(out);
        parent.list(out);
    }

    public void list(PrintWriter out) {
        super.list(out);
        parent.list(out);
    }
}
