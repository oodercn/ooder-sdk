/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
/**
 * $RCSfile: CompoundRoot.java,v $
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
package net.ooder.jds.core.esb.util;

import net.ooder.esb.util.EsbFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;




/**
 * A Stack that is implemented using a List.
 * 
 * @author plightbo
 * @version $Revision: 1.1 $
 */
public class CompoundRoot extends ArrayList {

    public CompoundRoot() {
    	Map contextRoot = EsbFactory.getContextRoot(true);
          if (!this.contains(contextRoot)){
              this.push(contextRoot);
          }
      }

    public CompoundRoot(List list) {
            super(list);
        }


        public CompoundRoot cutStack(int index) {
            return new CompoundRoot(subList(index, size()));
        }

        public Object peek() {
            return get(0);
        }

        public Object pop() {
            return remove(0);
        }
        public void removeObject(Class classzz) {
            try{
    	for (int i=0;i<this.size();i++){
        	Object object =this.get(i);
        	if (object.getClass().isAssignableFrom(classzz) ){
        		
        	this.remove(object);	
        	}
        }
      }catch (Exception e){
    	  
      }
    }
  

    public void push(Object o) {
        add(0, o);
    }
}
