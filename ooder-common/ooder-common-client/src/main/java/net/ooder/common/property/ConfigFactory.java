/**
 * $RCSfile: ConfigFactory.java,v $
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
package net.ooder.common.property;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ConfigFactory {

    private static Map<String, XMLProperties> fileCache = new HashMap<String, XMLProperties>();

    public static XMLProperties getXML(String path) {
        XMLProperties xml = fileCache.get(path);
        if (xml == null) {
            try {
                xml = new XMLProperties(new File(path));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            fileCache.put(path, xml);
        }
        return xml;
    }

    public static void reLoad() {
        Iterator<String> fileIt = fileCache.keySet().iterator();
        for (; fileIt.hasNext(); ) {
            String path = fileIt.next();
            XMLProperties xml = getXML(path);
            if (xml != null) {
                xml.reLoad();
            }
        }

    }


}
