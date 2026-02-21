/**
 * $RCSfile: XMLProperties.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/07/08 00:25:45 $
 * <p>
 * Copyright (C) 2003 spk, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of spk, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: XMLProperties.java,v $
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

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import java.io.*;
import java.util.*;

/**
 * <p>
 * Title: 常用代码打包
 * </p>
 * <p>
 * Description: Provides the the ability to use simple XML property files. Each property is in the form X.Y.Z, which
 * would map to an XML snippet of:
 *
 * <pre>
 * &lt;X&gt;
 *     &lt;Y&gt;
 *         &lt;Z&gt;someValue&lt;/Z&gt;
 *     &lt;/Y&gt;
 * &lt;/X&gt;
 * </pre>
 * <p>
 * The XML file is passed in to the constructor and must be readable.
 * <p>
 *
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 *
 * @author wenzhang li
 * @version 1.0
 */
public class XMLProperties {

    private File file;
    private Document doc;

    /**
     * Parsing the XML file every time we need a property is slow. Therefore, we use a Map to cache property values that
     * are accessed more than once.
     */
    private Map propertyCache = new HashMap();
    private Map propertiesCache = new HashMap();

    public void reLoad() {
        propertyCache.clear();
        propertiesCache.clear();
        try {
            init(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Creates a new XMLProperties object.
     *
     * @param file the full path the file that properties should be read from and written to.
     */
    XMLProperties(File file) throws FileNotFoundException {
        this.file = file;
        init(new FileInputStream(file));

    }

    public void init(InputStream xmlStream) {

        if (xmlStream == null) {
            throw new IllegalArgumentException("Parameter 'xmlStream' can't be null.");
        }
        Reader reader = null;
        try {
            reader = new InputStreamReader(xmlStream, "UTF-8");
            SAXBuilder builder = new SAXBuilder();
            doc = builder.build(reader);
        } catch (Exception e) {
            System.out.println("Error parsing XML stream file.");
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * Creates a new XMLProperties object.
     */
    XMLProperties() {

    }

    /**
     * Returns the value of the specified property.
     *
     * @param name the name of the property to get.
     * @return the value of the specified property.
     */
    public synchronized String getProperty(String name) {
        String value = (String) propertyCache.get(name);
        if (value == null) {
            value = System.getProperty(name);
            if (value != null) {
                propertyCache.put(value, name);
            }
        }


        if (value != null) {
            return value;
        }


        String[] propName = parsePropertyName(name);
        // Search for this property by traversing down the XML heirarchy.
        Element element = doc.getRootElement();
        for (int i = 0; i < propName.length; i++) {
            element = element.getChild(propName[i]);
            if (element == null) {
                // This node doesn't match this part of the property name which
                // indicates this property doesn't exist so return null.
                return null;
            }
        }
        // At this point, we found a matching property, so return its value.
        // Empty strings are returned as null.
        value = element.getText();
        if ("".equals(value)) {
            return null;
        } else {
            // Add to cache so that getting property next time is fast.
            value = value.trim();
            propertyCache.put(name, value);
            return value;
        }
    }

    /**
     * Returns the values of the specified property.
     *
     * @param name the name of the property to get.
     * @return the values of the specified property.
     */
    public synchronized String[] getProperties(String name) {
        String[] values = (String[]) propertiesCache.get(name);
        if (values != null) {
            return values;
        }

        List valueList = new ArrayList();
        String[] propName = parsePropertyName(name);
        // Search for this property by traversing down the XML heirarchy.
        Element element = doc.getRootElement();
        for (int i = 0; i < propName.length; i++) {
            if (i == (propName.length - 1)) {
                List elements = element.getChildren(propName[i]);
                String text;
                for (int j = 0; j < elements.size(); j++) {
                    element = (Element) elements.get(j);
                    // Empty strings are returned as null.
                    text = element.getText();
                    if (!"".equals(text)) { // Add to cache so that getting property next time is fast.
                        text = text.trim();
                        valueList.add(text);
                    }
                }
            } else {
                element = element.getChild(propName[i]);
                if (element == null) { // This node doesn't match this part of the property name which
                    // indicates this property doesn't exist so return null.
                    // return new String[0];
                    return null;
                }
            }
        } // At this point, we found a matching property, so return its value.

        values = (String[]) valueList.toArray(new String[valueList.size()]);
        propertiesCache.put(name, values);
        return values;
    }

    /**
     * Return all children property names of a parent property as a String array, or an empty array if the if there are
     * no children. For example, given the properties <tt>X.Y.A</tt>, <tt>X.Y.B</tt>, and <tt>X.Y.C</tt>, then the child
     * properties of <tt>X.Y</tt> are <tt>A</tt>, <tt>B</tt>, and <tt>C</tt>.
     *
     * @param parent the name of the parent property.
     * @return all child property values for the given parent.
     */
    public String[] getChildrenProperties(String parent) {
        String[] propName = parsePropertyName(parent);
        // Search for this property by traversing down the XML heirarchy.
        Element element = doc.getRootElement();
        for (int i = 0; i < propName.length; i++) {
            element = element.getChild(propName[i]);
            if (element == null) { // This node doesn't match this part of the property name which
                // indicates this property doesn't exist so return empty array.
                return new String[]{};
            }
        } // We found matching property, return names of children.
        List children = element.getChildren();
        int childCount = children.size();
        String[] childrenNames = new String[childCount];
        for (int i = 0; i < childCount; i++) {
            childrenNames[i] = ((Element) children.get(i)).getName();
        }
        return childrenNames;
    }

    /**
     * Sets the value of the specified property. If the property doesn't currently exist, it will be automatically
     * created.
     *
     * @param name  the name of the property to set.
     * @param value the new value for the property.
     */
    public synchronized void setProperty(String name, String value) {

        if (file == null)
            throw new IllegalArgumentException("XML property can not be modified");

        // Set cache correctly with prop name and value.
        propertyCache.put(name, value);

        String[] propName = parsePropertyName(name);
        // Search for this property by traversing down the XML heirarchy.
        Element element = doc.getRootElement();
        for (int i = 0; i < propName.length; i++) {
            // If we don't find this part of the property in the XML heirarchy
            // we add it as a new node
            if (element.getChild(propName[i]) == null) {
                element.addContent(new Element(propName[i]));
            }
            element = element.getChild(propName[i]);
        }
        // Set the value of the property in this node.
        element.setText(value);
        // write the XML properties to disk
        saveProperties();
    }

    /**
     * Deletes the specified property.
     *
     * @param name the property to delete.
     */
    public synchronized void deleteProperty(String name) {

        if (file == null)
            throw new IllegalArgumentException("XML property can not be modified");

        // Remove property from cache.
        propertyCache.remove(name);

        String[] propName = parsePropertyName(name);
        // Search for this property by traversing down the XML heirarchy.
        Element element = doc.getRootElement();
        for (int i = 0; i < propName.length - 1; i++) {
            element = element.getChild(propName[i]);
            // Can't find the property so return.
            if (element == null) {
                return;
            }
        }
        // Found the correct element to remove, so remove it...
        element.removeChild(propName[propName.length - 1]);
        // .. then write to disk.
        saveProperties();
    }

    /**
     * Saves the properties to disk as an XML document. A temporary file is used during the writing process for maximum
     * safety.
     */
    private synchronized void saveProperties() {
        Writer writer = null;
        boolean error = false;
        // Write data out to a temporary file first.
        File tempFile = null;
        try {
            tempFile = new File(file.getParentFile(), file.getName() + ".tmp");
            // Use JDOM's XMLOutputter to do the writing and formatting. The
            // file should always come out pretty-printed.
            XMLOutputter outputter = new XMLOutputter();
            writer = new OutputStreamWriter(new FileOutputStream(tempFile), "UTF-8");
            outputter.output(doc, writer);
        } catch (Exception e) {
            e.printStackTrace();
            // There were errors so abort replacing the old property file.
            error = true;
        } finally {
            try {
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
                error = true;
            }
        }
        // No errors occured, so delete the main file.
        if (!error) {
            // Reset error flag.
            error = false;
            // Delete the old file so we can replace it.
            if (file.exists() && !file.delete()) {
                System.out.println("Error deleting property file: " + file.getAbsolutePath());
                return;
            }
            // Write out the new contents to the file.
            try {
                // Use JDOM's XMLOutputter to do the writing and formatting. The
                // file should always come out pretty-printed.
                XMLOutputter outputter = new XMLOutputter();
                writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
                outputter.output(doc, writer);
            } catch (Exception e) {
                e.printStackTrace();
                // There were errors so abort replacing the old property file.
                error = true;
            } finally {
                try {
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    error = true;
                }
            }
            // If no errors, delete the temp file.
            if (!error) {
                tempFile.delete();
            }
        }
    }

    /**
     * Returns an array representation of the given Jive property. Jive properties are always in the format
     * "prop.name.is.this" which would be represented as an array of four Strings.
     *
     * @param name the name of the Jive property.
     * @return an array representation of the given Jive property.
     */
    private String[] parsePropertyName(String name) {
        List propName = new ArrayList(5); // Use a StringTokenizer to tokenize the property name.
        StringTokenizer tokenizer = new StringTokenizer(name, ".");
        while (tokenizer.hasMoreTokens()) {
            propName.add(tokenizer.nextToken());
        }
        return (String[]) propName.toArray(new String[propName.size()]);
    }
}