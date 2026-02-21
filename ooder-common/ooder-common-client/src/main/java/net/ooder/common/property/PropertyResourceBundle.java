/**
 * $RCSfile: PropertyResourceBundle.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/07/08 00:25:45 $
 *
 * Copyright (C) 2003 spk, Inc. All rights reserved.
 *
 * This software is the proprietary information of spk, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: PropertyResourceBundle.java,v $
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

import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;
import java.io.InputStream;
import java.io.IOException;

/**
 *  <code>PropertyResourceBundle</code> is a concrete subclass of <code>ResourceBundle</code>
 *  that manages resources for a locale using a set of static strings from a
 *  property file. See {@link ResourceBundle ResourceBundle} for more
 *  information about resource bundles. See {@link Properties Properties} for
 *  more information about properties files, in particular the <a
 *  href="Properties.html#encoding">information on character encodings</a> . <p>
 *
 *  Unlike other types of resource bundle, you don't subclass <code>PropertyResourceBundle</code>
 *  . Instead, you supply properties files containing the resource data. <code>ResourceBundle.getBundle</code>
 *  will automatically look for the appropriate properties file and create a
 *  <code>PropertyResourceBundle</code> that refers to it. See {@link
 *  ResourceBundle#getBundle(String, java.util.Locale,
 *  ClassLoader) ResourceBundle.getBundle} for a complete description
 *  of the search and instantiation strategy. <p>
 *
 *  The following <a name="sample">example</a> shows a member of a resource
 *  bundle family with the base name "MyResources". The text defines the bundle
 *  "MyResources_de", the German member of the bundle family. This member is
 *  based on <code>PropertyResourceBundle</code>, and the text therefore is the
 *  content of the file "MyResources_de.properties" (a related <a
 *  href="ListResourceBundle.html#sample">example</a> shows how you can add
 *  bundles to this family that are implemented as subclasses of <code>ListResourceBundle</code>
 *  ). The keys in this example are of the form "s1" etc. The actual keys are
 *  entirely up to your choice, so long as they are the same as the keys you use
 *  in your program to retrieve the objects from the bundle. Keys are
 *  case-sensitive. <blockquote> <pre>
 * # MessageFormat pattern
 * s1=Die Platte \"{1}\" enth&auml;lt {0}.
 *
 * # location of {0} in pattern
 * s2=1
 *
 * # sample disk name
 * s3=Meine Platte
 *
 * # first ChoiceFormat choice
 * s4=keine Dateien
 *
 * # second ChoiceFormat choice
 * s5=eine Datei
 *
 * # third ChoiceFormat choice
 * s6={0,number} Dateien
 *
 * # sample date
 * s7=3. M&auml;rz 1996
 * </pre> </blockquote>
 *
 *@see        ResourceBundle
 *@see        ListResourceBundle
 *@see        Properties
 *@since      JDK1.1
 */
public class PropertyResourceBundle extends ResourceBundle {
  /**
   *  Creates a property resource bundle.
   *
   *@param  stream           property file to read from.
   *@exception  IOException  Description of the Exception
   */
  public PropertyResourceBundle( InputStream stream )
    throws IOException {
    Properties properties = new Properties();
    properties.load( stream );
    lookup = new HashMap( properties );
  }

  // Implements java.util.ResourceBundle.handleGetObject; inherits javadoc specification.
  /**
   *  Description of the Method
   *
   *@param  key  Description of the Parameter
   *@return      Description of the Return Value
   */
  public Object handleGetObject( String key ) {
    if ( key == null )
    {
      throw new NullPointerException();
    }
    return lookup.get( key );
  }

  /**
   *  Implementation of ResourceBundle.getKeys.
   *
   *@return    The keys value
   */
  public Enumeration getKeys() {
    ResourceBundle parent = this.parent;
    return new ResourceBundleEnumeration( lookup.keySet(),
        ( parent != null ) ? parent.getKeys() : null );
  }

  // ==================privates====================

  private Map lookup;
}
