/**
 * $RCSfile: ResourceBundleEnumeration.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/07/08 00:25:45 $
 *
 * Copyright (C) 2003 spk, Inc. All rights reserved.
 *
 * This software is the proprietary information of spk, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: ResourceBundleEnumeration.java,v $
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

import java.util.Set;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 *  Implements an Enumeration that combines elements from a Set and an
 *  Enumeration. Used by ListResourceBundle and PropertyResourceBundle.
 */
class ResourceBundleEnumeration implements Enumeration {

  Set set;
  Iterator iterator;
  Enumeration enumeration;
  // may remain null

  /**
   *  Constructs a resource bundle enumeration.
   *
   *@param  set          an set providing some elements of the enumeration
   *@param  enumeration  an enumeration providing more elements of the
   *      enumeration. enumeration may be null.
   */
  ResourceBundleEnumeration( Set set, Enumeration enumeration ) {
    this.set = set;
    this.iterator = set.iterator();
    this.enumeration = enumeration;
  }

  Object next = null;

  /**
   *  Description of the Method
   *
   *@return    Description of the Return Value
   */
  public boolean hasMoreElements() {
    if ( next == null )
    {
      if ( iterator.hasNext() )
      {
        next = iterator.next();
      }
      else if ( enumeration != null )
      {
        while ( next == null && enumeration.hasMoreElements() )
        {
          next = enumeration.nextElement();
          if ( set.contains( next ) )
          {
            next = null;
          }
        }
      }
    }
    return next != null;
  }

  /**
   *  Description of the Method
   *
   *@return    Description of the Return Value
   */
  public Object nextElement() {
    if ( hasMoreElements() )
    {
      Object result = next;
      next = null;
      return result;
    }
    else
    {
      throw new NoSuchElementException();
    }
  }
}
