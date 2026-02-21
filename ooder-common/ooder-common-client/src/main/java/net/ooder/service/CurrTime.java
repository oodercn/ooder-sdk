/**
 * $RCSfile: CurrTime.java,v $
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
package net.ooder.service;

import java.text.SimpleDateFormat;
import java.util.Date;



    public class CurrTime{
    	
    	
    	
    public CurrTime() {
			super();
		}
	
	public String getCurrDateS(){
    	
    		Date cur = new Date();
    		SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
    		String val = format.format(cur);
    		return val;
    	}
  
	public Date getCurrDate(){
    	
    		Date cur = new Date();
    	
    		return cur;
    	}
  
    	public String getYear(){
    		Date cur = new Date();
    		SimpleDateFormat format = new SimpleDateFormat("yyyy");
    		String val = format.format(cur);
    		return val;
    	}
 
    	public String getMonth(){
    		Date cur = new Date();
    		SimpleDateFormat format = new SimpleDateFormat("MM");
    		String val = format.format(cur);
    		return val;
    	}
   
    	public String getDay(){
    		Date cur = new Date();
    		SimpleDateFormat format = new SimpleDateFormat("dd");
    		String val = format.format(cur);
    		return val;
    	}
  
    	public String getCurrTimeToS(){
    		Date cur = new Date(System.currentTimeMillis());
    		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    		String val = format.format(cur);
    		return val;
    	}
    public String toString(){
    	return this.getCurrDateS();
    }
    
    public static void main(String[] args) {


	}
    
    }
